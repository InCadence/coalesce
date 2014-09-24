package Coalesce.Framework.Geography;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Coalesce.Common.Exceptions.CoalesceDataFormatException;
import Coalesce.Common.Helpers.StringHelper;

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

public class Geolocation {

    // -----------------------------------------------------------------------//
    // Private Member Variables
    // -----------------------------------------------------------------------//

    private static final String PARSE_ERROR_MESSAGE = "fromXml must be in the form of '[POINT (double double)|MULTIPOINT ((double double) ...)] '";

    private double _latitude;
    private double _longitude;

    // -----------------------------------------------------------------------//
    // Constructors
    // -----------------------------------------------------------------------//

    public Geolocation(double latitude, double longitude) throws IllegalArgumentException
    {
        if (Math.abs(latitude) > 90) throw new IllegalArgumentException("Latitude values must be between -90 and 90 degrees.");
        if (Math.abs(longitude) > 90) throw new IllegalArgumentException("Longitude values must be between -90 and 90 degrees.");

        _latitude = latitude;
        _longitude = longitude;
    }

    public Geolocation()
    {
        // Use default values
        _latitude = 0;
        _longitude = 0;
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    public double getLatitude()
    {
        return _latitude;
    }

    public double getLongitude()
    {
        return _longitude;
    }

    @Override
    public String toString()
    {
        return "POINT (" + formatDouble(_longitude) + " " + formatDouble(_latitude) + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Geolocation)) return false;

        Geolocation otherLocation = (Geolocation) other;

        return (_latitude == otherLocation.getLatitude() && _longitude == otherLocation.getLongitude());
    }

    public static List<Geolocation> parseGeolocation(String fromXml) throws CoalesceDataFormatException
    {
        // MULTIPOINT ((-70.6280916 34.6873833), (-77.056138 38.87116))
        // POINT (-70.6280916 34.6873833)

        if (StringHelper.IsNullOrEmpty(fromXml)) return null;

        if (fromXml.endsWith(")"))
        {

            if (fromXml.startsWith("POINT ("))
            {
                Geolocation location = parsePointGeolocation(fromXml.replace("POINT ", ""));
                
                if (location == null) return null;
                
                return Arrays.asList(location);

            }
            else if (fromXml.startsWith("MULTIPOINT ("))
            {
                return parseMultiPointGeolocation(fromXml);
            }
        }

        throw new CoalesceDataFormatException(Geolocation.PARSE_ERROR_MESSAGE);

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private String formatDouble(double value)
    {
        if (value == (long) value)
            return String.format("%d", (long) value);
        else
            return String.format("%s", value);
    }

    private static Geolocation parsePointGeolocation(String pointXml) throws CoalesceDataFormatException
    {
        String[] points = pointXml.replace("(", "").replace(")", "").split(" ");

        if (points.length != 2)
        {
            throw new CoalesceDataFormatException(Geolocation.PARSE_ERROR_MESSAGE);
        }

        try
        {
            double longitude = Double.parseDouble(points[0]);
            double latitude = Double.parseDouble(points[1]);

            Geolocation location = new Geolocation(latitude, longitude);

            return location;

        }
        catch (NumberFormatException nfe)
        {
            throw new CoalesceDataFormatException(Geolocation.PARSE_ERROR_MESSAGE);
        }

    }

    private static List<Geolocation> parseMultiPointGeolocation(String fromXml) throws CoalesceDataFormatException
    {
        String[] pointParts = fromXml.replace("MULTIPOINT (", "").replace(")$", "").split(", ");

        List<Geolocation> locations = new ArrayList<Geolocation>();
        for (String pointPart : pointParts)
        {
            if (pointPart.startsWith("(") && pointPart.endsWith(")"))
            {
                locations.add(parsePointGeolocation(pointPart));

            }
            else
            {
                throw new CoalesceDataFormatException(Geolocation.PARSE_ERROR_MESSAGE);
            }
        }

        return locations;

    }
}
