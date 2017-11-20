package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This implementation uses a {@link MockInstance}.
 */
public class MockAccumuloDataConnector extends AccumuloDataConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloDataConnector.class);

    /**
     * Default Constructor.
     *
     * @param settings connection settings
     * @throws CoalescePersistorException on error
     */
    public MockAccumuloDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        super(settings, getParameters(settings));

        try
        {
            createTables(coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

    }

    private static Map<String, String> getParameters(ServerConn settings)
    {
        // For some reason the Mock Instance does not like the root user.
        if (settings.getUser().equalsIgnoreCase("root"))
        {
            settings.setUser("unittest");
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put(INSTANCE_ID, settings.getDatabase());
        parameters.put(ZOOKEEPERS, settings.getServerName());
        parameters.put(USER, settings.getUser());
        parameters.put(PASSWORD, settings.getPassword());
        parameters.put(TABLE_NAME, coalesceSearchTable);
        parameters.put(QUERY_THREADS, "1");
        parameters.put(RECORD_THREADS, "1");
        parameters.put(WRITE_THREADS, "1");
        parameters.put(GENERATE_STATS, "false");
        parameters.put(COLLECT_USAGE_STATS, "false");
        parameters.put(CACHING, "false");
        parameters.put(LOOSE_B_BOX, "false");
        parameters.put(USE_MOCK, "true");

        return parameters;
    }

}
