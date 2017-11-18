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

import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.geotools.data.Query;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.expression.PropertyName;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JPersistor;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4jSearchPersister;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4jSettings;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/**
 * These unit test ensure proper operation of the persister against a neo4j
 * database.
 * 
 * @author n78554
 *
 */
public class Neo4JPersistorIT extends AbstractCoalescePersistorTest<Neo4JPersistor> {

    /**
     * Initializes the test configuration.
     */
    @BeforeClass
    public static void initialize()
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);
        
        Neo4jSettings.setConnector(connector);
    }

    @Override
    protected Neo4JPersistor createPersister()
    {
        Neo4jSearchPersister persister = new Neo4jSearchPersister();
        persister.setIncludeXml(true);
        return persister;
    }
    
    /**
     * This unit test ensures that when creating linkages within Neo4j place
     * holders are created to preserve link information. Once the place holder's
     * entity is saved its filled out.
     * 
     * @throws Exception
     */
    @Test
    public void testPlaceHolderCreation() throws Exception
    {
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        // Set Field Values
        CoalesceBooleanField field1 = entity1.addRecord1().getBooleanField();
        field1.setValue(false);

        CoalesceBooleanField field2 = entity2.addRecord1().getBooleanField();
        field2.setValue(true);

        // Link Entities
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.IS_PARENT_OF, entity2,new Marking("(U)"), "", "", "Test Label", Locale.US, true, false, false);

        Neo4jSearchPersister persistor = new Neo4jSearchPersister();

        // Create Property List
        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(CoalescePropertyFactory.getName());
        properties.add(CoalescePropertyFactory.getSource());
        properties.add(CoalescePropertyFactory.getEntityType());
        properties.add(CoalescePropertyFactory.getEntityTitle());
        properties.add(CoalescePropertyFactory.getFieldProperty(field1));

        // Create Query for Entity1
        Query query = new Query();
        query.setFilter(CoalescePropertyFactory.getEntityKey(entity1.getKey()));
        query.setStartIndex(1);
        query.setMaxFeatures(50);
        query.setProperties(properties);

        ResultSet rowset;

        // Save Entity1 (Should create a place holder for entity2)
        persistor.saveEntity(false, entity1);

        // Verify Entity1 was saved
        rowset = persistor.search(query).getResults();

        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()),
                            rowset.getMetaData().getColumnName(1));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName()),
                            rowset.getMetaData().getColumnName(2));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource()),
                            rowset.getMetaData().getColumnName(3));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityType()),
                            rowset.getMetaData().getColumnName(4));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityTitle()),
                            rowset.getMetaData().getColumnName(5));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(field1), rowset.getMetaData().getColumnName(6));

        // Create Query for Entity2
        query.setFilter(CoalescePropertyFactory.getEntityKey(entity2.getKey()));

        // Verify Entity2 place holder was created
        rowset = persistor.search(query).getResults();

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(entity2.getName(), rowset.getString(2));
        Assert.assertEquals(entity2.getSource(), rowset.getString(3));
        Assert.assertNull(rowset.getString(4));
        Assert.assertNull(rowset.getString(5));
        Assert.assertNull(rowset.getString(6));

        // Save Entity2
        persistor.saveEntity(false, entity2);

        // Verify Entity2 was saved
        rowset = persistor.search(query).getResults();

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(entity2.getTitle(), rowset.getString(5));
        Assert.assertEquals(Boolean.toString(field2.getValue()), rowset.getString(6));

    }
}
