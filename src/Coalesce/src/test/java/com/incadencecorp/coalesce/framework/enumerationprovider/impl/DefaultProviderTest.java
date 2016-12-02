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


package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * These test ensures that you can initialize {@link EnumerationProviderUtil}
 * from a connector.
 * 
 * @author n78554
 */
public class DefaultProviderTest {

    /**
     * This test initializes the utility using a property file.
     */
    @Test
    public void test()
    {
        EnumerationProviderUtil.setEnumerationProviders();

        Assert.assertFalse(EnumerationProviderUtil.isInitialized());

        EnumerationProviderUtil.initializeFromConnector(new FilePropertyConnector(Paths.get("src", "test", "resources")));

        Assert.assertTrue(EnumerationProviderUtil.isInitialized());
    }

    enum MyEnum
    {
        HELLO, WORLD
    }

}
