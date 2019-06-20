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

package com.incadencecorp.coalesce.services.common.jaxrs.filters;

import com.incadencecorp.coalesce.services.common.jaxrs.interceptors.HeaderSecurityContextInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.security.SecurityContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * These test ensure correct behavior of the {@link HeaderSecurityContextInInterceptor}.
 *
 * @author Derek Clemenzi
 */
public class HeaderSecurityContextInInterceptorTest {

    /**
     * This test ensures that if no user headers were provided that the context is set to anonymous if allowAnonymous is true
     * otherwise throws an exception.
     */
    @Test
    public void testAnonymous()
    {
        TreeMap<String, List<String>> headers = new TreeMap<>();

        Map<String, String> params = new HashMap<>();
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_USERNAME, "X-Coalesce-User");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES, "X-Coalesce-Roles");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_PREFIXES, "ROLE");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_SEPERATOR, ":");

        SecurityContext context = createSecurityContext(params, headers);

        Assert.assertEquals("Anonymous", context.getUserPrincipal().getName());
        Assert.assertFalse(context.isUserInRole("Hello"));
        Assert.assertFalse(context.isUserInRole("World"));

        // Verify that roles are not applied to Anonymous users
        headers.put("X-Coalesce-Roles", Arrays.asList("ROLE:Hello", "ROLE:World", "GROUP:NonRole"));

        context = createSecurityContext(params, headers);

        Assert.assertEquals("Anonymous", context.getUserPrincipal().getName());
        Assert.assertFalse(context.isUserInRole("Hello"));
        Assert.assertFalse(context.isUserInRole("World"));

        params.put(HeaderSecurityContextInInterceptor.ATTR_ALLOW_ANONYMOUS, Boolean.FALSE.toString());

        try
        {
            createSecurityContext(params, headers);
            Assert.assertEquals("User not specified", "");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals("User not specified", e.getMessage());
        }

    }

    /**
     * This test ensures that the security context is created based on the configured headers.
     */
    @Test
    public void testSecurityContext()
    {
        String username = UUID.randomUUID().toString();

        TreeMap<String, List<String>> headers = new TreeMap<>();
        headers.put("X-Coalesce-User", Collections.singletonList(username));
        headers.put("X-Coalesce-Roles", Arrays.asList("ROLE:Hello", "ROLE:World", "GROUP:NonRole"));

        Map<String, String> params = new HashMap<>();
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_USERNAME, "X-Coalesce-User");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES, "X-Coalesce-Roles");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_PREFIXES, "ROLE");
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_SEPERATOR, ":");

        SecurityContext context = createSecurityContext(params, headers);

        Assert.assertNotNull(context);
        Assert.assertEquals(username, context.getUserPrincipal().getName());
        Assert.assertTrue(context.isUserInRole("Hello"));
        Assert.assertTrue(context.isUserInRole("World"));
        Assert.assertFalse(context.isUserInRole("NonRole"));

        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_PREFIXES, "ROLE,GROUP");

        context = createSecurityContext(params, headers);

        Assert.assertNotNull(context);
        Assert.assertEquals(username, context.getUserPrincipal().getName());
        Assert.assertTrue(context.isUserInRole("Hello"));
        Assert.assertTrue(context.isUserInRole("World"));
        Assert.assertTrue(context.isUserInRole("NonRole"));

        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_SEPERATOR, "&");

        context = createSecurityContext(params, headers);

        Assert.assertNotNull(context);
        Assert.assertEquals(username, context.getUserPrincipal().getName());
        Assert.assertFalse(context.isUserInRole("Hello"));
        Assert.assertFalse(context.isUserInRole("World"));
        Assert.assertFalse(context.isUserInRole("NonRole"));

        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_SEPERATOR, "");

        context = createSecurityContext(params, headers);

        Assert.assertNotNull(context);
        Assert.assertEquals(username, context.getUserPrincipal().getName());
        Assert.assertTrue(context.isUserInRole("ROLE:Hello"));
        Assert.assertTrue(context.isUserInRole("ROLE:World"));
        Assert.assertTrue(context.isUserInRole("GROUP:NonRole"));

    }

    private SecurityContext createSecurityContext(Map<String, String> params, TreeMap<String, List<String>> headers)
    {
        Message message = new MessageImpl();
        message.put(Message.PROTOCOL_HEADERS, headers);

        HeaderSecurityContextInInterceptor interceptor = new HeaderSecurityContextInInterceptor(params);
        interceptor.handleMessage(message);

        return message.get(SecurityContext.class);
    }

}
