package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceAssert;
import com.incadencecorp.coalesce.common.helpers.DocumentThumbnailHelper.DocumentThumbnailResults;

public class DocumentThumbnailHelperTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void getThumbnailForFileFullFilenameTest() throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile("src/test/resources/desert.jpg");

        CoalesceAssert.assertThumbnail(results);

    }

    @Test
    public void getThumbnailForFileFullFilenameWithoutEncryptionTest() throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile("src/test/resources/desert.jpg", false);

        CoalesceAssert.assertThumbnail(results);

    }

    @Test
    public void getThumbnailForFileFullFilenameWithEncryptionTest() throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile("src/test/resources/desert_encrypted.jpg",
                                                                                       true);

        CoalesceAssert.assertThumbnail(results);

    }

    @Test
    public void getThumbnailForFileFullFilenameWithoutEncryptionNotImageTest() throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile("src/test/resources/TestDocument.docx",
                                                                                       false);

        BufferedImage testImage = ImageIO.read(getClass().getResource("/LargeIcon_Word.png"));

        assertTrue("Thumbnail is not correct", CoalesceAssert.testImagesEqual(testImage, results.getThumbnail()));

    }

    @Test
    public void getThumbnailForFileFullFilenameWithEncryptionNotImageTest() throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile("src/test/resources/encryptedTestDocument.docx",
                                                                                       true);

        BufferedImage testImage = ImageIO.read(getClass().getResource("/LargeIcon_Word.png"));

        assertTrue("Thumbnail is not correct", CoalesceAssert.testImagesEqual(testImage, results.getThumbnail()));

    }

    @Test
    public void getThumbnailForFileBytesTest() throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/desert.jpg"));

        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile(bytes);

        CoalesceAssert.assertThumbnail(results);

    }

    @Test
    public void getThumbnailForFileBytesEmptyTest() throws IOException
    {
        byte[] bytes = new byte[0];

        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile(bytes);

        BufferedImage expectedThumbnail = ImageIO.read(new File("src/test/resources/LargeIcon_Image.png"));

        assertEquals(0, results.getOriginalWidth());
        assertEquals(0, results.getOriginalHeight());
        assertTrue("Thumbnail does not match", CoalesceAssert.testImagesEqual(expectedThumbnail, results.getThumbnail()));

    }

    @Test
    public void getThumbnailForFileExtensionsTest() throws IOException
    {
        getThumbnailForFileExtensionsTest("AIF", "LargeIcon_Audio.png");
        getThumbnailForFileExtensionsTest("ASR", "LargeIcon_Video.png");
        getThumbnailForFileExtensionsTest("CMX", "LargeIcon_Image.png");
        getThumbnailForFileExtensionsTest("BAS", "LargeIcon_Text.png");
        getThumbnailForFileExtensionsTest("DOC", "LargeIcon_Word.png");
        getThumbnailForFileExtensionsTest("DocX", "LargeIcon_Word.png");
        getThumbnailForFileExtensionsTest("XLS", "LargeIcon_Excel.png");
        getThumbnailForFileExtensionsTest("XlsX", "LargeIcon_Excel.png");
        getThumbnailForFileExtensionsTest("PPT", "LargeIcon_Powerpoint.png");
        getThumbnailForFileExtensionsTest("PPTX", "LargeIcon_Powerpoint.png");
        getThumbnailForFileExtensionsTest("ACCDB", "LargeIcon_Access.png");
        getThumbnailForFileExtensionsTest("PDF", "LargeIcon_Pdf.png");
        getThumbnailForFileExtensionsTest("TXT", "LargeIcon_Text.png");
        getThumbnailForFileExtensionsTest("ZIP", "LargeIcon_Zip.png");
        getThumbnailForFileExtensionsTest("XYZ", "LargeIcon_Blank.png");
    }

    @Test
    public void getDocumentThumbnailForMimeCategoryAudioTest() throws IOException
    {
        getDocumentThumbnailForMimeCategoryTest("AIF", "LargeIcon_Audio.png");
    }

    @Test
    public void getDocumentThumbnailForMimeCategoryVideoTest() throws IOException
    {
        getDocumentThumbnailForMimeCategoryTest("ASR", "LargeIcon_Video.png");
    }

    @Test
    public void getDocumentThumbnailForMimeCategoryImageTest() throws IOException
    {
        getDocumentThumbnailForMimeCategoryTest("CMX", "LargeIcon_Image.png");
    }

    @Test
    public void getDocumentThumbnailForMimeCategoryTextTest() throws IOException
    {
        getDocumentThumbnailForMimeCategoryTest("BAS", "LargeIcon_Text.png");
    }

    @Test
    public void getDocumentThumbnailForMimeCategoryUnknownTest() throws IOException
    {
        getDocumentThumbnailForMimeCategoryTest("XYZ", "LargeIcon_Blank.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeMSWordTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("DOC", "LargeIcon_Word.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeDocXTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("DocX", "LargeIcon_Word.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeMSExcelTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("XLS", "LargeIcon_Excel.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeExcelXTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("XlsX", "LargeIcon_Excel.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeMSPptTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("PPT", "LargeIcon_Powerpoint.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypePptXTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("PPTX", "LargeIcon_Powerpoint.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeMSAccessTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("ACCDB", "LargeIcon_Access.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypePDFTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("PDF", "LargeIcon_Pdf.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypePlainTextTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("TXT", "LargeIcon_Text.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeZipTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("ZIP", "LargeIcon_Zip.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeUnknownTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("XYZ", "LargeIcon_Blank.png");
    }

    private void getThumbnailForFileExtensionsTest(String extension, String resourceName) throws IOException
    {
        BufferedImage image = DocumentThumbnailHelper.getThumbnailForFileExtensions(extension);

        assertNotNull(image);

        BufferedImage testImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource(resourceName));

        assertTrue("Thumbnail for File extension failed: " + extension, CoalesceAssert.testImagesEqual(testImage, image));

    }

    private void getDocumentThumbnailForMimeCategoryTest(String extension, String resourceName) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(extension);
        BufferedImage image = DocumentThumbnailHelper.getThumbnailForMimeCategory(mimeType);

        assertNotNull(image);

        assertTrue("Extension Mime Category test failed: " + extension, testImagesEqual(resourceName, image));

    }

    private void getDocumentThumbnailForMimeTypeTest(String extension, String resourceName) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(extension);
        BufferedImage image = DocumentThumbnailHelper.getThumbnailForMimeType(mimeType);

        assertNotNull(image);

        assertTrue("Extension MimeType test failed: " + extension, testImagesEqual(resourceName, image));

    }

    private boolean testImagesEqual(String resourceName, BufferedImage img2) throws IOException
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        BufferedImage img1 = ImageIO.read(url);

        return CoalesceAssert.testImagesEqual(img1, img2);
    }

}
