package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class AccumuloPersistorIT extends AbstractCoalescePersistorTest<AccumuloPersistor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistorIT.class);

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        AccumuloSettings.setConnector(connector);

        AccumuloSettings.setPersistFieldDefAttr(false);
        AccumuloSettings.setPersistSectionAttr(true);
        AccumuloSettings.setPersistRecordsetAttr(false);
        AccumuloSettings.setPersistRecordAttr(false);

        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            // skip these tests
            Assume.assumeTrue(String.format("JRE %s Detected. These unit tests require JRE 1.8", version), false);
        }
    }

    @Override
    protected AccumuloPersistor createPersister() throws CoalescePersistorException
    {
        return new AccumuloPersistor();
    }

    @Override
    public String getFieldValue(String key) throws CoalescePersistorException
    {
        return (String) createPersister().getFieldValue(key);
    }
}
