/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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
package com.incadencecorp.coalesce.framework.persistance.derby;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceBinaryField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This record is designed for unit test and contains a field for each data
 * type.
 * 
 * @author n78554
 */
public class DerbyTestRecord extends CoalesceRecord {

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public DerbyTestRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public DerbyTestRecord(CoalesceRecord record)
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
        CoalesceFieldDefinition.create(recordset, "binaryField", ECoalesceFieldDataTypes.BINARY_TYPE);
        CoalesceFieldDefinition.create(recordset, "booleanField", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
        CoalesceFieldDefinition.create(recordset, "dateField", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordset, "doubleField", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceFieldDefinition.create(recordset, "fileField", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(recordset, "floatField", ECoalesceFieldDataTypes.FLOAT_TYPE);
        CoalesceFieldDefinition.create(recordset, "geolistField", ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "geoField", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
        CoalesceFieldDefinition.create(recordset, "guidField", ECoalesceFieldDataTypes.GUID_TYPE);
        CoalesceFieldDefinition.create(recordset, "intField", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(recordset, "stringField", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "uriField", ECoalesceFieldDataTypes.URI_TYPE);
        CoalesceFieldDefinition.create(recordset, "longField", ECoalesceFieldDataTypes.LONG_TYPE);
        CoalesceFieldDefinition.create(recordset, "polyField", ECoalesceFieldDataTypes.POLYGON_TYPE);
        CoalesceFieldDefinition.create(recordset, "lineField", ECoalesceFieldDataTypes.LINE_STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "circleField", ECoalesceFieldDataTypes.CIRCLE_TYPE);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enumField");
        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enumlistField");
        CoalesceFieldDefinition.create(recordset, "noflattenField", ECoalesceFieldDataTypes.STRING_TYPE).setFlatten(false);
        CoalesceFieldDefinition.create(recordset, "doublelistField", ECoalesceFieldDataTypes.DOUBLE_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "intlistField", ECoalesceFieldDataTypes.INTEGER_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "floatlistField", ECoalesceFieldDataTypes.FLOAT_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "longlistField", ECoalesceFieldDataTypes.LONG_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "stringlistField", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "guidlistField", ECoalesceFieldDataTypes.GUID_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "booleanlistField", ECoalesceFieldDataTypes.BOOLEAN_LIST_TYPE);

        return recordset;
    }

    /*--------------------------------------------------------------------------
    Public Getters
    --------------------------------------------------------------------------*/

    public CoalesceBinaryField getBinaryField()
    {
        return (CoalesceBinaryField) getFieldByName("binaryField");
    }

    public CoalesceBooleanField getBooleanField()
    {
        return (CoalesceBooleanField) getFieldByName("booleanField");
    }

    public CoalesceDateTimeField getDateField()
    {
        return (CoalesceDateTimeField) getFieldByName("dateField");
    }

    public CoalesceDoubleField getDoubleField()
    {
        return (CoalesceDoubleField) getFieldByName("doubleField");
    }

    public CoalesceFileField getFileField()
    {
        return (CoalesceFileField) getFieldByName("fileField");
    }

    public CoalesceFloatField getFloatField()
    {
        return (CoalesceFloatField) getFieldByName("floatField");
    }

    public CoalesceCoordinateListField getGeoListField()
    {
        return (CoalesceCoordinateListField) getFieldByName("geolistField");
    }

    public CoalesceCoordinateField getGeoField()
    {
        return (CoalesceCoordinateField) getFieldByName("geoField");
    }

    public CoalesceGUIDField getGuidField()
    {
        return (CoalesceGUIDField) getFieldByName("guidField");
    }

    public CoalesceIntegerField getIntegerField()
    {
        return (CoalesceIntegerField) getFieldByName("intField");
    }

    public CoalesceStringField getStringField()
    {
        return (CoalesceStringField) getFieldByName("stringField");
    }

    public CoalesceStringField getURIField()
    {
        return (CoalesceStringField) getFieldByName("uriField");
    }

    public CoalesceLongField getLongField()
    {
        return (CoalesceLongField) getFieldByName("longField");
    }

    public CoalescePolygonField getPolygonField()
    {
        return (CoalescePolygonField) getFieldByName("polyField");
    }

    public CoalesceLineStringField getLineField()
    {
        return (CoalesceLineStringField) getFieldByName("lineField");
    }

    public CoalesceCircleField getCircleField()
    {
        return (CoalesceCircleField) getFieldByName("circleField");
    }

    public CoalesceStringField getNoFlattenField()
    {
        return (CoalesceStringField) getFieldByName("noflattenField");
    }

    public CoalesceDoubleListField getDoubleListField()
    {
        return (CoalesceDoubleListField) getFieldByName("doublelistField");
    }

    public CoalesceIntegerListField getIntegerListField()
    {
        return (CoalesceIntegerListField) getFieldByName("intlistField");
    }

    public CoalesceFloatListField getFloatListField()
    {
        return (CoalesceFloatListField) getFieldByName("floatlistField");
    }

    public CoalesceLongListField getLongListField()
    {
        return (CoalesceLongListField) getFieldByName("longlistField");
    }

    public CoalesceStringListField getStringListField()
    {
        return (CoalesceStringListField) getFieldByName("stringlistField");
    }

    public CoalesceGUIDListField getGUIDListField()
    {
        return (CoalesceGUIDListField) getFieldByName("guidlistField");
    }

    public CoalesceEnumerationField getEnumerationField()
    {
        return (CoalesceEnumerationField) getFieldByName("enumField");
    }

    public CoalesceEnumerationListField getEnumerationListField()
    {
        return (CoalesceEnumerationListField) getFieldByName("enumlistField");
    }

}
