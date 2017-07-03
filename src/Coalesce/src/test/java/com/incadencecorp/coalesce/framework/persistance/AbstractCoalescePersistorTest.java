package com.incadencecorp.coalesce.framework.persistance;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;

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

public abstract class AbstractCoalescePersistorTest<T extends ICoalescePersistor> {

    protected abstract T createPersister() throws Exception;

    /**
     * This test attempts to create a entity within the data store.
     * 
     * @throws Exception
     */
    @Test
    public void testCreation() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        // Cleanup
        entity.markAsDeleted();

        persister.saveEntity(true, entity);

    }

    /**
     * This test attempts to create and then update a entity within the data
     * store.
     * 
     * @throws Exception
     */
    @Test
    public void testUpdates() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.UPDATE));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        TestRecord record1 = entity.addRecord1();
        record1.getBooleanField().setValue(true);

        Assert.assertTrue(persister.saveEntity(false, entity));

        CoalesceEntity updated = persister.getEntity(entity.getKey())[0];

        CoalesceRecord updatedRecord = (CoalesceRecord) updated.getCoalesceObjectForKey(record1.getKey());

        Assert.assertNotNull(updatedRecord);
        Assert.assertEquals(record1.getBooleanField().getBaseValue(),
                            updatedRecord.getFieldByName(record1.getBooleanField().getName()).getBaseValue());

        // Cleanup
        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test attempts to mark an entity as deleted as well as remove it
     * Completely.
     * 
     * @throws Exception
     */
    @Test
    public void testDeletion() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.DELETE));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));
        Assert.assertNotNull(persister.getEntityXml(entity.getKey())[0]);

        entity.markAsDeleted();

        Assert.assertTrue(persister.saveEntity(true, entity));
        Assert.assertEquals(0, persister.getEntityXml(entity.getKey()).length);

    }

    /**
     * This test attempts to retrieve an invalid entity key and should fail.
     * 
     * @throws Exception
     */
    @Test
    public void testRetrieveInvalidKey() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        String results[] = persister.getEntityXml(entity.getKey(),
                                                  UUID.randomUUID().toString(),
                                                  UUID.randomUUID().toString(),
                                                  UUID.randomUUID().toString());

        Assert.assertEquals(1, results.length);
        Assert.assertEquals(entity.getKey(), CoalesceEntity.create(results[0]).getKey());
    }

    /**
     * This test attempts to save a template and retrieve it.
     * 
     * @throws Exception
     */
    @Test
    public void testTemplates() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ_TEMPLATES));

        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceEntityTemplate template1 = CoalesceEntityTemplate.create(entity);
        entity.setName("HelloWorld");
        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity);

        persister.registerTemplate(template1, template2);

        Assert.assertEquals(template1.getKey(),
                            CoalesceEntityTemplate.create(persister.getEntityTemplateXml(template1.getKey())).getKey());
        Assert.assertEquals(template2.getKey(),
                            CoalesceEntityTemplate.create(persister.getEntityTemplateXml(template2.getKey())).getKey());
    }

}
