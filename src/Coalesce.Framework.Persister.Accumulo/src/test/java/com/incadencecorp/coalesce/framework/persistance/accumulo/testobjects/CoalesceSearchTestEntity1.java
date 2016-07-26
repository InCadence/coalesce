package com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

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
 * @author Jing Yang
 * Jun 21, 2016
 */

/**
 * 
 */
public class CoalesceSearchTestEntity1 extends CoalesceEntity{
    
    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//
    public static final String NAME = "GeoSearch";
    public static final String SOURCE = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected and Private Member Variables
    // ----------------------------------------------------------------------//

    protected CoalesceSection pointSection, areaSection;
    protected CoalesceRecordset pointRecordSet, multiPointRecordSet, areaRecordSet;

    protected CoalesceRecord pointRecord, multipointRecord, polygonRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(CoalesceSearchTestEntity1.SOURCE,
                              "1.0",
                              "G2Core_GeoSearch/Point_Section/Point_Recordset/Record/Point/MultiPoint_Recordset/Record/Multipoint,G2Core_GeoSearch/Area_Section/Area_Recordset/Record/Polygon")) return false;
        return initializeReferences();
    }

    
    protected boolean initializeEntity(String source, String version, String title)
    {

        // Already Initialized?
        if (pointRecord != null || multipointRecord != null || polygonRecord !=null) return false;
        
        // Initialize Entity
        if (!super.initializeEntity(CoalesceSearchTestEntity1.NAME, source, version, "", "", title)) return false;
        
        searchEntity1RecordsGen();
        
        // Initialize References
        return this.initializeReferences();
    }
    
    @Override
    protected boolean initializeReferences(){
        return searchEntity1initRef();
    }
    
    protected void searchEntity1RecordsGen(){

        // Create point Section
        pointSection = CoalesceSection.create(this, CoalesceSearchTestEntity1.NAME + "Point Section");
        
        pointRecordSet = CoalesceRecordset.create(pointSection, CoalesceSearchTestEntity1.NAME + "Point Recordset");
        CoalesceFieldDefinition.create(pointRecordSet, "StringPointData", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(pointRecordSet, "IntegerPointData", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(pointRecordSet, "DateTimePointData", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(pointRecordSet, "GeocoordinatePointData", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
        pointRecord = pointRecordSet.addNew();
        
        // Create multipoint Section
        multiPointRecordSet = CoalesceRecordset.create(pointSection, "Multi Point Recordset");
        CoalesceFieldDefinition.create(multiPointRecordSet, "StringMultiPointData", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(multiPointRecordSet, "BooleanMultipointData", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
        CoalesceFieldDefinition.create(multiPointRecordSet, "DoubleMultipointData", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceFieldDefinition.create(multiPointRecordSet, "FloatMultipointData", ECoalesceFieldDataTypes.FLOAT_TYPE);
        CoalesceFieldDefinition.create(multiPointRecordSet, "DateTimeMultiPointData", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(multiPointRecordSet, "GeoMultiPointData", ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE);
        multipointRecord = multiPointRecordSet.addNew();

        
        areaSection = CoalesceSection.create(this, CoalesceSearchTestEntity1.NAME + " Area Section");
        areaRecordSet = CoalesceRecordset.create(areaSection, CoalesceSearchTestEntity1.NAME + " Area Recordset");
        CoalesceFieldDefinition.create(areaRecordSet, "StringAreaData", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "BooleanAreaData", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "DateTimeAreaData", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "PolygonAreaData", ECoalesceFieldDataTypes.POLYGON_TYPE);
        polygonRecord = areaRecordSet.addNew();

    }
    
    protected boolean searchEntity1initRef(){
        // Point Record
        if (this.pointRecord == null)
        {
            pointRecordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(CoalesceSearchTestEntity1.NAME
                    + "/Point Section/Point Recordset");

            // Valid Xml?
            if (pointRecordSet == null) return false;

            if (pointRecordSet.getCount() == 0)
            {
                this.pointRecord = pointRecordSet.addNew();
            }
            else
            {
                this.pointRecord = pointRecordSet.getItem(0);
            }

        }

        // Multi Point Record
        if (this.multipointRecord == null)
        {
            multiPointRecordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(CoalesceSearchTestEntity1.NAME
                    + "/Multi Point Section/Multi Point Recordset");

            // Valid Xml?
            if (multiPointRecordSet == null) return false;

            if (multiPointRecordSet.getCount() == 0)
            {
                this.multipointRecord = multiPointRecordSet.addNew();
            }
            else
            {
                this.multipointRecord = multiPointRecordSet.getItem(0);
            }
        }

        // Polygon Record
        if (this.polygonRecord == null)
        {
            areaRecordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(CoalesceSearchTestEntity1.NAME
                    + "/Area Section/Area Recordset");

            // Valid Xml?
            if (areaRecordSet == null) return false;

            if (areaRecordSet.getCount() == 0)
            {
                this.polygonRecord = areaRecordSet.addNew();
            }
            else
            {
                this.polygonRecord =  areaRecordSet.getItem(0);
            }
        }
        
        return true;
    }

    // ----------------------------------------------------------------------//
    // Point Record Fields
    // ----------------------------------------------------------------------//
    public CoalesceRecord getPointRecord(){
        return pointRecord;
    }

    public CoalesceStringField getStringPointData(){
        return (CoalesceStringField) pointRecord.getFieldByName("StringPointData");
    }

    public CoalesceIntegerField getIntegerPointData(){
        return (CoalesceIntegerField) pointRecord.getFieldByName("IntegerPointData");
    }
    
    public CoalesceField<DateTime> getDateTimePointData(){
        return (CoalesceDateTimeField) pointRecord.getFieldByName("DateTimePointData");
    }
    
    public CoalesceCoordinateField getGeocoordinatePointData(){
        return (CoalesceCoordinateField) pointRecord.getFieldByName("GeocoordinatePointData");
    }

    // ----------------------------------------------------------------------//
    // Multi-Points Record Fields
    // ----------------------------------------------------------------------//
    public CoalesceStringField getStringMultiPointData(){
        return (CoalesceStringField) multipointRecord.getFieldByName("StringMultiPointData");
    }

    public CoalesceBooleanField getBooleanMultipointData(){
        return (CoalesceBooleanField) multipointRecord.getFieldByName("BooleanMultipointData");
    }

    public CoalesceDoubleField getDoubleMultipointData(){
        return (CoalesceDoubleField) multipointRecord.getFieldByName("DoubleMultipointData");
    }
    
    public CoalesceFloatField getFloatMultipointData(){
        return (CoalesceFloatField) multipointRecord.getFieldByName("FloatMultipointData");
    }

    public CoalesceField<DateTime> getDateTimeMultiPointData(){
        return (CoalesceDateTimeField) multipointRecord.getFieldByName("DateTimeMultiPointData");
    }
    
    public CoalesceCoordinateListField getGeoMultiPointData(){
        return (CoalesceCoordinateListField) multipointRecord.getFieldByName("GeoMultiPointData");
    }

    // ----------------------------------------------------------------------//
    // Polygon Record Fields
    // ----------------------------------------------------------------------//
    public CoalesceStringField getStringAreaData(){
        return (CoalesceStringField) polygonRecord.getFieldByName("StringAreaData");
    }

    public CoalesceBooleanField getBooleanAreaData(){
        return (CoalesceBooleanField) polygonRecord.getFieldByName("BooleanAreaData");
    }
    
    public CoalesceField<DateTime> getDateTimeAreaData(){
        return (CoalesceDateTimeField) polygonRecord.getFieldByName("DateTimeAreaData");
    }
    
    public CoalescePolygonField getPolygonAreaData(){
        return (CoalescePolygonField) polygonRecord.getFieldByName("PolygonAreaData");
    }
    
    // ----------------------------------------------------------------------//
    // add record
    // ----------------------------------------------------------------------//
    public CoalesceRecord addPointRecord()
    {
        //return pointRecord = pointRecordSet.addNew();
        return new CoalesceRecord(pointRecordSet.addNew());
    }

    public CoalesceRecord addMultiPointRecord()
    {
        //return multipointRecord = multiPointRecordSet.addNew();
        return new CoalesceRecord(multiPointRecordSet.addNew());
    }

    public CoalesceRecord addPolygonRecord()
    {
        //return polygonRecord = areaRecordSet.addNew();
        return new CoalesceRecord(areaRecordSet.addNew());
    }
    
    public void removePointRecord(){
        pointRecordSet.removeAll();
    }

    public void removeMultiPointRecord(){
        multiPointRecordSet.removeAll();
    }

    public void removePolygonRecord(){
        areaRecordSet.removeAll();
    }

}

