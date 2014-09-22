package com.database.persister;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import Coalesce.Common.Exceptions.CoalescePersistorException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

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

public class CoalesceDataConnector implements AutoCloseable {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ServerConn _serCon = null;
    private Connection _conn = null;
    private SQLServerDataSource sqlDataSource = null;
    private String _procedurePrefix = null;
    private String serverConnection = "";

    public CoalesceDataConnector(ServerConn settings, ConnectionType server) throws CoalescePersistorException
    {
        this._serCon = settings;

        try
        {

            switch (server) {
            case MySQL:
                serverConnection = "com.mysql.jdbc.Driver";
                this._procedurePrefix = "{call coalescedatabase.";
                break;
            case PostGresSQL:
                serverConnection = "org.postgresql.Driver";
                this._procedurePrefix = "{call public.";
                break;
            case SQLServer:
                serverConnection = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                this._procedurePrefix = "{ call coalescedatabase.dbo.";
                break;
            case NEO4J:
                serverConnection = "org.neo4j.jdbc.Driver";
                this._procedurePrefix = "";
                break;
            case Hybrid:
                serverConnection = "";
                break;
            }
            Class.forName(serverConnection);
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    public void OpenMSConnection() throws SQLException
    {
        this._serCon.setPostGres(false);
        this._conn = DriverManager.getConnection(this._serCon.getURL(), this._serCon.getUser(), this._serCon.getPassword());
    }

    public void OpenPSConnection() throws SQLException
    {
        this._serCon.setPostGres(true);
        this._conn = DriverManager.getConnection(this._serCon.getURL(), this._serCon.props);
    }
    public void OpenNEOJConnection() throws SQLException
    {
        this._serCon.setPostGres(false);
        this._conn=DriverManager.getConnection(this._serCon.getURL());
    }

    public void OpenSSConnection() throws SQLException
    {
        this._serCon.setPostGres(false);
        sqlDataSource = new SQLServerDataSource();
        sqlDataSource.setUser(this._serCon.getUser());
        sqlDataSource.setPassword(this._serCon.getPassword());
        sqlDataSource.setServerName(this._serCon.getServerName());
        sqlDataSource.setPortNumber(this._serCon.getPortNumber());
        sqlDataSource.setDatabaseName(this._serCon.getDatabase());
        this._conn = sqlDataSource.getConnection();
    }

    public void CloseConnection() throws Exception
    {
        this.close();
    }

    /**
     * Manages calling the correct db connection methods based on the db driver.for.name setup.
     */
    private void setConnection() throws SQLException
    {
        if (this._conn == null) if (serverConnection.toLowerCase().contains("mysql"))
        {
            this.OpenMSConnection();
        }
        else if (serverConnection.toLowerCase().contains("postgresql"))
        {
            this.OpenPSConnection();
        }
        else if (serverConnection.toLowerCase().contains("sqlserver"))
        {
            this.OpenSSConnection();
        }
        else if (serverConnection.toLowerCase().contains("neo4j"))
        {
            this.OpenNEOJConnection();
        }
        else if (serverConnection.toLowerCase().contains("hybrid"))
        {
            this.OpenMSConnection();
        }
    }



    public ResultSet ExecuteQuery(String SQL, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.setConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        if (serverConnection.toLowerCase().contains("postgresql"))
        {
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setObject(ii + 1, parameters[ii].trim(), Types.OTHER);
            }
        }
        else if (serverConnection.toLowerCase().contains("mysql") || serverConnection.toLowerCase().contains("sqlserver"))
        {
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setString(ii + 1, parameters[ii].trim());
            }
        }
        return stmt.executeQuery();

    }

    public ResultSet ExecuteLikeQuery(String SQL, int likeParams, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.setConnection();

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

    public boolean ExecuteCmd(String SQL, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.setConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        if (serverConnection.toLowerCase().contains("postgresql"))
        {
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setObject(ii + 1, parameters[ii].trim(), Types.OTHER);
            }
        }
        else if (serverConnection.toLowerCase().contains("mysql") || serverConnection.toLowerCase().contains("sqlserver"))
        {
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setString(ii + 1, parameters[ii].trim());
            }
        }

        stmt.executeUpdate();

        return true;

    }

    public boolean ExecuteCmd(String SQL) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.setConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        stmt.executeUpdate();

        return true;

    }

    public boolean ExecuteProcedure(String procedureName, String... parameters) throws SQLException
    {

        // Compile SQL Command
        StringBuilder sb = new StringBuilder(this._procedurePrefix + procedureName + " (");

        for (int ii = 0; ii < parameters.length; ii++)
        {
            if (ii != 0) sb.append(",");
            sb.append("?");
        }

        sb.append(")}");

        // TODO: Implement Retry

        // Open Connection if not already created
        if (this._conn == null) this.setConnection();

        CallableStatement stmt = this._conn.prepareCall(sb.toString());
        if (serverConnection.toLowerCase().contains("postgresql"))
        {
            // Add Parameters
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setObject(ii + 1, parameters[ii].trim(), Types.OTHER);
            }
        }
        else if (serverConnection.toLowerCase().contains("mysql") || serverConnection.toLowerCase().contains("sqlserver"))
        {
            // Add Parameters
            for (int ii = 0; ii < parameters.length; ii++)
            {
                stmt.setString(ii + 1, parameters[ii].trim());
            }
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

}
