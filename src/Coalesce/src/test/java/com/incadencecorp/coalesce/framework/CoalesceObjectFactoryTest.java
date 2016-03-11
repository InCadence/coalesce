/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;

import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

/**
 * These unit test are for ensuring the CoalesceObjectFactory is working
 * correctly.
 * 
 * @author n78554
 *
 */
public class CoalesceObjectFactoryTest {

    /**
     * Verifies that if the class name is not know the default is used.
     */
    @Test
    public void testDefault()
    {

        // Register
        CoalesceObjectFactory.registerDefault(Test1Entity.class);
        CoalesceObjectFactory.register(Test2Entity.class);

        Test2Entity obj = new Test2Entity();
        obj.initialize();

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(obj);

        // Verify its not of type Test1Entity
        assertFalse(entity instanceof Test2Entity);

        CoalesceEntity result = CoalesceObjectFactory.createAndLoad(entity);

        // Verify
        assertTrue(result instanceof Test1Entity);
        assertTrue(result instanceof Test2Entity);

        // Sets the class name to an unknown value so the default will be used.
        entity.setAttribute("classname", "unknown");

        result = CoalesceObjectFactory.createAndLoad(entity);

        assertTrue(result instanceof Test1Entity);
        assertFalse(result instanceof Test2Entity);

        // Unregister the default
        CoalesceObjectFactory.unregisterDefault();
        CoalesceObjectFactory.unregister(Test2Entity.class);

    }

    /**
     * Verifies that an exception is thrown if the class name is unknown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnregisteredObject()
    {

        Test1Entity obj = new Test1Entity();
        obj.initialize();

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(obj);
        entity.setAttribute("classname", "avoid classpath load");

        CoalesceObjectFactory.createAndLoad(entity);

    }
    
    /**
     * Verifies that you can register a different class to an object
     */
    @Test
    public void testRegisteringAParent()
    {

        CoalesceObjectFactory.register(Test2Entity.class.getName(), Test1Entity.class);
        
        Test2Entity obj = new Test2Entity();
        obj.initialize();

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(obj);

        assertFalse(entity instanceof Test1Entity);
        assertFalse(entity instanceof Test2Entity);

        entity = CoalesceObjectFactory.createAndLoad(entity);

        assertTrue(entity instanceof Test1Entity);
        assertFalse(entity instanceof Test2Entity);

    }

}
