/*-----------------------------------------------------------------------------'
 Copyright 2019 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import com.incadencecorp.unity.common.IConfigurationsConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is designed to be used in blueprints for loading properties into components from external files. This is
 * readonly and wont modify the property file.
 *
 * @author Derek Clemenzi
 */
public class PropertyMapLoader extends HashMap<String, String> {

    /**
     * Default Constructor
     *
     * @param connector implementation used for loading properties
     * @param name      of the configuration file.
     */
    public PropertyMapLoader(IConfigurationsConnector connector, String name)
    {
        this.putAll(connector.getSettings(name));
    }

    /**
     * Adds additional properties.
     *
     * @param properties additional properties to add to the map
     */
    public void setProperties(Map<String, String> properties)
    {
        this.putAll(properties);
    }

}
