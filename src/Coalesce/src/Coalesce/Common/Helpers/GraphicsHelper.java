package Coalesce.Common.Helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.DataModel.XsdField;

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

public class GraphicsHelper {

    // Make class static
    private GraphicsHelper()
    {

    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    public static BufferedImage resampleWithHeight(BufferedImage imageToResample, int height)
    {
        // Calculate the Width, maintaining the same aspect ratio as the original image.
        return resample(imageToResample, height, height * imageToResample.getWidth() / imageToResample.getHeight());
    }

    public static BufferedImage resampleWithWidth(BufferedImage imageToResample, int width)
    {
        // Calculate the Height, maintaining the same aspect ratio as the original image.
        return resample(imageToResample, width * imageToResample.getHeight() / imageToResample.getWidth(), width);
    }

    public static BufferedImage resampleToMaximum(BufferedImage imageToResample, int minHeight, int minWidth)
    {
        int x = imageToResample.getWidth();
        int y = imageToResample.getHeight();

        // Test: If we meet the minHeight, if new width > MinWidth
        int newX = (minHeight / y) * x;

        if (newX <= minWidth)
        {
            // resample to MinHeight
            return GraphicsHelper.resampleWithHeight(imageToResample, minHeight);
        }
        else
        {
            // resample to MinWidth
            return GraphicsHelper.resampleWithWidth(imageToResample, minWidth);
        }

    }

    public static BufferedImage resampleToMinimum(BufferedImage imageToResample, int minHeight, int minWidth)
    {
        int x = imageToResample.getWidth();
        int y = imageToResample.getHeight();

        // Test: If we meet the minHeight, if new width > MinWidth
        int newX = (minHeight / y) * x;

        if (newX >= minWidth)
        {
            // resample to MinHeight
            return GraphicsHelper.resampleWithHeight(imageToResample, minHeight);
        }
        else
        {
            // resample to MinWidth
            return GraphicsHelper.resampleWithWidth(imageToResample, minWidth);
        }

    }

    public static BufferedImage resample(BufferedImage imageToResample, int height, int width)
    {
        BufferedImage imgThumbnail = Scalr.resize(imageToResample, width, height, Scalr.OP_ANTIALIAS);

        return imgThumbnail;
    }

    public static boolean createFieldThumbnail(String Filename) throws IOException
    {
        String imageFormat = CoalesceSettings.getImageFormat();

        File imageFile = new File(Filename);
        String imageName = imageFile.getName();
        String imageFileFormat = imageName.substring(imageName.length() - 3);
        File imageDir = imageFile.getParentFile();

        // create thumbnail name
        String thumbnailName = imageName.substring(0, imageName.length() - 4) + "_" + "thumbnail." + imageFormat;
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

    public static boolean createFieldThumbnail(XsdField Field) throws IOException
    {
        return GraphicsHelper.createFieldThumbnail(Field.getCoalesceFullFilename());
    }

}
