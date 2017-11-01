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

package com.incadencecorp.coalesce.framework.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Abstract class that provides common functionality between fields and their
 * history.
 * 
 * @author n78554
 *
 * @param <T>
 */
public abstract class CoalesceFieldBase<T> extends CoalesceObject implements ICoalesceField<T> {

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

    public abstract String getClassificationMarkingAsString();

    public abstract void setClassificationMarkingAsString(String value);

    @Override
    public abstract Locale getInputLang();

    @Override
    public abstract void setInputLang(Locale value);

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns the field value with the classification marking.
     * 
     * @return String, Marking + " " + value.
     */
    @JsonIgnore
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

    /**
     * Sets the Field's ClassificationMarking attribute based on the Marking
     * class value parameter.
     * 
     * @param value Marking class to be the Field's ClassificationMarking
     *            attribute.
     */
    public void setClassificationMarking(Marking value)
    {
        setClassificationMarkingAsString(value.toString());
    }

    /**
     * Return a Marking class value of the Field's ClassificationMarking
     * attribute.
     * 
     * @return Marking class of the Field's ClassificationMarking attribute.
     */
    @JsonIgnore
    public Marking getClassificationMarking()
    {
        return new Marking(getClassificationMarkingAsString());
    }

    /**
     * Returns the portion marking representation of the full classification
     * marking.
     * 
     * @return String, the portion marking.
     */
    public String getPortionMarking()
    {
        Marking mrk = new Marking(getClassificationMarkingAsString());
        return mrk.toPortionString();
    }

    /**
     * Sets the key value for the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     * .
     * 
     * @param fieldHistory provides the value of the newer field's Previous
     *            History Key.
     */
    public void setPreviousHistoryKey(CoalesceFieldHistory fieldHistory)
    {
        if (fieldHistory == null)
            throw new NullArgumentException("fieldHistory");

        setPreviousHistoryKey(fieldHistory.getKey());
    }

    /**
     * Sets the Field's value by the String parameter.
     * 
     * @param value field's value as a String.
     */
    @JsonIgnore
    protected void setTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = getDataType();
        if (fieldType != ECoalesceFieldDataTypes.STRING_TYPE && fieldType != ECoalesceFieldDataTypes.URI_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setBaseValue(value);
    }

    @JsonIgnore
    protected void setTypedValue(String[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.STRING_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setArray(values);
    }

    /**
     * Sets the Field's value by the UUID parameter.
     * 
     * @param value field's value as a UUID.
     */
    @JsonIgnore
    protected void setTypedValue(UUID value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GUID_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(GUIDHelper.getGuidString(value));
        }

    }

    @JsonIgnore
    protected void setTypedValue(UUID[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GUID_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String[] results;

        if (values != null)
        {
            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = GUIDHelper.getGuidString(values[ii]);
            }
        }
        else
        {
            results = null;
        }

        setArray(results);
    }

