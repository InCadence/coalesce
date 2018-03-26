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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import java.io.IOException;

/**
 * This implementation appends cache headers to GET responses to indicate that the client should not cache.
 *
 * @author Derek Clemenzi
 */
public class NoCacheControlFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException
    {
        if (req.getMethod().equals("GET"))
        {
            CacheControl cc = new CacheControl();
            cc.setNoCache(true);
            cc.setNoStore(true);
            cc.setMustRevalidate(true);
            res.getHeaders().add("Cache-Control", cc);
            res.getHeaders().add("Pragma", "no-cache");
        }
    }
}
