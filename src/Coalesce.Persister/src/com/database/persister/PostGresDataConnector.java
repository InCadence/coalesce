package com.database.persister;

import java.sql.DriverManager;
import java.sql.SQLException;

import Coalesce.Common.Exceptions.CoalescePersistorException;


public class PostGresDataConnector extends CoalesceDataConnectorBase {

    public PostGresDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            _settings = settings;

            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    @Override
    public void openConnection() throws SQLException
    {
        this._settings.setPostGres(true);
        this._conn = DriverManager.getConnection(this._settings.getURL(), this._settings.props);
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call public.";
    }

}