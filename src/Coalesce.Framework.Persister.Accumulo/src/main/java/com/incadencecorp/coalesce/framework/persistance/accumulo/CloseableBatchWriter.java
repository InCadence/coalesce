package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Mutation;

public class CloseableBatchWriter implements BatchWriter, AutoCloseable {

    private final BatchWriter writer;

    public CloseableBatchWriter(Connector dbConnector, String table, BatchWriterConfig config) throws TableNotFoundException
    {
        this.writer = dbConnector.createBatchWriter(table, config);
    }

    @Override
    public void addMutation(Mutation arg0) throws MutationsRejectedException
    {
        writer.addMutation(arg0);
    }

    @Override
    public void addMutations(Iterable<Mutation> arg0) throws MutationsRejectedException
    {
        writer.addMutations(arg0);
    }

    @Override
    public void close() throws MutationsRejectedException
    {
        writer.close();
    }

    @Override
    public void flush() throws MutationsRejectedException
    {
        writer.flush();
    }

}
