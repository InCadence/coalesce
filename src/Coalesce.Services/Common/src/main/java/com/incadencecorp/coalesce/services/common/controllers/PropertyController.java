package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * This controller provides access to properties defined by the server such as URLs.
 *
 * @author Derek Clemenzi
 */
public class PropertyController {

    private SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));
    private String key = "client.properties";

    /**
     * Sets the connector to use when getting / setting properties.
     *
     * @param connector
     */
    public void setConnector(IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);
    }

    /**
     * Sets the config key used to store properties. This depends on the connector used but usually this maps to a file name.
     *
     * @param key
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * @param name
     * @return a property value.
     */
    public String getProperty(String name)
    {
        return settings.getSetting(key, name, "", false);
    }

    /**
     * Sets a property value. If the connector is readonly then this method wont do anything. Also this should be restricted to privileged users.
     *
     * @param name
     * @param value
     */
    public void setProperty(String name, String value)
    {
        settings.setSetting(key, name, value);
    }
}
