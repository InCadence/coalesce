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

package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * These test ensure that you can delete and restore versions of entities.
 * 
 * @author n78554
 */
public class CoalesceIteratorUpdateVersionStatusTest {

    private CoalesceIteratorUpdateVersionStatus iterator = new CoalesceIteratorUpdateVersionStatus();

    @Test (expected=IllegalArgumentException.class)
    public void testDeleteZeroFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        iterator.delete(entity, 0);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testDeleteNegativeFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        iterator.delete(entity, -1);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testDeleteCloneNegativeFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        entity.setObjectVersion(2);
        
        // Should Pass
        iterator.deleteClone(entity, 2);
        
        // Should Throw Exception
        iterator.deleteClone(entity, -1);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testDeleteCurrentFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        iterator.delete(entity, 1);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testRestoreZeroFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        iterator.restore(entity, 0);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testRestoreNegativeFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        iterator.restore(entity, -1);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testRestoreCloneNegativeFailure() 
    {
        CoalesceEntity entity = new CoalesceEntity(); 
        entity.initialize(); 
        
        // Should Pass
        iterator.restoreClone(entity, 1);
        
        // Should Throw Exception
        iterator.restoreClone(entity, -1);
    }
    /**
     * Creates 5 versions of the an entity and verifies deleting / restoring
     * versions.
     * 
     * @throws Exception
     */
    @Test
    public void testFields() throws Exception
    {

        // Create Entity
        TestEntity v1 = new TestEntity();
        v1.initialize();
        TestRecord record = v1.addRecord1();

        // Set Default Values
        record.getBooleanField().setValue(false);

        TestEntity v2 = updateEntity(v1, "Derek", record.getKey(), ECoalesceObjectStatus.ACTIVE, false, 5.1);
        TestEntity v3 = updateEntity(v2, "Derek1", record.getKey(), ECoalesceObjectStatus.READONLY, true, 5.4);
        TestEntity v4 = updateEntity(v3, "Derek", record.getKey(), ECoalesceObjectStatus.DELETED, true, 1.4);
        TestEntity v5 = updateEntity(v4, "Derek2", record.getKey(), ECoalesceObjectStatus.ACTIVE, false, 1.4);

        // Delete Version 3
        iterator.delete(v5, 3);

        verifyRecord(v5, record.getKey(), 1, ECoalesceObjectStatus.ACTIVE, false, null);
        verifyRecord(v5, record.getKey(), 2, ECoalesceObjectStatus.ACTIVE, false, 5.1);
        verifyRecord(v5, record.getKey(), 4, ECoalesceObjectStatus.DELETED, true, 1.4);
        verifyRecord(v5, record.getKey(), 5, ECoalesceObjectStatus.ACTIVE, false, 1.4);

        try
        {
            verifyRecord(v5, record.getKey(), 3, ECoalesceObjectStatus.ACTIVE, false, 5.1);
            fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected Result
        }

        iterator.restore(v5, 3);

        verifyRecord(v5, record.getKey(), 1, ECoalesceObjectStatus.ACTIVE, false, null);
        verifyRecord(v5, record.getKey(), 2, ECoalesceObjectStatus.ACTIVE, false, 5.1);
        verifyRecord(v5, record.getKey(), 3, ECoalesceObjectStatus.READONLY, true, 5.4);
        verifyRecord(v5, record.getKey(), 4, ECoalesceObjectStatus.DELETED, true, 1.4);
        verifyRecord(v5, record.getKey(), 5, ECoalesceObjectStatus.ACTIVE, false, 1.4);

    }

    /**
     * Updated the linkages and verifies that deleting / restoring versions
     * works correctly.
     * 
     * @throws Exception
     */
    @Test
    public void testLinkages() throws Exception
    {

        CoalesceIteratorGetVersion versioner = new CoalesceIteratorGetVersion();
        CoalesceEntity entity;

        String linkageCreatedKey = null;
        String linkageHasUseOfKey = null;

        // Create Linked Entity
        TestEntity linkedEntity = new TestEntity();
        linkedEntity.initialize();
        linkedEntity.setObjectVersion(5);

        // Create Entities
        TestEntity v1a = new TestEntity();
        TestEntity v1b = new TestEntity();

        v1a.initialize();
        v1b.initialize(v1a.toXml());

        // Create Linkages
        EntityLinkHelper.linkEntities(v1a, ELinkTypes.CREATED, linkedEntity, true);
        EntityLinkHelper.linkEntities(v1b, ELinkTypes.HAS_USE_OF, linkedEntity, true);

        // Merge V2
        TestEntity v2 = new TestEntity();
        v2.initialize(CoalesceEntity.mergeSyncEntity(v1a, v1b, "merge1", "127.0.0.1"));

        TestEntity v2b = new TestEntity();
        v2b.initialize(v2.toXml());

        EntityLinkHelper.unLinkEntities(v2b, linkedEntity, ELinkTypes.CREATED);

        // Merge V3
        TestEntity v3 = new TestEntity();
        v3.initialize(CoalesceEntity.mergeSyncEntity(v2, v2b, "merge1", "127.0.0.1"));

        // Up the version to avoid illegal argument exception
        v3.setObjectVersion(4);

        // Determine Keys
        for (CoalesceLinkage linkage : v3.getLinkageSection().getLinkages().values())
        {
            switch (linkage.getLinkType()) {
            case CREATED:
                linkageCreatedKey = linkage.getKey();
                break;
            case HAS_USE_OF:
                linkageHasUseOfKey = linkage.getKey();
                break;
            }
        }

        // Delete Version 3 (Added HasUseOf)
        iterator.delete(v3, 3);

        // Verify Version 3
        entity = versioner.getClonedVersion(v3, 3);
        assertEquals(ECoalesceObjectStatus.ACTIVE, entity.getCoalesceObjectForKey(linkageHasUseOfKey).getStatus());
        assertEquals(ECoalesceObjectStatus.DELETED, entity.getCoalesceObjectForKey(linkageCreatedKey).getStatus());

        iterator.restore(v3, 3);

        // Verify Version 3
        entity = versioner.getClonedVersion(v3, 3);
        assertEquals(ECoalesceObjectStatus.ACTIVE, entity.getCoalesceObjectForKey(linkageHasUseOfKey).getStatus());
        assertEquals(ECoalesceObjectStatus.DELETED, entity.getCoalesceObjectForKey(linkageCreatedKey).getStatus());

    }

