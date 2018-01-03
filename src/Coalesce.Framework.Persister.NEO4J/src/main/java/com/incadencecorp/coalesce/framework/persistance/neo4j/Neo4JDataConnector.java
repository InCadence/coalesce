package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class Neo4JDataConnector extends CoalesceDataConnectorBase {

    public Neo4JDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);

            Driver driver = new org.neo4j.jdbc.Driver();
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
        String http = Neo4jSettings.isSSLEnabled() ? "https" : "http";

        String url = "jdbc:neo4j:" + http + "://" + getSettings().getServerName() + ":" + getSettings().getPortNumber()
                + "/";

        return DriverManager.getConnection(url, getSettings().getProperties());
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }
}
