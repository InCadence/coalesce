/**
 * 
 */
package com.incadencecorp.coalesce.framework.datamodel;

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

//    @Test
//    public void getLabelStringTypeTest()
//    {
//        String a = "string";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.STRING_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelDateTimeTypeTest()
//    {
//        String a = "datetime";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.DATE_TIME_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelUriTypeTest()
//    {
//        String a = "uri";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.URI_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelBinaryTypeTest()
//    {
//        String a = "binary";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BINARY_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelBooleanTypeTest()
//    {
//        String a = "boolean";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.BOOLEAN_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelIntegerTypeTest()
//    {
//        String a = "integer";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.INTEGER_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelGuidTypeTest()
//    {
//        String a = "guid";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GUID_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelGeocoordinateTypeTest()
//    {
//        String a = "geocoordinate";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelFileTypeTest()
//    {
//        String a = "file";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.FILE_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//
//    @Test
//    public void getLabelGeocoordinateListTypeTest()
//    {
//        String a = "geocoordinatelist";
//        ECoalesceFieldDataTypes value = ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE;
//
//        assertEquals(a, value.getLabel());
//    }
//    
//    @Test
//    public void getTypeForCoalesceStringTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("string"));
//        
//    }
//
//    @Test
//    public void getTypeForCoalesceDateTimeTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("datetime"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceUriTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.URI_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("uri"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceBinaryTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.BINARY_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("binary"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceBooleanTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("boolean"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceIntegerTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("integer"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceGuidTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.GUID_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("guid"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceGeocoordinateTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinate"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceFileTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.FILE_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("file"));
//        
//    }
//    
//    @Test
//    public void getTypeForCoalesceGeocoordinateListTypeTest()
//    {
//        assertEquals(ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE, ECoalesceFieldDataTypes.getTypeForCoalesceType("geocoordinatelist"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeStringType1Test()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADVARWCHAR"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeStringType2Test()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARWCHAR"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeDateTimeTypeTest()
//    {
//               
//        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADDBTIMESTAMP"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeBooleanTypeTest()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADBOOLEAN"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeGuidTypeTest()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.GUID_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADGUID"));
//     
//    }
//    
//    @Test
//    public void getTypeForSQLTypeIntegerType1Test()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADSMALLINT"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeIntegerType2Test()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADINTEGER"));
//        
//    }
//    
//    @Test
//    public void getTypeForSQLTypeBinaryTypeTest()
//    {
//                
//        assertEquals(ECoalesceFieldDataTypes.BINARY_TYPE, ECoalesceFieldDataTypes.getTypeForSQLType("ADLONGVARBINARY"));
//     
//    }
    
 }
