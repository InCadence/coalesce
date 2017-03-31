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

package com.incadencecorp.coalesce.framework.persistance;

import java.io.StringWriter;
import java.sql.ResultSet;

import org.geotools.data.Query;
import org.junit.Test;
import org.opengis.filter.Filter;

import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4jFilterToCypher;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4jSearchPersister;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

public class Neo4jFilterToCypherTest {

    @Test
    public void testFilterToCypher() throws Exception
    {

        StringWriter writer = new StringWriter();

        Filter filter = CoalescePropertyFactory.getEntityKey("hello World");

        Neo4jFilterToCypher cypher = new Neo4jFilterToCypher(writer);
        cypher.encode(filter);

        cypher.setInline(true);
        cypher.setDefaultLabelMapping("b");

        System.out.println(writer.toString());

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

        ResultSet rowset = persister.search(query).getResults();

        if (rowset.first())
        {
            do
            {
                System.out.println(rowset.getString(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey())));
            }
            while (rowset.next());
        }

    }

}
