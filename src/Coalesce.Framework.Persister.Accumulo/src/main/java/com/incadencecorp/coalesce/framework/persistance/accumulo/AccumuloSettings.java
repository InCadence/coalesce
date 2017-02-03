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

import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;

/**
 * Configuration properties for the Accumulo persistor implementation.
 * 
 * @author Matthew DeFazio
 */
public class AccumuloSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static final String CONFIG_NAME = "accumulo-config.properties";
    private static SettingsBase settings = new SettingsBase(null);

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String ATTRIBUTE_ROWS = "attributeRowNames";
    private static final String PERSIST_SECTION_ATTR = "persistSectionAttr";
    private static final String PERSIST_RECORDSET_ATTR = "persistRecordsetAttr";
    private static final String PERSIST_FIELD_DEF_ATTR = "persistFieldDefAttr";
    private static final String PERSIST_RECORD_ATTR = "persistRecordAttr";
    private static final String PERSIST_FIELD_ATTR = "persistFieldAttr";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_ATTRIBUTE_ROWS = "entityid,entityidtype,value,key,lastmodified,name,datatype";
    
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

    /*--------------------------------------------------------------------------
    Settings
    --------------------------------------------------------------------------*/
    /**
     * @return Returns the address of the database.
     */
    public static String getAttributeFields() {
        return settings.getSetting(CONFIG_NAME, ATTRIBUTE_ROWS, DEFAULT_ATTRIBUTE_ROWS, true);
    }

    /**
     * Sets the address of the database.
     *
     * @param databaseAddress
     */
    public static void setAttributeFields(String databaseAddress) {
        settings.setSetting(CONFIG_NAME, ATTRIBUTE_ROWS, databaseAddress);
    }

    public static boolean getPersistSectionAttr()
    {
        return settings.getSetting(CONFIG_NAME, PERSIST_SECTION_ATTR,true,true);
    }

    public static void setPersistSectionAttr(boolean pERSIST_SECTION_ATTR)
    {
        settings.setSetting(CONFIG_NAME, PERSIST_SECTION_ATTR, pERSIST_SECTION_ATTR);
    }

    public static boolean getPersistRecordsetAttr()
    {
        return settings.getSetting(CONFIG_NAME,PERSIST_RECORDSET_ATTR ,true,true);
    }

    public static void setPersistRecordsetAttr(boolean persistRecordsetAttr)
    {
        settings.setSetting(CONFIG_NAME,PERSIST_RECORDSET_ATTR,persistRecordsetAttr);
    }

    public static boolean getPersistFieldDefAttr()
    {
        return settings.getSetting(CONFIG_NAME, PERSIST_FIELD_DEF_ATTR,true,true);
    }

    public static void setPersistFieldDefAttr(boolean persistFieldDefAttr)
    {
        settings.setSetting(CONFIG_NAME,PERSIST_FIELD_DEF_ATTR,persistFieldDefAttr);
    }

    public static boolean getPersistRecordAttr()
    {
        return settings.getSetting(CONFIG_NAME, PERSIST_RECORD_ATTR,true,true);
    }

    public static void setPersistRecordAttr(boolean persistRecordAttr)
    {
        settings.setSetting(CONFIG_NAME,PERSIST_RECORD_ATTR,persistRecordAttr);
    }

    public static boolean getPersistFieldAttr()
    {
        return settings.getSetting(CONFIG_NAME, PERSIST_FIELD_ATTR,true,true);
    }

    public static void setPersistFieldAttr(boolean persistFieldAttr)
    {
        settings.setSetting(CONFIG_NAME,PERSIST_FIELD_ATTR,persistFieldAttr);
    }

  

}
