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

package com.incadencecorp.coalesce.framework.persistance;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;

/**
 * These test exercise the {@link CoalescePersisterWrapper} to ensure proper
 * behavior.
 * 
 * @author n78554
 *
 */
public class CoalescePersisterWrapperTest {

    /**
     * This test ensures if you initialize {@link CoalesceFramework} with
     * wrappers it will filter appropriately.
     * 
     * @throws Exception
     */
    @Test
    public void testCoalesceFramework() throws Exception
    {
        MockPersister mock = new MockPersister();

        CoalescePersisterWrapper wrapper = new CoalescePersisterWrapper(mock, true, new ObjectMetaData(null,
                                                                                                       TestEntity.NAME,
                                                                                                       null,
                                                                                                       null));

        try (CoalesceFramework framework = new CoalesceFramework())
        {
            framework.setAuthoritativePersistor(wrapper);

            TestEntity entity = new TestEntity();
            entity.initialize();

            framework.saveCoalesceEntity(entity);

            Assert.assertEquals(0, mock.getEntity(entity.getKey()).length);
        }

    }

    /**
     * This test ensures that if no filters are specified that by default an
     * exclusion wrapper will reject everything and an inclusion will allow
     * everything.
     * 
     * @throws Exception
     */
    @Test
    public void testNoFilter() throws Exception
    {
        MockPersister mock = new MockPersister();

        CoalescePersisterWrapper exclusion = new CoalescePersisterWrapper(mock, true);
        CoalescePersisterWrapper inclusion = new CoalescePersisterWrapper(mock, false);

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(exclusion.isAllowed(entity));
        Assert.assertFalse(inclusion.isAllowed(entity));

    }

    /**
     * This test ensures you can filter entities based off their keys.
     * 
     * @throws Exception
     */
    @Test
    public void testKeyPrefixes() throws Exception
    {
        MockPersister mock = new MockPersister();

        ObjectMetaData[] filters = new ObjectMetaData[] {
            new ObjectMetaData("A", null, null, null)
        };

        CoalescePersisterWrapper exclusion = new CoalescePersisterWrapper(mock, true, filters);
        CoalescePersisterWrapper inclusion = new CoalescePersisterWrapper(mock, false, filters);

        TestEntity entity = new TestEntity();
        entity.initialize();
        entity.setKey("A" + entity.getKey().substring(1, 36));

        Assert.assertFalse(exclusion.isAllowed(entity));
        Assert.assertTrue(inclusion.isAllowed(entity));

        entity.setKey("a" + entity.getKey().substring(1, 36));

        Assert.assertFalse(exclusion.isAllowed(entity));
        Assert.assertTrue(inclusion.isAllowed(entity));

        entity.setKey("B" + entity.getKey().substring(1, 36));

        Assert.assertTrue(exclusion.isAllowed(entity));
        Assert.assertFalse(inclusion.isAllowed(entity));

        entity.setKey("b" + entity.getKey().substring(1, 36));

        Assert.assertTrue(exclusion.isAllowed(entity));
        Assert.assertFalse(inclusion.isAllowed(entity));

    }

    /**
     * This test ensures that specifying what to exclude works as expected.
     */
    @Test
    public void testExclusionWrapper()
    {
        testWrapper(true);
    }

    /**
     * This test ensures that specifying what to include works as expected.
     */
    @Test
    public void testInclusionWrapper()
    {
        testWrapper(false);
    }

    private void testWrapper(boolean isExclusion)
    {
        MockPersister mock = new MockPersister();

        // Null and * are treated as wilds.
        ObjectMetaData[] filters = new ObjectMetaData[] {
                new ObjectMetaData(null, "all", "all", "all"), new ObjectMetaData(null, "namesource", "namesource", null),
                new ObjectMetaData(null, "name", null, null), new ObjectMetaData(null, "nameversion", null, "nameversion"),
                new ObjectMetaData(null, null, "source", "*"),
                new ObjectMetaData("*", "*", "sourceversion", "sourceversion"),
                new ObjectMetaData("*", "*", "*", "version"),
        };

        CoalescePersisterWrapper wrapper = new CoalescePersisterWrapper(mock, isExclusion, filters);

        // Verify All Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("all", "all", "all")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "all", "all")));

        // Verify Name / Source Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("Namesource", "namesource", "namesource")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("namesource", "Namesource", "test")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "namesource", "namesource")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("namesource", "test", "namesource")));

        // Verify Name Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("name", "test", "name")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("name", "name", "test")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("name", "test", "test")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "name", "name")));

        // Verify Name / Version Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("nameversion", "test", "nameversion")));
        Assert.assertEquals(!isExclusion,
                            wrapper.isAllowed(CoalesceEntity.create("nameversion", "nameversion", "nameversion")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "nameversion", "nameversion")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("nameversion", "test", "test")));

        // Verify Source Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "source", "test")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "source", "source")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("source", "source", "test")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("source", "test", "source")));

        // Verify Source / Version Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "sourceversion", "sourceversion")));
        Assert.assertEquals(!isExclusion,
                            wrapper.isAllowed(CoalesceEntity.create("sourceversion", "sourceversion", "sourceversion")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("sourceversion", "sourceversion", "test")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "sourceversion", "test")));

        // Verify Version Filter
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "test", "version")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("test", "version", "version")));
        Assert.assertEquals(!isExclusion, wrapper.isAllowed(CoalesceEntity.create("version", "test", "version")));
        Assert.assertEquals(isExclusion, wrapper.isAllowed(CoalesceEntity.create("version", "version", "test")));

    }
}
