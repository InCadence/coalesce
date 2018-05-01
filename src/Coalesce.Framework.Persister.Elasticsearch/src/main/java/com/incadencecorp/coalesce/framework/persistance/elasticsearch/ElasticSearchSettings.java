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

package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for ElasticSearch persister implementations.
 */
public class ElasticSearchSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static String config_name = "elasticsearch-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));
    private static Boolean connectorInitialized = false;

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_ELASTIC_BASE = "elastic.";
    private static final String PARAM_SSL_BASE = "ssl.";

    /**
     * (Boolean) Specifies whether or not this persister is authoritative meaning it can be used to READ entities.
     */
    public static final String PARAM_IS_AUTHORITATIVE = PARAM_ELASTIC_BASE + "isAuthoritative";
    /**
     * (Boolean) Specifies whether or not to use SSL.
     */
    public static final String PARAM_SSL_ENABLED = PARAM_SSL_BASE + "enabled";

    /**
     * (String) Defines the location the the key store.
     */
    public static final String PARAM_KEYSTORE_FILE = PARAM_SSL_BASE + "keystore";

    /**
     * (String) Keystore Password
     */
    public static final String PARAM_KEYSTORE_PASSWORD = PARAM_KEYSTORE_FILE + ".password";

    /**
     * (String) Defines the location the the trust store.
     */
    public static final String PARAM_TRUSTSTORE_FILE = PARAM_SSL_BASE + "truststore";

    /**
     * (String) Truststore Password
     */
    public static final String PARAM_TRUSTSTORE_PASSWORD = PARAM_TRUSTSTORE_FILE + ".password";

    /**
     * (String) Defines the ElasticSearch cluster name
     */
    public static final String PARAM_CLUSTER_NAME = PARAM_ELASTIC_BASE + "clustername";
    /**
     * (CSV) Defines the ElasticSearch host
     */
    public static final String PARAM_HOSTS = PARAM_ELASTIC_BASE + "hosts";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private ElasticSearchSettings()
    {
        // Do Nothing
    }

    /**
     * Configures the settings to use a particular connector.
     *
     * @param connector
     */
    public static void setConnector(final IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);

        connectorInitialized = true;
    }

    /**
     * Configures the settings to use a particular connector and property name.
     *
     * @param connector
     * @param name
     */
    public static void setConnector(final IConfigurationsConnector connector, final String name)
    {
        config_name = name;
        settings = new SettingsBase(connector);

        connectorInitialized = true;
    }

    public static Boolean getConnectorInitialized()
    {
        return connectorInitialized;
    }

    /*--------------------------------------------------------------------------
    Settings
    --------------------------------------------------------------------------*/

    public static String getElastichosts()
    {
        return settings.getSetting(config_name, PARAM_HOSTS, "", false);
    }

    public static String getKeystoreFilepath()
    {
        return settings.getSetting(config_name, PARAM_KEYSTORE_FILE, "", false);
    }

    public static void setKeystoreFilepath(String keystoreFilepath)
    {
        settings.setSetting(config_name, PARAM_KEYSTORE_FILE, keystoreFilepath);
    }

    public static String getTruststoreFilepath()
    {
        return settings.getSetting(config_name, PARAM_TRUSTSTORE_FILE, "", false);
    }

    public static void setTruststoreFilepath(String truststoreFilepath)
    {
        settings.setSetting(config_name, PARAM_TRUSTSTORE_FILE, truststoreFilepath);
    }

    public static void setElasticClusterName(String clusterName)
    {
        settings.setSetting(config_name, PARAM_CLUSTER_NAME, clusterName);
    }

    public static String getElasticClusterName()
    {
        return settings.getSetting(config_name, PARAM_CLUSTER_NAME, "", false);
    }

    public static void setSSLEnabled(boolean value)
    {
        settings.setSetting(config_name, PARAM_SSL_ENABLED, value);
    }

    public static boolean isSSLEnabled()
    {
        return settings.getSetting(config_name, PARAM_SSL_ENABLED, false, false);
    }

    public static boolean isAuthoritative()
    {
        return settings.getSetting(config_name, PARAM_IS_AUTHORITATIVE, true, false);
    }

    public static void setIsAuthoritative(Boolean value)
    {
        settings.setSetting(config_name, PARAM_IS_AUTHORITATIVE, value);
    }

    public static Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(PARAM_IS_AUTHORITATIVE, Boolean.toString(isAuthoritative()));
        params.put(PARAM_SSL_ENABLED, Boolean.toString(isSSLEnabled()));
        params.put(PARAM_KEYSTORE_FILE, getKeystoreFilepath());
        params.put(PARAM_KEYSTORE_PASSWORD, "changeit");
        params.put(PARAM_TRUSTSTORE_FILE, getTruststoreFilepath());
        params.put(PARAM_TRUSTSTORE_PASSWORD, "changeit");
        params.put(PARAM_HOSTS, getElastichosts());
        params.put(PARAM_CLUSTER_NAME, getElasticClusterName());

        return params;
    }

}
