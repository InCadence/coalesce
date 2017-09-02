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

package com.incadencecorp.coalesce.framework.persistance.neo4j;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * Configuration properties for the Neo4j persistor implementation.
 * 
 * @author n78554
 */
public class Neo4jSettings {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static String config_name = "neo4j-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 7474;
    private static final int DEFAULT_ONERROR_RETRIES = 5;
    private static final int DEFAULT_ONERROR_BACKOFF_INTERVAL = 500;
    private static final boolean DEFAULT_ALLOW_DELETE = false;
    private static final boolean DEFAULT_SSL_ENABLED = false;

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "neo4j.";
    private static final String PROPERTY_ENABLED = PARAM_BASE + "enabled";
    private static final String PROPERTY_SERVER_NAME = PARAM_BASE + "dbServerName";
    private static final String PROPERTY_USERNAME = PARAM_BASE + "dbUser";
    private static final String PROPERTY_PASSWORD = PARAM_BASE + "dbPassword";
    private static final String PROPERTY_DATABASE_NAME = PARAM_BASE + "database";
    private static final String PROPERTY_PORT = PARAM_BASE + "dbServerPort";
    private static final String PROPERTY_ALLOW_DELETE = PARAM_BASE + "allowDelete";
    private static final String PROPERTY_RETRY_ATTEMPTS = PARAM_BASE + "onerror.retryattempts";
    private static final String PROPERTY_BACKOFF_INTERVAL = PARAM_BASE + "onerror.backoffinterval";
    private static final String PARAM_SSL_ENABLED = PARAM_BASE + "ssl.enabled";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private Neo4jSettings()
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
    }

    /*--------------------------------------------------------------------------
    Settings
    --------------------------------------------------------------------------*/

    /**
     * @return Whether Neo4j indexing is enabled.
     */
    public static boolean isEnabled()
    {
        return settings.getSetting(config_name, PROPERTY_ENABLED, true, true);
    }

    /**
     * Sets whether Neo4j indexing is enabled.
     * 
     * @param value
     */
    public static void setIsEnabled(boolean value)
    {
        settings.setSetting(config_name, PROPERTY_ENABLED, value);
    }

    /**
     * @return Returns the address of the database.
     */
    public static String getDatabaseAddress()
    {
        return settings.getSetting(config_name, PROPERTY_SERVER_NAME, DEFAULT_ADDRESS, true);
    }

    /**
     * Sets the address of the database.
     * 
     * @param databaseAddress
     */
    public static void setDatabaseAddress(String databaseAddress)
    {
        settings.setSetting(config_name, PROPERTY_SERVER_NAME, databaseAddress);
    }

    /**
     * @return Returns the username used for accessing the database.
     */
    public static String getUserName()
    {
        return settings.getSetting(config_name, PROPERTY_USERNAME, "", false);
    }

    /**
     * Sets the username used for accessing the database.
     * 
     * @param userName
     */
    public static void setUserName(String userName)
    {
        settings.setSetting(config_name, PROPERTY_USERNAME, userName);
    }

    /**
     * @return Returns the password used for accessing the database.
     */
    public static String getUserPassword()
    {
        return settings.getSetting(config_name, PROPERTY_PASSWORD, "", false);
    }

    /**
     * Sets the password used for accessing the database.
     * 
     * @param userPassword
     */
    public static void setUserPassword(String userPassword)
    {
        settings.setSetting(config_name, PROPERTY_PASSWORD, userPassword);
    }

    /**
     * @return Returns the database name.
     */
    public static String getDatabaseName()
    {
        return settings.getSetting(config_name, PROPERTY_DATABASE_NAME, "", true);
    }

    /**
     * Sets the database name.
     * 
     * @param databaseName
     */
    public static void setDatabaseName(String databaseName)
    {
        settings.setSetting(config_name, PROPERTY_DATABASE_NAME, databaseName);
    }

    /**
     * @return Returns the port used for accessing the database.
     */
    public static int getDatabasePort()
    {
        return settings.getSetting(config_name, PROPERTY_PORT, DEFAULT_PORT, true);
    }

    /**
     * Sets the port to be used for accessing the database.
     * 
     * @param databasePort
     */
    public static void setDatabasePort(int databasePort)
    {
        settings.setSetting(config_name, PROPERTY_PORT, databasePort);
    }

    /**
     * @return the number of attempts that should be tried when an error occurs.
     */
    public static int getRetryAttempts()
    {
        return settings.getSetting(config_name, PROPERTY_RETRY_ATTEMPTS, DEFAULT_ONERROR_RETRIES, true);
    }

    /**
     * Sets the number of attempts that should be tried when an error occurs.
     * 
     * @param value
     */
    public static void setRetryAttempts(int value)
    {
        settings.setSetting(config_name, PROPERTY_RETRY_ATTEMPTS, value);
    }

    /**
     * @return the number of milliseconds between retry attempts.
     */
    public static int getBackoffInterval()
    {
        return settings.getSetting(config_name, PROPERTY_BACKOFF_INTERVAL, DEFAULT_ONERROR_BACKOFF_INTERVAL, true);
    }

    /**
     * Sets the number of milliseconds between retry attempts.
     * 
     * @param millis
     */
    public static void setBackoffInterval(int millis)
    {
        settings.setSetting(config_name, PROPERTY_BACKOFF_INTERVAL, millis);
    }

    /**
     * @return whether marking a entity as deleted will remove it from the DB.
     */
    public static boolean isAllowDelete()
    {
        return settings.getSetting(config_name, PROPERTY_ALLOW_DELETE, DEFAULT_ALLOW_DELETE, true);
    }

    /**
     * Sets whether marking a entity as deleted will remove it from the DB.
     * 
     * @param value
     */
    public static void setAllowDelete(boolean value)
    {
        settings.setSetting(config_name, PROPERTY_ALLOW_DELETE, value);
    }

    /**
     * @return whether or not to use SSL. Default: {@value #DEFAULT_SSL_ENABLED}
     *         .
     */
    public static boolean isSSLEnabled()
    {
        return settings.getSetting(config_name, PARAM_SSL_ENABLED, DEFAULT_SSL_ENABLED, true);
    }

    /**
     * Sets whether or not to use SSL.
     *
     * @param value
     */
    public static void setSSLEnabled(boolean value)
    {
        settings.setSetting(config_name, PARAM_SSL_ENABLED, value);
    }

    /**
     * @return Returns database parameters.
     */
    public static ServerConn getServerConn()
    {

        ServerConn serCon = new ServerConn();

        serCon.setServerName(getDatabaseAddress());
        serCon.setPortNumber(getDatabasePort());
        serCon.setDatabase(getDatabaseName());
        serCon.setUser(getUserName());
        serCon.setPassword(getUserPassword());

        return serCon;
    }

}
