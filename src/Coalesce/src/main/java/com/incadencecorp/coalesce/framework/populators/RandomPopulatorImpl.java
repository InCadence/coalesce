/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.populators;

import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.util.GeometricShapeFactory;

/**
 * This implementation uses random values to populate fields.
 * 
 * @author n78554
 */
public class RandomPopulatorImpl extends AbstractPopulator {

    private Random rnd = new Random();
    private WKTWriter writer = new WKTWriter(3);

    private int maxListLen = 5;
    private int maxStringLen = 20;
    private boolean isCreateRecordsEnabled = true;

    /**
     * Sets the maximum length for List data types.
     * 
     * @param value
     */
    public void setMaxListLen(int value)
    {
        maxListLen = value;
    }

    /**
     * Sets the maximum string length for String data types.
     * 
     * @param value
     */
    public void setMaxStringLen(int value)
    {
        maxStringLen = value;
    }

    /**
     * Sets whether a record should be created if the record set is empty.
     * 
     * @param value
     */
    public void setCreateRecords(boolean value)
    {
        isCreateRecordsEnabled = value;
    }

    @Override
    public String populate(ECoalesceFieldDataTypes type)
    {
        String[] values;

        if (type.isListType())
        {
            values = new String[rnd.nextInt(maxListLen + 1)];
        }
        else
        {
            values = new String[1];
        }

        for (int ii = 0; ii < values.length; ii++)
        {
            switch (type) {
            case BOOLEAN_LIST_TYPE:
            case BOOLEAN_TYPE:
                values[ii] = Boolean.toString(rnd.nextBoolean());
                break;
            case DATE_TIME_TYPE:
                values[ii] = JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusMinutes(rnd.nextInt(60)));
                break;
            case DOUBLE_LIST_TYPE:
            case DOUBLE_TYPE:
                values[ii] = Double.toString(rnd.nextDouble());
                break;
            case ENUMERATION_LIST_TYPE:
            case ENUMERATION_TYPE:
                values[ii] = "0";
                break;
            case FLOAT_LIST_TYPE:
            case FLOAT_TYPE:
                values[ii] = Float.toString(rnd.nextFloat());
                break;
            case GUID_LIST_TYPE:
            case GUID_TYPE:
                values[ii] = UUID.randomUUID().toString();
                break;
            case INTEGER_LIST_TYPE:
            case INTEGER_TYPE:
                values[ii] = Integer.toString(rnd.nextInt());
                break;
            case LONG_LIST_TYPE:
            case LONG_TYPE:
                values[ii] = Long.toString(rnd.nextLong());
                break;
            case STRING_LIST_TYPE:
            case STRING_TYPE:
                int length = rnd.nextInt(maxStringLen + 1);

                StringBuilder sb = new StringBuilder("");

                for (int jj = 0; jj < length; jj++)
                {
                    char tmp = (char) ('a' + rnd.nextInt('z' - 'a'));
                    sb.append(tmp);
                }

                values[ii] = sb.toString();
                break;
            case GEOCOORDINATE_TYPE:
                values[ii] = writer.write(new GeometryFactory().createPoint(new Coordinate(rnd.nextInt(), rnd.nextInt())));
                break;
            case LINE_STRING_TYPE:
                GeometryFactory lineFactory = new GeometryFactory();
                LineString line = lineFactory.createLineString(new Coordinate[] {
                        getRandomCoordinate(), getRandomCoordinate(), getRandomCoordinate()
                });

                values[ii] = writer.write(line);
                break;
            case POLYGON_TYPE:
                GeometricShapeFactory polygonFactory = new GeometricShapeFactory();
                polygonFactory.setSize(10);
                polygonFactory.setNumPoints(4);
                polygonFactory.setCentre(getRandomCoordinate());
                Polygon shape = polygonFactory.createCircle();

                values[ii] = writer.write(shape);
                break;
            case CIRCLE_TYPE:
                values[ii] = writer.write(new GeometryFactory().createPoint(getRandomCoordinate()));
                break;
            default:
                values[ii] = "";
                break;

            }
        }

        return StringUtils.join(values, ",");
    }

    @Override
    public void populate(CoalesceField<?> field)
    {
        super.populate(field);

        if (field.getDataType() == ECoalesceFieldDataTypes.CIRCLE_TYPE)
        {
            field.setAttribute("radius", Integer.toString(rnd.nextInt()));
        }
    }

    @Override
    public void populate(CoalesceRecordset recordset)
    {
        // Create a record is option is enabled and the record set is empty.
        if (isCreateRecordsEnabled && recordset.getRecords().size() == 0)
        {
            recordset.addNew();
        }

        super.populate(recordset);
    }

    private Coordinate getRandomCoordinate()
    {
        return new Coordinate(getRandom(CoalesceSettings.EAxis.X.getMin(), CoalesceSettings.EAxis.X.getMax()),
                              getRandom(CoalesceSettings.EAxis.Y.getMin(), CoalesceSettings.EAxis.Y.getMax()));
    }

    private int getRandom(double min, double max)
    {
        return rnd.nextInt((int) max + Math.abs((int) min)) + (int) min;
    }
}
