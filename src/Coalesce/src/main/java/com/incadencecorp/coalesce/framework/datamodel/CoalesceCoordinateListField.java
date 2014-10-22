/**
 * 
 */
package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;

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
public class CoalesceCoordinateListField extends CoalesceField<Coordinate[]> {

    /*
     * (non-Javadoc)
     * 
     * @see com.incadencecorp.coalesce.framework.datamodel.CoalesceField#getValue()
     */
    @Override
    public Coordinate[] getValue() throws CoalesceDataFormatException
    {
        return getCoordinateListValue();
    }

    /**
     * Returns the MultiPoint coordinates for this CoalesceCoordinateListField.
     * 
     * @return
     *     possible object is
     *     {@link MultiPoint }
     * @throws CoalesceDataFormatException
     */
    public MultiPoint getValueAsMultiPoint() throws CoalesceDataFormatException
    {
        return getMultiPointValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.incadencecorp.coalesce.framework.datamodel.CoalesceField#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Coordinate[] value) throws CoalesceDataFormatException
    {
        setTypedValue(value);
    }
    
    /**
     * Sets the MultiPoint coordinates for this CoalesceCoordinateListField.
     * @param value
     *     allowed object is
     *     {@link MultiPoint }
     * @throws CoalesceDataFormatException
     */
    public void setValue(MultiPoint value) throws CoalesceDataFormatException
    {
        setTypedValue(value);
    }

}
