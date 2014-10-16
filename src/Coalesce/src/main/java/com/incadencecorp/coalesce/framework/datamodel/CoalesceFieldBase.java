package com.incadencecorp.coalesce.framework.datamodel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.xerces.impl.dv.util.Base64;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

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

public abstract class CoalesceFieldBase<T> extends CoalesceDataObject implements ICoalesceField<T> {

    /*--------------------------------------------------------------------------
    Public Abstract Functions
    --------------------------------------------------------------------------*/

    protected abstract String getBaseValue();

    protected abstract void setBaseValue(String value);

    protected abstract String getEntityInputLang();
    
    protected abstract void setEntityInputLang(String value);
    
    @Override
    public abstract ECoalesceFieldDataTypes getDataType();

    @Override
    public abstract String getLabel();

    @Override
    public abstract void setLabel(String value);

    @Override
    public abstract int getSize();

    @Override
    public abstract void setSize(int value);

    @Override
    public abstract String getModifiedBy();

    @Override
    public abstract void setModifiedBy(String value);

    @Override
    public abstract String getModifiedByIP();

    @Override
    public abstract void setModifiedByIP(String value);

    /**
     * Returns the string representation of the classification marking.
     * 
     * @return String, the classification marking.
     */
    public abstract String getClassificationMarkingAsString();

    /**
     * Sets the classification marking to the value of the string parameter.
     * 
     * @param value, String, the new classification marking.
     */
    public abstract void setClassificationMarking(String value);

    @Override
    public abstract String getPreviousHistoryKey();

    @Override
    public abstract void setPreviousHistoryKey(String value);

    @Override
    public abstract String getFilename();

    @Override
    public abstract void setFilename(String value);

    @Override
    public abstract String getExtension();

    @Override
    public abstract void setExtension(String value);

    @Override
    public abstract String getMimeType();

    @Override
    public abstract void setMimeType(String value);

    @Override
    public abstract String getHash();

    @Override
    public abstract void setHash(String value);

    @Override
    public Locale getInputLang()
    {
        String inputLang = getEntityInputLang();

        if (inputLang == null) return null;

        return LocaleUtils.toLocale(inputLang.replace("-", "_"));
    }

    @Override
    public void setInputLang(Locale value)
    {
        setEntityInputLang(value.toString());
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public String getCoalesceFilename()
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE) throw new ClassCastException("Type mismatch");

