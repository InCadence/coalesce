package Coalesce.Common.Classification;

import static org.junit.Assert.*;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class ISO3166CountryTest {

/*    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }*/

    @Test
    public void CountryEmptyConstructorTest() {
        
        ISO3166Country country = new ISO3166Country();
        
        assertNull(country.GetAlpha2());
        assertNull(country.GetAlpha3());
        assertNull(country.GetName());
    }
    
    @Test
    public void CountryCompleteConstructorTest() {
        
        ISO3166Country country = new ISO3166Country("Test1", "Test2", "Test3");
        
        assertEquals("Test1", country.GetAlpha2());
        assertEquals("Test2", country.GetAlpha3());
        assertEquals("Test3", country.GetName());
        
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountryConstructorNullAlpha2Test() {
        
        @SuppressWarnings("unused")
        ISO3166Country country = new ISO3166Country(null, "Test2", "Test3");
        
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountryConstructorNullAlpha3Test() {
        
        @SuppressWarnings("unused")
        ISO3166Country country = new ISO3166Country("Test1", null, "Test3");
        
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountryConstructorNullNameTest() {
        
        @SuppressWarnings("unused")
        ISO3166Country country = new ISO3166Country("Test1", "Test2", null);
        
    }
    
    @Test
    public void CountryConstructorWithAlpha3EqualToTest() {
        
        ISO3166Country country = ISO3166Country.WithAlpha3EqualTo("Test2");
        
        assertNull(country.GetAlpha2());
        assertEquals("Test2", country.GetAlpha3());
        assertNull(country.GetName());
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountryConstructorWithAlpha3EqualToNullTest() {
        
        @SuppressWarnings("unused")
        ISO3166Country country = ISO3166Country.WithAlpha3EqualTo(null);
        
    }
    
    @Test
    public void CountryUSATest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        assertEquals("US", country.GetAlpha2());
        assertEquals("USA", country.GetAlpha3());
        assertEquals("UNITED STATES", country.GetName());
        
    }
    
    @Test
    public void CountryUSAEqualsTest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        ISO3166Country otherCountry = new ISO3166Country("US", "USA", "UNITED STATES");
        
        assertEquals(country, otherCountry);
    }

    @Test
    public void CountryEqualsUSAEqualsAlpha2DifferentTest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        ISO3166Country otherCountry = new ISO3166Country("USA", "USA", "UNITED STATES");
        
        assertEquals(country, otherCountry);
    }
    
    @Test
    public void CountryEqualsUSAEqualsAlpha3DifferentTest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        ISO3166Country otherCountry = new ISO3166Country("US", "US", "UNITED STATES");
        
        assertEquals(country, otherCountry);
    }
    
    @Test
    public void CountryEqualsUSANotEqualsTest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        ISO3166Country otherCountry = new ISO3166Country("US", "US", "UNITED STATE");
        
        assertNotEquals(country, otherCountry);
    }
    
    @Test
    public void CountryEqualsAfganTest() {
        
        ISO3166Country country = new ISO3166Country("AF", "AFG", "AFGHANISTAN");
        
        ISO3166Country otherCountry = new ISO3166Country("AF", "AFG", "AFGHANISTAN");
        
        assertEquals(country, otherCountry);
        
    }
}
