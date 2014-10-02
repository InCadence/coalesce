package Coalesce.Framework.Persistance;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import coalesce.persister.sqlserver.SQLServerDataConnector;
import coalesce.persister.sqlserver.SQLServerPersistor;

public class CoalesceSQLServerPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setUpBeforeClass() throws CoalesceException
    {

        CoalesceSQLServerPersistorTest tester = new CoalesceSQLServerPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        CoalesceSQLServerPersistorTest tester = new CoalesceSQLServerPersistorTest();

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
