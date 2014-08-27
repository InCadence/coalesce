package Coalesce.Common.Classification;

import static org.junit.Assert.*;

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

/*    @BeforeClass
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
    }*/
 
    @Test
    public void MarkingValueEmptyConstructorTest() {
        
        MarkingValue mv = new MarkingValue();
        
        assertMarkingValue("", "", "", "", mv);
    }
    
    @Test
    public void MarkingValueArgConstructorTest() {
        
        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", "portion1");
        
        assertMarkingValue("parent1", "title1", "abbreviation1", "portion1", mv);
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueNullParentArgConstructorTest() {
        
        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue(null, "title1", "abbreviation1", "portion1");
       
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueNullTitleArgConstructorTest() {
        
        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", null, "abbreviation1", "portion1");
       
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueNullAbbreviationArgConstructorTest() {
        
        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", "title1", null, "portion1");
       
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueNullPortionArgConstructorTest() {
        
        @SuppressWarnings("unused")
        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", null);
       
    }
    
    @Test
    public void MarkingValueSetParentTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetParent("parent1");
        
        assertMarkingValue("parent1", "", "", "", mv);
    }
 
    @Test(expected=NullArgumentException.class)
    public void MarkingValueSetParentNullTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetParent(null);
        
    }

    @Test
    public void MarkingValueSetTitleTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetTitle("title1");
        
        assertMarkingValue("", "title1", "", "", mv);
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueSetTitleNullTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetTitle(null);
        
    }

    @Test
    public void MarkingValueSetAbbreviationTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetAbbreviation("Abbreviation1");
        
        assertMarkingValue("", "", "Abbreviation1", "", mv);
    }
    
    @Test(expected=NullArgumentException.class)
    public void MarkingValueSetAbbreviationtNullTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetAbbreviation(null);
        
    }

    @Test
    public void MarkingValueSetPortionTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetPortion("Portion1");
        
        assertMarkingValue("", "", "", "Portion1", mv);
    }

    @Test(expected=NullArgumentException.class)
    public void MarkingValueSetPortionNullTest() {
        
        MarkingValue mv = new MarkingValue();
    
        mv.SetPortion(null);
        
    }

    @Test()
    public void MarkingValueSerializeDeserializeTest() throws IOException, ClassNotFoundException {
        
        
        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", "portion1");
        
               
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(mv);
        out.close();
        byteOut.close();
        
        MarkingValue desMv = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desMv = (MarkingValue)in.readObject();
        in.close();
        byteIn.close();
        
        assertMarkingValue("parent1", "title1", "abbreviation1", "portion1", desMv);
    }
    
    @Test()
    public void MarkingValueSerializeDeserializeBlankMarkingValueTest() throws IOException, ClassNotFoundException {
        
        MarkingValue mv = new MarkingValue();
        
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(mv);
        out.close();
        byteOut.close();
        
        MarkingValue desMv = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        desMv = (MarkingValue)in.readObject();
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

        assertEquals(expectedParent, testValue.GetParent());
        assertEquals(expectedTitle, testValue.GetTitle());
        assertEquals(expectedAbbreviation, testValue.GetAbbreviation());
        assertEquals(expectedPortion, testValue.GetPortion());
    }
    
    public static void assertMarkingValue(MarkingValue exptectedValue,
                                          MarkingValue testValue)
    {

        assertEquals(exptectedValue.GetParent(), testValue.GetParent());
        assertEquals(exptectedValue.GetTitle(), testValue.GetTitle());
        assertEquals(exptectedValue.GetAbbreviation(), testValue.GetAbbreviation());
        assertEquals(exptectedValue.GetPortion(), testValue.GetPortion());
    }
    
}
