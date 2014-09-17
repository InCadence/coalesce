/**
 * 
 */
package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
 * 
 * @AfterClass public static void tearDownAfterClass() throws Exception { }
 * 
 * @Before public void setUp() throws Exception { }
 * 
 * @After public void tearDown() throws Exception { }
 */

public class ECoalesceFieldDataTypesTest {

    @Test
    public void getLabelStringTypeTest()
    {
        String a = "string";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.StringType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelDateTimeTypeTest()
    {
        String a = "datetime";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.DateTimeType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelUriTypeTest()
    {
        String a = "uri";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.UriType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelBinaryTypeTest()
    {
        String a = "binary";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BinaryType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelBooleanTypeTest()
    {
        String a = "boolean";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BooleanType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIntegerTypeTest()
    {
        String a = "integer";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.IntegerType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelGuidTypeTest()
    {
        String a = "guid";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GuidType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelGeocoordinateTypeTest()
    {
        String a = "geocoordinate";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GeocoordinateType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelFileTypeTest()
    {
        String a = "file";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.FileType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelGeocoordinateListTypeTest()
    {
        String a = "geocoordinatelist";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GeocoordinateListType;

        assertEquals(a, value.getLabel());
    }
    
    @Test
    public void getTypeForCoalesceStringTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForCoalesceType("string"));
        
    }

    @Test
    public void getTypeForCoalesceDateTimeTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.getTypeForCoalesceType("datetime"));
        
    }
    
    @Test
    public void getTypeForCoalesceUriTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.UriType, ECoalesceFieldDataTypes.getTypeForCoalesceType("uri"));
        
    }
    
    @Test
    public void getTypeForCoalesceBinaryTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.getTypeForCoalesceType("binary"));
        
    }
    
    @Test
    public void getTypeForCoalesceBooleanTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.getTypeForCoalesceType("boolean"));
        
    }
    
    @Test
    public void getTypeForCoalesceIntegerTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForCoalesceType("integer"));
        
    }
    
    @Test
    public void getTypeForCoalesceGuidTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.getTypeForCoalesceType("guid"));
        
    }
    
    @Test
    public void getTypeForCoalesceGeocoordinateTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateType, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinate"));
        
    }
    
    @Test
    public void getTypeForCoalesceFileTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.FileType, ECoalesceFieldDataTypes.getTypeForCoalesceType("file"));
        
    }
    
    @Test
    public void getTypeForCoalesceGeocoordinateListTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateListType, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinatelist"));
        
    }
    
    @Test
    public void getTypeForSQLTypeStringType1Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForSQLType("ADVARWCHAR"));
        
    }
    
    @Test
    public void getTypeForSQLTypeStringType2Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARWCHAR"));
        
    }
    
    @Test
    public void getTypeForSQLTypeDateTimeTypeTest()
    {
               
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.getTypeForSQLType("ADDBTIMESTAMP"));
        
    }
    
    @Test
    public void getTypeForSQLTypeBooleanTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.getTypeForSQLType("ADBOOLEAN"));
        
    }
    
    @Test
    public void getTypeForSQLTypeGuidTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.getTypeForSQLType("ADGUID"));
     
    }
    
    @Test
    public void getTypeForSQLTypeIntegerType1Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForSQLType("ADSMALLINT"));
        
    }
    
    @Test
    public void getTypeForSQLTypeIntegerType2Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForSQLType("ADINTEGER"));
        
    }
    
    @Test
    public void getTypeForSQLTypeBinaryTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARBINARY"));
     
    }
    
    
    //@test
    //getStatus 
    
}
