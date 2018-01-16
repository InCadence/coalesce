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

package com.incadencecorp.coalesce.persistance.soap.impl.test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.framework.persistance.soap.impl.SOAPPersisterImpl;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.search.service.client.SearchFrameworkClientImpl;
import org.junit.BeforeClass;

import java.nio.file.Paths;

/**
 * These test create a framework implementation of the CRUD and Search API using a Derby Persister.
 *
 * @author Derek Clemenzi
 */
public class SOAPPersisterImplTest extends AbstractCoalescePersistorTest<SOAPPersisterImpl> {

    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY,
                           Paths.get("src", "test", "resources").toString());
    }

    @Override
    protected SOAPPersisterImpl createPersister() throws CoalescePersistorException
    {
        DerbyPersistor persistor = new DerbyPersistor();

        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(persistor);

        return new SOAPPersisterImpl(new CrudFrameworkClientImpl(framework), new SearchFrameworkClientImpl(persistor));
    }
}
