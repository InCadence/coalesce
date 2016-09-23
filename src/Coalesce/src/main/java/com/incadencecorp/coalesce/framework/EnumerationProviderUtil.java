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

package com.incadencecorp.coalesce.framework;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;

/**
 * This utility class allows for multiple implementations of
 * {@link IEnumerationProvider} to be used for defining enumerations used by the
 * {@link CoalesceEnumerationField}.
 * 
 * @author n78554
 *
 */
public final class EnumerationProviderUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationProviderUtil.class);

    private static final List<IEnumerationProvider> providers = new ArrayList<IEnumerationProvider>();
    private static final Map<String, String> lookup = new HashMap<String, String>();

    /*
     * -----------------------------------------------------------------------
     * Initialization Methods
     * -----------------------------------------------------------------------
     */

    /**
     * Sets the providers to be used by the enumeration field types.
     * 
     * @param values
     */
    public static void setEnumerationProviders(IEnumerationProvider... values)
    {
        providers.clear();
        addEnumerationProviders(values);
    }

    /**
     * Adds providers to be used by the enumeration field types.
     * 
     * @param values
     */
    public static void addEnumerationProviders(IEnumerationProvider... values)
    {
        providers.addAll(Arrays.asList(values));
    }

    /**
     * Sets lookup entries to map enumeration names to a different value.
     * 
     * @param values
     */
    public static void setLookupEntries(Map<String, String> values)
    {
        lookup.clear();
        addLookupEntries(values);
    }

    /**
     * Adds lookup entries to map enumeration names to a different value.
     * 
     * @param values
     */
    public static void addLookupEntries(Map<String, String> values)
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Enumeration Maps:");

            for (Map.Entry<String, String> entry : values.entrySet())
            {
                LOGGER.info("\t{} => {}", entry.getKey(), entry.getValue());
            }
        }

        lookup.putAll(values);
    }

    /*
     * -----------------------------------------------------------------------
     * Utility Methods
     * -----------------------------------------------------------------------
     */

    /**
     * Calls {@link IEnumerationProvider#toString(Principal, String, int)} on
     * the first provider that handles the given enumeration.
     * 
     * @param principal
     * 
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#toString(Principal, String, int)}
     * @throws IndexOutOfBoundsException
     */
    public static String toString(Principal principal, String enumeration, int value) throws IndexOutOfBoundsException
    {
        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).toString(principal, enumeration, value);
    }

    /**
     * Calls {@link IEnumerationProvider#toPosition(Principal, String, String)}
     * on the first provider that handles the given enumeration.
     * 
     * @param principal
     * 
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#toPosition(Principal, String, String)}
     * @throws IndexOutOfBoundsException
     */
    public static int toPosition(Principal principal, String enumeration, String value) throws IndexOutOfBoundsException
    {
        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).toPosition(principal, enumeration, value);
    }

    /**
     * Calls {@link IEnumerationProvider#isValid(Principal, String, int)} on the
     * first provider that handles the given enumeration.
     * 
     * @param principal
     * 
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#isValid(Principal, String, int)}
     */
    public static boolean isValid(Principal principal, String enumeration, int value)
    {
        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).isValid(principal, enumeration, value);
    }

    /**
     * Calls {@link IEnumerationProvider#isValid(Principal, String, String)} on
     * the first provider that handles the given enumeration.
     * 
     * @param principal
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#isValid(Principal, String, String)}
     */
    public static boolean isValid(Principal principal, String enumeration, String value)
    {
        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).isValid(principal, enumeration, value);
    }

    /**
     * Calls {@link IEnumerationProvider#getValues(Principal, String)} on the
     * first provider that handles the given enumeration.
     * 
     * @param principal
     * @param enumeration
     * @return {@link IEnumerationProvider#getValues(Principal, String)}
     */
    public static List<String> getValues(Principal principal, String enumeration)
    {
        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).getValues(principal, enumeration);
    }

    /**
     * @param clazz
     * @return the first instance of the specified provider if it exists;
     *         otherwise <code>null</code>.
     */
    public static <E extends IEnumerationProvider> E getProvider(Class<E> clazz)
    {
        for (IEnumerationProvider provider : providers)
        {
            if (clazz.isInstance(provider))
            {
                return clazz.cast(provider);
            }
        }

        return null;
    }

    /**
     * @return whether this utility has been initialized.
     */
    public static boolean isInitialized()
    {
        return providers.size() > 0;
    }

    /*
     * -----------------------------------------------------------------------
     * Private Methods
     * -----------------------------------------------------------------------
     */

    private static String lookupEnumeration(String enumeration)
    {
        if (lookup.containsKey(enumeration))
        {
            enumeration = lookup.get(enumeration);
        }

        return enumeration;
    }

    private static IEnumerationProvider getProvider(Principal principal, String enumeration)
    {
        if (!isInitialized())
        {
            throw new IllegalStateException(String.format(CoalesceErrors.NOT_INITIALIZED, "Enumeration Providers"));
        }

        for (IEnumerationProvider provider : providers)
        {
            // Handles Enumeration?
            if (provider.handles(principal, enumeration))
            {
                return provider;
            }
        }

        // No Suitable Provider Found
        throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_ENUMERATION, enumeration));
    }

}
