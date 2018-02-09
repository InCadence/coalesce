package com.incadencecorp.coalesce.framework.persistance.testobjects;

import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
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

public class GDELT_Test_Entity extends CoalesceEntity {

	/**
	 * @author Bryan Moore
	 * July 2016
	 * 
	 * This class serves as a test object for persisting, retrieving, and searching JUnit tests.
	 * 
	 * GDELT_Test_Entity extends the CoalesceEntity object and fully defines the GDELT event schema.  
	 * The fields DateTime, Actor1Geo_Location, Actor2Geo_Location, and ActionGeo_Location are not 
	 * part of the GDELT schema but have been added to facilitate GeoMesa searching.
	 *  
	 * While all fields are defined here, only a few fields are populated for testing.
	 */
	
	private static String NAME = "GDELT_DATA";
	private static String SOURCE = "gdeltproject.org";
	private static String VERSION = "1.0";
	private static String TITLE = "G2_Core_GDELT";
	private static String SECTION = "Event_Section";
	private static String RECORDSET = "Event_Recordset";
	

	public static String getRecordSetName() {
		return RECORDSET;
	}

	public static String getQueryName() {
		return RECORDSET;
	}

	public GDELT_Test_Entity() {
		initialize();
        setName(NAME);
	    setSource(SOURCE);
	    setVersion(VERSION);
	    setTitle(TITLE);
		CoalesceSection eventSection = CoalesceSection.create(this, "Event_Section");
		CoalesceRecordset eventRecordSet = CoalesceRecordset.create(eventSection, "Event_Recordset");
		CoalesceFieldDefinition.create(eventRecordSet, "GlobalEventID", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Day", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "MonthYear", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Year", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "FractionDate", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Name", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1CountryCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1KnownGroupCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1EthnicCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Religion1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Religion2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Type1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Type2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Type3Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Name", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2CountryCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2KnownGroupCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2EthnicCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Religion1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Religion2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Type1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Type2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Type3Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "IsRootEvent", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "EventCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "EventBaseCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "EventRootCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "QuadClass", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "GoldsteinScale", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "NumMentions", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "NumSources", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "NumArticles", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "AvgTone", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_Type", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_Fullname", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_CountryCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_ADM1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_ADM2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_Lat", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_Long", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_FeatureID", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_Type", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_Fullname", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_CountryCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_ADM1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_ADM2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_Lat", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_Long", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_FeatureID", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_Type", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_Fullname", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_CountryCode", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_ADM1Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_ADM2Code", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_Lat", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_Long", ECoalesceFieldDataTypes.FLOAT_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_FeatureID", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "DATEADDED", ECoalesceFieldDataTypes.INTEGER_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "SOURCEURL", ECoalesceFieldDataTypes.STRING_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "DateTime", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor1Geo_Location",
				ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "Actor2Geo_Location",
				ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
		CoalesceFieldDefinition.create(eventRecordSet, "ActionGeo_Location",
				ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
		
		CoalesceRecord eventRecord = eventRecordSet.addNew();
		setIntegerField(eventRecord, "GlobalEventID", "562505648");
		setIntegerField(eventRecord, "Day", "20060725");
		setIntegerField(eventRecord, "MonthYear", "200607");
		setIntegerField(eventRecord, "Year", "2006");
		setFloatField(eventRecord, "FractionDate", "2006.5616");
		setStringField(eventRecord, "Actor1Code", "EUR");
		setStringField(eventRecord, "Actor1Name", "EUROPE");
		
		
		GeometryFactory factory = new GeometryFactory();
		try {
			Coordinate coord = new Coordinate(51.4347,	-3.18);
			((CoalesceCoordinateField) eventRecord.getFieldByName("Actor1Geo_Location")).setValue(factory.createPoint(coord));
		} catch (CoalesceDataFormatException e) {
			e.printStackTrace();
		}

		DateTime dt = new DateTime(2006, 07, 25, 00, 00, 00, 00);
		((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(dt);
	}
	
	private static void setIntegerField(CoalesceRecord eventRecord, String name, String value) {
		((CoalesceIntegerField) eventRecord.getFieldByName(name)).setValue(NumberUtils.toInt(value));
	}

	private static void setStringField(CoalesceRecord eventRecord, String name, String value) {
		((CoalesceStringField) eventRecord.getFieldByName(name)).setValue(value);
	}

	private static void setFloatField(CoalesceRecord eventRecord, String name, String value) {
		((CoalesceFloatField) eventRecord.getFieldByName(name)).setValue(NumberUtils.toFloat(value));
	}
}
