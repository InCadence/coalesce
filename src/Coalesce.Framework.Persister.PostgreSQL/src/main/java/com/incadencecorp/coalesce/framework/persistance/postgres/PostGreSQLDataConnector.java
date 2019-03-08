package com.incadencecorp.coalesce.framework.persistance.postgres;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalescePooledDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostGreSQLDataConnector extends CoalescePooledDataConnectorBase {

    private String _prefix;

    public PostGreSQLDataConnector(ServerConn settings, String prefix) throws CoalescePersistorException
    {
        super(settings, 100, Driver.class);
        try
        {
            _prefix = prefix;

            Driver driver = new org.postgresql.Driver();
            DriverManager.registerDriver(driver);

            String url = "jdbc:postgresql://" + getSettings().getServerNameWithPort() + "/" + getSettings().getDatabase();

            Properties props = new Properties();
            props.put("preparedStatementCacheQueries", 0);

            if (PostGreSQLSettings.isSSLEnabled())
            {
                props.put("ssl", "true");

                if (!PostGreSQLSettings.isSSLValidate())
                {
                    props.put("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
                }
            }

            initialize(url, props);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    /*
        @Override
        public Connection getDBConnection() throws SQLException
        {
            String url = "jdbc:postgresql://" + getSettings().getServerNameWithPort() + "/" + getSettings().getDatabase();

            if (PostGreSQLSettings.isSSLEnabled())
            {
                url += "?ssl=true";

                if (!PostGreSQLSettings.isSSLValidate())
                {
                    url += "&sslfactory=org.postgresql.ssl.NonValidatingFactory";
                }
            }

            return DriverManager.getConnection(url, getSettings().getProperties());
        }
    */
    @Override
    protected String getProcedurePrefix()
    {
        return "call " + _prefix;
    }

}
