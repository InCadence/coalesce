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

package com.incadencecorp.coalesce.services.search.service.client.tests;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.search.api.test.AbstractSearchTests;
import com.incadencecorp.coalesce.services.search.service.client.SearchFrameworkClientImpl;
import org.junit.BeforeClass;

/**
 * These unit test ensure correct behavior of the CRUD server.
 *
 * @author Derek Clemenzi
 */
public class SearchFrameworkClientImplTest extends AbstractSearchTests {

    @BeforeClass
    public static void initialize() throws Exception
    {
        DerbyPersistor persistor = new DerbyPersistor();

        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(persistor);

        crud = new CrudFrameworkClientImpl(framework);
        client = new SearchFrameworkClientImpl(persistor);
    }

}
