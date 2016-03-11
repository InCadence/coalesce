package com.incadencecorp.coalesce.framework.persistance;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

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

    /*-----------------------------------------------------------------------------'
    Private Member Variables
    -----------------------------------------------------------------------------*/

    private Connection _conn = null;
    private ServerConn _settings;

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
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     */
    public final ResultSet executeQuery(final String sql) throws SQLException
    {
        openDataConnection();

        PreparedStatement stmt = this._conn.prepareStatement(sql);

        return stmt.executeQuery();
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

        openDataConnection();

        PreparedStatement stmt = this._conn.prepareStatement(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
        }

        return stmt.executeQuery();

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

        openDataConnection();

        PreparedStatement stmt = this._conn.prepareStatement(sql);

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

        return stmt.executeQuery();

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
    public final boolean executeCmd(final String sql, final CoalesceParameter... parameters) throws SQLException
    {
        openDataConnection();

        PreparedStatement stmt = this._conn.prepareStatement(sql);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
        }

        stmt.executeUpdate();

        return true;
    }

    /**
     * Executes a SQL statement on a database.
     * 
     * @param sql the statement to be executed against the database.
     * @return true = success
     * @throws SQLException
     */
    public final boolean executeCmd(final String sql) throws SQLException
    {
        openDataConnection();

        PreparedStatement stmt = this._conn.prepareStatement(sql);

        stmt.executeUpdate();

        return true;
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

        CallableStatement stmt = this._conn.prepareCall(sb.toString());

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].getValue(), parameters[ii].getType());
        }

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
    public final String getTemplateMetaData(final String sql) throws SQLException, ParserConfigurationException
    {
        // Execute Query
        ResultSet results = executeQuery(sql);

        // Create Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Create Root Node
        Element rootElement = doc.createElement("coalescetemplates");
        doc.appendChild(rootElement);

        while (results.next())
        {
            // Create New Template Element
            Element templateElement = doc.createElement("coalescetemplate");

            // Set Attributes
            templateElement.setAttribute("templatekey", results.getString("TemplateKey"));
            templateElement.setAttribute("name", results.getString("Name"));
            templateElement.setAttribute("source", results.getString("Source"));
            templateElement.setAttribute("version", results.getString("Version"));
            templateElement.setAttribute("lastmodified", results.getString("LastModified"));
            templateElement.setAttribute("datecreated", results.getString("DateCreated"));

            // Append Element
            rootElement.appendChild(templateElement);
        }

        // Serialize to String
        return XmlHelper.formatXml(doc);
    }

    /**
     * Rolls back the connection.
     * 
     * @throws CoalescePersistorException
     */
    public final void rollback() throws CoalescePersistorException
    {

        if (this._conn != null)
        {
            try
            {
                this._conn.rollback();
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
            if (this._conn != null && !_conn.getAutoCommit())
            {
                this._conn.commit();
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
        if (this._conn != null)
        {
            // if (!this._conn.getAutoCommit())
            // {
            // this._conn.commit();
            // }

            try
            {
                this._conn.close();
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException("Failed to close connection: " + e.getMessage(), e);
            }
        }
    }

    public final Connection getConnection() throws SQLException
    {
        openDataConnection();

        return this._conn;
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
        if (this._conn == null)
        {
            this.openConnection(true);
        }
    }

}
