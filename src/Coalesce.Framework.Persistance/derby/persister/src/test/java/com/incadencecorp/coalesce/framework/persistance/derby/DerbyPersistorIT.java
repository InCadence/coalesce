/**
 * 
 */
package com.incadencecorp.coalesce.framework.persistance.derby;

import java.nio.file.Paths;

import org.junit.BeforeClass;

import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * @author mdaconta
 *
 */
public class DerbyPersistorIT extends AbstractCoalescePersistorTest<DerbyPersistor> {

    /**
     * Initializes the test configuration.
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);
        
        DerbySettings.setConnector(connector);
    }

    @Override
    protected DerbyPersistor createPersister()
    {
        return new DerbyPersistor();
    }

}
