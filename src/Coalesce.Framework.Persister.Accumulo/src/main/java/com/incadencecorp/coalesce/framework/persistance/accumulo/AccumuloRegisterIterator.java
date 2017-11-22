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

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIteratorDataTypes;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.DataStore;
import org.geotools.factory.Hints;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloRegisterIterator extends CoalesceIterator<AccumuloDataConnector> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloRegisterIterator.class);

    public static final String ENTITY_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityKey());
    public static final String ENTITY_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getName());
    public static final String ENTITY_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getSource());
    public static final String ENTITY_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getVersion());
    public static final String ENTITY_ID_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityId());
    public static final String ENTITY_ID_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityIdType());
    public static final String ENTITY_DATE_CREATED_COLUMN_NAME = normalize(CoalescePropertyFactory.getDateCreated());
    public static final String ENTITY_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModified());
    public static final String ENTITY_TITLE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityTitle());
    public static final String ENTITY_STATUS_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityStatus());
    public static final String ENTITY_SCOPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityScope());
    public static final String ENTITY_CREATOR_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityCreator());
    public static final String ENTITY_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityType());

    public static final String LINKAGE_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityKey());
    public static final String LINKAGE_ENTITY1_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityKey());
    public static final String LINKAGE_ENTITY1_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getName());
    public static final String LINKAGE_ENTITY1_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getSource());
    public static final String LINKAGE_ENTITY1_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getVersion());
    public static final String LINKAGE_ENTITY2_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageEntityKey());
    public static final String LINKAGE_ENTITY2_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageName());
    public static final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageSource());
    public static final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageVersion());
    public static final String LINKAGE_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModified());
    public static final String LINKAGE_LABEL_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageLabel());
    public static final String LINKAGE_LINK_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageType());

    public static final String DTG_INDEX = "geomesa.index.dtg";

    private static String normalize(PropertyName name)
    {
        return CoalescePropertyFactory.getColumnName(name);
    }

    private final ICoalesceNormalizer normalizer;
    private final CoalesceIteratorDataTypes iterator;

    public AccumuloRegisterIterator(ICoalesceNormalizer normalizer)
    {
        this.normalizer = normalizer;
        this.iterator = new CoalesceIteratorDataTypes(normalizer);
    }

    public void register(CoalesceEntityTemplate template, AccumuloDataConnector connector) throws CoalesceException
    {
        processAllElements(template.createNewEntity(), connector);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, AccumuloDataConnector connector)
            throws CoalesceException
    {
        String featureName = normalizer.normalize(recordset.getName());

        try
        {
            DataStore datastore = connector.getGeoDataStore();
            if (AccumuloSettings.overrideFeatures() || datastore.getSchema(featureName) == null)
            {
                String recordKeyCol = normalizer.normalize(recordset.getName(), "recordkey");

                // Get Feature Fields
                Map<String, ECoalesceFieldDataTypes> fields = new HashMap<>();
                fields.put(ENTITY_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_TITLE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(recordKeyCol, ECoalesceFieldDataTypes.BOOLEAN_TYPE);
                fields.putAll(iterator.getDataTypes(recordset));

                // Create Feature
                SimpleFeatureType feature = CoalesceFeatureTypeFactory.createSimpleFeatureType(featureName,
                                                                                               fields,
                                                                                               new AccumuloMapperImpl());

                // Create Indexes
                // TODO Do we need a full index on the record key?
                createIndex(feature, recordKeyCol, EIndex.FULL, ECardinality.HIGH);
                createIndex(feature, ENTITY_KEY_COLUMN_NAME, EIndex.FULL, ECardinality.HIGH);

                feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                feature.getUserData().put(DTG_INDEX, null);

                datastore.createSchema(feature);
                LOGGER.info("Created Schema: {}", feature.getName());

                if (LOGGER.isTraceEnabled())
                {
                    for (AttributeType attr : feature.getTypes())
                    {
                        LOGGER.trace("\t{}", attr.getName());
                    }
                }

                SimpleFeatureType schema = datastore.getSchema(feature.getName());
                schema.getUserData().put(DTG_INDEX, null);
            }
        }
        catch (IOException e)
        {
            throw new CoalesceException("(FAILED) Registering Recordset", e);
        }

        return false;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, AccumuloDataConnector connector) throws CoalesceException
    {
        final String indexes = "records,id,attr";

        try
        {
            DataStore datastore = connector.getGeoDataStore();
            if (AccumuloSettings.overrideFeatures() || datastore.getSchema(AccumuloDataConnector.ENTITY_FEATURE_NAME) == null)
            {
                // Get Feature Fields
                Map<String, ECoalesceFieldDataTypes> fields = new HashMap<>();
                fields.put(ENTITY_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_VERSION_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_DATE_CREATED_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
                fields.put(ENTITY_LAST_MODIFIED_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
                fields.put(ENTITY_TITLE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);

                fields.put(ENTITY_ID_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_ID_TYPE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_SCOPE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_CREATOR_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_TYPE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(ENTITY_STATUS_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);

                // Create Feature
                SimpleFeatureType feature = CoalesceFeatureTypeFactory.createSimpleFeatureType(AccumuloDataConnector.ENTITY_FEATURE_NAME,
                                                                                               fields,
                                                                                               new AccumuloMapperImpl());

                feature.getUserData().put(DTG_INDEX, ENTITY_LAST_MODIFIED_COLUMN_NAME);

                // Create Indexes (Key index is high because there is only one record per entity)
                createIndex(feature, ENTITY_KEY_COLUMN_NAME, EIndex.FULL, ECardinality.HIGH);
                createIndex(feature, ENTITY_NAME_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);
                createIndex(feature, ENTITY_SOURCE_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);
                createIndex(feature, ENTITY_VERSION_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);
                createIndex(feature, ENTITY_STATUS_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);

                feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                feature.getUserData().put("geomesa.indexes.enabled", indexes);

                datastore.createSchema(feature);
                LOGGER.info("Created Schema: {}", feature.getName());

                if (LOGGER.isTraceEnabled())
                {
                    for (AttributeType attr : feature.getTypes())
                    {
                        LOGGER.trace("\t{}", attr.getName());
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalesceException("(FAILED) Registering Entity Feature", e);
        }
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, AccumuloDataConnector connector)
            throws CoalesceException
    {
        final String indexes = "records,id,attr";

        try
        {
            DataStore datastore = connector.getGeoDataStore();

            if (AccumuloSettings.overrideFeatures() || datastore.getSchema(AccumuloDataConnector.LINKAGE_FEATURE_NAME) == null)
            {
                // Get Feature Fields
                Map<String, ECoalesceFieldDataTypes> fields = new HashMap<>();
                fields.put(LINKAGE_ENTITY1_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_ENTITY1_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_ENTITY1_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_ENTITY1_VERSION_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_ENTITY2_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
                fields.put(LINKAGE_ENTITY2_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
                fields.put(LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_ENTITY2_VERSION_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_LAST_MODIFIED_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
                fields.put(LINKAGE_LABEL_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
                fields.put(LINKAGE_LINK_TYPE_COLUMN_NAME, ECoalesceFieldDataTypes.ENUMERATION_TYPE);

                // Create Feature
                SimpleFeatureType feature = CoalesceFeatureTypeFactory.createSimpleFeatureType(AccumuloDataConnector.LINKAGE_FEATURE_NAME,
                                                                                               fields,
                                                                                               new AccumuloMapperImpl());

                feature.getUserData().put(DTG_INDEX, LINKAGE_LAST_MODIFIED_COLUMN_NAME);

                createIndex(feature, LINKAGE_ENTITY1_KEY_COLUMN_NAME, EIndex.JOIN, ECardinality.HIGH);
                createIndex(feature, LINKAGE_ENTITY2_KEY_COLUMN_NAME, EIndex.JOIN, ECardinality.HIGH);
                createIndex(feature, LINKAGE_LABEL_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);
                createIndex(feature, LINKAGE_LINK_TYPE_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);

                feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                feature.getUserData().put("geomesa.indexes.enabled", indexes);

                datastore.createSchema(feature);
                LOGGER.info("Created Schema: {}", feature.getName());

                if (LOGGER.isTraceEnabled())
                {
                    for (AttributeType attr : feature.getTypes())
                    {
                        LOGGER.trace("\t{}", attr.getName());
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalesceException("(FAILED) Registering Entity Feature", e);
        }

        return false;
    }

    private void createIndex(SimpleFeatureType feature, String property, EIndex index, ECardinality cardinality)
    {
        AttributeDescriptor descriptor = feature.getDescriptor(property);
        Map<Object, Object> userData = descriptor.getUserData();

        userData.put("index", index.toString());
        userData.put("cardinality", cardinality.toString());
    }

    private enum EIndex {
        FULL,
        JOIN;

        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }

    private enum ECardinality {
        HIGH,
        LOW;

        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }
}
