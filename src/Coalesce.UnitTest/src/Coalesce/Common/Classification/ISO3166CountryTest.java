package Coalesce.Common.Classification;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

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
        
        assertCountry(null, null, null, country);
    }
    
    @Test
    public void CountryCompleteConstructorTest() {
        
        ISO3166Country country = new ISO3166Country("Test1", "Test2", "Test3");
        
        assertCountry("Test1", "Test2", "Test3", country);
        
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
        
        ISO3166Country country = ISO3166Country.withAlpha3EqualTo("Test2");
        
        assertCountry(null, "Test2", null, country);
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountryConstructorWithAlpha3EqualToNullTest() {
        
        @SuppressWarnings("unused")
        ISO3166Country country = ISO3166Country.withAlpha3EqualTo(null);
        
    }
    
    @Test
    public void CountryUSATest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        assertCountry("US", "USA", "UNITED STATES", country);
        
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
    public void CountryEqualsUSAEqualsNameDifferentTest() {
        
        ISO3166Country country = ISO3166Country.USA();
        
        ISO3166Country otherCountry = new ISO3166Country("US", "USA", "UNITED STATE");
        
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
    
    @Test
    public void CountrySetAlpha2Test() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setAlpha2("Testing2");

        assertCountry("Testing2", "USA", "UNITED STATES", country);
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountrySetAlpha2NullTest() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setAlpha2(null);
        
    }
 
    @Test
    public void CountrySetAlpha3Test() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setAlpha3("Testing3");

        assertCountry("US", "Testing3", "UNITED STATES", country);
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountrySetAlpha3NullTest() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setAlpha3(null);
        
    }
    
    @Test
    public void CountrySetNameTest() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setName("Testing3");

        assertCountry("US", "USA", "Testing3", country);
    }
    
    @Test(expected=NullArgumentException.class)
    public void CountrySetNameNullTest() {
        
        ISO3166Country country = ISO3166Country.USA();

        country.setName(null);
        
    }
    
    @Test()
    public void CountrySerializeDeserializeTest() throws IOException, ClassNotFoundException {
        
        ISO3166Country country = new ISO3166Country("AF", "AFG", "AFGHANISTAN");
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(country);
        out.close();
        byteOut.close();
        
        ISO3166Country desCountry = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desCountry = (ISO3166Country)in.readObject();
        in.close();
        byteIn.close();
        
        assertCountry("AF", "AFG", "AFGHANISTAN", desCountry);
    }
    
    @Test()
    public void CountrySerializeDeserializeBlankCountryTest() throws IOException, ClassNotFoundException {
        
        ISO3166Country country = new ISO3166Country();
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(country);
        out.close();
        byteOut.close();
        
        ISO3166Country desCountry = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desCountry = (ISO3166Country)in.readObject();
        in.close();
        byteIn.close();
        
        assertCountry(null, null, null, desCountry);

    }
    
    public static void assertCountry(String expectedAlpha2,
                                     String expectedAlpha3,
                                     String expectedName,
                                     ISO3166Country actual) {
    
        assertEquals(expectedAlpha2, actual.getAlpha2());
        assertEquals(expectedAlpha3, actual.getAlpha3());
        assertEquals(expectedName, actual.getName());

    }
    
    public static void assertCountry(ISO3166Country expected, ISO3166Country actual) {
    
        assertEquals(expected.getAlpha2(), actual.getAlpha2());
        assertEquals(expected.getAlpha3(), actual.getAlpha3());
        assertEquals(expected.getName(), actual.getName());

    }
}
