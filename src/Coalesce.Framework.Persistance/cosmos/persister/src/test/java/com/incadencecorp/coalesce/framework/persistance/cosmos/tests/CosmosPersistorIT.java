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

package com.incadencecorp.coalesce.framework.persistance.cosmos.tests;

import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosPersistor;
import com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosSettings;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.BeforeClass;

import java.nio.file.Paths;

/**
 * This implementation execute test against {@link com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosPersistor}.
 *
 * @author Derek Clemennzi
 */
public class CosmosPersistorIT extends AbstractCoalescePersistorTest<CosmosPersistor> {

    /**
     * Initializes the test configuration.
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        CosmosSettings.setConnector(connector);
        CoalesceSettings.setConnector(connector);
    }

    @Override
    protected CosmosPersistor createPersister()
    {
        return new CosmosPersistor();
    }

}