package Coalesce.Framework.DataModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
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

public abstract class XsdFieldBase extends XsdDataObject implements ICoalesceField {

    /*--------------------------------------------------------------------------
    Public Abstract Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String getValue();

    @Override
    public abstract void setValue(String value);

    @Override
    public abstract ECoalesceFieldDataTypes getDataType();

    @Override
    public abstract void setDataType(ECoalesceFieldDataTypes value);

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

    public abstract String getClassificationMarkingAsString();

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
    public abstract String getInputLang();

    @Override
    public abstract void setInputLang(String value);

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public String getValueWithMarking()
    {
        String val = getValue();
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

    public String getPortionMarking()
    {
        Marking mrk = new Marking(getClassificationMarkingAsString());
        return mrk.ToPortionString();
    }

    public void setPreviousHistoryKey(XsdFieldHistory fieldHistory)
    {
        if (fieldHistory == null) throw new NullArgumentException("fieldHistory");

        setPreviousHistoryKey(fieldHistory.getKey());
    }

    public Object getData() throws CoalesceDataFormatException
    {

        switch (getDataType()) {
        case StringType:
        case UriType:
            return getValue();

        case DateTimeType:
            return getDateTimeValue();

        case BinaryType:
            return getBinaryValue();

        case BooleanType:
            return getBooleanValue();

        case IntegerType:
            return getIntegerValue();

        case GuidType:
            return getGuidValue();

        case FileType:
            return getBinaryValue();

        case GeocoordinateType:
            return getCoordinateValue();

        case GeocoordinateListType:
            return getCoordinateListValue();

        default:
            throw new NotImplementedException(getDataType() + " not implemented");
        }

    }

    public void setData(Object value)
    {
        setTypedValue(value.toString());
    }

    public void setTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = getDataType();
        if (fieldType != ECoalesceFieldDataTypes.StringType && fieldType != ECoalesceFieldDataTypes.UriType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(value);
    }

    public void setTypedValue(UUID value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(GUIDHelper.GetGuidString(value));
    }

    public void setTypedValue(DateTime value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(JodaDateTimeHelper.ToXmlDateTimeUTC(value));
    }

    public void setTypedValue(boolean value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(String.valueOf(value));
    }

    public void setTypedValue(int value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(String.valueOf(value));
    }

    public void setTypedValue(Point value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(value.toText());
    }

    public void setTypedValue(Coordinate value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(WKTWriter.toPoint(value));
    }

    public void setTypedValue(MultiPoint multiPoint)
    {

        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateListType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(multiPoint.toText());
    }

    public void setTypedValue(Coordinate value[])
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

    public void setTypedValue(byte[] dataBytes)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }
        String value = Base64.encode(dataBytes);
        setValue(value);
        setSize(dataBytes.length);
    }

    public void setTypedValue(byte[] dataBytes, String filename, String extension, String mimeType)
    {
        String value = Base64.encode(dataBytes);
        setValue(value);
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setSize(dataBytes.length);
    }

    public void setTypedValue(String filename, String extension, String mimeType, String hash)
    {
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setHash(hash);
    }

    public void setTypedValue(byte[] dataBytes, DocumentProperties docProps)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Set Bytes
        setValue(Base64.encode(dataBytes));
        setFilename(docProps.getFilename());
        setExtension(docProps.getExtension());
        setMimeType(docProps.getMimeType());
        setSize(dataBytes.length);

    }

    public void setTypedValue(DocumentProperties docProps) throws IOException
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
            setValue(Base64.encode(fileBytes));
            setFilename(docProps.getFilename());
            setExtension(docProps.getExtension());
            setMimeType(docProps.getMimeType());
            setSize(fileBytes.length);
        }
    }

    public UUID getGuidValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = getValue();

        if (GUIDHelper.IsValid(value))
        {
            return GUIDHelper.GetGuid(value);
        }
        else
        {
            return null;
        }
    }

    public DateTime getDateTimeValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.FromXmlDateTimeUTC(getValue());

        if (value == null) return null;

        return value;

    }

    public boolean getBooleanValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.IsNullOrEmpty(getValue())) throw new ClassCastException("Type mismatch");

        boolean value = Boolean.parseBoolean(getValue());

        return value;

    }

    public int getIntegerValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            int value = Integer.parseInt(getValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    public Point getPointValue() throws CoalesceDataFormatException
    {
        Point point = null;

        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateType)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            WKTReader reader = new WKTReader();
            point = (Point) reader.read(getValue());
        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }

        return point;
    }

    public Coordinate getCoordinateValue() throws CoalesceDataFormatException
    {
        Point point = getPointValue(); 
        
        if (point == null ) return null;
        
        return point.getCoordinate();
    }

    public MultiPoint getMultiPointValue() throws CoalesceDataFormatException
    {
        MultiPoint multiPoint = null;

        if (getDataType() != ECoalesceFieldDataTypes.GeocoordinateListType)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            WKTReader reader = new WKTReader();
            multiPoint = (MultiPoint) reader.read(getValue());
        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse coordinates value for: " + getName());
        }

        return multiPoint;
    }

    public Coordinate[] getCoordinateListValue() throws CoalesceDataFormatException
    {
        return getMultiPointValue().getCoordinates();
    }

    public byte[] getBinaryValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType && getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Basic Check
        String rawValue = getValue();
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

}
