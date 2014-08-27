package Coalesce.Common.Classification;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    @Test(expected = NullArgumentException.class)
    public void ConstructorMarkingStringNull()
    {

        @SuppressWarnings("unused")
        Marking mk = new Marking(null);
    }

    @Test
    public void GetClassifications() throws NoSuchMethodException, SecurityException, IllegalAccessException,
    IllegalArgumentException, InvocationTargetException
    {

        List<MarkingValue> classifications = new ArrayList<MarkingValue>();

        CallGetClassifications(classifications);

        FieldValuesTest.assertClassifications(FieldValuesTest.AllClassifications, classifications);

    }

    @Test
    public void GetIsJointSelectedCountriesZeroTest()
    {
        fail("Not implemented");
    }

    @Test
    public void GetIsJointSelectedCountriesOneTest()
    {
        fail("Not implemented");

    }

    @Test
    public void GetIsJointSelectedCoutnreisTwoTest()
    {
        fail("Not implemented");
    }

    private void CallGetClassifications(List<MarkingValue> classList) throws NoSuchMethodException, SecurityException,
    IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        Method method = Marking.class.getDeclaredMethod("GetClassificaitons", new Class[0]);
        method.setAccessible(true);

        method.invoke(null, classList);
    }
}
