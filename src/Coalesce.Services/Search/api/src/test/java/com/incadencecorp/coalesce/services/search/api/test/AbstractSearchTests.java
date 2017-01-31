/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.services.search.api.test;

import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * These test exercise the Search API. When extending these test you must set the
 * client member variable.
 * 
 * @author Derek Clemenzi
 */
public abstract class AbstractSearchTests {

    /**
     * Must set this in the @BeforeClass method.
     */
    protected static ISearchClient client;
    
    @Test
    public void testSearch() throws Exception {
        
        FilterFactory ff = CommonFactoryFinder.getFilterFactory();
        
        Filter filter = ff.equals(ff.property("aa"), ff.literal("aa"));
        
        System.out.println(filter.toString());
        
        client.search(filter, 1);
        
//        FilterType query = new FilterTypeImpl();
//        
//        client.search(filter, 1);
        
    }

}
