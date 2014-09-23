package Coalesce.Common.Helpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalesceCryptoException;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.Persistance.CoalesceEncrypter;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;

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
    private String _creator;
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

    public boolean initialize(String fullFilename) throws ImageProcessingException, IOException, JDOMException, CoalesceCryptoException
    {
        return initialize(fullFilename, false);
    }

    public boolean initialize(String fullFilename, boolean encrypted) throws ImageProcessingException, IOException,
            JDOMException, CoalesceCryptoException
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
        if (fullFilename == null) throw new NullArgumentException("fullFilename");
        if (StringHelper.IsNullOrEmpty(fullFilename)) return false;

        Path path;
        try
        {
            path = Paths.get(fullFilename);
        }
        catch (InvalidPathException ipe)
        {
            return false;
        }

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

            Metadata info = getMetadataInfo(fullFilename);

            setGeoLocationInformation(fullFilename, info);

            return setDateTakenAndThumbnail(fullFilename, info, encrypted);

        }
        else
        {
            return false;
        }

    }

    private Metadata getMetadataInfo(String fullFilename) throws ImageProcessingException, IOException
    {
        // Must override the default behavior to ensure the Xerces factory is used instead of the Jaxen
        String oldFactory = System.getProperty("javax.xml.parsers.DocumentBuilderFactory");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

        File fi = new File(fullFilename);
        Metadata info = ImageMetadataReader.readMetadata(fi);

        // Clear it to revert back to default behavior
        if (oldFactory == null)
        {
            System.clearProperty("javax.xml.parsers.DocumentBuilderFactory");
        }
        else
        {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", oldFactory);
        }

        return info;

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

    private boolean initializeOpenXmlProperties(String fullFilename, boolean encrypted) throws IOException, JDOMException,
            CoalesceCryptoException
    {
        Path path = Paths.get(fullFilename);
        if (Files.exists(path))
        {

            if (encrypted)
            {
                try
                {
                    CoalesceEncrypter crypto = new CoalesceEncrypter(CoalesceSettings.getPassPhrase());

                    byte[] decryptBytes = crypto.decryptValueToBytes(Files.readAllBytes(Paths.get(fullFilename)));

                    Collection<Namespace> namespaces = new ArrayList<Namespace>();
                    Document coreXml = getXspCorePropertiesDocument(decryptBytes, namespaces);

                    setCoreXpsProperties(coreXml, namespaces);

                }
                catch (InvalidKeyException | NoSuchAlgorithmException e)
                {
                    return false;
                }

            }
            else
            {

                try (ZipFile zipFile = new ZipFile(fullFilename))
                {

                    Collection<Namespace> namespaces = new ArrayList<Namespace>();
                    Document coreXml = getXspCorePropertiesDocument(zipFile, namespaces);

                    setCoreXpsProperties(coreXml, namespaces);

                    // TODO: Current logic not converting colors correctly
                    // getEmbeddedThumbnail(zipFile);

                }

            }

            return true;

        }
        else
        {
            return false;
        }
    }

    private Document getXspCorePropertiesDocument(byte[] zipBytes, Collection<Namespace> namespaces)
    {

        String partNamePath = getCoreXmlPartName(zipBytes, namespaces);

        if (partNamePath == null) return null;

        Document coreXml = getCoreXml(zipBytes, partNamePath);
        
        return coreXml;
    }

    private String getCoreXmlPartName(byte[] zipBytes, Collection<Namespace> namespaces)
    {
        try (@SuppressWarnings("resource") // This is a known IDE bug in Eclipse (371614).  ZipInputStream is Closable and will be automatically closed in the finally
        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zipBytes)))
        {

            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null)
            {

                String entryName = entry.getName();

                if (entryName.equals("_rels/.rels"))
                {
                    SAXBuilder builder = new SAXBuilder();
                    Document startPartXml = builder.build(zipStream);

                    String partNamePath = getCoreXmlPartName(startPartXml, builder, namespaces);

                    // This close should not be required per use of try-resource above but is necessary to avoid IDE warning
                    zipStream.close();
                    return partNamePath;

                }

            }

            return null;

        }
        catch (IOException | JDOMException e)
        {
            return null;
        }
    }

    private Document getCoreXml(byte[] zipBytes, String partNamePath)
    {
        try (@SuppressWarnings("resource") // This is a known IDE bug in Eclipse (371614).  ZipInputStream is Closable and will be automatically closed in the finally
        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zipBytes)))
        {

            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null)
            {

                String entryName = entry.getName();

                if (entryName.equals(partNamePath))
                {
                    SAXBuilder builder = new SAXBuilder();
                    Document corePartXml = builder.build(zipStream);

                    // This close should not be required per use of try-resource above but is necessary to avoid IDE warning
                    zipStream.close();
                    return corePartXml;

                }

            }

            return null;

        }
        catch (IOException | JDOMException e)
        {
            return null;
        }
    }

    private Document getXspCorePropertiesDocument(ZipFile zipFile, Collection<Namespace> namespaces) throws JDOMException,
            IOException
    {
        ZipEntry startPartEntry = zipFile.getEntry("_rels/.rels");

        SAXBuilder builder = new SAXBuilder();
        Document startPartXml = builder.build(zipFile.getInputStream(startPartEntry));

        String partnamePath = getCoreXmlPartName(startPartXml, builder, namespaces);

        ZipEntry partNameEntry = zipFile.getEntry(partnamePath);

        Document coreXml = builder.build(zipFile.getInputStream(partNameEntry));

        return coreXml;

    }

    private String getCoreXmlPartName(Document startPartXml, SAXBuilder builder, Collection<Namespace> namespaces)
    {

        namespaces.add(Namespace.getNamespace("rel", "http://schemas.openxmlformats.org/package/2006/relationships"));
        namespaces.add(Namespace.getNamespace("cp",
                                              "http://schemas.openxmlformats.org/package/2006/metadata/core-properties"));
        namespaces.add(Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/"));
        namespaces.add(Namespace.getNamespace("dcterms", "http://purl.org/dc/terms/"));

        XPathExpression<Element> xPath = XPathFactory.instance().compile("/rel:Relationships/rel:Relationship[@Type='http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties']",
                                                                         Filters.element(),
                                                                         null,
                                                                         namespaces);

        Element coreElm = xPath.evaluateFirst(startPartXml);
        String partNamePath = coreElm.getAttributeValue("Target");

        return partNamePath.replace("$/", "");

    }

    private void setCoreXpsProperties(Document coreXml, Collection<Namespace> namespaces)
    {
        _category = getPropertyValueCP("category", coreXml, namespaces);
        _contentStatus = getPropertyValueCP("contentStatus", coreXml, namespaces);
        _contentType = getPropertyValueCP("contentType", coreXml, namespaces);
        _created = convertStringToDate(getPropertyValueDCterms("created", coreXml, namespaces));
        _creator = getPropertyValueDC("creator", coreXml, namespaces);
        _description = getPropertyValueDC("description", coreXml, namespaces);
        _identifier = getPropertyValueDC("identifier", coreXml, namespaces);
        _keywords = getPropertyValueCP("keywords", coreXml, namespaces);
        _language = getPropertyValueDC("language", coreXml, namespaces);
        _lastModifiedBy = getPropertyValueCP("lastModifiedBy", coreXml, namespaces);
        _lastPrinted = convertStringToDate(getPropertyValueCP("lastPrinted", coreXml, namespaces));
        _modified = convertStringToDate(getPropertyValueDCterms("modified", coreXml, namespaces));
        _revision = getPropertyValueCP("revision", coreXml, namespaces);
        _subject = getPropertyValueDC("subject", coreXml, namespaces);
        _title = getPropertyValueDC("title", coreXml, namespaces);
        _version = getPropertyValueCP("version", coreXml, namespaces);

    }

    private String getPropertyValueDC(String propertyString, Document coreXml, Collection<Namespace> namespaces)
    {
        String value = "";

        XPathExpression<Element> titlePath = XPathFactory.instance().compile("/cp:coreProperties/dc:" + propertyString,
                                                                             Filters.element(),
                                                                             null,
                                                                             namespaces);
        Element property = titlePath.evaluateFirst(coreXml);
        if (property != null)
        {
            value = property.getText();

        }

        return value;
    }

    private String getPropertyValueCP(String propertyString, Document coreXml, Collection<Namespace> namespaces)
    {
        String value = "";

        XPathExpression<Element> titlePath = XPathFactory.instance().compile("/cp:coreProperties/cp:" + propertyString,
                                                                             Filters.element(),
                                                                             null,
                                                                             namespaces);
        Element property = titlePath.evaluateFirst(coreXml);
        if (property != null)
        {
            value = property.getText();

        }

        return value;
    }

    private String getPropertyValueDCterms(String propertyString, Document coreXml, Collection<Namespace> namespaces)
    {
        String value = "";

        XPathExpression<Element> titlePath = XPathFactory.instance().compile("/cp:coreProperties/dcterms:" + propertyString,
                                                                             Filters.element(),
                                                                             null,
                                                                             namespaces);
        Element property = titlePath.evaluateFirst(coreXml);
        if (property != null)
        {
            value = property.getText();

        }

        return value;
    }

    private DateTime convertStringToDate(String date)
    {
        try
        {
            return new DateTime(date);
        }
        catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

    // TODO: This code does convert the wmf image embedded in the Open XML
    // document but the colors are being inverted at the moment for some reason.
    @SuppressWarnings("unused")
    private void getEmbeddedThumbnail(ZipFile zipFile)
    {
        try
        {
            ZipEntry startPartEntry = zipFile.getEntry("_rels/.rels");

            SAXBuilder builder = new SAXBuilder();
            Document startPartXml;
            startPartXml = builder.build(zipFile.getInputStream(startPartEntry));
            Collection<Namespace> namespaces = new ArrayList<Namespace>();
            namespaces.add(Namespace.getNamespace("rel", "http://schemas.openxmlformats.org/package/2006/relationships"));

            XPathExpression<Element> xPath = XPathFactory.instance().compile("/rel:Relationships/rel:Relationship[@Type='http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail']",
                                                                             Filters.element(),
                                                                             null,
                                                                             namespaces);

            Element coreElm = xPath.evaluateFirst(startPartXml);
            String partnamePath = coreElm.getAttributeValue("Target");
            ZipEntry partNameEntry = zipFile.getEntry(partnamePath.replace("$/", ""));

            /*
             * String fileName = "<storage location>" + partNameEntry.getName().split("/")[1]; try(FileOutputStream fos = new
             * FileOutputStream(fileName)) { IOUtils.copy(zipFile.getInputStream(partNameEntry), fos); fos.flush(); }
             */

            InputStream inputStream = zipFile.getInputStream(partNameEntry);
            BufferedImage xpsThumbnail = convertToJpg(inputStream, partNameEntry.getName().split("/")[1]);
            if (xpsThumbnail == null) return;

            _thumbnail = GraphicsHelper.resample(xpsThumbnail, 80, 80);

            _thumbnailFilename = getFilenameWithoutExtension() + "_thumb.jpg";

        }
        catch (JDOMException | IOException e)
        {
            // Failed to load thumbnail
        }
    }

    private BufferedImage convertToJpg(InputStream wmfStream, String filename)
    {
        try
        {

            ByteArrayOutputStream imageOut = new ByteArrayOutputStream();

            IOUtils.copy(wmfStream, imageOut);

            WMFTranscoder transcoder = new WMFTranscoder();
            TranscodingHints hints = new TranscodingHints();
            // hints.put(WMFTranscoder.KEY_HEIGHT, 2000f);
            // hints.put(WMFTranscoder.KEY_WIDTH, 2000f);
            transcoder.setTranscodingHints(hints);
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(imageOut.toByteArray()));
            ByteArrayOutputStream svg = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(svg);

            transcoder.transcode(input, output);
            /*
             * String svgFile = "<storage location>" + StringUtils.replace(filename, "wmf", "svg");
             * 
             * try (FileOutputStream fileOut = new FileOutputStream(svgFile)) { fileOut.write(svg.toByteArray());
             * fileOut.flush(); }
             */

            // svg -> jpg
            BufferedImage image = rasterize(svg);
            // ImageIO.write(image, "jpg", new File(StringUtils.replace(svgFile, "svg", "jpg")));

            return image;

        }
        catch (IOException | TranscoderException ex)
        {
            // Failed to convert
            return null;
        }
    }

    private BufferedImage rasterize(ByteArrayOutputStream svgStream) throws IOException
    {
        final BufferedImage[] imagePointer = new BufferedImage[1];

        // Rendering hints can't be set programatically, so
        // we override defaults with a temporary stylesheet.
        // These defaults emphasize quality and precision, and
        // are more similar to the defaults of other SVG viewers.
        // SVG documents can still override these defaults.
        String css = "svg {" + "shape-rendering: geometricPrecision;" + "text-rendering:  geometricPrecision;"
                + "color-rendering: optimizeQuality;" + "image-rendering: optimizeQuality;" + "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());

        try
        {

            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgStream.toByteArray()));

            ImageTranscoder t = new ImageTranscoder() {

                @Override
                public BufferedImage createImage(int w, int h)
                {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out) throws TranscoderException
                {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        }
        catch (TranscoderException ex)
        {
            throw new IOException("Couldn't convert image");
        }
        finally
        {
            cssFile.delete();
        }

        return imagePointer[0];

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

    public String getCreator()
    {
        return _creator;
    }

    public void setCreator(String value)
    {
        _creator = value;
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
