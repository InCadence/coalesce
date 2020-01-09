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

package com.incadencecorp.coalesce.framework.persistance.mongo;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import com.mongodb.ConnectionString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for the Cosmos persistor implementation.
 *
 * @author Derek Clemenzi
 */
public class MongoSettings {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static String config_name = "mongo-config.properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "com.incadence.persister.mongo.";
    /**
     * (CSV) List of host
     */
    public static final String PARAM_HOST = PARAM_BASE + "host";
    /**
     * (Integer) Port number to connect on.
     */
    public static final String PARAM_PORT = PARAM_BASE + "port";
    /**
     * (String) Service account used to access the database
     */
    public static final String PARAM_USER = PARAM_BASE + "user";
    /**
     * (String) Service account's password
     */
    public static final String PARAM_PASS = PARAM_BASE + "pass";
    /**
     * (Boolean) Service account's password
     */
    public static final String PARAM_SSL_ENABLED = PARAM_BASE + "ssl";
    /**
     * (String) Replication set
     */
    public static final String PARAM_REPLICA_SET = PARAM_BASE + "replicaset";
    /**
     * (Boolean) Specifies whether or not this persistor is authoritative meaning it can be used to READ entities.
     */
    public static final String PARAM_IS_AUTHORITATIVE = PARAM_BASE + "isAuthoritative";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_IS_AUTHORITATIVE = "true";

    private static final String DEFAULT_SSL_ENABLED = "false";

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = 27017;

    private static final String DEFAULT_USER = "root";

    private static final String DEFAULT_PASS = "changeit";

    private static final boolean SET_IF_NOT_FOUND = false;

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private MongoSettings()
    {
        // Do Nothing
    }

    /**
     * Configures the settings to use a particular connector.
     *
     * @param connector to use for settings
     */
    public static void setConnector(final IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);
    }

    /**
     * Configures the settings to use a particular connector and property name.
     *
     * @param connector to use for settings
     * @param name      of configuration file
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
     * @param params configuration.
     * @return a connection string based of the parameters passed in.
     */
    public static ConnectionString createConnectionString(Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder("mongodb://");

        boolean isFirst = true;

        String user = params.get(PARAM_USER);
        String pass = params.get(PARAM_PASS);
        String port = params.get(PARAM_PORT);

        for (String host : params.get(PARAM_HOST).split("[,]"))
        {
            if (isFirst)
            {
                isFirst = false;
            }
            else
            {
                sb.append(",");
            }

            sb.append(user).append(":").append(pass).append("@").append(host).append(":").append(port);

        }

        sb.append("/?ssl=").append(params.get(PARAM_SSL_ENABLED));

        if (params.containsKey(PARAM_REPLICA_SET) && !params.get(PARAM_REPLICA_SET).isEmpty())
        {
            sb.append("&replicaSet=").append(params.get(PARAM_REPLICA_SET));
        }

        return new ConnectionString(sb.toString());
    }

    /**
     * @return the hosts; defaults to {@value DEFAULT_HOST}.
     */
    public static String[] getHosts()
    {
        return settings.getSetting(config_name, PARAM_HOST, DEFAULT_HOST, SET_IF_NOT_FOUND).split("[,]");
    }

    /**
     * Sets the hosts.
     */
    public static void setHosts(String[] value)
    {
        settings.setSetting(config_name, PARAM_HOST, Arrays.toString(value));
    }

    /**
     * @return the port; defaults to {@value DEFAULT_PORT}.
     */
    public static int getPort()
    {
        return settings.getSetting(config_name, PARAM_PORT, DEFAULT_PORT, SET_IF_NOT_FOUND);
    }

    /**
     * Sets the host name.
     */
    public static void setPort(int value)
    {
        settings.setSetting(config_name, PARAM_PORT, value);
    }

    /**
     * @return the host name; defaults to {@value DEFAULT_USER}.
     */
    public static String getUser()
    {
        return settings.getSetting(config_name, PARAM_USER, DEFAULT_USER, SET_IF_NOT_FOUND);
    }

    /**
     * Sets the host name.
     */
    public static void setUser(String value)
    {
        settings.setSetting(config_name, PARAM_USER, value);
    }

    /**
     * @return the master key; defaults to {@value DEFAULT_PASS}.
     */
    public static String getPass()
    {
        return settings.getSetting(config_name, PARAM_PASS, DEFAULT_PASS, SET_IF_NOT_FOUND);
    }

    /**
     * Sets the master key.
     */
    public static void setPass(String value)
    {
        settings.setSetting(config_name, PARAM_PASS, value);
    }

    /**
     * @return the replica set.
     */
    public static String getReplicaSet()
    {
        return settings.getSetting(config_name, PARAM_REPLICA_SET, "", SET_IF_NOT_FOUND);
    }

    /**
     * Sets the replica set.
     */
    public static void setReplicaSet(String value)
    {
        settings.setSetting(config_name, PARAM_REPLICA_SET, value);
    }

    /**
     * @return whether or not to store entity's XML.
     */
    public static boolean isAuthoritative()
    {
        return Boolean.parseBoolean(settings.getSetting(config_name,
                                                        PARAM_IS_AUTHORITATIVE,
                                                        DEFAULT_IS_AUTHORITATIVE,
                                                        false));
    }

    /**
     * Sets whether or not SSL is enabled
     */
    public static void setSSLEnabled(boolean value)
    {
        settings.setSetting(config_name, PARAM_SSL_ENABLED, value);
    }

    /**
     * @return whether or not SSL is enabled
     */
    public static boolean isSSLEnabled()
    {
        return Boolean.parseBoolean(settings.getSetting(config_name, PARAM_SSL_ENABLED, DEFAULT_SSL_ENABLED, false));
    }

    /**
     * Sets whether or not to store entity's XML.
     */
    public static void SetIsAuthoritative(boolean value)
    {
        settings.setSetting(config_name, PARAM_IS_AUTHORITATIVE, value);
    }

    public static Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(PARAM_IS_AUTHORITATIVE, Boolean.toString(isAuthoritative()));
        params.put(PARAM_HOST, settings.getSetting(config_name, PARAM_HOST, DEFAULT_HOST, SET_IF_NOT_FOUND));
        params.put(PARAM_PORT,
                   settings.getSetting(config_name, PARAM_PORT, Integer.toString(DEFAULT_PORT), SET_IF_NOT_FOUND));
        params.put(PARAM_USER, getUser());
        params.put(PARAM_PASS, getPass());
        params.put(PARAM_SSL_ENABLED, Boolean.toString(isSSLEnabled()));
        params.put(PARAM_REPLICA_SET, getReplicaSet());

        return params;
    }

}
