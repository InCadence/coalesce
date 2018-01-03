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

package com.incadencecorp.coalesce.framework.persistance.accumulo.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractStringResponseJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloCreateSchemaTask;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This job creates {@link AccumuloCreateSchemaTask} tasks.
 *
 * @author Derek Clemenzi
 */
public class AccumuloCreateSchemaJob extends AbstractStringResponseJob<List<SimpleFeatureType>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureJob.class);
    private DataStore datastore;

    public AccumuloCreateSchemaJob(DataStore datastore, List<SimpleFeatureType> features)
    {
        super(features);

        this.datastore = datastore;
    }

    @Override
    protected Collection<AbstractTask<?, CoalesceStringResponseType, ?>> getTasks(List<SimpleFeatureType> features)
            throws CoalesceException
    {
        List<AbstractTask<?, CoalesceStringResponseType, ?>> tasks = new ArrayList<>();

        // Create Tasks
        for (SimpleFeatureType feature : features)
        {
            AccumuloCreateSchemaTask task = new AccumuloCreateSchemaTask();
            task.setParams(feature);
            task.setTarget(datastore);

            tasks.add(task);
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
