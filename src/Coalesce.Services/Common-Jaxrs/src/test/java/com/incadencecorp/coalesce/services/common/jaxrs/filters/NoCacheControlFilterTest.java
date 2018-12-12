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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;

/**
 * These test ensure that the {@link NoCacheControlFilter} works as intended.
 *
 * @author Derek Clemenzi
 */
public class NoCacheControlFilterTest {

    /**
     * This test ensures that the no cache header is appended to the response. If URI does not match a specified regex filter
     * then a default CacheControl is used.
     */
    @Test
    public void testNoCacheControlFilter() throws Exception
    {
        final UriInfo info = Mockito.mock(UriInfo.class);
        Mockito.when(info.getBaseUri()).thenReturn(new URI("www.google.com/specials"));

        final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getMethod()).thenReturn("GET");
        Mockito.when(requestContext.getUriInfo()).thenReturn(info);

        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        final ContainerResponseContext responseContext = Mockito.mock(ContainerResponseContext.class);
        Mockito.when(responseContext.getHeaders()).thenReturn(headers);

        final NoCacheControlFilter filter = new NoCacheControlFilter();
        filter.filter(requestContext, responseContext);

        Assert.assertTrue(headers.containsKey(NoCacheControlFilter.HEADER_CACHE_CONTROL));
        Assert.assertEquals(1, headers.get(NoCacheControlFilter.HEADER_CACHE_CONTROL).size());
        Assert.assertTrue(((CacheControl) headers.get(NoCacheControlFilter.HEADER_CACHE_CONTROL).get(0)).isNoCache());
        Assert.assertTrue(headers.containsKey(NoCacheControlFilter.HEADER_PRAGMA));

        CacheControl cc = new CacheControl();
        cc.setNoCache(false);
        cc.setNoStore(true);
        cc.setMustRevalidate(true);

        filter.setCacheControls(Collections.singletonMap(Collections.singleton("^(?!\\/(specials|profiles)).*$"), cc));

        headers.clear();
        filter.filter(requestContext, responseContext);

        Assert.assertTrue(headers.containsKey(NoCacheControlFilter.HEADER_CACHE_CONTROL));
        Assert.assertEquals(1, headers.get(NoCacheControlFilter.HEADER_CACHE_CONTROL).size());
        Assert.assertFalse(((CacheControl) headers.get(NoCacheControlFilter.HEADER_CACHE_CONTROL).get(0)).isNoCache());
        Assert.assertFalse(headers.containsKey(NoCacheControlFilter.HEADER_PRAGMA));
    }

}
