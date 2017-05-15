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

package com.incadencecorp.coalesce.exim.xsd;

import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;

/**
 * Configuration properties for {@link XSDGeneratorUtil}.
 * 
 * @author n78554
 */
public final class XSDSettings {

    private static final String PARAM_BASE = "coalesce.xsd.";
    private static final String PARAM_MAX_STRING = PARAM_BASE + "max-string";
    private static final String PARAM_MAX_POINTS = PARAM_BASE + "max-points";

    private static final int DEFAULT_MAX_STRING = 20;
    private static final int DEFAULT_MAX_POINTS = 10000;

    private static String config_name = XSDSettings.class.getSimpleName().toLowerCase() + ".properties";
    private static SettingsBase settings = new SettingsBase(null);

    /**
     * Default Constructor
     */
    private XSDSettings()
    {
        // Do Nothing
        config_name = "coalesce.properties";

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

    /**
     * Sets the maximum number of characters allowed in a string when generating
     * XSDs.
     * 
     * @param value
     */
    public static void setMaxString(int value)
    {
        settings.setSetting(config_name, PARAM_MAX_STRING, value);
    }

    /**
     * @return the maximum number of characters allowed in a string when
     *         generating XSDs. Defaults to {@value #DEFAULT_MAX_STRING}.
     */
    public static int getMaxString()
    {
        return settings.getSetting(config_name, PARAM_MAX_STRING, DEFAULT_MAX_STRING, true);
    }

    /**
     * Sets the maximum number of points allowed in a MULTIPOINT, LINESTRING,
     * etc when generating XSDs.
     * 
     * @param value
     */
    public static void setMaxPoints(int value)
    {
        settings.setSetting(config_name, PARAM_MAX_POINTS, value);
    }

    /**
     * @return the maximum number of points allowed in a MULTIPOINT, LINESTRING,
     *         etc when generating XSDs. Defaults to
     *         {@value #DEFAULT_MAX_POINTS}.
     */
    public static int getMaxPoints()
    {
        return settings.getSetting(config_name, PARAM_MAX_POINTS, DEFAULT_MAX_POINTS, true);
    }

}
