package Coalesce.Framework.Geography;

public class Geolocation {

    private double Latitude;
    private double Longitude;
    
    public Geolocation(double Lat, double Lng){
        Latitude = Lat;
        Longitude = Lng;
    }
    
    public Geolocation(){
        //Use default values
        Latitude = 0;
        Longitude = 0;
    }
    
    public double getLatitude(){
        return Latitude;
    }
    
    public double getLongitude(){
        return Longitude;
    }
    
//    public void setLatitude(double value){
//        Latitude = value;
//    }
//    
//    public void setLongitude(double value){
//        Longitude = value;
//    }
    
    @Override
    public String toString(){
//        Point2D Builder = Point2D();
//        Builder.SetSrid(4326); // WGS 84
//        Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.Point);
//        Builder.BeginFigure(Me.Latitude, Me.Longitude);
//        Builder.EndFigure();
//        Builder.EndGeography();
//        Builder.setLocation(Longitude, Latitude);
    
//        return Builder.toString();
        return "POINT (" + Double.toString(Longitude) + " " + Double.toString(Latitude) + ")";
    }
    
}
