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

import com.incadencecorp.unity.common.SettingType;
import com.incadencecorp.unity.common.connectors.MemoryConnector;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * Ensures proper operation of the {@link PropertyMapLoader}.
 *
 * @author Derek Clemenzi
 */
public class PropertyMapLoaderTest {

    private static final String PROPERTY_NAME = "prop";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY2_NAME = "prop2";
    private static final String PROPERTY2_VALUE = "value2";
    private static final String CONFIG_NAME = "HelloWorld";

    /**
     * Ensures that a property can be retrieved after settings.
     */
    @Test
    public void testPropertyLoader()
    {
        MemoryConnector connector = new MemoryConnector();
        connector.setSetting(CONFIG_NAME, PROPERTY_NAME, PROPERTY_VALUE, SettingType.ST_STRING);

        PropertyMapLoader loader = new PropertyMapLoader(connector, CONFIG_NAME);
        loader.setProperties(Collections.singletonMap(PROPERTY2_NAME, PROPERTY2_VALUE));

        Assert.assertEquals(PROPERTY_VALUE, loader.get(PROPERTY_NAME));
        Assert.assertEquals(PROPERTY2_VALUE, loader.get(PROPERTY2_NAME));
    }

}
