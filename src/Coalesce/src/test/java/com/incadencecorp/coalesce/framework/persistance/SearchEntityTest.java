package com.incadencecorp.coalesce.framework.persistance;
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
 * Jun 22, 2016
 */

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.CoalesceObjectFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * 
 */
public class SearchEntityTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception{
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception{
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception{
    }

    @Test 
    public void test()
    {
        CoalesceSearchTestEntity1 searchEntity1 = new CoalesceSearchTestEntity1();
        searchEntity1.initialize();
        //System.out.println(searchEntity1.toXml());
        CoalesceObjectFactory.register(CoalesceSearchTestEntity1.class);
        assertFalse(searchEntity1 instanceof CoalesceSearchTestEntity2);
        
        CoalesceSearchTestEntity2 searchEntity2 = new CoalesceSearchTestEntity2();
        searchEntity2.initialize();
        //System.out.println(searchEntity2.toXml());
        CoalesceObjectFactory.register(CoalesceSearchTestEntity2.class);
        assertTrue(searchEntity2 instanceof CoalesceSearchTestEntity1);
    }
    
    @Test
    public void geoSpatialAccuracyTest(){
        Coordinate point1 = new Coordinate(-77.455811, 38.944533);
        Coordinate point2 = new Coordinate(-77.037722, 38.852083);
        
        SearchCoordinates scoord1 =  new SearchCoordinates(point1);
        SearchCoordinates scoord2 =  new SearchCoordinates(point2);
        
        double distanceMtr = scoord1.distanceTo(scoord2);
        double bearingDeg = scoord1.getBearingAsDouble(scoord2);
        
        SearchCoordinates scoord3 = scoord1.getTarget(distanceMtr, bearingDeg);

        //check the accuracy of the computation algorithms: 
        assertEquals(point2.y, scoord3.getLatitude(), 0.0001);
        assertEquals(point2.x, scoord3.getLongitude(), 0.0001);
    }
    
    @Test
    /**
     * 3 individual points and 1 multi-point for each entity, total of 5 entities
     * test: the entity 1 will be in the center of the 5 entities,
     * other 4 entities will be 90 degrees apart from the center starts from north
     * and 50 km away from the center   
     *  */
    public void addPointsToEntity1Test() throws CoalesceDataFormatException
    {
        GeometryFactory factory = new GeometryFactory();

        //entity1, the center point
        CoalesceSearchTestEntity1 searchEntity1 = new CoalesceSearchTestEntity1();
        searchEntity1.initialize();
        
        Coordinate e1pt1 = new Coordinate(-77.455811, 38.944533);
        Coordinate e1pt2 = new Coordinate(-77.037722, 38.852083);
        Coordinate e1pt3 = new Coordinate(-76.668333, 39.175361);

        Coordinate e1mp_1 = new Coordinate(-77.455811, 38.944533);
        Coordinate e1mp_2 = new Coordinate(-77.037722, 38.852083);
        Coordinate e1mp_3 = new Coordinate(-76.668333, 39.175361);

        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt1));
        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt2));
        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt3));
        searchEntity1.addPointRecord();
        
        searchEntity1.getGeoMultiPointData().setValue(new Coordinate[] {e1mp_1, e1mp_2, e1mp_3});
        searchEntity1.addMultiPointRecord();
        
        //System.out.println(searchEntity1.toXml());
        
        //entity2
        CoalesceSearchTestEntity1 searchEntity2 = new CoalesceSearchTestEntity1();
        searchEntity2.initialize();
        
        double northbearing = 0.0;//degrees
        double range1to2 = 50000.0;//meter
        SearchCoordinates entity1point =  new SearchCoordinates(e1pt1);
        SearchCoordinates entity2point = entity1point.getTarget(range1to2, northbearing);

        Coordinate e2pt1 = new Coordinate(entity2point.getLongitude(), entity2point.getLatitude());
        Coordinate e2pt2 = new Coordinate(e2pt1.x +0.2, e2pt1.y+0.1);
        Coordinate e2pt3 = new Coordinate(e2pt2.x +0.2, e2pt2.y+0.1);

        Coordinate e2mp_1 = new Coordinate(e2pt1.x, e2pt1.y);
        Coordinate e2mp_2 = new Coordinate(e2mp_1.x +0.4, e2mp_1.y+0.3);
        Coordinate e2mp_3 = new Coordinate(e2mp_2.x +0.4, e2mp_2.y+0.3);

        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt1));
        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt2));
        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt3));
        searchEntity2.addPointRecord();
        
        searchEntity2.getGeoMultiPointData().setValue(new Coordinate[] {e2mp_1, e2mp_2, e2mp_3});
        searchEntity2.addMultiPointRecord();

        //System.out.println(searchEntity2.toXml());
        
        //check the geo queries between entity1 and entity2
        double distance1to2 = entity1point.distanceTo(entity2point);
        assertEquals(range1to2, distance1to2, 0.01);
        
        double bearing1to2 = entity1point.getBearingAsDouble(entity2point);
        assertEquals(northbearing, bearing1to2, 0.01);
        
        //entity3
        CoalesceSearchTestEntity1 searchEntity3 = new CoalesceSearchTestEntity1();
        searchEntity3.initialize();

        double eastbearing = 90.0;//degrees
        double range1to3 = 50000.0;//meter
        SearchCoordinates entity3point = entity1point.getTarget(range1to3, eastbearing);

        Coordinate e3pt1 = new Coordinate(entity3point.getLongitude(), entity3point.getLatitude());
        Coordinate e3pt2 = new Coordinate(e3pt1.x +0.2, e3pt1.y+0.1);
        Coordinate e3pt3 = new Coordinate(e3pt2.x +0.2, e3pt2.y+0.1);

        Coordinate e3mp_1 = new Coordinate(e3pt1.x, e3pt1.y);
        Coordinate e3mp_2 = new Coordinate(e3mp_1.x +0.4, e3mp_1.y+0.3);
        Coordinate e3mp_3 = new Coordinate(e3mp_2.x +0.4, e3mp_2.y+0.3);

        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt1));
        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt2));
        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt3));
        searchEntity3.addPointRecord();
        
        searchEntity3.getGeoMultiPointData().setValue(new Coordinate[] {e3mp_1, e3mp_2, e3mp_3});
        searchEntity3.addMultiPointRecord();

        //System.out.println(searchEntity3.toXml());

        //check the geo queries between entity1 and entity3
        double distance1to3 = entity1point.distanceTo(entity3point);
        assertEquals(range1to3, distance1to3, 0.01);
        
        double bearing1to3 = entity1point.getBearingAsDouble(entity3point);
        assertEquals(eastbearing, bearing1to3, 0.01);

        //entity4
        CoalesceSearchTestEntity1 searchEntity4 = new CoalesceSearchTestEntity1();
        searchEntity4.initialize();

        double southbearing = 180.0;//degrees
        double range1to4 = 50000.0;//meter
        SearchCoordinates entity4point = entity1point.getTarget(range1to4, southbearing);

        Coordinate e4pt1 = new Coordinate(entity4point.getLongitude(), entity4point.getLatitude());
        Coordinate e4pt2 = new Coordinate(e4pt1.x +0.2, e4pt1.y+0.1);
        Coordinate e4pt3 = new Coordinate(e4pt2.x +0.2, e4pt2.y+0.1);

        Coordinate e4mp_1 = new Coordinate(e4pt1.x, e4pt1.y);
        Coordinate e4mp_2 = new Coordinate(e4mp_1.x +0.4, e4mp_1.y+0.3);
        Coordinate e4mp_3 = new Coordinate(e4mp_2.x +0.4, e4mp_2.y+0.3);

        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt1));
        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt2));
        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt3));
        searchEntity4.addPointRecord();
        
        searchEntity4.getGeoMultiPointData().setValue(new Coordinate[] {e4mp_1, e4mp_2, e4mp_3});
        searchEntity4.addMultiPointRecord();

        //System.out.println(searchEntity4.toXml());
        
        //check the geo queries between entity1 and entity4
        double distance1to4 = entity1point.distanceTo(entity4point);
        assertEquals(range1to4, distance1to4, 0.01);
        
        double bearing1to4 = entity1point.getBearingAsDouble(entity4point);
        assertEquals(southbearing, bearing1to4, 0.01);
        
        //entity5
        CoalesceSearchTestEntity1 searchEntity5 = new CoalesceSearchTestEntity1();
        searchEntity5.initialize();

        double westbearing = 270.0;//degrees
        double range1to5 = 50000.0;//meter
        SearchCoordinates entity5point = entity1point.getTarget(range1to5, westbearing);

        Coordinate e5pt1 = new Coordinate(entity5point.getLongitude(), entity5point.getLatitude());
        Coordinate e5pt2 = new Coordinate(e5pt1.x +0.2, e5pt1.y+0.1);
        Coordinate e5pt3 = new Coordinate(e5pt2.x +0.2, e5pt2.y+0.1);

        Coordinate e5mp_1 = new Coordinate(e5pt1.x, e5pt1.y);
        Coordinate e5mp_2 = new Coordinate(e5mp_1.x +0.4, e5mp_1.y+0.3);
        Coordinate e5mp_3 = new Coordinate(e5mp_2.x +0.4, e5mp_2.y+0.3);

        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt1));
        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt2));
        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt3));
        searchEntity5.addPointRecord();
        
        searchEntity5.getGeoMultiPointData().setValue(new Coordinate[] {e5mp_1, e5mp_2, e5mp_3});
        searchEntity5.addMultiPointRecord();

        //System.out.println(searchEntity5.toXml());

        //check the geo queries between entity1 and entity5
        double distance1to5 = entity1point.distanceTo(entity5point);
        assertEquals(range1to5, distance1to5, 0.01);
        
        double bearing1to5 = entity1point.getBearingAsDouble(entity5point);
        assertEquals(westbearing, bearing1to5, 0.01);

        
    }

    @Test
    /**
     * 3 individual points and 1 multi-point for each entity, total of 5 entities
     * test: the entity 1 will be in the center of the 5 entities,
     * other 4 entities will be 90 degrees apart from the center starts at 45 degrees
     * east from the north and 80 km away from the center  
     * test geo query on filter by cirle - pick up all points within a circle 
     *  */
    public void addFeaturesToEntity2Test() throws CoalesceDataFormatException
    {
        GeometryFactory factory = new GeometryFactory();
        SearchCoordinates searchQueries = new SearchCoordinates();
        ArrayList<Coordinate> pointCollection = new ArrayList<>(); 
        Map<Double, Coordinate> fileteredPoints = new TreeMap<>();
        //entity1
        CoalesceSearchTestEntity2 searchEntity1 = new CoalesceSearchTestEntity2();
        searchEntity1.initialize();
        
        Coordinate e1pt1 = new Coordinate(-77.455811, 38.944533);
        Coordinate e1pt2 = new Coordinate(-77.037722, 38.852083);
        Coordinate e1pt3 = new Coordinate(-76.668333, 39.175361);

        Coordinate e1mp_1 = new Coordinate(-77.455811, 38.944533);
        Coordinate e1mp_2 = new Coordinate(-77.037722, 38.852083);
        Coordinate e1mp_3 = new Coordinate(-76.668333, 39.175361);
        //add 3 points
        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt1));
        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt2));
        searchEntity1.getGeocoordinatePointData().setValue(factory.createPoint(e1pt3));
        searchEntity1.addPointRecord();
        //add 1 multi-points
        searchEntity1.getGeoMultiPointData().setValue(new Coordinate[] {e1mp_1, e1mp_2, e1mp_3});
        searchEntity1.addMultiPointRecord();
        //add 3 cirles
        Double ent1Radius=100000.0;
        searchEntity1.getCirclegeomeryData().setValue(factory.createPoint(e1pt1), ent1Radius);
        searchEntity1.getCirclegeomeryData().setValue(factory.createPoint(e1pt2), ent1Radius);
        searchEntity1.getCirclegeomeryData().setValue(factory.createPoint(e1pt3), ent1Radius);
        searchEntity1.addCircleRecord();
        //add 3 lines
        searchEntity1.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e1mp_1, e1mp_2, e1mp_3}));
        searchEntity1.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e1mp_2, e1mp_3, e1mp_1}));
        searchEntity1.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e1mp_3, e1mp_1, e1mp_2}));
        searchEntity1.addLineRecord();

        //System.out.println(searchEntity1.toXml());
        pointCollection.add(e1pt1);
        pointCollection.add(e1pt2);
        pointCollection.add(e1pt3);
        
        //entity2
        CoalesceSearchTestEntity2 searchEntity2 = new CoalesceSearchTestEntity2();
        searchEntity2.initialize();

        double nebearing = 45.0;//degrees
        double range1to2 = 80000.0;//meter
        SearchCoordinates entity1point =  new SearchCoordinates(e1pt1);
        SearchCoordinates entity2point = entity1point.getTarget(range1to2, nebearing);

        Coordinate e2pt1 = new Coordinate(entity2point.getLongitude(), entity2point.getLatitude());
        Coordinate e2pt2 = new Coordinate(e2pt1.x +0.2, e2pt1.y+0.1);
        Coordinate e2pt3 = new Coordinate(e2pt2.x +0.2, e2pt2.y+0.1);

        Coordinate e2mp_1 = new Coordinate(e2pt1.x, e2pt1.y);
        Coordinate e2mp_2 = new Coordinate(e2mp_1.x +0.4, e2mp_1.y+0.3);
        Coordinate e2mp_3 = new Coordinate(e2mp_2.x +0.4, e2mp_2.y+0.3);

        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt1));
        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt2));
        searchEntity2.getGeocoordinatePointData().setValue(factory.createPoint(e2pt3));
        searchEntity2.addPointRecord();
        
        searchEntity2.getGeoMultiPointData().setValue(new Coordinate[] {e2mp_1, e2mp_2, e2mp_3});
        searchEntity2.addMultiPointRecord();

        //add 3 cirles
        Double ent2Radius=100000.0;
        searchEntity2.getCirclegeomeryData().setValue(factory.createPoint(e2pt1), ent2Radius);
        searchEntity2.getCirclegeomeryData().setValue(factory.createPoint(e2pt2), ent2Radius);
        searchEntity2.getCirclegeomeryData().setValue(factory.createPoint(e2pt3), ent2Radius);
        searchEntity2.addCircleRecord();
        //add 3 lines
        searchEntity2.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e2mp_1, e2mp_2, e2mp_3}));
        searchEntity2.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e2mp_2, e2mp_3, e2mp_1}));
        searchEntity2.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e2mp_3, e2mp_1, e2mp_2}));
        searchEntity2.addLineRecord();

        //System.out.println(searchEntity2.toXml());
        
        pointCollection.add(e2pt1);
        pointCollection.add(e2pt2);
        pointCollection.add(e2pt3);

        //check the geo queries between entity1 and entity2
        double distance1to2 = entity1point.distanceTo(entity2point);
        assertEquals(range1to2, distance1to2, 0.01);
        
        double bearing1to2 = entity1point.getBearingAsDouble(entity2point);
        assertEquals(nebearing, bearing1to2, 0.01);

        

        //entity3
        CoalesceSearchTestEntity2 searchEntity3 = new CoalesceSearchTestEntity2();
        searchEntity3.initialize();
        
        double sebearing = 135.0;//degrees
        double range1to3 = 80000.0;//meter
        SearchCoordinates entity3point = entity1point.getTarget(range1to3, sebearing);

        Coordinate e3pt1 = new Coordinate(entity3point.getLongitude(), entity3point.getLatitude());
        Coordinate e3pt2 = new Coordinate(e3pt1.x +0.2, e3pt1.y+0.1);
        Coordinate e3pt3 = new Coordinate(e3pt2.x +0.2, e3pt2.y+0.1);

        Coordinate e3mp_1 = new Coordinate(e3pt1.x, e3pt1.y);
        Coordinate e3mp_2 = new Coordinate(e3mp_1.x +0.4, e3mp_1.y+0.3);
        Coordinate e3mp_3 = new Coordinate(e3mp_2.x +0.4, e3mp_2.y+0.3);

        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt1));
        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt2));
        searchEntity3.getGeocoordinatePointData().setValue(factory.createPoint(e3pt3));
        searchEntity3.addPointRecord();
        
        searchEntity3.getGeoMultiPointData().setValue(new Coordinate[] {e3mp_1, e3mp_2, e3mp_3});
        searchEntity3.addMultiPointRecord();

        //add 3 cirles
        Double ent3Radius=100000.0;
        searchEntity3.getCirclegeomeryData().setValue(factory.createPoint(e3pt1), ent3Radius);
        searchEntity3.getCirclegeomeryData().setValue(factory.createPoint(e3pt2), ent3Radius);
        searchEntity3.getCirclegeomeryData().setValue(factory.createPoint(e3pt3), ent3Radius);
        searchEntity3.addCircleRecord();
        //add 3 lines
        searchEntity3.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e3mp_1, e3mp_2, e3mp_3}));
        searchEntity3.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e3mp_2, e3mp_3, e3mp_1}));
        searchEntity3.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e3mp_3, e3mp_1, e3mp_2}));
        searchEntity3.addLineRecord();

        //System.out.println(searchEntity3.toXml());
        
        pointCollection.add(e3pt1);
        pointCollection.add(e3pt2);
        pointCollection.add(e3pt3);

        //check the geo queries between entity1 and entity3
        double distance1to3 = entity1point.distanceTo(entity3point);
        assertEquals(range1to3, distance1to3, 0.01);
        
        double bearing1to3 = entity1point.getBearingAsDouble(entity3point);
        assertEquals(sebearing, bearing1to3, 0.01);

        //entity4
        CoalesceSearchTestEntity2 searchEntity4 = new CoalesceSearchTestEntity2();
        searchEntity4.initialize();
        
        double swbearing = 225.0;//degrees
        double range1to4 = 80000.0;//meter
        SearchCoordinates entity4point = entity1point.getTarget(range1to4, swbearing);

        Coordinate e4pt1 = new Coordinate(entity4point.getLongitude(), entity4point.getLatitude());
        Coordinate e4pt2 = new Coordinate(e4pt1.x +0.2, e4pt1.y+0.1);
        Coordinate e4pt3 = new Coordinate(e4pt2.x +0.2, e4pt2.y+0.1);

        Coordinate e4mp_1 = new Coordinate(e4pt1.x, e4pt1.y);
        Coordinate e4mp_2 = new Coordinate(e4mp_1.x +0.4, e4mp_1.y+0.3);
        Coordinate e4mp_3 = new Coordinate(e4mp_2.x +0.4, e4mp_2.y+0.3);

        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt1));
        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt2));
        searchEntity4.getGeocoordinatePointData().setValue(factory.createPoint(e4pt3));
        searchEntity4.addPointRecord();
        
        searchEntity4.getGeoMultiPointData().setValue(new Coordinate[] {e4mp_1, e4mp_2, e4mp_3});
        searchEntity4.addMultiPointRecord();

        //add 3 cirles
        Double ent4Radius=100000.0;
        searchEntity4.getCirclegeomeryData().setValue(factory.createPoint(e4pt1), ent4Radius);
        searchEntity4.getCirclegeomeryData().setValue(factory.createPoint(e4pt2), ent4Radius);
        searchEntity4.getCirclegeomeryData().setValue(factory.createPoint(e4pt3), ent4Radius);
        searchEntity4.addCircleRecord();
        //add 3 lines
        searchEntity4.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e4mp_1, e4mp_2, e4mp_3}));
        searchEntity4.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e4mp_2, e4mp_3, e4mp_1}));
        searchEntity4.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e4mp_3, e4mp_1, e4mp_2}));
        searchEntity4.addLineRecord();

        pointCollection.add(e4pt1);
        pointCollection.add(e4pt2);
        pointCollection.add(e4pt3);

        //check the geo queries between entity1 and entity4
        double distance1to4 = entity1point.distanceTo(entity4point);
        assertEquals(range1to4, distance1to4, 0.01);
        
        double bearing1to4 = entity1point.getBearingAsDouble(entity4point);
        assertEquals(swbearing, bearing1to4, 0.01);

        
        //entity5
        CoalesceSearchTestEntity2 searchEntity5 = new CoalesceSearchTestEntity2();
        searchEntity5.initialize();
        
        double nwbearing = 315.0;//degrees
        double range1to5 = 80000.0;//meter
        SearchCoordinates entity5point = entity1point.getTarget(range1to5, nwbearing);

        Coordinate e5pt1 = new Coordinate(entity5point.getLongitude(), entity5point.getLatitude());
        Coordinate e5pt2 = new Coordinate(e5pt1.x +0.2, e5pt1.y+0.1);
        Coordinate e5pt3 = new Coordinate(e5pt2.x +0.2, e5pt2.y+0.1);

        Coordinate e5mp_1 = new Coordinate(e5pt1.x, e5pt1.y);
        Coordinate e5mp_2 = new Coordinate(e5mp_1.x +0.4, e5mp_1.y+0.3);
        Coordinate e5mp_3 = new Coordinate(e5mp_2.x +0.4, e5mp_2.y+0.3);

        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt1));
        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt2));
        searchEntity5.getGeocoordinatePointData().setValue(factory.createPoint(e5pt3));
        searchEntity5.addPointRecord();
        
        searchEntity5.getGeoMultiPointData().setValue(new Coordinate[] {e5mp_1, e5mp_2, e5mp_3});
        searchEntity5.addMultiPointRecord();

        //add 3 cirles
        Double ent5Radius=100000.0;
        searchEntity5.getCirclegeomeryData().setValue(factory.createPoint(e5pt1), ent5Radius);
        searchEntity5.getCirclegeomeryData().setValue(factory.createPoint(e5pt2), ent5Radius);
        searchEntity5.getCirclegeomeryData().setValue(factory.createPoint(e5pt3), ent5Radius);
        searchEntity5.addCircleRecord();
        //add 3 lines
        searchEntity5.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e5mp_1, e5mp_2, e5mp_3}));
        searchEntity5.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e5mp_2, e5mp_3, e5mp_1}));
        searchEntity5.getLineGeometryData().setValue(factory.createLineString(new Coordinate[] {e5mp_3, e5mp_1, e5mp_2}));
        searchEntity5.addLineRecord();

        //System.out.println(searchEntity5.toXml());
        pointCollection.add(e5pt1);
        pointCollection.add(e5pt2);
        pointCollection.add(e5pt3);

        //check the geo queries between entity1 and entity5
        double distance1to5 = entity1point.distanceTo(entity5point);
        assertEquals(range1to5, distance1to5, 0.01);
        
        double bearing1to5 = entity1point.getBearingAsDouble(entity5point);
        assertEquals(nwbearing, bearing1to5, 0.01);

        fileteredPoints = searchQueries.locationFilterByCircle(e1pt1, ent1Radius, pointCollection);
        
        for(Map.Entry<Double, Coordinate> entry: fileteredPoints.entrySet()){
            //System.out.println(entry.getKey()+" - "+entry.getValue());
            assertTrue(entry.getKey() <=ent1Radius);
        }
        
    }
   
}

