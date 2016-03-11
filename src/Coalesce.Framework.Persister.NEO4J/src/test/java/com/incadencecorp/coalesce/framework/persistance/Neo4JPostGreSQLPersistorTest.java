package com.incadencecorp.coalesce.framework.persistance;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JPostgreSQLPersistor;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLDataConnector;

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

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

/**
 *
 */
public class Neo4JPostGreSQLPersistorTest extends CoalescePersistorBaseTest {

    @Test
    public void test() throws CoalescePersistorException
    {
        Neo4JPostgreSQLPersistor persistor = new Neo4JPostgreSQLPersistor();
        persistor.setSchema("coalesce");

        ServerConn svConnNeo4J = new ServerConn();
        svConnNeo4J.setPortNumber(7474);
        svConnNeo4J.setServerName("dbsp3");
        ServerConn postgresSQLconn = getConnection();

        persistor.initialize(postgresSQLconn, svConnNeo4J);
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName("NeoTestEntity");
        entity.setSource("OMEGA");
        persistor.saveEntity(true, entity);
    }

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        CoalescePersistorBaseTest tester = new Neo4JPostGreSQLPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        Neo4JPostGreSQLPersistorTest tester = new Neo4JPostGreSQLPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {

        ServerConn conPostgres = new ServerConn();
        conPostgres.setServerName("10.0.51.90");
        conPostgres.setDatabase("OMEGA");
        conPostgres.setUser("enterprisedb");
        conPostgres.setPassword("enterprisedb");
        conPostgres.setPortNumber(5444);

        return conPostgres;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        Neo4JPostgreSQLPersistor persistor = new Neo4JPostgreSQLPersistor();

        ServerConn svConnNeo4J = new ServerConn();

        svConnNeo4J = new ServerConn();
        svConnNeo4J.setServerName("dbsp3");
        svConnNeo4J.setPortNumber(7474);

        persistor.initialize(conn, svConnNeo4J);
        persistor.setSchema("coalesce");

        return persistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new PostGreSQLDataConnector(conn, "omega");
    }

}
