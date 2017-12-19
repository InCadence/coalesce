/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.accumulo.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractStringResponseJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloDeleteMutationTask;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloDeleteSchemaTask;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Clemenzi
 */
public class AccumuloDeleteSchemaJob extends AbstractStringResponseJob<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloDeleteSchemaJob.class);

    private AccumuloDataConnector conn;
    private BatchWriterConfig config;

    public AccumuloDeleteSchemaJob(AccumuloDataConnector conn, List<String> keysToDelete)
    {
        super(keysToDelete);

        this.conn = conn;
    }

    public void setConfig(BatchWriterConfig value)
    {
        this.config = value;
    }

    @Override
    protected Collection<AbstractTask<?, CoalesceStringResponseType, ?>> getTasks(List<String> keysToDelete)
            throws CoalesceException
    {
        List<AbstractTask<?, CoalesceStringResponseType, ?>> tasks = new ArrayList<>();

        // Create Task to Delete Templates
        AccumuloDeleteMutationTask task = new AccumuloDeleteMutationTask();
        task.setName(String.format("%s) Updating (%s [-%s]",
                                   task.getName(),
                                   AccumuloDataConnector.COALESCE_TEMPLATE_TABLE,
                                   keysToDelete.size()));
        task.setConfig(config);
        task.setTablename(AccumuloDataConnector.COALESCE_TEMPLATE_TABLE);
        task.setParams(keysToDelete);
        task.setTarget(conn);

        tasks.add(task);

        // Check to see if the recordset is used by another template not in the list to delete
        for (String key : keysToDelete)
        {
            // Get Template's Recordsets
            Set<String> recordsets = CoalesceTemplateUtil.getRecordsets(key);

            for (String recordset : recordsets)
            {
                boolean stillReferenced = false;

                // Get Recordset's Templates
                Set<String> keys = CoalesceTemplateUtil.getTemplateKey(recordset);

                // Record set belong to multiple templates?
                if (keys.size() != 1)
                {
                    // Yes; Verify that all templates are marked for deleted.
                    for (String templateKey : keys)
                    {
                        if (!keysToDelete.contains(templateKey))
                        {
                            LOGGER.info("(SKIPPING) Deleting schema ({}) because its still referenced by ({})", templateKey);

                            stillReferenced = true;
                            break;
                        }
                    }
                }

                if (stillReferenced)
                {
                    // Delete Entries
                    // TODO Not Implemented, remove the entry for this template
                }
                else
                {
                    // Delete Schema
                    AccumuloDeleteSchemaTask schemaTask = new AccumuloDeleteSchemaTask();
                    schemaTask.setName(String.format("%s) Deleting Schema (%s", task.getName(), recordset));
                    schemaTask.setParams(recordset);
                    schemaTask.setTarget(conn.getGeoDataStore());

                    tasks.add(schemaTask);
                }

                // TODO Remove entries from mutations
            }
        }

        try
        {
            conn.getGeoDataStore().removeSchema("test1");
        }
        catch (IOException e)
        {
            throw new CoalesceException(e.getMessage(), e);
        }

        return tasks;
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> createResponse()
    {
        return new CoalesceResponseType<>();
    }

    @Override
    protected CoalesceStringResponseType createResults()
    {
        return new CoalesceStringResponseType();
    }
}
