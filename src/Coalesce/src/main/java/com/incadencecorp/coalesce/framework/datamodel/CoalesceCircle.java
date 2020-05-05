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

package com.incadencecorp.coalesce.framework.datamodel;

import org.locationtech.jts.geom.Coordinate;

/**
 * Represents a circle.
 * 
 * @author n78554
 *
 */
public class CoalesceCircle {

    private Coordinate center;
    private double radius;

    /**
     * @return the coordinate of the center of the circle.
     */
    public Coordinate getCenter()
    {
        return center;
    }

    /**
     * Sets the coordinate of the center of the circle.
     * 
     * @param center
     */
    public void setCenter(Coordinate center)
    {
        this.center = center;
    }

    /**
     * @return the radius of the circle.
     */
    public double getRadius()
    {
        return radius;
    }

    /**
     * Sets the radius of the circle.
     * 
     * @param radius
     */
    public void setRadius(double radius)
    {
        this.radius = radius;
    }

}
