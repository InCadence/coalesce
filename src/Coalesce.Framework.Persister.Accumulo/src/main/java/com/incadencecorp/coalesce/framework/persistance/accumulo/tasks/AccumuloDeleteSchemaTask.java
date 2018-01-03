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

package com.incadencecorp.coalesce.framework.persistance.accumulo.tasks;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This task will remove the schema on the provided datastore.
 *
 * @author Derek Clemenzi
 */
public class AccumuloDeleteSchemaTask extends AbstractTask<String, CoalesceStringResponseType, DataStore> {

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<DataStore, String> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        DataStore datastore = parameters.getTarget();
        String name = parameters.getParams();

        try
        {
            datastore.removeSchema(name);
            result.setStatus(EResultStatus.SUCCESS);
        }
        catch (IOException | IllegalArgumentException e)
        {
            throw new CoalescePersistorException(String.format("(FAILED) Removing Schema: (%s)", name), e);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(String params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }
}
