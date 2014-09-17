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
    public void GetLabelStringTypeTest()
    {
        String a = "string";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.StringType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelDateTimeTypeTest()
    {
        String a = "datetime";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.DateTimeType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelUriTypeTest()
    {
        String a = "uri";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.UriType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelBinaryTypeTest()
    {
        String a = "binary";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BinaryType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelBooleanTypeTest()
    {
        String a = "boolean";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BooleanType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIntegerTypeTest()
    {
        String a = "integer";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.IntegerType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelGuidTypeTest()
    {
        String a = "guid";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GuidType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelGeocoordinateTypeTest()
    {
        String a = "geocoordinate";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GeocoordinateType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelFileTypeTest()
    {
        String a = "file";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.FileType;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelGeocoordinateListTypeTest()
    {
        String a = "geocoordinatelist";
        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GeocoordinateListType;

        assertEquals(a, value.getLabel());
    }
    
    @Test
    public void GetTypeForCoalesceStringTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("string"));
        
    }

    @Test
    public void GetTypeForCoalesceDateTimeTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("datetime"));
        
    }
    
    @Test
    public void GetTypeForCoalesceUriTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.UriType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("uri"));
        
    }
    
    @Test
    public void GetTypeForCoalesceBinaryTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("binary"));
        
    }
    
    @Test
    public void GetTypeForCoalesceBooleanTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("boolean"));
        
    }
    
    @Test
    public void GetTypeForCoalesceIntegerTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("integer"));
        
    }
    
    @Test
    public void GetTypeForCoalesceGuidTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("guid"));
        
    }
    
    @Test
    public void GetTypeForCoalesceGeocoordinateTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("geocoordinate"));
        
    }
    
    @Test
    public void GetTypeForCoalesceFileTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.FileType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("file"));
        
    }
    
    @Test
    public void GetTypeForCoalesceGeocoordinateListTypeTest()
    {
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateListType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("geocoordinatelist"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeStringType1Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADVARWCHAR"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeStringType2Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADLONGVARWCHAR"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeDateTimeTypeTest()
    {
               
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADDBTIMESTAMP"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeBooleanTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADBOOLEAN"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeGuidTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADGUID"));
     
    }
    
    @Test
    public void GetTypeForSQLTypeIntegerType1Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADSMALLINT"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeIntegerType2Test()
    {
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADINTEGER"));
        
    }
    
    @Test
    public void GetTypeForSQLTypeBinaryTypeTest()
    {
                
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADLONGVARBINARY"));
     
    }
    
    //@test
    //getStatus 
    
}
