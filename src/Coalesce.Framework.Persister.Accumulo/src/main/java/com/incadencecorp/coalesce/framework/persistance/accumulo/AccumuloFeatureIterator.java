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
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.joda.time.DateTime;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author Derek Clemenzi
 */
public class AccumuloFeatureIterator extends CoalesceIterator<Map<String, AccumuloFeatureIterator.FeatureCollections>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureIterator.class);
    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    private DataStore datastore;
    private ICoalesceNormalizer normalizer;

    public AccumuloFeatureIterator(DataStore datastore, ICoalesceNormalizer normalizer)
    {
        this.datastore = datastore;
        this.normalizer = normalizer;
    }

    public void iterate(CoalesceEntity entity, Map<String, AccumuloFeatureIterator.FeatureCollections> features) throws CoalesceException
    {
        processAllElements(entity, features);
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, Map<String, AccumuloFeatureIterator.FeatureCollections> features)
            throws CoalesceException
    {
        String featureName = AccumuloDataConnector.ENTITY_FEATURE_NAME;

        try
        {
            SimpleFeatureType featureType = datastore.getSchema(featureName);

            if (featureType != null)
            {
                //SimpleFeatureStore store = (SimpleFeatureStore) datastore.getFeatureSource(featureName);
                FeatureCollections collection = features.get(featureName);

                if (collection == null)
                {
                    collection = new FeatureCollections();
                    features.put(featureName, collection);
                }

                if (entity.getStatus() != ECoalesceObjectStatus.DELETED)
                {
                    SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[] {}, entity.getKey());
                    feature.getUserData().put(Hints.USE_PROVIDED_FID, true);

                    populateFeature(feature, entity);
                    ensureDefaultGeometry(featureType, feature);

                    collection.featureToAdd.add(feature);
                }
                else
                {
                    collection.keysToDelete.add(FF.featureId(entity.getKey()));
                    //store.removeFeatures(FF.id(Collections.singleton(FF.featureId(entity.getKey()))));
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

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section,
                                                  Map<String, AccumuloFeatureIterator.FeatureCollections> features) throws CoalesceException
    {
        String featureName = AccumuloDataConnector.LINKAGE_FEATURE_NAME;

        try
        {
            SimpleFeatureType featureType = datastore.getSchema(featureName);

            if (featureType != null)
            {
                //SimpleFeatureStore store = (SimpleFeatureStore) datastore.getFeatureSource(featureName);
                FeatureCollections collection = features.get(featureName);

                if (collection == null)
                {
                    collection = new FeatureCollections();
                    features.put(featureName, collection);
                }

                CoalesceEntity entity = section.getEntity();

                for (CoalesceLinkage linkage : section.getLinkagesAsList())
                {
                    if (linkage.getStatus() != ECoalesceObjectStatus.DELETED)
                    {
                        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[] {}, linkage.getKey());
                        feature.getUserData().put(Hints.USE_PROVIDED_FID, true);

                        populateFeature(feature, entity, linkage);
                        ensureDefaultGeometry(featureType, feature);

                        collection.featureToAdd.add(feature);
                    }
                    else
                    {
                        collection.keysToDelete.add(FF.featureId(linkage.getKey()));
                        //store.removeFeatures(FF.id(Collections.singleton(FF.featureId(linkage.getKey()))));
                    }
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
        String featureName = normalizer.normalize(recordset.getName());

        try
        {
            SimpleFeatureType featureType = datastore.getSchema(featureName);

            if (featureType != null)
            {
                //SimpleFeatureStore store = (SimpleFeatureStore) datastore.getFeatureSource(featureName);
                FeatureCollections collection = features.get(featureName);

                if (collection == null)
                {
                    collection = new FeatureCollections();
                    features.put(featureName, collection);
                }

                CoalesceEntity entity = recordset.getEntity();

                for (CoalesceRecord record : recordset.getAllRecords())
                {
                    if (record.getStatus() != ECoalesceObjectStatus.DELETED)
                    {
                        // TODO Check if update is needed
                        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, new Object[] {}, record.getKey());
                        feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
                        feature.getUserData().put(AccumuloRegisterIterator.DTG_INDEX, null);

                        populateFeature(feature, entity, record);
                        ensureDefaultGeometry(featureType, feature);

                        collection.featureToAdd.add(feature);
                    }
                    else
                    {
                        collection.keysToDelete.add(FF.featureId(record.getKey()));
                        //store.removeFeatures(FF.id(Collections.singleton(FF.featureId(record.getKey()))));
                    }
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

        return false;
    }

    /**
     * If the default geometry field is null; then default it to the world polygon.
     *
     * @param featureType
     * @param feature
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
                LOGGER.debug("Default Geometry ({}) = (World)", descriptor.getLocalName());
            }
            else
            {
                LOGGER.debug("Default Geometry ({}) = ({})",
                             descriptor.getLocalName(),
                             feature.getAttribute(descriptor.getLocalName()).toString());
            }
        }
        else
        {
            LOGGER.debug("Default Geometry (None)");
        }
    }

    private void populateFeature(SimpleFeature feature, CoalesceEntity entity) throws CoalesceException, IOException
    {
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getKey());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_NAME_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getName());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_SOURCE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getSource());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_TITLE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getTitle());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_VERSION_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getVersion());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_ID_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getEntityId());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_ID_TYPE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getEntityIdType());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_DATE_CREATED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            entity.getDateCreated());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_LAST_MODIFIED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            entity.getLastModified());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_STATUS_COLUMN_NAME,
                            ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                            entity.getStatus());
    }

    private void populateFeature(SimpleFeature feature, CoalesceEntity entity, CoalesceLinkage linkage)
            throws CoalesceException, IOException
    {
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getKey());

        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY1_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity1Key());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY1_NAME_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity1Name());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY1_SOURCE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity1Source());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY1_VERSION_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity1Version());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY2_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Key());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY2_NAME_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Name());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Source());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_ENTITY2_VERSION_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getEntity2Version());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_LAST_MODIFIED_COLUMN_NAME,
                            ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                            linkage.getLastModified());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_LABEL_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            linkage.getLabel());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.LINKAGE_LINK_TYPE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            linkage.getLinkType());
    }

    private void populateFeature(SimpleFeature feature, CoalesceEntity entity, CoalesceRecord record)
            throws CoalesceException, IOException
    {
        String recordsetName = record.getParent().getName();

        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_KEY_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getKey());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_NAME_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getName());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_SOURCE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getSource());
        setFeatureAttribute(feature,
                            AccumuloRegisterIterator.ENTITY_TITLE_COLUMN_NAME,
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getTitle());
        setFeatureAttribute(feature,
                            normalizer.normalize(recordsetName, "recordkey"),
                            ECoalesceFieldDataTypes.STRING_TYPE,
                            record.getKey());

        for (CoalesceField<?> field : record.getFields())
        {
            String fieldName = normalizer.normalize(recordsetName, field.getName());
            ECoalesceFieldDataTypes dataType = field.getDataType();
            Object fieldValue = field.getValue();

            // If there is not a value do not set the attribute.
            if (fieldValue != null)
            {
                LOGGER.trace("Setting FeatureAttribute {}, is type {}", fieldName, dataType);
                setFeatureAttribute(feature, fieldName, dataType, fieldValue);
            }
        }
    }

    private Polygon createWorldPolygon()
    {
        // create a polygon of the WORLD!!!!!
        Coordinate coord1 = new Coordinate(-180, -90);
        Coordinate coord2 = new Coordinate(-180, 90);
        Coordinate coord3 = new Coordinate(180, 90);
        Coordinate coord4 = new Coordinate(180, -90);
        Coordinate coord5 = new Coordinate(-180, -90);

        GeometryFactory geoFactory = new GeometryFactory();

        CoordinateSequence coordSeq = new CoordinateArraySequence(new Coordinate[] { coord1, coord2, coord3, coord4, coord5
        });

        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(coordSeq), geoFactory);

        return new Polygon(linearRing, null, geoFactory);
    }

    private boolean setFeatureAttribute(SimpleFeature feature,
                                        String fieldName,
                                        ECoalesceFieldDataTypes dataType,
                                        Object fieldValue) throws CoalesceDataFormatException
    {
        String liststring;

        boolean isGeoField = false;

        try
        {
            switch (dataType)
            {

            // These types should be able to be handled directly
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
                liststring = Arrays.toString((String[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case DOUBLE_LIST_TYPE:
                liststring = Arrays.toString((double[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case INTEGER_LIST_TYPE:
                liststring = Arrays.toString((int[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case LONG_LIST_TYPE:
                liststring = Arrays.toString((long[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case FLOAT_LIST_TYPE:
                liststring = Arrays.toString((float[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case GUID_LIST_TYPE:
                liststring = Arrays.toString((UUID[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;
            case BOOLEAN_LIST_TYPE:

                liststring = Arrays.toString((boolean[]) fieldValue);
                feature.setAttribute(fieldName, liststring);
                break;

            case GUID_TYPE:
                String guid = ((UUID) fieldValue).toString();
                feature.setAttribute(fieldName, guid);
                break;

            case ENUMERATION_TYPE:
                String enumname = fieldValue.toString();
                feature.setAttribute(fieldName, enumname);
                break;

            case GEOCOORDINATE_LIST_TYPE:
                MultiPoint points = new GeometryFactory().createMultiPoint((Coordinate[]) fieldValue);
                feature.setAttribute(fieldName, points);
                isGeoField = true;
                break;

            case GEOCOORDINATE_TYPE:
                Point point = new GeometryFactory().createPoint((Coordinate) fieldValue);
                feature.setAttribute(fieldName, point);
                isGeoField = true;
                break;

            case LINE_STRING_TYPE:
                feature.setAttribute(fieldName, fieldValue);
                isGeoField = true;
                break;

            case POLYGON_TYPE:
                feature.setAttribute(fieldName, fieldValue);
                isGeoField = true;
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
                isGeoField = true;
                break;

            case DATE_TIME_TYPE:
                feature.setAttribute(fieldName, ((DateTime) fieldValue).toDate());
                break;
            case FILE_TYPE:
            case BINARY_TYPE:
            default:
                break;
            }
        }
        catch (IllegalAttributeException e)
        {

            LOGGER.warn("{} => {}", feature.getName(), String.format(CoalesceErrors.INVALID_INPUT_REASON, fieldName, e.getMessage()));
            isGeoField = false;
        }

        return isGeoField;
        /*
         *
         * // accumulate this new feature in the collection featureCollection.add(simpleFeature); }
         *
         * return featureCollection; }
         *
         * static void insertFeatures(String simpleFeatureTypeName, DataStore dataStore, FeatureCollection featureCollection)
         * throws IOException {
         *
         * FeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(simpleFeatureTypeName);
         * featureStore.addFeatures(featureCollection); }
         */
    }

    public class FeatureCollections {

        public DefaultFeatureCollection featureToAdd = new DefaultFeatureCollection();
        public List<FeatureId> keysToDelete = new ArrayList<>();

    }

}
