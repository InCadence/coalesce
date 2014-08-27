package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringHelperTest {

    @BeforeClass
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
    }

    @Test
    public void TrimParenthesesTest()
    {

        String onlyOpen = "(Testing ()Testing";
        assertEquals("Testing ()Testing", StringHelper.TrimParentheses(onlyOpen));

        String onlyEnd = "Testing ()Testing)";
        assertEquals("Testing ()Testing", StringHelper.TrimParentheses(onlyEnd));

        String both = "(Testing ()Testing)";
        assertEquals("Testing ()Testing", StringHelper.TrimParentheses(both));

        String multiple = "((Testing ()Testing))";
        assertEquals("Testing ()Testing", StringHelper.TrimParentheses(multiple));

    }

    @Test
    public void IsNullOrEmptyNullTest()
    {
        String nullString = null;
        assertTrue(StringHelper.IsNullOrEmpty(nullString));
    }

    @Test
    public void IsNullOrEmptyEmptyTest()
    {
        assertTrue(StringHelper.IsNullOrEmpty(""));
    }

    @Test
    public void IsNullOrEmptyWhiteSpaceTest()
    {
        assertFalse(StringHelper.IsNullOrEmpty(" "));
    }

    @Test
    public void IsNullOrEmptyStuffTest()
    {
        assertFalse(StringHelper.IsNullOrEmpty("a"));
    }

    @Test
    public void IsNullOrEmptyAllNullTest()
    {
        String nullString1 = null;
        String nullString2 = null;
        assertTrue(StringHelper.IsNullOrEmpty(nullString1, nullString2));
    }

    @Test
    public void IsNullOrEmtpyAllEmptyTest()
    {
        assertTrue(StringHelper.IsNullOrEmpty("", "", ""));
    }

    @Test
    public void IsNullOrEmptySomeNullTest()
    {
        assertTrue(StringHelper.IsNullOrEmpty(null, "a", "b"));
    }

    @Test
    public void IsNullOrEmptySomeEmptyTest()
    {
        assertTrue(StringHelper.IsNullOrEmpty("", "a", "b"));
    }

    @Test
    public void IsNullOrEmptySomeNullSomeEmptyTest()
    {
        assertTrue(StringHelper.IsNullOrEmpty(null, "", "a"));
    }

    @Test
    public void IsNullOrEmptyNoNullNoEmptyTest() {
        assertFalse(StringHelper.IsNullOrEmpty("a", "b", "c"));
    }
}
