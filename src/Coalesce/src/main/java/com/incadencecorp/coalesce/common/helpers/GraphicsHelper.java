package com.incadencecorp.coalesce.common.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;

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

/**
 * Provides helper methods for resampling images and generating thumbnails.
 * 
 * @author InCadence
 *
 */
public final class GraphicsHelper {

    // Make class static
    private GraphicsHelper()
    {
        // Do Nothing
    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns a new image resampled from the provided image with a specified height. The aspect ratio of the original image
     * is maintained.
     * 
     * @param imageToResample the image to use for resampling.
     * @param height the height of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleWithHeight(BufferedImage imageToResample, int height)
    {
        if (imageToResample == null) throw new IllegalArgumentException("imageToResample");

        // Calculate the Width, maintaining the same aspect ratio as the original image.
        return resampleToExact(imageToResample, height, height * imageToResample.getWidth() / imageToResample.getHeight());
    }

    /**
     * Returns a new image resampled from the provided image with a specified width. The aspect ratio of the original image
     * is maintained.
     * 
     * @param imageToResample the image to use for resampling.
     * @param width the width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleWithWidth(BufferedImage imageToResample, int width)
    {
        if (imageToResample == null) throw new IllegalArgumentException("imageToResample");

        // Calculate the Height, maintaining the same aspect ratio as the original image.
        return resampleToExact(imageToResample, width * imageToResample.getHeight() / imageToResample.getWidth(), width);
    }

    /**
     * Returns a new image resampled from the provided image with a specified width and height. The aspect ratio of the
     * original image is not maintained.
     * 
     * @param imageToResample the image to use for resampling.
     * @param height the height of the resampled image.
     * @param width the width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleToExact(BufferedImage imageToResample, int height, int width)
    {
        if (imageToResample == null) throw new IllegalArgumentException("imageToResample");

        return Scalr.resize(imageToResample, Scalr.Mode.FIT_EXACT, width, height, Scalr.OP_ANTIALIAS);
    }

    /**
     * Returns a new image resampled from the provided image while maintaining the original aspect ratio. The calculation for
     * resizing selects from the provided target parameters the one that will produce the smallest final size of the
     * resampled image.
     * 
     * @param imageToResample the image to use for resampling.
     * @param targetHeight the target height of the resampled image.
     * @param targetWidth the target width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleToSmallest(BufferedImage imageToResample, int targetHeight, int targetWidth)
    {

        double scaledWidth = GraphicsHelper.scaleWidthByHeightResampleRatio(imageToResample, targetHeight, targetWidth);
        if (scaledWidth <= targetWidth)
        {
            return GraphicsHelper.resampleWithHeight(imageToResample, targetHeight);
        }
        else
        {
            return GraphicsHelper.resampleWithWidth(imageToResample, targetWidth);
        }

    }

    /**
     * Returns a new image resampled from the provided image while maintaining the original aspect ratio. The calculation for
     * resizing selects from the provided target parameters the one that will produce the largest final size of the resampled
     * image.
     * 
     * @param imageToResample the image to use for resampling.
     * @param targetHeight the target height of the resampled image.
     * @param targetWidth the target width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleToLargest(BufferedImage imageToResample, int targetHeight, int targetWidth)
    {
        double scaledWidth = GraphicsHelper.scaleWidthByHeightResampleRatio(imageToResample, targetHeight, targetWidth);
        if (scaledWidth >= targetWidth)
        {
            return GraphicsHelper.resampleWithHeight(imageToResample, targetHeight);
        }
        else
        {
            return GraphicsHelper.resampleWithWidth(imageToResample, targetWidth);
        }

    }

    /**
     * Creates a new thumbnail image of the file provided. The new images filename will have the format of the original file
     * name minus extension plus '_thumbnail' and the extension of the image format provided by
     * {@link CoalesceSettings#getImageFormat()}. The new thumbnail image will be saved in the same location as the original
     * image file. If the image format provided by {@link CoalesceSettings#getImageFormat()} does not match the format of the
     * original file then the thumbnail will not be created.
     * 
     * <pre>
     * Ex.
     * 
     *    Original filename                 = testImage.jpg
     *    CoalesceSettings.getImageFormat() = "jpg"
     *    New thumbnail filename            = testImage_thumbnail.jpg
     * 
     * </pre>
     * 
     * @param filename the name of the file including full path.
     * @return <code>true</code> if the file is successfully created. <code>false</code> if the thumbnail file already exists
     *         or the system image format does not match the format of the provided file.
     * @throws IOException
     */
    public static boolean createFieldThumbnail(String filename) throws IOException
    {
        String imageFormat = CoalesceSettings.getImageFormat();

        File imageFile = new File(filename);
        if (!(imageFile.exists() && imageFile.isFile())) return false;

        String imageName = imageFile.getName();
        String imageFileFormat = imageName.substring(imageName.length() - 3);
        File imageDir = imageFile.getParentFile();

        // create thumbnail name
        String thumbnailName = imageName.substring(0, imageName.length() - 4) + "_thumb." + imageFormat;
        File thumbnail = new File(imageDir, thumbnailName);

        // create thumbnail
        if (!thumbnail.exists() && imageFileFormat.equals(imageFormat))
        {
            BufferedImage img = ImageIO.read(imageFile);
            BufferedImage imgThumbnail = GraphicsHelper.resampleToSmallest(img, 80, 80);
            ImageIO.write(imgThumbnail, imageFormat, thumbnail);
            return true;
        }
        else
        {
            // thumbnail already exists or image format does not match settings
            return false;
        }
    }

    /**
     * Created a new thumbnail image for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField}.
     * 
     * @param field the field to generate the thumbnail for.
     * @return <code>true</code> if the file is successfully created.
     * @throws IOException
     * @see GraphicsHelper#createFieldThumbnail(String)
     */
    public static boolean createFieldThumbnail(CoalesceFileField field) throws IOException
    {
        return GraphicsHelper.createFieldThumbnail(field.getCoalesceFullFilename());
    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    private static double scaleWidthByHeightResampleRatio(BufferedImage imageToResample, int height, int width)
    {
        if (imageToResample == null) throw new IllegalArgumentException("imageToResample");

        double originalWidth = imageToResample.getWidth();
        double originalHeight = imageToResample.getHeight();

        double newHeightRatio = (double) height / originalHeight;

        return newHeightRatio * originalWidth;

    }

}
