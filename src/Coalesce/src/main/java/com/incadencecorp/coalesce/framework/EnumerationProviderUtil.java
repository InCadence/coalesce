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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationFieldBase;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import com.incadencecorp.unity.common.factories.PropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This utility class allows for multiple implementations of
 * {@link IEnumerationProvider} to be used for defining enumerations used by the
 * {@link CoalesceEnumerationField}. If this utility is not initialized before
 * using it will call {@link EnumerationProviderUtil#initializeFromConnector()}.
 *
 * @author n78554
 */
public final class EnumerationProviderUtil {

    /**
     * Defines the environment variable that contains the location of the
     * configuration files.
     */
    public static final String ENV_CONFIG_LOCATION = "DSS_CONFIG_LOCATION";

    /**
     * Defines the filename used to load the default providers; if this utility
     * is not already initialized.
     */
    public static final String ENUMERATION_PROVIDERS_FILENAME = "enumeration-providers.properties";

    /**
     * Defines the filename used to load enumeration maps; if this utility is
     * not already initialized.
     */
    public static final String ENUMERATION_LOOKUPS_FILENAME = "enumeration-lookups.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationProviderUtil.class);

    private static final List<IEnumerationProvider> providers = new ArrayList<>();
    private static final Map<String, String> lookup = new ConcurrentHashMap<>();
    private static Principal defaultPrincipal;

    private static final Object SYNC_OBJECT = new Object();

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
        lookup.putAll(values);
    }

    /**
     * Populates providers with the given principal.
     *
     * @param principal
     */
    public static void populate(Principal principal)
    {
        for (IEnumerationProvider provider : providers)
        {
            provider.populate(principal);
        }
    }

    /**
     * Sets the principal to use if non are provided. If a SAML principal is
     * used keep in mind that tokens expire and the principal should be
     * refreshed before each call into this utility.
     *
     * @param principal
     */
    public static void setPrincipal(Principal principal)
    {
        defaultPrincipal = principal;
    }

    /**
     * @param name
     * @return whether a lookup entry was created for the given enumeration
     * name.
     */
    public static boolean hasLookup(String name)
    {
        return lookup.containsKey(name);
    }

    /**
     * Logs the lookup table if logging level is set to information or lower.
     */
    public static void logLookups()
    {
        LOGGER.info("Enumeration Maps:");

        for (Map.Entry<String, String> entry : lookup.entrySet())
        {
            String value = entry.getValue();
            String map;
            StringBuilder sb = new StringBuilder(" => (" + value + ")");

            while (lookup.containsKey(value))
            {
                value = lookup.get(value);
                sb.append(" => (" + value + ")");
            }

            if (LOGGER.isTraceEnabled())
            {
                map = sb.toString();
            }
            else
            {
                map = " => (" + value + ")";
            }

            try
            {
                LOGGER.info("\t({}) {} provided by: ({})",
                            entry.getKey(),
                            map,
                            getProvider(null, value).getClass().getSimpleName());
            }
            catch (IllegalArgumentException e)
            {
                LOGGER.error("\t({}) {} provided by: (NOT PROVIDED)", entry.getKey(), map);
            }

        }
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
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#toString(Principal, String, int)}
     * @throws IndexOutOfBoundsException
     */
    public static String toString(Principal principal, String enumeration, int value) throws IndexOutOfBoundsException
    {
        if (principal == null)
        {
            principal = defaultPrincipal;
        }

        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).toString(principal, enumeration, value);
    }

    /**
     * @param principal
     * @param enumeration
     * @param value
     * @param defaultValue
     * @return {@link #toString(Principal, String, int)}; Returns the default
     * value instead of an {@link IndexOutOfBoundsException} on failure.
     */
    public static String toString(Principal principal, String enumeration, int value, String defaultValue)
    {
        String result;

        try
        {
            result = toString(principal, enumeration, value);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e)
        {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Calls {@link IEnumerationProvider#toPosition(Principal, String, String)}
     * on the first provider that handles the given enumeration.
     *
     * @param principal
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#toPosition(Principal, String, String)}
     * @throws IndexOutOfBoundsException
     */
    public static int toPosition(Principal principal, String enumeration, String value) throws IndexOutOfBoundsException
    {
        if (principal == null)
        {
            principal = defaultPrincipal;
        }

        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).toPosition(principal, enumeration, value);
    }

    /**
     * @param principal
     * @param enumeration
     * @param value
     * @param defaultValue
     * @return {@link #toPosition(Principal, String, String)}; Returns the
     * default value instead of an {@link IndexOutOfBoundsException} on
     * failure.
     */
    public static int toPosition(Principal principal, String enumeration, String value, int defaultValue)
    {
        int result;

        try
        {
            result = toPosition(principal, enumeration, value);
        }
        catch (IndexOutOfBoundsException e)
        {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Calls {@link IEnumerationProvider#isValid(Principal, String, int)} on the
     * first provider that handles the given enumeration.
     *
     * @param principal
     * @param enumeration
     * @param value
     * @return {@link IEnumerationProvider#isValid(Principal, String, int)}
     */
    public static boolean isValid(Principal principal, String enumeration, int value)
    {
        if (principal == null)
        {
            principal = defaultPrincipal;
        }

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
        if (principal == null)
        {
            principal = defaultPrincipal;
        }

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
        if (principal == null)
        {
            principal = defaultPrincipal;
        }

        enumeration = lookupEnumeration(enumeration);

        return getProvider(principal, enumeration).getValues(principal, enumeration);
    }

    /**
     * @param clazz
     * @return the first instance of the specified provider if it exists;
     * otherwise <code>null</code>.
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

    /**
     * Initializes this utility using a {@link FilePropertyConnector} using the
     * environment variable {@link EnumerationProviderUtil#ENV_CONFIG_LOCATION}.
     */
    public static void initializeFromConnector()
    {
        initializeFromConnector(new FilePropertyConnector(System.getenv(ENV_CONFIG_LOCATION)));
    }

    /**
     * Initializes this utility using the specified connector.
     *
     * @param connector
     */
    public static void initializeFromConnector(IConfigurationsConnector connector)
    {
        providers.clear();
        lookup.clear();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Loading Default Provider(s)");
        }

        try
        {
            PropertyFactory factory = new PropertyFactory(connector);

            // Load Enumeration Mappings
            lookup.putAll(factory.getProperties(ENUMERATION_LOOKUPS_FILENAME));

            Map<String, String> properties = factory.getProperties(ENUMERATION_PROVIDERS_FILENAME);

            HashSet<Integer> keys = new HashSet<>();

            // Re-Order Based on Numeric Key
            for (String key : properties.keySet())
            {
                keys.add(Integer.parseInt(key));
            }

            // Load Providers
            for (Integer key : keys)
            {
                String classname = properties.get(key.toString());

                if (!StringHelper.isNullOrEmpty(classname))
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Loading Provider: ({})", classname);
                    }

                    Class<?> clazz = Class.forName(classname);
                    providers.add((IEnumerationProvider) clazz.newInstance());
                }
            }

            if (!isInitialized())
            {
                LOGGER.warn("Providers not found using: JavaEnumerationProviderImpl and ConstraintEnumerationProviderImpl");

                providers.add(new JavaEnumerationProviderImpl());
                providers.add(new ConstraintEnumerationProviderImpl());

                lookup.put("coalesceentity.status", ECoalesceObjectStatus.class.getName());
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NumberFormatException e)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("FAILED", e);
            }
        }

        if (!isInitialized())
        {
            throw new IllegalStateException(String.format(CoalesceErrors.NOT_INITIALIZED, "Enumeration Providers"));
        }
    }

    /**
     * Looks up an enumeration from the enumeration map.
     *
     * @param enumeration
     * @return the enumeration used to retrieve values.
     */
    public static String lookupEnumeration(String enumeration)
    {
        synchronized (SYNC_OBJECT)
        {
            if (!isInitialized())
            {
                initializeFromConnector();
            }
        }

        while (lookup.containsKey(enumeration))
        {
            enumeration = lookup.get(enumeration);
        }

        return enumeration;
    }

    /**
     * Looks up an enumeration from a provided field.
     *
     * @param field
     * @return the enumeration used to retrieve values.
     */
    public static String lookupEnumeration(CoalesceField<?> field)
    {
        String enumeration;

        if (field instanceof CoalesceEnumerationFieldBase)
        {
            enumeration = ((CoalesceEnumerationFieldBase<?>) field).getEnumerationName();
        }
        else
        {
            enumeration = String.format("%s.%s", field.getParent().getParent().getName(), field.getName());
        }

        return lookupEnumeration(enumeration);
    }

    /*
     * -----------------------------------------------------------------------
     * Private Methods
     * -----------------------------------------------------------------------
     */

    private static IEnumerationProvider getProvider(Principal principal, String enumeration)
    {
        synchronized (SYNC_OBJECT)
        {
            if (!isInitialized())
            {
                initializeFromConnector();
            }
        }

        if (enumeration != null)
        {
            for (IEnumerationProvider provider : providers)
            {
                // Handles Enumeration?
                if (provider != null && provider.handles(principal, enumeration))
                {
                    return provider;
                }
            }
        }

        // No Suitable Provider Found
        throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_ENUMERATION, enumeration));
    }

}
