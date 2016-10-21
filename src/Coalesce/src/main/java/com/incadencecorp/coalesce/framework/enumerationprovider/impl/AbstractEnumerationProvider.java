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

package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.IEnumerationProvider;

/**
 * Abstract implementation that implements common functionality between
 * providers.
 * 
 * @author Derek
 *
 */
public abstract class AbstractEnumerationProvider implements IEnumerationProvider {

    private Map<String, List<String>> supported;

    /*
     * -----------------------------------------------------------------------
     * Constructors
     * -----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    public AbstractEnumerationProvider()
    {
        supported = new HashMap<String, List<String>>();
    }

    /*
     * -----------------------------------------------------------------------
     * Override Methods
     * -----------------------------------------------------------------------
     */

    @Override
    public boolean handles(Principal principal, String enumeration)
    {
        return getEnumerationValues(principal, enumeration, true) != null;
    }

    @Override
    public String toString(Principal principal, String enumeration, int value) throws IndexOutOfBoundsException
    {
        List<String> values = getEnumerationValues(principal, enumeration, false);
        
        if (value >= values.size()) {
            throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, value, enumeration));
        }

        return values.get(value);
    }

    @Override
    public int toPosition(Principal principal, String enumeration, String value) throws IndexOutOfBoundsException
    {
        int result = getEnumerationValues(principal, enumeration, false).indexOf(value);

        if (result == -1)
        {
            throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, value, enumeration));
        }

        return result;
    }

    @Override
    public boolean isValid(Principal principal, String enumeration, int value)
    {
        return value >= 0 && value < getEnumerationValues(principal, enumeration, false).size();
    }

    @Override
    public boolean isValid(Principal principal, String enumeration, String value)
    {
        return getEnumerationValues(principal, enumeration, false).contains(value);
    }

    @Override
    public List<String> getValues(Principal principal, String enumeration)
    {
        return getEnumerationValues(principal, enumeration, false);
    }

    /*
     * -----------------------------------------------------------------------
     * Protected Methods
     * -----------------------------------------------------------------------
     */

    /**
     * Attempt to find the missing enumeration.
     * 
     * @param principal
     * @param enumeration
     * @return a list of values if successful; otherwise <code>null</code>.
     */
    protected abstract List<String> lookup(Principal principal, String enumeration);

    /**
     * Adds the enumeration to the supported list.
     * 
     * @param enumeration
     * @param values
     */
    protected void addEnumeration(Principal principal, String enumeration, List<String> values)
    {
        supported.put(enumeration, values);
    }

    /*
     * -----------------------------------------------------------------------
     * Private Methods
     * -----------------------------------------------------------------------
     */
    
    private List<String> getEnumerationValues(Principal principal, String enumeration, boolean allowNullResult)
    {
        if (!supported.containsKey(enumeration))
        {
            supported.put(enumeration, lookup(principal, enumeration));
        }

        List<String> values = supported.get(enumeration);
        
        if (!allowNullResult && values == null) {
            throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_ENUMERATION, enumeration));
        }
        
        return values;
    }

}
