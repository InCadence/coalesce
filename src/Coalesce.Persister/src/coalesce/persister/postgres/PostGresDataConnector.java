package coalesce.persister.postgres;

import java.sql.DriverManager;
import java.sql.SQLException;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Framework.Persistance.CoalesceDataConnectorBase;
import Coalesce.Framework.Persistance.ServerConn;

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
        String url = "jdbc:postgresql://" + _settings.getServerNameWithPort() + "/" + _settings.getDatabase();

        this._settings.setPostGres(true);
        this._conn = DriverManager.getConnection(url, this._settings.getProperties());
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call public.";
    }

}