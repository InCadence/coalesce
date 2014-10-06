package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import com.incadencecorp.coalesce.common.helpers.GUIDHelper;

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

public class GUIDHelperTest {

    private static final String UPPERCASE_NO_BRACKETS = "487568C1-B306-4B23-A94E-456E6AC3C113";
    private static final String UPPERCASE_WITH_BRACKETS = "{487568C1-B306-4B23-A94E-456E6AC3C113}";
    private static final String LOWERCASE_NO_BRACKETS = "487568c1-b306-4b23-a94e-456e6ac3c113";
    private static final String LOWERCASE_WITH_BRACKETS = "{487568c1-b306-4b23-a94e-456e6ac3c113}";

    private static final String UPPERCASE_MISSING_OPENING_BRACKET = "487568C1-B306-4B23-A94E-456E6AC3C113}";
    private static final String UPPERCASE_MISSING_CLOSING_BRACKET = "{487568C1-B306-4B23-A94E-456E6AC3C113";
    private static final String UPPERCASE_NO_BRACKETS_SHORT = "487568C1-B306-4B23-A94E-456E6AC3C13";
    private static final String UPPERCASE_NO_BRACKETS_LONG  = "487568C1-B306-4B23-A94E-456E6AC3C1113";
    private static final String UPPERCASE_WITH_BRACKETS_WRONG_LETTER = "{487568C1-Z306-4B23-A94E-456E6AC3C113}";
    private static final String UPPERCASE_NO_BRACKETS_WRONG_LETTER = "487568C1-Z306-4B23-A94E-456E6AC3C13";
    private static final String UPPERCASE_NO_BRACKETS_SPECIAL_CHAR = "487568C1-!306-4B23-A94E-456E6AC3C13";

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
    public void IsValidTest()
    {
        assertTrue(GUIDHelper.isValid(UUID.randomUUID().toString().toLowerCase()));
    }

    @Test
    public void IsValidLowerCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(LOWERCASE_WITH_BRACKETS));
    }

    @Test
    public void IsValidLowerCaseWithoutBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(LOWERCASE_NO_BRACKETS));
    }

    @Test
    public void IsValidUpperCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(UPPERCASE_WITH_BRACKETS));
    }

    @Test
    public void IsValidUpperCaseWithoutBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS));
    }

    @Test
    public void IsValidUpperCaseNullTest()
    {
        assertFalse(GUIDHelper.isValid(null));
    }

    @Test
    public void IsValidUpperCaseEmptyTest()
    {
        assertFalse(GUIDHelper.isValid(""));
    }

    @Test
    public void IsValidUpperCaseWhitespaceTest()
    {
        assertFalse(GUIDHelper.isValid(" "));
    }

    @Test
    public void IsValidUpperCaseStringTest()
    {
        assertFalse(GUIDHelper.isValid("String"));
    }

    @Test
    public void IsValidInvalidShortTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_SHORT));
    }

    @Test
    public void IsValidInvalidLongTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_LONG));
    }

    @Test
    public void IsValidInvalidWrongLeterTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void IsValidInvalidSpecialCharacterTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_SPECIAL_CHAR));
    }

    @Test
    public void IsValidInvalidMissingOpenningBracketTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_MISSING_OPENING_BRACKET));
    }

    @Test
    public void IsValidInvalidMissingClosingBracketTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_MISSING_CLOSING_BRACKET));
    }

    @Test
    public void HasBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UUID.randomUUID().toString().toLowerCase()));
    }

    @Test
    public void HasBracketsLowerCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.hasBrackets(LOWERCASE_WITH_BRACKETS));
    }

    @Test
    public void HasBracketsLowerCaseWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(LOWERCASE_NO_BRACKETS));
    }

    @Test
    public void HasBracketsUpperCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.hasBrackets(UPPERCASE_WITH_BRACKETS));
    }

    @Test
    public void HasBracketsUpperCaseWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS));
    }

    @Test
    public void HasBracketsInvalidShortTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_SHORT));
    }

    @Test
    public void HasBracketsInvalidLongTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_LONG));
    }

    @Test
    public void HasBracketsInvalidWithBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void HasBracketsInvalidWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void HasBracketsInvalidMissingOpeningBracketTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_MISSING_OPENING_BRACKET));
    }

    @Test
    public void HasBracketsInvalidMissingClosingBracketTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_MISSING_CLOSING_BRACKET));
    }

    @Test
    public void AddBracketsTest()
    {

        String uuid = UUID.randomUUID().toString().toLowerCase();

        String value = GUIDHelper.addBrackets(uuid);

        assertEquals("{" + uuid.toUpperCase() + "}", value);
    }

    @Test
    public void AddBracketsLowerCaseWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(LOWERCASE_WITH_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void AddBracketsLowerCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(LOWERCASE_NO_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void AddBracketsUpperCaseWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_WITH_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void AddBracketsUpperCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void AddBracketsInvalidShortTest()
    {

        String validUuid = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(validUuid);
    }

    @Test
    public void AddBracketsInvalidLongTest()
    {

        String validUuid = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(validUuid);
    }

    @Test
    public void AddBracketsInvalidWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void AddBracketsInvalidWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void RemoveBracketsTest()
    {

        String uuid = UUID.randomUUID().toString().toLowerCase();

        String value = GUIDHelper.removeBrackets("{" + uuid + "}");

        assertEquals(uuid.toUpperCase(), value);
    }

    @Test
    public void RemoveBracketsLowerCaseWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(LOWERCASE_WITH_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void RemoveBracketsLowerCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(LOWERCASE_NO_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void RemoveBracketsUpperCaseWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_WITH_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void RemoveBracketsUpperCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void RemoveBracketsInvalidShortTest()
    {

        String validUuid = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(validUuid);
    }

    @Test
    public void RemoveBracketsInvalidLongTest()
    {

        String validUuid = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(validUuid);
    }

    @Test
    public void RemoveBracketsInvalidWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void RemoveBracketsInvalidWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void GetGuidLowerCaseWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(LOWERCASE_WITH_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void GetGuidLowerCaseWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(LOWERCASE_NO_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void GetGuidUpperCaseWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_WITH_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void GetGuidUpperCaseWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void GetGuidInvalidShortTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(guid);
    }

    @Test
    public void GetGuidInvalidLongTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(guid);
    }

    @Test
    public void GetGuidInvalidWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(guid);
    }

    @Test
    public void GetGuidInvalidWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(guid);
    }

    @Test
    public void GetGuidStringTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid);

        assertEquals(UPPERCASE_NO_BRACKETS, guidString);
    }

    @Test
    public void GetGuidStringNullTest()
    {

        String guidString = GUIDHelper.getGuidString(null);

        assertNull(guidString);
    }

    @Test
    public void GetGuidStringWithBracketsFalseTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid, false);

        assertEquals(UPPERCASE_NO_BRACKETS, guidString);
    }

    @Test
    public void GetGuidStringWithBracketsFalseNullTest()
    {

        String guidString = null;

        try
        {

            guidString = GUIDHelper.getGuidString(null, false);
            assertNull(guidString);
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

    }

    @Test
    public void GetGuidStringWithBracketsTrueTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid, true);

        assertEquals(UPPERCASE_WITH_BRACKETS, guidString);
    }

    @Test
    public void GetGuidStringWithBracketsTrueNullTest()
    {

        String guidString = GUIDHelper.getGuidString(null, true);

        assertNull(guidString);
    }

}
