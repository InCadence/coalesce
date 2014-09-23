package Coalesce.Common.Helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.NullArgumentException;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Coalesce.Common.Exceptions.CoalesceCryptoException;
import Coalesce.Common.Runtime.CoalesceSettings;

import com.drew.imaging.ImageProcessingException;

public class DocumentPropertiesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void initializeJpgTest() throws IOException, JDOMException, ImageProcessingException, CoalesceCryptoException
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
        assertEquals(new DateTime("2014-09-19T15:51:18.637Z"), docProps.getModified());
        
        ImageIO.write(docProps.getThumbnail(),
                      CoalesceSettings.getImageFormat(),
                      new File("bin\\desert_thumbnail_test.jpg"));
    }

    @Test
    public void initializeDocXTest() throws ImageProcessingException, IOException, JDOMException, CoalesceCryptoException
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

    @Test
    public void initializeNullFilenameTest() throws ImageProcessingException, IOException, JDOMException,
            CoalesceCryptoException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("fullFilename");

        DocumentProperties docProps = new DocumentProperties();

        docProps.initialize(null);
    }

    @Test
    public void initializeEmptyFilenameTest() throws ImageProcessingException, IOException, JDOMException,
            CoalesceCryptoException
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize(""));

    }

    @Test
    public void initializeWhitespaceFilenameTest() throws ImageProcessingException, IOException, JDOMException,
            CoalesceCryptoException
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize("   "));

    }

    @Test
    public void initializeInvalidFilenameTest() throws ImageProcessingException, IOException, JDOMException,
            InvalidKeyException, NoSuchAlgorithmException, CoalesceCryptoException
    {

        DocumentProperties docProps = new DocumentProperties();

        assertFalse(docProps.initialize("abc.xyz", true));

    }

    @Test
    public void initializeEncryptedFileTest() throws ImageProcessingException, IOException, JDOMException,
            CoalesceCryptoException
    {
        DocumentProperties plainTextDocProps = new DocumentProperties();
        plainTextDocProps.initialize("src\\resources\\TestDocument.docx", false);

        DocumentProperties encryptedDocProps = new DocumentProperties();
        encryptedDocProps.initialize("src\\resources\\encryptedTestDocument.docx", true);

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
        assertEquals(plainTextDocProps.getRevision(), encryptedDocProps.getRevision());
        assertEquals(plainTextDocProps.getSubject(), encryptedDocProps.getSubject());
        assertEquals(plainTextDocProps.getTitle(), encryptedDocProps.getTitle());
        assertEquals(plainTextDocProps.getVersion(), encryptedDocProps.getVersion());
    }

}
