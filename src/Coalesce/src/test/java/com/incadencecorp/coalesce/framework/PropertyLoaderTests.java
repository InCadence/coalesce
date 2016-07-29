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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingType;

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
        PropertyLoader loader = new PropertyLoader(new MockConnector(), "HelloWorld");
        loader.setProperty(PROPERTY_NAME, PROPERTY_VALUE);

        Assert.assertEquals(PROPERTY_VALUE, loader.getProperty(PROPERTY_NAME));

    }

    private class MockConnector implements IConfigurationsConnector {

        private Map<String, String> cache = new HashMap<String, String>();

        @Override
        public String getAddress()
        {
            return null;
        }

        @Override
        public int getPort()
        {
            return 0;
        }

        @Override
        public String getSetting(String configurationFileName,
                                 String settingPath,
                                 String defaultValue,
                                 SettingType type,
                                 Boolean setIfNotFound)
        {
            return cache.get(settingPath);
        }

        @Override
        public boolean setSetting(String configurationFileName, String settingPath, String value, SettingType type)
        {
            cache.put(settingPath, value);
            return true;
        }

        @Override
        public boolean log(String logName, String callResultXml)
        {
            // TODO Auto-generated method stub
            return false;
        }

    }

}
