package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;


public class Neo4JDataConnector extends CoalesceDataConnectorBase {

    public Neo4JDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);

            Class.forName("org.neo4j.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    @Override
    public void openConnection() throws SQLException
    {
        String url = "jdbc:neo4j://" + getSettings().getServerName() +":" + getSettings().getPortNumber();

        getSettings().setPostGres(false);
        setConnection(DriverManager.getConnection(url));
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }
}