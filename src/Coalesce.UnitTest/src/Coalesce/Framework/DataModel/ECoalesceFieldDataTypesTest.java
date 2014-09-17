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
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForCoalesceType("string"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("string"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceDateTimeTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.getTypeForCoalesceType("datetime"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("datetime"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceUriTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.UriType, ECoalesceFieldDataTypes.getTypeForCoalesceType("uri"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.UriType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("uri"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceBinaryTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.getTypeForCoalesceType("binary"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("binary"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceBooleanTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.getTypeForCoalesceType("boolean"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("boolean"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceIntegerTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForCoalesceType("integer"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("integer"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceGuidTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.getTypeForCoalesceType("guid"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("guid"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceGeocoordinateTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateType, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinate"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateType,
                     ECoalesceFieldDataTypes.GetTypeForCoalesceType("geocoordinate"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceFileTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.FileType, ECoalesceFieldDataTypes.getTypeForCoalesceType("file"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.FileType, ECoalesceFieldDataTypes.GetTypeForCoalesceType("file"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForCoalesceGeocoordinateListTypeTest()
    {
<<<<<<< HEAD
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateListType, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinatelist"));
        
=======
        assertEquals(ECoalesceFieldDataTypes.GeocoordinateListType,
                     ECoalesceFieldDataTypes.GetTypeForCoalesceType("geocoordinatelist"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeStringType1Test()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForSQLType("ADVARWCHAR"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADVARWCHAR"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeStringType2Test()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARWCHAR"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.StringType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADLONGVARWCHAR"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeDateTimeTypeTest()
    {
<<<<<<< HEAD
               
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.getTypeForSQLType("ADDBTIMESTAMP"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.DateTimeType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADDBTIMESTAMP"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeBooleanTypeTest()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.getTypeForSQLType("ADBOOLEAN"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.BooleanType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADBOOLEAN"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeGuidTypeTest()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.getTypeForSQLType("ADGUID"));
     
=======

        assertEquals(ECoalesceFieldDataTypes.GuidType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADGUID"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeIntegerType1Test()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForSQLType("ADSMALLINT"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADSMALLINT"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeIntegerType2Test()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.getTypeForSQLType("ADINTEGER"));
        
=======

        assertEquals(ECoalesceFieldDataTypes.IntegerType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADINTEGER"));

>>>>>>> Test now complete
    }

    @Test
    public void getTypeForSQLTypeBinaryTypeTest()
    {
<<<<<<< HEAD
                
        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARBINARY"));
     
=======

        assertEquals(ECoalesceFieldDataTypes.BinaryType, ECoalesceFieldDataTypes.GetTypeForSQLType("ADLONGVARBINARY"));

>>>>>>> Test now complete
    }

}
