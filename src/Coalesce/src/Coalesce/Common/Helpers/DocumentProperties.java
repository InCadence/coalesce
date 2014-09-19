package Coalesce.Common.Helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

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

public class DocumentProperties {

    // Common Core properties
    private String _category = "";
    private String _contentStatus = "";
    private String _contentType = "";
    private DateTime _created;
    private String _description = "";
    private String _identifier = "";
    private String _keywords = "";
    private String _language = "";
    private String _lastModifiedBy = "";
    private DateTime _lastPrinted;
    private DateTime _modified;
    private String _revision = "";
    private String _subject = "";
    private String _title = "";
    private String _version = "";

    // App or Doc Specific Properties
    private int _pageCount = 0;
    private int _imageHeight = 0;
    private int _imageWidth = 0;
    private double _latitude;
    private double _longitude;

    // File Properties
    private String _fullFilename = "";
    private String _filename = "";
    private String _filenameWithoutExtension = "";
    private String _extension = "";
    private String _mimeType = "";
    private String _documentType = "";
    private long _size = 0;
    private BufferedImage _thumbnail = null;
    private String _thumbnailFilename = "";

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    public boolean initialize(String fullFilename) throws ImageProcessingException, IOException
    {
        return initialize(fullFilename, false);
    }

    public boolean initialize(String fullFilename, boolean encrypted) throws ImageProcessingException, IOException
    {

        if (!initializeFileInfo(fullFilename)) return false;

        switch (getExtension().toLowerCase()) {
        case "pptx":
        case "docx":
        case "xlsx":

            // Use XPS Packaging to extract from ExcelX
            initializeOpenXmlProperties(fullFilename, encrypted);

            break;

        case "jpg":

            // Use EXIFLib to extract data about the JPEG image (Latitude, Longitude, etc...)
            initializeJpegProperties(fullFilename, encrypted);

        }

        if (getThumbnail() == null)
        {
            _thumbnail = DocumentThumbnailHelper.getDocumentThumbnailForFile(fullFilename,
                                                                             encrypted,
                                                                             getImageHeight(),
                                                                             getImageWidth());
        }

        return true;
    }

    private boolean initializeFileInfo(String fullFilename)
    {
        Path path = Paths.get(fullFilename);
        if (Files.exists(path))
        {

            File fi = new File(fullFilename);

            _fullFilename = fullFilename;
            _filename = FilenameUtils.getName(fullFilename);
            _filenameWithoutExtension = FilenameUtils.getBaseName(fullFilename);
            _extension = FilenameUtils.getExtension(fullFilename);
            _size = fi.length();

            String docMimeType = MimeHelper.getMimeTypeForExtension(getExtension());
            if (docMimeType != null) _mimeType = docMimeType;

            BasicFileAttributes attr;
            try
            {
                attr = Files.readAttributes(path, BasicFileAttributes.class);

                try
                {
                    FileTime creation = attr.creationTime();
                    String creationStr = creation.toString();
                    if (_created == null) _created = new DateTime(creationStr);
                }
                catch (IllegalArgumentException iae)
                {
                    // Something wrong with date format
                }

                try
                {
                    FileTime lastMod = attr.lastModifiedTime();
                    String lastModStr = lastMod.toString();
                    if (_modified == null) _modified = new DateTime(lastModStr);
                }
                catch (IllegalArgumentException iae)
                {
                    // Something wrong with date format
                }
            }
            catch (IOException e)
            {
                return false;
            }

            return true;
        }

        return false;

    }

    private boolean initializeJpegProperties(String fullFilename, boolean encrypted) throws ImageProcessingException,
            IOException
    {
        // Set the latitude and longitude to out of range values as a flag to the outside code
        // that we either could not find valid coordinates
        _latitude = 200.0;
        _longitude = 200.0;

        Path path = Paths.get(fullFilename);
        if (Files.exists(path))
        {

            File fi = new File(fullFilename);
            Metadata info = ImageMetadataReader.readMetadata(fi);

            setGeoLocationInformation(fullFilename, info);

            return setDateTakenAndThumbnail(fullFilename, info, encrypted);

        }
        else
        {
            return false;
        }

    }

    private void setGeoLocationInformation(String fullFilename, Metadata info) throws ImageProcessingException, IOException
    {
        GpsDirectory gpsDirectory = info.getDirectory(GpsDirectory.class);
        GeoLocation location = gpsDirectory.getGeoLocation();

        if (!location.isZero())
        {
            _latitude = location.getLatitude();
            _longitude = location.getLongitude();
        }

    }

