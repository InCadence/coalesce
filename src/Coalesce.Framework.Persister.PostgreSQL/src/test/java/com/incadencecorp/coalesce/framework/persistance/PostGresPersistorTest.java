package com.incadencecorp.coalesce.framework.persistance;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLDataConnector;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLPersistor;

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

public class PostGresPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        PostGresPersistorTest tester = new PostGresPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass() 
    {
        PostGresPersistorTest tester = new PostGresPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();

        // InCadence Settings
        // serCon.setServerName("127.0.0.1");
        // serCon.setDatabase("CoalesceDatabase");
        // serCon.setUser("postgres");
        // serCon.setPassword("Passw0rd");

        // ACINT Settings
        serCon.setServerName("dbsp3");
        serCon.setDatabase("DSS_SNAPSHOT");
        serCon.setUser("enterprisedb");
        serCon.setPassword("enterprisedb");
        serCon.setPortNumber(5444);

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        PostGreSQLPersistor postGresSQLPersistor = new PostGreSQLPersistor();
        postGresSQLPersistor.setConnectionSettings(conn);
        postGresSQLPersistor.setSchema("coalesce");

        return postGresSQLPersistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new PostGreSQLDataConnector(conn, null);
    }
    
}
