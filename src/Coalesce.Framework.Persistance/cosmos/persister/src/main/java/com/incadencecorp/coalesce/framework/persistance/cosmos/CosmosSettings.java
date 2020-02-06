/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for the Cosmos persistor implementation.
 *
 * @author Derek Clemenzi
 */
public class CosmosSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static String config_name = "cosmos-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "com.incadence.persister.cosmos.";
    /**
     * (String) Hostname
     */
    public static final String PARAM_HOST = PARAM_BASE + "host";
    /**
     * (String) Master key for authentication
     */
    public static final String PARAM_KEY = PARAM_BASE + "key";
    /**
     * (Boolean) Specifies whether or not this persister is authoritative meaning it can be used to READ entities.
     */
    public static final String PARAM_IS_AUTHORITATIVE = PARAM_BASE + "isAuthoritative";

    /**
     * (String) Name of the database; defaults to {@value DEFAULT_DATABASE_NAME}.
     */
    public static final String PARAM_DATABASE_NAME = PARAM_BASE + "name";

    /**
     * (String) Prefix to use when creating collections; defaults to {@value DEFAULT_DATABASE_NAME}.
     */
    public static final String PARAM_COLLECTION_PREFIX = PARAM_BASE + "prefix";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_IS_AUTHORITATIVE = "true";

    // The default values are credentials of the local emulator, which are not used in any production environment.
    private static final String DEFAULT_HOST = "https://localhost:8081/";
    private static final String DEFAULT_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";
    private static final String DEFAULT_DATABASE_NAME = "coalesce";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private CosmosSettings()
    {
        // Do Nothing
    }

    /**
     * Configures the settings to use a particular connector.
     *
     * @param connector to use for settings
     */
    public static void setConnector(final IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);
    }

    /**
     * Configures the settings to use a particular connector and property name.
     *
     * @param connector to use for settings
     * @param name      of configuration file
     */
    public static void setConnector(final IConfigurationsConnector connector, final String name)
    {
        config_name = name;
        settings = new SettingsBase(connector);
    }

    /*--------------------------------------------------------------------------
    Settings
    --------------------------------------------------------------------------*/

    /**
     * @return the host name; defaults to {@value DEFAULT_HOST} which is the emulator.
     */
    public static String getHost()
    {
        return settings.getSetting(config_name, PARAM_HOST, DEFAULT_HOST, false);
    }

    /**
     * Sets the host name.
     */
    public static void setHost(String value)
    {
        settings.setSetting(config_name, PARAM_HOST, value);
    }

    /**
     * @return the master key; defaults to {@value DEFAULT_KEY} which is the emulator.
     */
    public static String getMasterKey()
    {
        return settings.getSetting(config_name, PARAM_KEY, DEFAULT_KEY, false);
    }

    /**
     * Sets the master key.
     */
    public static void setMasterKey(String value)
    {
        settings.setSetting(config_name, PARAM_KEY, value);
    }

    /**
     * @return whether or not to store entity's XML.
     */
    public static boolean isAuthoritative()
    {
        return Boolean.parseBoolean(settings.getSetting(config_name,
                                                        PARAM_IS_AUTHORITATIVE,
                                                        DEFAULT_IS_AUTHORITATIVE,
                                                        false));
    }

    /**
     * @return the database's name.
     */
    public static String getDatabaseName()
    {
        return settings.getSetting(config_name, PARAM_DATABASE_NAME, DEFAULT_DATABASE_NAME, false);
    }

    /**
     * Set the name to use for the database.
     */
    public static void setDatabaseName(String value)
    {
        settings.setSetting(config_name, PARAM_DATABASE_NAME, value);
    }

    /**
     * @return the prefix to use when creating collections.
     */
    public static String getCollectionPrefix()
    {
        return settings.getSetting(config_name, PARAM_COLLECTION_PREFIX, DEFAULT_DATABASE_NAME, false);
    }

    /**
     * Sets the prefix to use when creating collections.
     */
    public static void setCollectionPrefix(String value)
    {
        settings.setSetting(config_name, PARAM_COLLECTION_PREFIX, value);
    }

    /**
     * Sets whether or not to store entity's XML.
     */
    public static void SetIsAuthoritative(boolean value)
    {
        settings.setSetting(config_name, PARAM_IS_AUTHORITATIVE, value);
    }

    public static Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(PARAM_IS_AUTHORITATIVE, Boolean.toString(isAuthoritative()));
        params.put(PARAM_HOST, getHost());
        params.put(PARAM_KEY, getMasterKey());

        return params;
    }

}
