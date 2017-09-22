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

package com.incadencecorp.coalece.services.search.client.jaxws.tests;

import java.net.InetAddress;
import java.net.URL;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.services.crud.client.jaxws.CrudJaxwsClientImpl;
import com.incadencecorp.coalesce.services.search.api.test.AbstractSearchTests;
import com.incadencecorp.coalesce.services.search.client.jaxws.SearchJaxwsClientImpl;

/**
 * These unit test ensure correct behavior of the CRUD server.
 * 
 * @author Derek Clemenzi
 */
public class SearchJaxwsClientImplTestIT extends AbstractSearchTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchJaxwsClientImplTestIT.class);

    @BeforeClass
    public static void initialize() throws Exception
    {
        try
        {
            String host = "bdpingest.bdpdev.incadencecorp.com";
            URL url = new URL("http", host, 8181, "/cxf/search?wsdl");

            LOGGER.info("Metadata Manager WSDL Location: {}", url.toString());

            // Host Reachable?
            InetAddress address = InetAddress.getByName(host);
            //Assume.assumeTrue(address.isReachable(5000));

            // Create Client
            crud = new CrudJaxwsClientImpl(new URL("http", host, 8181, "/cxf/crud?wsdl"));
            client = new SearchJaxwsClientImpl(url);
        }
        catch (Exception e)
        {
            LOGGER.error("(Metadata Manager) Unit Test Initialization Failed", e);

            // Skip Integration Tests
            Assume.assumeNoException(e);
        }

    }

}
