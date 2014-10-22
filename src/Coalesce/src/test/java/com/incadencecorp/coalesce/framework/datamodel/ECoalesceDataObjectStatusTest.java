package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
 * 
 * @AfterClass public static void tearDownAfterClass() throws Exception { }
 * 
 * @Before public void setUp() throws Exception { }
 * 
 * @After public void tearDown() throws Exception { }
 */

public class ECoalesceDataObjectStatusTest {

    @Test
    public void getTypeForLabelActiveStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getTypeForLabel("ACTIVE"));

    }

    @Test
    public void getTypeForLabelDeletedStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.getTypeForLabel("DELETED"));

    }

    @Test
    public void getTypeForLabelUnknownStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel("UNKNOWN"));

    }

    @Test
    public void getTypeForLabelActiveStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getTypeForLabel("active"));

    }

    @Test
    public void getTypeForLabelDeletedStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.getTypeForLabel("deleted"));

    }

    @Test
    public void getTypeForLabelUnknownStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel("unknown"));

    }

    @Test
    public void getLabelActiveStatusTest()
    {

        String a = "Active";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.ACTIVE;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelDeletedStatusTest()
    {

        String a = "Deleted";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.DELETED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelUnknownStatusTest()
    {

        String a = "Unknown";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.UNKNOWN;

        assertEquals(a, value.getLabel());
    }

}
