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

public class ECoalesceObjectStatusTest {

    @Test
    public void getTypeForLabelActiveStatusUpperCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.ACTIVE, ECoalesceObjectStatus.getTypeForLabel("ACTIVE"));

    }

    @Test
    public void getTypeForLabelDeletedStatusUpperCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.DELETED, ECoalesceObjectStatus.getTypeForLabel("DELETED"));

    }

    @Test
    public void getTypeForLabelUnknownStatusUpperCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.UNKNOWN, ECoalesceObjectStatus.getTypeForLabel("UNKNOWN"));

    }

    @Test
    public void getTypeForLabelActiveStatusLowerCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.ACTIVE, ECoalesceObjectStatus.getTypeForLabel("active"));

    }

    @Test
    public void getTypeForLabelDeletedStatusLowerCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.DELETED, ECoalesceObjectStatus.getTypeForLabel("deleted"));

    }

    @Test
    public void getTypeForLabelUnknownStatusLowerCaseTest()
    {
        assertEquals(ECoalesceObjectStatus.UNKNOWN, ECoalesceObjectStatus.getTypeForLabel("unknown"));

    }

    @Test
    public void getLabelActiveStatusTest()
    {

        String a = "Active";
        ECoalesceObjectStatus value = ECoalesceObjectStatus.ACTIVE;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelDeletedStatusTest()
    {

        String a = "Deleted";
        ECoalesceObjectStatus value = ECoalesceObjectStatus.DELETED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelUnknownStatusTest()
    {

        String a = "Unknown";
        ECoalesceObjectStatus value = ECoalesceObjectStatus.UNKNOWN;

        assertEquals(a, value.getLabel());
    }

}
