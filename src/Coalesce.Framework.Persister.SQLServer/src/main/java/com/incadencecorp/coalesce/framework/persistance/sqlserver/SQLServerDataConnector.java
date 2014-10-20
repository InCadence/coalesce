package com.incadencecorp.coalesce.framework.persistance.sqlserver;

import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class SQLServerDataConnector extends CoalesceDataConnectorBase {

    public SQLServerDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    @Override
    public void openConnection() throws SQLException
    {

        getSettings().setPostGres(false);

        SQLServerDataSource sqlDataSource = new SQLServerDataSource();
        //  On Linux change set setIntegratedSecurity(false)
        sqlDataSource.setIntegratedSecurity(true);
        sqlDataSource.setServerName(getSettings().getServerName());
        sqlDataSource.setPortNumber(getSettings().getPortNumber());
        sqlDataSource.setDatabaseName(getSettings().getDatabase());

        setConnection(sqlDataSource.getConnection());
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call " + getSettings().getDatabase() + ".dbo.";
    }
}
