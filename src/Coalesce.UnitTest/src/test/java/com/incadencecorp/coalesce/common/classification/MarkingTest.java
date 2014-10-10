package com.incadencecorp.coalesce.common.classification;

import static org.junit.Assert.*;

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
    public void CompareToContainsTest()
    {

        MarkingValue mv = new MarkingValue("", "Testing Testing TOP SECRET TESTING", "", "");

        assertTrue(mv.getTitle().contains("TOP SECRET"));
        assertFalse(mv.getTitle().contains("top secret"));
    }

    @Test
    public void ListContainsUSATest()
    {

        List<ISO3166Country> countries = FieldValues.getListOfCountries();

        assertTrue(countries.contains(ISO3166Country.getUSA()));
    }

    @Test
    public void ListNotContainsUSATest()
    {

        List<ISO3166Country> countries = new ArrayList<ISO3166Country>();
        countries.add(FieldValues.getCountryByName("ZAMBIA"));

        assertFalse(countries.contains(ISO3166Country.getUSA()));

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

    @Test
    public void GetClassifications() throws NoSuchMethodException, SecurityException, IllegalAccessException,
    IllegalArgumentException, InvocationTargetException
    {

        List<MarkingValue> classifications = CallGetClassifications();

        FieldValuesTest.assertClassifications(FieldValuesTest.AllClassifications, classifications);

    }

    @Test
    public void CompareToTopSecretTopSecretTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("TOP SECRET");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void CompareToTopSecretSecretTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("SECRET");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToTopSecretConfidentialTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToTopSecretRestrictedTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToTopSecretUnclassifiedTest()
    {

        Marking first = new Marking("//ATA TOP SECRET");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToSecretTopSecretTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToSecretSecretTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("SECRET");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void CompareToSecretConfidentialTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToSecretRestrictedTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToSecretUnclassifiedTest()
    {

        Marking first = new Marking("//ATA SECRET");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToConfidentialTopSecretTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToConfidentialSecretTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToConfidentialConfidentialTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void CompareToConfidentialRestrictedTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("RESTRICTED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToConfidentialUnclassifiedTest()
    {

        Marking first = new Marking("//ATA CONFIDENTIAL");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToRestrictedTopSecretTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToRestrictedSecretTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToRestrictedConfidentialTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToRestrictedRestrictedTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("RESTRICTED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void CompareToRestrictedUnclassifiedTest()
    {

        Marking first = new Marking("//ATA RESTRICTED");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(-1, first.compareTo(second));

    }

    @Test
    public void CompareToUnclassifiedTopSecretTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("TOP SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToUnclassifiedSecretTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("SECRET");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToUnclassifiedConfidentialTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("CONFIDENTIAL");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToUnclassifiedRestrictedTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("RESTRICTED");

        assertEquals(1, first.compareTo(second));

    }

    @Test
    public void CompareToUnclassifiedUnclassifiedTest()
    {

        Marking first = new Marking("//ATA UNCLASSIFIED");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void CompareToUnknownUnclassifiedTest()
    {

        Marking first = new Marking("//ATA");
        Marking second = new Marking("UNCLASSIFIED");

        assertEquals(0, first.compareTo(second));

    }

    @Test
    public void ConstructorMarkingString()
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
    public void ConstructorMarkingStringNull()
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
    public void ConstructorMarkingStringInvalidPortion()
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
    public void ConstructorMarkingStringInvalidNotPortion()
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

    public void ConstructorMarkingStringNoSlashPortion()
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
    public void ConstructorMarkingStringSlashNoPortion()
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

    public void ConstructorMarkingStringSlashPortionBlank()
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
    public void ConstructorMarkingStringSlashNoPortionBlank()
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

    public void ConstructorMarkingStringSlashPortionBeginClass()
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
    public void ConstructorMarkingStringSlashNoPortionBeginClass()
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
    public void ConstructorMarkingStringSlashPortionClassAfterEmpty()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterEmpty()
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
    public void ConstructorMarkingStringSlashPortionClassAfter()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfter()
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
    public void ConstructorMarkingStringSlashPortionClassAfterInvalid()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterInvalid()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointInvalid()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointInvalid()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointInvalidTopSecret()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJoint()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJoint()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointTopSecret()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointTopSecretReverseCountries()
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
    public void ConstructorMarkingStringSlashPortionClassAfterCosmic()
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
    public void ConstructorMarkingStringSlashPortionClassAfterNato()
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
    public void ConstructorMarkingStringSlashPortionClassAfterAtomal()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterCosmic()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterNato()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterAtomal()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointFOUO_LES()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFOUO_LES()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointFOUO()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFOUO()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointLES()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointLES()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointRELIDO()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointRELIDO()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointPROPIN()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointPROPIN()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointFISA()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFISA()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointIMCON()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointIMCON()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointORCON()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointORCON()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDSEN()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDSEN()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointNOFORN()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointNOFORN()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyEmpty()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyEmpty()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyOne()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyOne()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwo()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwo()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwoCommas()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwoCommas()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToEmpty()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToEmpty()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToOne()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToOne()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToTwo()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwo()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToTwoCommas()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwoCommas()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointSUBNOFORN()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointSBUNOFORN()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointSBU()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointSBU()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointEXDIS()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointEXDIS()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointDS()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDS()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMOne()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMOne()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMTwo()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMTwo()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMEmpty()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMEmpty()
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
    public void ConstructorMarkingStringSlashPortionClassAfterJointEverything()
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
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointEverything()
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
    public void SetIsNATOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsNATO());

        mk.setIsNATO(true);
        assertTrue(mk.getIsNATO());
    }

    @Test
    public void GetIsJointSelectedCountriesZeroTest()
    {
        Marking mk = new Marking("");
        mk.getSelectedCountries().clear();

        assertFalse(mk.getIsJOINT());
        assertEquals(0, mk.getSelectedCountries().size());

    }

    @Test
    public void GetIsJointSelectedCountriesOneTest()
    {
        Marking mk = new Marking("//USA TOP SECRET ");

        assertFalse(mk.getIsJOINT());
        assertEquals(1, mk.getSelectedCountries().size());

    }

    @Test
    public void GetIsJointSelectedCoutnreisTwoTest()
    {
        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertTrue(mk.getIsJOINT());
        assertEquals(2, mk.getSelectedCountries().size());

    }

    @Test
    public void SetIsFOUOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsFOUO());

        mk.setIsFOUO(true);
        assertTrue(mk.getIsFOUO());
    }

    @Test
    public void SetIsLESTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsLES());

        mk.setIsLES(true);
        assertTrue(mk.getIsLES());

    }

    @Test
    public void SetIsORCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsORCON());

        mk.setIsORCON(true);
        assertTrue(mk.getIsORCON());
    }

    @Test
    public void SetIsIMCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsIMCON());

        mk.setIsIMCON(true);
        assertTrue(mk.getIsIMCON());
    }

    @Test
    public void GetIsDisplayOnlyTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//DISPLAY ONLY AND USA");

        assertTrue(mk.getIsDisplay_Only());
    }

    @Test
    public void GetIsDisplayOnlyEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsDisplay_Only());
    }

    @Test
    public void SetIsDSENTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsDSEN());

        mk.setIsDSEN(true);
        assertTrue(mk.getIsDSEN());
    }

    @Test
    public void SetIsFISATest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsFISA());

        mk.setIsFISA(true);
        assertTrue(mk.getIsFISA());
    }

    @Test
    public void SetIsPROPINTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsPROPIN());

        mk.setIsPROPIN(true);
        assertTrue(mk.getIsPROPIN());
    }

    @Test
    public void SetIsRELIDOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsRELIDO());

        mk.setIsRELIDO(true);
        assertTrue(mk.getIsRELIDO());

    }

    @Test
    public void GetIsReleaseToTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED USA//REL TO AND USA");

        assertTrue(mk.getIsReleaseTo());
    }

    @Test
    public void GetIsReleaseToEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsReleaseTo());
    }

    @Test
    public void SetIsNOFORNTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsNOFORN());

        mk.setIsNOFORN(true);
        assertTrue(mk.getIsNOFORN());
    }

    @Test
    public void SetClassificationTest()
    {

        Marking mk = new Marking();

        MarkingValueTest.assertMarkingValue("", "UNCLASSIFIED", "", "U", mk.getClassification());

        mk.setClassification(new MarkingValue("Parent1", "Title1", "Abb1", "Portion1"));

        MarkingValueTest.assertMarkingValue("Parent1", "Title1", "Abb1", "Portion1", mk.getClassification());
    }

    @Test
    public void SetIsLIMDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsLIMDIS());

        mk.setIsLIMDIS(true);
        assertTrue(mk.getIsLIMDIS());
    }

    @Test
    public void SetIsEXDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsEXDIS());

        mk.setIsEXDIS(true);
        assertTrue(mk.getIsEXDIS());
    }

    @Test
    public void SetIsSBUTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsSBU());

        mk.setIsSBU(true);

        assertTrue(mk.getIsSBU());
    }

    @Test
    public void SetIsSBUNFTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.getIsSBUNF());

        mk.setIsSBUNF(true);
        assertTrue(mk.getIsSBUNF());
    }

    @Test
    public void GetIsACCMTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//ACCM-BOB/JIM");

        assertTrue(mk.getIsACCM());
    }

    @Test
    public void GetIsACCMEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.getIsDisplay_Only());
    }

    @SuppressWarnings("unchecked")
    private List<MarkingValue> CallGetClassifications() throws NoSuchMethodException, SecurityException,
    IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        Method method = Marking.class.getDeclaredMethod("GetClassifications", (Class<?>[]) null);
        method.setAccessible(true);

        Object results = method.invoke(null, (Object[]) null);

        return (List<MarkingValue>) results;
    }
}
