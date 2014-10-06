package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.incadencecorp.coalesce.common.helpers.FileHelper;

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

public class FileHelperTest {

    private static final String GUID = "313dab28-ac40-4cf2-b990-92f0e85eb15c";

    private static final String _binaryFileStoreBasePath = "C:\\Program Files\\Java\\jre7\\bin\\uploads\\";

    /*
     * @BeforeClass public static void setUpBeforeClass() { }
     * 
     * @AfterClass public static void tearDownAfterClass() { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void getExtensionNullTest()
    {
       assertEquals("", FileHelper.getExtension(null)); 
    }
    
    @Test
    public void getExtensionEmptyStringTest()
    {
        assertEquals("", FileHelper.getExtension(""));
    }
    
    @Test
    public void getExtensionWhiteSpaceTest()
    {
        assertEquals("", FileHelper.getExtension("  "));
    }
    
    @Test
    public void getExtensionNoneTest()
    {
        assertEquals("", FileHelper.getExtension("file"));
    }
    
    @Test
    public void getShortFilenameTest()
    {
        assertEquals("testFile.txt", FileHelper.getShortFilename("C:\\location\\testFile.txt"));
    }
    
    @Test
    public void getShortFilenameJustFilenameTest()
    {
        assertEquals("testFile.txt", FileHelper.getShortFilename("testFile.txt"));
    }
    
    @Test
    public void getShortFilenameNullTest()
    {
        assertEquals(null, FileHelper.getShortFilename(null));
    }
    
    @Test
    public void getShortFilenameEmptyTest()
    {
        assertEquals("", FileHelper.getShortFilename(""));
    }

    @Test
    public void getShortFilenameWhiteSpaceTest()
    {
        assertEquals("   ", FileHelper.getShortFilename("   "));
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyNullTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 0, null, false);

        assertNull(filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyEmptyTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 0, "", false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyWhitespaceTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 0, " ", false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyInvalidGuidTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 0, "ABCD", false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirZeroTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 0, GUID, false);

        assertEquals(_binaryFileStoreBasePath + GUID.toUpperCase(), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirOneTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 1, GUID, false);

        assertEquals(FilenameUtils.concat(_binaryFileStoreBasePath,
                                          GUID.substring(0, 1).toUpperCase() + "\\" + GUID.toUpperCase()),
                     filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirThirtyFiveTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 35, GUID, false);

        assertEquals(FilenameUtils.concat(_binaryFileStoreBasePath,
                                          GUID.substring(0, 35).toUpperCase() + "\\" + GUID.toUpperCase()),
                     filename);
    }

    @Test
    public void GetBaseFilenameWithFullDirectoryPathForKeySubDirThirtySixTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 36, GUID, false);

        assertEquals(_binaryFileStoreBasePath + GUID.toUpperCase(), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirThirtySevenTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(_binaryFileStoreBasePath, 37, GUID, false);

        assertEquals(_binaryFileStoreBasePath + GUID.toUpperCase(), filename);
    }

    private String callGetBaseFilenameWithFullDirectoryPathForKey(String binaryFileStoreBasePath,
                                                                  int subDirectoryLength,
                                                                  String key,
                                                                  boolean createIfDoesNotExist)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Class<?>[] args = new Class[4];
        args[0] = String.class;
        args[1] = int.class;
        args[2] = String.class;
        args[3] = boolean.class;

        Method method = FileHelper.class.getDeclaredMethod("getBaseFilenameWithFullDirectoryPathForKey", args);
        method.setAccessible(true);

        Object results = method.invoke(null, binaryFileStoreBasePath, subDirectoryLength, key, createIfDoesNotExist);

        return (String) results;
    }
}
