package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AccumuloPersistorTest extends AbstractAccumuloPersistorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistorTest.class);

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        AccumuloSettings.setConnector(connector);

        CoalesceUnitTestSettings.initialize();

        AccumuloSettings.setPersistFieldDefAttr(true);
        AccumuloSettings.setPersistSectionAttr(true);
        AccumuloSettings.setPersistRecordsetAttr(true);
        AccumuloSettings.setPersistRecordAttr(true);

        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            // skip these tests
            Assume.assumeTrue(String.format("JRE %s Detected. These unit tests require JRE 1.8", version), false);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        // TODO - Delete everything from Database?
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    @Override
    protected AccumuloPersistor createPersister() throws CoalescePersistorException
    {
        //override to mockout AccumuloDataConnector
        return new AccumuloPersistor() {

            @Override
            protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
            {
                return new MockAccumuloDataConnector(getConnectionSettings());
            }

        };
    }

}
