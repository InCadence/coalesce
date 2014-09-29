package com.database.persister;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import Coalesce.Common.Exceptions.CoalescePersistorException;

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

public abstract class CoalesceDataConnectorBase implements AutoCloseable {

    /*-----------------------------------------------------------------------------'
    Private Member Variables
    -----------------------------------------------------------------------------*/

    protected Connection _conn = null;
    protected ServerConn _settings;

    /*-----------------------------------------------------------------------------'
    Abstract Functions
    -----------------------------------------------------------------------------*/

    public abstract void openConnection() throws SQLException;

    protected abstract String getProcedurePrefix();

    /*-----------------------------------------------------------------------------'
    Public Functions
    -----------------------------------------------------------------------------*/

    /**
     * Returns the results from the executed SQL Command.
     * 
     * @param SQL the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     * @throws CoalescePersistorException
     */
    public ResultSet ExecuteQuery(String SQL, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.openConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
                stmt.setObject(ii + 1, parameters[ii].trim(), Types.CHAR);
        }

        return stmt.executeQuery();

    }

    /**
     * Returns the results from the executed SQL Command, that contains LIKE wildcards.
     * 
     * @param SQL the statement to be executed against the database.
     * @param likeParams the number of like parameters in the SQL statement
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return ResultSet - A table of data representing a database result set.
     * @throws SQLException
     * @throws CoalescePersistorException
     */
    public ResultSet ExecuteLikeQuery(String SQL, int likeParams, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.openConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii + 1 <= likeParams)
                stmt.setString(ii + 1, "%" + parameters[ii].trim() + "%"); // Like
                                                                           // Clause
                                                                           // Search
                                                                           // String
            else
                stmt.setString(ii + 1, parameters[ii].trim()); // Normal
                                                               // parameter
        }

        return stmt.executeQuery();

    }

    /**
     * Executes a SQL statement on a database.
     * 
     * @param SQL the statement to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return true = success
     * @throws SQLException
     */
    public boolean ExecuteCmd(String SQL, String... parameters) throws SQLException
    {
        // Open Connection if not already created
        if (this._conn == null) this.openConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].trim(), Types.CHAR);
        }

        stmt.executeUpdate();

        return true;
    }

    /**
     * Executes a SQL statement on a database.
     * 
     * @param SQL the statement to be executed against the database.
     * @return true = success
     * @throws SQLException
     */
    public boolean ExecuteCmd(String SQL) throws SQLException
    {
        // Open Connection if not already created
        if (this._conn == null) this.openConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        stmt.executeUpdate();

        return true;
    }

    /**
     * Executes a stored procedure(or function) on a database.
     * 
     * @param procedureName the name of the stored procedure to be executed against the database.
     * @param parameters the multiple parameters to be applied to the SQL statement
     * @return true = success
     * @throws SQLException
     */
    public boolean ExecuteProcedure(String procedureName, String... parameters) throws SQLException
    {

        // Compile SQL Command
        StringBuilder sb = new StringBuilder("{" + getProcedurePrefix() + procedureName + " (");

        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii != 0) sb.append(",");
            sb.append("?");
        }

        sb.append(")}");

        // TODO: Implement Retry

        // Open Connection if not already created
        if (this._conn == null) this.openConnection();

        CallableStatement stmt = this._conn.prepareCall(sb.toString());

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setObject(ii + 1, parameters[ii].trim(), Types.CHAR);
        }

        stmt.executeUpdate();

        return true;
    }

    @Override
    public void close() throws Exception
    {
        if (this._conn != null)
        {
            if (!this._conn.getAutoCommit()) this._conn.commit();
            this._conn.close();
        }
    }

    /*-----------------------------------------------------------------------------'
    Protected Functions
    -----------------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------------'
    Private Functions
    -----------------------------------------------------------------------------*/

}
