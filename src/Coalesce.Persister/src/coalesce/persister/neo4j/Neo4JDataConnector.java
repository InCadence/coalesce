package coalesce.persister.neo4j;

import java.sql.DriverManager;
import java.sql.SQLException;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Framework.Persistance.CoalesceDataConnectorBase;
import Coalesce.Framework.Persistance.ServerConn;


public class Neo4JDataConnector extends CoalesceDataConnectorBase {

    public Neo4JDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            _settings = settings;

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
        String url = "jdbc:neo4j://" + _settings.getServerName() +":" + _settings.getPortNumber();

        this._settings.setPostGres(false);
        this._conn=DriverManager.getConnection(url);
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }
}