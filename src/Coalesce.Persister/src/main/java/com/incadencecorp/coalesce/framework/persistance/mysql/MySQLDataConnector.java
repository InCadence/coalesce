package com.incadencecorp.coalesce.framework.persistance.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class MySQLDataConnector extends CoalesceDataConnectorBase {

    public MySQLDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            _settings = settings;

            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    @Override
    public void openConnection() throws SQLException
    {

        String url = "jdbc:mysql://" + _settings.getServerNameWithPort() + "/" + _settings.getDatabase();

        this._settings.setPostGres(false);
        this._conn = DriverManager.getConnection(url, this._settings.getUser(), this._settings.getPassword());
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call " + _settings.getDatabase() + ".";
    }

}
