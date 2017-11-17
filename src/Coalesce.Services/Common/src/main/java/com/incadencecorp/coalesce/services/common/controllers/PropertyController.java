package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * This controller provides access to properties defined by the server such as URLs.
 *
 * @author Derek Clemenzi
 */
public class PropertyController {

    private IConfigurationsConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
    private SettingsBase settings = new SettingsBase(connector);
    private String key = "client.properties";
    private boolean isReadOnly = false;

    /**
     * Sets the connector to use when getting / setting properties.
     *
     * @param connector
     */
    public void setConnector(IConfigurationsConnector connector)
    {
        this.connector = connector;
        this.settings = new SettingsBase(connector);
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
     * Sets whether or not a client can update settings defined on the server.
     *
     * @param value
     */
    public void setIsReadOnly(boolean value)
    {
        this.isReadOnly = value;
    }

    /**
     * @param name
     * @return a single property's value.
     */
    public String getProperty(String name) throws RemoteException
    {

        return settings.getSetting(key, name, "", false);
    }

    public void setProperty(String name, String value) throws RemoteException
    {
        if (isReadOnly)
        {
            throw new RemoteException(String.format(CoalesceErrors.NOT_SAVED, name, "String", "Read Only"));
        }
        settings.setSetting(key, name, value);
    }

    /**
     * @return all the properties and their values that are handled by this controller.
     */
    public Map<String, String> getProperties() throws RemoteException
    {
        return connector.getSettings(key);
    }

    /**
     * Sets multiple property's values. If the connector is readonly then this method wont do anything. Also this should be restricted to privileged users.
     */
    public void setProperties(Map<String, String> values) throws RemoteException
    {
        if (isReadOnly)
        {
            throw new RemoteException(String.format(CoalesceErrors.NOT_SAVED, "Properties", "Map<String, String>", "Read Only"));
        }

        for (Map.Entry<String, String> entry : values.entrySet())
        {
            settings.setSetting(key, entry.getKey(), entry.getValue());
        }
    }

}
