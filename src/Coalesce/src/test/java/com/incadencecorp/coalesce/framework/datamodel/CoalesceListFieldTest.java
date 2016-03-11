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

import java.util.Random;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;

/**
 * Unit test designed to test the list field data types from Coalesce
 * 
 * @author n78554
 */
public class CoalesceListFieldTest {

    private static TestRecord record;
    private static Random rand;

    @BeforeClass
    public static void initialize()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        // Create Section
        CoalesceSection section = CoalesceSection.create(entity, "test");

        // Create Record Sets
        // CoalesceRecordset recordset = createCoalesceRecordset(section,
        // "test");

        record = new TestRecord(TestRecord.createCoalesceRecordset(section, "test").addNew());

        rand = new Random();
    }

    @Test
    public void testCasting()
    {
        record.getDoubleListField();
        record.getIntegerListField();
        record.getFloatListField();
        record.getLongListField();
        record.getStringListField();
        record.getGUIDListField();

    }

    @Test
    public void testDoubleArray() throws CoalesceDataFormatException
    {

        double[] values = {
                rand.nextDouble(), rand.nextDouble()
        };

        record.getDoubleListField().setValue(values);

        assertEquals(Double.toString(values[0]) + "," + Double.toString(values[1]),
                     record.getDoubleListField().getBaseValue());
        assertEquals(values[0], record.getDoubleListField().getValue()[0], 0);
        assertEquals(values[1], record.getDoubleListField().getValue()[1], 0);

        record.getDoubleListField().addValues(values);

        assertEquals(values[0], record.getDoubleListField().getValue()[0], 0);
        assertEquals(values[1], record.getDoubleListField().getValue()[1], 0);
        assertEquals(values[0], record.getDoubleListField().getValue()[2], 0);
        assertEquals(values[1], record.getDoubleListField().getValue()[3], 0);

    }

    @Test
    public void testLongArray() throws CoalesceDataFormatException
    {

        long[] values = {
                rand.nextLong(), rand.nextLong()
        };

        record.getLongListField().setValue(values);

        assertEquals(Long.toString(values[0]) + "," + Long.toString(values[1]), record.getLongListField().getBaseValue());
        assertEquals(values[0], record.getLongListField().getValue()[0], 0);
        assertEquals(values[1], record.getLongListField().getValue()[1], 0);

        record.getLongListField().addValues(values);

        assertEquals(values[0], record.getLongListField().getValue()[0], 0);
        assertEquals(values[1], record.getLongListField().getValue()[1], 0);
        assertEquals(values[0], record.getLongListField().getValue()[2], 0);
        assertEquals(values[1], record.getLongListField().getValue()[3], 0);
    }

    @Test
    public void testIntegerArray() throws CoalesceDataFormatException
    {

        int[] values = {
                rand.nextInt(), rand.nextInt()
        };

        record.getIntegerListField().setValue(values);

        assertEquals(Integer.toString(values[0]) + "," + Integer.toString(values[1]),
                     record.getIntegerListField().getBaseValue());
        assertEquals(values[0], record.getIntegerListField().getValue()[0]);
        assertEquals(values[1], record.getIntegerListField().getValue()[1]);

        record.getIntegerListField().addValues(values);

        assertEquals(values[0], record.getIntegerListField().getValue()[0], 0);
        assertEquals(values[1], record.getIntegerListField().getValue()[1], 0);
        assertEquals(values[0], record.getIntegerListField().getValue()[2], 0);
        assertEquals(values[1], record.getIntegerListField().getValue()[3], 0);
    }

    @Test
    public void testFloatArray() throws CoalesceDataFormatException
    {

        float[] values = {
                rand.nextFloat(), rand.nextFloat()
        };

        record.getFloatListField().setValue(values);

        assertEquals(Float.toString(values[0]) + "," + Float.toString(values[1]), record.getFloatListField().getBaseValue());
        assertEquals(values[0], record.getFloatListField().getValue()[0], 0);
        assertEquals(values[1], record.getFloatListField().getValue()[1], 0);

        record.getFloatListField().addValues(values);

        assertEquals(values[0], record.getFloatListField().getValue()[0], 0);
        assertEquals(values[1], record.getFloatListField().getValue()[1], 0);
        assertEquals(values[0], record.getFloatListField().getValue()[2], 0);
        assertEquals(values[1], record.getFloatListField().getValue()[3], 0);

    }

    @Test
    public void testStringArray() throws CoalesceDataFormatException
    {

        String[] values = {
                "Hello World", "Derek", "comma,test"
        };

        record.getStringListField().setValue(values);

        for (String value : record.getStringListField().getValue())
        {
            System.out.println(value);
        }

        assertEquals("Hello World,Derek,\"comma,test\"", record.getStringListField().getBaseValue());
        assertEquals(values[0], record.getStringListField().getValue()[0]);
        assertEquals(values[1], record.getStringListField().getValue()[1]);
        assertEquals(values[2], record.getStringListField().getValue()[2]);

        record.getStringListField().addValues(values);

        assertEquals(values[0], record.getStringListField().getValue()[0]);
        assertEquals(values[1], record.getStringListField().getValue()[1]);
        assertEquals(values[2], record.getStringListField().getValue()[2]);
        assertEquals(values[0], record.getStringListField().getValue()[3]);
        assertEquals(values[1], record.getStringListField().getValue()[4]);
        assertEquals(values[2], record.getStringListField().getValue()[5]);
    }

    @Test
    public void testGUIDArray() throws CoalesceDataFormatException
    {

        UUID[] values = {
                UUID.randomUUID(), UUID.randomUUID()
        };

        record.getGUIDListField().setValue(values);

        assertEquals(GUIDHelper.getGuidString(values[0]) + "," + GUIDHelper.getGuidString(values[1]),
                     record.getGUIDListField().getBaseValue());
        assertEquals(0, values[0].compareTo(record.getGUIDListField().getValue()[0]));
        assertEquals(0, values[1].compareTo(record.getGUIDListField().getValue()[1]));

        record.getGUIDListField().addValues(values);

        assertEquals(values[0], record.getGUIDListField().getValue()[0]);
        assertEquals(values[1], record.getGUIDListField().getValue()[1]);
        assertEquals(values[0], record.getGUIDListField().getValue()[2]);
        assertEquals(values[1], record.getGUIDListField().getValue()[3]);
    }

    /**
     * Ensures that you can set a list field to null to clear the values.
     * 
     * @throws Exception
     */
    @Test
    public void testSettingNull() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceSection section = CoalesceSection.create(entity, "test");
        TestRecord record = new TestRecord(TestRecord.createCoalesceRecordset(section, "test").addNew());

        // Set Fields
        record.getDoubleListField().addValues(new double[] {
                1.0, 2.0
        });
        record.getIntegerListField().addValues(new int[] {
                1, 2
        });
        record.getLongListField().addValues(new long[] {
                1, 2
        });
        record.getFloatListField().addValues(new float[] {
                1.0f, 2.0f
        });
        record.getStringListField().addValues(new String[] {
                "1", "2"
        });
        record.getGUIDListField().addValues(new UUID[] {
                UUID.randomUUID(), UUID.randomUUID()
        });

        // Verify
        assertEquals(2, record.getDoubleListField().getValue().length);
        assertEquals(2, record.getIntegerListField().getValue().length);
        assertEquals(2, record.getLongListField().getValue().length);
        assertEquals(2, record.getFloatListField().getValue().length);
        assertEquals(2, record.getStringListField().getValue().length);
        assertEquals(2, record.getGUIDListField().getValue().length);

        // Clear Field
        record.getDoubleListField().setValue(null);
        record.getIntegerListField().setValue(null);
        record.getLongListField().setValue(null);
        record.getFloatListField().setValue(null);
        record.getStringListField().setValue(null);
        record.getGUIDListField().setValue(null);

        // Verify
        assertEquals(0, record.getDoubleListField().getValue().length);
        assertEquals(0, record.getIntegerListField().getValue().length);
        assertEquals(0, record.getLongListField().getValue().length);
        assertEquals(0, record.getFloatListField().getValue().length);
        assertEquals(0, record.getStringListField().getValue().length);
        assertEquals(0, record.getGUIDListField().getValue().length);

    }

    // @AfterClass
    // public static void printXml()
    // {
    //
    // System.out.println(record.toXml());
    // }

}
