package com.incadencecorp.coalesce.framework.persistance.postgres;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class PostGreSQLDataConnector extends CoalesceDataConnectorBase {

    public PostGreSQLDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);

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
        String url = "jdbc:postgresql://" + getSettings().getServerNameWithPort() + "/" + getSettings().getDatabase();

        setConnection(DriverManager.getConnection(url, this.getSettings().getProperties()));
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call public.";
    }

}