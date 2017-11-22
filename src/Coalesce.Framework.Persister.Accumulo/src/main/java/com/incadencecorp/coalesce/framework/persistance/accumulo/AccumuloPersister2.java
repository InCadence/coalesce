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

import com.google.common.base.Stopwatch;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 */
public class AccumuloPersister2 extends AccumuloTemplatePersister implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersister2.class);

    private BatchWriterConfig config;
    private AccumuloFeatureIterator iterator = null;

    /**
     * Default Constructor
     *
     * @param params
     * @throws CoalescePersistorException
     */
    public AccumuloPersister2(Map<String, String> params)
    {
        super(params);

        config = new BatchWriterConfig();
        config.setMaxLatency(1, TimeUnit.SECONDS);
        config.setMaxMemory(52428800);
        config.setTimeout(600, TimeUnit.SECONDS);
        config.setMaxWriteThreads(10);
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        Stopwatch watch = new Stopwatch();

        watch.start();

        List<Mutation> entityMutations = new ArrayList<>();
        List<Mutation> indexMutations = new ArrayList<>();
        Map<String, DefaultFeatureCollection> features = new HashMap<>();

        // Create Mutations & Features
        for (CoalesceEntity entity : entities)
        {
            try
            {
                // Persist XML
                MutationWrapperFactory mfactory = new MutationWrapperFactory();
                MutationWrapper mutationGuy = mfactory.createMutationGuy(entity);

                entityMutations.add(mutationGuy.getMutation());

                // Persist Index
                Mutation mutation = new Mutation(entity.getKey());
                Text columnFamily = new Text(
                        entity.getEntityIdType() + "\0" + entity.getEntityId() + "\0" + entity.getName() + "\0"
                                + entity.getSource());
                mutation.put(columnFamily, new Text(entity.getNamePath()), new Value(new byte[0]));

                indexMutations.add(mutation);

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

        LOGGER.debug("Creating Mutations & Features:  {} ms", watch.elapsed(TimeUnit.MILLISECONDS));
        watch.reset();
        watch.start();

        AccumuloDataConnector connector = getDataConnector();

        // Write Mutations
        writeMutation(connector, AccumuloDataConnector.COALESCE_ENTITY_TABLE, config, entityMutations);
        writeMutation(connector, AccumuloDataConnector.COALESCE_ENTITY_IDX_TABLE, config, indexMutations);

        LOGGER.debug("Saved Mutations:  {} ms", watch.elapsed(TimeUnit.MILLISECONDS));
        watch.reset();

        // Write Features
        DataStore datastore = connector.getGeoDataStore();

        // TODO Check if it exists

        for (Map.Entry<String, DefaultFeatureCollection> entry : features.entrySet())
        {
            try
            {
                watch.start();
                SimpleFeatureStore featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getKey());

                //				GEOMESA Does not currently support transactions
                //               Transaction transaction = new DefaultTransaction();
                //                featureStore.setTransaction(transaction);
                featureStore.addFeatures(entry.getValue());

                //                transaction.commit();
                //                transaction.close();

            }
            catch (IOException | IllegalArgumentException e)
            {
                LOGGER.error("(FAILED) Saving Feature: ({})", entry.getKey(), e);
            }
            finally
            {
                LOGGER.debug("Saved Feature ({}):  {} ms", entry.getKey(), watch.elapsed(TimeUnit.MILLISECONDS));
                watch.reset();
            }

        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {

        List<CoalesceEntity> results = new ArrayList<CoalesceEntity>();

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
        List<String> results = new ArrayList<String>();
        ArrayList<Range> ranges = new ArrayList<Range>();
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
            IteratorSetting iter = new IteratorSetting(1, "modifiedFilter", RegExFilter.class);
            RegExFilter.setRegexs(iter, null, "entity:*", "entityxml", null, false, true);
            scanner.addScanIterator(iter);

            for (Map.Entry<Key, Value> e : scanner)
            {
                String xml = new String(e.getValue().get());
                results.add(xml);
            }
        }
        catch (TableNotFoundException ex)
        {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return results != null ? results.toArray(new String[results.size()]) : null;
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

    private void writeMutation(AccumuloDataConnector connector,
                               String table,
                               BatchWriterConfig config,
                               List<Mutation> mutations) throws CoalescePersistorException
    {
        try (CloseableBatchWriter writer = new CloseableBatchWriter(connector.getDBConnector(), table, config))
        {
            for (Mutation mutation : mutations)
            {
                writer.addMutation(mutation);
                writer.flush();
            }
        }
        catch (MutationsRejectedException | TableNotFoundException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED, table, table, e.getMessage()), e);
        }
    }

    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        // TODO We will want to create a a table of just field values by key for
        // now we will scan the main table
        Object value = null;
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
