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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor;

/**
 * This class implements the Indexing interface.
 *
 * @author n67152
 */
public class PostGreSQLPersistorExt2 extends PostGreSQLPersistorExt implements ICoalesceIndexingPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostGreSQLPersistorExt.class);

    /**
     * Uses {@link PostGreSQLSettings} to configure the database settings.
     */
    public PostGreSQLPersistorExt2()
    {
        setConnectionSettings(PostGreSQLSettings.getServerConn());
        setSchema(PostGreSQLSettings.getDatabaseSchema());

    }

    /**
     * Uses {@link PostGreSQLSettings} to configure the database settings.
     *
     * @param userId User ID used for connection to the DB.
     * @param password User's password used for connecting to the DB.
     */
    public PostGreSQLPersistorExt2(String userId, String password)
    {

        PostGreSQLSettings.setUserName(userId);
        PostGreSQLSettings.setUserPassword(password);

        setConnectionSettings(PostGreSQLSettings.getServerConn());
        setSchema(PostGreSQLSettings.getDatabaseSchema());

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * #createIndex(java .lang.String, java.lang.String, boolean)
     */
    @Override
    public void createIndex(String fieldPath, String indexName, boolean concurrently) throws CoalescePersistorException
    {
        try
        {
            if (!indexExists(indexName))
            {
                String[] toks = fieldPath.split("\\.");
                if (toks == null || toks.length != 2)
                {
                    throw new CoalescePersistorException("Invalid field path:" + fieldPath);
                }
                String tableName = toks[0];
                String fieldName = toks[1];
                CoalesceDataConnectorBase conn = getDataConnector();
                StringBuilder sql = new StringBuilder("CREATE INDEX").append(concurrently ? " CONCURRENTLY " : " ").append(indexName).append(" ON ").append(getSchemaPrefix()).append(tableName).append(" ( ").append(fieldName).append(" )");
                conn.executeUpdate(sql.toString());
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("SQLException:", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * #indexExists(java .lang.String)
     */
    @Override
    public boolean indexExists(String indexName) throws CoalescePersistorException
    {
        CoalesceDataConnectorBase conn = getDataConnector();
        String schemaName = getSchema();
        boolean exists = false;
        ResultSet results = null;

        StringBuilder sql = new StringBuilder("select 1 from pg_class c join pg_namespace n on n.oid=c.relnamespace where ").append("c.relname = ? and n.nspname = ?");
        try
        {
            results = conn.executeQuery(sql.toString(), new CoalesceParameter(indexName), new CoalesceParameter(schemaName));
            if (results != null && results.next())
            {
                exists = results.getInt(1) == 1;
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("ERROR:", e);
        }

        return exists;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * #deleteIndex(java .lang.String)
     */
    @Override
    public void deleteIndex(String indexName, boolean concurrently) throws CoalescePersistorException
    {
        try
        {
            if (indexExists(indexName))
            {
                CoalesceDataConnectorBase conn = getDataConnector();
                StringBuilder sql = new StringBuilder("DROP INDEX").append(concurrently ? " CONCURRENTLY " : " ").append(getSchemaPrefix()).append(indexName);
                conn.executeUpdate(sql.toString());
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("SQLException:", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * #reindex(com.
     * incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * .EIndexTarget, java.lang.String)
     */
    @Override
    public void reindex(String indexName, boolean concurrently) throws CoalescePersistorException
    {
        try
        {
            if (indexExists(indexName))
            {
                CoalesceDataConnectorBase conn = getDataConnector();
                StringBuilder sql = new StringBuilder("REINDEX INDEX").append(getSchemaPrefix()).append(indexName);
                conn.executeUpdate(sql.toString());
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("SQLException:", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.incadencecorp.coalesce.framework.persistance.ICoalesceIndexingPersistor
     * #listIndexes()
     */
    @Override
    public List<String> listIndexes() throws CoalescePersistorException
    {
        ArrayList<String> indexList = new ArrayList<String>();
        CoalesceDataConnectorBase conn = getDataConnector();
        String schemaName = getSchema();
        ResultSet results = null;

        StringBuilder sql = new StringBuilder("select relname from pg_class c join pg_namespace n on n.oid=c.relnamespace where ").append("n.nspname = ?");
        try
        {
            results = conn.executeQuery(sql.toString(), new CoalesceParameter(schemaName));
            while (results.next())
            {
                indexList.add(results.getString("relname"));
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("ERROR:", e);
        }
        return indexList;
    }
}
