package com.incadencecorp.coalesce.framework.datamodel;


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

/**
 * Base implementation for enumerated fields that defines common logic.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class CoalesceEnumerationFieldBase<T> extends CoalesceField<T> {

    /**
     * @return the name of the enumeration this field is tied to.
     */
    public String getEnumerationName()
    {
        String result = null;

        for (CoalesceConstraint constraint : getFieldDefinition().getConstraints())
        {
            if (constraint.getConstraintType() == ConstraintType.ENUMERATION)
            {
                result = constraint.getValue();
            }
        }

        return result;
    }
}
