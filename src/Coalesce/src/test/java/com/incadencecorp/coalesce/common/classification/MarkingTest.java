package com.incadencecorp.coalesce.common.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
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
    public void getClassifications() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
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
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());

    }

    @Test
    public void constructorMarkingStringNull()
    {

        Marking mk = new Marking(null);

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringInvalidPortion()
    {

        Marking mk = new Marking("()");

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringInvalidNotPortion()
    {

        Marking mk = new Marking("SECRE");

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    public void constructorMarkingStringNoSlashPortion()
    {

        Marking mk = new Marking("(R)");

        assertEquals("(R)", mk.toPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortion()
    {

        Marking mk = new Marking("RESTRICTED");

        assertEquals("(R)", mk.toPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    public void constructorMarkingStringSlashPortionBlank()
    {

        Marking mk = new Marking("(//)");

        assertEquals("(U)", mk.toPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("RESTRICTED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionBlank()
    {

        Marking mk = new Marking("//");

        assertEquals("(U)", mk.toPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("UNCLASSIFIED", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    public void constructorMarkingStringSlashPortionBeginClass()
    {

        Marking mk = new Marking("(TS//)");

        assertEquals("(TS)", mk.toPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionBeginClass()
    {

        Marking mk = new Marking("TOP SECRET//");

        assertEquals("(TS)", mk.toPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterEmpty()
    {

        Marking mk = new Marking("(//TS)");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterEmpty()
    {

        Marking mk = new Marking("//TOP SECRET");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfter()
    {

        Marking mk = new Marking("(//AND TS)");

        assertEquals("(//AND TS)", mk.toPortionString());
        assertEquals("//AND TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfter()
    {

        Marking mk = new Marking("//AND SECRET");

        assertEquals("(//AND S)", mk.toPortionString());
        assertEquals("//AND SECRET", mk.toString());

        assertEquals("SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterInvalid()
    {

        Marking mk = new Marking("(//XYZ TS)");

        assertEquals("(//TS)", mk.toPortionString());
        assertEquals("//TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterInvalid()
    {

        Marking mk = new Marking("//XYZ SECRET");

        assertEquals("(//S)", mk.toPortionString());
        assertEquals("//SECRET", mk.toString());

        assertEquals("SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("(//JOINT TS AND)");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("//JOINT SECRET AND");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointInvalidTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND");

        assertEquals("(//)", mk.toPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.getClassification());
        assertFalse(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJoint()
    {

        Marking mk = new Marking("(//JOINT TS AND USA)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJoint()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND USA");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointTopSecretReverseCountries()
    {

        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertEquals("(//JOINT TS USA AND)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET USA AND", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterCosmic()
    {

        Marking mk = new Marking("(//CTS-B)");

        assertEquals("(//CTS-B)", mk.toPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterNato()
    {

        Marking mk = new Marking("(//NU)");

        assertEquals("(//NU)", mk.toPortionString());
        assertEquals("//NATO UNCLASSIFIED", mk.toString());

        assertEquals("NATO UNCLASSIFIED", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterAtomal()
    {

        Marking mk = new Marking("(//NC-A)");

        assertEquals("(//NC-A)", mk.toPortionString());
        assertEquals("//CONFIDENTIAL ATOMAL", mk.toString());

        assertEquals("CONFIDENTIAL ATOMAL", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterCosmic()
    {

        Marking mk = new Marking("//COSMIC TOP SECRET BOHEMIA");

        assertEquals("(//CTS-B)", mk.toPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterNato()
    {

        Marking mk = new Marking("//NATO SECRET");

        assertEquals("(//NS)", mk.toPortionString());
        assertEquals("//NATO SECRET", mk.toString());

        assertEquals("NATO SECRET", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterAtomal()
    {

        Marking mk = new Marking("//SECRET ATOMAL");

        assertEquals("(//NS-A)", mk.toPortionString());
        assertEquals("//SECRET ATOMAL", mk.toString());

        assertEquals("SECRET ATOMAL", mk.getClassification().getTitle());
        assertTrue(mk.getIsNATO());
        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO-LES)");

        assertEquals("(//JOINT TS AND USA//FOUO-LES)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO-LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO-LES");

        assertEquals("(//JOINT S AND USA//FOUO-LES)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO-LES", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO)");

        assertEquals("(//JOINT TS AND USA//FOUO)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO");

        assertEquals("(//JOINT S AND USA//FOUO)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointLES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//LES)");

        assertEquals("(//JOINT TS AND USA//LES)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointLES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LES");

        assertEquals("(//JOINT S AND USA//LES)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//RELIDO)");

        assertEquals("(//JOINT TS AND USA//RELIDO)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertTrue(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//RELIDO");

        assertEquals("(//JOINT S AND USA//RELIDO)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertTrue(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//PR)");

        assertEquals("(//JOINT TS AND USA//PR)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertTrue(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//PROPIN");

        assertEquals("(//JOINT S AND USA//PR)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertTrue(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FISA)");

        assertEquals("(//JOINT TS AND USA//FISA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertTrue(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FISA");

        assertEquals("(//JOINT S AND USA//FISA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertTrue(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//IMC)");

        assertEquals("(//JOINT TS AND USA//IMC)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertTrue(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//IMCON");

        assertEquals("(//JOINT S AND USA//IMC)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertTrue(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//OC)");

        assertEquals("(//JOINT TS AND USA//OC)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertTrue(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ORCON");

        assertEquals("(//JOINT S AND USA//OC)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertTrue(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DSEN)");

        assertEquals("(//JOINT TS AND USA//DSEN)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertTrue(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DSEN");

        assertEquals("(//JOINT S AND USA//DSEN)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertTrue(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//NF)");

        assertEquals("(//JOINT TS AND USA//NF)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertTrue(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//NOFORN");

        assertEquals("(//JOINT S AND USA//NF)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertTrue(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(1, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(1, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getDisplayOnlyCountries().get(1));
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(1, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU");

        assertEquals("(//JOINT S AND USA//REL TO BMU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(1, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU, COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU, COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertTrue(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointSUBNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU-NF)");

        assertEquals("(//JOINT TS AND USA//SBU-NF)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertTrue(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());

    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointSBUNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU NOFORN");

        assertEquals("(//JOINT S AND USA//SBU-NF)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertTrue(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU)");

        assertEquals("(//JOINT TS AND USA//SBU)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertTrue(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU");

        assertEquals("(//JOINT S AND USA//SBU)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertTrue(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//XD)");

        assertEquals("(//JOINT TS AND USA//XD)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertTrue(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//EXDIS");

        assertEquals("(//JOINT S AND USA//XD)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertTrue(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointDS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DS)");

        assertEquals("(//JOINT TS AND USA//DS)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertTrue(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointDS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LIMITED DISTRIBUTION");

        assertEquals("(//JOINT S AND USA//DS)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertTrue(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(1, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB");

        assertEquals("(//JOINT S AND USA//ACCM-BOB)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(1, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB/JIM)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(2, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertEquals("JIM", mk.getNicknames().get(1));
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//ACCM-BOB/JIM)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(2, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertEquals("JIM", mk.getNicknames().get(1));
        assertFalse(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-)");

        assertEquals("(//JOINT TS AND USA)", mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-");

        assertEquals("(//JOINT S AND USA)", mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertFalse(mk.getIsFOUO());
        assertFalse(mk.getIsLES());
        assertFalse(mk.getIsORCON());
        assertFalse(mk.getIsIMCON());
        assertFalse(mk.getIsRELIDO());
        assertFalse(mk.getIsPROPIN());
        assertFalse(mk.getIsFISA());
        assertFalse(mk.getIsNOFORN());
        assertFalse(mk.getIsDSEN());
        assertEquals(0, mk.getReleaseToCountries().size());
        assertEquals(0, mk.getDisplayOnlyCountries().size());
        assertFalse(mk.getIsLIMDIS());
        assertFalse(mk.getIsEXDIS());
        assertFalse(mk.getIsSBU());
        assertFalse(mk.getIsSBUNF());
        assertEquals(0, mk.getNicknames().size());
        assertFalse(mk.hasDeseminationControls());
        assertFalse(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO-LES//FOUO/LES/RELIDO/PR/FISA/IMC/OC/DSEN//NF/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU-NF/SBU/XD/DS/ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//DS/XD/SBU/SBU-NF/ACCM-BOB/JIM)",
                     mk.toPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN/ACCM-BOB/JIM",
                     mk.toString());

        assertEquals("JOINT TOP SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertTrue(mk.getIsORCON());
        assertTrue(mk.getIsIMCON());
        assertTrue(mk.getIsRELIDO());
        assertTrue(mk.getIsPROPIN());
        assertTrue(mk.getIsFISA());
        assertTrue(mk.getIsNOFORN());
        assertTrue(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("IND"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("VIR"), mk.getDisplayOnlyCountries().get(1));
        assertTrue(mk.getIsLIMDIS());
        assertTrue(mk.getIsEXDIS());
        assertTrue(mk.getIsSBU());
        assertTrue(mk.getIsSBUNF());
        assertEquals(2, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertEquals("JIM", mk.getNicknames().get(1));
        assertTrue(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void constructorMarkingStringSlashNoPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO-LES/FOUO/LES/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU NOFORN/SBU/EXDIS/LIMITED DISTRIBUTION/ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//DS/XD/SBU/SBU-NF/ACCM-BOB/JIM)",
                     mk.toPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN/ACCM-BOB/JIM",
                     mk.toString());

        assertEquals("JOINT SECRET", mk.getClassification().getTitle());
        assertFalse(mk.getIsNATO());
        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("AND"), mk.getSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.getUSA(), mk.getSelectedCountries().get(1));
        assertTrue(mk.getIsFOUO());
        assertTrue(mk.getIsLES());
        assertTrue(mk.getIsORCON());
        assertTrue(mk.getIsIMCON());
        assertTrue(mk.getIsRELIDO());
        assertTrue(mk.getIsPROPIN());
        assertTrue(mk.getIsFISA());
        assertTrue(mk.getIsNOFORN());
        assertTrue(mk.getIsDSEN());
        assertEquals(2, mk.getReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("BMU"), mk.getReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("COL"), mk.getReleaseToCountries().get(1));
        assertEquals(2, mk.getDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("IND"), mk.getDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.getCountryByAlpha3("VIR"), mk.getDisplayOnlyCountries().get(1));
        assertTrue(mk.getIsLIMDIS());
        assertTrue(mk.getIsEXDIS());
        assertTrue(mk.getIsSBU());
        assertTrue(mk.getIsSBUNF());
        assertEquals(2, mk.getNicknames().size());
        assertEquals("BOB", mk.getNicknames().get(0));
        assertEquals("JIM", mk.getNicknames().get(1));
        assertTrue(mk.hasDeseminationControls());
        assertTrue(mk.hasOtherDeseminationControls());
    }

    @Test
    public void setIsNATOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsNATO());

        mk.setIsNATO(true);
        assertTrue(mk.getIsNATO());
    }

    @Test
    public void getIsJointSelectedCountriesZeroTest()
    {
        Marking mk = new Marking("");
        mk.getSelectedCountries().clear();

        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());

    }

    @Test
    public void getIsJointSelectedCountriesOneTest()
    {
        Marking mk = new Marking("//USA TOP SECRET ");

        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());

    }

    @Test
    public void getIsJointSelectedCoutnreisTwoTest()
    {
        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());

    }

    @Test
    public void setIsFOUOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsFOUO());

        mk.setIsFOUO(true);
        assertTrue(mk.getIsFOUO());
    }

    @Test
    public void setIsLESTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsLES());

        mk.setIsLES(true);
        assertTrue(mk.getIsLES());

    }

    @Test
    public void setIsORCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsORCON());

        mk.setIsORCON(true);
        assertTrue(mk.getIsORCON());
    }

    @Test
    public void setIsIMCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsIMCON());

        mk.setIsIMCON(true);
        assertTrue(mk.getIsIMCON());
    }

    @Test
    public void getIsDisplayOnlyTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//DISPLAY ONLY AND USA");

        assertTrue(mk.getIsDisplayOnly());
    }

    @Test
    public void getIsDisplayOnlyEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsDisplayOnly());
    }

    @Test
    public void setIsDSENTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsDSEN());

        mk.setIsDSEN(true);
        assertTrue(mk.getIsDSEN());
    }

    @Test
    public void setIsFISATest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsFISA());

        mk.setIsFISA(true);
        assertTrue(mk.getIsFISA());
    }

    @Test
    public void setIsPROPINTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsPROPIN());

        mk.setIsPROPIN(true);
        assertTrue(mk.getIsPROPIN());
    }

    @Test
    public void setIsRELIDOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsRELIDO());

        mk.setIsRELIDO(true);
        assertTrue(mk.getIsRELIDO());

    }

    @Test
    public void getIsReleaseToTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED USA//REL TO AND USA");

        assertTrue(mk.getIsReleaseTo());
    }

    @Test
    public void getIsReleaseToEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsReleaseTo());
    }

    @Test
    public void setIsNOFORNTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsNOFORN());

        mk.setIsNOFORN(true);
        assertTrue(mk.getIsNOFORN());
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

        assertFalse(mk.getIsLIMDIS());

        mk.setIsLIMDIS(true);
        assertTrue(mk.getIsLIMDIS());
    }

    @Test
    public void setIsEXDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsEXDIS());

        mk.setIsEXDIS(true);
        assertTrue(mk.getIsEXDIS());
    }

    @Test
    public void setIsSBUTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsSBU());

        mk.setIsSBU(true);

        assertTrue(mk.getIsSBU());
    }

    @Test
    public void setIsSBUNFTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsSBUNF());

        mk.setIsSBUNF(true);
        assertTrue(mk.getIsSBUNF());
    }

    @Test
    public void getIsACCMTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//ACCM-BOB/JIM");

        assertTrue(mk.getIsACCM());
    }

    @Test
    public void getIsACCMEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsDisplayOnly());
    }

    @SuppressWarnings("unchecked")
    private List<MarkingValue> callGetClassifications() throws NoSuchMethodException, 
            IllegalAccessException, InvocationTargetException
    {

        Method method = Marking.class.getDeclaredMethod("getClassifications", (Class<?>[]) null);
        method.setAccessible(true);

        Object results = method.invoke(null, (Object[]) null);

        return (List<MarkingValue>) results;
    }
}
