package Coalesce.Framework.DataModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.xerces.impl.dv.util.Base64;
import org.joda.time.DateTime;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Exceptions.CoalesceDataFormatException;
import Coalesce.Common.Helpers.DocumentProperties;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;

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
     * Returns the string representation of the classification marking
     * 
     * @return String, the classification marking
     */
    public abstract String getClassificationMarkingAsString();

    /**
     * Sets the classification marking to the value of the string parameter
     * 
     * @param value, String, the new classification marking
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
        String inputLang = getAttribute("inputlang");

        if (inputLang == null) return null;

        return LocaleUtils.toLocale(inputLang.replace("-", "_"));
    }

    @Override
    public void setInputLang(Locale value)
    {
        setAttribute("inputlang", value.toString());
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns the field value with the classification marking.
     * 
     * @return String, Marking + " " + value
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
     * Returns the portion marking representation of the full classification marking
     * 
     * @return String, the portion marking
     */
    public String getPortionMarking()
    {
        Marking mrk = new Marking(getClassificationMarkingAsString());
        return mrk.toPortionString();
    }

    /**
     * Sets the key value for the XsdFieldHistory
     * 
     * @param fieldHistory, provides the value of the newer field's Previous History Key
     */
    public void setPreviousHistoryKey(CoalesceFieldHistory fieldHistory)
    {
        if (fieldHistory == null) throw new NullArgumentException("fieldHistory");

        setPreviousHistoryKey(fieldHistory.getKey());
    }

    /**
     * Sets the Field's value by the String parameter
     * 
     * @param value, field's value as a String
     */
    protected void setTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = getDataType();
        if (fieldType != ECoalesceFieldDataTypes.StringType && fieldType != ECoalesceFieldDataTypes.UriType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(value);
    }

    /**
     * Sets the Field's value by the UUID parameter
     * 
     * @param value, field's value as a UUID
     */
    protected void setTypedValue(UUID value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(GUIDHelper.getGuidString(value));
    }

    /**
     * Sets the Field's value by the DateTime parameter
     * 
     * @param value, field's value as a DateTime
     */
    protected void setTypedValue(DateTime value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(JodaDateTimeHelper.toXmlDateTimeUTC(value));
    }

    /**
     * Sets the Field's value by the boolean parameter
     * 
     * @param value, field's value as a boolean
     */
    protected void setTypedValue(boolean value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
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
    protected void setTypedValue(int value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(String.valueOf(value));
    }

    /**
     * Sets the Field's value by the geometry Point parameter
     * 
     * @param value, field's value as a geometry point
     * @throws CoalesceDataFormatException
     */
    protected void setTypedValue(Point value) throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
        {
            throw new ClassCastException("Type mismatch");
        }

        assertValid(value);

        setBaseValue(value.toText());
    }

    /**
     * Sets the Field's value by the geometry Coordinate parameter
     * 
     * @param value, field's value as a geometry coordinate
     * @throws CoalesceDataFormatException
     */
    protected void setTypedValue(Coordinate value) throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
        {
            throw new ClassCastException("Type mismatch");
        }

        assertValid(value);

        setBaseValue(WKTWriter.toPoint(value));
    }

    /**
     * Sets the Field's value by the geometry MultiPoint parameter
     * 
     * @param multiPoint, field's value as a geometry multipoint
     * @throws CoalesceDataFormatException
     */
    protected void setTypedValue(MultiPoint multiPoint) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateListType)
        {
            throw new ClassCastException("Type mismatch");
        }

        assertValid(multiPoint);

        setBaseValue(multiPoint.toText());
    }

    /**
     * Sets the Field's value by the geometry coordinate array parameter
     * 
     * @param value, field's value as a geometry coordinate array
     * @throws CoalesceDataFormatException
     */
    protected void setTypedValue(Coordinate value[]) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateListType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Initialize Array
        Point points[] = new Point[value.length];

        // Couple Values
        for (int ii = 0; ii < value.length; ii++)
        {
            points[ii] = new Point(new CoordinateArraySequence(new Coordinate[] { new Coordinate(value[ii].x, value[ii].y) }),
                                   new GeometryFactory());
        }

        setTypedValue(new MultiPoint(points, new GeometryFactory()));
    }

    /**
     * Sets the Field's value by the byte array parameter
     * 
     * @param dataBytes, field's value as a byte array
     */
    protected void setTypedValue(byte[] dataBytes)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }
        String value = Base64.encode(dataBytes);
        setBaseValue(value);
        setSize(dataBytes.length);
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and mimetype.
     * 
     * @param dataBytes, field's value as a byte array
     * @param filename, field's filename
     * @param extension, field's extension
     * @param mimeType, field's mimetype
     */
    protected void setTypedValue(byte[] dataBytes, String filename, String extension, String mimeType)
    {
        String value = Base64.encode(dataBytes);
        setBaseValue(value);
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setSize(dataBytes.length);
    }

    /**
     * Sets the Field's hash value. Also sets the filename, extension and mimetype.
     * 
     * @param filename, field's filename
     * @param extension, field's extension
     * @param mimeType, field's mimetype
     * @param hash, field's hash value
     */
    protected void setTypedValue(String filename, String extension, String mimeType, String hash)
    {
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setHash(hash);
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and mimetype by the Document
     * Properties.
     * 
     * @param dataBytes, file's value as a byte array
     * @param docProps, file's DocumentProperties
     */
    protected void setTypedValue(byte[] dataBytes, DocumentProperties docProps)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Set Bytes
        setBaseValue(Base64.encode(dataBytes));
        setFilename(docProps.getFilename());
        setExtension(docProps.getExtension());
        setMimeType(docProps.getMimeType());
        setSize(dataBytes.length);

    }

    /**
     * Sets value, filename, extension, mimetype if the datatype equals the file type and the file exists.
     * 
     * @param docProps, file's DocumentProperties
     * @throws IOException
     */
    protected void setTypedValue(DocumentProperties docProps) throws IOException
    {
        if (getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Does File Exist?
        Path path = Paths.get(docProps.getFullFilename());
        if (Files.exists(path))
        {
            // Read Bytes
            byte[] fileBytes = Files.readAllBytes(path);

            // Set Bytes
            setBaseValue(Base64.encode(fileBytes));
            setFilename(docProps.getFilename());
            setExtension(docProps.getExtension());
            setMimeType(docProps.getMimeType());
            setSize(fileBytes.length);
        }
    }

    /**
     * Returns the UUID value of the field
     * 
     * @return UUID, field's value as a UUID. Null if not a UUID.
     * @throws ClassCastException
     */
    protected UUID getGuidValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
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
     * @throws ClassCastException
     */
    protected DateTime getDateTimeValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
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
     * @return boolean, field's value as a boolean
     * @throws ClassCastException
     */
    protected Boolean getBooleanValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.isNullOrEmpty(getBaseValue())) throw new ClassCastException("Type mismatch");

        boolean value = Boolean.parseBoolean(getBaseValue());

        return value;

    }

    /**
     * Returns the Field's Integer value.
     * 
     * @return integer, field's value as an int
     * @throws CoalesceDataFormatException
     */
    protected Integer getIntegerValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
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
     * Returns the Field's geometry Point value.
     * 
     * @return Point, field's value as a geometry point
     * @throws CoalesceDataFormatException
     */
    protected Point getPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
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
     * @return Coordinate, field's value as a geometry Coordinate. Null if not populated
     * @throws CoalesceDataFormatException
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
     * @return MultiPoint, field's value as a geometry MultiPoint
     * @throws CoalesceDataFormatException
     */
    protected MultiPoint getMultiPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateListType)
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
     * @return Coordinate[], field's value as a geometry Coordinate array
     * 
     * @throws CoalesceDataFormatException
     */
    protected Coordinate[] getCoordinateListValue() throws CoalesceDataFormatException
    {
        return getMultiPointValue().getCoordinates();
    }

    /**
     * Returns the binary value of Field's associated file.
     * 
     * @return byte[], field's value as a byte array
     * 
     * @throws ClassCastException
     */
    protected byte[] getBinaryValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType && getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Basic Check
        String rawValue = getBaseValue();
        if (rawValue.length() > 0)
        {
            // Needs to be tested for compatibility with .Net. Should be.
            byte[] bytes = Base64.decode(rawValue);

            return bytes;

        }
        else
        {
            return new byte[0];
        }
    }

    /*--------------------------------------------------------------------------
    Private Functions
    --------------------------------------------------------------------------*/

    private void assertValid(Coordinate location) throws CoalesceDataFormatException
    {
        if (location == null || Math.abs(location.x) > 90 || Math.abs(location.y) > 90)
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
