package Coalesce.Common.Helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import com.drew.imaging.ImageProcessingException;

import Coalesce.Common.Runtime.CoalesceSettings;

public class DocumentPropertiesTest {

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        removeOldTestImages();

    }

    private static void removeOldTestImages()
    {
        File testThumbnail = new File("bin\\desert_thumbnail_test.jpg");
        testThumbnail.delete();

    }

    @Test
    public void initializeJpgTest() throws IOException, JDOMException, ImageProcessingException
    {
        DocumentProperties docProps = new DocumentProperties();

        assertTrue(docProps.initialize("src\\resources\\Desert.jpg"));

        assertEquals("src\\resources\\Desert.jpg", docProps.getFullFilename());
        assertEquals("Desert.jpg", docProps.getFilename());
        assertEquals("Desert", docProps.getFilenameWithoutExtension());
        assertEquals("jpg", docProps.getExtension());
        assertEquals(851530, docProps.getSize());
        assertEquals(MimeHelper.getMimeTypeForExtension("jpg"), docProps.getMimeType());
        assertEquals(new DateTime("2008-03-14T17:59:26.000Z"), docProps.getCreated());
        assertEquals(new DateTime("2014-09-17T18:22:56.000Z"), docProps.getModified());
        
        ImageIO.write(docProps.getThumbnail(),
                      CoalesceSettings.GetImageFormat(),
                      new File("bin\\desert_thumbnail_test.jpg"));
    }

    @Test
    public void initializeDocXTest() throws ImageProcessingException, IOException, JDOMException
    {
        DocumentProperties docProps = new DocumentProperties();

        assertTrue(docProps.initialize("src\\resources\\TestDocument.docx"));

        assertEquals("Documentation", docProps.getCategory());
        assertEquals("Draft", docProps.getContentStatus());
        assertEquals("", docProps.getContentType());
        assertEquals(new DateTime("2014-09-19T15:23:00.000Z"), docProps.getCreated());
        assertEquals("Test user", docProps.getCreator());
        assertEquals("This is for testing", docProps.getDescription());
        assertEquals("", docProps.getIdentifier());
        assertEquals("Test, Document", docProps.getKeywords());
        assertEquals("", docProps.getLanguage());
        assertEquals("The Test User", docProps.getLastModifiedBy());
        assertEquals(new DateTime("2009-07-30T23:00:00.000Z"), docProps.getLastPrinted());
        assertEquals(new DateTime("2014-09-19T15:24:00.000Z"), docProps.getModified());
        assertEquals("4", docProps.getRevision());
        assertEquals("Testing subject", docProps.getSubject());
        assertEquals("Testing document title", docProps.getTitle());
        assertEquals("", docProps.getVersion());
        
        
    }
}
