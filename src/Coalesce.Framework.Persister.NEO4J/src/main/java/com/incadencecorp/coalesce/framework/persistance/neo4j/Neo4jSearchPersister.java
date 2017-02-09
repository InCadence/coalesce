/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;

/**
 * Extension to {@link Neo4JPersistor} that implements
 * {@link ICoalesceSearchPersistor}.
 * 
 * @author n78554
 */
public class Neo4jSearchPersister extends Neo4JPersistor implements ICoalesceSearchPersistor {

    private static final String QUERY = "MATCH (n) %s RETURN %s SKIP %s LIMIT %s";

    @Override
    public CachedRowSet search(Query query, CoalesceParameter... parameters) throws CoalescePersistorException
    {
        CachedRowSet results;

        Neo4jFilterToCypher converter = new Neo4jFilterToCypher();
        converter.setInline(false);
        converter.setDefaultLabelMapping("n");

        try
        {
            int offset = query.getMaxFeatures() * (query.getStartIndex() - 1);

            String returnValues = "n.entityKey";

            String cypher = String.format(QUERY,
                                          converter.encodeToString(query.getFilter()),
                                          returnValues,
                                          offset,
                                          query.getMaxFeatures());

            try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(Neo4jSettings.getServerConn()))
            {
                results = executeQuery(cypher);
            }
        }
        catch (CoalesceException e)
        {
            throw new CoalescePersistorException("(FAILED) Filter Encoding", e);
        }

        return results;
    }
}
