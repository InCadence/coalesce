package Coalesce.Common.Helpers;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Coalesce.Common.UnitTest.CoalesceAssert;

public class GraphicsHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void resampleWithHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 60);

        CoalesceAssert.assertThumbnail(thumbnail);

    }

    @Test
    public void resampleWithHeightLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 768 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleWithZeroHeightTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithOneHeightTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithHeightSameTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 768);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleWithHeightNullTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("imageToResample");

        @SuppressWarnings("unused")
        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(null, 768);

    }

    @Test
    public void resampleWithWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 80);

        CoalesceAssert.assertThumbnail(thumbnail);

    }

    @Test
    public void resampleWithWidthLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleWithZeroWidthTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithOneWidthTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithWidthSameTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleWithWidthNullTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("imageToResample");

        @SuppressWarnings("unused")
        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(null, 768);

    }

    @Test
    public void resampleToExactSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 345, 234);

        assertEquals(234, thumbnail.getWidth());
        assertEquals(345, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 789, 456);

        assertEquals(456, thumbnail.getWidth());
        assertEquals(789, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactWithZeroSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactWithOneSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 1, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 618, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactNullTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("imageToResample");

        @SuppressWarnings("unused")
        BufferedImage thumbnail = GraphicsHelper.resampleToExact(null, 768, 888);

    }

    @Test
    public void resampleSmallestNullTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("imageToResample");

        @SuppressWarnings("unused")
        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(null, 768, 888);

    }

    @Test
    public void resampleToSmallestImageWideWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageWideHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 1024);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 80);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 1024 * 2);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 3, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024 * 3);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 383);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 37);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 2, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 383 * 2);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 2, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 3, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 2, 383 * 3);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestNullTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("imageToResample");

        @SuppressWarnings("unused")
        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(null, 768, 888);

    }

    @Test
    public void resampleToLargestImageWideWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768, 80);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 0);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 3, 1024 * 2);

        assertEquals(1024 * 3, thumbnail.getWidth());
        assertEquals(768 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024 * 3);

        assertEquals(1024 * 3, thumbnail.getWidth());
        assertEquals(768 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 37);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 0);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 3, 383 * 2);

        assertEquals(383 * 3, thumbnail.getWidth());
        assertEquals(618 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383 * 3);

        assertEquals(383 * 3, thumbnail.getWidth());
        assertEquals(618 * 3, thumbnail.getHeight());

    }

    @Test
    public void createFieldThumbnailTest()
    {
    }

}
