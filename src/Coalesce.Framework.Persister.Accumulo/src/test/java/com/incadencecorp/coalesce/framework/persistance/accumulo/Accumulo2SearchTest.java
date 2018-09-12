/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.search.AbstractSearchTest;
import org.junit.Assume;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class Accumulo2SearchTest extends AbstractSearchTest<AccumuloSearchPersistor> {

    @BeforeClass
    public static void initialize()
    {
        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            // skip these tests
            Assume.assumeTrue(String.format("JRE %s Detected. These unit tests require JRE 1.8", version), false);
        }
    }

    /**
     * TODO Need to resolve COAL-44 before removing this override.
     */
    @Override
    public void testUpdateRecordKey() throws Exception
    {
        Assume.assumeTrue(false);
    }

    @Override
    protected AccumuloSearchPersistor createPersister()
    {
        return new AccumuloSearchPersistor(getParameters());
    }

    protected Map<String, String> getParameters()
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(AccumuloDataConnector.INSTANCE_ID, "unit_test");
        parameters.put(AccumuloDataConnector.ZOOKEEPERS, "unit_test");
        parameters.put(AccumuloDataConnector.USER, "unit_test");
        parameters.put(AccumuloDataConnector.PASSWORD, "unit_test");
        parameters.put(AccumuloDataConnector.TABLE_NAME, AccumuloDataConnector.COALESCE_SEARCH_TABLE);
        parameters.put(AccumuloDataConnector.QUERY_THREADS, Integer.toString(AccumuloSettings.getQueryThreads()));
        parameters.put(AccumuloDataConnector.RECORD_THREADS, Integer.toString(AccumuloSettings.getRecordThreads()));
        parameters.put(AccumuloDataConnector.WRITE_THREADS, Integer.toString(AccumuloSettings.getWriteThreads()));
        parameters.put(AccumuloDataConnector.GENERATE_STATS, "false");
        parameters.put(AccumuloDataConnector.COLLECT_USAGE_STATS, "false");
        parameters.put(AccumuloDataConnector.CACHING, "false");
        parameters.put(AccumuloDataConnector.LOOSE_B_BOX, "false");
        parameters.put(AccumuloDataConnector.USE_MOCK, "true");
        parameters.put(AccumuloDataConnector.USE_COMPRESSION, "true");

        return parameters;
    }

}
