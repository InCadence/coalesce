package com.incadencecorp.coalesce.common.classification;

import static org.junit.Assert.assertEquals;

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

public class MarkingValueTest {

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
    public void markingValueEmptyConstructorTest()
    {

        MarkingValue mv = new MarkingValue();

        assertMarkingValue("", "", "", "", mv);
    }

    @Test
    public void markingValueArgConstructorTest()
    {

        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", "portion1");

        assertMarkingValue("parent1", "title1", "abbreviation1", "portion1", mv);
    }

    @Test(expected = NullArgumentException.class)
    public void markingValueNullParentArgConstructorTest()
    {

        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue(null, "title1", "abbreviation1", "portion1");

    }

    @Test(expected = NullArgumentException.class)
    public void markingValueNullTitleArgConstructorTest()
    {

        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", null, "abbreviation1", "portion1");

    }

    @Test(expected = NullArgumentException.class)
    public void markingValueNullAbbreviationArgConstructorTest()
    {

        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", "title1", null, "portion1");

    }

    @Test(expected = NullArgumentException.class)
    public void markingValueNullPortionArgConstructorTest()
    {

        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", null);

    }

    @Test
    public void markingValueSetParentTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setParent("parent1");

        assertMarkingValue("parent1", "", "", "", mv);
    }

    @Test(expected = NullArgumentException.class)
    public void markingValueSetParentNullTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setParent(null);

    }

    @Test
    public void markingValueSetTitleTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setTitle("title1");

        assertMarkingValue("", "title1", "", "", mv);
    }

    @Test(expected = NullArgumentException.class)
    public void markingValueSetTitleNullTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setTitle(null);

    }

    @Test
    public void markingValueSetAbbreviationTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setAbbreviation("Abbreviation1");

        assertMarkingValue("", "", "Abbreviation1", "", mv);
    }

    @Test(expected = NullArgumentException.class)
    public void markingValueSetAbbreviationtNullTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setAbbreviation(null);

    }

    @Test
    public void markingValueSetPortionTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setPortion("Portion1");

        assertMarkingValue("", "", "", "Portion1", mv);
    }

    @Test(expected = NullArgumentException.class)
    public void markingValueSetPortionNullTest()
    {

        MarkingValue mv = new MarkingValue();

        mv.setPortion(null);

    }

    @Test()
    public void markingValueSerializeDeserializeTest() throws IOException, ClassNotFoundException
    {

        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", "portion1");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(mv);
        out.close();
        byteOut.close();

        MarkingValue desMv = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desMv = (MarkingValue) in.readObject();
        in.close();
        byteIn.close();

        assertMarkingValue("parent1", "title1", "abbreviation1", "portion1", desMv);
    }

    @Test()
    public void markingValueSerializeDeserializeBlankMarkingValueTest() throws IOException, ClassNotFoundException
    {

        MarkingValue mv = new MarkingValue();

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(mv);
        out.close();
        byteOut.close();

        MarkingValue desMv = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desMv = (MarkingValue) in.readObject();
        in.close();
        byteIn.close();

        assertMarkingValue("", "", "", "", desMv);

    }

    public static void assertMarkingValue(String expectedParent,
                                          String expectedTitle,
                                          String expectedAbbreviation,
                                          String expectedPortion,
                                          MarkingValue testValue)
    {

        assertEquals(expectedParent, testValue.getParent());
        assertEquals(expectedTitle, testValue.getTitle());
        assertEquals(expectedAbbreviation, testValue.getAbbreviation());
        assertEquals(expectedPortion, testValue.getPortion());
    }

    public static void assertMarkingValue(MarkingValue expectedValue, MarkingValue testValue)
    {

        assertEquals(expectedValue.getParent(), testValue.getParent());
        assertEquals(expectedValue.getTitle(), testValue.getTitle());
        assertEquals(expectedValue.getAbbreviation(), testValue.getAbbreviation());
        assertEquals(expectedValue.getPortion(), testValue.getPortion());
    }

}
