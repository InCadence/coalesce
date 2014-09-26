package Coalesce.Common.Helpers;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

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
        getDocumentThumbnailForMimeCategoryTest("XYZ", "LargeIcon_Unknown.png");
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
    public void getDocumentThumbnailForMimeTypeZipTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("ZIP", "LargeIcon_Zip.png");
    }

    @Test
    public void getDocumentThumbnailForMimeTypeUnknownTest() throws IOException
    {
        getDocumentThumbnailForMimeTypeTest("XYZ", "LargeIcon_Unknown.png");
    }

    private void getDocumentThumbnailForMimeCategoryTest(String extension, String resourceName) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(extension);
        BufferedImage image = DocumentThumbnailHelper.getThumbnailForMimeCategory(mimeType);

        assertNotNull(image);

        BufferedImage testImage = ImageIO.read(getClass().getResource("/resources/" + resourceName));

        assertImagesEqual(testImage, image);

    }

    private void getDocumentThumbnailForMimeTypeTest(String extension, String resourceName) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(extension);
        BufferedImage image = DocumentThumbnailHelper.getThumbnailForMimeType(mimeType);

        assertNotNull(image);

        BufferedImage testImage = ImageIO.read(getClass().getResource("/resources/" + resourceName));

        assertImagesEqual(testImage, image);

    }

    private boolean assertImagesEqual(BufferedImage img1, BufferedImage img2)
    {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())
        {
            for (int x = 0; x < img1.getWidth(); x++)
            {
                for (int y = 0; y < img1.getHeight(); y++)
                {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) return false;
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }
}
