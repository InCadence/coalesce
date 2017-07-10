/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.derby;

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
public class DerbySettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static final int SRID = 4326; // Spatial Reference ID, default is WGS84

    private static String config_name = "derby-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String ENTERPRISEDB = "enterprisedb";
    private static final String DSS_SRID = "omega.dss.srid";
    private static final String DSS_DB_SERVER_PORT = "omega.dss.dbServerPort";
    private static final String DSS_DATABASE = "omega.dss.database";
    private static final String DSS_SCHEMA = "omega.dss.schema";
    private static final String DSS_DB_PASSWORD = "omega.dss.dbPassword";
    private static final String DSS_DB_USER = "omega.dss.dbUser";
    private static final String DSS_DB_SERVER_NAME = "omega.dss.dbServerName";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_USERNAME = ENTERPRISEDB;
    private static final String DEFAULT_PASSWORD = ENTERPRISEDB;

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private DerbySettings()
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
        return settings.getSetting(config_name, DSS_DB_SERVER_NAME, "10.0.51.90", true);
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseAddress
     */
    public static void setDatabaseAddress(String databaseAddress)
    {
        settings.setSetting(config_name, DSS_DB_SERVER_NAME, databaseAddress);
    }

    /**
     * @return Returns the username used for accessing the database.
     */
    public static String getUserName()
    {
        return settings.getSetting(config_name, DSS_DB_USER, DEFAULT_USERNAME, false);
    }

    /**
     * Sets the username used for accessing the database.
     *
     * @param userName
     */
    public static void setUserName(String userName)
    {
        settings.setSetting(config_name, DSS_DB_USER, userName);
    }

    /**
     * @return Returns the password used for accessing the database.
     */
    public static String getUserPassword()
    {
        return settings.getSetting(config_name, DSS_DB_PASSWORD, DEFAULT_PASSWORD, false);
    }

    /**
     * Sets the password used for accessing the database.
     *
     * @param userPassword
     */
    public static void setUserPassword(String userPassword)
    {
        settings.setSetting(config_name, DSS_DB_PASSWORD, userPassword);
    }

    /**
     * @return Returns the schema used for the database.
     */
    public static String getDatabaseSchema()
    {
        return settings.getSetting(config_name, DSS_SCHEMA, "coalesce", true);
    }

    /**
     * Sets the schema used for the database.
     *
     * @param databaseSchema
     */
    public static void setDatabaseSchema(String databaseSchema)
    {
        settings.setSetting(config_name, DSS_SCHEMA, databaseSchema);
    }

    /**
     * @return Returns the database name.
     */
    public static String getDatabaseName()
    {
        return settings.getSetting(config_name, DSS_DATABASE, "OMEGA", true);
    }

    /**
     * Sets the database name.
     *
     * @param databaseName
     */
    public static void setDatabaseName(String databaseName)
    {
        settings.setSetting(config_name, DSS_DATABASE, databaseName);
    }

    /**
     * @return Returns the port used for accessing the database.
     */
    public static int getDatabasePort()
    {
        return settings.getSetting(config_name, DSS_DB_SERVER_PORT, 5444, true);
    }

    /**
     * Sets the port to be used for accessing the database.
     *
     * @param databasePort
     */
    public static void setDatabasePort(int databasePort)
    {
        settings.setSetting(config_name, DSS_DB_SERVER_PORT, databasePort);
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
     * Gets the Spatial Reference Identifier (SRID)
     *
     * @return SRID (Spatial Reference Identifier for geospatial fields)
     */
    public static int getSRID()
    {
        return settings.getSetting(config_name, DSS_SRID, SRID, true);
    }

    /**
     * Sets the Spatial Reference Identifier (SRID) - required for geospatial
     * fields
     *
     * @param srid
     */
    public static void setSRID(int srid)
    {
        settings.setSetting(config_name, DSS_SRID, srid);
    }

}
