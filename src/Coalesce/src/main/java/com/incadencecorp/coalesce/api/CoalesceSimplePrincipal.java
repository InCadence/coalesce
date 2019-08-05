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

package com.incadencecorp.coalesce.api;

import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link ICoalescePrincipal}
 *
 * @author Derek Clemenzi
 */
public class CoalesceSimplePrincipal implements ICoalescePrincipal {

    private String ip;
    private String name;
    private Set<String> roles = new HashSet<>();
    private Map<String, List<String>> attributes = new HashMap<>();

    /**
     * Creates a principal for the user running this thread.
     */
    public CoalesceSimplePrincipal()
    {
        this(System.getProperty("user.name"), getHostIP());
    }

    /**
     * Creates a principal for the specified user using the localhost as the IP
     *
     * @param name of the user
     */
    public CoalesceSimplePrincipal(String name)
    {
        this(name, getHostIP());
    }

    /**
     * Creates a principal from an existing principal using the localhost as the IP.
     *
     * @param principal of user.
     */
    public CoalesceSimplePrincipal(Principal principal)
    {
        this(principal.getName(), getHostIP());
    }

    /**
     * @param name of user
     * @param ip   of user
     */
    public CoalesceSimplePrincipal(String name, String ip)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Principal name can not be null");
        }
        else
        {
            this.name = name;
        }

        this.ip = ip;
    }

    @Override
    public String getIp()
    {
        return ip;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Set<String> getRoles()
    {
        return roles;
    }

    public void addRole(String role)
    {
        this.roles.add(role);
    }

    public void addRoles(List<String> roles)
    {
        this.roles.addAll(roles);
    }

    @Override
    public Map<String, List<String>> getAttributes()
    {
        return attributes;
    }

    public void addAttribute(String key, String value)
    {
        if (attributes.containsKey(key))
        {
            attributes.get(key).add(value);
        }
        else
        {
            attributes.put(key, Collections.singletonList(value));
        }
    }

    public void addAttributes(Map<String, List<String>> attributes)
    {
        this.attributes.putAll(attributes);
    }

    public boolean equals(Object obj)
    {
        return obj instanceof CoalesceSimplePrincipal && this.name.equals(((CoalesceSimplePrincipal) obj).name);
    }

    public int hashCode()
    {
        return this.name.hashCode();
    }

    public String toString()
    {
        return this.name;
    }

    private static String getHostIP()
    {
        try
        {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            return null;
        }
    }
}
