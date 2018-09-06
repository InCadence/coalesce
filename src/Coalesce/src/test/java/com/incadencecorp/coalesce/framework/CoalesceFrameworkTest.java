package com.incadencecorp.coalesce.framework;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.MockPersister;
import org.junit.Assert;
import org.junit.Test;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

public class CoalesceFrameworkTest {

    @Test
    public void testSecondaryReads() throws Exception
    {
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        TestEntity entity3 = new TestEntity();
        entity3.initialize();

        MockPersister mock1 = new MockPersister();
        MockPersister mock2 = new MockPersister();

        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(mock1);
        framework.setSecondaryPersistors(mock2);

        mock1.saveEntity(false, entity1);
        mock2.saveEntity(false, entity3, entity2);

        CoalesceEntity[] entities;

        // Verify order in which entities are returned.
        entities = framework.getCoalesceEntities(entity1.getKey(), entity2.getKey(), entity3.getKey());

        Assert.assertEquals(3, entities.length);
        Assert.assertEquals(entity1.getKey(), entities[0].getKey());
        Assert.assertEquals(entity2.getKey(), entities[1].getKey());
        Assert.assertEquals(entity3.getKey(), entities[2].getKey());

        entities = framework.getCoalesceEntities(entity2.getKey(), entity3.getKey(), entity1.getKey());

        Assert.assertEquals(3, entities.length);
        Assert.assertEquals(entity2.getKey(), entities[0].getKey());
        Assert.assertEquals(entity3.getKey(), entities[1].getKey());
        Assert.assertEquals(entity1.getKey(), entities[2].getKey());

    }
}
