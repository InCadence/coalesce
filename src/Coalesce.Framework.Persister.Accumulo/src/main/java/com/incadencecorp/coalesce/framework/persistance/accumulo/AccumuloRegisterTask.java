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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloRegisterTask extends AbstractTask<SimpleFeatureType, CoalesceStringResponseType, DataStore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloTask.class);

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<DataStore, SimpleFeatureType> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        DataStore datastore = parameters.getTarget();
        SimpleFeatureType feature = parameters.getParams();

        try
        {
            SimpleFeatureType original = datastore.getSchema(feature.getName());

            if (AccumuloSettings.overrideFeatures() || original == null)
            {
                if (original == null)
                {
                    datastore.createSchema(feature);
                    LOGGER.info("Created Schema: {}", feature.getName());

                    result.setStatus(EResultStatus.SUCCESS);
                }
                else
                {
                    result.setStatus(EResultStatus.FAILED);
                    result.setError(String.format("(FAILED) Updating Schema: %s (Not Supported)", feature.getName()));

                }

                if (result.isSuccessful() && LOGGER.isTraceEnabled())
                {
                    for (AttributeType attr : feature.getTypes())
                    {
                        LOGGER.trace("\t{}", attr.getName());
                    }
                }

            }
        }
        catch (IOException | IllegalArgumentException e)
        {
            throw new CoalescePersistorException(String.format("(FAILED) Registering Feature: (%s)", feature.getName()), e);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(SimpleFeatureType params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }
}
