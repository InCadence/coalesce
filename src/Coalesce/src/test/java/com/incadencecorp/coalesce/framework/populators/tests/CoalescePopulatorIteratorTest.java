/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.populators.tests;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.iterators.CoalescePopulatorIterator;

/**
 * These tests ensure populating entities with random values works as expected.
 * 
 * @author n78554
 */
public class CoalescePopulatorIteratorTest {

    /**
     * Ensures no exceptions are thrown when populating entities with default
     * values.
     * 
     * @throws Exception
     */
    @Test
    public void test() throws Exception
    {
        CoalescePopulatorIterator iterator = new CoalescePopulatorIterator();

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertEquals(0, entity.getRecordset1().getRecords().size());

        iterator.iterate(entity);
        
        // Verify Record Creation
        Assert.assertEquals(1, entity.getRecordset1().getRecords().size());

        for (CoalesceField<?> field : entity.getRecordset1().getRecords().get(0).getFields())
        {
            switch (field.getDataType()) {
            case STRING_TYPE:
                Assert.assertTrue(field.getBaseValue().length() <= 20);
                break;
            default:
                // Do Nothing
                break;
            }
        }
        
    }

}
