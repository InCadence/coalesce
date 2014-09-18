package Coalesce.Common.Helpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

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

public class DocumentThumbnailHelper {

    // Make class static
    private DocumentThumbnailHelper()
    {

    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    public static BufferedImage getDocumentThumbnailForFile(String fullFilename) throws IOException
    {
        return getDocumentThumbnailForFile(fullFilename, 0, 0);
    }

    public static BufferedImage getDocumentThumbnailForFile(String fullFilename, int originalHeight, int originalWidth)
            throws IOException
    {
        return getDocumentThumbnailForFile(fullFilename, false, originalHeight, originalWidth);
    }

    public static BufferedImage getDocumentThumbnailForFile(String fullFilename,
                                                            boolean encrypted,
                                                            int originalHeight,
                                                            int originalWidth) throws IOException
    {
        String mimeType = MimeHelpers.getMimeTypeForExtension(FileHelper.getExtension(fullFilename));

        BufferedImage thumbnail;

        // Special case for Images, since we have the file we can build the thumbnail here.
        switch (mimeType.toLowerCase()) {

        case "image/bmp":
        case "image/jpeg":
        case "image/pjpeg":
        case "image/x-png":
        case "image/gif":
        case "image/tiff":

            BufferedImage originalImage = ImageIO.read(new File(fullFilename));
            thumbnail = GraphicsHelper.resampleToMaximum(originalImage, 80, 80);

            break;

        default:

            // Get Thumbnail for Mime Type
            thumbnail = getDocumentThumbnailForMimeType(mimeType);

        }

        return thumbnail;

    }

    public static BufferedImage getDocumentThumbnailForFile(byte[] bytes) throws IOException
    {
        return DocumentThumbnailHelper.getDocumentThumbnailForFile(bytes, 0, 0);
    }

    public static BufferedImage getDocumentThumbnailForFile(byte[] bytes, int originalHeight, int originalWidth)
            throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        BufferedImage image = ImageIO.read(bais);

        BufferedImage thumbnail = GraphicsHelper.resampleToMaximum(image, 80, 80);
        if (thumbnail == null) thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Image.png");

        return thumbnail;
    }

    public static BufferedImage getDocumentThumbnailForFileExtensions(String extension) throws IOException
    {
        String mimeType = MimeHelpers.getMimeTypeForExtension(extension);

        return DocumentThumbnailHelper.getDocumentThumbnailForMimeType(mimeType);

    }

    public static BufferedImage getDocumentThumbnailForMimeCategory(String mimeType) throws IOException
    {
        BufferedImage thumbnail;

        if (mimeType.startsWith("audio"))
        {

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Audio.png");

        }
        else if (mimeType.startsWith("video"))
        {

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Video.png");

        }
        else if (mimeType.startsWith("image"))
        {

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Image.png");

        }
        else if (mimeType.startsWith("text"))
        {

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Text.png");

        }
        else
        {

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Blank.png");

        }

        return thumbnail;

    }

    public static BufferedImage getDocumentThumbnailForMimeType(String mimeType) throws IOException
    {
        BufferedImage thumbnail = DocumentThumbnailHelper.getDocumentThumbnailForMimeCategory(mimeType);

        switch (mimeType) {
        case "application/msword":
        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Word.png");
            break;

        case "application/vnd.ms-excel":
        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Excel.png");
            break;

        case "application/vnd.ms-powerpoint":
        case "application/vnd.openxmlformats-officedocument.presentationml.presentation":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_PowerPoint.png");
            break;

        case "application/msaccess":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Access.png");
            break;

        case "application/pdf":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_PDF.png");
            break;

        case "text/plain":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Text.png");
            break;

        case "application/zip":

            thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Zip.png");

        }

        return thumbnail;

    }

    // -----------------------------------------------------------------------//
    // Private Shared Methods
    // -----------------------------------------------------------------------//

    private static BufferedImage getImageForResource(String resource) throws IOException
    {

        Class<?> docClass = new DocumentProperties().getClass();

        return getImage(docClass.getResource("/resources/" + resource));
    }

    private static BufferedImage getImage(URL url) throws IOException
    {
        return ImageIO.read(url);
    }

}
