package Coalesce.Common.Classification;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class MarkingTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void CompareToContainsTest()
    {

        MarkingValue mv = new MarkingValue("", "Testing Testing TOP SECRET TESTING", "", "");

        assertTrue(mv.GetTitle().contains("TOP SECRET"));
        assertFalse(mv.GetTitle().contains("top secret"));
    }

    @Test
    public void ListContainsUSATest()
    {

        List<ISO3166Country> countries = FieldValues.GetListOfCountries();

        assertTrue(countries.contains(ISO3166Country.USA()));
    }

    @Test
    public void ListNotContainsUSATest()
    {

        List<ISO3166Country> countries = new ArrayList<ISO3166Country>();
        countries.add(FieldValues.GetCountryByName("ZAMBIA"));

        assertFalse(countries.contains(ISO3166Country.USA()));

    }

    @Test
    public void TernaryConditionalTest()
    {

        List<String> joint = new ArrayList<String>();
        joint.add("TOP");
        joint.add("SECRET");
        joint.add("TEST");

        assertTrue(4 < (joint.contains("TOP") ? 5 : 4));
        assertFalse(5 < (joint.contains("TOP") ? 5 : 4));

        assertTrue(3 < (joint.contains("Other") ? 5 : 4));
        assertFalse(4 < (joint.contains("Other") ? 5 : 4));

    }

    @Test(expected=NullArgumentException.class)
    public void ConstructorMarkingStringNull() {
        
        @SuppressWarnings("unused")
        Marking mk = new Marking(null);
    }
    
    @Test
    public void GetIsJointSelectedCountriesZeroTest() {
        fail("Not implemented");
    }
    
    @Test
    public void GetIsJointSelectedCountriesOneTest() {
        fail("Not implemented");
       
    }
    
    @Test
    public void GetIsJointSelectedCoutnreisTwoTest() {
        fail("Not implemented");
    }

    @Test
    public void test()
    {
        fail("Not implemented");
    }

}
