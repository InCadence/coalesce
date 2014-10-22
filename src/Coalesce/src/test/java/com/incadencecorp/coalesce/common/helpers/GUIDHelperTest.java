package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

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
    public void isValidTest()
    {
        assertTrue(GUIDHelper.isValid(UUID.randomUUID().toString().toLowerCase()));
    }

    @Test
    public void isValidLowerCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(LOWERCASE_WITH_BRACKETS));
    }

    @Test
    public void isValidLowerCaseWithoutBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(LOWERCASE_NO_BRACKETS));
    }

    @Test
    public void isValidUpperCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(UPPERCASE_WITH_BRACKETS));
    }

    @Test
    public void isValidUpperCaseWithoutBracketsTest()
    {
        assertTrue(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS));
    }

    @Test
    public void isValidUpperCaseNullTest()
    {
        assertFalse(GUIDHelper.isValid(null));
    }

    @Test
    public void isValidUpperCaseEmptyTest()
    {
        assertFalse(GUIDHelper.isValid(""));
    }

    @Test
    public void isValidUpperCaseWhitespaceTest()
    {
        assertFalse(GUIDHelper.isValid(" "));
    }

    @Test
    public void isValidUpperCaseStringTest()
    {
        assertFalse(GUIDHelper.isValid("String"));
    }

    @Test
    public void isValidInvalidShortTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_SHORT));
    }

    @Test
    public void isValidInvalidLongTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_LONG));
    }

    @Test
    public void isValidInvalidWrongLeterTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void isValidInvalidSpecialCharacterTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_NO_BRACKETS_SPECIAL_CHAR));
    }

    @Test
    public void isValidInvalidMissingOpenningBracketTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_MISSING_OPENING_BRACKET));
    }

    @Test
    public void isValidInvalidMissingClosingBracketTest()
    {
        assertFalse(GUIDHelper.isValid(UPPERCASE_MISSING_CLOSING_BRACKET));
    }

    @Test
    public void hasBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UUID.randomUUID().toString().toLowerCase()));
    }

    @Test
    public void hasBracketsLowerCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.hasBrackets(LOWERCASE_WITH_BRACKETS));
    }

    @Test
    public void hasBracketsLowerCaseWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(LOWERCASE_NO_BRACKETS));
    }

    @Test
    public void hasBracketsUpperCaseWithBracketsTest()
    {
        assertTrue(GUIDHelper.hasBrackets(UPPERCASE_WITH_BRACKETS));
    }

    @Test
    public void hasBracketsUpperCaseWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS));
    }

    @Test
    public void hasBracketsInvalidShortTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_SHORT));
    }

    @Test
    public void hasBracketsInvalidLongTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_LONG));
    }

    @Test
    public void hasBracketsInvalidWithBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void hasBracketsInvalidWithoutBracketsTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER));
    }

    @Test
    public void hasBracketsInvalidMissingOpeningBracketTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_MISSING_OPENING_BRACKET));
    }

    @Test
    public void hasBracketsInvalidMissingClosingBracketTest()
    {
        assertFalse(GUIDHelper.hasBrackets(UPPERCASE_MISSING_CLOSING_BRACKET));
    }

    @Test
    public void addBracketsTest()
    {

        String uuid = UUID.randomUUID().toString().toLowerCase();

        String value = GUIDHelper.addBrackets(uuid);

        assertEquals("{" + uuid.toUpperCase() + "}", value);
    }

    @Test
    public void addBracketsLowerCaseWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(LOWERCASE_WITH_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void addBracketsLowerCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(LOWERCASE_NO_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void addBracketsUpperCaseWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_WITH_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void addBracketsUpperCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS);

        assertEquals(UPPERCASE_WITH_BRACKETS, value);
    }

    @Test
    public void addBracketsInvalidShortTest()
    {

        String validUuid = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(validUuid);
    }

    @Test
    public void addBracketsInvalidLongTest()
    {

        String validUuid = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(validUuid);
    }

    @Test
    public void addBracketsInvalidWithBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void addBracketsInvalidWithoutBracketsTest()
    {

        String value = GUIDHelper.addBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void removeBracketsTest()
    {

        String uuid = UUID.randomUUID().toString().toLowerCase();

        String value = GUIDHelper.removeBrackets("{" + uuid + "}");

        assertEquals(uuid.toUpperCase(), value);
    }

    @Test
    public void removeBracketsLowerCaseWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(LOWERCASE_WITH_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void removeBracketsLowerCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(LOWERCASE_NO_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void removeBracketsUpperCaseWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_WITH_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void removeBracketsUpperCaseWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS);

        assertTrue(UPPERCASE_NO_BRACKETS.equals(value));
    }

    @Test
    public void removeBracketsInvalidShortTest()
    {

        String validUuid = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(validUuid);
    }

    @Test
    public void removeBracketsInvalidLongTest()
    {

        String validUuid = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(validUuid);
    }

    @Test
    public void removeBracketsInvalidWithBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void removeBracketsInvalidWithoutBracketsTest()
    {

        String value = GUIDHelper.removeBrackets(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(value);
    }

    @Test
    public void getGuidLowerCaseWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(LOWERCASE_WITH_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void getGuidLowerCaseWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(LOWERCASE_NO_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void getGuidUpperCaseWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_WITH_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void getGuidUpperCaseWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        assertEquals(guid.toString(), LOWERCASE_NO_BRACKETS);
    }

    @Test
    public void getGuidInvalidShortTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_SHORT);

        assertNull(guid);
    }

    @Test
    public void getGuidInvalidLongTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_LONG);

        assertNull(guid);
    }

    @Test
    public void getGuidInvalidWithBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_WITH_BRACKETS_WRONG_LETTER);

        assertNull(guid);
    }

    @Test
    public void getGuidInvalidWithoutBracketsTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS_WRONG_LETTER);

        assertNull(guid);
    }

    @Test
    public void getGuidStringTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid);

        assertEquals(UPPERCASE_NO_BRACKETS, guidString);
    }

    @Test
    public void getGuidStringNullTest()
    {

        String guidString = GUIDHelper.getGuidString(null);

        assertNull(guidString);
    }

    @Test
    public void getGuidStringWithBracketsFalseTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid, false);

        assertEquals(UPPERCASE_NO_BRACKETS, guidString);
    }

    @Test
    public void getGuidStringWithBracketsFalseNullTest()
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
    public void getGuidStringWithBracketsTrueTest()
    {

        UUID guid = GUIDHelper.getGuid(UPPERCASE_NO_BRACKETS);

        String guidString = GUIDHelper.getGuidString(guid, true);

        assertEquals(UPPERCASE_WITH_BRACKETS, guidString);
    }

    @Test
    public void getGuidStringWithBracketsTrueNullTest()
    {

        String guidString = GUIDHelper.getGuidString(null, true);

        assertNull(guidString);
    }

}
