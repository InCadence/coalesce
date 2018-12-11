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

package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * These are the configuration settings for {@link SQLTemplatePersisterImpl}
 *
 * @author GGaito
 */
public class SQLPersisterImplSettings {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector("src/test/resources"));
    private static String config_name = "sql-config.properties";


    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/
    private static final String PARAM_BASE = "asid.";
    private static final String PARAM_SRID = PARAM_BASE + "srid";
    private static final String DB_SERVER_PORT = PARAM_BASE + "dbServerPort";
    private static final String DATABASE = PARAM_BASE + "database";
    private static final String SCHEMA = PARAM_BASE + "schema";
    private static final String DB_PASSWORD = PARAM_BASE + "dbPassword";
    private static final String DB_USER = PARAM_BASE + "dbUser";
    private static final String DB_SERVER_NAME = PARAM_BASE + "dbServerName";
    private static final String PARAM_USE_FOREIGN_KEYS = PARAM_BASE + "usefk";


    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_USERNAME = "enterprisedb";
    private static final String DEFAULT_PASSWORD = DEFAULT_USERNAME;
    private static final boolean DEFAULT_USE_FOREIGN_KEYS = false;
    private static final boolean DEFAULT_SSL_ENABLED = false;
    private static final boolean DEFAULT_SSL_CERT_VALIDATION = false;
    private static final int DEFAULT_SRID = 4326; // WGS84



    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/
    /**
     * Default Constructor
     */
    public SQLPersisterImplSettings()
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



     /*--------------------------------------------------------------------------
    Settings
    --------------------------------------------------------------------------*/
    /**
     * @return Returns the schema used for the database.
     */
    public static String getDatabaseSchema()
    {
        return settings.getSetting(config_name,SCHEMA, "dbo", true);
    }
    /**
     * Sets the schema used for the database.
     *
     * @param databaseSchema
     */
    public static void setDatabaseSchema(String databaseSchema)
    {
        settings.setSetting(config_name, SCHEMA, databaseSchema);
    }
    /**
     * @return Returns database parameters.
     */
    public static ServerConn getServerConn()
    {

        ServerConn serCon = new ServerConn();

        serCon.setServerName(getDatabaseServerName());
        serCon.setPortNumber(getDatabasePort());
        serCon.setDatabase(getDatabaseName());
        serCon.setUser(getUserName());
        serCon.setPassword(getUserPassword());

        return serCon;
    }

    public static void  setParameters(Map<String, String> params)
    {
        setDatabaseServerName(params.get(DB_SERVER_NAME));
        setDatabasePort(Integer.parseInt(params.get(DB_SERVER_PORT)));
        setUserName(params.get(DB_USER));
        setUserPassword(params.get(DB_PASSWORD));
        setDatabaseName(params.get(DATABASE));
    }
    /**
     * @return Returns the address of the database.
     */
    public static String getDatabaseServerName()
    {
        return settings.getSetting(config_name, DB_SERVER_NAME, "localhost", false);
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseServerName
     */
    public static void setDatabaseServerName(String databaseServerName)
    {
        settings.setSetting(config_name, DB_SERVER_NAME, databaseServerName);
    }

    /**
     * @return Returns the username used for accessing the database.
     */
    public static String getUserName()
    {
        return settings.getSetting(config_name, DB_USER, "", false);
    }

    /**
     * Sets the username used for accessing the database.
     *
     * @param userName
     */
    public static void setUserName(String userName)
    {
        settings.setSetting(config_name, DB_USER, userName);
    }

    /**
     * @return Returns the password used for accessing the database.
     */
    public static String getUserPassword()
    {
        return settings.getSetting(config_name, DB_PASSWORD, "", false);
    }

    /**
     * Sets the password used for accessing the database.
     *
     * @param userPassword
     */
    public static void setUserPassword(String userPassword)
    {
        settings.setSetting(config_name, DB_PASSWORD, userPassword);
    }

    /**
     * @return Returns the database name.
     */
    public static String getDatabaseName()
    {
        return settings.getSetting(config_name, DATABASE, "IdentityHubDatabase", true);
    }

    /**
     * Sets the database name.
     *
     * @param databaseName
     */
    public static void setDatabaseName(String databaseName)
    {
    settings.setSetting(config_name, DATABASE, databaseName);
    }

    /**
     * @return Returns the port used for accessing the database.
     */
    public static int getDatabasePort()
    {
        return settings.getSetting(config_name, DB_SERVER_PORT, 1433, true);
    }

    /**
     * Sets the port to be used for accessing the database.
     *
     * @param databasePort
     */
    public static void setDatabasePort(int databasePort)
    {
        settings.setSetting(config_name, DB_SERVER_PORT, databasePort);
    }

    /**
     * @return whether or not foreign keys should be created when generating
     *         tables while registering an entity. Default:
     *         {@value #DEFAULT_USE_FOREIGN_KEYS}.
     */
    public static boolean isUseForeignKeys()
    {
        return settings.getSetting(config_name, PARAM_USE_FOREIGN_KEYS, DEFAULT_USE_FOREIGN_KEYS, true);
    }

    /**
     * @return the Spatial Reference Identifier (SRID). Default:
     *         {@value #DEFAULT_SRID}.
     */
    public static int getSRID()
    {
        return settings.getSetting(config_name, PARAM_SRID, DEFAULT_SRID, true);
    }

    public static Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(DB_USER, getUserName());
        params.put(DB_PASSWORD, getUserPassword());
        params.put(DATABASE, getDatabaseName());
        params.put(DB_SERVER_NAME,getDatabaseServerName());
        params.put(DB_SERVER_PORT,Integer.toString(getDatabasePort()));

        return params;
    }





}
