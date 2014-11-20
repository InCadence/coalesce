package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.mysql.MySQLPersistor;

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
public class Neo4JMySQLPersistor extends MySQLPersistor {

    private ServerConn _svConnNeo4j;

    public void initialize(ServerConn svConn, ServerConn svConnNeo4j)
    {
        super.initialize(svConn);

        _svConnNeo4j = svConnNeo4j;
    }

    @Override
    protected boolean flattenObject(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        try (Neo4JDataConnector connNeo4J = new Neo4JDataConnector(_svConnNeo4j))
        {
            connNeo4J.executeCmd("CREATE CONSTRAINT ON (item:" + entity.getName() + ") ASSERT item.EntityKey IS UNIQUE");

            // Persist Entity to Neo4J
            persistEntityObject(entity, connNeo4J);

            try (CoalesceDataConnectorBase conn = super.getDataConnector())
            {
                // Persist (Recursively)
                isSuccessful = updateCoalesceObject(entity, connNeo4J, conn, allowRemoval);

                // Persist Entity Last to Include Changes
                if (isSuccessful)
                {
                    // Persist Entity to DB
                    isSuccessful = persistEntityObject(entity, conn);
                }
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("flattenObject", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("flattenObject", e);
        }

        return isSuccessful;
    }

    private boolean updateCoalesceObject(CoalesceObject coalesceObject,
                                         Neo4JDataConnector connNeo,
                                         CoalesceDataConnectorBase conn,
                                         boolean allowRemoval) throws SQLException

    {
        boolean isSuccessful = false;

        if (coalesceObject.getFlatten())
        {
            switch (coalesceObject.getStatus()) {
            case ACTIVE:
                // Persist Object
                isSuccessful = persistObject(coalesceObject, connNeo, conn);
                break;

            case DELETED:
                if (allowRemoval)
                {
                    // Delete Object
                    // TODO: Implement for Neo4J
                    isSuccessful = deleteObject(coalesceObject, conn);
                }
                else
                {
                    // Mark Object as Deleted
                    isSuccessful = persistObject(coalesceObject, connNeo, conn);
                }

                break;

            default:
                isSuccessful = false;
            }

            // Successful?
            if (isSuccessful)
            {
                // Yes; Iterate Through Children
                for (CoalesceObject childObject : coalesceObject.getChildCoalesceObjects().values())
                {
                    updateCoalesceObject(childObject, connNeo, conn, allowRemoval);
                }
            }
        }
        return isSuccessful;
    }

    private boolean persistObject(CoalesceObject coalesceObject, Neo4JDataConnector connNeo, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        boolean isSuccessful;

        switch (coalesceObject.getType().toLowerCase()) {
        case "linkage":
            // Override Behavior
            isSuccessful = persistLinkageObject((CoalesceLinkage) coalesceObject, connNeo);
            break;
        default:
            isSuccessful = super.persistObject(coalesceObject, conn);
        }

        return isSuccessful;
    }

    private boolean persistEntityObject(CoalesceEntity entity, Neo4JDataConnector conn) throws SQLException
    {

        // Obtain a list of all field values
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        this.getFieldValues(entity, fieldValues);

        CoalesceParameter[] parameters = new CoalesceParameter[fieldValues.size() + 4];

        parameters[0] = new CoalesceParameter(entity.getTitle());
        parameters[1] = new CoalesceParameter(entity.getLastModified());
        parameters[2] = new CoalesceParameter(entity.getSource());
        parameters[3] = new CoalesceParameter(entity.getKey());

        int idx = 4;
        String params = "";

        // Add Field Values
        for (Entry<String, String> entry : fieldValues.entrySet())
        {
            parameters[idx] = new CoalesceParameter(entry.getValue());

            idx = idx + 1;

            params = params + ", Entity." + entry.getKey() + " = {" + idx + "}";
        }

        String query = "MERGE (Entity:"
                + entity.getName()
                + " {EntityKey: {4}})"
                + " ON CREATE SET Entity.Title = {1}, Entity.LastModified = {2}, Entity.EntitySource = {3}, Entity.EntityKey = {4}"
                + params + " ON MATCH SET Entity.Title = {1}, Entity.LastModified = {2}" + params;

        return conn.executeCmd(query, parameters);
    }

    private boolean persistLinkageObject(CoalesceLinkage linkage, Neo4JDataConnector conn) throws SQLException
    {
        String query;

        if (linkage.isMarkedDeleted())
        {
            // Delete Link
            query = "MATCH (n1:" + linkage.getEntity1Name() + " {EntityKey: {1}})-[rel:" + linkage.getLinkType() + "]->(n2:"
                    + linkage.getEntity2Name() + " {EntityKey: {2}}) DELETE rel";
        }
        else
        {
            // Add / Update Link
            query = "MATCH (n1:" + linkage.getEntity1Name() + " {EntityKey: {1}}), (n2:" + linkage.getEntity2Name()
                    + " {EntityKey: {2}}) CREATE UNIQUE (n1)-[:" + linkage.getLinkType() + "]->(n2)";
        }

        // Execute Query
        return conn.executeCmd(query,
                               new CoalesceParameter(linkage.getEntity1Key()),
                               new CoalesceParameter(linkage.getEntity2Key()));
    }

    private void persistEntityObject(CoalesceEntity entity, GraphDatabaseService graphDB)
    {

        // Obtain a list of all field values
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        this.getFieldValues(entity, fieldValues);

        try (Transaction tx = graphDB.beginTx())
        {
            Label label = DynamicLabel.label(entity.getName());

            Node entityNode;

            // Search for Entity
            try (ResourceIterator<Node> entities = graphDB.findNodesByLabelAndProperty(label, "key", entity.getKey()).iterator())
            {
                if (entities.hasNext())
                {
                    // Get First Entity
                    entityNode = entities.next();
                }
                else
                {
                    // Create New Entity
                    entityNode = graphDB.createNode();
                    entityNode.addLabel(label);
                    entityNode.setProperty("name", entity.getName());
                    entityNode.setProperty("source", entity.getSource());
                    entityNode.setProperty("key", entity.getKey());
                }
            }

            entityNode.setProperty("title", entity.getTitle());
            entityNode.setProperty("lastmodified", entity.getLastModified());

            // Add Field Values
            for (Entry<String, String> entry : fieldValues.entrySet())
            {
                entityNode.setProperty(entry.getKey(), entry.getValue());
            }

            tx.success();
        }
    }

    private void getFieldValues(CoalesceObject coalesceObject, HashMap<String, String> results)
    {
        // Is Active?
        if (coalesceObject.getStatus() == ECoalesceObjectStatus.ACTIVE)
        {
            // Yes; Is a CoalesceField?
            if (coalesceObject.getType().equalsIgnoreCase("field"))
            {
                // Yes; Check Data Type
                CoalesceField<?> field = (CoalesceField<?>) coalesceObject;

                switch (field.getDataType()) {
                case BINARY_TYPE:
                case FILE_TYPE:
                    // Ignore these types.
                    break;
                default:
                    // Add field value to results
                    results.put(field.getName().replace(" ", ""), field.getBaseValue());
                    break;
                }
            }

            // Recurse Through Children
            for (CoalesceObject child : coalesceObject.getChildCoalesceObjects().values())
            {
                getFieldValues(child, results);
            }
        }
    }

}