    private boolean setDateTakenAndThumbnail(String fullFilename, Metadata info, boolean encrypted) throws IOException
    {
        // Need to decrypt the file to a byte array to then pass a Stream to an Image object
        byte[] decryptedBytes = FileHelper.getFileAsByteArray(fullFilename, encrypted);
        if (decryptedBytes == null) return false;

        // Set the created date to the date that the picture was taken, not when it was put on a computer.
        // This only applies to jpegs, the property doesn't exist for gifs or bimaps

        Directory directory = info.getDirectory(ExifIFD0Directory.class);
        if (directory != null)
        {
            DateTime dateTaken = new DateTime(directory.getDate(ExifIFD0Directory.TAG_DATETIME));

            _created = dateTaken;

        }

        _thumbnail = DocumentThumbnailHelper.getDocumentThumbnailForFile(fullFilename);

        return true;
    }

    private boolean initializeOpenXmlProperties(String fullFilename, boolean encrypted)
    {
        Path path = Paths.get(fullFilename);
        if (Files.exists(path))
        {

            // TODO: Need to Implement
            
            return true;

        }
        else
        {
            return false;
        }
    }

    // ----------------------------------------------------------------------//
    // Public Properties
    // ----------------------------------------------------------------------//

    public String getCategory()
    {
        return _category;
    }

    public void setCategory(String value)
    {
        _category = value;
    }

    public String getContentStatus()
    {
        return _contentStatus;
    }

    public void setContentStatus(String value)
    {
        _contentStatus = value;
    }

    public String getContentType()
    {
        return _contentType;
    }

    public void setContentType(String value)
    {
        _contentType = value;
    }

    public DateTime getCreated()
    {
        return _created;
    }

    public void setCreated(DateTime value)
    {
        _created = value;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String value)
    {
        _description = value;
    }

    public String getIdentifier()
    {
        return _identifier;
    }

    public void setIdentifier(String value)
    {
        _identifier = value;
    }

    public String getKeywords()
    {
        return _keywords;
    }

    public void setKeywords(String value)
    {
        _keywords = value;
    }

    public String getLanguage()
    {
        return _language;
    }

    public void setLanguage(String value)
    {
        _language = value;
    }

    public String getLastModifiedBy()
    {
        return _lastModifiedBy;
    }

    public void setLastModifiedBy(String value)
    {
        _lastModifiedBy = value;
    }

    public DateTime getLastPrinted()
    {
        return _lastPrinted;
    }

    public void setLastPrinted(DateTime value)
    {
        _lastPrinted = value;
    }

    public DateTime getModified()
    {
        return _modified;
    }

    public void setModified(DateTime value)
    {
        _modified = value;
    }

    public String getRevision()
    {
        return _revision;
    }

    public void setRevision(String value)
    {
        _revision = value;
    }

    public String getSubject()
    {
        return _subject;
    }

    public void setSubject(String value)
    {
        _subject = value;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String value)
    {
        _title = value;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setVersion(String value)
    {
        _version = value;
    }

    public String getFullFilename()
    {
        return _fullFilename;
    }

    public void setFullFilename(String value)
    {
        _fullFilename = value;
    }

    public String getFilename()
    {
        return _filename;
    }

    public void setFilename(String value)
    {
        _filename = value;
    }

    public String getExtension()
    {
        return _extension;
    }

    public void setExtension(String value)
    {
        _extension = value;
    }

    public String getFilenameWithoutExtension()
    {
        return _filenameWithoutExtension;
    }

    public void setFilenameWithoutExtension(String value)
    {
        _filenameWithoutExtension = value;
    }

    public String getMimeType()
    {
        return _mimeType;
    }

    public void setMimeType(String value)
    {
        _mimeType = value;
    }

    public String getDocumentType()
    {
        return _documentType;
    }

    public void setDocumentType(String value)
    {
        _documentType = value;
    }

    public long getSize()
    {
        return _size;
    }

    public void setSize(long value)
    {
        _size = value;
    }

    public int getPageCount()
    {
        return _pageCount;
    }

    public void setPageCount(int value)
    {
        _pageCount = value;
    }

    public int getImageHeight()
    {
        return _imageHeight;
    }

    public void setImageHeight(int value)
    {
        _imageHeight = value;
    }

    public int getImageWidth()
    {
        return _imageWidth;
    }

    public void setImageWidth(int value)
    {
        _imageWidth = value;
    }

    public double getLatitude()
    {
        return _latitude;
    }

    public void setLatitude(double value)
    {
        _latitude = value;
    }

    public double getLongitude()
    {
        return _longitude;
    }

    public void setLongitude(double value)
    {
        _longitude = value;
    }

    public BufferedImage getThumbnail()
    {
        return _thumbnail;
    }

    public void setThumbnail(BufferedImage value)
    {
        _thumbnail = value;
    }

    public String getThumbnailFilename()
    {
        return _thumbnailFilename;
    }

    public void setThumbnailFilename(String thumbnailFilename)
    {
        _thumbnailFilename = thumbnailFilename;
    }

}
