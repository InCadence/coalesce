/*-----------------------------------------------------------------------------'
 Copyright 2015 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;

/**
 * This unit test is performed against {@link CoalesceMap}
 * 
 * @author n78554
 */
public class CoalesceMapTest {

    private static final String SECTION_NAME = "section";
    private static final String RECORDSET_NAME = "recordset";

    /*--------------------------------------------------------------------------
    Public Tests
    --------------------------------------------------------------------------*/

    private <T> Map<String, T> createRecordset(Class<T> clazz)
    {

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        return CoalesceMap.createRecordSet(CoalesceSection.create(entity, SECTION_NAME), RECORDSET_NAME, clazz);

    }

    /**
     * Attempt to create a recordset of an invalid type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidType()
    {
        createRecordset(Date.class);
    }

    /**
     * Verifies that adding and removing items from the hash correctly updates
     * the size.
     */
    @Test
    public void testSize()
    {

        Map<String, Integer> recordset = createRecordset(Integer.class);

        recordset.put("1", 1);
        recordset.put("2", 2);
        recordset.put("3", 3);
        recordset.put("4", 4);

        assertEquals(4, recordset.size());

        recordset.remove("2");

        assertEquals(3, recordset.size());

        recordset.put("2", 2);

        assertEquals(4, recordset.size());

    }

    /**
     * Verifies that the isEmpty method works correctly.
     */
    @Test
    public void testIsEmpty()
    {
        Map<String, Integer> recordset = createRecordset(Integer.class);

        assertTrue(recordset.isEmpty());

        recordset.put("1", 1);
        recordset.put("2", 2);

        assertFalse(recordset.isEmpty());

        recordset.clear();

        assertTrue(recordset.isEmpty());

    }

    /**
     * Verifies that the contains method works correctly as items are added and
     * removed..
     */
    @Test
    public void testContains()
    {
        Map<String, Double> recordset = createRecordset(Double.class);

        recordset.put("1", 1.0);
        recordset.put("2", 2.0);

        assertTrue(recordset.containsKey("1"));
        assertTrue(recordset.containsKey("2"));
        assertFalse(recordset.containsKey("3"));

        assertTrue(recordset.containsValue(1.0));
        assertTrue(recordset.containsValue(2.0));
        assertFalse(recordset.containsValue(3.0));
        assertFalse(recordset.containsValue("3"));

        recordset.remove("2");

        assertFalse(recordset.containsKey("2"));
        assertFalse(recordset.containsValue(2.0));

    }

    /**
     * Verifies that the get method works correctly as items are added and
     * removed.
     */
    @Test
    public void testGet()
    {
        Map<String, String> recordset = createRecordset(String.class);

        recordset.put("1", "1");
        recordset.put("2", "2");

        assertEquals("1", recordset.get("1"));
        assertEquals("2", recordset.get("2"));

        recordset.remove("2");

        assertEquals(null, recordset.get("2"));

    }

    /**
     * Verifies that the putAll method works correctly.
     */
    @Test
    public void testPutAll()
    {

        DateTime date = JodaDateTimeHelper.nowInUtc();

        Map<String, DateTime> recordset = createRecordset(DateTime.class);

        recordset.put("0", date);

        HashMap<String, DateTime> map = new HashMap<String, DateTime>();

        map.put("1", date.minusDays(1));
        map.put("2", date.minusDays(2));
        map.put("3", date.minusDays(3));
        map.put("4", date.minusDays(4));

        recordset.putAll(map);

        assertEquals(5, recordset.size());
        assertEquals(date, recordset.get("0"));
        assertEquals(date.minusDays(1), recordset.get("1"));
        assertEquals(date.minusDays(2), recordset.get("2"));
        assertEquals(date.minusDays(3), recordset.get("3"));
        assertEquals(date.minusDays(4), recordset.get("4"));

    }

