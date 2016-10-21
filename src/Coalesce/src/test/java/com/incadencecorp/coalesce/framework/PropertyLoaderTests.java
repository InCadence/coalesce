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

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.unity.common.connectors.MemoryConnector;

/**
 * Ensures proper operation of the {@link PropertyLoader}.
 * 
 * @author n78554
 */
public class PropertyLoaderTests {

    private static final String PROPERTY_NAME = "prop";
    private static final String PROPERTY_VALUE = "value";

    /**
     * This test ensures that a null pointer exception is thrown when getting
     * settings if the connector is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetFailure()
    {
        PropertyLoader loader = new PropertyLoader(null, "HelloWorld");
        loader.getProperty(PROPERTY_NAME);
    }

    /**
     * This test ensures that a null pointer exception is thrown when setting
     * settings if the connector is null.
     */
    @Test(expected = NullPointerException.class)
    public void testSetFailure()
    {
        PropertyLoader loader = new PropertyLoader(null, "HelloWorld");
        loader.setProperty(PROPERTY_NAME, PROPERTY_VALUE);
    }

    /**
     * Ensures that a property can be retrieved after settings.
     */
    @Test
    public void testPropertyLoader()
    {
        PropertyLoader loader = new PropertyLoader(new MemoryConnector(), "HelloWorld");
        loader.setProperty(PROPERTY_NAME, PROPERTY_VALUE);

        Assert.assertEquals(PROPERTY_VALUE, loader.getProperty(PROPERTY_NAME));

    }

}
