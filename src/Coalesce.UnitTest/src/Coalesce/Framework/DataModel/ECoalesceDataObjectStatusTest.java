package Coalesce.Framework.DataModel;

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
    public void GetTypeForLabelActiveStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getTypeForLabel("ACTIVE"));

    }

    @Test
    public void GetTypeForLabelDeletedStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.getTypeForLabel("DELETED"));

    }

    @Test
    public void GetTypeForLabelUnknownStatusUpperCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel("UNKNOWN"));

    }

    @Test
    public void GetTypeForLabelActiveStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getTypeForLabel("active"));

    }

    @Test
    public void GetTypeForLabelDeletedStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.getTypeForLabel("deleted"));

    }

    @Test
    public void GetTypeForLabelUnknownStatusLowerCaseTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel("unknown"));

    }

    @Test
    public void GetLabelActiveStatusTest()
    {

        String a = "Active";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.ACTIVE;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelDeletedStatusTest()
    {

        String a = "Deleted";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.DELETED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelUnknownStatusTest()
    {

        String a = "Unknown";
        ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.UNKNOWN;

        assertEquals(a, value.getLabel());
    }

}
