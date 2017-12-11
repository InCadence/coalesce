package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.util.Map;

public class AccumuloDataConnectorIT extends AccumuloDataConnectorTest {

    @Override
    protected Map<String, String> getParameters()
    {
        Map<String, String> parameters = super.getParameters();
        parameters.put(AccumuloDataConnector.INSTANCE_ID, AccumuloSettings.getDatabaseName());
        parameters.put(AccumuloDataConnector.ZOOKEEPERS, AccumuloSettings.getZookeepers());
        parameters.put(AccumuloDataConnector.USER, AccumuloSettings.getUserName());
        parameters.put(AccumuloDataConnector.PASSWORD, AccumuloSettings.getUserPassword());
        parameters.put(AccumuloDataConnector.USE_MOCK, "false");

        return parameters;
    }
}
