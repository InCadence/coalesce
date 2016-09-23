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

import java.util.Map;

import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingType;

/**
 * Container class used by the synchronizer to combine a configuration name with
 * a connector.
 * 
 * @author n78554
 */
public class PropertyLoader {

    private IConfigurationsConnector connector = null;
    private String configurationName;

    /**
     * Default Constructor
     * 
     * @param connector
     * @param name of the configuration file.
     */
    public PropertyLoader(IConfigurationsConnector connector, String name)
    {
        this.connector = connector;
        this.configurationName = name;
    }

    /**
     * Sets multiple properties by calling {@link #setProperty(String, String)}.
     * 
     * @param properties
     */
    public void setProperties(Map<String, String> properties)
    {
        for (Map.Entry<String, String> entry: properties.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets the property's value.
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, String value)
    {
        connector.setSetting(configurationName, name, value, SettingType.ST_STRING);
    }

    /**
     * @param name
     * @return the property's value.
     */
    public String getProperty(String name)
    {
        return connector.getSetting(configurationName, name, null, SettingType.ST_STRING, false);
    }

}