    /**
     * This test creates two versions of the same entity and merges them
     * together into an entity with two versions. It then deletes the second
     * version and attempts to retrieve the record created during the second
     * version which should fail.
     * 
     * @throws Exception
     */
    @Test
    public void testRecords() throws Exception
    {

        CoalesceIteratorGetVersion versioner = new CoalesceIteratorGetVersion();

        // Create Entity
        TestEntity v1a = new TestEntity();
        v1a.initialize();
        TestRecord recordV0a = v1a.addRecord1();

        TestEntity v1b = new TestEntity();
        v1b.initialize(v1a.toXml());
        TestRecord recordV0b = v1b.addRecord1();

        // Merge the two version.
        TestEntity v2 = new TestEntity();
        v2.initialize(CoalesceEntity.mergeSyncEntity(v1a, v1b, "Derek", "127.0.0.1"));

        TestEntity v2b = new TestEntity();
        v2b.initialize(v2.toXml());

        TestEntity v3 = new TestEntity();
        v3.initialize(CoalesceEntity.mergeSyncEntity(v2, v2b, "Derek", "127.0.0.1"));

        // Delete Version 2
        iterator.delete(v3, 2);

        // Verify Records
        CoalesceEntity entity = versioner.getClonedVersion(v3, 3);
        assertEquals(ECoalesceObjectStatus.ACTIVE, entity.getCoalesceObjectForKey(recordV0a.getKey()).getStatus());
        assertEquals(ECoalesceObjectStatus.ACTIVE, v2.getCoalesceObjectForKey(recordV0b.getKey()).getStatus());

        // Restore Version 2
        iterator.restore(v3, 2);

        // Verify Records
        entity = versioner.getClonedVersion(v3, 3);
        assertEquals(ECoalesceObjectStatus.ACTIVE, v2.getCoalesceObjectForKey(recordV0a.getKey()).getStatus());
        assertEquals(ECoalesceObjectStatus.ACTIVE, v2.getCoalesceObjectForKey(recordV0b.getKey()).getStatus());

    }

    private static TestEntity updateEntity(CoalesceEntity entity,
                                           String modifiedBy,
                                           String recordKey,
                                           ECoalesceObjectStatus status,
                                           boolean booleanValue,
                                           double doubleValue) throws CoalesceException
    {

        // Clone Entity
        CoalesceEntity updated = new CoalesceEntity();
        updated.initialize(entity.toXml());

        updated.setStatus(status);

        // Update Fields
        TestRecord record = new TestRecord((CoalesceRecord) updated.getCoalesceObjectForKey(recordKey));
        record.getBooleanField().setValue(booleanValue);
        record.getDoubleField().setValue(doubleValue);

        // Merge Entity
        CoalesceEntity merged = CoalesceEntity.mergeSyncEntity(entity, updated, modifiedBy, "127.0.0.1");

        TestEntity results = new TestEntity();
        results.initialize(merged);

        return results;

    }

    private void verifyRecord(CoalesceEntity entity,
                              String recordKey,
                              int objectVersion,
                              ECoalesceObjectStatus status,
                              Boolean booleanValue,
                              Double doubleValue) throws CoalesceDataFormatException
    {

        CoalesceIteratorGetVersion version = new CoalesceIteratorGetVersion();

        CoalesceEntity versioned = version.getClonedVersion(entity, objectVersion);
        TestRecord record = new TestRecord((CoalesceRecord) versioned.getCoalesceObjectForKey(recordKey));

        assertEquals(status, versioned.getStatus());

        if (doubleValue == null)
        {
            assertTrue(record.getDoubleField().getBaseValue(),
                       StringHelper.isNullOrEmpty(record.getDoubleField().getBaseValue()));
        }
        else
        {
            assertEquals(doubleValue, record.getDoubleField().getValue(), 0);
        }

        assertEquals(booleanValue, record.getBooleanField().getValue());

    }

}
