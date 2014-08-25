package Coalesce.Common.Classification;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

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
        
        assertEquals("", mv.GetParent());
        assertEquals("", mv.GetTitle());
        assertEquals("", mv.GetAbbreviation());
        assertEquals("", mv.GetPortion());
    }
    
    @Test
    public void MarkingValueArgConstructorTest() {
        
        MarkingValue mv = new MarkingValue("parent1", "title1", "abbreviation1", "portion1");
        
        assertEquals("parent1", mv.GetParent());
        assertEquals("title1", mv.GetTitle());
        assertEquals("abbreviation1", mv.GetAbbreviation());
        assertEquals("portion1", mv.GetPortion());
        
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
        
        assertEquals("parent1", mv.GetParent());
        assertEquals("", mv.GetTitle());
        assertEquals("", mv.GetAbbreviation());
        assertEquals("", mv.GetPortion());
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
        
        assertEquals("", mv.GetParent());
        assertEquals("title1", mv.GetTitle());
        assertEquals("", mv.GetAbbreviation());
        assertEquals("", mv.GetPortion());
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
        
        assertEquals("", mv.GetParent());
        assertEquals("", mv.GetTitle());
        assertEquals("Abbreviation1", mv.GetAbbreviation());
        assertEquals("", mv.GetPortion());
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
        
        assertEquals("", mv.GetParent());
        assertEquals("", mv.GetTitle());
        assertEquals("", mv.GetAbbreviation());
        assertEquals("Portion1", mv.GetPortion());
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
        
        assertEquals("parent1", desMv.GetParent());
        assertEquals("title1", desMv.GetTitle());
        assertEquals("abbreviation1", desMv.GetAbbreviation());
        assertEquals("portion1", desMv.GetPortion());
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
        
        assertEquals("", desMv.GetParent());
        assertEquals("", desMv.GetTitle());
        assertEquals("", desMv.GetAbbreviation());
        assertEquals("", desMv.GetPortion());
        
    }
}
