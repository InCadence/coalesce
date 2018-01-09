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

package com.incadencecorp.coalesce.services.client.common.jaxws.util;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Settings used by {@link JAXWSUtil}.
 *
 * @author Derek Clemenzi
 */
public class JAXWSUtilSettings {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static String config_name = JAXWSUtil.class.getSimpleName() + ".properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_BUS_CONFIGURATION = Paths.get("src",
                                                                      "test",
                                                                      "resources",
                                                                      "wssec-client.xml").toAbsolutePath().toString();

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "com.incadencecorp.coalesce.jaxws.util.";
    private static final String PARAM_BUS_BASE = PARAM_BASE + "bus.";

    /**
     * File path to the bus configuration.
     */
    public static final String PROPERTY_BUS_CONFIGURATION = PARAM_BUS_BASE + "config_location";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private JAXWSUtilSettings()
    {
        // Do Nothing
    }

    /**
     * @return the property value of {@value PROPERTY_BUS_CONFIGURATION}
     */
    public static Path getBusConfiguration()
    {
        return Paths.get(getBusConfigurationAsString()).toAbsolutePath();
    }

    /**
     * Sets the property value {@value PROPERTY_BUS_CONFIGURATION}
     *
     * @param value
     */
    public static void setBusConfiguration(Path value)
    {
        settings.setSetting(config_name, PROPERTY_BUS_CONFIGURATION, value.toString());
    }

    public static Map<String, String> getProperties()
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(PROPERTY_BUS_CONFIGURATION, getBusConfigurationAsString());

        return properties;
    }

    private static String getBusConfigurationAsString()
    {
        return settings.getSetting(config_name, PROPERTY_BUS_CONFIGURATION, DEFAULT_BUS_CONFIGURATION, true);
    }

}
