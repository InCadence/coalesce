package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloPersistorIT extends AbstractAccumuloPersistorTest {

    private static ServerConn conn;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistorIT.class);

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        InputStream in = AccumuloDataConnectorIT.class.getClassLoader().getResourceAsStream("accumuloConnectionInfo.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ServerConn.Builder().db(dbName).serverName(zookeepers).user(user).password(password).build();

        AccumuloSettings.setPersistFieldDefAttr(false);
        AccumuloSettings.setPersistSectionAttr(true);
        AccumuloSettings.setPersistRecordsetAttr(false);
        AccumuloSettings.setPersistRecordAttr(false);

        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            LOGGER.warn("JRE {} Detected. These unit tests require JRE 1.8", version);
            LOGGER.warn("Skipping unit tests");
            // skip these tests
            Assume.assumeTrue(false);
        }
    }

    @Override
    protected ServerConn getConnection()
    {
        return conn;
    }

    @Override
    protected AccumuloPersistor  createPersister()
    {
        try
        {
            return new AccumuloPersistor(conn);
        }
        catch (CoalescePersistorException e)
        {
            return null;
        }
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new AccumuloDataConnector(conn);
    }

    @Override
    protected AccumuloDataConnector getAccumuloDataConnector() throws CoalescePersistorException
    {
        return new AccumuloDataConnector(getConnection());
    }

}
