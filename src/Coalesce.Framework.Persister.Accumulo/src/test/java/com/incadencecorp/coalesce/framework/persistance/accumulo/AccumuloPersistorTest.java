package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBaseTest;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloPersistorTest extends AbstractAccumuloPersistorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistorTest.class);

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {

        CoalesceUnitTestSettings.initialize();
        
        AccumuloSettings.setPersistFieldDefAttr(true);
        AccumuloSettings.setPersistSectionAttr(true);
        AccumuloSettings.setPersistRecordsetAttr(true);
        AccumuloSettings.setPersistRecordAttr(true);

        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            LOGGER.warn("JRE {} Detected. These unit tests require JRE 1.8", version);
            LOGGER.warn("Skipping unit tests");
            // skip these tests
            Assume.assumeTrue(false);
        }

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    @Override
    protected AccumuloPersistor createPersister()
    {
        
       
        ServerConn serverConn = getConnection();
        
        try
        {
            //override to mockout AccumuloDataConnector
            return new AccumuloPersistor(serverConn) {

                @Override
                protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
                {
                    return new MockAccumuloDataConnector(getConnectionSettings());
                }

            };
        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error(e.getMessage(),e);
            return null;
        }
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new MockAccumuloDataConnector(conn);
    }

    @Override
    protected ServerConn getConnection()
    {
        String name = "shouldnot";
        String zookeepers = "matter";
        
        return new ServerConn.Builder().db(name).serverName(zookeepers).user("unittest").password("password").build();
    }

    @Override
    protected AccumuloDataConnector getAccumuloDataConnector() throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return new MockAccumuloDataConnector(getConnection());
    }
}
