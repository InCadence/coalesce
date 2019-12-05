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
     * Prefix used to identify index default settings.
     */
    public static final String PARAM_INDEX_SETTING_PREFIX = PARAM_ELASTIC_BASE + "index.settings.default.";

    /**
     * (Boolean) Specifies whether or not this persister is authoritative meaning it can be used to READ entities.
     */
    public static final String PARAM_IS_AUTHORITATIVE = PARAM_ELASTIC_BASE + "isAuthoritative";
    /**
     * (Boolean) Specifies whether or not to use SSL.
     */
    public static final String PARAM_SSL_ENABLED = PARAM_SSL_BASE + "enabled";

    /**
     * (Boolean) Specifies whether to validate server certificate (ignored if ssl_enabled=false)
     */
    public static final String PARAM_SSL_REJECT_UNAUTHORIZED = PARAM_SSL_BASE + "reject_unauthorized";

    /**
     * (String) Defines the location the the key store. Defaults to System Property javax.net.ssl.keyStore.
     */
    public static final String PARAM_KEYSTORE_FILE = PARAM_SSL_BASE + "keystore";

    /**
     * (String) Key store's Password. Defaults to System Property javax.net.ssl.keyStorePassword.
     */
    public static final String PARAM_KEYSTORE_PASSWORD = PARAM_KEYSTORE_FILE + ".password";

    /**
     * (String) Defines the location the the trust store. Defaults to System Property javax.net.ssl.trustStore.
     */
    public static final String PARAM_TRUSTSTORE_FILE = PARAM_SSL_BASE + "truststore";

    /**
     * (String) Trust store's Password. Defaults to System Property javax.net.ssl.trustStorePassword.
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

    /**
     * (String) Defines the host used for HTTP requests
     */
    public static final String PARAM_HTTP_HOST = PARAM_ELASTIC_BASE + "http.host";

    /**
     * (String) Defines the port used for HTTP requests
     */
    public static final String PARAM_HTTP_PORT = PARAM_ELASTIC_BASE + "http.port";

    /**
     * (Integer) Number of attempts to save an entity on a NoNodeAvailableExceptions before giving up.
     */
    public static final String PARAM_RETRY_ATTEMPTS = PARAM_ELASTIC_BASE + "onerror.retryattempts";

    /**
     * (Integer) Milliseconds range that back off logic should use before retrying.
     */
    public static final String PARAM_BACKOFF_INTERVAL = PARAM_ELASTIC_BASE + "onerror.backoffinterval";

    /**
     * (Boolean) Specifies whether or not the datastores should be cached for reused.
     */
    public static final String PARAM_DATASTORE_CACHE_ENABLED = PARAM_ELASTIC_BASE + "datastore.cache.enabled";

    public static final String PARAM_REFRESH_POLICY = PARAM_ELASTIC_BASE + "index.refreshpolicy";

    private static final String DEFAULT_KEYSTORE_FILE = getSystemProperty("javax.net.ssl.keyStore");
    private static final String DEFAULT_KEYSTORE_PASSWORD = getSystemProperty("javax.net.ssl.keyStorePassword");
    private static final String DEFAULT_TRUSTSTORE_FILE = getSystemProperty("javax.net.ssl.trustStore");
    private static final String DEFAULT_TRUSTSTORE_PASSWORD = getSystemProperty("javax.net.ssl.trustStorePassword");
    private static final int DEFAULT_RETRIES = 5;
    private static final int DEFAULT_BACKOFF_INTERVAL = 500;

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

    public static String getHTTPHost()
    {
        return settings.getSetting(config_name, PARAM_HTTP_HOST, "localhost", false);
    }

    public static String getHTTPPort()
    {
        return settings.getSetting(config_name, PARAM_HTTP_PORT, "9200", false);
    }

    public static String getKeystoreFilepath()
    {
        return settings.getSetting(config_name, PARAM_KEYSTORE_FILE, DEFAULT_KEYSTORE_FILE, false);
    }

    public static void setKeystoreFilepath(String keystoreFilepath)
    {
        settings.setSetting(config_name, PARAM_KEYSTORE_FILE, keystoreFilepath);
    }

    public static String getKeystorePassword()
    {
        return settings.getSetting(config_name, PARAM_KEYSTORE_PASSWORD, DEFAULT_KEYSTORE_PASSWORD, false);
    }

    public static void setKeystorePassword(String value)
    {
        settings.setSetting(config_name, PARAM_KEYSTORE_PASSWORD, value);
    }

    public static String getTruststoreFilepath()
    {
        return settings.getSetting(config_name, PARAM_TRUSTSTORE_FILE, DEFAULT_TRUSTSTORE_FILE, false);
    }

    public static void setTruststoreFilepath(String truststoreFilepath)
    {
        settings.setSetting(config_name, PARAM_TRUSTSTORE_FILE, truststoreFilepath);
    }

    public static String getTruststorePassword()
    {
        return settings.getSetting(config_name, PARAM_TRUSTSTORE_PASSWORD, DEFAULT_TRUSTSTORE_PASSWORD, false);
    }

    public static void setTruststorePassword(String value)
    {
        settings.setSetting(config_name, PARAM_TRUSTSTORE_PASSWORD, value);
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

    public static void setRejectUnauthorized(boolean value)
    {
        settings.setSetting(config_name, PARAM_SSL_REJECT_UNAUTHORIZED, value);
    }

    public static boolean isRejectUnauthorized()
    {
        return settings.getSetting(config_name, PARAM_SSL_REJECT_UNAUTHORIZED, true, false);
    }

    public static boolean isAuthoritative()
    {
        return settings.getSetting(config_name, PARAM_IS_AUTHORITATIVE, true, false);
    }

    public static void setIsAuthoritative(Boolean value)
    {
        settings.setSetting(config_name, PARAM_IS_AUTHORITATIVE, value);
    }

    public static int getRetryAttempts()
    {
        return settings.getSetting(config_name, PARAM_RETRY_ATTEMPTS, DEFAULT_RETRIES, false);
    }

    public static void setRetryAttempts(Boolean value)
    {
        settings.setSetting(config_name, PARAM_RETRY_ATTEMPTS, value);
    }

    /**
     * @return the number of milliseconds between retry attempts.
     */
    public static int getBackoffInterval()
    {
        return settings.getSetting(config_name, PARAM_BACKOFF_INTERVAL, DEFAULT_BACKOFF_INTERVAL, false);
    }

    /**
     * Sets the number of milliseconds between retry attempts.
     *
     * @param millis
     */
    public static void setBackoffInterval(int millis)
    {
        settings.setSetting(config_name, PARAM_BACKOFF_INTERVAL, millis);
    }

    /**
     * @return whether datastore caching is enabled.
     */
    public static Boolean isDataStoreCacheEnabled()
    {
        return settings.getSetting(config_name, PARAM_DATASTORE_CACHE_ENABLED, false, false);
    }

    /**
     * Sets whether datastore caching is enabled.
     *
     * @param value
     */
    public static void setDataStoreCacheEnabled(boolean value)
    {
        settings.setSetting(config_name, PARAM_DATASTORE_CACHE_ENABLED, value);
    }

    /**
     * @return the Index Refresh Policy for all indices
     */
    public static String getIndexRefreshPolicy()
    {
        return settings.getSetting(config_name, PARAM_REFRESH_POLICY, "NONE", false);
    }

    /**
     * Sets the Index Refresh Policy for all indices to refresh upon ingesting new data.
     *
     * Valid values: false, true, wait_for
     *
     * @param value
     */
    public static void setIndexRefreshPolicy(String value)
    {
        settings.setSetting(config_name, PARAM_REFRESH_POLICY, value);
    }

    public static Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(PARAM_IS_AUTHORITATIVE, Boolean.toString(isAuthoritative()));
        params.put(PARAM_SSL_ENABLED, Boolean.toString(isSSLEnabled()));
        params.put(PARAM_SSL_REJECT_UNAUTHORIZED, Boolean.toString(isRejectUnauthorized()));
        params.put(PARAM_KEYSTORE_FILE, getKeystoreFilepath());
        params.put(PARAM_KEYSTORE_PASSWORD, getKeystorePassword());
        params.put(PARAM_TRUSTSTORE_FILE, getTruststoreFilepath());
        params.put(PARAM_TRUSTSTORE_PASSWORD, getTruststorePassword());
        params.put(PARAM_HOSTS, getElastichosts());
        params.put(PARAM_CLUSTER_NAME, getElasticClusterName());
        params.put(PARAM_HTTP_HOST, getHTTPHost());
        params.put(PARAM_HTTP_PORT, getHTTPPort());
        params.put(PARAM_RETRY_ATTEMPTS, Integer.toString(getRetryAttempts()));
        params.put(PARAM_DATASTORE_CACHE_ENABLED, Boolean.toString(isDataStoreCacheEnabled()));

        return params;
    }

    private static String getSystemProperty(String property)
    {
        String value = System.getProperty(property);

        if (value == null)
        {
            value = "";
        }

        return value;
    }
}
