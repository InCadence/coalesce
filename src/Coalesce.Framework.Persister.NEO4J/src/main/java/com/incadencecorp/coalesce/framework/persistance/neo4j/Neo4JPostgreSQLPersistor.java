package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
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

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

/**
 * Persister used to store Coalesce entities in PostGres and their relationships
 * within Neo4j.
 */
public class Neo4JPostgreSQLPersistor extends PostGreSQLPersistor {

    private ServerConn _svConnNeo4j;

    /**
     * @param svConn
     * @param svConnNeo4j
     */
    public void initialize(ServerConn svConn, ServerConn svConnNeo4j)
    {
        initializeNeo4j(svConnNeo4j);

        super.initialize(svConn);
    }

    /**
     * @param svConnNeo4j
     */
    public void initializeNeo4j(ServerConn svConnNeo4j)
    {
        _svConnNeo4j = svConnNeo4j;
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        if (_svConnNeo4j == null)
        {
            throw new CoalescePersistorException("Neo4J connection is not configured", null);
        }

        Neo4JDataConnector connNeo4J = new Neo4JDataConnector(_svConnNeo4j);
        CoalesceDataConnectorBase conn = super.getDataConnector();

        try
        {

            connNeo4J.openConnection(false);
            conn.openConnection(false);

            for (CoalesceEntity entity : entities)
            {
                // Persist Entity to Neo4J
                persistEntityObject(entity, connNeo4J);

                connNeo4J.executeCmd("CREATE CONSTRAINT ON (item:" + entity.getName().replace(" ", "_")
                        + ") ASSERT item.EntityKey IS UNIQUE");

                // Persist (Recursively)
                isSuccessful = updateCoalesceObject(entity, connNeo4J, conn, allowRemoval);
            }

            conn.commit();
            connNeo4J.commit();
        }
        catch (Exception e)
        {
            conn.rollback();
            connNeo4J.rollback();

            throw new CoalescePersistorException("flattenObject", e);
        }
        finally
        {
            conn.close();
            connNeo4J.close();
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
            case READONLY:
            case ACTIVE:
                // Persist Object;
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
            isSuccessful = persistObject(coalesceObject, conn);
        }

        return isSuccessful;
    }

    private boolean persistEntityObject(CoalesceEntity entity, Neo4JDataConnector conn) throws SQLException
    {

        // Obtain a list of all field values
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        getFieldValues(entity, fieldValues);

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

            params = params + ", Entity." + entry.getKey().toLowerCase() + " = {" + idx + "}";
        }

        String query = "MERGE (Entity:"
                + entity.getName().replace(" ", "_")
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
            query = "MATCH (n1:" + linkage.getEntity1Name().replace(" ", "_") + " {EntityKey: {1}})-[rel:"
                    + linkage.getLinkType() + "]->(n2:" + linkage.getEntity2Name().replace(" ", "_")
                    + " {EntityKey: {2}}) DELETE rel";
        }
        else
        {
            // Add / Update Link
            query = "MATCH (n1:" + linkage.getEntity1Name().replace(" ", "_") + " {EntityKey: {1}}), (n2:"
                    + linkage.getEntity2Name().replace(" ", "_") + " {EntityKey: {2}}) CREATE UNIQUE (n1)-[:"
                    + linkage.getLinkType() + "]->(n2)";
        }

        // Execute Query
        return conn.executeCmd(query,
                               new CoalesceParameter(linkage.getEntity1Key()),
                               new CoalesceParameter(linkage.getEntity2Key()));
    }

    private void getFieldValues(CoalesceObject coalesceObject, HashMap<String, String> results)
    {
        // Is Active?
        if (!coalesceObject.isMarkedDeleted())
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
