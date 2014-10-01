package Coalesce.Common.UnitTest;

import static org.junit.Assert.assertEquals;


public class CoalesceAssert {

    private CoalesceAssert()
    {
        
    }
    
    public static void assertXmlEquals(String expected, String actual, String encoding)
    {
        String stripped = actual.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\" standalone=\"yes\"?>", "");
        String converted = stripped.replace(" ", "").replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        String expectedStripped = expected.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\" standalone=\"yes\"?>","");
        expectedStripped = expectedStripped.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>", "");
        
        String expectedConverted = expectedStripped.replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        assertEquals(expectedConverted, converted);
 
    }
}
