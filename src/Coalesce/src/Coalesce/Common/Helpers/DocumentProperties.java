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
import java.util.HashMap;
import java.util.Map;
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
import Coalesce.Common.Helpers.DocumentThumbnailHelper.DocumentThumbnailResults;
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

/**
 * Provides access to the properties of a file. If the file is an OpenXML document it will provide access to the internal
 * properties of the document.
 * 
 * @author InCadence
 *
 */
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

    /**
     * Initializes a {@link DocumentProperties} from a file without encryption. If the file is an OpenXml file type then the
     * internal Core properties are extracted. If the file is an image then the properties of the image are extracted and a
     * thumbnail version of the file is generated.
     * 
     * @param fullFilename the full path and filename of the file.
     * @return True if successful.
     * @throws ImageProcessingException
     * @throws IOException
     * @throws JDOMException
     * @throws CoalesceCryptoException
     */
    public boolean initialize(String fullFilename) throws ImageProcessingException, IOException, JDOMException,
            CoalesceCryptoException
    {
        return initialize(fullFilename, false);
    }

    /**
     * Initializes a {@link DocumentProperties} from a file without encryption. If the file is an OpenXml file type then the
     * internal Core properties are extracted. If the file is an image then the properties of the image are extracted and a
     * thumbnail version of the file is generated.
     * 
     * @param fullFilename the full path and filename of the file.
     * @param encrypted Whether the file needs to be decrypted or not.
     * @return True if successful.
     * @throws ImageProcessingException
     * @throws IOException
     * @throws JDOMException
     * @throws CoalesceCryptoException
     */
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
            generateThumbnail(fullFilename);
        }

        return true;
    }

    private boolean initializeFileInfo(String fullFilename)
    {
        if (fullFilename == null) throw new NullArgumentException("fullFilename");
        if (StringHelper.isNullOrEmpty(fullFilename)) return false;

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

            setFullFilename(fullFilename);
            setFilename(FilenameUtils.getName(fullFilename));
            setFilenameWithoutExtension(FilenameUtils.getBaseName(fullFilename));
            setExtension(FilenameUtils.getExtension(fullFilename));
            setSize(fi.length());

            String docMimeType = MimeHelper.getMimeTypeForExtension(getExtension());
            if (docMimeType != null) setMimeType(docMimeType);

            String docType = MimeHelper.getFileTypeForMimeType(getMimeType());
            if (docType != null) setDocumentType(docType);

            BasicFileAttributes attr;
            try
            {
                attr = Files.readAttributes(path, BasicFileAttributes.class);

                try
                {
                    FileTime creation = attr.creationTime();
                    String creationStr = creation.toString();
                    if (_created == null) setCreated(new DateTime(creationStr));
                }
                catch (IllegalArgumentException iae)
                {
                    // Something wrong with date format
                }

                try
                {
                    FileTime lastMod = attr.lastModifiedTime();
                    String lastModStr = lastMod.toString();
                    if (_modified == null) setModified(new DateTime(lastModStr));
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
        setLatitude(200.0);
        setLongitude(200.0);

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
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
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

            setCreated(dateTaken);

        }

        generateThumbnail(fullFilename);

        return true;
    }

    private void generateThumbnail(String fullFilename) throws IOException
    {
        DocumentThumbnailResults results = DocumentThumbnailHelper.getThumbnailForFile(fullFilename);

        setThumbnail(results.getThumbnail());
        setImageHeight(results.getOriginalHeight());
        setImageWidth(results.getOriginalWidth());

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
                    Map<String, Document> coreXml = getXspDocuments(decryptBytes, namespaces);

                    setCoreXpsProperties(coreXml.get("core"), namespaces);

                    setAppXpsProperties(coreXml.get("app"), namespaces);

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
                    Map<String, Document> coreXml = getXspDocuments(zipFile, namespaces);

                    setCoreXpsProperties(coreXml.get("core"), namespaces);

                    setAppXpsProperties(coreXml.get("app"), namespaces);

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

    private Map<String, Document> getXspDocuments(byte[] zipBytes, Collection<Namespace> namespaces)
    {

        Map<String, String> partNamePaths = getXmlPartNames(zipBytes, namespaces);

        if (partNamePaths == null) return null;

        Map<String, Document> xmlParts = getXmlDocuments(zipBytes, partNamePaths);

        return xmlParts;
    }

    private Map<String, String> getXmlPartNames(byte[] zipBytes, Collection<Namespace> namespaces)
    {
        try (@SuppressWarnings("resource")
        // This is a known IDE bug in Eclipse (371614). ZipInputStream is Closable and will be automatically closed in the
        // finally
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

                    Map<String, String> partNamePaths = getXmlPartNames(startPartXml, builder, namespaces);

                    // This close should not be required per use of try-resource above but is necessary to avoid IDE warning
                    zipStream.close();
                    return partNamePaths;

                }

            }

            return null;

        }
        catch (IOException | JDOMException e)
        {
            return null;
        }
    }

    private Map<String, Document> getXmlDocuments(byte[] zipBytes, Map<String, String> partNamePaths)
    {
        Map<String, Document> parts = new HashMap<String, Document>();

        for (Map.Entry<String, String> pathEntry : partNamePaths.entrySet())
        {
            try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zipBytes)))
            {

                ZipEntry entry = null;
                while ((entry = zipStream.getNextEntry()) != null)
                {

                    String entryName = entry.getName();

                    if (entryName.equals(pathEntry.getKey()))
                    {
                        SAXBuilder builder = new SAXBuilder();
                        Document partXml = builder.build(zipStream);

                        parts.put(pathEntry.getValue(), partXml);

                        break;
                    }

                }

                // This close should not be required per use of try-resource above but is necessary to avoid IDE warning
                zipStream.close();

            }
            catch (IOException | JDOMException e)
            {
                return null;
            }
        }

        if (parts.size() == partNamePaths.size())
        {
            return parts;
        }
        else
        {
            return null;
        }
    }

    private Map<String, Document> getXspDocuments(ZipFile zipFile, Collection<Namespace> namespaces) throws JDOMException,
            IOException
    {
        ZipEntry startPartEntry = zipFile.getEntry("_rels/.rels");

        SAXBuilder builder = new SAXBuilder();
        Document startPartXml = builder.build(zipFile.getInputStream(startPartEntry));

        Map<String, String> partnamePaths = getXmlPartNames(startPartXml, builder, namespaces);

        Map<String, Document> documents = new HashMap<String, Document>();

        for (Map.Entry<String, String> pathEntry : partnamePaths.entrySet())
        {
            ZipEntry partNameEntry = zipFile.getEntry(pathEntry.getKey());

            Document docXml = builder.build(zipFile.getInputStream(partNameEntry));

            documents.put(pathEntry.getValue(), docXml);

        }

        return documents;

    }

    private Map<String, String> getXmlPartNames(Document startPartXml, SAXBuilder builder, Collection<Namespace> namespaces)
    {

        Map<String, String> partNames = new HashMap<String, String>();

        namespaces.add(Namespace.getNamespace("rel", "http://schemas.openxmlformats.org/package/2006/relationships"));
        namespaces.add(Namespace.getNamespace("cp",
                                              "http://schemas.openxmlformats.org/package/2006/metadata/core-properties"));
        namespaces.add(Namespace.getNamespace("d",
                                              "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"));
        namespaces.add(Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/"));
        namespaces.add(Namespace.getNamespace("dcterms", "http://purl.org/dc/terms/"));

        XPathExpression<Element> xPath = XPathFactory.instance().compile("/rel:Relationships/rel:Relationship[@Type='http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties']",
                                                                         Filters.element(),
                                                                         null,
                                                                         namespaces);

        Element coreElm = xPath.evaluateFirst(startPartXml);
        String partNamePath = coreElm.getAttributeValue("Target");
        partNamePath.replace("$/", "");
        partNames.put(partNamePath, "core");

        xPath = XPathFactory.instance().compile("/rel:Relationships/rel:Relationship[@Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties']",
                                                Filters.element(),
                                                null,
                                                namespaces);

        Element appElm = xPath.evaluateFirst(startPartXml);
        partNamePath = appElm.getAttributeValue("Target");
        partNamePath.replace("$/", "");
        partNames.put(partNamePath, "app");

        return partNames;

    }

    private void setCoreXpsProperties(Document coreXml, Collection<Namespace> namespaces)
    {
        setCategory(getPropertyValueCP("category", coreXml, namespaces));
        setContentStatus(getPropertyValueCP("contentStatus", coreXml, namespaces));
        setContentType(getPropertyValueCP("contentType", coreXml, namespaces));
        setCreated(convertStringToDate(getPropertyValueDCterms("created", coreXml, namespaces)));
        setCreator(getPropertyValueDC("creator", coreXml, namespaces));
        setDescription(getPropertyValueDC("description", coreXml, namespaces));
        setIdentifier(getPropertyValueDC("identifier", coreXml, namespaces));
        setKeywords(getPropertyValueCP("keywords", coreXml, namespaces));
        setLanguage(getPropertyValueDC("language", coreXml, namespaces));
        setLastModifiedBy(getPropertyValueCP("lastModifiedBy", coreXml, namespaces));
        setLastPrinted(convertStringToDate(getPropertyValueCP("lastPrinted", coreXml, namespaces)));
        setModified(convertStringToDate(getPropertyValueDCterms("modified", coreXml, namespaces)));
        setRevision(getPropertyValueCP("revision", coreXml, namespaces));
        setSubject(getPropertyValueDC("subject", coreXml, namespaces));
        setTitle(getPropertyValueDC("title", coreXml, namespaces));
        setVersion(getPropertyValueCP("version", coreXml, namespaces));

    }

    private void setAppXpsProperties(Document appXml, Collection<Namespace> namespaces)
    {
        setPageCount(getIntegerProperty("Pages", appXml, namespaces));
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

    private int getIntegerProperty(String propertyString, Document appXml, Collection<Namespace> namespaces)
    {
        int value = 0;

        XPathExpression<Element> titlePath = XPathFactory.instance().compile("/d:Properties/d:" + propertyString,
                                                                             Filters.element(),
                                                                             null,
                                                                             namespaces);
        Element property = titlePath.evaluateFirst(appXml);
        if (property != null)
        {
            try
            {
                value = Integer.parseInt(property.getText());
            }
            catch (NumberFormatException nfe)
            {
                value = 0;
            }
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

            setThumbnail(GraphicsHelper.resampleToLargest(xpsThumbnail, 80, 80));

            setThumbnailFilename(getFilenameWithoutExtension() + "_thumb.jpg");

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

    /**
     * Returns the category of the file.
     * 
     * @return the category of the file.
     */
    public String getCategory()
    {
        return _category;
    }

    /**
     * Sets the category of the file.
     * 
     * @param value the category of the file.
     */
    public void setCategory(String value)
    {
        _category = value;
    }

    /**
     * Returns the content status of the file.
     * 
     * @return the content status of the file.
     */
    public String getContentStatus()
    {
        return _contentStatus;
    }

    /**
     * Sets the content status of the file.
     * 
     * @param value the content status of the file.
     */
    public void setContentStatus(String value)
    {
        _contentStatus = value;
    }

    /**
     * Returns the content type of the file.
     * 
     * @return the content type of the file.
     */
    public String getContentType()
    {
        return _contentType;
    }

    /**
     * Sets the content type of the file.
     * 
     * @param value the content type of the file.
     */
    public void setContentType(String value)
    {
        _contentType = value;
    }

    /**
     * Returns when the file was created.
     * 
     * @return When the file was created.
     */
    public DateTime getCreated()
    {
        return _created;
    }

    /**
     * Sets when the file was created.
     * 
     * @param value When the file was created.
     */
    public void setCreated(DateTime value)
    {
        _created = value;
    }

    /**
     * Returns the creator of the file.
     * 
     * @return the creator of the file.
     */
    public String getCreator()
    {
        return _creator;
    }

    /**
     * Sets the creator of the file.
     * 
     * @param value the creator of the file.
     */
    public void setCreator(String value)
    {
        _creator = value;
    }

    /**
     * Returns the description of the file.
     * 
     * @return the description of the file.
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the description of the file.
     * 
     * @param value the description of the file.
     */
    public void setDescription(String value)
    {
        _description = value;
    }

    /**
     * Returns the identifier for the file.
     * 
     * @return the identifier for the file.
     */
    public String getIdentifier()
    {
        return _identifier;
    }

    /**
     * Sets the identifier for the file.
     * 
     * @param value the identifier for the file.
     */
    public void setIdentifier(String value)
    {
        _identifier = value;
    }

    /**
     * Returns the keywords associated with the file.
     * 
     * @return the keywords associated with the file.
     */
    public String getKeywords()
    {
        return _keywords;
    }

    /**
     * Sets the keywords associated with the file.
     * 
     * @param value the keywords associated with the file.
     */
    public void setKeywords(String value)
    {
        _keywords = value;
    }

    /**
     * Returns the language of the file.
     * 
     * @return the language of the file.
     */
    public String getLanguage()
    {
        return _language;
    }

    /**
     * Sets the language of the file.
     * 
     * @param value the language of the file.
     */
    public void setLanguage(String value)
    {
        _language = value;
    }

    /**
     * Returns who the file was last modified by.
     * 
     * @return Who the file was last modified by.
     */
    public String getLastModifiedBy()
    {
        return _lastModifiedBy;
    }

    /**
     * Sets who the file was last modified by.
     * 
     * @param value Who the file was last modified by.
     */
    public void setLastModifiedBy(String value)
    {
        _lastModifiedBy = value;
    }

    /**
     * Returns when the file was last printed.
     * 
     * @return When the file was last printed.
     */
    public DateTime getLastPrinted()
    {
        return _lastPrinted;
    }

    /**
     * Sets when the file was last printed.
     * 
     * @param value When the file was last printed.
     */
    public void setLastPrinted(DateTime value)
    {
        _lastPrinted = value;
    }

    /**
     * Returns when the file was last modified.
     * 
     * @return When the file was last modified.
     */
    public DateTime getModified()
    {
        return _modified;
    }

    /**
     * Sets when the file was last modified.
     * 
     * @param value When the file was last modified.
     */
    public void setModified(DateTime value)
    {
        _modified = value;
    }

    /**
     * Returns the revision of the file.
     * 
     * @return the revision of the file.
     */
    public String getRevision()
    {
        return _revision;
    }

    /**
     * Sets the revision of the file.
     * 
     * @param value the revision of the file.
     */
    public void setRevision(String value)
    {
        _revision = value;
    }

    /**
     * Returns the subject of the file.
     * 
     * @return the subject of the file.
     */
    public String getSubject()
    {
        return _subject;
    }

    /**
     * Sets the subject of the file.
     * 
     * @param value the subject of the file.
     */
    public void setSubject(String value)
    {
        _subject = value;
    }

    /**
     * Returns the title of the file.
     * 
     * @return the title of the file.
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Sets the title of the file.
     * 
     * @param value the title of the file.
     */
    public void setTitle(String value)
    {
        _title = value;
    }

    /**
     * Returns the version of the file.
     * 
     * @return the version of the file.
     */
    public String getVersion()
    {
        return _version;
    }

    /**
     * Sets the version of the file.
     * 
     * @param value the version of the file.
     */
    public void setVersion(String value)
    {
        _version = value;
    }

    /**
     * Returns the full filename of the file including path.
     * 
     * @return the full filename of the file including path.
     */
    public String getFullFilename()
    {
        return _fullFilename;
    }

    /**
     * Sets the full filename of the file.
     * 
     * @param value the full filename of the file including path.
     */
    public void setFullFilename(String value)
    {
        _fullFilename = value;
    }

    /**
     * Returns the filename of the file without path.
     * 
     * @return the filename of the file without path.
     */
    public String getFilename()
    {
        return _filename;
    }

    /**
     * Sets the filename of the file.
     * 
     * @param value the filename of the file without path.
     */
    public void setFilename(String value)
    {
        _filename = value;
    }

    /**
     * Returns the extension of the file.
     * 
     * @return the extension of the file.
     */
    public String getExtension()
    {
        return _extension;
    }

    /**
     * Sets the extension of the file.
     * 
     * @param value the extension of the file.
     */
    public void setExtension(String value)
    {
        _extension = value;
    }

    /**
     * Returns the filename of the file without extension or path.
     * 
     * @return the filename of the file without extension or path.
     */
    public String getFilenameWithoutExtension()
    {
        return _filenameWithoutExtension;
    }

    /**
     * Sets the filename of the file without extension or path.
     * 
     * @param value the filename of the file without extension or path.
     */
    public void setFilenameWithoutExtension(String value)
    {
        _filenameWithoutExtension = value;
    }

    /**
     * Returns the mime type of the file.
     * 
     * @return the mime type of the file.
     */
    public String getMimeType()
    {
        return _mimeType;
    }

    /**
     * Sets the mime type of the file.
     * 
     * @param value the mime type of the file.
     */
    public void setMimeType(String value)
    {
        _mimeType = value;
    }

    /**
     * Returns the document type of the file.
     * 
     * @return the document type of the file.
     */
    public String getDocumentType()
    {
        return _documentType;
    }

    /**
     * Sets the document type of the file.
     * 
     * @param value the document type of the file.
     */
    public void setDocumentType(String value)
    {
        _documentType = value;
    }

    /**
     * Returns the size of the file in bytes.
     * 
     * @return the size of the file in bytes.
     */
    public long getSize()
    {
        return _size;
    }

    /**
     * Sets the size of the file in bytes.
     * 
     * @param value the size of the file in bytes.
     */
    public void setSize(long value)
    {
        _size = value;
    }

    /**
     * Returns the number of pages contained in the file.
     * 
     * @return the number of pages contained in the file.
     */
    public int getPageCount()
    {
        return _pageCount;
    }

    /**
     * Sets the number of pages contained in the file.
     * 
     * @param value the number of pages contained in the file.
     */
    public void setPageCount(int value)
    {
        _pageCount = value;
    }

    /**
     * Returns the height of the image file.
     * 
     * @return the height of the image file.
     */
    public int getImageHeight()
    {
        return _imageHeight;
    }

    /**
     * Sets the height of the image file.
     * 
     * @param value the height of the image file.
     */
    public void setImageHeight(int value)
    {
        _imageHeight = value;
    }

    /**
     * Returns the width of the image file.
     * 
     * @return the width of the image file.
     */
    public int getImageWidth()
    {
        return _imageWidth;
    }

    /**
     * Sets the width of the image file.
     * 
     * @param value the width of the image file.
     */
    public void setImageWidth(int value)
    {
        _imageWidth = value;
    }

    /**
     * Returns the latitude associated with the file.
     * 
     * @return the latitude associated with the file.
     */
    public double getLatitude()
    {
        return _latitude;
    }

    /**
     * Sets the latitude associated with the file.
     * 
     * @param value the latitude associated with the file.
     */
    public void setLatitude(double value)
    {
        _latitude = value;
    }

    /**
     * Returns the longitude associated with the file.
     * 
     * @return the longitude associated with the file.
     */
    public double getLongitude()
    {
        return _longitude;
    }

    /**
     * Sets the longitude associated with the file.
     * 
     * @param value the longitude associated with the file.
     */
    public void setLongitude(double value)
    {
        _longitude = value;
    }

    /**
     * Returns the thumbnail for the file.
     * 
     * @return the thumbnail for the file.
     */
    public BufferedImage getThumbnail()
    {
        return _thumbnail;
    }

    /**
     * Sets the thumbnail for the file.
     * 
     * @param value the thumbnail for the file.
     */
    public void setThumbnail(BufferedImage value)
    {
        _thumbnail = value;
    }

    /**
     * Returns the filename of the thumbnail for the file.
     * 
     * @return the filename of the thumbnail for the file.
     */
    public String getThumbnailFilename()
    {
        return _thumbnailFilename;
    }

    /**
     * Sets the filename of the thumbnail for the file.
     * 
     * @param thumbnailFilename the filename of the thumbnail for the file.
     */
    public void setThumbnailFilename(String thumbnailFilename)
    {
        _thumbnailFilename = thumbnailFilename;
    }

}
