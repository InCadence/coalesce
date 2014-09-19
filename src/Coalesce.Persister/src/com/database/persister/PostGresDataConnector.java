package com.database.persister;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class PostGresDataConnector implements AutoCloseable {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    Connection _conn = null;
    private ServerConn _serCon = null;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    public PostGresDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        this._serCon = settings;
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("PostGresDataConnector", e);
        }
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public void OpenConnection() throws SQLException
    {
        this._serCon.setPostGres(true);
        this._conn = DriverManager.getConnection(this._serCon.getURL(), this._serCon.props);
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
            stmt.setObject(ii + 1, parameters[ii].trim(),Types.OTHER);
        }

        return stmt.executeQuery();

    }
    public ResultSet ExecuteLikeQuery(String SQL,int likeParams, String... parameters) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        // Add Parameters
        for (int ii = 0; ii < parameters.length; ii++)
        {
            if(ii+1<=likeParams)
                stmt.setString(ii + 1, "%" +parameters[ii].trim() + "%");  //  Like Clause Search String
            else
                stmt.setString(ii + 1, parameters[ii].trim());  //  Normal parameter
        }

        return stmt.executeQuery();

    }

    public ResultSet ExecuteQuery(String SQL) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

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
            stmt.setObject(ii + 1, parameters[ii].trim(),Types.OTHER);
        }

        stmt.executeUpdate();

        return true;

    }

    public boolean ExecuteCmd(String SQL) throws SQLException
    {

        // Open Connection if not already created
        if (this._conn == null) this.OpenConnection();

        CallableStatement stmt = this._conn.prepareCall(SQL);

        stmt.executeUpdate();

        return true;

    }

    public boolean ExecuteProcedure(String procedureName, String... parameters) throws SQLException
    {

        // Compile SQL Command
        StringBuilder sb = new StringBuilder("{call public." + procedureName + " (");

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
            stmt.setObject(ii + 1, parameters[ii].trim(),Types.OTHER);
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
