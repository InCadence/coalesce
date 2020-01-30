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

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import org.locationtech.jts.geom.Point;

/**
 * 
 * @author n78554
 *
 */
public class CoalesceCircleField extends CoalesceField<CoalesceCircle> {

    /**
     * Attribute used to specify the circle's radius. The center is stored as
     * the base value.
     */
    public static final String ATTRIBUTE_RADIUS = "radius";

    @Override
    public CoalesceCircle getValue() throws CoalesceDataFormatException
    {

        return getCircleValue();
    }

    @Override
    public void setValue(CoalesceCircle value) throws CoalesceDataFormatException
    {
        setTypedValue(value);
    }

    /**
     * Creates a circle.
     * 
     * @param center
     * @param radius
     * @throws CoalesceDataFormatException
     */
    public void setValue(Point center, double radius) throws CoalesceDataFormatException
    {
        CoalesceCircle value = new CoalesceCircle();
        value.setCenter(center.getCoordinate());
        value.setRadius(radius);

        setTypedValue(value);
    }

}
