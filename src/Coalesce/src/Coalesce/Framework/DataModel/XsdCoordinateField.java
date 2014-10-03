/**
 * 
 */
package Coalesce.Framework.DataModel;

import Coalesce.Common.Exceptions.CoalesceDataFormatException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

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
 *
 */
public class XsdCoordinateField extends XsdField<Coordinate> {

    @Override
    public Coordinate getValue() throws CoalesceDataFormatException
    {
        return getCoordinateValue();
    }

    public Point getValueAsPoint() throws CoalesceDataFormatException
    {
        return getPointValue();
    }
    
    @Override
    public void setValue(Coordinate value) throws CoalesceDataFormatException
    {
        setTypedValue(value);
    }

    public void setValue(Point value) throws CoalesceDataFormatException
    {
        setTypedValue(value);
    }

}