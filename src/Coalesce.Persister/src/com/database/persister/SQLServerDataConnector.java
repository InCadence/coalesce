package com.database.persister;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import Coalesce.Common.Exceptions.CoalescePersistorException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;    // Not in Maven Central Repository, MUST USE JRE 1.7


public class SQLServerDataConnector implements AutoCloseable {
    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ServerConn _serCon = null;
    Connection _conn = null;
    SQLServerDataSource sqlDataSource=null;
    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    public SQLServerDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        this._serCon = settings;

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("SQLServerDataConnector", e);
        }
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public void OpenConnection() throws SQLException
    {
        sqlDataSource=new SQLServerDataSource();
        sqlDataSource.setUser(this._serCon.getUser());
        sqlDataSource.setPassword(this._serCon.getPassword());
        sqlDataSource.setServerName(this._serCon.getServerName());
        sqlDataSource.setPortNumber(this._serCon.getPortNumber()); 
        sqlDataSource.setDatabaseName(this._serCon.getDatabase());
        this._conn= sqlDataSource.getConnection();
    }

    public void CloseConnection() throws Exception
    {
        this.close();
    }

    public ResultSet ExecuteQuery(String SQL, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setString(ii + 1, parameters[ii].trim());
        }

        return stmt.executeQuery();

    }

    public boolean ExecuteCmd(String SQL, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setString(ii + 1, parameters[ii].trim());
        }

        stmt.executeUpdate();

        return true;

    }

    public boolean ExecuteProcedure(String procedureName, String... parameters) throws SQLException
    {

        // Compile SQL Command
        StringBuilder sb = new StringBuilder("{call coalescedatabase.dbo." + procedureName + " (");

        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii != 0) sb.append(",");
            sb.append("?");
        }

        sb.append(")}");

        // TODO: Implement Retry

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(sb.toString());

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            stmt.setString(ii + 1, parameters[ii].trim());
        }

        stmt.executeUpdate();

        return true;
    }

    /*--------------------------------------------------------------------------
    Finalize
    --------------------------------------------------------------------------*/

    @Override
    public void close() throws SQLException
    {
        if (this._conn != null)
        {
            if (!this._conn.getAutoCommit()) this._conn.commit();
            this._conn.close();
        }
    }
}
