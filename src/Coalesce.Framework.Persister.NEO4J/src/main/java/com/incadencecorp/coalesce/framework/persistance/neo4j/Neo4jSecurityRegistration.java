/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.SQLException;

import com.incadencecorp.coalesce.api.ISecurityRegistration;
import com.incadencecorp.coalesce.common.classification.MarkingValue;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;

/**
 * This is the Neo4j implementation.
 * 
 * @author n78554
 * @deprecated
 * @since 0.0.12-SNAPSHOT
 * @see Neo4JRegistration
 */
public class Neo4jSecurityRegistration implements ISecurityRegistration<Neo4JDataConnector> {

    private static final String MERGE_SECURITY_GROUP = "MERGE (n:SECURITY_GROUP {name: {1}})";

    private static final String MERGE_CLASSIFICATION = "MERGE (n:CLASSIFICATION_LEVEL {name: {1}})";

    private static final String CLASSIFICATION_LINK = "MATCH (n1:CLASSIFICATION_LEVEL {name: {1}}), (n2:CLASSIFICATION_LEVEL {name: {2}}) "
            + "CREATE UNIQUE (n1)-[:IS_LOWER_THAN]->(n2)";

    private static final String CYPHER_CLS_CONSTRAINT = "CREATE CONSTRAINT ON (n:CLASSIFICATION_LEVEL) ASSERT n.name IS UNIQUE";

    private Neo4JDataConnector conn;

    /**
     * @param conn
     */
    public Neo4jSecurityRegistration(Neo4JDataConnector conn)
    {
        this.conn = conn;
    }

    @Override
    public void registerClassificationLevels(MarkingValue... values) throws SQLException
    {

        CoalesceParameter[] parameters;

        conn.executeQuery(CYPHER_CLS_CONSTRAINT);

        if (values.length != 0)
        {

            for (int ii = 0; ii < values.length; ii++)
            {

                // Create Parameters
                parameters = new CoalesceParameter[1];
                parameters[0] = new CoalesceParameter(values[ii].getTitle());

                // Execute Query
                conn.executeUpdate(MERGE_CLASSIFICATION, parameters);

                // First Classification?
                if (ii != 0)
                {

                    // No; Link to the previous
                    parameters = new CoalesceParameter[2];
                    parameters[0] = new CoalesceParameter(values[ii - 1].getTitle());
                    parameters[1] = new CoalesceParameter(values[ii].getTitle());

                    // Execute Query
                    conn.executeUpdate(CLASSIFICATION_LINK, parameters);
                }

            }
        }

    }

}
