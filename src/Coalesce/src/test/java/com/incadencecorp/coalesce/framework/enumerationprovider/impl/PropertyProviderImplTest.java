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

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.vividsolutions.jts.util.Assert;

/**
 * These test ensure proper behavior of the PropertyProviderImpl
 * 
 * @author n78554
 *
 */
public class PropertyProviderImplTest {

    /**
     * Initializes these tests
     */
    @BeforeClass
    public static void initialize()
    {
        EnumerationProviderUtil.setEnumerationProviders(new PropertyEnumerationProviderImpl("src" + File.separator + "test"
                + File.separator + "resources"));
    }

    /**
     * This test ensures that an exception is thrown if you specify an invalid
     * enumeration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidResourceFiles()
    {
        EnumerationProviderUtil.getValues(null, "test");
    }

    /**
     * This test ensures that the resource file can be read and used as an
     * enumeration.
     */
    @Test
    public void testValidResourceFiles()
    {
        List<String> values = EnumerationProviderUtil.getValues(null, "valid");

        Assert.equals(2, values.size());
        Assert.isTrue(values.contains("HELLO"));
        Assert.isTrue(values.contains("WORLD"));
    }

    @Test
    public void testValidFiles()
    {
        PropertyEnumerationProviderImpl provider = new PropertyEnumerationProviderImpl();

        provider.setPaths("./src/test/resources");

        List<String> values = provider.lookup(null, "valid");

        Assert.equals(2, values.size());
        Assert.isTrue(values.contains("HELLO"));
        Assert.isTrue(values.contains("WORLD"));
    }

}