    /**
     * Verifies that the keySet method works correctly.
     */
    @Test
    public void testKeySet()
    {
        Map<String, Float> recordset = createRecordset(Float.class);

        recordset.put("0", 0.0F);
        recordset.put("1", 1.0F);
        recordset.put("2", 2.0F);
        recordset.put("3", 3.0F);
        recordset.put("4", 4.0F);

        recordset.remove("0");

        Set<String> set = recordset.keySet();

        assertEquals(4, set.size());
        assertFalse(set.contains("0"));
        assertTrue(set.contains("1"));
        assertTrue(set.contains("2"));
        assertTrue(set.contains("3"));
        assertTrue(set.contains("4"));

        Collection<Float> collection = recordset.values();

        assertEquals(4, collection.size());
        assertFalse(collection.contains(0.0F));
        assertTrue(collection.contains(1.0F));
        assertTrue(collection.contains(2.0F));
        assertTrue(collection.contains(3.0F));
        assertTrue(collection.contains(4.0F));

        Set<Entry<String, Float>> entries = recordset.entrySet();

        assertEquals(4, entries.size());

        for (Entry<String, Float> entry : entries)
        {
            switch (entry.getKey()) {
            case "1":
                assertEquals(1.0F, entry.getValue(), 0);
                break;
            case "2":
                assertEquals(2.0F, entry.getValue(), 0);
                break;
            case "3":
                assertEquals(3.0F, entry.getValue(), 0);
                break;
            case "4":
                assertEquals(4.0F, entry.getValue(), 0);
                break;
            default:
                fail("Invalid Entry");
            }
        }

    }

    @Ignore // There are no assertions
    @Test
    public void testMerge() throws Exception
    {

        String path = "/" + SECTION_NAME + "/" + RECORDSET_NAME;

        CoalesceSection section;
        CoalesceMap<Integer> recordset1;
        CoalesceMap<Integer> recordset2;
        CoalesceMap<Integer> merged;

        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();
        entity1.setName("Test");
        section = CoalesceSection.create(entity1, SECTION_NAME);
        recordset1 = CoalesceMap.createRecordSet(section, RECORDSET_NAME, Integer.class);

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize(entity1.toXml());
        recordset2 = new CoalesceMap<Integer>((CoalesceRecordset) entity2.getCoalesceObjectForNamePath(path));

        recordset1.put("1", 1);
        recordset2.put("1", 2);

        CoalesceEntity entity3 = new CoalesceEntity();
        entity3.initialize(CoalesceEntity.mergeSyncEntity(entity1, entity2, null, null).toXml());
        merged = new CoalesceMap<Integer>((CoalesceRecordset) entity3.getCoalesceObjectForNamePath(path));

        // TODO Merging two object with the same key added will create a
        // duplicate entry.

        System.out.println(merged.toXml());

        System.out.println(merged.get("1"));
        System.out.println(merged.size());

        merged.put("1", 5);

        System.out.println(merged.toXml());

        System.out.println(merged.get("1"));
        System.out.println(merged.size());

    }

    @Ignore // There are no assertions
    @Test
    public void testMerge2() throws Exception
    {

        String path = "/" + SECTION_NAME + "/" + RECORDSET_NAME;

        CoalesceSection section;
        CoalesceMap<Integer> recordset1;
        CoalesceMap<Integer> recordset2;
        CoalesceMap<Integer> merged;

        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();
        entity1.setName("Test");
        section = CoalesceSection.create(entity1, SECTION_NAME);
        recordset1 = CoalesceMap.createRecordSet(section, RECORDSET_NAME, Integer.class);

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize(entity1.toXml());
        recordset2 = new CoalesceMap<Integer>((CoalesceRecordset) entity2.getCoalesceObjectForNamePath(path));

        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("1", 1);

        recordset1.putAll(map);

        map.put("1", 2);

        recordset2.putAll(map);

        CoalesceEntity entity3 = new CoalesceEntity();
        entity3.initialize(CoalesceEntity.mergeSyncEntity(entity1, entity2, null, null).toXml());
        merged = new CoalesceMap<Integer>((CoalesceRecordset) entity3.getCoalesceObjectForNamePath(path));

        // TODO Merging two object with the same key added will create a
        // duplicate entry.

        System.out.println(merged.toXml());

        System.out.println(merged.get("1"));
        System.out.println(merged.size());

        merged.put("1", 5);

        System.out.println(merged.toXml());

        System.out.println(merged.get("1"));
        System.out.println(merged.size());

    }

    /**
     * This test ensures that putting a key that was previously removed will
     * active the old record instead of creating a new one.
     * 
     * @throws Exception
     */
    @Test
    public void testRemove() throws Exception
    {
        Map<String, Integer> recordset = createRecordset(Integer.class);

        recordset.put("1", 1);

        assertEquals(1, ((CoalesceMap<Integer>) recordset).getCount());

        recordset.remove("1");

        assertEquals(0, ((CoalesceMap<Integer>) recordset).getCount());
        assertEquals(1, ((CoalesceMap<Integer>) recordset).getAllRecords().size());

        recordset.put("1", 2);

        assertEquals(1, ((CoalesceMap<Integer>) recordset).getCount());
        assertEquals(1, ((CoalesceMap<Integer>) recordset).getAllRecords().size());

    }

}
