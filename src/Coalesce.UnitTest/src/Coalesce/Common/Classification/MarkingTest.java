package Coalesce.Common.Classification;

import static org.junit.Assert.*;

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

        assertEquals("UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());

    }

    @Test
    public void ConstructorMarkingStringNull()
    {

        Marking mk = new Marking(null);

        assertEquals("UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringInvalidPortion()
    {

        Marking mk = new Marking("()");

        assertEquals("UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringInvalidNotPortion()
    {

        Marking mk = new Marking("SECRE");

        assertEquals("UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    public void ConstructorMarkingStringNoSlashPortion()
    {

        Marking mk = new Marking("(R)");

        assertEquals("(R)", mk.ToPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortion()
    {

        Marking mk = new Marking("RESTRICTED");

        assertEquals("(R)", mk.ToPortionString());
        assertEquals("RESTRICTED", mk.toString());

        assertEquals("RESTRICTED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    public void ConstructorMarkingStringSlashPortionBlank()
    {

        Marking mk = new Marking("(//)");

        assertEquals("(U)", mk.ToPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("RESTRICTED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionBlank()
    {

        Marking mk = new Marking("//");

        assertEquals("(U)", mk.ToPortionString());
        assertEquals("UNCLASSIFIED", mk.toString());

        assertEquals("UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    public void ConstructorMarkingStringSlashPortionBeginClass()
    {

        Marking mk = new Marking("(TS//)");

        assertEquals("(TS)", mk.ToPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionBeginClass()
    {

        Marking mk = new Marking("TOP SECRET//");

        assertEquals("(TS)", mk.ToPortionString());
        assertEquals("TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterEmpty()
    {

        Marking mk = new Marking("(//TS)");

        assertEquals("(//)", mk.ToPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.GetClassification());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterEmpty()
    {

        Marking mk = new Marking("//TOP SECRET");

        assertEquals("(//)", mk.ToPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.GetClassification());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfter()
    {

        Marking mk = new Marking("(//AND TS)");

        assertEquals("(//AND TS)", mk.ToPortionString());
        assertEquals("//AND TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfter()
    {

        Marking mk = new Marking("//AND SECRET");

        assertEquals("(//AND S)", mk.ToPortionString());
        assertEquals("//AND SECRET", mk.toString());

        assertEquals("SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterInvalid()
    {

        Marking mk = new Marking("(//XYZ TS)");

        assertEquals("(//TS)", mk.ToPortionString());
        assertEquals("//TOP SECRET", mk.toString());

        assertEquals("TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterInvalid()
    {

        Marking mk = new Marking("//XYZ SECRET");

        assertEquals("(//S)", mk.ToPortionString());
        assertEquals("//SECRET", mk.toString());

        assertEquals("SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("(//JOINT TS AND)");

        assertEquals("(//)", mk.ToPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.GetClassification());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointInvalid()
    {

        Marking mk = new Marking("//JOINT SECRET AND");

        assertEquals("(//)", mk.ToPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.GetClassification());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointInvalidTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND");

        assertEquals("(//)", mk.ToPortionString());
        assertEquals("//", mk.toString());

        MarkingValueTest.assertMarkingValue("", "", "", "", mk.GetClassification());
        assertFalse(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJoint()
    {

        Marking mk = new Marking("(//JOINT TS AND USA)");

        assertEquals("(//JOINT TS AND USA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJoint()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA");

        assertEquals("(//JOINT S AND USA)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointTopSecret()
    {

        Marking mk = new Marking("//JOINT TOP SECRET AND USA");

        assertEquals("(//JOINT TS AND USA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointTopSecretReverseCountries()
    {

        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertEquals("(//JOINT TS USA AND)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET USA AND", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterCosmic()
    {

        Marking mk = new Marking("(//CTS-B)");

        assertEquals("(//CTS-B)", mk.ToPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterNato()
    {

        Marking mk = new Marking("(//NU)");

        assertEquals("(//NU)", mk.ToPortionString());
        assertEquals("//NATO UNCLASSIFIED", mk.toString());

        assertEquals("NATO UNCLASSIFIED", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterAtomal()
    {

        Marking mk = new Marking("(//NC-A)");

        assertEquals("(//NC-A)", mk.ToPortionString());
        assertEquals("//CONFIDENTIAL ATOMAL", mk.toString());

        assertEquals("CONFIDENTIAL ATOMAL", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterCosmic()
    {

        Marking mk = new Marking("//COSMIC TOP SECRET BOHEMIA");

        assertEquals("(//CTS-B)", mk.ToPortionString());
        assertEquals("//COSMIC TOP SECRET BOHEMIA", mk.toString());

        assertEquals("COSMIC TOP SECRET BOHEMIA", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterNato()
    {

        Marking mk = new Marking("//NATO SECRET");

        assertEquals("(//NS)", mk.ToPortionString());
        assertEquals("//NATO SECRET", mk.toString());

        assertEquals("NATO SECRET", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterAtomal()
    {

        Marking mk = new Marking("//SECRET ATOMAL");

        assertEquals("(//NS-A)", mk.ToPortionString());
        assertEquals("//SECRET ATOMAL", mk.toString());

        assertEquals("SECRET ATOMAL", mk.GetClassification().GetTitle());
        assertTrue(mk.GetIsNATO());
        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO-LES)");

        assertEquals("(//JOINT TS AND USA//FOUO-LES)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO-LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFOUO_LES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO-LES");

        assertEquals("(//JOINT S AND USA//FOUO-LES)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO-LES", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO)");

        assertEquals("(//JOINT TS AND USA//FOUO)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFOUO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO");

        assertEquals("(//JOINT S AND USA//FOUO)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointLES()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//LES)");

        assertEquals("(//JOINT TS AND USA//LES)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointLES()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LES");

        assertEquals("(//JOINT S AND USA//LES)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//LES", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//RELIDO)");

        assertEquals("(//JOINT TS AND USA//RELIDO)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertTrue(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointRELIDO()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//RELIDO");

        assertEquals("(//JOINT S AND USA//RELIDO)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//RELIDO", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertTrue(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//PR)");

        assertEquals("(//JOINT TS AND USA//PR)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertTrue(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointPROPIN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//PROPIN");

        assertEquals("(//JOINT S AND USA//PR)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//PROPIN", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertTrue(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FISA)");

        assertEquals("(//JOINT TS AND USA//FISA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertTrue(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointFISA()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FISA");

        assertEquals("(//JOINT S AND USA//FISA)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//FISA", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertTrue(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//IMC)");

        assertEquals("(//JOINT TS AND USA//IMC)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertTrue(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointIMCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//IMCON");

        assertEquals("(//JOINT S AND USA//IMC)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//IMCON", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertTrue(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//OC)");

        assertEquals("(//JOINT TS AND USA//OC)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertTrue(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointORCON()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ORCON");

        assertEquals("(//JOINT S AND USA//OC)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//ORCON", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertTrue(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DSEN)");

        assertEquals("(//JOINT TS AND USA//DSEN)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertTrue(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDSEN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DSEN");

        assertEquals("(//JOINT S AND USA//DSEN)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//DSEN", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertTrue(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//NF)");

        assertEquals("(//JOINT TS AND USA//NF)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertTrue(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//NOFORN");

        assertEquals("(//JOINT S AND USA//NF)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertTrue(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY)");

        assertEquals("(//JOINT TS AND USA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY");

        assertEquals("(//JOINT S AND USA)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(1, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(1, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetDisplayOnlyCountries().get(1));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetDisplayOnlyCountries().get(1));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)");

        assertEquals("(//JOINT TS AND USA//DISPLAY ONLY BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetDisplayOnlyCountries().get(1));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDisplayOnlyTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL");

        assertEquals("(//JOINT S AND USA//DISPLAY ONLY BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//DISPLAY ONLY BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetDisplayOnlyCountries().get(1));
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO)");

        assertEquals("(//JOINT TS AND USA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO");

        assertEquals("(//JOINT S AND USA)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(1, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU");

        assertEquals("(//JOINT S AND USA//REL TO BMU)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(1, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//REL TO BMU, COL)");

        assertEquals("(//JOINT TS AND USA//REL TO BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointReleaseToTwoCommas()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//REL TO BMU, COL");

        assertEquals("(//JOINT S AND USA//REL TO BMU, COL)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//REL TO BMU, COL", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertTrue(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointSUBNOFORN()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU-NF)");

        assertEquals("(//JOINT TS AND USA//SBU-NF)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertTrue(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());

    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointSBUNOFORN()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU NOFORN");

        assertEquals("(//JOINT S AND USA//SBU-NF)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU NOFORN", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertTrue(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//SBU)");

        assertEquals("(//JOINT TS AND USA//SBU)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertTrue(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointSBU()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//SBU");

        assertEquals("(//JOINT S AND USA//SBU)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//SBU", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertTrue(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//XD)");

        assertEquals("(//JOINT TS AND USA//XD)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertTrue(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointEXDIS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//EXDIS");

        assertEquals("(//JOINT S AND USA//XD)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//EXDIS", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertTrue(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointDS()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//DS)");

        assertEquals("(//JOINT TS AND USA//DS)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertTrue(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointDS()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//LIMITED DISTRIBUTION");

        assertEquals("(//JOINT S AND USA//DS)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//LIMITED DISTRIBUTION", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertTrue(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(1, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMOne()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB");

        assertEquals("(//JOINT S AND USA//ACCM-BOB)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(1, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//ACCM-BOB/JIM)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(2, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertEquals("JIM", mk.GetNicknames().get(1));
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMTwo()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//ACCM-BOB/JIM)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//ACCM-BOB/JIM", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(2, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertEquals("JIM", mk.GetNicknames().get(1));
        assertFalse(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//ACCM-)");

        assertEquals("(//JOINT TS AND USA)", mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA", mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointACCMEmpty()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//ACCM-");

        assertEquals("(//JOINT S AND USA)", mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA", mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertFalse(mk.GetIsFOUO());
        assertFalse(mk.GetIsLES());
        assertFalse(mk.GetIsORCON());
        assertFalse(mk.GetIsIMCON());
        assertFalse(mk.GetIsRELIDO());
        assertFalse(mk.GetIsPROPIN());
        assertFalse(mk.GetIsFISA());
        assertFalse(mk.GetIsNOFORN());
        assertFalse(mk.GetIsDSEN());
        assertEquals(0, mk.GetReleaseToCountries().size());
        assertEquals(0, mk.GetDisplayOnlyCountries().size());
        assertFalse(mk.GetIsLIMDIS());
        assertFalse(mk.GetIsEXDIS());
        assertFalse(mk.GetIsSBU());
        assertFalse(mk.GetIsSBUNF());
        assertEquals(0, mk.GetNicknames().size());
        assertFalse(mk.HasDeseminationControls());
        assertFalse(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("(//JOINT TS AND USA//FOUO-LES//FOUO/LES/RELIDO/PR/FISA/IMC/OC/DSEN//NF/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU-NF/SBU/XD/DS/ACCM-BOB/JIM)");

        assertEquals("(//JOINT TS AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//DS/XD/SBU/SBU-NF/ACCM-BOB/JIM)",
                     mk.ToPortionString());
        assertEquals("//JOINT TOP SECRET AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN/ACCM-BOB/JIM",
                     mk.toString());

        assertEquals("JOINT TOP SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertTrue(mk.GetIsORCON());
        assertTrue(mk.GetIsIMCON());
        assertTrue(mk.GetIsRELIDO());
        assertTrue(mk.GetIsPROPIN());
        assertTrue(mk.GetIsFISA());
        assertTrue(mk.GetIsNOFORN());
        assertTrue(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("IND"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("VIR"), mk.GetDisplayOnlyCountries().get(1));
        assertTrue(mk.GetIsLIMDIS());
        assertTrue(mk.GetIsEXDIS());
        assertTrue(mk.GetIsSBU());
        assertTrue(mk.GetIsSBUNF());
        assertEquals(2, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertEquals("JIM", mk.GetNicknames().get(1));
        assertTrue(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void ConstructorMarkingStringSlashNoPortionClassAfterJointEverything()
    {

        Marking mk = new Marking("//JOINT SECRET AND USA//FOUO-LES/FOUO/LES/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN/DISPLAY ONLY VIR IND/REL TO BMU COL//SBU NOFORN/SBU/EXDIS/LIMITED DISTRIBUTION/ACCM-BOB/JIM");

        assertEquals("(//JOINT S AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PR/FISA/IMC/OC/DSEN/NF//DS/XD/SBU/SBU-NF/ACCM-BOB/JIM)",
                     mk.ToPortionString());
        assertEquals("//JOINT SECRET AND USA//FOUO-LES/REL TO BMU, COL/DISPLAY ONLY IND, VIR/RELIDO/PROPIN/FISA/IMCON/ORCON/DSEN/NOFORN//LIMITED DISTRIBUTION/EXDIS/SBU/SBU NOFORN/ACCM-BOB/JIM",
                     mk.toString());

        assertEquals("JOINT SECRET", mk.GetClassification().GetTitle());
        assertFalse(mk.GetIsNATO());
        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("AND"), mk.GetSelectedCountries().get(0));
        ISO3166CountryTest.assertCountry(ISO3166Country.USA(), mk.GetSelectedCountries().get(1));
        assertTrue(mk.GetIsFOUO());
        assertTrue(mk.GetIsLES());
        assertTrue(mk.GetIsORCON());
        assertTrue(mk.GetIsIMCON());
        assertTrue(mk.GetIsRELIDO());
        assertTrue(mk.GetIsPROPIN());
        assertTrue(mk.GetIsFISA());
        assertTrue(mk.GetIsNOFORN());
        assertTrue(mk.GetIsDSEN());
        assertEquals(2, mk.GetReleaseToCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("BMU"), mk.GetReleaseToCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("COL"), mk.GetReleaseToCountries().get(1));
        assertEquals(2, mk.GetDisplayOnlyCountries().size());
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("IND"), mk.GetDisplayOnlyCountries().get(0));
        ISO3166CountryTest.assertCountry(FieldValues.GetCountryByAlpha3("VIR"), mk.GetDisplayOnlyCountries().get(1));
        assertTrue(mk.GetIsLIMDIS());
        assertTrue(mk.GetIsEXDIS());
        assertTrue(mk.GetIsSBU());
        assertTrue(mk.GetIsSBUNF());
        assertEquals(2, mk.GetNicknames().size());
        assertEquals("BOB", mk.GetNicknames().get(0));
        assertEquals("JIM", mk.GetNicknames().get(1));
        assertTrue(mk.HasDeseminationControls());
        assertTrue(mk.HasOtherDeseminationControls());
    }

    @Test
    public void SetIsNATOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsNATO());

        mk.SetIsNATO(true);
        assertTrue(mk.GetIsNATO());
    }

    @Test
    public void GetIsJointSelectedCountriesZeroTest()
    {
        Marking mk = new Marking("");
        mk.GetSelectedCountries().clear();

        assertFalse(mk.GetIsJOINT());
        assertEquals(0, mk.GetSelectedCountries().size());

    }

    @Test
    public void GetIsJointSelectedCountriesOneTest()
    {
        Marking mk = new Marking("//USA TOP SECRET ");

        assertFalse(mk.GetIsJOINT());
        assertEquals(1, mk.GetSelectedCountries().size());

    }

    @Test
    public void GetIsJointSelectedCoutnreisTwoTest()
    {
        Marking mk = new Marking("//JOINT TOP SECRET USA AND");

        assertTrue(mk.GetIsJOINT());
        assertEquals(2, mk.GetSelectedCountries().size());

    }

    @Test
    public void SetIsFOUOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsFOUO());

        mk.SetIsFOUO(true);
        assertTrue(mk.GetIsFOUO());
    }

    @Test
    public void SetIsLESTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsLES());

        mk.SetIsLES(true);
        assertTrue(mk.GetIsLES());

    }

    @Test
    public void SetIsORCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsORCON());

        mk.SetIsORCON(true);
        assertTrue(mk.GetIsORCON());
    }

    @Test
    public void SetIsIMCONTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsIMCON());

        mk.SetIsIMCON(true);
        assertTrue(mk.GetIsIMCON());
    }

    @Test
    public void GetIsDisplayOnlyTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//DISPLAY ONLY AND USA");

        assertTrue(mk.GetIsDISPLAY_ONLY());
    }

    @Test
    public void GetIsDisplayOnlyEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.GetIsDISPLAY_ONLY());
    }

    @Test
    public void SetIsDSENTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsDSEN());

        mk.SetIsDSEN(true);
        assertTrue(mk.GetIsDSEN());
    }

    @Test
    public void SetIsFISATest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsFISA());

        mk.SetIsFISA(true);
        assertTrue(mk.GetIsFISA());
    }

    @Test
    public void SetIsPROPINTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsPROPIN());

        mk.SetIsPROPIN(true);
        assertTrue(mk.GetIsPROPIN());
    }

    @Test
    public void SetIsRELIDOTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsRELIDO());

        mk.SetIsRELIDO(true);
        assertTrue(mk.GetIsRELIDO());

    }

    @Test
    public void GetIsReleaseToTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED USA//REL TO AND USA");

        assertTrue(mk.GetIsReleaseTo());
    }

    @Test
    public void GetIsReleaseToEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.GetIsReleaseTo());
    }

    @Test
    public void SetIsNOFORNTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsNOFORN());

        mk.SetIsNOFORN(true);
        assertTrue(mk.GetIsNOFORN());
    }

    @Test
    public void SetClassificationTest()
    {

        Marking mk = new Marking();

        MarkingValueTest.assertMarkingValue("", "UNCLASSIFIED", "", "U", mk.GetClassification());

        mk.SetClassification(new MarkingValue("Parent1", "Title1", "Abb1", "Portion1"));

        MarkingValueTest.assertMarkingValue("Parent1", "Title1", "Abb1", "Portion1", mk.GetClassification());
    }

    @Test
    public void SetIsLIMDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsLIMDIS());

        mk.SetIsLIMDIS(true);
        assertTrue(mk.GetIsLIMDIS());
    }

    @Test
    public void SetIsEXDISTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsEXDIS());

        mk.SetIsEXDIS(true);
        assertTrue(mk.GetIsEXDIS());
    }

    @Test
    public void SetIsSBUTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsSBU());

        mk.SetIsSBU(true);

        assertTrue(mk.GetIsSBU());
    }

    @Test
    public void SetIsSBUNFTest()
    {

        Marking mk = new Marking();

        assertFalse(mk.GetIsSBUNF());

        mk.SetIsSBUNF(true);
        assertTrue(mk.GetIsSBUNF());
    }

    @Test
    public void GetIsACCMTest()
    {

        Marking mk = new Marking("UNCLASSIFIED//ACCM-BOB/JIM");

        assertTrue(mk.GetIsACCM());
    }

    @Test
    public void GetIsACCMEmptyTest()
    {

        Marking mk = new Marking("//UNCLASSIFIED");

        assertFalse(mk.GetIsDISPLAY_ONLY());
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
