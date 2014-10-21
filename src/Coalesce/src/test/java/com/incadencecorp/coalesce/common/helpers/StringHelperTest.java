package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

public class StringHelperTest {

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
    public void trimParenthesesTest()
    {

        String onlyOpen = "(Testing ()Testing";
        assertEquals("Testing ()Testing", StringHelper.trimParentheses(onlyOpen));

        String onlyEnd = "Testing ()Testing)";
        assertEquals("Testing ()Testing", StringHelper.trimParentheses(onlyEnd));

        String both = "(Testing ()Testing)";
        assertEquals("Testing ()Testing", StringHelper.trimParentheses(both));

        String multiple = "((Testing ()Testing))";
        assertEquals("Testing ()Testing", StringHelper.trimParentheses(multiple));

    }

    @Test
    public void isNullOrEmptyNullTest()
    {
        String nullString = null;
        assertTrue(StringHelper.isNullOrEmpty(nullString));
    }

    @Test
    public void isNullOrEmptyEmptyTest()
    {
        assertTrue(StringHelper.isNullOrEmpty(""));
    }

    @Test
    public void isNullOrEmptyWhiteSpaceTest()
    {
        assertTrue(StringHelper.isNullOrEmpty(" "));
    }

    @Test
    public void isNullOrEmptyStuffTest()
    {
        assertFalse(StringHelper.isNullOrEmpty("a"));
    }

    @Test
    public void isNullOrEmptyAllNullTest()
    {
        String nullString1 = null;
        String nullString2 = null;
        assertTrue(StringHelper.isNullOrEmpty(nullString1, nullString2));
    }

    @Test
    public void isNullOrEmtpyAllEmptyTest()
    {
        assertTrue(StringHelper.isNullOrEmpty("", "", ""));
    }

    @Test
    public void isNullOrEmptySomeNullTest()
    {
        assertTrue(StringHelper.isNullOrEmpty(null, "a", "b"));
    }

    @Test
    public void isNullOrEmptySomeEmptyTest()
    {
        assertTrue(StringHelper.isNullOrEmpty("", "a", "b"));
    }

    @Test
    public void isNullOrEmptySomeNullSomeEmptyTest()
    {
        assertTrue(StringHelper.isNullOrEmpty(null, "", "a"));
    }

    @Test
    public void isNullOrEmptyNoNullNoEmptyTest()
    {
        assertFalse(StringHelper.isNullOrEmpty("a", "b", "c"));
    }
}
