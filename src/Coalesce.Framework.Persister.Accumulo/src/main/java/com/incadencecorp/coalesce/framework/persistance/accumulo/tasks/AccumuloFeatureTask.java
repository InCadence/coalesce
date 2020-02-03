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
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloSettings;
import com.incadencecorp.coalesce.framework.persistance.accumulo.FeatureCollections;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This task adds and removes features from the specified datastore based on its parameters.
 *
 * @author Derek Clemenzi
 */
public class AccumuloFeatureTask extends
        AbstractTask<Map.Entry<String, FeatureCollections>, CoalesceStringResponseType, DataStore> {

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureTask.class);

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<DataStore, Map.Entry<String, FeatureCollections>> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();
        String featureTypeName = parameters.getParams().getKey();
        DefaultFeatureCollection featuresToAdd = parameters.getParams().getValue().featuresToAdd;
        List<FeatureId> keysToDelete = parameters.getParams().getValue().keysToDelete;

        try
        {
            if (AccumuloSettings.useFeatureWritter())
            {
                useFeatureWriter(parameters.getTarget(), featureTypeName, featuresToAdd, keysToDelete);
            }
            else
            {
                useFeatureStore(parameters.getTarget(), featureTypeName, featuresToAdd, keysToDelete);
            }

            result.setStatus(EResultStatus.SUCCESS);
        }
        catch (IOException | IllegalArgumentException e)
        {
            throw new CoalescePersistorException(String.format("(FAILED) Updating: (%s)", featureTypeName), e);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(Map.Entry<String, FeatureCollections> params,
                                                boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }

    private void useFeatureStore(DataStore store,
                                 String featureTypeName,
                                 DefaultFeatureCollection featuresToAdd,
                                 List<FeatureId> keysToDelete) throws IOException
    {
        StopWatch watch = new StopWatch();

        SimpleFeatureStore featureStore = (SimpleFeatureStore) store.getFeatureSource(featureTypeName);

        if (featureStore != null)
        {
            if (featuresToAdd.size() > 0)
            {
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Updating ({}) ({})", featuresToAdd.size(), featureTypeName);
                    watch.reset();
                    watch.start();
                }

                featureStore.addFeatures(featuresToAdd);

                if (LOGGER.isTraceEnabled())
                {
                    watch.finish();
                    LOGGER.trace("Completed updating ({}) in ({}) ms", featureTypeName, watch.getTotalLife());
                }
            }

            if (keysToDelete.size() > 0)
            {
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Deleting ({}) from ({})", keysToDelete.toString(), featureTypeName);
                    watch.reset();
                    watch.start();
                }

                featureStore.removeFeatures(FF.id(keysToDelete.toArray(new FeatureId[keysToDelete.size()])));

                if (LOGGER.isTraceEnabled())
                {
                    watch.finish();
                    LOGGER.trace("Completed deleting ({}) in ({}) ms", featureTypeName, watch.getTotalLife());
                }
            }
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Feature Store", featureTypeName));
        }
    }

    /**
     * TODO Does not handle the delete case
     */
    private void useFeatureWriter(DataStore store, String featureTypeName, DefaultFeatureCollection featuresToAdd, List<FeatureId> keysToDelete) throws IOException
    {
        for (SimpleFeature feature : featuresToAdd)
        {
            LOGGER.debug("ID = {} {}", feature.getID(), feature.getName());

            StopWatch watch = new StopWatch();

            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer = store.getFeatureWriter(featureTypeName,
                                                                                                 FF.id(Collections.singleton(
                                                                                                         FF.featureId(feature.getID()))),
                                                                                                 Transaction.AUTO_COMMIT))
            {
                if (writer.hasNext())
                {
                    LOGGER.trace("Found");

                    SimpleFeature toModify = writer.next();

                    watch.start();

                    for (AttributeDescriptor descriptor : feature.getType().getAttributeDescriptors())
                    {
                        Object value = feature.getAttribute(descriptor.getName());
                        if (value != null)
                        {
                            LOGGER.trace("Writing: {} = {}", descriptor.getName(), value);
                            toModify.setAttribute(descriptor.getName(), feature.getAttribute(descriptor.getName()));
                        }
                    }

                    writer.write();
                }
            }
            finally
            {
                watch.finish();
                LOGGER.debug("Create Writer: ({}) Updated ({}) in ({}) ms", watch.getPendingLife(), featureTypeName, watch.getWorkLife());
            }
        }
    }
}
