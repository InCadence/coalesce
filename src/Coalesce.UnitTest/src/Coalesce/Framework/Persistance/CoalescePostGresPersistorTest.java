package Coalesce.Framework.Persistance;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import coalesce.persister.postgres.PostGresDataConnector;
import coalesce.persister.postgres.PostGresSQLPersistor;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 private static CoalesceEntity _entity;
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class CoalescePostGresPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {
        CoalescePostGresPersistorTest tester = new CoalescePostGresPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        CoalescePostGresPersistorTest tester = new CoalescePostGresPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();
        serCon.setServerName("127.0.0.1");
        serCon.setDatabase("CoalesceDatabase");
        serCon.setUser("postgres");
        serCon.setPassword("Passw0rd");

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        PostGresSQLPersistor postGresSQLPersister = new PostGresSQLPersistor();
        postGresSQLPersister.Initialize(conn);

        return postGresSQLPersister;

    }

    @Override
    protected ServerConn getInvalidConnection()
    {
        ServerConn serConFail = new ServerConn();
        serConFail.setServerName("192.168.1.1");
        serConFail.setDatabase("CoalesceDatabase");
        serConFail.setUser("postgres");
        serConFail.setPassword("Passw0rd");

        return serConFail;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new PostGresDataConnector(conn);
    }

}
