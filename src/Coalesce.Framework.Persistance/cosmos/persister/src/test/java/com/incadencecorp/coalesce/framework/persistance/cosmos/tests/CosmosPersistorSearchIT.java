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
import com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosSettings;
import com.incadencecorp.coalesce.search.AbstractSearchTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.Assume;
import org.junit.BeforeClass;

import java.nio.file.Paths;

/**
 * This implementation execute test against {@link com.incadencecorp.coalesce.framework.persistance.cosmos.CosmosSearchPersistor}.
 *
 * @author Derek Clemenzi
 */
public class CosmosPersistorSearchIT extends AbstractSearchTest<CosmosSearchPersistor> {

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
    protected CosmosSearchPersistor createPersister()
    {
        return new CosmosSearchPersistor();
    }

    @Override
    public void test20KRecords() throws Exception
    {
        Assume.assumeTrue("Skip this test because it causes a timeout.", false);
    }

    @Override
    public void testPaging() throws Exception
    {
        Assume.assumeTrue("Offset appears not to be supported", false);
    }

    @Override
    public void testUpdateRecordKey() throws Exception
    {
        Assume.assumeTrue("Handle phantom records", false);
    }
}
