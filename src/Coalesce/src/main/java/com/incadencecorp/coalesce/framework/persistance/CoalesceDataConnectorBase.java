package com.incadencecorp.coalesce.framework.persistance;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;

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
 * Defines default behavior for Data Connectors that extends this abstract
 * class.
 */
public abstract class CoalesceDataConnectorBase implements AutoCloseable {

    private static Logger LOGGER = LoggerFactory.getLogger(CoalesceDataConnectorBase.class);

    /*-----------------------------------------------------------------------------'
    Private Member Variables
    -----------------------------------------------------------------------------*/

    protected Connection _conn = null;
    protected ServerConn _settings;

    /*-----------------------------------------------------------------------------'
    Abstract Functions
    -----------------------------------------------------------------------------*/

    protected abstract Connection getDBConnection() throws SQLException;

    protected abstract String getProcedurePrefix();

    /*-----------------------------------------------------------------------------'
    Public Functions
    -----------------------------------------------------------------------------*/

    /**
     * Opens a connection.
     *
     * @param autocommit
     * @throws SQLException
     */
    public void openConnection(boolean autocommit) throws SQLException
    {
        _conn = getDBConnection();
        _conn.setAutoCommit(autocommit);
    }

    /**
     * Returns the results from the executed SQL Command.
     *
     * @param sql the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL
     *            statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     */
    public final ResultSet executeQuery(final String sql, final CoalesceParameter... parameters) throws SQLException
    {
        ResultSet results = null;
        openDataConnection();

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Executing: {}", sql);
        }
        
        PreparedStatement stmt = _conn.prepareStatement(sql);

        if (parameters != null)
        {
            if (LOGGER.isTraceEnabled() && parameters.length > 0)
            {
                LOGGER.trace("Parameters:");
            }

            // Add Parameters
            for (int ii = 0; ii < parameters.length; ii++)
            {
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("\t{}:{}", parameters[ii].getValue(), parameters[ii].getType());
                }

                stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
            }
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Prepared Statement is: " + stmt.toString());
        }

        results = stmt.executeQuery();

        // stmt.close(); // Cannot close here or ResultSet is closed. Possible
        // resource leak.
        return results;
    }

    /**
     * Returns the results from the executed SQL Command, that contains LIKE
     * wildcards.
     *
     * @param sql the statement to be executed against the database.
     * @param likeParams the number of like parameters in the SQL statement
     * @param parameters the multiple parameters to be applied to the SQL
     *            statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     */
    public final ResultSet executeLikeQuery(final String sql, final int likeParams, final CoalesceParameter... parameters)
            throws SQLException
    {
        ResultSet results = null;
        openDataConnection();

        PreparedStatement stmt = _conn.prepareStatement(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii + 1 <= likeParams)
            {
                stmt.setObject(ii + 1, "%" + parameters[ii].getValue() + "%", parameters[ii].getType()); // Like
            }
            else
            {
                stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType()); // Normal
            }
        }

        results = stmt.executeQuery();
        // stmt.close(); // cannot close here or ResultSet is closed. Possible
        // resource leak.
        return results;
    }

    /**
     * Executes a SQL statement on a database.
     *
     * @param sql the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL
     *            statement
     * @return true = success
     * @throws SQLException
     */
    public final int executeUpdate(final String sql, final CoalesceParameter... parameters) throws SQLException
    {
        int status = -1;
        openDataConnection();

        PreparedStatement stmt = _conn.prepareStatement(sql);

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Executing: {}", sql);
        }

        if (parameters != null)
        {
            if (LOGGER.isTraceEnabled() && parameters.length > 0)
            {
                LOGGER.trace("Parameters:");
            }

            // Add Parameters
            for (int ii = 0; ii < parameters.length; ii++)
            {
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("\t{}:{}", parameters[ii].getValue(), parameters[ii].getType());
                }

                stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
            }
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Prepared Statement is: " + stmt.toString());
        }
        status = stmt.executeUpdate();
        stmt.close();
        return status;
    }

    /**
     * Executes a stored procedure(or function) on a database.
     *
     * @param procedureName the name of the stored procedure to be executed
     *            against the database.
     * @param parameters the multiple parameters to be applied to the SQL
     *            statement
     * @return true = success
     * @throws SQLException
     */
    public final boolean executeProcedure(final String procedureName, final CoalesceParameter... parameters)
            throws SQLException
    {
        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Executing Procedure: {}", procedureName);
        }

        // Compile SQL Command
        StringBuilder sb = new StringBuilder("{" + getProcedurePrefix() + procedureName + " (");

        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii != 0)
            {
                sb.append(",");
            }

            sb.append("?");
        }

        sb.append(")}");

        // TODO: Implement Retry

        openDataConnection();

        CallableStatement stmt = _conn.prepareCall(sb.toString());

        if (LOGGER.isTraceEnabled() && parameters.length > 0)
        {
            LOGGER.trace("Parameters:");
        }

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("\t{}", parameters[ii].getValue());
            }

            stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
        }

        LOGGER.trace(stmt.toString());

        stmt.executeUpdate();

        
        return true;

    }

    /**
     * Returns meta data for Coalesce Entity Template as XML.
     *
     * @param sql statement specifying the elements to be returned.
     * @return XML of meta data for template.
     * @throws SQLException
     * @throws ParserConfigurationException
     */
    public final List<ObjectMetaData> getTemplateMetaData(final String sql) throws SQLException,
            ParserConfigurationException
    {
        List<ObjectMetaData> templates = new ArrayList<ObjectMetaData>();

        // Execute Query
        ResultSet results = executeQuery(sql);

        while (results.next())
        {
            templates.add(new ObjectMetaData(results.getString("TemplateKey"),
                                             results.getString("Name"),
                                             results.getString("Source"),
                                             results.getString("Version"),
                                             JodaDateTimeHelper.getPostGresDateTim(results.getString("DateCreated")),
                                             JodaDateTimeHelper.getPostGresDateTim(results.getString("LastModified"))));
        }

        return templates;
    }

    /**
     * Rolls back the connection.
     *
     * @throws CoalescePersistorException
     */
    public final void rollback() throws CoalescePersistorException
    {

        if (_conn != null)
        {
            try
            {
                _conn.rollback();
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException("Failed to rollback the transaction: " + e.getMessage(), e);
            }
        }

    }

    /**
     * Commits.
     *
     * @throws CoalescePersistorException
     */
    public final void commit() throws CoalescePersistorException
    {

        try
        {
            if (_conn != null && !_conn.getAutoCommit())
            {
                _conn.commit();
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("Failed to commit: " + e.getMessage(), e);
        }

    }

    @Override
    public final void close() throws CoalescePersistorException
    {
        if (_conn != null)
        {
            // if (!this._conn.getAutoCommit())
            // {
            // this._conn.commit();
            // }

            try
            {
                _conn.close();
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException("Failed to close connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @return the connection
     * @throws SQLException
     */
    public final Connection getConnection() throws SQLException
    {
        openDataConnection();

        return _conn;
    }

    /*-----------------------------------------------------------------------------'
    Protected Functions
    -----------------------------------------------------------------------------*/

    protected final void setConnection(final Connection conn)
    {
        _conn = conn;
    }

    protected final void setSettings(final ServerConn settings)
    {
        _settings = settings;
    }

    protected final ServerConn getSettings()
    {
        return _settings;
    }

    /*-----------------------------------------------------------------------------'
    Private Functions
    -----------------------------------------------------------------------------*/

    private void openDataConnection() throws SQLException
    {
        if (_conn == null)
        {
            openConnection(true);
        }
    }

}
