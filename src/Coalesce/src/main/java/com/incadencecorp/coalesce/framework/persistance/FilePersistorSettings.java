/*-----------------------------------------------------------------------------'
Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved
　
Notwithstanding any contractor copyright notice, the Government has Unlimited
Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014. Use
of this work other than as specifically authorized by these DFARS Clauses may
violate Government rights in this work.
　
DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
Unlimited Rights. The Government has the right to use, modify, reproduce,
perform, display, release or disclose this computer software and to have or
authorize others to do so.
　
Distribution Statement D. Distribution authorized to the Department of
Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
-----------------------------------------------------------------------------*/
package com.incadencecorp.coalesce.framework.persistance;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * Configuration properties for {@link FilePersistorImpl}.
 * 
 * @author n78554
 */
public class FilePersistorSettings {
    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private static String config_name = FilePersistorImpl.class.getSimpleName() + ".properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private FilePersistorSettings()
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
     * @return Returns the root directory to be used as the database.
     */
    public static Path getDirectory()
    {
        Path result;
        String value = settings.getSetting(config_name, CoalesceParameters.PARAM_DIRECTORY, "", true);

        if (StringHelper.isNullOrEmpty(value))
        {
            result = Paths.get("db");
        }
        else
        {
            result = Paths.get(value);
        }

        return result;
    }

    /**
     * Sets the root directory to be used as the database.
     *
     * @param value
     */
    public static void setDirectory(Path value)
    {
        settings.setSetting(config_name, CoalesceParameters.PARAM_DIRECTORY, value.toString());
    }

    /**
     * @return Returns the length of the sub directories to store entities in.
     */
    public static int getSubDirectoryLength()
    {
        int result = settings.getSetting(config_name, CoalesceParameters.PARAM_SUBDIR_LEN, 0, false);

        if (result < 0)
        {
            result = 0;
        }

        return result;
    }

    /**
     * Sets the length of the sub directories to store entities in.
     *
     * @param value
     */
    public static void setSubDirectoryLength(int value)
    {
        settings.setSetting(config_name, CoalesceParameters.PARAM_SUBDIR_LEN, value);
    }
}
