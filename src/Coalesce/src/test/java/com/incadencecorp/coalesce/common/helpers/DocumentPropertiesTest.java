package com.incadencecorp.coalesce.common.helpers;

import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.imageio.ImageIO;
import java.io.File;

import static org.junit.Assert.*;

public class DocumentPropertiesTest {

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        CoalesceUnitTestSettings.initialize();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    /*
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void initializeJpgTest() throws Exception
    {
        DocumentProperties docProps = new DocumentProperties();

        String imagePath = CoalesceUnitTestSettings.getResourceAbsolutePath("Desert.jpg");
        assertTrue(docProps.initialize(imagePath));

        assertEquals(imagePath, docProps.getFullFilename());
        assertEquals("Desert.jpg", docProps.getFilename());
        assertEquals("Desert", docProps.getFilenameWithoutExtension());
        assertEquals("jpg", docProps.getExtension());
        assertEquals(1024, docProps.getImageWidth());
        assertEquals(768, docProps.getImageHeight());
        assertEquals(49.39875240003339, docProps.getLatitude(), 0.00000000000001);
        assertEquals(8.67243350003624, docProps.getLongitude(), 0.00000000000001);
        assertEquals(851530, docProps.getSize());
        assertEquals(MimeHelper.getMimeTypeForExtension("jpg"), docProps.getMimeType());
        assertEquals(MimeHelper.getFileTypeForMimeType(docProps.getMimeType()), docProps.getDocumentType());
        //assertEquals(new DateTime("2008-03-14T17:59:26.000Z"), docProps.getCreated());
        // assertEquals(new DateTime("2014-09-19T15:51:18.637Z"), docProps.getModified());
        assertEquals("", docProps.getThumbnailFilename());

        ImageIO.write(docProps.getThumbnail(),
                      CoalesceSettings.getImageFormat(),
                      new File(FilenameUtils.concat(CoalesceUnitTestSettings.getDefaultApplicationRoot(),
                                                    "desert_thumbnail_test.jpg")));
    }

    @Test
    public void initializeDocXTest() throws Exception
    {
        DocumentProperties docProps = new DocumentProperties();

        String imagePath = CoalesceUnitTestSettings.getResourceAbsolutePath("TestDocument.docx");
        assertTrue(docProps.initialize(imagePath));

        assertEquals("Documentation", docProps.getCategory());
        assertEquals("Draft", docProps.getContentStatus());
        assertEquals("", docProps.getContentType());
        assertEquals(new DateTime("2014-09-19T15:23:00.000Z"), docProps.getCreated());
        assertEquals("Test user", docProps.getCreator());
        assertEquals("This is for testing", docProps.getDescription());
        assertEquals("", docProps.getIdentifier());
        assertEquals(0, docProps.getImageWidth());
        assertEquals(0, docProps.getImageHeight());
        assertEquals("Test, Document", docProps.getKeywords());
        assertEquals("", docProps.getLanguage());
        assertEquals(0, docProps.getLatitude(), 0.00000000000001);
        assertEquals(0, docProps.getLongitude(), 0.00000000000001);
        assertEquals("The Test User", docProps.getLastModifiedBy());
        assertEquals(new DateTime("2009-07-30T23:00:00.000Z"), docProps.getLastPrinted());
        assertEquals(new DateTime("2014-09-19T15:24:00.000Z"), docProps.getModified());
        assertEquals(MimeHelper.getMimeTypeForExtension("docx"), docProps.getMimeType());
        assertEquals(MimeHelper.getFileTypeForMimeType(docProps.getMimeType()), docProps.getDocumentType());
        assertEquals(1, docProps.getPageCount());
        assertEquals("4", docProps.getRevision());
        assertEquals("Testing subject", docProps.getSubject());
        assertEquals("Testing document title", docProps.getTitle());
        assertEquals("", docProps.getVersion());

    }

    @Test
    public void initializeDocXMultiPageTest() throws Exception
    {
        DocumentProperties docProps = new DocumentProperties();

        String imagePath = CoalesceUnitTestSettings.getResourceAbsolutePath("TestDocumentMultipage.docx");
        assertTrue(docProps.initialize(imagePath));

        assertEquals(4, docProps.getPageCount());

    }

    @Test
    public void initializeNullFilenameTest() throws Exception
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("fullFilename");

        DocumentProperties docProps = new DocumentProperties();

        docProps.initialize(null);
    }

    @Test
    public void initializeEmptyFilenameTest() throws Exception
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize(""));

    }

    @Test
    public void initializeWhitespaceFilenameTest() throws Exception
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize("   "));

    }

    @Test
    public void initializeInvalidFilenameTest() throws Exception
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize("abc.xyz", true));

        assertFalse(docProps.initialize("abc<.txt", false));

    }

    @Test
    public void initializeEncryptedFileTest() throws Exception
    {
        CoalesceUnitTestSettings.verifyEncryption();

        DocumentProperties plainTextDocProps = new DocumentProperties();
        String filePath = CoalesceUnitTestSettings.getResourceAbsolutePath("TestDocument.docx");
        assertTrue(plainTextDocProps.initialize(filePath, false));

        DocumentProperties encryptedDocProps = new DocumentProperties();
        String encryptedFilePath = CoalesceUnitTestSettings.getResourceAbsolutePath("encryptedTestDocument.docx");
        assertTrue(encryptedDocProps.initialize(encryptedFilePath, true));

        assertEquals(plainTextDocProps.getCategory(), encryptedDocProps.getCategory());
        assertEquals(plainTextDocProps.getContentStatus(), encryptedDocProps.getContentStatus());
        assertEquals(plainTextDocProps.getContentType(), encryptedDocProps.getContentType());
        assertEquals(plainTextDocProps.getCreated(), encryptedDocProps.getCreated());
        assertEquals(plainTextDocProps.getCreator(), encryptedDocProps.getCreator());
        assertEquals(plainTextDocProps.getDescription(), encryptedDocProps.getDescription());
        assertEquals(plainTextDocProps.getIdentifier(), encryptedDocProps.getIdentifier());
        assertEquals(plainTextDocProps.getKeywords(), encryptedDocProps.getKeywords());
        assertEquals(plainTextDocProps.getLanguage(), encryptedDocProps.getLanguage());
        assertEquals(plainTextDocProps.getLastModifiedBy(), encryptedDocProps.getLastModifiedBy());
        assertEquals(plainTextDocProps.getLastPrinted(), encryptedDocProps.getLastPrinted());
        assertEquals(plainTextDocProps.getModified(), encryptedDocProps.getModified());
        assertEquals(plainTextDocProps.getPageCount(), encryptedDocProps.getPageCount());
        assertEquals(plainTextDocProps.getRevision(), encryptedDocProps.getRevision());
        assertEquals(plainTextDocProps.getSubject(), encryptedDocProps.getSubject());
        assertEquals(plainTextDocProps.getTitle(), encryptedDocProps.getTitle());
        assertEquals(plainTextDocProps.getVersion(), encryptedDocProps.getVersion());
    }

    @Test
    public void thumbnailFilenameTest()
    {

        DocumentProperties docProps = new DocumentProperties();

        assertEquals("", docProps.getThumbnailFilename());
        docProps.setThumbnailFilename("testingFilename.txt");

        assertEquals("testingFilename.txt", docProps.getThumbnailFilename());
    }

}
