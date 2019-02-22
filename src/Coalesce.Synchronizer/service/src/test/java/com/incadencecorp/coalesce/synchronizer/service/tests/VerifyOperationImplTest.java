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

package com.incadencecorp.coalesce.synchronizer.service.tests;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.synchronizer.service.operations.VerifyOperationImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Derek Clemenzi
 */
public class VerifyOperationImplTest {

    private static final Path TEST_RESOURCES = Paths.get("src", "test", "resources");

    @BeforeClass
    public static void initialize() throws Exception
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY, TEST_RESOURCES.toString());
    }

    @Test
    public void testOperation() throws Exception
    {
        DerbyPersistor derby = new DerbyPersistor();

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertEquals(1, VerifyOperationImpl.verify(derby, new String[] { entity.getKey() }).size());

        derby.saveEntity(false, entity);

        Assert.assertEquals(0, VerifyOperationImpl.verify(derby, new String[] { entity.getKey() }).size());
    }

}
