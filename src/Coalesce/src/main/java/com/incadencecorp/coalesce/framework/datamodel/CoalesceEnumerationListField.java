package com.incadencecorp.coalesce.framework.datamodel;

import java.lang.reflect.Array;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
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

    /**
     * Defines the separator when returning a the enumerated list as a string.
     */
    public static final String SEPERATOR = " ";

    @Override
    public int[] getValue() throws CoalesceDataFormatException
    {
        return getIntegerListValue();
    }

    /**
     * Uses a null principal.
     * 
     * @return converts the ordinal values into an array of Strings using
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
     * @return converts the ordinal values into an array of Strings using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String getValueAsString(Principal principal) throws CoalesceDataFormatException
    {
        return StringUtils.join(getValueAsList(principal), SEPERATOR);
    }

    /**
     * Uses a null principal.
     * 
     * @return converts the ordinal values into an array of Strings using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String[] getValueAsList() throws CoalesceDataFormatException
    {
        return getValueAsList(null);
    }

    /**
     * 
     * @param principal
     * @return converts the ordinal values into an array of Strings using
     *         {@link EnumerationProviderUtil}.
     * @throws CoalesceDataFormatException
     */
    public String[] getValueAsList(Principal principal) throws CoalesceDataFormatException
    {
        int values[] = getValue();
        String results[] = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = EnumerationProviderUtil.toString(principal, getEnumerationName(), values[ii]);
        }

        return results;
    }

    /**
     * @param clazz
     * @return ordinal value(s) converted to an enumeration.
     */
    public <E extends Enum<E>> E[] getValueAsEnumeration(Class<E> clazz)
    {
        E results[];

        try
        {
            int oridinal[] = getValue();

            results = (E[]) Array.newInstance(clazz, oridinal.length);

            for (int ii = 0; ii < oridinal.length; ii++)
            {
                Enum<E>[] values = clazz.getEnumConstants();

                try
                {
                    results[ii] = (E) values[oridinal[ii]];
                }
                catch (IndexOutOfBoundsException e)
                {
                    throw new RuntimeException(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION,
                                                             oridinal[ii],
                                                             clazz.getName()));
                }
            }
        }
        catch (CoalesceDataFormatException | IndexOutOfBoundsException e)
        {
            throw new RuntimeException(e);
        }

        return results;
    }

    @Override
    public void setValue(int[] value)
    {
        setTypedValue(value);
    }

    /**
     * Adds additional values to the list
     * 
     * @param values
     * @throws CoalesceDataFormatException
     */
    public void addValue(int[] values) throws CoalesceDataFormatException
    {
        if (values != null)
        {
            int[] existing = getValue();
            int[] results = new int[existing.length + values.length];

            int ii = 0;

            // Copy Existing
            for (int jj = 0; jj < existing.length; jj++)
            {
                results[ii++] = existing[jj];
            }

            // Copy New
            for (int jj = 0; jj < values.length; jj++)
            {
                results[ii++] = values[jj];
            }

            setTypedValue(results);
        }
    }

    /**
     * Sets the ordinal value(s) using {@link EnumerationProviderUtil} with a
     * null principal.
     * 
     * @param values
     */
    public void setValueAsString(String values)
    {
        setValueAsString(null, values);
    }

    /**
     * Sets the ordinal value(s) using {@link EnumerationProviderUtil}
     * 
     * @param principal
     * @param values
     */
    public void setValueAsString(Principal principal, String values)
    {
        if (!StringHelper.isNullOrEmpty(values))
        {
            setValueAsList(principal, values.split(SEPERATOR));

        }
        else
        {
            setTypedValue((int[]) null);
        }
    }

    /**
     * Sets the ordinal value(s) using {@link EnumerationProviderUtil} with a
     * null principal.
     * 
     * @param values
     */
    public void setValueAsList(String[] values)
    {
        setValueAsList(null, values);
    }

    /**
     * Sets the ordinal value(s) using {@link EnumerationProviderUtil}
     * 
     * @param principal
     * @param values
     */
    public void setValueAsList(Principal principal, String[] values)
    {
        if (values != null && values.length != 0)
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

    /**
     * Sets the ordinal value(s) from the provided enumeration.
     * 
     * @param values
     */
    public <E extends Enum<E>> void setValueAsEnumeration(E[] values)
    {
        if (values != null)
        {
            int ordinals[] = new int[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                ordinals[ii] = values[ii].ordinal();
            }

            setTypedValue(ordinals);
        }
        else
        {
            setBaseValue(null);
        }
    }

    /**
     * Adds the ordinal value(s) using {@link EnumerationProviderUtil}
     * 
     * @param principal
     * @param values
     * @throws CoalesceDataFormatException
     */
    public void addValueAsString(Principal principal, String[] values) throws CoalesceDataFormatException
    {
        if (values != null)
        {
            int results[] = new int[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = EnumerationProviderUtil.toPosition(principal, getEnumerationName(), values[ii]);
            }

            addValue(results);
        }
    }

    /**
     * Adds the ordinal value(s) of the provided enumerations.
     * 
     * @param values
     * @throws CoalesceDataFormatException
     */
    public <E extends Enum<E>> void addValueAsEnumeration(E[] values) throws CoalesceDataFormatException
    {
        if (values != null)
        {
            int results[] = new int[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = values[ii].ordinal();
            }

            addValue(results);
        }
    }

}
