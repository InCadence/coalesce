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

package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;

/**
 * CoalesceField that represents a list of integers.
 */
public class CoalesceIntegerListField extends CoalesceListField<int[]> {

    @Override
    public int[] getValue() throws CoalesceDataFormatException
    {
        return getIntegerListValue();
    }

    @Override
    public void setValue(int[] values)
    {
        setTypedValue(values);
    }

    @Override
    public void addValues(int[] values)
    {
        addArray(ArrayHelper.toStringArray(values));
    }
}
