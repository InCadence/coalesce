package Coalesce.Common.Helpers;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import Coalesce.Common.Runtime.CoalesceSettings;

import com.drew.imaging.ImageProcessingException;

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
        File testThumbnail = new File("c:\\users\\wbrannock\\Desktop\\desert_thumbnail_test.jpg");
        testThumbnail.delete();

    }

    @Test
    public void initializeTest() throws ImageProcessingException, IOException
    {
        DocumentProperties docProps = new DocumentProperties();

        assertTrue(docProps.initialize("C:\\users\\wbrannock\\Desktop\\Desert.jpg"));

        ImageIO.write(docProps.getThumbnail(),
                      CoalesceSettings.GetImageFormat(),
                      new File("c:\\users\\wbrannock\\Desktop\\desert_thumbnail_test.jpg"));
    }

}
