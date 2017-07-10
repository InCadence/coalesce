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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * Configuration properties for the Accumulo persistor implementation.
 * 
 * @author Matthew DeFazio
 */
public class AccumuloSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloSettings.class);

    private static String config_name = "accumulo-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(System.getProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION)));

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String ATTRIBUTE_ROWS = "attributeRowNames";
    private static final String PERSIST_SECTION_ATTR = "persistSectionAttr";
    private static final String PERSIST_RECORDSET_ATTR = "persistRecordsetAttr";
    private static final String PERSIST_FIELD_DEF_ATTR = "persistFieldDefAttr";
    private static final String PERSIST_RECORD_ATTR = "persistRecordAttr";
    private static final String PERSIST_FIELD_ATTR = "persistFieldAttr";
    private static final String PASSWORD = "accumulo.password";
    private static final String USER = "accumulo.userid";
    private static final String ZOOKEEPERS = "accumulo.zookeepers";
    private static final String DATABASE_NAME = "accumulo.database";
    
    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_ATTRIBUTE_ROWS = "entityid,entityidtype,value,key,lastmodified,name,datatype";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "secret";
    
    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private AccumuloSettings()
    {
        // Do Nothing
    }

    /**
     * Configures the settings to use a particular connector.
     *
     * @param connector
     */
    public static void initialize(final IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);
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
    public static String getDatabaseAddress() {
    	String dbAddr = settings.getSetting(config_name, ZOOKEEPERS, "localhost", true);
    	LOGGER.debug("settings.getDatabaseAddress: "+dbAddr);
        return dbAddr;
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseAddress
     */
    public static void setDatabaseAddress(String databaseAddress) {
    	// Not sure about this
        settings.setSetting(config_name, ZOOKEEPERS, databaseAddress);
    }

    /**
     * @return Returns the username used for accessing the database.
     */
    public static String getUserName() {
        return settings.getSetting(config_name, USER, DEFAULT_USERNAME, false);
    }

    /**
     * Sets the username used for accessing the database.
     *
     * @param userName
     */
    public static void setUserName(String userName) {
        settings.setSetting(config_name, USER, userName);
    }

    /**
     * @return Returns the password used for accessing the database.
     */
    public static String getUserPassword() {
        return settings.getSetting(config_name, PASSWORD, DEFAULT_PASSWORD, false);
    }

    /**
     * Sets the password used for accessing the database.
     *
     * @param userPassword
     */
    public static void setUserPassword(String userPassword) {
        settings.setSetting(config_name, PASSWORD, userPassword);
    }

//    /**
//     * @return Returns the schema used for the database.
//     */
//    public static String getDatabaseSchema() {
//        return settings.getSetting(CONFIG_NAME, DSS_SCHEMA, "coalesce", true);
//    }
//
//    /**
//     * Sets the schema used for the database.
//     *
//     * @param databaseSchema
//     */
//    public static void setDatabaseSchema(String databaseSchema) {
//        settings.setSetting(CONFIG_NAME, DSS_SCHEMA, databaseSchema);
//    }

    /**
     * @return Returns the database name.
     */
    public static String getDatabaseName() {
        return settings.getSetting(config_name, DATABASE_NAME, "accumulo", true);
    }

    /**
     * Sets the database name.
     *
     * @param databaseName
     */
    public static void setDatabaseName(String databaseName) {
        settings.setSetting(config_name, DATABASE_NAME, databaseName);
    }

    
    /**
     * @return Returns the address of the database.
     */
    public static String getAttributeFields() {
        return settings.getSetting(config_name, ATTRIBUTE_ROWS, DEFAULT_ATTRIBUTE_ROWS, true);
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseAddress
     */
    public static void setAttributeFields(String databaseAddress) {
        settings.setSetting(config_name, ATTRIBUTE_ROWS, databaseAddress);
    }

    public static boolean getPersistSectionAttr()
    {
        return settings.getSetting(config_name, PERSIST_SECTION_ATTR,true,true);
    }

    public static void setPersistSectionAttr(boolean pERSIST_SECTION_ATTR)
    {
        settings.setSetting(config_name, PERSIST_SECTION_ATTR, pERSIST_SECTION_ATTR);
    }

    public static boolean getPersistRecordsetAttr()
    {
        return settings.getSetting(config_name,PERSIST_RECORDSET_ATTR ,true,true);
    }

    public static void setPersistRecordsetAttr(boolean persistRecordsetAttr)
    {
        settings.setSetting(config_name,PERSIST_RECORDSET_ATTR,persistRecordsetAttr);
    }

    public static boolean getPersistFieldDefAttr()
    {
        return settings.getSetting(config_name, PERSIST_FIELD_DEF_ATTR,true,true);
    }

    public static void setPersistFieldDefAttr(boolean persistFieldDefAttr)
    {
        settings.setSetting(config_name,PERSIST_FIELD_DEF_ATTR,persistFieldDefAttr);
    }

    public static boolean getPersistRecordAttr()
    {
        return settings.getSetting(config_name, PERSIST_RECORD_ATTR,true,true);
    }

    public static void setPersistRecordAttr(boolean persistRecordAttr)
    {
        settings.setSetting(config_name,PERSIST_RECORD_ATTR,persistRecordAttr);
    }

    public static boolean getPersistFieldAttr()
    {
        return settings.getSetting(config_name, PERSIST_FIELD_ATTR,true,true);
    }

    public static void setPersistFieldAttr(boolean persistFieldAttr)
    {
        settings.setSetting(config_name,PERSIST_FIELD_ATTR,persistFieldAttr);
    }

    public static ServerConn getServerConn() {

        ServerConn serCon = new ServerConn();
        
        serCon.setServerName(getDatabaseAddress());
        serCon.setDatabase(getDatabaseName());
        serCon.setUser(getUserName());
        serCon.setPassword(getUserPassword());
        
        return serCon;
    }
  

}
