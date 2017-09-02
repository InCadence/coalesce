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

package com.incadencecorp.coalesce.synchronizer.api.common.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.api.common.mocks.MockOperation;
import com.incadencecorp.unity.common.connectors.ResourcePropertyConnector;

/**
 * These tests exercise the base component.
 * 
 * @author n78554
 */
public class SynchronizerComponentBaseTest {

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeDelay() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "-1");

        MockOperation operation = new MockOperation();
        operation.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumberDelay() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "A");

        MockOperation operation = new MockOperation();
        operation.setProperties(params);
    }

    /**
     * Ensures that if the name is not set it defaults to the fully qualified
     * class name.
     * 
     * @throws Exception
     */
    @Test
    public void testComponentName() throws Exception
    {
        UnitTestComponent component = new UnitTestComponent();

        Assert.assertEquals(UnitTestComponent.class.getName(), component.getName());

        component.setName("HelloWorld");

        Assert.assertEquals("HelloWorld", component.getName());
    }

    /**
     * Ensures setting parameters works correctly.
     * 
     * @throws Exception
     */
    @Test
    public void testProperties() throws Exception
    {
        UnitTestComponent component = new UnitTestComponent();
        component.setProperties(null);

        Assert.assertEquals(0, component.getParameters().size());

        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("1", "A");
        parameters.put("2", "B");
        parameters.put("3", "C");

        component.setProperties(parameters);

        Assert.assertEquals(3, component.getParameters().size());

    }

    /**
     * Ensures setting property loader works correctly.
     * 
     * @throws Exception
     */
    @Test
    public void testPropertyLoader() throws Exception
    {
        UnitTestComponent component = new UnitTestComponent();
        component.setPropertyLoader(null);

        Assert.assertNull(component.getPropertyLoader());

        PropertyLoader loader = new PropertyLoader(new ResourcePropertyConnector(), "HelloWorld");

        component.setPropertyLoader(loader);

        Assert.assertEquals(loader, component.getPropertyLoader());
    }

    private class UnitTestComponent extends CoalesceComponentImpl {

        public Map<String, String> getParameters()
        {
            return parameters;
        }

        public PropertyLoader getPropertyLoader()
        {
            return loader;
        }

    }

}
