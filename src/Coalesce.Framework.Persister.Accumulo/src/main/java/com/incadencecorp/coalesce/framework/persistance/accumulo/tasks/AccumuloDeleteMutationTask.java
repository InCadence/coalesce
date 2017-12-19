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

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloSettings;
import com.incadencecorp.coalesce.framework.persistance.accumulo.CloseableBatchDeleter;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloDeleteMutationTask
        extends AbstractTask<List<String>, CoalesceStringResponseType, AccumuloDataConnector> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloDeleteMutationTask.class);

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
    protected CoalesceStringResponseType doWork(TaskParameters<AccumuloDataConnector, List<String>> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        try (CloseableBatchDeleter bd = new CloseableBatchDeleter(parameters.getTarget().getDBConnector(),
                                                                  tablename,
                                                                  Authorizations.EMPTY,
                                                                  AccumuloSettings.getQueryThreads(),
                                                                  config))
        {
            List<Range> ranges = new ArrayList<>();

            for (String key : parameters.getParams())
            {
                ranges.add(Range.exact(new Text(key)));
            }

            bd.setRanges(ranges);
            bd.delete();
        }
        catch (TableNotFoundException | AccumuloException e)
        {
            throw new CoalescePersistorException(String.format("(FAILED) Deleting (%s) from (%s)",
                                                               parameters.getParams().toString(),
                                                               tablename), e);
        }

        result.setStatus(EResultStatus.SUCCESS);
        return result;
    }

    @Override
    protected Map<String, String> getParameters(List<String> params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }
}
