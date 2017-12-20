package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.AbstractSearchTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * @deprecated
 */
public class AccumuloSearchIT extends AbstractSearchTest<AccumuloPersistor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloSearchIT.class);

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
            LOGGER.warn("JRE {} Detected. These unit tests require JRE 1.8", version);
            LOGGER.warn("Skipping unit tests");
            // skip these tests
            Assume.assumeTrue(false);
        }
    }

    @Override
    protected AccumuloPersistor createPersister()
    {
        try
        {
            return new AccumuloPersistor();
        }
        catch (CoalescePersistorException e)
        {
            return null;
        }
    }

}
