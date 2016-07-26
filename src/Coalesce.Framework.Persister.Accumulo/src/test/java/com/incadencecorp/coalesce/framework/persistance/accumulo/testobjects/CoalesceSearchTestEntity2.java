package com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
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
public class CoalesceSearchTestEntity2 extends CoalesceSearchTestEntity1{
    
    // ----------------------------------------------------------------------//
    // Private Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceSection lineSection;
    private CoalesceRecordset lineRecordSet;
    private CoalesceRecord lineRecord;
    private CoalesceRecord circleRecord;//in area section     

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        //super.initialize();
        
        if (!initializeEntity(CoalesceSearchTestEntity2.SOURCE,
                              "1.0",
                              "G2Core_GeoSearch/Point Section/Point Recordset/Record/Point/MultiPoint Recordset Record/Multipoint,G2Core_GeoSearch /Area Section/Area Recordset/Record/Polygon")) return false;
        return initializeReferences();
    }

    
    protected boolean initializeEntity(String source, String version, String title)
    {

        // Already Initialized?
        if (pointRecord != null || multipointRecord != null || polygonRecord !=null || circleRecord != null || lineRecord !=null) return false;
        
        // Initialize Entity
        if (!super.initializeEntity(CoalesceSearchTestEntity2.NAME, source, version, "", "", title)) return false; 
        
        searchEntity1RecordsGen();

        areaSection = CoalesceSection.create(this, CoalesceSearchTestEntity2.NAME + "Area Section");
        areaRecordSet = CoalesceRecordset.create(areaSection, CoalesceSearchTestEntity2.NAME + "Area Recordset");
        
        CoalesceFieldDefinition.create(areaRecordSet, "StringAreaData", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "IntergerAreaData", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "DateTimeAreaData", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "CirclegeomeryData", ECoalesceFieldDataTypes.CIRCLE_TYPE);
        CoalesceFieldDefinition.create(areaRecordSet, "PointGeometryData", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Create New area Record
        circleRecord = areaRecordSet.addNew();
        
        // add Line Section
        lineSection = CoalesceSection.create(this, CoalesceSearchTestEntity2.NAME + " Line Section");
        lineRecordSet = CoalesceRecordset.create(lineSection, CoalesceSearchTestEntity2.NAME + " Line Recordset");
        CoalesceFieldDefinition.create(lineRecordSet, "BooleanLineData", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
        CoalesceFieldDefinition.create(lineRecordSet, "DoubleLineData", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceFieldDefinition.create(lineRecordSet, "LineGeometryData", ECoalesceFieldDataTypes.LINE_STRING_TYPE);

        // Create New line Record
        lineRecord = lineRecordSet.addNew();
        
        // Initialize References
        return this.initializeReferences();
    }
    
    @Override
    protected boolean initializeReferences()
    {
        boolean entity1initRef;
        entity1initRef = searchEntity1initRef();
        if(entity1initRef){
            // Area Record
            if (this.circleRecord == null)
            {
                areaRecordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(CoalesceSearchTestEntity2.NAME
                        + "/Area Section/Area Recordset");
    
                // Valid Xml?
                if (areaRecordSet == null) return false;
    
                if (areaRecordSet.getCount() == 0)
                {
                    this.circleRecord =  areaRecordSet.addNew();
                }
                else
                {
                    this.circleRecord =  areaRecordSet.getItem(0);
                }
    
            }
    
            // Line Record
            if (this.lineRecord == null)
            {
                lineRecordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(CoalesceSearchTestEntity2.NAME
                        + "/Line Section/Line Recordset");
    
                // Valid Xml?
                if (lineRecordSet == null) return false;
    
                if (lineRecordSet.getCount() == 0)
                {
                    this.lineRecord = lineRecordSet.addNew();
                }
                else
                {
                    this.lineRecord = lineRecordSet.getItem(0);
                }
            }
            return true;        
        }
        else{
            return false;
        }

    }
    

    // ----------------------------------------------------------------------//
    // circle Record Fields
    // ----------------------------------------------------------------------//
    public CoalesceStringField getStringAreaData(){
        return (CoalesceStringField) circleRecord.getFieldByName("StringAreaData");
    }

    public CoalesceIntegerField getIntergerAreaData(){
        return (CoalesceIntegerField) circleRecord.getFieldByName("IntergerAreaData");
    }
    
    public CoalesceField<DateTime> getDateTimeAreaData(){
        return (CoalesceDateTimeField) circleRecord.getFieldByName("DateTimeAreaData");
    }
    
    public CoalesceCircleField getCirclegeomeryData(){
        return (CoalesceCircleField) circleRecord.getFieldByName("CirclegeomeryData");
    }
    public CoalesceCoordinateField getPointGeometryData(){
        return (CoalesceCoordinateField) circleRecord.getFieldByName("PointGeometryData");
    }

    // ----------------------------------------------------------------------//
    // line Record Fields
    // ----------------------------------------------------------------------//

    public CoalesceBooleanField getBooleanLineData(){
        return (CoalesceBooleanField) lineRecord.getFieldByName("BooleanLineData");
    }

    public CoalesceDoubleField getDoubleLineData(){
        return (CoalesceDoubleField) lineRecord.getFieldByName("DoubleLineData");
    }

    public CoalesceLineStringField getLineGeometryData(){
        return (CoalesceLineStringField) lineRecord.getFieldByName("LineGeometryData");
    }

    // ----------------------------------------------------------------------//
    // add record
    // ----------------------------------------------------------------------//
    public CoalesceRecord addCircleRecord()
    {
        return circleRecord = areaRecordSet.addNew();
        //return new CoalesceRecord(areaRecordSet.addNew());
    }

    public CoalesceRecord addLineRecord()
    {
        return lineRecord = lineRecordSet.addNew();
        //return new CoalesceRecord(lineRecordSet.addNew());
    }

}

