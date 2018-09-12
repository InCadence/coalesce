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

package com.incadencecorp.coalesce.framework.persistance.soap.impl;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * These are the configuration settings for {@link SOAPPersisterImpl}
 *
 * @author Derek Clemenzi
 */
public class SOAPSettings {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static String config_name = SOAPPersisterImpl.class.getSimpleName() + ".properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_URL = "http://localhost:8181/cxf/";

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "com.incadencecorp.coalesce.persister.soap.";
    private static final String PARAM_CRUD_BASE = PARAM_BASE + "crud.";
    private static final String PARAM_SEARCH_BASE = PARAM_BASE + "search.";

    /**
     * URL that specifies the location of the CRUD service.
     */
    public static final String PROPERTY_CRUD_URL = PARAM_CRUD_BASE + "url";

    /**
     * URL that specifies the location of the Search service.
     */
    public static final String PROPERTY_SEARCH_URL = PARAM_SEARCH_BASE + "dbUser";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private SOAPSettings()
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
     * @return the property value of {@value PROPERTY_CRUD_URL}
     */
    public static URL getCrudUrl() throws MalformedURLException
    {
        return new URL(getCrudUrlAsString());
    }

    /**
     * Sets the property value {@value PROPERTY_CRUD_URL}
     *
     * @param value to set
     */
    public static void setCrudUrl(URL value)
    {
        settings.setSetting(config_name, PROPERTY_CRUD_URL, value.toString());
    }

    /**
     * @return the property value of {@value PROPERTY_SEARCH_URL}
     */
    public static URL getSearchUrl() throws MalformedURLException
    {
        return new URL(getSearchUrlAsString());
    }

    /**
     * Sets the property value {@value PROPERTY_SEARCH_URL}
     *
     * @param value to set
     */
    public static void setSearchUrl(URL value)
    {
        settings.setSetting(config_name, PROPERTY_SEARCH_URL, value.toString());
    }

    public static Map<String, String> getProperties()
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(PROPERTY_CRUD_URL, getCrudUrlAsString());
        properties.put(PROPERTY_SEARCH_URL, getSearchUrlAsString());

        return properties;
    }

    private static String getCrudUrlAsString()
    {
        return settings.getSetting(config_name, PROPERTY_CRUD_URL, DEFAULT_URL + "crud?wsdl", true);
    }

    private static String getSearchUrlAsString()
    {
        return settings.getSetting(config_name, PROPERTY_SEARCH_URL, DEFAULT_URL + "search?wsdl", true);
    }
}
