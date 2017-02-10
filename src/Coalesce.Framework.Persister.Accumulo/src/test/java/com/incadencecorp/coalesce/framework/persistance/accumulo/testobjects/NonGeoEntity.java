package com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects;

import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class NonGeoEntity extends CoalesceEntity {

    /**
     * @author Matthew DeFazio
     * 
     *         This class serves as a test object for persisting, retrieving, and searching JUnit tests.
     * 
     *         NonGeoEntity extends the CoalesceEntity object.
     */

    private static String NAME = "UnitTest_DATA";
    private static String SOURCE = "UnitTest";
    private static String VERSION = "1.0";
    private static String TITLE = "UnitTestTitle";
    private static String SECTION = "UnitTest_Section";
    private static String RECORDSET = "UnitTest_Recordset";
    private CoalesceRecordset eventRecordSet;

    public static String getRecordSetName()
    {
        return NAME + "/" + SECTION + "/" + RECORDSET;
    }

    public static String getQueryName()
    {
        return NAME + "_" + SOURCE + "_" + VERSION + "." + SECTION + "." + RECORDSET;
    }

    public NonGeoEntity()
    {
        initialize();
        setName(NAME);
        setSource(SOURCE);
        setVersion(VERSION);
        setTitle(TITLE);
        CoalesceSection eventSection = CoalesceSection.create(this, SECTION);
        eventRecordSet = CoalesceRecordset.create(eventSection, RECORDSET);
        CoalesceFieldDefinition.create(eventRecordSet, "GlobalEventID", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(eventRecordSet, "Actor1Name", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(eventRecordSet, "DateTime", ECoalesceFieldDataTypes.DATE_TIME_TYPE);

    }

    public CoalesceRecordset getEventRecordSet()
    {
        return eventRecordSet;
    }

    public void setEventRecordSet(CoalesceRecordset eventRecordSet)
    {
        this.eventRecordSet = eventRecordSet;
    }

    public void setIntegerField(CoalesceRecord eventRecord, String name, int value)
    {
        ((CoalesceIntegerField) eventRecord.getFieldByName(name)).setValue(value);
    }

    public void setStringField(CoalesceRecord eventRecord, String name, String value)
    {
        ((CoalesceStringField) eventRecord.getFieldByName(name)).setValue(value);
    }

}
