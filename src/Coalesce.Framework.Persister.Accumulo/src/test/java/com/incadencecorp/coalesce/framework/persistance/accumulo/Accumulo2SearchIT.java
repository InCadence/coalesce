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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.NonGeoEntity;
import com.incadencecorp.coalesce.search.AbstractSearchTest;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class Accumulo2SearchIT extends AbstractSearchTest<AccumuloSearchPersistor> {

    private boolean useMock = true;

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

    @Override
    protected AccumuloSearchPersistor createPersister()
    {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(AccumuloDataConnector.INSTANCE_ID, AccumuloSettings.getDatabaseName());
        parameters.put(AccumuloDataConnector.ZOOKEEPERS, AccumuloSettings.getZookeepers());
        parameters.put(AccumuloDataConnector.USER, (useMock) ? "aa" : AccumuloSettings.getUserName());
        parameters.put(AccumuloDataConnector.PASSWORD, AccumuloSettings.getUserPassword());
        parameters.put(AccumuloDataConnector.TABLE_NAME, AccumuloDataConnector.COALESCE_SEARCH_TABLE);
        parameters.put(AccumuloDataConnector.QUERY_THREADS, "1");
        parameters.put(AccumuloDataConnector.RECORD_THREADS, "1");
        parameters.put(AccumuloDataConnector.WRITE_THREADS, "1");
        parameters.put(AccumuloDataConnector.GENERATE_STATS, "false");
        parameters.put(AccumuloDataConnector.COLLECT_USAGE_STATS, "false");
        parameters.put(AccumuloDataConnector.CACHING, "false");
        parameters.put(AccumuloDataConnector.LOOSE_B_BOX, "false");
        parameters.put(AccumuloDataConnector.USE_MOCK, Boolean.toString(useMock));


        return new AccumuloSearchPersistor(parameters);
    }

}