    /**
     * Sets the Field's value by the DateTime parameter.
     * 
     * @param value field's value as a DateTime.
     */
    @JsonIgnore
    protected void setTypedValue(DateTime value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DATE_TIME_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(JodaDateTimeHelper.toXmlDateTimeUTC(value));
        }
    }

    /**
     * Sets the Field's value by the boolean parameter.
     * 
     * @param value field's value as a boolean.
     */
    @JsonIgnore
    protected void setTypedValue(Boolean value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(value.toString());
        }

    }

    @JsonIgnore
    protected void setTypedValue(boolean[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setArray(ArrayHelper.toStringArray(values));
    }

    /**
     * Sets the Field's value by the int parameter.
     * 
     * @param value field's value as an integer.
     */
    @JsonIgnore
    protected void setTypedValue(Integer value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_TYPE
                && getDataType() != ECoalesceFieldDataTypes.ENUMERATION_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(value.toString());
        }

    }

    @JsonIgnore
    protected void setTypedValue(int[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_LIST_TYPE
                && getDataType() != ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String[] results;

        if (values != null)
        {
            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = Integer.toString(values[ii]);
            }
        }
        else
        {
            results = null;
        }

        setArray(results);
    }

    /**
     * Sets the Field's value by the int parameter.
     * 
     * @param value field's value as an integer
     */
    @JsonIgnore
    protected void setTypedValue(Double value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DOUBLE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(value.toString());
        }
    }

    @JsonIgnore
    protected void setTypedValue(double[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DOUBLE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String[] results;

        if (values != null)
        {
            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = Double.toString(values[ii]);
            }
        }
        else
        {
            results = null;
        }

        setArray(results);
    }

    /**
     * Sets the Field's value by the float parameter.
     * 
     * @param value field's value as an integer.
     */
    @JsonIgnore
    protected void setTypedValue(Float value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FLOAT_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(value.toString());
        }
    }

    @JsonIgnore
    protected void setTypedValue(float[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FLOAT_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String[] results;

        if (values != null)
        {
            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = Float.toString(values[ii]);
            }
        }
        else
        {
            results = null;
        }

        setArray(results);
    }

    /**
     * Sets the Field's value by the int parameter.
     * 
     * @param value field's value as an integer.
     */
    @JsonIgnore
    protected void setTypedValue(Long value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.LONG_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            setBaseValue(value.toString());
        }

    }

    @JsonIgnore
    protected void setTypedValue(long[] values)
    {
        if (getDataType() != ECoalesceFieldDataTypes.LONG_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String[] results;

        if (values != null)
        {
            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = Long.toString(values[ii]);
            }
        }
        else
        {
            results = null;
        }

        setArray(results);
    }

    /**
     * Sets the Field's value by the geometry Point parameter.
     * 
     * @param value field's value as a geometry point.
     * @throws CoalesceDataFormatException
     */
    @JsonIgnore
    protected void setTypedValue(Point value) throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            assertValid(value);

            setBaseValue(geometryToWKT(value));
        }
    }

    /**
     * Sets the Field's value by the geometry Coordinate parameter.
     * 
     * @param value field's value as a geometry coordinate.
     * @throws CoalesceDataFormatException
     */
    @JsonIgnore
    protected void setTypedValue(Coordinate value) throws CoalesceDataFormatException
    {
        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            GeometryFactory factory = new GeometryFactory();
            setTypedValue(factory.createPoint(value));
        }
    }

    /**
     * Sets the Field's value by the geometry MultiPoint parameter.
     * 
     * @param multiPoint field's value as a geometry multipoint.
     * @throws CoalesceDataFormatException
     */
    @JsonIgnore
    protected void setTypedValue(MultiPoint multiPoint) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (multiPoint == null)
        {
            setBaseValue(null);
        }
        else
        {
            assertValid(multiPoint);
            WKTWriter writer = new WKTWriter(3);
            setBaseValue(writer.write(multiPoint));

        }
    }

    /**
     * Sets the Field's value by the geometry coordinate array parameter.
     * 
     * @param value field's value as a geometry coordinate array.
     * @throws CoalesceDataFormatException
     */
    @JsonIgnore
    protected void setTypedValue(Coordinate[] value) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setTypedValue((MultiPoint) null);
        }
        else
        {

            GeometryFactory factory = new GeometryFactory();

            // Initialize Array
            Point[] points = new Point[value.length];

            for (int ii = 0; ii < value.length; ii++)
            {
                points[ii] = factory.createPoint(value[ii]);
            }

            setTypedValue(new MultiPoint(points, factory));
        }
    }

    @JsonIgnore
    protected void setTypedValue(LineString value) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.LINE_STRING_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {

            assertValid(value.getCoordinates());
            WKTWriter writer = new WKTWriter(3);
            setBaseValue(writer.write(value));

        }
    }

    @JsonIgnore
    protected void setTypedValue(Polygon value) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.POLYGON_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            assertValid(value.getCoordinates());
            WKTWriter writer = new WKTWriter(3);
            setBaseValue(writer.write(value));

        }
    }

    @JsonIgnore
    protected void setTypedValue(CoalesceCircle value) throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.CIRCLE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value == null)
        {
            setBaseValue(null);
        }
        else
        {
            assertValid(value.getCenter());

            GeometryFactory factory = new GeometryFactory();
            setBaseValue(geometryToWKT(factory.createPoint(value.getCenter())));
            setAttribute(CoalesceCircleField.ATTRIBUTE_RADIUS, Double.toString(value.getRadius()));

        }
    }

    /**
     * Returns the UUID value of the field.
     * 
     * @return UUID, field's value as a UUID. Null if not a UUID.
     */
    protected UUID getGuidValue()
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

    protected UUID[] getGuidListValue()
    {

        if (getDataType() != ECoalesceFieldDataTypes.GUID_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return ArrayHelper.toUUIDArray(getArray());

    }

    /**
     * Returns the Field's DateTime value.
     * 
     * @return DateTime, field's value as a DateTime. Null if not populated or
     *         invalid.
     */
    protected DateTime getDateTimeValue()
    {

        if (getDataType() != ECoalesceFieldDataTypes.DATE_TIME_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.fromXmlDateTimeUTC(getBaseValue());

        if (value == null)
            return null;

        return value;

    }

    /**
     * Returns the Field's boolean value.
     * 
     * @return boolean, field's value as a boolean.
     */
    protected Boolean getBooleanValue()
    {
        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.isNullOrEmpty(getBaseValue()))
            return null;

        return Boolean.parseBoolean(getBaseValue());
    }

    /**
     * Returns the Field's Coordinate array values.
     * 
     * @return Boolean[], field's value as a boolean array.
     * 
     * @throws CoalesceDataFormatException
     */
    protected boolean[] getBooleanListValue() throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.BOOLEAN_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.isNullOrEmpty(getBaseValue()))
        {
            return null;
        }
        else
        {
            return ArrayHelper.toBooleanArray(getArray());
        }
    }

    /**
     * Returns the Field's Integer value.
     * 
     * @return integer, field's value as an int.
     * @throws CoalesceDataFormatException
     */
    protected Integer getIntegerValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_TYPE
                && getDataType() != ECoalesceFieldDataTypes.ENUMERATION_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            return Integer.parseInt(getBaseValue());

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    protected int[] getIntegerListValue()
    {
        if (getDataType() != ECoalesceFieldDataTypes.INTEGER_LIST_TYPE
                && getDataType() != ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return ArrayHelper.toIntegerArray(getArray());
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

            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            return Double.parseDouble(getBaseValue());

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    protected double[] getDoubleListValue()
    {

        if (getDataType() != ECoalesceFieldDataTypes.DOUBLE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return ArrayHelper.toDoubleArray(getArray());
    }

    /**
     * Returns the Field's Float value.
     * 
     * @return Float, field's value as an Float
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

            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            return Float.parseFloat(getBaseValue());

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    protected float[] getFloatListValue()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FLOAT_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return ArrayHelper.toFloatArray(getArray());

    }

    /**
     * Returns the Field's Long value.
     * 
     * @return Long, field's value as an Float
     * @throws CoalesceDataFormatException
     */
    protected Long getLongValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.LONG_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            return Long.parseLong(getBaseValue());

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException("Failed to parse integer value for: " + getName());
        }

    }

    protected long[] getLongListValue()
    {
        if (getDataType() != ECoalesceFieldDataTypes.LONG_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return ArrayHelper.toLongArray(getArray());

    }

    /**
     * Returns the Field's geometry Point value.
     * 
     * @return Point, field's value as a geometry point.
     * @throws CoalesceDataFormatException
     */
    protected Point getPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

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
     * @return Coordinate, field's value as a geometry Coordinate. Null if not
     *         populated.
     * @throws CoalesceDataFormatException
     */
    protected Coordinate getCoordinateValue() throws CoalesceDataFormatException
    {
        Point point = getPointValue();

        if (point == null)
            return null;

        assertValid(point);

        return point.getCoordinate();
    }

    /**
     * Returns the Field's geometry MultiPoint value.
     * 
     * @return MultiPoint, field's value as a geometry MultiPoint.
     * @throws CoalesceDataFormatException
     */
    protected MultiPoint getMultiPointValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

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
        boolean isSuccessful = false;

        if (value.endsWith("MULTIPOINT EMPTY"))
            return;

        if (value.startsWith("MULTIPOINT (") || value.startsWith("MULTIPOINT("))
        {
            String trimmed = value.replace("MULTIPOINT (", "").replace("MULTIPOINT(", "");

            if (trimmed.endsWith(")"))
            {
                trimmed = trimmed.replaceAll("\\)$", "");

                // Get Points
                String[] points = trimmed.split(", ");

                for (String point : points)
                {
                    isSuccessful = false;

                    if (point.startsWith("(") && point.endsWith(")"))
                    {
                        point = point.replace("(", "").replace(")", "");
                    }

                    String[] values = point.split(" ");

                    // At Least 2D Point?
                    if (values.length >= 2)
                    {
                        try
                        {
                            // Yes; Verify Each Number
                            for (String number : values)
                            {
                                Double.parseDouble(number);
                            }
                        }
                        catch (NumberFormatException nfe)
                        {
                            throw new CoalesceDataFormatException("Invalid coordinate (" + getName() + "): "
                                    + nfe.getMessage());
                        }

                        isSuccessful = true;
                    }

                    // Exit for loop if any point is not successful
                    if (!isSuccessful)
                        break;
                }
            }
        }

        if (!isSuccessful)
        {
            throw new CoalesceDataFormatException("Invalid coordinate (" + getName() + "): Malformed");
        }

        return;

    }

    /**
     * Returns the Field's Coordinate array values.
     * 
     * @return Coordinate[], field's value as a geometry Coordinate array.
     * 
     * @throws CoalesceDataFormatException
     */
    protected Coordinate[] getCoordinateListValue() throws CoalesceDataFormatException
    {
        if (StringHelper.isNullOrEmpty(getBaseValue()))
        {
            return null;
        }
        else
        {
            return getMultiPointValue().getCoordinates();
        }
    }

    /**
     * Returns the Field's geometry LineString value.
     * 
     * @return Point, field's value as a geometry LineString.
     * @throws CoalesceDataFormatException
     */
    protected LineString getLineStringValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.LINE_STRING_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            WKTReader reader = new WKTReader();
            LineString retval = (LineString) reader.read(getBaseValue());

            assertValid(retval.getCoordinates());

            return retval;

        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }
    }

    /**
     * Returns the Field's geometry Polygon value.
     * 
     * @return Point, field's value as a geometry Polygon.
     * @throws CoalesceDataFormatException
     */
    protected Polygon getPolygonValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.POLYGON_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            WKTReader reader = new WKTReader();
            Polygon polygon = (Polygon) reader.read(getBaseValue());

            assertValid(polygon.getCoordinates());

            return polygon;

        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }
    }

    protected CoalesceCircle getCircleValue() throws CoalesceDataFormatException
    {

        if (getDataType() != ECoalesceFieldDataTypes.CIRCLE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getBaseValue()))
                return null;

            WKTReader reader = new WKTReader();
            Point point = (Point) reader.read(getBaseValue());

            assertValid(point);

            CoalesceCircle reval = new CoalesceCircle();
            reval.setCenter(point.getCoordinate());
            reval.setRadius(Double.valueOf(getAttribute(CoalesceCircleField.ATTRIBUTE_RADIUS)));

            return reval;

        }
        catch (ParseException e)
        {
            throw new CoalesceDataFormatException("Failed to parse point value for: " + getName());
        }
    }

    protected String[] getArray()
    {

        String[] results;

        if (!StringHelper.isNullOrEmpty(this.getBaseValue()))
        {
            results = this.getBaseValue().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            for (int ii = 0; ii < results.length; ii++)
            {
                results[ii] = StringEscapeUtils.unescapeCsv(results[ii]);
            }

        }
        else
        {
            results = new String[0];
        }

        return results;

    }

    protected void addArray(String[] values)
    {

        List<String> list = new ArrayList<String>();

        // Combine List
        list.addAll(Arrays.asList(getArray()));
        list.addAll(Arrays.asList(values));

        // Set List
        setArray(list.toArray(new String[list.size()]));

    }

    protected void setArray(String[] values)
    {

        if (values != null && values.length > 0)
        {

            String[] escaped = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                escaped[ii] = StringEscapeUtils.escapeCsv(values[ii]);
            }

            setBaseValue(StringUtils.join(escaped, ","));
        }
        else
        {
            setBaseValue(null);
        }

    }

    /*--------------------------------------------------------------------------
    Private Functions
    --------------------------------------------------------------------------*/

    private String geometryToWKT(Geometry geometry)
    {

        WKTWriter writer = new WKTWriter(3);

        String wkt = writer.write(geometry);

        return wkt;

        // Add the following code back in to support NaN

        // Get Local Symbols
        // Locale def = Locale.getDefault(Locale.Category.FORMAT);
        // DecimalFormatSymbols symbols = new DecimalFormatSymbols(def);

        // Replace NaN symbol with 'NaN'
        // return wkt.replaceAll(symbols.getNaN(), "NaN");

    }

    private void assertValid(Coordinate location) throws CoalesceDataFormatException
    {

        if (location != null)
        {

            // Valid Number?
            if (Double.isNaN(location.x) || Double.isNaN(location.y))
            {
                throw new CoalesceDataFormatException("X and Y Coordinates cannot be NaN: " + getName());
            }

            // In Range?
            if (!CoalesceSettings.EAxis.X.isValid(location.x) || !CoalesceSettings.EAxis.Y.isValid(location.y)
                    || !CoalesceSettings.EAxis.Z.isValid(location.z))
            {
                throw new CoalesceDataFormatException("Coordinate out of range: " + getName());
            }

            if (Double.isNaN(location.z))
            {
                location.z = CoalesceSettings.getDefaultZValue();
            }

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

    private void assertValid(Coordinate[] values) throws CoalesceDataFormatException
    {
        for (Coordinate location : values)
        {
            assertValid(location);
        }
    }

    private void assertValid(MultiPoint multiPoint) throws CoalesceDataFormatException
    {
        for (Coordinate location : multiPoint.getCoordinates())
        {
            assertValid(location);
        }
    }

}
