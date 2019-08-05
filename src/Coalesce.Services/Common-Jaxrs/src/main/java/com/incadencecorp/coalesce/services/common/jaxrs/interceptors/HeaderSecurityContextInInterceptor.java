/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.common.jaxrs.interceptors;

import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import org.apache.cxf.common.security.SimpleGroup;
import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This interceptor creates a SecurityContext based on the configured headers.
 *
 * @author Derek Clemenzi
 * @see #ATTR_ALLOW_ANONYMOUS
 * @see #ATTR_HEADER_USERNAME
 * @see #ATTR_HEADER_ROLES
 * @see #ATTR_HEADER_ROLES_PREFIXES
 * @see #ATTR_HEADER_ROLES_SEPERATOR
 */
public class HeaderSecurityContextInInterceptor extends AbstractPhaseInterceptor<Message> {

    private Logger LOGGER = LoggerFactory.getLogger(HeaderSecurityContextInInterceptor.class);

    // Default Values
    private static final String DEFAULT_HEADER_USERNAME = "X-Coalesce-User";
    private static final String DEFAULT_HEADER_ROLES = "X-Coalesce-Roles";
    private static final String DEFAULT_HEADER_ROLES_SEPERATOR = ":";
    private static final String DEFAULT_HEADER_ROLES_PREFIXES = "ROLE";

    // Configuration Parameters
    private static final String ATTR_BASE = "com.incadencecorp.coalesce.security.headers.";
    /**
     * (Boolean) Specifies whether an Anonymous subject should be used in the event no username header was provided.
     */
    public static final String ATTR_ALLOW_ANONYMOUS = ATTR_BASE + "allowAnonymous";
    /**
     * (String) Specifies the header that contains the username.
     */
    public static final String ATTR_HEADER_USERNAME = ATTR_BASE + "username";
    /**
     * (String) Specifies the header that contains the user's roles.
     */
    public static final String ATTR_HEADER_ROLES = ATTR_BASE + "roles";
    /**
     * (String) Specifies the role's prefrex (ex. ROLE:ADMIN).
     */
    public static final String ATTR_HEADER_ROLES_PREFIXES = ATTR_BASE + "roles.prefixes";
    /**
     * (String) Specifies the character that seperates the role name from the prefix. If this is null then the prefix is ignored.
     */
    public static final String ATTR_HEADER_ROLES_SEPERATOR = ATTR_BASE + "roles.seperator";

    private final String usernameHeader;
    private final String rolesHeader;
    private final Set<String> rolesPrefixes = new HashSet<>();
    private final String rolesSeperator;
    private final boolean allowAnonymous;

    /**
     * Default Configuration
     */
    public HeaderSecurityContextInInterceptor()
    {
        this(Collections.emptyMap());
    }

    /**
     * Provides additional configuration options.
     *
     * @param params configuration parameters.
     */
    public HeaderSecurityContextInInterceptor(Map<String, String> params)
    {
        super("unmarshal");

        usernameHeader = params.getOrDefault(ATTR_HEADER_USERNAME, DEFAULT_HEADER_USERNAME);
        rolesHeader = params.getOrDefault(ATTR_HEADER_ROLES, DEFAULT_HEADER_ROLES);
        rolesPrefixes.addAll(Arrays.asList(params.getOrDefault(ATTR_HEADER_ROLES_PREFIXES,
                                                               DEFAULT_HEADER_ROLES_PREFIXES).split(",")));
        rolesSeperator = params.getOrDefault(ATTR_HEADER_ROLES_SEPERATOR, DEFAULT_HEADER_ROLES_SEPERATOR);
        allowAnonymous = Boolean.parseBoolean(params.getOrDefault(ATTR_ALLOW_ANONYMOUS, Boolean.TRUE.toString()));
    }

    @Override
    public void handleMessage(final Message message) throws Fault
    {
        SecurityContext sc = message.get(SecurityContext.class);

        // Context Exists?
        if (sc == null || sc.getUserPrincipal() == null || sc.getUserPrincipal().getName() == null)
        {
            // No; Create BDP Context
            TreeMap<String, List<String>> headers = (TreeMap<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Message:");
                for (Map.Entry<String, Object> entry : message.entrySet())
                {
                    LOGGER.debug("\t{} = {}", entry.getKey(), entry.getValue());
                }

                LOGGER.debug("Header:");
                for (Map.Entry<String, List<String>> entry : headers.entrySet())
                {
                    LOGGER.debug("\t{} = {}", entry.getKey(), entry.getValue());
                }
            }

            message.put(SecurityContext.class.getName(), new DefaultSecurityContext(createSubject(headers)));
        }

    }

    private Subject createSubject(TreeMap<String, List<String>> headers)
    {
        Subject subject = new Subject();

        // User Specified?
        if (headers.containsKey(usernameHeader))
        {
            // Yes; Get Username
            String username = headers.get(usernameHeader).get(0);
            CoalesceSimplePrincipal principal = new CoalesceSimplePrincipal(username);

            subject.getPrincipals().add(principal);

            // Roles Specified?
            if (headers.containsKey(rolesHeader))
            {
                // Yes; Get Roles
                for (String value : headers.get(rolesHeader))
                {
                    if (StringHelper.isNullOrEmpty(rolesSeperator))
                    {
                        subject.getPrincipals().add(new SimpleGroup(value, username));
                    }
                    else
                    {
                        String[] parts = value.split(rolesSeperator);

                        if (parts.length == 2)
                        {
                            if (rolesPrefixes.contains(parts[0]))
                            {
                                subject.getPrincipals().add(new SimpleGroup(parts[1], username));
                                principal.addRole(parts[1]);
                            }
                            else
                            {
                                principal.addAttribute(parts[0], parts[1]);
                                LOGGER.debug("(WARN) Unknown attribute prefix: {}", parts[0]);
                            }
                        }
                        else
                        {
                            LOGGER.debug("(WARN) Unknown attribute: {}", value);
                        }
                    }
                }
            }
        }
        else if (allowAnonymous)
        {
            // No; But Anonymous access is allowed so grant access with no roles.
            LOGGER.debug("Anonymous Access");
            subject.getPrincipals().add(new SimplePrincipal("Anonymous"));
        }
        else
        {
            throw new IllegalArgumentException("User not specified");
        }

        subject.setReadOnly();

        return subject;
    }

}
