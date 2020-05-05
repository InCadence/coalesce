/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.search.filter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
import org.joda.time.DateTime;
import org.opengis.temporal.Period;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This helper is used to simplify the filter creation process.
 *
 * @author n78554
 */
public final class FilterHelper {

    private FilterHelper()
    {
        // Do Nothing
    }

    /**
     * Creates a time period which is used with temporal filters such as during.
     *
     * @param start date-time
     * @param end date-time
     * @return a period
     */
    public static Period createTimePeriod(DateTime start, DateTime end)
    {
        return createTimePeriod(new Date(start.getMillis()), new Date(end.getMillis()));
    }

    /**
     * Creates a time period which is used with temporal filters such as during.
     *
     * @param start date-time
     * @param end date-time
     * @return a period
     */
    public static Period createTimePeriod(Date start, Date end)
    {
        DefaultInstant startInstant = new DefaultInstant(new DefaultPosition(start));
        DefaultInstant endInstant = new DefaultInstant(new DefaultPosition(end));

        // Create Filters
        return new DefaultPeriod(startInstant, endInstant);
    }

    /**
     * @param polygon   to have its vertices sorted
     * @param clockwise direction in which to sort the vertices.
     * @return a polygon with it's vertices sorted in the specified direction.
     */
    public static Polygon sortVertices(Polygon polygon, boolean clockwise)
    {
        ArrayList<Coordinate> coords = new ArrayList<>(Arrays.asList(polygon.getCoordinates()));

        // Open the polygon
        coords.remove(0);

        sortVertices(coords, clockwise);

        // Close the polygon
        coords.add(coords.get(0));

        return new GeometryFactory().createPolygon(coords.toArray(new Coordinate[0]));
    }

    /**
     * @param coords    list of coordinates to sort
     * @param clockwise direction in which to sort the vertices.
     */
    public static List<Coordinate> sortVertices(List<Coordinate> coords, boolean clockwise)
    {
        // Get Centroid
        Coordinate center = findCentroid(coords);

        // Sort Vertices
        coords.sort((a, b) -> {
            double a1 = (Math.toDegrees(Math.atan2(a.x - center.x, a.y - center.y)) + 360) % 360;
            double a2 = (Math.toDegrees(Math.atan2(b.x - center.x, b.y - center.y)) + 360) % 360;
            return (int) (clockwise ? (a1 - a2) : (a2 - a1));
        });

        return coords;
    }

    private static Coordinate findCentroid(List<Coordinate> coords)
    {
        int x = 0;
        int y = 0;

        for (Coordinate coord : coords)
        {
            x += coord.x;
            y += coord.y;
        }

        return new Coordinate(x / coords.size(), y / coords.size());
    }

}
