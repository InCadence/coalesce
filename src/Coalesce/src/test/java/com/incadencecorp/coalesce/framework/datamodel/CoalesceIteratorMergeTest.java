/**
 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
 * /// Copyright 2015 - Lockheed Martin Corporation, All Rights Reserved /// ///
 * Notwithstanding any contractor copyright notice, the government has ///
 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
 * 252.227-7014. Use of this work other than as specifically authorized by ///
 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
 * Rights. The Government has the right to use, modify, /// reproduce, perform,
 * display, release or disclose this computer software /// in whole or in part,
 * in any manner, and for any purpose whatsoever, /// and to have or authorize
 * others to do so. /// /// Distribution Statement D. Distribution authorized to
 * the Department of /// Defense and U.S. DoD contractors only in support of US
 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
 * Program /// Management under the Director of the Office of Naval
 * Intelligence. ///
 * -------------------------------UNCLASSIFIED---------------------------------
 */

package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * These test ensure that you can promote versions of entities by specifing XML.
 * 
 * @author n78554
 *
 */
public class CoalesceIteratorMergeTest {

    /**
     * Ensures that promoting an earlier version of an entity with out a record
     * that was added later will mark the record as deleted.
     * 
     * @throws Exception
     */
    @Test
    public void testPromote() throws Exception
    {

        // Create Iterators
        CoalesceIteratorMerge merger = new CoalesceIteratorMerge();
        CoalesceIteratorGetVersion versioner = new CoalesceIteratorGetVersion();

        // Get Entities
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();

        // Initialize Entities
        entity1.initialize();
        entity2.initialize(entity1.toXml());

        // Create New Record in Each
        TestRecord record1 = entity1.addRecord1();
        TestRecord record2 = entity2.addRecord1();

        record1.getIntegerField().setValue(1);
        record2.getIntegerField().setValue(2);

        // Merge Entities (Entity2's record should be marked as version 2)
        CoalesceEntity result = CoalesceEntity.mergeSyncEntity(entity1, entity2, null, null);

        // Promote Version 1 
        TestEntity promoted = new TestEntity();
        promoted.initialize(merger.promote("derek", "127.0.0.1", result, entity1));

        // Validate
        assertTrue(promoted.getRecordset1().getCoalesceObjectForKey(record2.getKey()).isMarkedDeleted());

        // Record did not exists so it should have been pruned
        assertEquals(null, versioner.getClonedVersion(promoted, 1).getCoalesceObjectForKey(record2.getKey()));
        // Record was added in this version
        assertFalse(versioner.getClonedVersion(promoted, 2).getCoalesceObjectForKey(record2.getKey()).isMarkedDeleted());
        // Record should be marked as deleted because the promotion did not have the record.
        assertTrue(versioner.getClonedVersion(promoted, 3).getCoalesceObjectForKey(record2.getKey()).isMarkedDeleted());

    }

}
