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

import com.incadencecorp.coalesce.common.regex.RegexCollection;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation appends cache headers to GET responses to indicate that the client should not cache by default. Regex
 * can be applied to add additional cache headers as required.
 *
 * @author Derek Clemenzi
 */
public class NoCacheControlFilter implements ContainerResponseFilter {

    private final Map<RegexCollection, CacheControl> controls = new HashMap<>();

    /**
     * Header that specifies the cache control
     */
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    /**
     * Header required for IE support
     */
    public static final String HEADER_PRAGMA = "Pragma";

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException
    {
        CacheControl cc = null;

        if (req.getMethod().equals("GET"))
        {
            for (Map.Entry<RegexCollection, CacheControl> entry : controls.entrySet())
            {
                if (entry.getKey().match(req.getUriInfo().getBaseUri().toString()))
                {
                    cc = entry.getValue();
                    break;
                }
            }

            if (cc == null)
            {
                // Default Behaviour
                cc = new CacheControl();
                cc.setNoCache(true);
                cc.setNoStore(true);
                cc.setMustRevalidate(true);
            }

            res.getHeaders().add(HEADER_CACHE_CONTROL, cc);

            if (cc.isNoCache())
            {
                // Required for IE
                res.getHeaders().add(HEADER_PRAGMA, "no-cache");
            }
        }
    }

    /**
     * Apply custom cache headers to URIs that match given regexes.
     *
     * @param values is a map one or more regexes and the cache control that should be applied.
     */
    public void setCacheControls(Map<Collection<String>, CacheControl> values)
    {
        controls.clear();

        for (Map.Entry<Collection<String>, CacheControl> entry : values.entrySet())
        {
            if (!entry.getKey().isEmpty())
            {
                RegexCollection regex = new RegexCollection();
                regex.setRegex(entry.getKey());

                controls.put(regex, entry.getValue());
            }
        }
    }
}
