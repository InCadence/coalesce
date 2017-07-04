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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * Configuration properties for Postgres persister implementations.
 * 
 * @author n78554
 */
public class PostGreSQLSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static String config_name = "postgres-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(System.getProperty("COALESCE_CONFIG_LOCATION")));

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    // TODO Update base parameter to be coalesce within the InCadence repo
    private static final String PARAM_BASE = "omega.dss.";
    private static final String PARAM_SRID = PARAM_BASE + "srid";
    private static final String PARAM_PORT = PARAM_BASE + "dbServerPort";
    private static final String PARAM_DATABASE = PARAM_BASE + "database";
    private static final String PARAM_SCHEMA = PARAM_BASE + "schema";
    private static final String PARAM_PASSWORD = PARAM_BASE + "dbPassword";
    private static final String PARAM_USER = PARAM_BASE + "dbUser";
    private static final String PARAM_HOST = PARAM_BASE + "dbServerName";
    private static final String PARAM_USE_FOREIGN_KEYS = PARAM_BASE + "usefk";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_USERNAME = "enterprisedb";
    private static final String DEFAULT_PASSWORD = DEFAULT_USERNAME;
    private static final boolean DEFAULT_USE_FOREIGN_KEYS = false;
    private static final int DEFAULT_SRID = 4326; // WGS84

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private PostGreSQLSettings()
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
     * @return Returns the address of the database.
     */
    public static String getDatabaseAddress()
    {
        return settings.getSetting(config_name, PARAM_HOST, "10.0.51.90", true);
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseAddress
     */
    public static void setDatabaseAddress(String databaseAddress)
    {
        settings.setSetting(config_name, PARAM_HOST, databaseAddress);
    }

    /**
     * @return Returns the username used for accessing the database.
     */
    public static String getUserName()
    {
        return settings.getSetting(config_name, PARAM_USER, DEFAULT_USERNAME, false);
    }

    /**
     * Sets the username used for accessing the database.
     *
     * @param userName
     */
    public static void setUserName(String userName)
    {
        settings.setSetting(config_name, PARAM_USER, userName);
    }

    /**
     * @return Returns the password used for accessing the database.
     */
    public static String getUserPassword()
    {
        return settings.getSetting(config_name, PARAM_PASSWORD, DEFAULT_PASSWORD, false);
    }

    /**
     * Sets the password used for accessing the database.
     *
     * @param userPassword
     */
    public static void setUserPassword(String userPassword)
    {
        settings.setSetting(config_name, PARAM_PASSWORD, userPassword);
    }

    /**
     * @return Returns the schema used for the database.
     */
    public static String getDatabaseSchema()
    {
        return settings.getSetting(config_name, PARAM_SCHEMA, "coalesce", true);
    }

    /**
     * Sets the schema used for the database.
     *
     * @param databaseSchema
     */
    public static void setDatabaseSchema(String databaseSchema)
    {
        settings.setSetting(config_name, PARAM_SCHEMA, databaseSchema);
    }

    /**
     * @return Returns the database name.
     */
    public static String getDatabaseName()
    {
        return settings.getSetting(config_name, PARAM_DATABASE, "OMEGA", true);
    }

    /**
     * Sets the database name.
     *
     * @param databaseName
     */
    public static void setDatabaseName(String databaseName)
    {
        settings.setSetting(config_name, PARAM_DATABASE, databaseName);
    }

    /**
     * @return Returns the port used for accessing the database.
     */
    public static int getDatabasePort()
    {
        return settings.getSetting(config_name, PARAM_PORT, 5444, true);
    }

    /**
     * Sets the port to be used for accessing the database.
     *
     * @param databasePort
     */
    public static void setDatabasePort(int databasePort)
    {
        settings.setSetting(config_name, PARAM_PORT, databasePort);
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

    /**
     * @return the Spatial Reference Identifier (SRID). Default: {@value #DEFAULT_SRID}.
     */
    public static int getSRID()
    {
        return settings.getSetting(config_name, PARAM_SRID, DEFAULT_SRID, true);
    }

    /**
     * Sets the Spatial Reference Identifier (SRID) - required for geospatial
     * fields.
     *
     * @param srid
     */
    public static void setSRID(int srid)
    {
        settings.setSetting(config_name, PARAM_SRID, srid);
    }

    /**
     * @return whether or not foreign keys should be created when generating
     *         tables while registering an entity. Default: {@value #DEFAULT_USE_FOREIGN_KEYS}.
     */
    public static boolean isUseForeignKeys()
    {
        return settings.getSetting(config_name, PARAM_USE_FOREIGN_KEYS, DEFAULT_USE_FOREIGN_KEYS, true);
    }

    /**
     * Sets whether or not foreign keys should be created when generating tables
     * while registering an entity.
     *
     * @param value
     */
    public static void setUseForeignKeys(boolean value)
    {
        settings.setSetting(config_name, PARAM_USE_FOREIGN_KEYS, value);
    }

}
