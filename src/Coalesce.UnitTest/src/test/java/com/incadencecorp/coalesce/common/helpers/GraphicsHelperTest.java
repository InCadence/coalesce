package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.apache.commons.lang.NullArgumentException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.incadencecorp.coalesce.common.CoalesceAssert;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

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

public class GraphicsHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static String FILE_FIELD_IMAGE_PATH;
    private static final String FILE_FIELD_GUID = "228df990-4d5b-11e4-916c-0800200c9a66";
    private static String FILE_FIELD_THUMBNAIL_PATH;
    private static final String NOT_FILE_FIELD_GUID = "486af7d0-4d5b-11e4-916c-0800200c9a66";
    private static String NOT_FILE_FIELD_THUMBNAIL_PATH;

    /*
     * @After public void tearDown() throws Exception { }
     */

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        CoalesceUnitTestSettings.initialize();

        GraphicsHelperTest.copyTestImagesToBin();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    @Before
    public void setUp() throws Exception
    {
        GraphicsHelperTest.cleanUpTestThumbnails();
    }

    private static void copyTestImagesToBin() throws IOException
    {

        GraphicsHelperTest.FILE_FIELD_IMAGE_PATH = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(GraphicsHelperTest.FILE_FIELD_GUID)
                + ".jpg";

        GraphicsHelperTest.FILE_FIELD_THUMBNAIL_PATH = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(GraphicsHelperTest.FILE_FIELD_GUID)
                + "_thumb." + CoalesceSettings.getImageFormat();
        GraphicsHelperTest.NOT_FILE_FIELD_THUMBNAIL_PATH = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(GraphicsHelperTest.NOT_FILE_FIELD_GUID)
                + "_thumb." + CoalesceSettings.getImageFormat();

        File fieldFile = new File(GraphicsHelperTest.FILE_FIELD_IMAGE_PATH);
        fieldFile.getParentFile().mkdirs();

        Files.copy(Paths.get("src/test/resources/Desert.jpg"),
                   Paths.get(GraphicsHelperTest.FILE_FIELD_IMAGE_PATH),
                   StandardCopyOption.REPLACE_EXISTING);

    }

    private static void cleanUpTestThumbnails()
    {
        try
        {
            Files.delete(Paths.get(GraphicsHelperTest.FILE_FIELD_THUMBNAIL_PATH));
        }
        catch (IOException e1)
        {
        }

        try
        {
            Files.delete(Paths.get(GraphicsHelperTest.NOT_FILE_FIELD_THUMBNAIL_PATH));
        }
        catch (IOException e)
        {
        }

    }

    @Test
    public void resampleWithHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 60);

        CoalesceAssert.assertThumbnail(thumbnail);

    }

    @Test
    public void resampleWithHeightLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 768 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleWithZeroHeightTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithOneHeightTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithHeight(originalImage, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithHeightSameTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

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
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 80);

        CoalesceAssert.assertThumbnail(thumbnail);

    }

    @Test
    public void resampleWithWidthLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleWithZeroWidthTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithOneWidthTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleWithWidth(originalImage, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleWithWidthSameTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

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
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 345, 234);

        assertEquals(234, thumbnail.getWidth());
        assertEquals(345, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 789, 456);

        assertEquals(456, thumbnail.getWidth());
        assertEquals(789, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactWithZeroSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 0, 0);

        org.junit.Assert.assertEquals((long) 1, (long) thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactWithOneSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToExact(originalImage, 1, 1);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToExactSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_tall.jpg"));

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
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageWideHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 1024);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

        CoalesceAssert.assertThumbnail(thumbnail);
    }

    @Test
    public void resampleToSmallestImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 80);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768, 1024 * 2);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 3, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageWideBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 768 * 2, 1024 * 3);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 383);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 60, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 37);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 2, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618, 383 * 2);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 2, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToSmallest(originalImage, 618 * 3, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToSmallestImageTallBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

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
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768, 80);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 1024);

        assertEquals(1024, thumbnail.getWidth());
        assertEquals(768, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 0);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 80);

        assertEquals(80, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024 * 2);

        assertEquals(1024 * 2, thumbnail.getWidth());
        assertEquals(768 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 3, 1024 * 2);

        assertEquals(1024 * 3, thumbnail.getWidth());
        assertEquals(768 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageWideBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 768 * 2, 1024 * 3);

        assertEquals(1024 * 3, thumbnail.getWidth());
        assertEquals(768 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallWidthSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 37);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallHeightSmallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageExactTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageWidthZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 60, 0);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(60, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageHeightZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 37);

        assertEquals(37, thumbnail.getWidth());
        assertEquals(59, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallImageZeroTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 0, 0);

        assertEquals(1, thumbnail.getWidth());
        assertEquals(1, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallSameSizeTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 383);

        assertEquals(383, thumbnail.getWidth());
        assertEquals(618, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallHeightTallerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallWidthWiderTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383 * 2);

        assertEquals(383 * 2, thumbnail.getWidth());
        assertEquals(618 * 2, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerHeightBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 3, 383 * 2);

        assertEquals(383 * 3, thumbnail.getWidth());
        assertEquals(618 * 3, thumbnail.getHeight());

    }

    @Test
    public void resampleToLargestImageTallBothLargerWidthBiggestTest() throws IOException
    {
        BufferedImage originalImage = ImageIO.read(new File("src/test/resources/desert_Tall.jpg"));

        BufferedImage thumbnail = GraphicsHelper.resampleToLargest(originalImage, 618 * 2, 383 * 3);

        assertEquals(383 * 3, thumbnail.getWidth());
        assertEquals(618 * 3, thumbnail.getHeight());

    }

    @Test
    public void createFieldThumbnailCoalesceFieldTest() throws IOException, CoalesceException
    {

        CoalesceRecord record = GraphicsHelperTest.getFieldThumbnailRecord();

        @SuppressWarnings("unchecked")
        CoalesceField<byte[]> file = (CoalesceField<byte[]>) record.getFieldByName("File");
        assertTrue("Thumbnail creation failed for file type",
                   GraphicsHelper.createFieldThumbnail(record.getFieldByName("File")));

        File thumbnail = new File(file.getCoalesceFullThumbnailFilename());
        assertTrue("Thumbnail was not created correctly!", thumbnail.exists());

        assertFalse("Thumbnail creation should have failed for a non-file type",
                    GraphicsHelper.createFieldThumbnail(record.getFieldByName("NotFile")));

    }

    @Test
    public void createFieldThumbnailFilenameTest() throws IOException, CoalesceException
    {

        CoalesceRecord record = GraphicsHelperTest.getFieldThumbnailRecord();

        @SuppressWarnings("unchecked")
        CoalesceField<byte[]> file = (CoalesceField<byte[]>) record.getFieldByName("File");
        assertTrue("Thumbnail creation failed for file type",
                   GraphicsHelper.createFieldThumbnail(file.getCoalesceFullFilename()));

        File thumbnail = new File(file.getCoalesceFullThumbnailFilename());
        assertTrue("Thumbnail was not created correctly!", thumbnail.exists());

        assertFalse("Thumbnail already exists", GraphicsHelper.createFieldThumbnail(file.getCoalesceFullFilename()));

        assertFalse("Thumbnail creation should have failed for a non-file type",
                    GraphicsHelper.createFieldThumbnail(record.getFieldByName("NotFile").getCoalesceFullFilename()));

    }

    @Test
    public void createFieldThumbnailFilenameFilenameIsDirectorTest() throws IOException, CoalesceException
    {

        CoalesceRecord record = GraphicsHelperTest.getFieldThumbnailRecord();

        @SuppressWarnings("unchecked")
        CoalesceField<byte[]> file = (CoalesceField<byte[]>) record.getFieldByName("File");

        Files.createDirectory(Paths.get(file.getCoalesceFullThumbnailFilename()));

        assertFalse("Thumbnail creation should have failed due to directory existing",
                    GraphicsHelper.createFieldThumbnail(file.getCoalesceFullFilename()));

    }

    private static CoalesceRecord getFieldThumbnailRecord() throws CoalesceException, IOException
    {
        CoalesceEntity entity = CoalesceEntity.create("Test Entity",
                                                      "Unit Test",
                                                      "1.0.0.0",
                                                      "TestEntity",
                                                      "UnitTest",
                                                      "Thumbnail Testing");
        CoalesceSection section = entity.createSection("Testing Section");
        CoalesceRecordset recordset = section.createRecordset("Testing Recordset");
        recordset.createFieldDefinition("File", ECoalesceFieldDataTypes.FILE_TYPE);
        recordset.createFieldDefinition("NotFile", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceFileField fileField = (CoalesceFileField) record.getFieldByName("File");
        fileField.setValue(Files.readAllBytes(Paths.get("src/test/resources/desert.jpg")), "desert.jpg", "jpg");

        @SuppressWarnings("unchecked")
        CoalesceField<byte[]> file = (CoalesceField<byte[]>) record.getFieldByName("File");
        file.setKey(GraphicsHelperTest.FILE_FIELD_GUID);

        CoalesceStringField notFile = (CoalesceStringField) record.getFieldByName("NotFile");
        notFile.setValue("Some Test Data");
        notFile.setKey(GraphicsHelperTest.NOT_FILE_FIELD_GUID);

        return record;

    }

}
