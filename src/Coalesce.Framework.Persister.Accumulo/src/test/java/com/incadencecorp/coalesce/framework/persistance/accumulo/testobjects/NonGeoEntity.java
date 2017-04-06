package com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

public class NonGeoEntity extends CoalesceEntity {

    /**
     * @author Matthew DeFazio
     * 
     *         This class serves as a test object for persisting, retrieving, and searching JUnit tests.
     * 
     *         NonGeoEntity extends the CoalesceEntity object.
     */

    public static final String NAME = "UnitTest_DATA";
    public static final String SOURCE = "UnitTest";
    public static final String VERSION = "1.0";
    public static final String TITLE = "UnitTestTitle";
    public static final String SECTION = "UnitTest_Section";
    public static final String RECORDSET = "UnitTest_Recordset";
    public CoalesceRecordset eventRecordSet;

    public static String getRecordSetName()
    {
        return RECORDSET; // NAME + "/" + SECTION + "/" + RECORDSET;
    }

    public static String getQueryName()
    {
        return RECORDSET; //NAME + "_" + SOURCE + "_" + VERSION + "." + SECTION + "." + RECORDSET;
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
