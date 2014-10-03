package Coalesce.Framework.Persistance;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import coalesce.persister.sqlserver.SQLServerDataConnector;
import coalesce.persister.sqlserver.SQLServerPersistor;

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

public class SQLServerPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setUpBeforeClass() throws CoalesceException
    {

        SQLServerPersistorTest tester = new SQLServerPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        SQLServerPersistorTest tester = new SQLServerPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();
        serCon.setServerName("127.0.0.1");
        serCon.setPortNumber(1433);
        serCon.setDatabase("coalescedatabase");

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        SQLServerPersistor mySQLServerPersistor = new SQLServerPersistor();
        mySQLServerPersistor.Initialize(conn);

        return mySQLServerPersistor;

    }

    @Override
    protected ServerConn getInvalidConnection()
    {
        ServerConn serConFail = new ServerConn();
        serConFail.setServerName("192.168.1.1");
        serConFail.setPortNumber(1433);
        serConFail.setDatabase("coalescedatabase");

        return serConFail;

    }
    
    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new SQLServerDataConnector(conn);
    }
}
