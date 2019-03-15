package com.incadencecorp.coalesce.framework.datamodel;

import java.security.Principal;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
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
public class CoalesceEnumerationField extends CoalesceEnumerationFieldBase<Integer> {

    @Override
    public Integer getValue() throws CoalesceDataFormatException
    {
        return getIntegerValue();
    }

    /**
     * @return ordinal value converted to a String using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String getValueAsString() throws CoalesceDataFormatException
    {
        return getValueAsString(null);
    }

    /**
     * 
     * @param principal
     * @return ordinal value converted to a String using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String getValueAsString(Principal principal) throws CoalesceDataFormatException
    {
        String results = null;
        Integer ordinal = getIntegerValue();

        if (ordinal != null)
        {
            results = EnumerationProviderUtil.toString(principal, getEnumerationName(), ordinal);
        }

        return results;
    }

    /**
     * @param clazz
     * @return ordinal value converted to a String using
     *         {@link EnumerationProviderUtil}.
     */
    public <E extends Enum<E>> E getValueAsEnumeration(Class<E> clazz)
    {
        return getValueAsEnumeration(null, clazz);
    }

    /**
     * @param defaultValue
     * @return ordinal value converted to a String using
     *         {@link EnumerationProviderUtil}.
     */
    public <E extends Enum<E>> E getValueAsEnumeration(E defaultValue)
    {
        return getValueAsEnumeration(defaultValue, (Class<E>) defaultValue.getClass());
    }

    /**
     * 
     * @param defaultValue
     * @param clazz
     * @return ordinal value converted to a String using
     *         {@link EnumerationProviderUtil}.
     */
    private <E extends Enum<E>> E getValueAsEnumeration(E defaultValue, Class<E> clazz)
    {
        E result = defaultValue;
        Integer ordinal = null;

        try
        {
            ordinal = getValue();

            if (ordinal != null)
            {
                Enum<E>[] values = clazz.getEnumConstants();
                result = (E) values[ordinal];
            }

        }
        catch (CoalesceDataFormatException | IndexOutOfBoundsException e)
        {
            if (defaultValue == null)
            {
                if (ordinal != null)
                {
                    throw new RuntimeException(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION,
                                                             ordinal,
                                                             clazz.getName()));
                }
                else
                {
                    throw new RuntimeException(e);
                }
            }
            result = defaultValue;
        }

        return result;
    }

    @Override
    public void setValue(Integer value)
    {
        setTypedValue(value);
    }

    /**
     * Sets the ordinal value from the provided enumeration value.
     * 
     * @param value
     */
    public <E extends Enum<E>> void setValueAsEnumeration(E value)
    {
        if (value != null)
        {
            setTypedValue(value.ordinal());
        }
        else
        {
            setBaseValue(null);
        }
    }

    /**
     * Sets the ordinal value using {@link EnumerationProviderUtil}
     * 
     * @param value
     */
    public void setValueAsString(String value)
    {
        setValueAsString(null, value);
    }

    /**
     * Sets the ordinal value using {@link EnumerationProviderUtil}
     * 
     * @param principal
     * @param value
     */
    public void setValueAsString(Principal principal, String value)
    {
        if (!StringHelper.isNullOrEmpty(value))
        {
            setTypedValue(EnumerationProviderUtil.toPosition(principal, getEnumerationName(), value));
        }
        else
        {
            setTypedValue((Integer) null);
        }
    }

}
