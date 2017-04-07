/**
 * 
 */
package com.incadencecorp.coalesce.framework.persistance.derby;

import java.nio.file.Paths;

import org.junit.BeforeClass;

import com.incadencecorp.coalesce.search.AbstractSearchTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * @author mdaconta
 *
 */
public class DerbyPersistorSearchIT extends AbstractSearchTest<DerbyPersistor> {

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
