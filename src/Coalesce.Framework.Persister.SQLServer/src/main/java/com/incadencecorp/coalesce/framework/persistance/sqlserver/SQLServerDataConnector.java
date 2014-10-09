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
            _settings = settings;

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

        this._settings.setPostGres(false);

        SQLServerDataSource sqlDataSource = new SQLServerDataSource();
        //  On Linux change set setIntegratedSecurity(false)
        sqlDataSource.setIntegratedSecurity(true);
        sqlDataSource.setServerName(this._settings.getServerName());
        sqlDataSource.setPortNumber(this._settings.getPortNumber());
        sqlDataSource.setDatabaseName(this._settings.getDatabase());

        this._conn = sqlDataSource.getConnection();
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call " + _settings.getDatabase() + ".dbo.";
    }
}
