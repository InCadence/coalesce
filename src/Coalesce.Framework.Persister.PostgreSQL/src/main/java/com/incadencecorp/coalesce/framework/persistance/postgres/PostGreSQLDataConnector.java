package com.incadencecorp.coalesce.framework.persistance.postgres;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class PostGreSQLDataConnector extends CoalesceDataConnectorBase {

    private String _prefix;

    public PostGreSQLDataConnector(ServerConn settings, String prefix) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);
            _prefix = prefix;

            Driver driver = new org.postgresql.Driver();
            DriverManager.registerDriver(driver);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    @Override
    public Connection getDBConnection() throws SQLException
    {
        String url = "jdbc:postgresql://" + getSettings().getServerNameWithPort() + "/" + getSettings().getDatabase();

        return DriverManager.getConnection(url, getSettings().getProperties());
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call " + _prefix;
    }

}
