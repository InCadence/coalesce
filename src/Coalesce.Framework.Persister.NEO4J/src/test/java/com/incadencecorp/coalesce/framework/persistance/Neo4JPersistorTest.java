package com.incadencecorp.coalesce.framework.persistance;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JDataConnector;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JPersistor;

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

public class Neo4JPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        Neo4JPersistorTest tester = new Neo4JPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        Neo4JPersistorTest tester = new Neo4JPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();
        serCon.setPortNumber(7474);

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        Neo4JPersistor neo4jPersistor = new Neo4JPersistor();
        neo4jPersistor.initialize(conn);

        return neo4jPersistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new Neo4JDataConnector(conn);
    }

}
