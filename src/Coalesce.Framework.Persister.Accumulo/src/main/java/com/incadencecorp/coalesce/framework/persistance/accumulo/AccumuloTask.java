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
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloTask extends
        AbstractTask<Map.Entry<String, AccumuloFeatureIterator.FeatureCollections>, CoalesceStringResponseType, DataStore> {

    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloTask.class);

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<DataStore, Map.Entry<String, AccumuloFeatureIterator.FeatureCollections>> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        try
        {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) parameters.getTarget().getFeatureSource(parameters.getParams().getKey());

            if (featureStore != null)
            {
                //				GEOMESA Does not currently support transactions
                //               Transaction transaction = new DefaultTransaction();
                //                featureStore.setTransaction(transaction);
                featureStore.addFeatures(parameters.getParams().getValue().featuresToAdd);

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug(String.format("Deleting (%s) from (%s)",
                                               parameters.getParams().getValue().keysToDelete.toString(),
                                               featureStore.getName()));
                }

                featureStore.removeFeatures(FF.id(parameters.getParams().getValue().keysToDelete.toArray(new FeatureId[parameters.getParams().getValue().keysToDelete.size()])));

                result.setStatus(EResultStatus.SUCCESS);

                //                transaction.commit();
                //                transaction.close();
            }
        }
        catch (IOException | IllegalArgumentException e)
        {
            throw new CoalescePersistorException(String.format("(FAILED) Saving Feature: (%s)",
                                                               parameters.getParams().getKey()), e);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(Map.Entry<String, AccumuloFeatureIterator.FeatureCollections> params,
                                                boolean isTrace)
    {
        return new HashMap<String, String>();
    }

    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }
}
