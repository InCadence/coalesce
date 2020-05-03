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
import com.incadencecorp.coalesce.api.ICoalesceFilter;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.filter.CoalesceVersionFilter;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import org.geotools.data.DataStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.factory.Hints;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class AccumuloFeatureIterator extends CoalesceIterator<Map<String, FeatureCollections>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureIterator.class);
    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();

    private final String ENTITY_KEY_COLUMN_NAME;
    private final String ENTITY_NAME_COLUMN_NAME;
    private final String ENTITY_SOURCE_COLUMN_NAME;
    private final String ENTITY_VERSION_COLUMN_NAME;
    private final String ENTITY_ID_COLUMN_NAME;
    private final String ENTITY_ID_TYPE_COLUMN_NAME;
    private final String ENTITY_DATE_CREATED_COLUMN_NAME;
    private final String ENTITY_LAST_MODIFIED_BY_COLUMN_NAME;
    private final String ENTITY_LAST_MODIFIED_COLUMN_NAME;
    private final String ENTITY_TITLE_COLUMN_NAME;
    private final String ENTITY_STATUS_COLUMN_NAME;
    private final String ENTITY_SCOPE_COLUMN_NAME;
    private final String ENTITY_CREATOR_COLUMN_NAME;
    private final String ENTITY_TYPE_COLUMN_NAME;

    private final String LINKAGE_ENTITY2_KEY_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_NAME_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME;
    private final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME;
    private final String LINKAGE_LAST_MODIFIED_COLUMN_NAME;
    private final String LINKAGE_LABEL_COLUMN_NAME;
    private final String LINKAGE_LINK_TYPE_COLUMN_NAME;
    private final String LINKAGE_LINK_STATUS_COLUMN_NAME;

    private DataStore datastore;
    private ICoalesceNormalizer normalizer;
    private ICoalesceFilter filter = new CoalesceVersionFilter();
    private CoalesceCommonColumns columns;

    public AccumuloFeatureIterator(DataStore datastore, ICoalesceNormalizer normalizer)
    {
        this.datastore = datastore;
        this.normalizer = normalizer;

        ENTITY_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityKey());
        ENTITY_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getName());
        ENTITY_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getSource());
        ENTITY_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getVersion());
        ENTITY_ID_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityId());
        ENTITY_ID_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityIdType());
        ENTITY_DATE_CREATED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getDateCreated());
        ENTITY_LAST_MODIFIED_BY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModifiedBy());
        ENTITY_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        ENTITY_TITLE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityTitle());
        ENTITY_STATUS_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityStatus());
        ENTITY_SCOPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityScope());
        ENTITY_CREATOR_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getCreatedBy());
        ENTITY_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getEntityType());

        LINKAGE_ENTITY2_KEY_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageEntityKey());
        LINKAGE_ENTITY2_NAME_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageName());
        LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageSource());
        LINKAGE_ENTITY2_VERSION_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageVersion());
        LINKAGE_LAST_MODIFIED_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLastModified());
        LINKAGE_LABEL_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageLabel());
        LINKAGE_LINK_TYPE_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageType());
        LINKAGE_LINK_STATUS_COLUMN_NAME = getColumnName(CoalescePropertyFactory.getLinkageStatus());
    }

    /**
     * Set a filter to use when determining which features to include.
     *
     * @param filter used to filter features.
     */
    public void setFilter(ICoalesceFilter filter)
    {
        this.filter = filter;
    }

    private String getColumnName(PropertyName name)
    {
        return CoalescePropertyFactory.getColumnName(normalizer, name);
    }

    public void iterate(CoalesceEntity entity, Map<String, FeatureCollections> features) throws CoalesceException
    {
        processAllElements(entity, features);
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, Map<String, FeatureCollections> features)
            throws CoalesceException
    {
        String featureName = AccumuloDataConnector.ENTITY_FEATURE_NAME;

        try
        {
            SimpleFeatureType featureType = datastore.getSchema(featureName);

            if (featureType != null)
            {
                FeatureCollections collection = getCollection(features, featureName);

                String id = entity.getKey();

                if (!entity.isMarkedDeleted())
                {
                    SimpleFeature feature = new SimpleFeatureBuilder(featureType).buildFeature(id);
                    feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                    feature.getUserData().put(AccumuloRegisterIterator.DTG_INDEX, null);

                    populateFeature(feature, entity);

                    ensureDefaultGeometry(featureType, feature);

                    collection.featuresToAdd.add(feature);
                }
                else
                {
                    collection.keysToDelete.add(FF.featureId(id));
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalesceException(String.format(CoalesceErrors.INVALID_OBJECT,
                                                      featureName,
                                                      datastore.getInfo().getSource()), e);
        }
        // Continue Recursion
        return true;
    }

    private FeatureCollections getCollection(Map<String, FeatureCollections> features, String name)
    {
        FeatureCollections collection = features.get(name);

        if (collection == null)
        {
            collection = new FeatureCollections();
            features.put(name, collection);
        }

        return collection;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, Map<String, FeatureCollections> features)
            throws CoalesceException
    {
        String featureName = AccumuloDataConnector.LINKAGE_FEATURE_NAME;

        try
        {
            SimpleFeatureType featureType = datastore.getSchema(featureName);

            if (featureType != null)
            {
                FeatureCollections collection = getCollection(features, featureName);
                CoalesceEntity entity = section.getEntity();

                if (entity != null)
                {
                    SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);

                    for (CoalesceLinkage linkage : section.getLinkagesAsList())
                    {
                        if (filter.filter(linkage))
                        {
                            String id = linkage.getKey();

                            if (!linkage.isMarkedDeleted() && !entity.isMarkedDeleted())
                            {
                                SimpleFeature feature = builder.buildFeature(id);
                                feature.getUserData().put(Hints.USE_PROVIDED_FID, true);

                                populateCommon(feature, entity);
                                populateFeature(feature, linkage);

                                ensureDefaultGeometry(featureType, feature);

                                collection.featuresToAdd.add(feature);
                            }
                            else
                            {
                                collection.keysToDelete.add(FF.featureId(id));
                            }
                        }
                    }
                }
                else
                {
                    throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT, "Linkage's Entity is Null"));
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalesceException(String.format(CoalesceErrors.INVALID_OBJECT,
                                                      featureName,
                                                      datastore.getInfo().getSource()), e);
        }
        return false;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Map<String, FeatureCollections> features)
            throws CoalesceException
    {
        if (recordset.isFlatten())
        {
            String featureName = normalizer.normalize(recordset.getName());

            try
            {
                SimpleFeatureType featureType = datastore.getSchema(featureName);

                if (featureType != null)
                {
                    FeatureCollections collection = getCollection(features, featureName);

                    CoalesceEntity entity = recordset.getEntity();

                    if (entity != null)
                    {
                        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);

                        for (CoalesceRecord record : recordset.getAllRecords())
                        {
                            String id = record.getKey();

                            if (filter.filter(record) && record.isFlatten())
                            {
                                if (!record.isMarkedDeleted() && !entity.isMarkedDeleted())
                                {
                                    SimpleFeature feature = builder.buildFeature(id);
                                    feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                                    feature.getUserData().put(AccumuloRegisterIterator.DTG_INDEX, null);

                                    populateCommon(feature, entity);
                                    populateFeature(feature, record);

                                    ensureDefaultGeometry(featureType, feature);

                                    collection.featuresToAdd.add(feature);
                                }
                                else
                                {
                                    collection.keysToDelete.add(FF.featureId(id));
                                }
                            }
                        }
                    }
                    else
                    {
                        throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT,
                                                                  "Recordset's Entity is Null"));
                    }
                }
                else
                {
                    LOGGER.warn(String.format(CoalesceErrors.INVALID_OBJECT, featureName, datastore.getInfo().getSource()));
                }
            }
            catch (IOException e)
            {
                throw new CoalesceException(String.format(CoalesceErrors.INVALID_OBJECT,
                                                          featureName,
                                                          datastore.getInfo().getSource()), e);
            }
        }

        return false;
    }

    /**
     * If the default geometry field is null; then default it to the world polygon.
     *
     * @param featureType Feature Definition
     * @param feature     Feature to check for geometry field
     */
    private void ensureDefaultGeometry(FeatureType featureType, SimpleFeature feature)
    {
        GeometryDescriptor descriptor = featureType.getGeometryDescriptor();

        // Default Geometry?
        if (descriptor != null)
        {
            // Yes; Contains Value?
            if (feature.getAttribute(descriptor.getLocalName()) == null)
            {
                feature.setAttribute(descriptor.getLocalName(), createWorldPolygon());
                LOGGER.debug("({}) Default Geometry ({}) = (World)", featureType.getName(), descriptor.getLocalName());
            }
            else
            {
                LOGGER.debug("({}) Default Geometry ({}) = ({})",
                             featureType.getName(),
                             descriptor.getLocalName(),
                             feature.getAttribute(descriptor.getLocalName()).toString());
            }
        }
        else
        {
            LOGGER.trace("({}) Default Geometry (None)", featureType.getName());
        }
    }

    private void populateFeature(SimpleFeature feature, CoalesceEntity entity)
    {
        populateCommon(feature, entity);

        setFeatureAttribute(feature, ENTITY_ID_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getEntityId());
        setFeatureAttribute(feature,
                            ENTITY_ID_TYPE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getEntityIdType());
        setFeatureAttribute(feature,
                            ENTITY_STATUS_COLUMN_NAME,
                            ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            entity.getStatus().ordinal());
    }

    private void populateFeature(SimpleFeature feature, CoalesceLinkage linkage)
    {
        // TODO Is this needed since the FID is the linkage key?
        //setFeatureAttribute(feature, LINKAGE_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, linkage.getKey());

        setFeatureAttribute(feature,
                            LINKAGE_ENTITY2_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Key());
        setFeatureAttribute(feature,
                            LINKAGE_ENTITY2_NAME_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Name());
        setFeatureAttribute(feature,
                            LINKAGE_ENTITY2_SOURCE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Source());
        setFeatureAttribute(feature,
                            LINKAGE_ENTITY2_VERSION_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Version());
        setFeatureAttribute(feature,
                            LINKAGE_LAST_MODIFIED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            linkage.getLastModified());
        setFeatureAttribute(feature, LINKAGE_LABEL_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, linkage.getLabel());
        setFeatureAttribute(feature,
                            LINKAGE_LINK_TYPE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            linkage.getLinkType().ordinal());
        setFeatureAttribute(feature,
                            LINKAGE_LINK_STATUS_COLUMN_NAME,
                            ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            linkage.getStatus().ordinal());

    }

    private void populateFeature(SimpleFeature feature, CoalesceRecord record) throws CoalesceException
    {
        String recordsetName = record.getParent().getName();
        String statusAttr = normalizer.normalize(recordsetName, "status");
        setFeatureAttribute(feature, statusAttr, ECoalesceFieldDataTypes.ENUMERATION_TYPE, record.getStatus().ordinal());

        for (CoalesceField<?> field : record.getFields())
        {
            String fieldName = normalizer.normalize(recordsetName, field.getName());
            ECoalesceFieldDataTypes dataType = field.getDataType();

            if (field.isFlatten())
            {
                if (dataType == ECoalesceFieldDataTypes.FILE_TYPE)
                {
                    setFeatureAttribute(feature,
                                        fieldName,
                                        ECoalesceFieldDataTypes.STRING_TYPE,
                                        ((CoalesceFileField) field).getFilename());
                }
                else if (field.getBaseValue() != null)
                {
                    // If there is not a value do not set the attribute.
                    if (field.isListType())
                    {
                        setFeatureAttribute(feature, fieldName, dataType, field.getBaseValue());
                    }
                    else
                    {
                        setFeatureAttribute(feature, fieldName, dataType, field.getValue());
                    }
                }
            }
        }
    }

    private void populateCommon(SimpleFeature feature, CoalesceEntity entity)
    {
        setFeatureAttribute(feature, ENTITY_KEY_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getKey());
        setFeatureAttribute(feature, ENTITY_NAME_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getName());
        setFeatureAttribute(feature, ENTITY_SOURCE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getSource());
        setFeatureAttribute(feature, ENTITY_VERSION_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getVersion());
        setFeatureAttribute(feature, ENTITY_CREATOR_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getCreatedBy());
        setFeatureAttribute(feature,
                            ENTITY_DATE_CREATED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            entity.getDateCreated());
        setFeatureAttribute(feature,
                            ENTITY_LAST_MODIFIED_BY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getModifiedBy());
        setFeatureAttribute(feature,
                            ENTITY_LAST_MODIFIED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            entity.getLastModified());
        setFeatureAttribute(feature, ENTITY_TITLE_COLUMN_NAME, ECoalesceFieldDataTypes.STRING_TYPE, entity.getTitle());
    }

    private Polygon createWorldPolygon()
    {
        // create a polygon of the WORLD!!!!!
        Coordinate[] points = { new Coordinate(-180, -90), new Coordinate(-180, 90), new Coordinate(180, 90),
                                new Coordinate(180, -90), new Coordinate(-180, -90)
        };

        return (new GeometryFactory()).createPolygon(points);
    }

    private void setFeatureAttribute(SimpleFeature feature,
                                     String fieldName,
                                     ECoalesceFieldDataTypes dataType,
                                     Object fieldValue)
    {

        if (fieldValue == null)
        {
            feature.setAttribute(fieldName, null);
        }
        else
        {
            try
            {
                switch (dataType)
                {

                // These types should be able to be handled directly
                case ENUMERATION_TYPE:
                case BOOLEAN_TYPE:
                case DOUBLE_TYPE:
                case FLOAT_TYPE:
                case INTEGER_TYPE:
                case LONG_TYPE:
                case STRING_TYPE:
                case URI_TYPE:
                    feature.setAttribute(fieldName, fieldValue);
                    break;

                case STRING_LIST_TYPE:
                case DOUBLE_LIST_TYPE:
                case INTEGER_LIST_TYPE:
                case LONG_LIST_TYPE:
                case FLOAT_LIST_TYPE:
                case GUID_LIST_TYPE:
                case BOOLEAN_LIST_TYPE:
                case ENUMERATION_LIST_TYPE:
                    feature.setAttribute(fieldName, fieldValue);
                    break;

                case GUID_TYPE:
                    String guid = fieldValue.toString();
                    feature.setAttribute(fieldName, guid);
                    break;

                case GEOCOORDINATE_LIST_TYPE:
                    MultiPoint points = new GeometryFactory().createMultiPoint((Coordinate[]) fieldValue);
                    feature.setAttribute(fieldName, points);
                    break;

                case GEOCOORDINATE_TYPE:
                    Point point = new GeometryFactory().createPoint((Coordinate) fieldValue);
                    feature.setAttribute(fieldName, point);
                    break;

                case LINE_STRING_TYPE:
                    feature.setAttribute(fieldName, fieldValue);
                    break;

                case POLYGON_TYPE:
                    feature.setAttribute(fieldName, fieldValue);
                    break;

                // Circles will be converted to polygons
                case CIRCLE_TYPE:
                    // Create Polygon

                    CoalesceCircle circle = (CoalesceCircle) fieldValue;
                    GeometricShapeFactory factory = new GeometricShapeFactory();
                    factory.setSize(circle.getRadius());
                    factory.setNumPoints(360); // 1 degree points
                    factory.setCentre(circle.getCenter());
                    Polygon shape = factory.createCircle();
                    feature.setAttribute(fieldName, shape);
                    break;

                case DATE_TIME_TYPE:
                    feature.setAttribute(fieldName, ((DateTime) fieldValue).toDate());
                    break;
                case FILE_TYPE:
                case BINARY_TYPE:
                default:
                    break;
                }

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Set Feature ({}) = ({})", fieldName, feature.getAttribute(fieldName));
                }
            }
            catch (IllegalAttributeException e)
            {

                LOGGER.warn("{} => {}",
                            feature.getName(),
                            String.format(CoalesceErrors.INVALID_INPUT_REASON, fieldName, e.getMessage()));
            }
        }

    }

}
