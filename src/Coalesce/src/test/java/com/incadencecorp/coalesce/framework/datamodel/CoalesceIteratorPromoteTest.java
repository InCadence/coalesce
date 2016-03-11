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

import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * These test ensure that you can promote versions of entities.
 * 
 * @author n78554
 *
 */
public class CoalesceIteratorPromoteTest {

    /**
     * Creates 5 versions of the an entity and verifies promoting each version.
     * 
     * @throws Exception
     */
    @Test
    public void testPromoteFields() throws Exception
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
    public void testPromoteLinkages() throws Exception
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

        EntityLinkHelper.linkEntities(v2b, ELinkTypes.CREATED, linkedEntity, "Derek3", "127.0.0.1", "", true, true, true);

        // Merge to Create History
        TestEntity v3 = new TestEntity();
        v3.initialize(CoalesceEntity.mergeSyncEntity(v2, v2b, "merge2", "127.0.0.1"));

        // Verify
        CoalesceEntity promoted;
        CoalesceLinkage linkage;
        CoalesceHistory history;

        CoalesceIteratorPromote promotor = new CoalesceIteratorPromote();

        // Promote Version 0
        promoted = promotor.promoteClone(v3, 1, "promote", "127.0.0.5");

        // Verify Linkage
        linkage = promoted.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.ACTIVE, linkage.getStatus());
        assertEquals(5, linkage.getEntity2ObjectVersion());
        assertEquals("promote", linkage.getModifiedBy());
        assertEquals("127.0.0.5", linkage.getModifiedByIP());
        assertEquals(3, linkage.getHistory().length);

        // Verify History
        history = linkage.getHistoryRecord(linkage.getPreviousHistoryKey());

        assertEquals(ECoalesceObjectStatus.READONLY, history.getStatus());
        assertEquals(6, (int) Integer.valueOf(history.getAttribute("entity2objectversion")));
        assertEquals("merge2", history.getModifiedBy());
        assertEquals("127.0.0.1", history.getModifiedByIP());

        // Verify

        // Promote Version 1
        promoted = promotor.promoteClone(v3, 2, "promote", "127.0.0.5");

        // Verify Linkage
        linkage = promoted.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.DELETED, linkage.getStatus());
        assertEquals(5, linkage.getEntity2ObjectVersion());
        assertEquals("promote", linkage.getModifiedBy());
        assertEquals("127.0.0.5", linkage.getModifiedByIP());
        assertEquals(3, linkage.getHistory().length);

        // Verify History
        history = linkage.getHistoryRecord(linkage.getPreviousHistoryKey());

        assertEquals(ECoalesceObjectStatus.READONLY, history.getStatus());
        assertEquals(6, (int) Integer.valueOf(history.getAttribute("entity2objectversion")));
        assertEquals("merge2", history.getModifiedBy());
        assertEquals("127.0.0.1", history.getModifiedByIP());

        // Promote Current Version
        promoted = promotor.promoteClone(v3, 3, "promote", "127.0.0.5");

        // Verify Linkage
        linkage = promoted.getLinkageSection().getLinkages().values().iterator().next();

        assertEquals(ECoalesceObjectStatus.READONLY, linkage.getStatus());
        assertEquals(6, linkage.getEntity2ObjectVersion());
        assertEquals("merge2", linkage.getModifiedBy());
        assertEquals("127.0.0.1", linkage.getModifiedByIP());
        assertEquals(2, linkage.getHistory().length);

        // Verify History
        history = linkage.getHistoryRecord(linkage.getPreviousHistoryKey());

        assertEquals(ECoalesceObjectStatus.DELETED, history.getStatus());
        assertEquals(5, (int) Integer.valueOf(history.getAttribute("entity2objectversion")));
        assertEquals("merge1", history.getModifiedBy());
        assertEquals("127.0.0.1", history.getModifiedByIP());
    }

    /**
     * This test ensures creating new records in different versions work as
     * expected
     * 
     * @throws Exception
     */
    @Test
    public void testPromoteRecords() throws Exception
    {

        CoalesceIteratorPromote iterator = new CoalesceIteratorPromote();
        CoalesceRecordset promotedRecordset;
        CoalesceRecord verifyRecord;

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

        // Verify Records
        verifyRecord = (CoalesceRecord) v2.getCoalesceObjectForKey(recordV0a.getKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, verifyRecord.getStatus());
        assertEquals("", verifyRecord.getModifiedBy());
        assertEquals("", verifyRecord.getModifiedByIP());

        verifyRecord = (CoalesceRecord) v2.getCoalesceObjectForKey(recordV0b.getKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, verifyRecord.getStatus());
        assertEquals("Derek", verifyRecord.getModifiedBy());
        assertEquals("127.0.0.1", verifyRecord.getModifiedByIP());

        // Promote Version 0
        CoalesceEntity entity = iterator.promoteClone(v2, 1, "promote", "127.0.0.1");

        promotedRecordset = (CoalesceRecordset) entity.getCoalesceObjectForKey(v2.getRecordset1().getKey());

        // Verify Records
        verifyRecord = (CoalesceRecord) promotedRecordset.getCoalesceObjectForKey(recordV0a.getKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, verifyRecord.getStatus());
        assertEquals("", verifyRecord.getModifiedBy());
        assertEquals("", verifyRecord.getModifiedByIP());

        verifyRecord = (CoalesceRecord) promotedRecordset.getCoalesceObjectForKey(recordV0b.getKey());

        assertEquals(ECoalesceObjectStatus.DELETED, verifyRecord.getStatus());
        assertEquals("promote", verifyRecord.getModifiedBy());
        assertEquals("127.0.0.1", verifyRecord.getModifiedByIP());

        // Promote Version 1
        promotedRecordset = (CoalesceRecordset) iterator.promoteClone(v2, 2, "promote", "127.0.0.1").getCoalesceObjectForKey(v2.getRecordset1().getKey());

        // Verify Records
        verifyRecord = (CoalesceRecord) promotedRecordset.getCoalesceObjectForKey(recordV0a.getKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, verifyRecord.getStatus());
        assertEquals("", verifyRecord.getModifiedBy());
        assertEquals("", verifyRecord.getModifiedByIP());

        verifyRecord = (CoalesceRecord) promotedRecordset.getCoalesceObjectForKey(recordV0b.getKey());

        assertEquals(ECoalesceObjectStatus.ACTIVE, verifyRecord.getStatus());
        assertEquals("Derek", verifyRecord.getModifiedBy());
        assertEquals("127.0.0.1", verifyRecord.getModifiedByIP());

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

        CoalesceIteratorPromote version = new CoalesceIteratorPromote();

        TestRecord originalRecord = new TestRecord((CoalesceRecord) entity.getCoalesceObjectForKey(recordKey));

        CoalesceEntity promoted = version.promoteClone(entity, objectVersion, "promote", "127.0.0.1");
        TestRecord record = new TestRecord((CoalesceRecord) promoted.getCoalesceObjectForKey(recordKey));

        assertEquals(status, promoted.getStatus());

        // Verify Current Value
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

        // Object Promoted?
        if (entity.getObjectVersion() > objectVersion)
        {
            assertEquals("promote", promoted.getModifiedBy());
        }
        else
        {
            // Verify Same Entity
            assertEquals(entity.toXml(), promoted.toXml());
        }

        // Verify History
        if (originalRecord.getDoubleField().getObjectVersion() > objectVersion)
        {
            assertEquals(originalRecord.getDoubleField().getBaseValue(),
                         record.getDoubleField().getHistoryRecord(record.getDoubleField().getPreviousHistoryKey()).getBaseValue());
            assertEquals("promote", record.getDoubleField().getModifiedBy());
        }
        else
        {
            assertEquals(originalRecord.getDoubleField().getModifiedBy(), record.getDoubleField().getModifiedBy());
        }

        if (originalRecord.getBooleanField().getObjectVersion() > objectVersion)
        {
            assertEquals(originalRecord.getBooleanField().getBaseValue(),
                         record.getBooleanField().getHistoryRecord(record.getBooleanField().getPreviousHistoryKey()).getBaseValue());
            assertEquals("promote", record.getBooleanField().getModifiedBy());
        }
        else
        {
            assertEquals(originalRecord.getBooleanField().getModifiedBy(), record.getBooleanField().getModifiedBy());
        }
    }

}
