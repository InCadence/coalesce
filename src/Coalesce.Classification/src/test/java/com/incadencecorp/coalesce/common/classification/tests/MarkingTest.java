package com.incadencecorp.coalesce.common.classification.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.classification.FieldValues;
import com.incadencecorp.coalesce.common.classification.ISO3166Country;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.classification.MarkingValue;

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

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FieldValuesTest.setUpBeforeClass();
    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void compareToContainsTest()
    {

        MarkingValue mv = new MarkingValue("", "Testing Testing TOP SECRET TESTING", "", "");

        assertTrue(mv.getTitle().contains("TOP SECRET"));
        assertFalse(mv.getTitle().contains("top secret"));
    }

    @Test
    public void listContainsUSATest()
    {

        List<ISO3166Country> countries = FieldValues.getListOfCountries();

        assertTrue(countries.contains(ISO3166Country.getUSA()));
    }

    @Test
    public void listNotContainsUSATest()
    {

        List<ISO3166Country> countries = new ArrayList<ISO3166Country>();
        countries.add(FieldValues.getCountryByName("ZAMBIA"));

        assertFalse(countries.contains(ISO3166Country.getUSA()));

    }

    @Test
    public void ternaryConditionalTest()
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

    @Test
    public void getClassifications() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {

        List<MarkingValue> classifications = callGetClassifications();

        FieldValuesTest.assertClassifications(FieldValuesTest.getAllClassifications(), classifications);

    }

    @Test
    public void compareToTopSecretTopSecretTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("TOP SECRET");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void compareToTopSecretSecretTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("SECRET");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToTopSecretConfidentialTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToTopSecretRestrictedTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToTopSecretUnclassifiedTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToSecretTopSecretTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToSecretSecretTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("SECRET");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void compareToSecretConfidentialTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToSecretRestrictedTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToSecretUnclassifiedTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToConfidentialTopSecretTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToConfidentialSecretTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToConfidentialConfidentialTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void compareToConfidentialRestrictedTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToConfidentialUnclassifiedTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToRestrictedTopSecretTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToRestrictedSecretTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToRestrictedConfidentialTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToRestrictedRestrictedTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("RESTRICTED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void compareToRestrictedUnclassifiedTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void compareToUnclassifiedTopSecretTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToUnclassifiedSecretTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToUnclassifiedConfidentialTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToUnclassifiedRestrictedTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("RESTRICTED");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void compareToUnclassifiedUnclassifiedTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void compareToUnknownUnclassifiedTest()
    {

        Marking first = new Marking("//ATA");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void constructorMarkingString()
    {

        Marking mk = new Marking();

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);

    }

    @Test
    public void constructorMarkingStringNull()
    {

        Marking mk = new Marking(null);

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringInvalidPortion()
    {

        Marking mk = new Marking("()");

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringInvalidNotPortion()
    {

        Marking mk = new Marking("SECRE");

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    public void constructorMarkingStringNoSlashPortion()
    {

        Marking mk = new Marking("(R)");

        assertEquals("(R)", mk.toPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortion()
    {

        Marking mk = new Marking("RESTRICTED");

        assertEquals("(R)", mk.toPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    public void constructorMarkingStringSlashPortionBlank()
    {

        Marking mk = new Marking("(//)");

        assertEquals("(U)", mk.toPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionBlank()
    {

        Marking mk = new Marking("//");

        assertEquals("(U)", mk.toPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    public void constructorMarkingStringSlashPortionBeginClass()
    {

        Marking mk = new Marking("(TS//)");

        assertEquals("(TS)", mk.toPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionBeginClass()
    {

        Marking mk = new Marking("TOP SECRET//");

        assertEquals("(TS)", mk.toPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashPortionClassAfterEmpty()
    {

        Marking mk = new Marking("(//TS)");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashNoPortionClassAfterEmpty()
    {

        Marking mk = new Marking("//TOP SECRET");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfter()
    {

        Marking mk = new Marking("(//AND TS)");

        assertEquals("(//AND TS)", mk.toPortionString());
        assertEquals("//AND TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfter()
    {

        Marking mk = new Marking("//AND SECRET");

        assertEquals("(//AND S)", mk.toPortionString());
        assertEquals("//AND SECRET", mk.toString());

        assertEquals("SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterInvalid()
    {

        Marking mk = new Marking("(//XYZ TS)");

        assertEquals("(//TS)", mk.toPortionString());
        assertEquals("//TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterInvalid()
    {

        Marking mk = new Marking("//XYZ SECRET");

        assertEquals("(//S)", mk.toPortionString());
        assertEquals("//SECRET", mk.toString());

        assertEquals("SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("(//JOINT TS AND)");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashNoPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("//JOINT SECRET AND");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashNoPortionClassAfterJointInvalidTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJoint()
    {

        Marking mk = new Marking("(//JOINT TS AND USA)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJoint()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND USA");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointTopSecretReverseCountries()
    {

        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertEquals("(//JOINT TS USA AND)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET USA AND", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterCosmic()
    {

        Marking mk = new Marking("(//CTS-B)");

        assertEquals("(//CTS-B)", mk.toPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterNato()
    {

        Marking mk = new Marking("(//NU)");

        assertEquals("(//NU)", mk.toPortionString());
        assertEquals("//NATO UNCLASSIFIED", mk.toString());

        assertEquals("NATO UNCLASSIFIED", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterAtomal()
    {

        Marking mk = new Marking("(//NC-A)");

        assertEquals("(//NC-A)", mk.toPortionString());
        assertEquals("//CONFIDENTIAL ATOMAL", mk.toString());

        assertEquals("CONFIDENTIAL ATOMAL", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterCosmic()
    {

        Marking mk = new Marking("//COSMIC TOP SECRET BOHEMIA");

        assertEquals("(//CTS-B)", mk.toPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterNato()
    {

        Marking mk = new Marking("//NATO SECRET");

        assertEquals("(//NS)", mk.toPortionString());
        assertEquals("//NATO SECRET", mk.toString());

        assertEquals("NATO SECRET", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterAtomal()
    {

        Marking mk = new Marking("//SECRET ATOMAL");

        assertEquals("(//NS-A)", mk.toPortionString());
        assertEquals("//SECRET ATOMAL", mk.toString());

        assertEquals("SECRET ATOMAL", mk.getClassification().getTitle());
        assertTrue(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO//LES)");

        assertEquals("(//JOINT TS AND USA//FOUO//LES)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO//LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertTrue(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO//LES");

        assertEquals("(//JOINT S AND USA//FOUO//LES)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO//LES", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertTrue(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO)");

        assertEquals("(//JOINT TS AND USA//FOUO)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO");

        assertEquals("(//JOINT S AND USA//FOUO)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointLES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//LES)");

        assertEquals("(//JOINT TS AND USA//LES)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertTrue(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointLES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LES");

        assertEquals("(//JOINT S AND USA//LES)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertTrue(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//RELIDO)");

        assertEquals("(//JOINT TS AND USA//RELIDO)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertTrue(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//RELIDO");

        assertEquals("(//JOINT S AND USA//RELIDO)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertTrue(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//PR)");

        assertEquals("(//JOINT TS AND USA//PR)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertTrue(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//PROPIN");

        assertEquals("(//JOINT S AND USA//PR)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertTrue(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FISA)");

        assertEquals("(//JOINT TS AND USA//FISA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertTrue(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FISA");

        assertEquals("(//JOINT S AND USA//FISA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertTrue(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//IMC)");

        assertEquals("(//JOINT TS AND USA//IMC)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertTrue(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//IMCON");

        assertEquals("(//JOINT S AND USA//IMC)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertTrue(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//OC)");

        assertEquals("(//JOINT TS AND USA//OC)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertTrue(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ORCON");

        assertEquals("(//JOINT S AND USA//OC)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertTrue(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DSEN)");

        assertEquals("(//JOINT TS AND USA//DSEN)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertTrue(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DSEN");

        assertEquals("(//JOINT S AND USA//DSEN)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertTrue(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//NF)");

        assertEquals("(//JOINT TS AND USA//NF)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertTrue(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//NOFORN");

        assertEquals("(//JOINT S AND USA//NF)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertTrue(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(1, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(1, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(1, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU");

        assertEquals("(//JOINT S AND USA//REL TO BMU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(1, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU, COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU, COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertTrue(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointSUBNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU-NF)");

        assertEquals("(//JOINT TS AND USA//SBU-NF)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertTrue(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointSBUNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU NOFORN");

        assertEquals("(//JOINT S AND USA//SBU-NF)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertTrue(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU)");

        assertEquals("(//JOINT TS AND USA//SBU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertTrue(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU");

        assertEquals("(//JOINT S AND USA//SBU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertTrue(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//XD)");

        assertEquals("(//JOINT TS AND USA//XD)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertTrue(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//EXDIS");

        assertEquals("(//JOINT S AND USA//XD)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertTrue(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DS)");

        assertEquals("(//JOINT TS AND USA//DS)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertTrue(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LIMITED DISTRIBUTION");

        assertEquals("(//JOINT S AND USA//DS)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertTrue(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(1, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB");

        assertEquals("(//JOINT S AND USA//ACCM-BOB)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(1, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB/JIM)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(2, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertEquals("JIM", mk.getACCMNicknames()[1].getNickname());
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//ACCM-BOB/JIM)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(2, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertEquals("JIM", mk.getACCMNicknames()[1].getNickname());
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringWithACCMCaveats()
    {

        Marking mk = new Marking("(U//ACCM-XXX AAA BBB/YYY)");

        System.out.println(mk.toString());

        // assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("UNCLASSIFIED//ACCM-XXX AAA BBB/YYY", mk.toString());

        // assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertEquals("UNITED STATES", mk.getSelectedCountries().get(0).getName());
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(2, mk.getACCMNicknames().length);
        assertEquals("XXX", mk.getACCMNicknames()[0].getNickname());
        assertEquals("YYY", mk.getACCMNicknames()[1].getNickname());
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.isFOUO());
        assertFalse(mk.isLES());
        assertFalse(mk.isORCON());
        assertFalse(mk.isIMCON());
        assertFalse(mk.isRELIDO());
        assertFalse(mk.isPROPIN());
        assertFalse(mk.isFISA());
        assertFalse(mk.isNOFORN());
        assertFalse(mk.isDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.isLIMDIS());
        assertFalse(mk.isEXDIS());
        assertFalse(mk.isSBU());
        assertFalse(mk.isSBUNF());
        assertEquals(0, mk.getACCMNicknames().length);
        assertFalse(mk.hasDisseminationControls());
        assertFalse(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO/LES/RELIDO/PR/FISA/IMC/OC/DSEN//NF/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU-NF/SBU/XD/DS//ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB/JIM//FOUO/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//LES/DS/XD/SBU/SBU-NF)",
                     mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB/JIM//FOUO/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LES/LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN",
                     mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertTrue(mk.isLES());
        assertTrue(mk.isORCON());
        assertTrue(mk.isIMCON());
        assertTrue(mk.isRELIDO());
        assertTrue(mk.isPROPIN());
        assertTrue(mk.isFISA());
        assertTrue(mk.isNOFORN());
        assertTrue(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("IND"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("VIR"), mk.getDisplayOnlyCountries().get(1));
        assertTrue(mk.isLIMDIS());
        assertTrue(mk.isEXDIS());
        assertTrue(mk.isSBU());
        assertTrue(mk.isSBUNF());
        assertEquals(2, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertEquals("JIM", mk.getACCMNicknames()[1].getNickname());
        assertTrue(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO/LES/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU NOFORN/SBU/EXDIS/LIMITED DISTRIBUTION//ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//ACCM-BOB/JIM//FOUO/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//LES/DS/XD/SBU/SBU-NF)",
                     mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB/JIM//FOUO/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LES/LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN",
                     mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.isNATO());
        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.isFOUO());
        assertTrue(mk.isLES());
        assertTrue(mk.isORCON());
        assertTrue(mk.isIMCON());
        assertTrue(mk.isRELIDO());
        assertTrue(mk.isPROPIN());
        assertTrue(mk.isFISA());
        assertTrue(mk.isNOFORN());
        assertTrue(mk.isDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("IND"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("VIR"), mk.getDisplayOnlyCountries().get(1));
        assertTrue(mk.isLIMDIS());
        assertTrue(mk.isEXDIS());
        assertTrue(mk.isSBU());
        assertTrue(mk.isSBUNF());
        assertEquals(2, mk.getACCMNicknames().length);
        assertEquals("BOB", mk.getACCMNicknames()[0].getNickname());
        assertEquals("JIM", mk.getACCMNicknames()[1].getNickname());
        assertTrue(mk.hasDisseminationControls());
        assertTrue(mk.hasOtherDisseminationControls());
    }

    @Test
    public void setIsNATOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isNATO());

        mk.setIsNATO(true);
        assertTrue(mk.isNATO());
    }

    @Test
    public void isJointSelectedCountriesZeroTest()
    {
        Marking mk = new Marking("");
        mk.getSelectedCountries().clear();

        assertFalse(mk.isJOINT());
        assertEquals(0, mk.getSelectedCountries().size());

    }

    @Test
    public void isJointSelectedCountriesOneTest()
    {
        Marking mk = new Marking("//USA TOP SECRET ");

        assertFalse(mk.isJOINT());
        assertEquals(1, mk.getSelectedCountries().size());

    }

    @Test
    public void isJointSelectedCoutnreisTwoTest()
    {
        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertTrue(mk.isJOINT());
        assertEquals(2, mk.getSelectedCountries().size());

    }

    @Test
    public void setIsFOUOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isFOUO());

        mk.setIsFOUO(true);
        assertTrue(mk.isFOUO());
    }

    @Test
    public void setIsLESTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isLES());

        mk.setIsLES(true);
        assertTrue(mk.isLES());

    }

    @Test
    public void setIsORCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isORCON());

        mk.setIsORCON(true);
        assertTrue(mk.isORCON());
    }

    @Test
    public void setIsIMCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isIMCON());

        mk.setIsIMCON(true);
        assertTrue(mk.isIMCON());
    }

    @Test
    public void isDisplayOnlyTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//DISPLAY ONLY AND USA");

        assertTrue(mk.isDisplayOnly());
    }

    @Test(expected = IllegalArgumentException.class)
    public void isDisplayOnlyEmptyTest()
    {
        Marking mk = new Marking("//UNCLASSIFIED");
    }

    @Test
    public void setIsDSENTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isDSEN());

        mk.setIsDSEN(true);
        assertTrue(mk.isDSEN());
    }

    @Test
    public void setIsFISATest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isFISA());

        mk.setIsFISA(true);
        assertTrue(mk.isFISA());
    }

    @Test
    public void setIsPROPINTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isPROPIN());

        mk.setIsPROPIN(true);
        assertTrue(mk.isPROPIN());
    }

    @Test
    public void setIsRELIDOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isRELIDO());

        mk.setIsRELIDO(true);
        assertTrue(mk.isRELIDO());

    }

    @Test
    public void isReleaseToTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED USA//REL TO AND USA");

        assertTrue(mk.isReleaseTo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void isReleaseToEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.isReleaseTo());
    }

    @Test
    public void setIsNOFORNTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isNOFORN());

        mk.setIsNOFORN(true);
        assertTrue(mk.isNOFORN());
    }

    @Test
    public void setClassificationTest()
    {

        Marking mk = new Marking();

        MarkingValueTest.assertMarkingValue("", "UNCLASSIFIED", "", "U", mk.getClassification());

        mk.setClassification(new MarkingValue("Parent1", "Title1", "Abb1", "Portion1"));

        MarkingValueTest.assertMarkingValue("Parent1", "Title1", "Abb1", "Portion1", mk.getClassification());
    }

    @Test
    public void setIsLIMDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isLIMDIS());

        mk.setIsLIMDIS(true);
        assertTrue(mk.isLIMDIS());
    }

    @Test
    public void setIsEXDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isEXDIS());

        mk.setIsEXDIS(true);
        assertTrue(mk.isEXDIS());
    }

    @Test
    public void setIsSBUTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isSBU());

        mk.setIsSBU(true);

        assertTrue(mk.isSBU());
    }

    @Test
    public void setIsSBUNFTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.isSBUNF());

        mk.setIsSBUNF(true);
        assertTrue(mk.isSBUNF());
    }

    @Test
    public void isACCMTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//ACCM-BOB/JIM");

        assertTrue(mk.getACCMNicknames().length > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isACCMEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.isDisplayOnly());
    }

    @Test
    public void isRSENTest()
    {

        Marking mk = new Marking("UNCLASSIFIED");

        mk.setIsRSEN(true);

        assertEquals("UNCLASSIFIED//RSEN", mk.toString());
        assertEquals("(U//RS)", mk.toPortionString());
    }

    @Test
    public void isNODISTest()
    {

        Marking mk = new Marking("UNCLASSIFIED");

        mk.setIsNODIS(true);

        assertEquals("UNCLASSIFIED//NODIS", mk.toString());
        assertEquals("(U//ND)", mk.toPortionString());

    }

    @Test
    public void isLESNFTest()
    {

        Marking mk = new Marking("UNCLASSIFIED");

        mk.setIsLESNF(true);

        assertEquals("UNCLASSIFIED//LES NOFORN", mk.toString());
        assertEquals("(U//LES-NF)", mk.toPortionString());

    }

    @Test
    public void isSSITest()
    {

        Marking mk = new Marking("UNCLASSIFIED");

        mk.setIsSSI(true);

        assertEquals("UNCLASSIFIED//SSI", mk.toString());
        assertEquals("(U//SSI)", mk.toPortionString());

    }

    @Test
    public void usaDismTest()
    {

        Marking mk = new Marking("UNCLASSIFIED");

        mk.getReleaseToCountries().add(FieldValues.getCountryByName("GERMANY"));
        mk.getReleaseToCountries().add(FieldValues.getCountryByName("UNITED STATES"));

        assertEquals("UNCLASSIFIED//REL TO USA, DEU", mk.toString());
        assertEquals("(U//REL TO USA, DEU)", mk.toPortionString());

    }
    
    @Test
    public void sapTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//SAR-BP-A12 CDE 125-121//NOFORN");

        assertEquals(1, mk.getSAPPrograms().length);
        assertEquals("BP", mk.getSAPPrograms()[0].getProgramName());
        assertEquals(2, mk.getSAPPrograms()[0].getCompartments().length);
        assertEquals("121", mk.getSAPPrograms()[0].getCompartments()[0].getCompartmentName());
        assertEquals("A12", mk.getSAPPrograms()[0].getCompartments()[1].getCompartmentName());

        assertEquals(0, mk.getSAPPrograms()[0].getCompartments()[0].getSubCompartments().length);
        assertEquals(2, mk.getSAPPrograms()[0].getCompartments()[1].getSubCompartments().length);

        assertEquals("125", mk.getSAPPrograms()[0].getCompartments()[1].getSubCompartments()[0]);
        assertEquals("CDE", mk.getSAPPrograms()[0].getCompartments()[1].getSubCompartments()[1]);

        assertTrue(mk.isNOFORN());
        
        
    }

    @SuppressWarnings("unchecked")
    private List<MarkingValue> callGetClassifications() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        Method method = Marking.class.getDeclaredMethod("getClassifications", (Class<?>[]) null);
        method.setAccessible(true);

        Object results = method.invoke(null, (Object[]) null);

        return (List<MarkingValue>) results;
    }
}
