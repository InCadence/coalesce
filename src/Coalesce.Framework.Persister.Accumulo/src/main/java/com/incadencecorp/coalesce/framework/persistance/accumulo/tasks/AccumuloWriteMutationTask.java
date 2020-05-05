/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.accumulo.tasks;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloWriteMutationTask
        extends AbstractTask<List<Mutation>, CoalesceStringResponseType, AccumuloDataConnector> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloWriteMutationTask.class);

    private String tablename;
    private BatchWriterConfig config;

    /**
     * @param name Table Name
     */
    public void setTablename(String name)
    {
        this.tablename = name;
    }

    /**
     * @param config Configuration Settings
     */
    public void setConfig(BatchWriterConfig config)
    {
        this.config = config;
    }

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<AccumuloDataConnector, List<Mutation>> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        try (BatchWriter writer = parameters.getTarget().getDBConnector().createBatchWriter(tablename, config))
        {
            for (Mutation mutation : parameters.getParams())
            {
                writer.addMutation(mutation);
                writer.flush();
            }
        }
        catch (MutationsRejectedException | TableNotFoundException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                               tablename,
                                                               tablename,
                                                               e.getMessage()), e);
        }

        result.setStatus(EResultStatus.SUCCESS);
        return result;
    }

    @Override
    protected Map<String, String> getParameters(List<Mutation> params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }

}
