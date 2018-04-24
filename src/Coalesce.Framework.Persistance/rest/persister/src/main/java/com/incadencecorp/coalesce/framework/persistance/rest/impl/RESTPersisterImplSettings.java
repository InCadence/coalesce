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

package com.incadencecorp.coalesce.framework.persistance.rest.impl;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * These are the configuration settings for {@link RESTTemplatePersisterImpl}
 *
 * @author Derek Clemenzi
 */
public class RESTPersisterImplSettings {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static String config_name = RESTTemplatePersisterImpl.class.getSimpleName() + ".properties";
    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final String DEFAULT_URL = "http://localhost:8181/cxf/data/";
    private static final String DEFAULT_TEMPLATE_URL = DEFAULT_URL + "templates";
    private static final String DEFAULT_ENTITY_URL = DEFAULT_URL + "entity";
    private static final String DEFAULT_SEARCH_URL = DEFAULT_URL + "search";

    /*--------------------------------------------------------------------------
    Property Names
    --------------------------------------------------------------------------*/

    private static final String PARAM_BASE = "com.incadencecorp.coalesce.persister.rest.";
    private static final String PARAM_TEMPLATE_BASE = PARAM_BASE + "template.";
    private static final String PARAM_ENTITY_BASE = PARAM_BASE + "entity.";
    private static final String PARAM_SEARCH_BASE = PARAM_BASE + "search.";

    /**
     * URL property ({@value PARAM_URL}) that specifies the base url.
     */
    public static final String PARAM_URL = PARAM_BASE + "url";
    /**
     * URL property ({@value PARAM_TEMPLATE_URL}) that overrides the base url and specifies the location of the template data controller.
     */
    public static final String PARAM_TEMPLATE_URL = PARAM_TEMPLATE_BASE + "url";
    /**
     * URL property ({@value PARAM_ENTITY_URL}) that overrides the base url and specifies the location of the entity data controller.
     */
    public static final String PARAM_ENTITY_URL = PARAM_ENTITY_BASE + "url";
    /**
     * URL property ({@value PARAM_SEARCH_URL}) that overrides the base url and specifies the location of the search data controller.
     */
    public static final String PARAM_SEARCH_URL = PARAM_SEARCH_BASE + "url";

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    private RESTPersisterImplSettings()
    {
        // Do Nothing
    }

    /**
     * @return the property value of {@value PARAM_URL}
     */
    public static URL getBaseUrl() throws MalformedURLException
    {
        return new URL(getBaseUrlAsString());
    }

    /**
     * Sets the property value {@value PARAM_URL}
     *
     * @param value to set
     */
    public static void setBaseUrl(URL value)
    {
        settings.setSetting(config_name, PARAM_URL, value.toString());
    }

    /**
     * @return the property value of {@value PARAM_TEMPLATE_URL}
     */
    public static URL getTemplateUrl() throws MalformedURLException
    {
        return new URL(getBaseUrlAsString());
    }

    /**
     * Sets the property value {@value PARAM_TEMPLATE_URL}
     *
     * @param value to set
     */
    public static void setTemplateUrl(URL value)
    {
        settings.setSetting(config_name, PARAM_TEMPLATE_URL, value.toString());
    }

    /**
     * @return the property value of {@value PARAM_ENTITY_URL}
     */
    public static URL getEntityUrl() throws MalformedURLException
    {
        return new URL(getBaseUrlAsString());
    }

    /**
     * Sets the property value {@value PARAM_ENTITY_URL}
     *
     * @param value to set
     */
    public static void setEntityUrl(URL value)
    {
        settings.setSetting(config_name, PARAM_ENTITY_URL, value.toString());
    }

    /**
     * @return the property value of {@value PARAM_SEARCH_URL}
     */
    public static URL getSearchUrl() throws MalformedURLException
    {
        return new URL(getSearchUrlAsString());
    }

    /**
     * Sets the property value {@value PARAM_SEARCH_URL}
     *
     * @param value to set
     */
    public static void setSearchUrl(URL value)
    {
        settings.setSetting(config_name, PARAM_SEARCH_URL, value.toString());
    }

    public static Map<String, String> getProperties()
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(PARAM_URL, getBaseUrlAsString());
        properties.put(PARAM_TEMPLATE_URL, getTemplateUrlAsString());
        properties.put(PARAM_ENTITY_URL, getEntityUrlAsString());
        properties.put(PARAM_SEARCH_URL, getSearchUrlAsString());

        return properties;
    }

    private static String getBaseUrlAsString()
    {
        return settings.getSetting(config_name, PARAM_URL, DEFAULT_URL, true);
    }

    public static String getTemplateUrlAsString()
    {
        return settings.getSetting(config_name, PARAM_TEMPLATE_URL, DEFAULT_TEMPLATE_URL, true);
    }

    public static String getEntityUrlAsString()
    {
        return settings.getSetting(config_name, PARAM_ENTITY_URL, DEFAULT_ENTITY_URL, true);
    }

    public static String getSearchUrlAsString()
    {
        return settings.getSetting(config_name, PARAM_SEARCH_URL, DEFAULT_SEARCH_URL, true);
    }

}
