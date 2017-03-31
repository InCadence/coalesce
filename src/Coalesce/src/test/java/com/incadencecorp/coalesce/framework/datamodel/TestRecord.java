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

import com.incadencecorp.coalesce.framework.datamodel.CoalesceBinaryField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This record is designed for unit test and contains a field for each data
 * type.
 * 
 * @author n78554
 */
public class TestRecord extends CoalesceRecord {

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public TestRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public TestRecord(CoalesceRecord record)
    {
        super(record);
    }

    /*--------------------------------------------------------------------------
    Record Set Factory Method
    --------------------------------------------------------------------------*/

    /**
     * @param section
     * @param name
     * @return a record set that can be used for create new records of this
     *         type.
     */
    public static CoalesceRecordset createCoalesceRecordset(CoalesceSection section, String name)
    {

        CoalesceRecordset recordset = CoalesceRecordset.create(section, name);

        // Add a field of each type
        CoalesceFieldDefinition.create(recordset, "binary", ECoalesceFieldDataTypes.BINARY_TYPE);
        CoalesceFieldDefinition.create(recordset, "boolean", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
        CoalesceFieldDefinition.create(recordset, "date", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordset, "double", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceFieldDefinition.create(recordset, "file", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(recordset, "float", ECoalesceFieldDataTypes.FLOAT_TYPE);
        CoalesceFieldDefinition.create(recordset, "geolist", ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "geo", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
        CoalesceFieldDefinition.create(recordset, "guid", ECoalesceFieldDataTypes.GUID_TYPE);
        CoalesceFieldDefinition.create(recordset, "int", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(recordset, "string", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "uri", ECoalesceFieldDataTypes.URI_TYPE);
        CoalesceFieldDefinition.create(recordset, "long", ECoalesceFieldDataTypes.LONG_TYPE);
        CoalesceFieldDefinition.create(recordset, "poly", ECoalesceFieldDataTypes.POLYGON_TYPE);
        CoalesceFieldDefinition.create(recordset, "line", ECoalesceFieldDataTypes.LINE_STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "circle", ECoalesceFieldDataTypes.CIRCLE_TYPE);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum");
        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enumlist");
        CoalesceFieldDefinition.create(recordset, "noflatten", ECoalesceFieldDataTypes.STRING_TYPE).setFlatten(false);
        CoalesceFieldDefinition.create(recordset, "doublelist", ECoalesceFieldDataTypes.DOUBLE_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "intlist", ECoalesceFieldDataTypes.INTEGER_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "floatlist", ECoalesceFieldDataTypes.FLOAT_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "longlist", ECoalesceFieldDataTypes.LONG_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "stringlist", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "guidlist", ECoalesceFieldDataTypes.GUID_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "booleanlist", ECoalesceFieldDataTypes.BOOLEAN_LIST_TYPE);

        return recordset;

    }

    /*--------------------------------------------------------------------------
    Public Getters
    --------------------------------------------------------------------------*/

    public CoalesceBinaryField getBinaryField()
    {
        return (CoalesceBinaryField) getFieldByName("binary");
    }

    public CoalesceBooleanField getBooleanField()
    {
        return (CoalesceBooleanField) getFieldByName("boolean");
    }

    public CoalesceDateTimeField getDateField()
    {
        return (CoalesceDateTimeField) getFieldByName("date");
    }

    public CoalesceDoubleField getDoubleField()
    {
        return (CoalesceDoubleField) getFieldByName("double");
    }

    public CoalesceFileField getFileField()
    {
        return (CoalesceFileField) getFieldByName("file");
    }

    public CoalesceFloatField getFloatField()
    {
        return (CoalesceFloatField) getFieldByName("float");
    }

    public CoalesceCoordinateListField getGeoListField()
    {
        return (CoalesceCoordinateListField) getFieldByName("geolist");
    }

    public CoalesceCoordinateField getGeoField()
    {
        return (CoalesceCoordinateField) getFieldByName("geo");
    }

    public CoalesceGUIDField getGuidField()
    {
        return (CoalesceGUIDField) getFieldByName("guid");
    }

    public CoalesceIntegerField getIntegerField()
    {
        return (CoalesceIntegerField) getFieldByName("int");
    }

    public CoalesceStringField getStringField()
    {
        return (CoalesceStringField) getFieldByName("string");
    }

    public CoalesceStringField getURIField()
    {
        return (CoalesceStringField) getFieldByName("uri");
    }

    public CoalesceLongField getLongField()
    {
        return (CoalesceLongField) getFieldByName("long");
    }

    public CoalescePolygonField getPolygonField()
    {
        return (CoalescePolygonField) getFieldByName("poly");
    }

    public CoalesceLineStringField getLineField()
    {
        return (CoalesceLineStringField) getFieldByName("line");
    }

    public CoalesceCircleField getCircleField()
    {
        return (CoalesceCircleField) getFieldByName("circle");
    }

    public CoalesceStringField getNoFlattenField()
    {
        return (CoalesceStringField) getFieldByName("noflatten");
    }

    public CoalesceDoubleListField getDoubleListField()
    {
        return (CoalesceDoubleListField) getFieldByName("doublelist");
    }

    public CoalesceIntegerListField getIntegerListField()
    {
        return (CoalesceIntegerListField) getFieldByName("intlist");
    }

    public CoalesceFloatListField getFloatListField()
    {
        return (CoalesceFloatListField) getFieldByName("floatlist");
    }

    public CoalesceLongListField getLongListField()
    {
        return (CoalesceLongListField) getFieldByName("longlist");
    }

    public CoalesceStringListField getStringListField()
    {
        return (CoalesceStringListField) getFieldByName("stringlist");
    }

    public CoalesceGUIDListField getGUIDListField()
    {
        return (CoalesceGUIDListField) getFieldByName("guidlist");
    }
    
    public CoalesceEnumerationField getEnumerationField()
    {
        return (CoalesceEnumerationField) getFieldByName("enum");
    }

    public CoalesceEnumerationListField getEnumerationListField()
    {
        return (CoalesceEnumerationListField) getFieldByName("enumlist");
    }
}
