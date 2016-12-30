package com.incadencecorp.coalesce.framework.persistance;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.vividsolutions.jts.geom.Coordinate;

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
 * Jun 27, 2016
 */


/**
 * 
 */
public class SearchCoordinates {
    
    public static final double Re = 6371000; //earth radius in meters
    
    private double latitude;
    private double longitude;
    private double elevation;
   
    /**
     * 
     */
    public SearchCoordinates(){
      
    }

    /**
     * @param Coordinate
     */
    public SearchCoordinates(Coordinate coord)
    {
        latitude = coord.y;
        longitude = coord.x; 
    }

    public SearchCoordinates(double lat, double lon)
    {
        latitude = lat;
        longitude = lon;
        elevation = 0.0;
    }
    
    // take string params (decimal degrees)
    public SearchCoordinates(String lat, String lon, String elev)
    {
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lon);
        elevation = Double.parseDouble(elev);
    }
    
    // take string params (decimal degrees)
    public SearchCoordinates(String lat, String lon)
    {
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lon);
        elevation = 0.0;
    }
    
    /**
     * Constructor using an array of doubles
     * @param location array ordered long, lat
     */
    public SearchCoordinates(Double location[])
    {
        latitude = location[1];
        longitude = location[0];
        elevation = 0.0;
    }
    


    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double distanceTo(SearchCoordinates coord)
    {
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(coord.latitude);
        double lon2 = Math.toRadians(coord.longitude);

        double dist = ( Re * Math.acos(Math.cos(lat2) * Math.cos(lat1) * Math.cos(lon1 - lon2) + Math.sin(lat2) * Math.sin(lat1)));
        return dist;
    }

    /**
     * Assumes a SPHERICAL earth
     * @param range distance to target in meters
     * @param bearing Degrees east of north
     * @return The location indicated by the specified range and bearing
     */
    public SearchCoordinates getTarget( double range, double bearing )
    {
        double a = range / Re;
        double c = 0.5*Math.PI - Math.toRadians(this.latitude);
        double beta = Math.toRadians(bearing);
        
        double b = Math.acos( Math.cos(a)*Math.cos(c) + Math.sin(a)*Math.sin(c)*Math.cos(beta) );

        double lat = Math.toDegrees(0.5*Math.PI - b);
        
        double alpha = Math.asin( Math.sin(a)*Math.sin(beta) / Math.sin(b) );
        double lon = this.longitude + Math.toDegrees(alpha);
        
        SearchCoordinates target = new SearchCoordinates(lat,lon);
        return target;
    }
    
    
    /**
     * Assumes a SPHERICAL earth
     * @param coord
     * @return the bearing to another coordinate as a double
     */
    public Double getBearingAsDouble(SearchCoordinates coord)
    {
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(coord.latitude);
        double lon2 = Math.toRadians(coord.longitude);
        double dlon = lon2-lon1;

        double y = Math.sin(dlon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dlon);
        Double bearing = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

        return bearing;
    }
    
    /**
     * search/filter all points within a circle
     * @param center point of a circle
     * @param radius of a circle
     * @param collection of the points in Coordinate
     * @return a sorted map, the shortest distance on the top,
     *      the distance in meters as the key, the location point in coordinate as the value
     */
    public Map<Double, Coordinate> locationFilterByCircle(Coordinate center, Double radius, ArrayList<Coordinate> collection){
        Map<Double, Coordinate> fileteredPoints = new TreeMap<>();
        SearchCoordinates centerPoint =  new SearchCoordinates(center);
        if(collection!=null){
            
            for(Coordinate point: collection){
                SearchCoordinates thisPoint = new SearchCoordinates(point);
                double distance = centerPoint.distanceTo(thisPoint);
                if(distance<=radius){
                    Coordinate fltpoint = new Coordinate(thisPoint.getLongitude(), thisPoint.getLatitude(), 0.0);
                    fileteredPoints.put(distance, fltpoint);
                }
            }
            return (TreeMap<Double, Coordinate>) fileteredPoints;
        }
        return null;
    }
    
    /**
     * search query to check if a line and a circle is intersected
     * @param center: center point of a circle
     * @param radius: radius of a circle 
     * @param linePoints: a collection of line points
     * @return boolean: true or false
     */
    public boolean isIntersected(Coordinate center, Double radius, ArrayList<Coordinate> linePoints){
        int inCircleCount = 0, outCircleCount = 0;
        SearchCoordinates centerPoint =  new SearchCoordinates(center);
        if(linePoints!=null){
            
            for(Coordinate point: linePoints){
                SearchCoordinates thisPoint = new SearchCoordinates(point);
                double distance = centerPoint.distanceTo(thisPoint);
                if(distance<=radius){
                    inCircleCount++;
                }
                else{
                    outCircleCount++;
                }
            }
            if(inCircleCount>0 && outCircleCount>0){
                return true;
            }
        }
        return false;
    }
}

