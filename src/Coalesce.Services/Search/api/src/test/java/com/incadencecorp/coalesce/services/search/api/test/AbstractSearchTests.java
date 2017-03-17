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

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * These test exercise the Search API. When extending these test you must set
 * the client member variable.
 * 
 * @author Derek Clemenzi
 */
public abstract class AbstractSearchTests {

    /**
     * Must set this in the @BeforeClass method.
     */
    protected static ISearchClient client;
    protected static ICrudClient crud;

    /**
     * This is a simple test to verify that an entity saved will result in a hit
     * when searching for the key.
     * 
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("Hello", "World", "1", null, null);

        // Create Entity
        crud.createDataObject(entity);

        // Search for Entity Key
        SearchDataObjectResponse results = client.search(CoalescePropertyFactory.getEntityKey(entity.getKey()), 1);

        // Verify Hit
        Assert.assertEquals(1, results.getResult().size());
        Assert.assertEquals(1, results.getResult().get(0).getResult().getHits().size());
        
        Assert.assertEquals(EResultStatus.SUCCESS, results.getStatus());
        Assert.assertEquals(EResultStatus.SUCCESS, results.getResult().get(0).getStatus());
        
        HitType hit = results.getResult().get(0).getResult().getHits().get(0);

        // Verify Hit's Properties
        Assert.assertEquals(entity.getKey(), hit.getEntityKey());
        Assert.assertEquals(entity.getName(), hit.getName());
        Assert.assertEquals(entity.getSource(), hit.getSource());
        Assert.assertEquals(entity.getTitle(), hit.getTitle());
    }

}
