package Coalesce.Common.UnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Coalesce.Common.Helpers.DocumentThumbnailHelper.DocumentThumbnailResults;

public class CoalesceAssert {

    private static BufferedImage EXPECTED_DESERT_THUMBNAIL;

    // Make class static
    private CoalesceAssert()
    {

    }

    public static void assertXmlEquals(String expected, String actual, String encoding)
    {
        String stripped = actual.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\" standalone=\"yes\"?>", "");
        stripped = stripped.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>", "");
        String converted = stripped.replace(" ", "").replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        String expectedStripped = expected.replace("<?xml version=\"1.0\" encoding=\"" + encoding
                + "\" standalone=\"yes\"?>", "");
        expectedStripped = expectedStripped.replace("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>", "");

        String expectedConverted = expectedStripped.replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        assertEquals(expectedConverted, converted);

    }

    public static void assertThumbnail(DocumentThumbnailResults actual)
    {
        assertEquals(1024, actual.getOriginalWidth());
        assertEquals(768, actual.getOriginalHeight());

        assertThumbnail(actual.getThumbnail());

    }

    public static void assertThumbnail(BufferedImage actual)
    {
        assertTrue("Thumbnail is not correct", testImagesEqual(CoalesceAssert.getExpectedThumbnail(), actual));
    }

    public static boolean testImagesEqual(BufferedImage img1, BufferedImage img2)
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

    private static BufferedImage getExpectedThumbnail()
    {
        if (CoalesceAssert.EXPECTED_DESERT_THUMBNAIL == null)
        {
            try
            {
                CoalesceAssert.EXPECTED_DESERT_THUMBNAIL = ImageIO.read(new File("src/resources/desert_thumb.png"));
            }
            catch (IOException e)
            {
            }
        }

        return CoalesceAssert.EXPECTED_DESERT_THUMBNAIL;
    }
}
