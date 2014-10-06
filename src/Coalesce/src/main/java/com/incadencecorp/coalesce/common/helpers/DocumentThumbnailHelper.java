package com.incadencecorp.coalesce.common.helpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

/**
 * Provides helper methods for creating thumbnail images for files.
 * 
 * @author InCadence
 *
 */
public class DocumentThumbnailHelper {

    // Make class static
    private DocumentThumbnailHelper()
    {

    }

    public static class DocumentThumbnailResults {

        private int _originalWidth = 0;
        private int _originalHeight = 0;
        private BufferedImage _thumbnail;

        public DocumentThumbnailResults(int originalWidth, int originalHeight, BufferedImage thumbnail)
        {
            _originalWidth = originalWidth;
            _originalHeight = originalHeight;
            _thumbnail = thumbnail;
        }

        public DocumentThumbnailResults(BufferedImage original, BufferedImage thumbnail)
        {
            if (original != null)
            {
                _originalWidth = original.getWidth();
                _originalHeight = original.getHeight();
            }
            
            _thumbnail = thumbnail;

        }
        
        public int getOriginalWidth()
        {
            return _originalWidth;
        }

        public int getOriginalHeight()
        {
            return _originalHeight;
        }

        public BufferedImage getThumbnail()
        {
            return _thumbnail;
        }

    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns a thumbnail for a file with no encryption. If the file is an image file then an attempt is made to resample
     * the file. If for any reason a resampled image cannot be generated then a default thumbnail is returns based on the
     * files MIME type.
     * 
     * @param fullFilename the full path and filename of the file to generate a thumbnail for.
     * @return the thumbnail image for the file.
     * @throws IOException
     */
    public static DocumentThumbnailResults getThumbnailForFile(String fullFilename) throws IOException
    {
        return getThumbnailForFile(fullFilename, false);
    }

    /**
     * Returns a thumbnail for a file. If the file is an image file then an attempt is made to resample the file. If for any
     * reason a resampled image cannot be generated then a default thumbnail is returns based on the files MIME type
     * 
     * @param fullFilename the full path and filename of the file to generate a thumbnail for.
     * @return the {@link DocumentThumbnailResults} from generating the thumbnail.
     * @throws IOException
     */
    public static DocumentThumbnailResults getThumbnailForFile(String fullFilename, boolean encrypted) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(FileHelper.getExtension(fullFilename));

        BufferedImage thumbnail;

        // Special case for Images, since we have the file we can build the thumbnail here.
        switch (mimeType.toLowerCase()) {

        case "image/bmp":
        case "image/jpeg":
        case "image/pjpeg":
        case "image/x-png":
        case "image/gif":
        case "image/tiff":

            byte[] bytes = FileHelper.getFileAsByteArray(fullFilename, encrypted);

            if (bytes.length > 0) {
                return DocumentThumbnailHelper.getThumbnailForFile(bytes);
            }

        default:

            thumbnail = getThumbnailForMimeType(mimeType);

            return new DocumentThumbnailResults(0, 0, thumbnail);
        }

    }

    /**
     * Returns a thumbnail for an array of bytes. The array of bytes are assumed to be an image file and are resampled to
     * generate the thumbnail.
     * 
     * @param bytes the array of bytes representing an image file.
     * @return the {@link DocumentThumbnailResults} from generating the thumbnail.
     * @throws IOException
     */
    public static DocumentThumbnailResults getThumbnailForFile(byte[] bytes) throws IOException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
        {
            BufferedImage image = ImageIO.read(bais);

            BufferedImage thumbnail = null;
            
            if (image != null) thumbnail = GraphicsHelper.resampleToSmallest(image, 80, 80);

            if (thumbnail == null) thumbnail = DocumentThumbnailHelper.getImageForResource("LargeIcon_Image.png");

            return new DocumentThumbnailResults(image, thumbnail);

        }
    }

    /**
     * Returns a thumbnail based on an extension.
     * 
     * @param extension the extension.
     * @return the thumbnail image for the extension.
     * @throws IOException
     */
    public static BufferedImage getThumbnailForFileExtensions(String extension) throws IOException
    {
        String mimeType = MimeHelper.getMimeTypeForExtension(extension);

        return DocumentThumbnailHelper.getThumbnailForMimeType(mimeType);

    }

    /**
     * Returns a thumbnail based on the category of the MIME type provided (e.g., audio/x-aiff, image/gif, text/css, and
     * video/mpeg).
     * 
     * @param mimeType the MIME type.
     * @return the thumbnail image for the MIME type.
     * @throws IOException
     */
    public static BufferedImage getThumbnailForMimeCategory(String mimeType) throws IOException
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

    /**
     * Returns a thumbnail based on the MIME type provided. If a specific thumbnail is not defined for the provided MIME type
     * then the returned thumbnail behavior falls back to {@link DocumentThumbnailHelper#getThumbnailForMimeCategory(String)}
     * .
     * 
     * @param mimeType the MIME type.
     * @return the thumbnail image for the MIME type.
     * @throws IOException
     */
    public static BufferedImage getThumbnailForMimeType(String mimeType) throws IOException
    {
        BufferedImage thumbnail = null;

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

        if (thumbnail == null)
        {
            thumbnail = DocumentThumbnailHelper.getThumbnailForMimeCategory(mimeType);
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
