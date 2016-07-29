package com.incadencecorp.coalesce.framework.datamodel;

import java.security.Principal;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;

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
 * Represents a field that contains an Enumeration
 * 
 * @author n78554
 */
public class CoalesceEnumerationListField extends CoalesceEnumerationFieldBase<int[]> {

    @Override
    public int[] getValue() throws CoalesceDataFormatException
    {
        return getIntegerListValue();
    }

    /**
     * 
     * @param principal
     * @return converts the ordinal values into an array of Strings using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String[] getValueAsString(Principal principal) throws CoalesceDataFormatException
    {
        int values[] = getValue();
        String results[] = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = EnumerationProviderUtil.toString(principal, getEnumerationName(), values[ii]);
        }

        return results;
    }

    @Override
    public void setValue(int[] value)
    {
        setTypedValue(value);
    }

    /**
     * Sets the ordinal value using {@link EnumerationProviderUtil}
     * 
     * @param principal
     * @param values
     */
    public void setValueAsString(Principal principal, String[] values)
    {
        if (values != null)
        {
            int results[] = new int[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = EnumerationProviderUtil.toPosition(principal, getEnumerationName(), values[ii]);
            }

            setTypedValue(results);
        }
        else
        {
            setTypedValue((int[]) null);
        }
    }

}