        return GUIDHelper.removeBrackets(getKey()) + "." + getExtension();
    }

    public String getCoalesceFullFilename()
    {
        return getCoalesceFullFilename(getCoalesceFilename());
    }

    public String getCoalesceFilenameWithLastModifiedTag()
    {
        return getCoalesceFilenameWithLastModifiedTag(getCoalesceFullFilename());
    }

    public String getCoalesceThumbnailFilename()
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE) throw new ClassCastException("Type mismatch");

        return GUIDHelper.removeBrackets(getKey()) + "_thumb.jpg";
    }

    public String getCoalesceThumbnailFullFilename()
    {
        return getCoalesceFullFilename(getCoalesceThumbnailFilename());
    }

    public String getCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        return getCoalesceFilenameWithLastModifiedTag(getCoalesceThumbnailFullFilename());
    }

    /**
     * Returns the field value with the classification marking.
     * 
     * @return String, Marking + " " + value.
     */
    public String getValueWithMarking()
    {
        String val = getBaseValue();
        Marking mrk = new Marking(getClassificationMarkingAsString());
        return mrk.toString() + " " + val;
    }

    @Override
    public String toString()
    {
        return getValueWithMarking();
    }

    @Override
    public void setClassificationMarking(Marking value)
    {
        setClassificationMarking(value.toString());
    }

    @Override
    public Marking getClassificationMarking()
    {
        return new Marking(getClassificationMarkingAsString());
    };

    /**
     * Returns the portion marking representation of the full classification marking.
     * 
     * @return String, the portion marking.
     */
    public String getPortionMarking()
    {
        Marking mrk = new Marking(getClassificationMarkingAsString());
        return mrk.toPortionString();
    }

    /**
     * Sets the key value for the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}.
     * 
     * @param fieldHistory, provides the value of the newer field's Previous History Key.
     */
    public void setPreviousHistoryKey(CoalesceFieldHistory fieldHistory)
    {
        if (fieldHistory == null) throw new NullArgumentException("fieldHistory");

        setPreviousHistoryKey(fieldHistory.getKey());
    }

    /**
     * Sets the Field's value by the String parameter.
     * 
     * @param value, field's value as a String.
     */
    protected void setTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = getDataType();
        if (fieldType != ECoalesceFieldDataTypes.STRING_TYPE && fieldType != ECoalesceFieldDataTypes.URI_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(value);
    }

    /**
     * Sets the Field's value by the UUID parameter.
     * 
     * @param value, field's value as a UUID.
     */
    protected void setTypedValue(UUID value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GUID_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(GUIDHelper.getGuidString(value));
    }

    /**
     * Sets the Field's value by the DateTime parameter.
     * 
     * @param value, field's value as a DateTime.
     */
    protected void setTypedValue(DateTime value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DATE_TIME_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(JodaDateTimeHelper.toXmlDateTimeUTC(value));
    }

    /**
     * Sets the Field's value by the boolean parameter.
     * 
     * @param value, field's value as a boolean.
     */
    protected void setTypedValue(boolean value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(String.valueOf(value));
    }

    /**
     * Sets the Field's value by the int parameter.
     * 
     * @param value, field's value as an integer.
     */
    protected void setTypedValue(int value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(String.valueOf(value));
    }

    /**
     * Sets the Field's value by the int parameter
     * 
     * @param value, field's value as an integer
     */
    protected void setTypedValue(double value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DOUBLE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(Double.toString(value));
    }

    /**
     * Sets the Field's value by the int parameter
     * 
     * @param value, field's value as an integer
     */
    protected void setTypedValue(float value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FLOAT_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(Float.toString(value));
    }

    /**
     * Sets the Field's value by the geometry Point parameter.
     * 
     * @param value, field's value as a geometry point.
     * @throws CoalesceDataFormatException.
     */
    protected void setTypedValue(Point value) throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (!Double.isNaN(value.getX()) && !Double.isNaN(value.getY()))
        {
            assertValid(value);

            setBaseValue(value.toText());
        }
    }

    /**
     * Sets the Field's value by the geometry Coordinate parameter.
     * 
     * @param value, field's value as a geometry coordinate.
     * @throws CoalesceDataFormatException.
     */
    protected void setTypedValue(Coordinate value) throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (!Double.isNaN(value.x) && !Double.isNaN(value.y))
        {
            assertValid(value);

            setBaseValue(WKTWriter.toPoint(value));
        }
    }

    /**
     * Sets the Field's value by the geometry MultiPoint parameter.
     * 
     * @param multiPoint, field's value as a geometry multipoint.
     * @throws CoalesceDataFormatException.
     */
    protected void setTypedValue(MultiPoint multiPoint) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        assertValid(multiPoint);

        setBaseValue(multiPoint.toText());
    }

    /**
     * Sets the Field's value by the geometry coordinate array parameter.
     * 
     * @param value, field's value as a geometry coordinate array.
     * @throws CoalesceDataFormatException.
     */
    protected void setTypedValue(Coordinate[] value) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Initialize Array
        Point[] points = new Point[value.length];

        // Couple Values
        for (int ii = 0; ii < value.length; ii++)
        {
            points[ii] = new Point(new CoordinateArraySequence(new Coordinate[] { new Coordinate(value[ii].x, value[ii].y) }),
                                   new GeometryFactory());
        }

        setTypedValue(new MultiPoint(points, new GeometryFactory()));
    }

    /**
     * Sets the Field's value by the byte array parameter.
     * 
     * @param dataBytes, field's value as a byte array.
     */
    protected void setTypedValue(byte[] dataBytes)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(Base64.encode(dataBytes));
        setSize(dataBytes.length);
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and mimetype.
     * 
     * @param dataBytes, field's value as a byte array.
     * @param filename, field's filename.
     * @param extension, field's extension.
     */
    protected void setTypedValue(byte[] dataBytes, String filename, String extension)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (dataBytes == null) throw new NullArgumentException("dataBytes");

        setBaseValue(Base64.encode(dataBytes));
        setFilename(filename);
        setExtension(extension);
        setMimeType(MimeHelper.getMimeTypeForExtension(extension));
        setSize(dataBytes.length);
    }

    /**
     * Sets the Field's hash value. Also sets the filename, extension and MIME type.
     * 
     * @param filename, field's filename.
     * @param extension, field's extension.
     * @param hash, field's hash value.
     */
    protected void setTypedValue(String filename, String extension, String hash)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setFilename(filename);
        setExtension(extension);
        setMimeType(MimeHelper.getMimeTypeForExtension(extension));
        setHash(hash);
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and mimetype by the Document
     * Properties.
     * 
     * @param dataBytes, file's value as a byte array.
     * @param docProps, file's DocumentProperties.
     */
    protected void setTypedValue(byte[] dataBytes, DocumentProperties docProps)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setTypedValue(dataBytes, docProps.getFilename(), docProps.getExtension());
    }

    /**
     * Sets value, filename, extension, mimetype if the datatype equals the file type and the file exists.
     * 
     * @param docProps, file's DocumentProperties.
     * @throws IOException.
     */
    protected void setTypedValue(DocumentProperties docProps) throws IOException
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Does File Exist?
        Path path = Paths.get(docProps.getFullFilename());
        if (Files.exists(path))
        {
            setTypedValue(Files.readAllBytes(path), docProps);
        }
    }

    /**
     * Returns the UUID value of the field.
     * 
     * @return UUID, field's value as a UUID. Null if not a UUID.
     * @throws ClassCastException.
     */
    protected UUID getGuidValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GUID_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = getBaseValue();

        if (GUIDHelper.isValid(value))
        {
            return GUIDHelper.getGuid(value);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the Field's DateTime value.
     * 
     * @return DateTime, field's value as a DateTime. Null if not populated or invalid.
     * @throws ClassCastException.
     */
    protected DateTime getDateTimeValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.DATE_TIME_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.fromXmlDateTimeUTC(getBaseValue());

        if (value == null) return null;

        return value;

    }

    /**
     * Returns the Field's boolean value.
     * 
     * @return boolean, field's value as a boolean.
     * @throws ClassCastException.
     */
    protected Boolean getBooleanValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.isNullOrEmpty(getBaseValue())) return false;

        return Boolean.parseBoolean(getBaseValue());
    }

    /**
     * Returns the Field's Integer value.
     * 
     * @return integer, field's value as an int.
     * @throws CoalesceDataFormatException.
     */
    protected Integer getIntegerValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            int value = Integer.parseInt(getBaseValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    /**
     * Returns the Field's Double value.
     * 
     * @return integer, field's value as an Double
     * @throws CoalesceDataFormatException
     */
    protected Double getDoubleValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.DOUBLE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            Double value = Double.parseDouble(getBaseValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    /**
     * Returns the Field's Float value.
     * 
     * @return integer, field's value as an Float
     * @throws CoalesceDataFormatException
     */
    protected Float getFloatValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.FLOAT_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            Float value = Float.parseFloat(getBaseValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    /**
     * Returns the Field's geometry Point value.
     * 
     * @return Point, field's value as a geometry point.
     * @throws CoalesceDataFormatException.
     */
    protected Point getPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getBaseValue())) return null;

            WKTReader reader = new WKTReader();
            Point point = (Point) reader.read(getBaseValue());

            assertValid(point);

            return point;

        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }
    }

    /**
     * Returns the Field's geometry Coordinate value.
     * 
     * @return Coordinate, field's value as a geometry Coordinate. Null if not populated.
     * @throws CoalesceDataFormatException.
     */
    protected Coordinate getCoordinateValue() throws CoalesceDataFormatException
    {
        Point point = getPointValue();

        if (point == null) return null;

        assertValid(point);

        return point.getCoordinate();
    }

    /**
     * Returns the Field's geometry MultiPoint value.
     * 
     * @return MultiPoint, field's value as a geometry MultiPoint.
     * @throws CoalesceDataFormatException.
     */
    protected MultiPoint getMultiPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            validateMultiPointFormat(getBaseValue());

            WKTReader reader = new WKTReader();
            MultiPoint multiPoint = (MultiPoint) reader.read(getBaseValue());

            assertValid(multiPoint);

            return multiPoint;

        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse coordinates value for: " + getName());
        }
    }

    private void validateMultiPointFormat(String value) throws CoalesceDataFormatException
    {
        if (value.endsWith("MULTIPOINT EMPTY")) return;

        if (value.startsWith("MULTIPOINT (") || value.startsWith("MULTIPOINT("))
        {
            String trimmed = value.replace("MULTIPOINT (", "").replace("MULTIPOINT(", "");

            if (trimmed.endsWith(")"))
            {
                trimmed = trimmed.replaceAll("\\)$", "");

                String[] points = trimmed.split(", ");
                for (String point : points)
                {
                    if (point.startsWith("(") && point.endsWith(")"))
                    {
                        String trimmedPoint = point.replace("(", "").replace(")", "");

                        String[] values = trimmedPoint.split(" ");

                        if (values.length == 2)
                        {
                            try
                            {
                                @SuppressWarnings("unused")
                                double longitude = Double.parseDouble(values[0]);
                                @SuppressWarnings("unused")
                                double latitude = Double.parseDouble(values[1]);

                                continue;

                            }
                            catch (NumberFormatException nfe)
                            {
                            }
                        }
                    }

                    throw new CoalesceDataFormatException("Failed to parse coordinates value for: " + getName());

                }

                return;

            }
        }

        throw new CoalesceDataFormatException("Failed to parse coordinates value for: " + getName());

    }

    /**
     * Returns the Field's Coordinate array values.
     * 
     * @return Coordinate[], field's value as a geometry Coordinate array.
     * 
     * @throws CoalesceDataFormatException.
     */
    protected Coordinate[] getCoordinateListValue() throws CoalesceDataFormatException
    {
        return getMultiPointValue().getCoordinates();
    }

    /**
     * Returns the binary value of Field's associated file.
     * 
     * @return byte[], field's value as a byte array.
     * 
     * @throws ClassCastException.
     */
    protected byte[] getBinaryValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String rawValue = getBaseValue();

        if (rawValue == null) return null;

        if (rawValue != null && rawValue.length() > 0)
        {
            byte[] bytes = Base64.decode(rawValue);

            return bytes;

        }
        else
        {
            return new byte[0];
        }
    }

    /**
     * Returns the document properties of a File type.
     * 
     * @return DocumentProperties The properties of the file stored within the Coalesce Entity.
     * 
     * @throws ClassCastException.
     */
    protected DocumentProperties getFileValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            // Initialize Properties from Filename
            DocumentProperties properties = new DocumentProperties();
            properties.initialize(getFilename());

            return properties;
        }
        catch (ImageProcessingException | CoalesceCryptoException | IOException | JDOMException e)
        {
            throw new CoalesceDataFormatException("Invalid File");
        }
    }

    /*--------------------------------------------------------------------------
    Private Functions
    --------------------------------------------------------------------------*/

    private String getCoalesceFullFilename(String fileName)
    {
        // Add Sub Directory?
        if (CoalesceSettings.getSubDirectoryLength() > 0 && CoalesceSettings.getSubDirectoryLength() < fileName.length())
        {
            fileName = CoalesceSettings.getBinaryFileStoreBasePath() + File.separator
                    + fileName.substring(0, CoalesceSettings.getSubDirectoryLength()) + File.separator + fileName;
        }
        else
        {
            fileName = CoalesceSettings.getBinaryFileStoreBasePath() + File.separator + fileName;
        }

        return fileName;
    }

    private String getCoalesceFilenameWithLastModifiedTag(String fileName)
    {
        try
        {
            File file = new File(getCoalesceFullFilename());

            return fileName + "?" + file.lastModified();
        }
        catch (Exception ex)
        {
            // Return Now
            return fileName + "?" + JodaDateTimeHelper.nowInUtc().getMillis();
        }
    }

    private void assertValid(Coordinate location) throws CoalesceDataFormatException
    {
        if (location == null || Math.abs(location.x) > 180 || Math.abs(location.y) > 90)
        {
            throw new CoalesceDataFormatException("Failed to parse coordinate value for: " + getName());
        }
    }

    private void assertValid(Point location) throws CoalesceDataFormatException
    {
        if (location == null)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }

        assertValid(location.getCoordinate());
    }

    private void assertValid(MultiPoint multiPoint) throws CoalesceDataFormatException
    {
        for (Coordinate location : multiPoint.getCoordinates())
        {
            assertValid(location);
        }
    }
}
