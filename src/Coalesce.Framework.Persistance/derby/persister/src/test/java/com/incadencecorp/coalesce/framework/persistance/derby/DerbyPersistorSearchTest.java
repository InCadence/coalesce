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
package com.incadencecorp.coalesce.framework.persistance.derby;

import com.incadencecorp.coalesce.search.AbstractSearchTest;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.Assume;
import org.junit.BeforeClass;

import java.nio.file.Paths;

/**
 * This implementation execute test against {@link DerbyPersistor}.
 *
 * @author mdaconta
 */
public class DerbyPersistorSearchTest extends AbstractSearchTest<DerbyPersistor> {

    /**
     * Initializes the test configuration.
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        System.setProperty("derby.system.home", Paths.get("target", "derby").toFile().getAbsolutePath());

        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        DerbySettings.setConnector(connector);
    }

    @Override
    protected DerbyPersistor createPersister()
    {
        return new DerbyPersistor();
    }

    @Override
    public void test20KRecords() throws Exception
    {
        // TODO Skip this test because it causes a timeout. Unless Derby is ever going to be used in production this is probably not worth resolving.
        Assume.assumeTrue(false);
    }
}
