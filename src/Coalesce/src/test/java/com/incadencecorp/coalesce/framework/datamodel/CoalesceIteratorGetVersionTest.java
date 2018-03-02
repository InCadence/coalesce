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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * These test ensure that you can retrieve versions of entities.
 * 
 * @author n78554
 *
 */
public class CoalesceIteratorGetVersionTest {

    /**
     * Creates 5 versions of the an entity and verifies retrieving each version.
     * 
     * @throws Exception
     */
    @Test
    public void testGetVersionFields() throws Exception
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

        record = new TestRecord((CoalesceRecord) v5.getCoalesceObjectForKey(record.getKey()));

        verifyRecord(v5, record.getKey(), 1, ECoalesceObjectStatus.ACTIVE, false, null);
        verifyRecord(v5, record.getKey(), 2, ECoalesceObjectStatus.ACTIVE, false, 5.1);
        verifyRecord(v5, record.getKey(), 3, ECoalesceObjectStatus.READONLY, true, 5.4);
        verifyRecord(v5, record.getKey(), 4, ECoalesceObjectStatus.DELETED, true, 1.4);
        verifyRecord(v5, record.getKey(), 5, ECoalesceObjectStatus.ACTIVE, false, 1.4);

    }

    /**
     * Updated the linkages and verifies that the history is created correctly.
     * 
     * @throws Exception
     */
    @Test
    public void testLinkages() throws Exception
    {

        // Create Linked Entity
        TestEntity linkedEntity = new TestEntity();
        linkedEntity.initialize();
        linkedEntity.setObjectVersion(5);

        // Create Entity
        TestEntity v1a = new TestEntity();
        v1a.initialize();

        EntityLinkHelper.linkEntities(v1a, ELinkTypes.CREATED, linkedEntity, true);

        // Update Entity Linkage (Delete)
        TestEntity v1b = new TestEntity();
        v1b.initialize(v1a.toXml());

        EntityLinkHelper.unLinkEntities(v1b, linkedEntity, ELinkTypes.CREATED);

        // Merge to Create History
        TestEntity v2 = new TestEntity();
        v2.initialize(CoalesceEntity.mergeSyncEntity(v1a, v1b, "merge1", "127.0.0.1"));

        // Change Entity2 Version
        linkedEntity.setObjectVersion(6);

        // Update Entity Linkage (Read Only)
        TestEntity v2b = new TestEntity();
        v2b.initialize(v1b.toXml());

        EntityLinkHelper.linkEntities(v2b, ELinkTypes.CREATED, linkedEntity, "Derek3", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, true);

        // Merge to Create History
        TestEntity v3 = new TestEntity();
        v3.initialize(CoalesceEntity.mergeSyncEntity(v2, v2b, "merge2", "127.0.0.1"));

        // Validate Results
        CoalesceEntity versioned;
        CoalesceLinkage linkage;

        CoalesceIteratorGetVersion versioner = new CoalesceIteratorGetVersion();

        // Get Version 1
        versioned = versioner.getClonedVersion(v3, 1);

        // Verify Linkage
        linkage = versioned.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.ACTIVE, linkage.getStatus());
        assertEquals(5, linkage.getEntity2ObjectVersion());
        assertEquals("", linkage.getModifiedBy());
        assertEquals("", linkage.getModifiedByIP());

        // Get Version 2
        versioned = versioner.getClonedVersion(v3, 2);

        // Verify Linkage
        linkage = versioned.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.DELETED, linkage.getStatus());
        assertEquals(5, linkage.getEntity2ObjectVersion());
        assertEquals("merge1", linkage.getModifiedBy());
        assertEquals("127.0.0.1", linkage.getModifiedByIP());

        // Get Current Version
        versioned = versioner.getClonedVersion(v3, 3);

        // Verify Linkage
        linkage = versioned.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.READONLY, linkage.getStatus());
        assertEquals(6, linkage.getEntity2ObjectVersion());
        assertEquals("merge2", linkage.getModifiedBy());
        assertEquals("127.0.0.1", linkage.getModifiedByIP());

    }

    /**
     * This test ensures creating new records in different versions work as
     * expected
     * 
     * @throws Exception
     */
    @Test
    public void testGetVersionRecords() throws Exception
    {

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

        assertHasRecords(v2.getRecordset1(), recordV0a.getKey(), recordV0b.getKey());

        CoalesceIteratorGetVersion iterator = new CoalesceIteratorGetVersion();

        assertHasRecords(iterator.getClonedVersion(v2, 1).getCoalesceObjectForNamePath(v2.getRecordset1().getNamePath()),
                         recordV0a.getKey());
        assertHasRecords(iterator.getClonedVersion(v2, 2).getCoalesceObjectForNamePath(v2.getRecordset1().getNamePath()),
                         recordV0a.getKey(),
                         recordV0b.getKey());

    }

    /**
     * This unit ensure that both logical paths for creating history retain the
     * last modified and date created time stamps of the original entity.
     * 
     * @throws Exception
     */
    @Test
    public void testVersionTimestamp() throws Exception
    {

        // Create Entity
        TestEntity v1a = new TestEntity();
        v1a.initialize();

        TestRecord recordv1a = v1a.addRecord1();
        recordv1a.getBooleanField().setValue(false);

        // Create Updated Entity
        TestEntity v1b = new TestEntity();
        v1b.initialize(v1a.toXml());

        // Change Field Value
        TestRecord recordv1b = new TestRecord((CoalesceRecord) v1b.getCoalesceObjectForKey(recordv1a.getKey()));
        recordv1b.getBooleanField().setValue(true);

        v1a.setDateCreated(v1a.getDateCreated().plusMinutes(5));
        recordv1a.getBooleanField().setLastModified(v1a.getDateCreated());

        // Merge Versions.
        TestEntity v2 = new TestEntity();
        v2.initialize(CoalesceEntity.mergeSyncEntity(v1a, v1b, "Derek", "127.0.0.1"));

        // Verify Entity History LastModified
        Assert.assertEquals(v1a.getLastModified().getMillis(), v2.getHistory()[0].getLastModified().getMillis(), 50);
        Assert.assertEquals(recordv1b.getBooleanField().getLastModified().getMillis(),
                            v2.getHistory()[0].getLastModified().getMillis(),
                            50);

        TestRecord recordv2 = new TestRecord((CoalesceRecord) v2.getCoalesceObjectForKey(recordv1a.getKey()));

        // Verify Field History LastModified
        Assert.assertEquals(recordv1a.getBooleanField().getDateCreated(), recordv2.getBooleanField().getDateCreated());
        Assert.assertEquals(recordv1a.getBooleanField().getLastModified().getMillis(),
                            recordv2.getBooleanField().getHistory()[0].getLastModified().getMillis(), 50);

    }

    private void assertHasRecords(CoalesceObject recordset, String... keys)
    {

        if (recordset instanceof CoalesceRecordset)
        {
            assertHasRecords((CoalesceRecordset) recordset, keys);
        }
        else
        {
            assertTrue("Invalid Object", false);
        }

    }

    private void assertHasRecords(CoalesceRecordset recordset, String... keys)
    {

        boolean found[] = new boolean[keys.length];

        Arrays.fill(found, false);

        List<CoalesceRecord> records = recordset.getRecords();

        assertEquals(keys.length, records.size());

        for (CoalesceRecord record : records)
        {
            for (int ii = 0; ii < keys.length; ii++)
            {
                if (keys[ii].equalsIgnoreCase(record.getKey()))
                {
                    found[ii] = true;
                }
            }
        }

        for (boolean wasFound : found)
        {
            assertTrue("Record Not Found", wasFound);
        }

    }

    private TestEntity updateEntity(CoalesceEntity entity,
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
