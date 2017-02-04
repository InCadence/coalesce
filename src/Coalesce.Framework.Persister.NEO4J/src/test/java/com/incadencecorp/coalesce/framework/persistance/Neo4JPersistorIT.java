package com.incadencecorp.coalesce.framework.persistance;

import java.io.IOException;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JDataConnector;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JPersistor;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4jSearchPersister;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

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

public class Neo4JPersistorIT extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {

        Neo4JPersistorIT tester = new Neo4JPersistorIT();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        Neo4JPersistorIT tester = new Neo4JPersistorIT();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();
        serCon.setServerName("dbsp3");
        serCon.setPortNumber(7474);

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        Neo4JPersistor neo4jPersistor = new Neo4JPersistor();
        neo4jPersistor.setConnectionSettings(conn);

        return neo4jPersistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new Neo4JDataConnector(conn);
    }
    
    @Test
    public void testSearchPersister() throws Exception
    {

        TestEntity entity = new TestEntity();
        entity.initialize();
        
        Neo4jSearchPersister persister = new Neo4jSearchPersister();

        persister.saveEntity(false, entity);

        Filter filter = CoalescePropertyFactory.getEntityKey(entity.getKey());
        Query query = new Query();
        query.setFilter(filter);
        query.setStartIndex(1);
        query.setMaxFeatures(50);

        CachedRowSet rowset = persister.search(query);

        if (rowset.first())
        {
            do
            {
                System.out.println(rowset.getString("n.entityKey"));
            }
            while (rowset.next());
        }

    }

}
