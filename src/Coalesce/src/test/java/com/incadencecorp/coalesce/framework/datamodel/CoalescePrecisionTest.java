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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;

/**
 * Unit test designed to ensure numeric fields have the correct precision.
 * 
 * @author n78554
 */
public class CoalescePrecisionTest {

    private static TestRecord record;

    /**
     * Creates an entity that will be used to perform these test
     */
    @BeforeClass
    public static void initialize()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        // Create Section
        CoalesceSection section = CoalesceSection.create(entity, "test");

        // Create Record Sets
        CoalesceRecordset recordset = TestRecord.createCoalesceRecordset(section, "test");

        record = new TestRecord(recordset.addNew());

    }

    /**
     * @throws CoalesceDataFormatException
     */
    @Test
    public void doublePrecisionTest() throws CoalesceDataFormatException
    {
        record.getDoubleField().setValue(11.1234567890123456789);

        assertEquals(11.1234567890123456789, record.getDoubleField().getValue(), 0);
        assertEquals(11.123456789012345, record.getDoubleField().getValue(), 0.00000000000001);
        assertEquals("11.123456789012346", record.getDoubleField().getBaseValue());
    }

    /**
     * @throws CoalesceDataFormatException
     */
    @Test
    public void floatPrecisionTest() throws CoalesceDataFormatException
    {
        record.getFloatField().setValue(11.1234567890123456789f);

        assertEquals(11.1234567890123456789f, record.getFloatField().getValue(), 0);
        assertEquals(11.123456f, record.getFloatField().getValue(), 0.000001);
        assertEquals("11.123457", record.getFloatField().getBaseValue());
    }

    /**
     * Displays the record as XML for visual inspection.
     */
    @AfterClass
    public static void printXml()
    {
        //System.out.println(record.toXml());
    }

}
