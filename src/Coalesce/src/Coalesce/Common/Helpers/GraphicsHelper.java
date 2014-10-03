package Coalesce.Common.Helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.DataModel.CoalesceStringField;

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
public class GraphicsHelper {

    // Make class static
    private GraphicsHelper()
    {

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
        // Calculate the Width, maintaining the same aspect ratio as the original image.
        return resample(imageToResample, height, height * imageToResample.getWidth() / imageToResample.getHeight());
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
        // Calculate the Height, maintaining the same aspect ratio as the original image.
        return resample(imageToResample, width * imageToResample.getHeight() / imageToResample.getWidth(), width);
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
    public static BufferedImage resample(BufferedImage imageToResample, int height, int width)
    {
        BufferedImage imgThumbnail = Scalr.resize(imageToResample, width, height, Scalr.OP_ANTIALIAS);

        return imgThumbnail;
    }

    /**
     * Returns a new image resampled from the provided image with a maximum height and width while maintaining the aspect
     * ratio of the original image.
     * 
     * @param imageToResample the image to use for resampling.
     * @param maxHeight the maximum height of the resampled image.
     * @param maxWidth the maximum width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleToMaximum(BufferedImage imageToResample, int maxHeight, int maxWidth)
    {
        double scaledWidth = GraphicsHelper.scaleWidthByHeightResampleRatio(imageToResample, maxHeight, maxWidth);
        if (scaledWidth <= maxWidth)
        {
            return GraphicsHelper.resampleWithHeight(imageToResample, maxHeight);
        }
        else
        {
            return GraphicsHelper.resampleWithWidth(imageToResample, maxWidth);
        }

    }

    /**
     * Returns a new image resampled from the provided image with a minimum height and width while maintaining the aspect
     * ratio of the original image.
     * 
     * @param imageToResample the image to use for resampling.
     * @param minHeight the minimum height of the resampled image.
     * @param minWidth the minimum width of the resampled image.
     * @return the resampled image.
     */
    public static BufferedImage resampleToMinimum(BufferedImage imageToResample, int minHeight, int minWidth)
    {
        double scaledWidth = GraphicsHelper.scaleWidthByHeightResampleRatio(imageToResample, minHeight, minWidth);
        if (scaledWidth >= minWidth)
        {
            return GraphicsHelper.resampleWithHeight(imageToResample, minHeight);
        }
        else
        {
            return GraphicsHelper.resampleWithWidth(imageToResample, minWidth);
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
        String imageName = imageFile.getName();
        String imageFileFormat = imageName.substring(imageName.length() - 3);
        File imageDir = imageFile.getParentFile();

        // create thumbnail name
        String thumbnailName = imageName.substring(0, imageName.length() - 4) + "_thumbnail." + imageFormat;
        File thumbnail = new File(imageDir, thumbnailName);

        // create thumbnail
        if (!thumbnail.exists() && imageFileFormat.equals(imageFormat))
        {
            BufferedImage img = ImageIO.read(imageFile);
            BufferedImage imgThumbnail = GraphicsHelper.resampleToMaximum(img, 80, 80);
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
     * Created a new thumbnail image for an {@link CoalesceStringField}.
     * 
     * @param field the field to generate the thumbnail for.
     * @return <code>true</code> if the file is successfully created.
     * @throws IOException
     * @see GraphicsHelper#createFieldThumbnail(String)
     */
    public static boolean createFieldThumbnail(CoalesceStringField field) throws IOException
    {
        return GraphicsHelper.createFieldThumbnail(field.getCoalesceFullFilename());
    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    private static double scaleWidthByHeightResampleRatio(BufferedImage imageToResample, int height, int width)
    {
        double originalWidth = imageToResample.getWidth();
        double originalHeight = imageToResample.getHeight();

        double newHeightRatio = height / originalHeight;
        double widthScaledForHeight = newHeightRatio * originalWidth;

        return widthScaledForHeight;

    }

}
