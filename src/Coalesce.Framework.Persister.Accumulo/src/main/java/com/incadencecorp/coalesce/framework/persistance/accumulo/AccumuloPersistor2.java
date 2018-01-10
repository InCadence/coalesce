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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.accumulo.jobs.AccumuloFeatureJob;
import com.incadencecorp.coalesce.framework.util.CoalesceCompressionUtil;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.commons.lang.NotImplementedException;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author Derek Clemenzi
 */
public class AccumuloPersistor2 extends AccumuloTemplatePersistor implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistor2.class);

    private final boolean useCompression;
    private AccumuloFeatureIterator iterator = null;

    /**
     * Default constructor using {@link AccumuloSettings} for configuration
     *
     * @throws CoalescePersistorException
     */
    public AccumuloPersistor2()
    {
        this(AccumuloSettings.getParameters());
    }

    /**
     * Default Constructor using a default {@link ExecutorService}
     *
     * @param params Configuration parameters
     */
    public AccumuloPersistor2(Map<String, String> params)
    {
        this(null, params);
    }

    /**
     * Specify an external {@link ExecutorService} to use for internal threads.
     *
     * @param service Service pool used for executing internal task in parallel.
     * @param params  Configuration parameters
     */
    public AccumuloPersistor2(ExecutorService service, Map<String, String> params)
    {
        super(service, params);

        if (params.containsKey(AccumuloDataConnector.USE_COMPRESSION))
        {
            useCompression = Boolean.parseBoolean(params.get(AccumuloDataConnector.USE_COMPRESSION));
        }
        else
        {
            useCompression = false;
        }
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        StopWatch watch = new StopWatch();
        watch.start();

        List<Mutation> entityMutations = new ArrayList<>();
        List<Mutation> indexMutations = new ArrayList<>();
        List<String> keysToDelete = new ArrayList<>();

        Map<String, AccumuloFeatureIterator.FeatureCollections> features = new HashMap<>();

        // Create Mutations & Features
        for (CoalesceEntity entity : entities)
        {
            try
            {
                if (!allowRemoval || entity.getStatus() != ECoalesceObjectStatus.DELETED)
                {
                    // Persist XML
                    MutationWrapperFactory mfactory = new MutationWrapperFactory(useCompression);
                    MutationWrapper mutationGuy = mfactory.createMutationGuy(entity);

                    entityMutations.add(mutationGuy.getMutation());

                    // Persist Index
                    Mutation mutation = new Mutation(entity.getKey());
                    Text columnFamily = new Text(
                            entity.getEntityIdType() + "\0" + entity.getEntityId() + "\0" + entity.getName() + "\0"
                                    + entity.getSource());
                    mutation.put(columnFamily, new Text(entity.getNamePath()), new Value(new byte[0]));
                    indexMutations.add(mutation);
                }
                else
                {
                    keysToDelete.add(entity.getKey());
                }

                // Create Features
                getIterator().iterate(entity, features);
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                                   entity.getKey(),
                                                                   entity.getType(),
                                                                   e.getMessage()), e);
            }
        }

        watch.finish();
        LOGGER.debug("Creating Mutations & Features:  {} ms", watch.getWorkLife());
        watch.reset();
        watch.start();

        AccumuloFeatureJob job = new AccumuloFeatureJob(getDataConnector());
        job.setFeatures(features);
        job.setConfig(getConfig());
        job.setMutations(entityMutations, indexMutations);
        job.setKeysToDelete(keysToDelete);
        job.setExecutor(this);
        checkResults(job.call());

        watch.finish();
        LOGGER.trace("Saved Features:  {} ms", watch.getWorkLife());
        watch.reset();
        watch.start();

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {

        List<CoalesceEntity> results = new ArrayList<>();

        for (String xml : getEntityXml(keys))
        {
            CoalesceEntity entity = new CoalesceEntity();

            // Found?
            if (!StringHelper.isNullOrEmpty(xml) && entity.initialize(xml))
            {
                // Yes; Add to Results
                results.add(entity);
            }
        }

        return results.toArray(new CoalesceEntity[results.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> results = new ArrayList<>();
        ArrayList<Range> ranges = new ArrayList<>();
        for (String key : keys)
        {
            ranges.add(new Range(key));
        }

        try (CloseableBatchScanner scanner = new CloseableBatchScanner(getDataConnector().getDBConnector(),
                                                                       AccumuloDataConnector.COALESCE_ENTITY_TABLE,
                                                                       Authorizations.EMPTY,
                                                                       4))
        {
            scanner.setRanges(ranges);
            IteratorSetting iterator = new IteratorSetting(1, "modifiedFilter", RegExFilter.class);
            RegExFilter.setRegexs(iterator, null, "entity:*", "entityxml", null, false, true);
            scanner.addScanIterator(iterator);

            for (Map.Entry<Key, Value> e : scanner)
            {
                try
                {
                    results.add(CoalesceCompressionUtil.decompress(e.getValue().get()));
                }
                catch (IOException e1)
                {
                    throw new CoalescePersistorException(e1);
                }
            }
        }
        catch (TableNotFoundException ex)
        {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return results.toArray(new String[results.size()]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.CREATE,
                          EPersistorCapabilities.READ,
                          EPersistorCapabilities.DELETE,
                          EPersistorCapabilities.UPDATE,
                          EPersistorCapabilities.READ_TEMPLATES);
    }

    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        /*
        Object value = null;

        // TODO We will want to create a a table of just field values by key for
        // now we will scan the main table
        Connector dbConnector = getDataConnector().getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.COALESCE_ENTITY_TABLE,
                                                                Authorizations.EMPTY))
        {
            // Set up an RegEx Iterator to get the row with a field with the key
            IteratorSetting iter = new IteratorSetting(20, "fieldkeymatch", RegExFilter.class);
            // Only get rows for fields that hold key values and match the key
            RegExFilter.setRegexs(iter, null, "field.*", "key", fieldKey, false, true);
            keyscanner.addScanIterator(iter);

            // Just return the first entry
            if (keyscanner.iterator().hasNext())
            {
                Key rowKey = keyscanner.iterator().next().getKey();
                String key = rowKey.getRow().toString();
                Text cf = rowKey.getColumnFamily();
                try (CloseableScanner valuescanner = new CloseableScanner(dbConnector,
                                                                          AccumuloDataConnector.COALESCE_ENTITY_TABLE,
                                                                          Authorizations.EMPTY))
                {
                    valuescanner.setRange(new Range(key));
                    valuescanner.fetchColumn(cf, new Text("value"));
                    if (valuescanner.iterator().hasNext())
                    {
                        value = valuescanner.iterator().next().getValue().toString();
                    }
                    valuescanner.close();
                }
            }
            keyscanner.close();
        }
        catch (TableNotFoundException ex)
        {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }

        return value;
        */
        throw new NotImplementedException("Not Implemented");
    }

    private AccumuloFeatureIterator getIterator() throws CoalesceException
    {
        if (iterator == null)
        {
            iterator = new AccumuloFeatureIterator(getDataConnector().getGeoDataStore(), getNormalizer());
        }

        return iterator;
    }

}
