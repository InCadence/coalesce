package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;

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

    @BeforeClass
    public static void setUpBeforeClass() throws IOException
    {
        CoalesceUnitTestSettings.initialize();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

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
    public void getFileAsByteArrayTest() throws IOException
    {
        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");

        Path filePath = Paths.get(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "fileAsByteArray.txt"));

        Files.write(filePath, dataBytes);

        byte[] fileBytes = FileHelper.getFileAsByteArray(filePath.toAbsolutePath().toString());

        assertArrayEquals(dataBytes, fileBytes);

        Files.delete(filePath);

    }

    @Test
    public void getFileAsByteArrayDoesNotExistTest() throws IOException
    {
        Path filePath = Paths.get(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "fileAsByteArray.txt"));

        byte[] fileBytes = FileHelper.getFileAsByteArray(filePath.toAbsolutePath().toString());

        assertArrayEquals(null, fileBytes);

    }

    @Test
    public void deleteFileTest() throws IOException
    {
        File newFile = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "fileToDelete.txt"));

        assertFalse(newFile.exists());

        assertFalse(FileHelper.deleteFile(newFile.getAbsolutePath()));

        assertTrue(newFile.createNewFile());

        assertTrue(FileHelper.deleteFile(newFile.getAbsolutePath()));

        assertFalse(newFile.exists());

        assertFalse(FileHelper.deleteFile(null));
        
    }

    @Test
    public void deleteFileThatIsAFolderTest()
    {
        File newFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "folderToDelete"));

        assertFalse(newFolder.exists());

        assertTrue(newFolder.mkdir());

        assertFalse(FileHelper.deleteFile(newFolder.getAbsolutePath()));

        assertTrue(newFolder.exists());

        assertTrue(FileHelper.deleteFolder(newFolder.getAbsolutePath()));
    }

    @Test
    public void deleteFolderTest()
    {
        File newFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "folderToDelete"));

        assertFalse(newFolder.exists());

        assertFalse(FileHelper.deleteFolder(newFolder.getAbsolutePath()));

        assertTrue(newFolder.mkdir());

        assertTrue(FileHelper.deleteFolder(newFolder.getAbsolutePath()));

        assertFalse(newFolder.exists());

        assertFalse(FileHelper.deleteFolder(null));

        File badPathFile = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "this<file>.txt"));
        
        assertFalse(badPathFile.exists());
        
        assertFalse(FileHelper.deleteFolder(badPathFile.getAbsolutePath()));
        
    }

    @Test
    public void deleteFolderWithFilesTest() throws IOException
    {
        File newFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "folderToDelete"));

        assertTrue(newFolder.mkdir());

        File newFile = new File(newFolder.getAbsolutePath() + "\\file1.txt");

        newFile.createNewFile();

        assertTrue(FileHelper.deleteFolder(newFolder.getAbsolutePath()));

        assertFalse(newFolder.exists());

    }

    @Test
    public void deleteFolderNoForceDeleteWithFilesTest() throws IOException
    {
        File newFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "folderToDelete"));

        assertTrue(newFolder.mkdir());

        File newFile = new File(newFolder.getAbsolutePath() + "\\file1.txt");

        newFile.createNewFile();

        assertFalse(FileHelper.deleteFolder(newFolder.getAbsolutePath(), false));

        assertTrue(newFolder.exists());

        FileHelper.deleteFolder(newFolder.getAbsolutePath(), true);
        
        assertFalse(FileHelper.deleteFolder(null, false));
        
    }

    @Test
    public void deleteFolderThatIsAFileTest() throws IOException
    {
        File newFile = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "fileToDelete.txt"));

        assertFalse(newFile.exists());

        assertFalse(FileHelper.deleteFolder(newFile.getAbsolutePath()));

        assertTrue(newFile.createNewFile());

        assertFalse(FileHelper.deleteFolder(newFile.getAbsolutePath()));

        assertTrue(newFile.exists());

        assertTrue(FileHelper.deleteFile(newFile.getAbsolutePath()));

    }

    @Test
    public void checkFolderTest()
    {
        File newFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "folderToDelete"));

        assertFalse(newFolder.exists());
        
        assertTrue(FileHelper.checkFolder(newFolder.getAbsolutePath()));
        
        assertTrue(newFolder.exists());
        
        assertTrue(FileHelper.checkFolder(newFolder.getAbsolutePath()));
        
        newFolder.delete();
        
        assertFalse(FileHelper.checkFolder(null));
        
        File badPathFolder = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "this<file>"));
        
        assertFalse(badPathFolder.exists());
        
        assertFalse(FileHelper.checkFolder(badPathFolder.getAbsolutePath()));
        
        assertFalse(badPathFolder.exists());
        
    }
    
    @Test
    public void checkFolderFileExistsTest() throws IOException
    {
        File newFile = new File(FilenameUtils.concat(CoalesceSettings.getBinaryFileStoreBasePath(), "fileToDelete.txt"));

        assertFalse(newFile.exists());
        
        newFile.createNewFile();
        
        assertTrue(newFile.exists());
        
        assertFalse(FileHelper.checkFolder(newFile.getAbsolutePath()));

        newFile.delete();        
        
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyNullTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         0,
                                                                         null,
                                                                         false);

        assertNull(filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyEmptyTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         0,
                                                                         "",
                                                                         false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyWhitespaceTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         0,
                                                                         " ",
                                                                         false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeyInvalidGuidTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         0,
                                                                         "ABCD",
                                                                         false);

        assertNull(filename);

    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirZeroTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         0,
                                                                         GUID,
                                                                         false);

        assertEquals(CoalesceUnitTestSettings.getBinaryFileStoreBasePath() + GUID.toUpperCase(), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirOneTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         1,
                                                                         GUID,
                                                                         false);

        assertEquals(FilenameUtils.concat(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                          GUID.substring(0, 1).toUpperCase() + "\\" + GUID.toUpperCase()), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirThirtyFiveTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         35,
                                                                         GUID,
                                                                         false);

        assertEquals(FilenameUtils.concat(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                          GUID.substring(0, 35).toUpperCase() + "\\" + GUID.toUpperCase()), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirThirtySixTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         36,
                                                                         GUID,
                                                                         false);

        assertEquals(CoalesceUnitTestSettings.getBinaryFileStoreBasePath() + GUID.toUpperCase(), filename);
    }

    @Test
    public void getBaseFilenameWithFullDirectoryPathForKeySubDirThirtySevenTest() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {

        String filename = callGetBaseFilenameWithFullDirectoryPathForKey(CoalesceUnitTestSettings.getBinaryFileStoreBasePath(),
                                                                         37,
                                                                         GUID,
                                                                         false);

        assertEquals(CoalesceUnitTestSettings.getBinaryFileStoreBasePath() + GUID.toUpperCase(), filename);
    }

    private String callGetBaseFilenameWithFullDirectoryPathForKey(String binaryFileStoreBasePath,
                                                                  int subDirectoryLength,
                                                                  String key,
                                                                  boolean createIfDoesNotExist)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
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
