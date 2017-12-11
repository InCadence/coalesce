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

import com.drew.lang.StringUtil;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIteratorDataTypes;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.factory.Hints;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.PropertyName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloRegisterIterator extends CoalesceIterator<List<SimpleFeatureType>> {

    private final String ENTITY_KEY_COLUMN_NAME;
    private final String ENTITY_NAME_COLUMN_NAME;
    private final String ENTITY_SOURCE_COLUMN_NAME;
    private final String ENTITY_VERSION_COLUMN_NAME;
    private final String ENTITY_ID_COLUMN_NAME;
    private final String ENTITY_ID_TYPE_COLUMN_NAME;
    private final String ENTITY_DATE_CREATED_COLUMN_NAME;
    private final String ENTITY_LAST_MODIFIED_COLUMN_NAME;
    private final String ENTITY_TITLE_COLUMN_NAME;
    private final String ENTITY_STATUS_COLUMN_NAME;
    private final String ENTITY_SCOPE_COLUMN_NAME;
    private final String ENTITY_CREATOR_COLUMN_NAME;
    private final String ENTITY_TYPE_COLUMN_NAME;

    private final String LINKAGE_KEY_COLUMN_NAME;
    private final String LINKAGE_ENTITY1_KEY_COLUMN_NAME;
    private final String LINKAGE_ENTITY1_NAME_COLUMN_NAME;
    private final String LINKAGE_ENTITY1_SOURCE_COLUMN_NAME;
    private final String LINKAGE_ENTITY1_VERSION_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_KEY_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_NAME_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME;
    private final String LINKAGE_LAST_MODIFIED_COLUMN_NAME;
    private final String LINKAGE_LABEL_COLUMN_NAME;
    private final String LINKAGE_LINK_TYPE_COLUMN_NAME;

    public static final String DTG_INDEX = "geomesa.index.dtg";
    public static final String INDEXES = "geomesa.indexes.enabled";

    private final ICoalesceNormalizer normalizer;
    private final CoalesceIteratorDataTypes iterator;

    public AccumuloRegisterIterator(ICoalesceNormalizer normalizer)
    {
        this.normalizer = normalizer;
        this.iterator = new CoalesceIteratorDataTypes(normalizer);

        ENTITY_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityKey());
        ENTITY_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getName());
        ENTITY_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getSource());
        ENTITY_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getVersion());
        ENTITY_ID_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityId());
        ENTITY_ID_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityIdType());
        ENTITY_DATE_CREATED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getDateCreated());
        ENTITY_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        ENTITY_TITLE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityTitle());
        ENTITY_STATUS_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityStatus());
        ENTITY_SCOPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityScope());
        ENTITY_CREATOR_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityCreator());
        ENTITY_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityType());

        LINKAGE_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityKey());
        LINKAGE_ENTITY1_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityKey());
        LINKAGE_ENTITY1_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getName());
        LINKAGE_ENTITY1_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getSource());
        LINKAGE_ENTITY1_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getVersion());
        LINKAGE_ENTITY2_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageEntityKey());
        LINKAGE_ENTITY2_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageName());
        LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageSource());
        LINKAGE_ENTITY2_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageVersion());
        LINKAGE_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        LINKAGE_LABEL_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageLabel());
        LINKAGE_LINK_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageType());

    }

    private String getColumnName(PropertyName name)
    {
        return CoalescePropertyFactory.getColumnName(normalizer, name);
    }

    public void register(CoalesceEntityTemplate template, List<SimpleFeatureType> features) throws CoalesceException
    {
        processAllElements(template.createNewEntity(), features);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, List<SimpleFeatureType> features)
            throws CoalesceException
    {
        String featureName = normalizer.normalize(recordset.getName());

        String recordKeyCol = normalizer.normalize(recordset.getName(), "recordkey");

        // Get Feature Fields
        Map<String, ECoalesceFieldDataTypes> fields = getCommonFields();
        //fields.put(recordKeyCol, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.putAll(iterator.getDataTypes(recordset));

        // Create Feature
        SimpleFeatureType feature = CoalesceFeatureTypeFactory.createSimpleFeatureType(featureName,
                                                                                       fields,
                                                                                       new AccumuloMapperImpl());

        // Create Indexes
        // TODO Do we need a full index on the record key?
        //createIndex(feature, recordKeyCol, EIndex.FULL, ECardinality.HIGH);
        createIndex(feature, ENTITY_KEY_COLUMN_NAME, EIndex.FULL, ECardinality.HIGH);

        feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
        feature.getUserData().put(DTG_INDEX, null);

        features.add(feature);

        return false;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, List<SimpleFeatureType> features) throws CoalesceException
    {
        final String[] indexes = { "records", "attr" };

        // Get Feature Fields
        Map<String, ECoalesceFieldDataTypes> fields = getCommonFields();
        fields.put(ENTITY_ID_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_ID_TYPE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_SCOPE_COLUMN_NAME, ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        fields.put(ENTITY_CREATOR_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_TYPE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_STATUS_COLUMN_NAME, ECoalesceFieldDataTypes.ENUMERATION_TYPE);

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
        feature.getUserData().put(INDEXES, StringUtil.join(indexes, ","));

        features.add(feature);

        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, List<SimpleFeatureType> features)
            throws CoalesceException
    {
        final String[] indexes = { "records", "attr" };

        // Get Feature Fields
        Map<String, ECoalesceFieldDataTypes> fields = getCommonFields();
        fields.put(LINKAGE_ENTITY2_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(LINKAGE_ENTITY2_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
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

        //createIndex(feature, LINKAGE_ENTITY1_KEY_COLUMN_NAME, EIndex.JOIN, ECardinality.HIGH);
        createIndex(feature, LINKAGE_ENTITY2_KEY_COLUMN_NAME, EIndex.JOIN, ECardinality.HIGH);
        createIndex(feature, LINKAGE_LABEL_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);
        createIndex(feature, LINKAGE_LINK_TYPE_COLUMN_NAME, EIndex.JOIN, ECardinality.LOW);

        feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
        feature.getUserData().put(INDEXES, StringUtil.join(indexes, ","));

        features.add(feature);

        return false;
    }

    private Map<String, ECoalesceFieldDataTypes> getCommonFields()
    {
        Map<String, ECoalesceFieldDataTypes> fields = new HashMap<>();
        fields.put(ENTITY_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_VERSION_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        fields.put(ENTITY_DATE_CREATED_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        fields.put(ENTITY_LAST_MODIFIED_COLUMN_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        fields.put(ENTITY_TITLE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE);

        return fields;
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
