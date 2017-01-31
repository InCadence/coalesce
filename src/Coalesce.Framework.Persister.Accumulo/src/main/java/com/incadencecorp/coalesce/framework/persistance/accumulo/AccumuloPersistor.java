package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.CachedRowSet;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
// import org.apache.accumulo.core.client.Durability; // Accumulo 1.7 depenency
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.iterators.user.WholeRowIterator;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.joda.time.DateTime;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.Fielddefinition;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.EntityMetaData;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

/*-----------------------------------------------------------------------------'
Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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
/**
 * @author Dave Boyd May 13, 2016
 */

/**
* 
*/

public class AccumuloPersistor extends CoalescePersistorBase implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistor.class);

    private final AccumuloDataConnector connect;
    /*
     * private static String entityColumnFamily = "Coalesce:MetaData"; private
     * static String linkageColumnFamily = "Coalesce:Linkage"; private static
     * String linkColumnFamilyPrefix = "LinkID:"; private static String
     * sectionColumnFamilyPrefix = "SectionID:"; private static String
     * recordsetColumnFamilyPrefix = "RecordSetID:"; private static String
     * fielddefinitionColumnFamilyPrefix = "FieldDefinitionID:"; private static
     * String recordColumnFamilyPrefix = "RecordID:"; private static String
     * entityTypeColumnQualifier = "Coalesce:EntityType"; private static String
     * entityNameColumnQualifier = "Coalesce:EntityName"; private static String
     * entityVersionColumnQualifier = "Coalesce:EntityVersion"; private static
     * String entityIdTypeColumnQualifier = "Coalesce:EntityIdType"; private
     * static String entityTitleColumnQualifier = "Coalesce:EntityTitle";
     * private static String entitySourceColumnQualifier =
     * "Coalesce:EntitySource"; private static String
     * entityClassNameColumnQualifier = "Coalesce:EntityClassName"; private
     * static String entityXMLColumnQualifier = "Coalesce:EntityXML"; private
     * static String entityLastModifiedColumnQualifier =
     * "Coalesce:EntityLastModified"; private static String
     * entityCreatedColumnQualifier = "Coalesce:EntityCreated";
     */

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    public AccumuloPersistor(ServerConn svConn) throws CoalescePersistorException
    {
        setConnectionSettings(svConn);
        connect = (AccumuloDataConnector) getDataConnector();

    }

    public AccumuloPersistor(ICoalesceCacher cacher, ServerConn svConn) throws CoalescePersistorException
    {
        setConnectionSettings(svConn);
        setCacher(cacher);
        connect = (AccumuloDataConnector) getDataConnector();
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
        Connector dbConnector = connect.getDBConnector();
        try (CloseableBatchScanner scanner = new CloseableBatchScanner(dbConnector,
                                                                       AccumuloDataConnector.coalesceTable,
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
            System.err.println(ex.getLocalizedMessage());
        }
        return results != null ? results.toArray(new String[results.size()]) : null;
    }

    @Override
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        // Use are sharded term index to find the merged keys
        String xml = null;
        String indexcf = entityIdType + "\0" + entityId + ".*";
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner scanner = new CloseableScanner(dbConnector,
                                                             AccumuloDataConnector.coalesceEntityIndex,
                                                             Authorizations.EMPTY))
        {
            // Set up an IntersectingIterator for the values
            IteratorSetting iter = new IteratorSetting(1, "cfmatch", RegExFilter.class);
            RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
            scanner.addScanIterator(iter);
            // Just return the first entry
            if (scanner.iterator().hasNext())
            {
                Key rowKey = scanner.iterator().next().getKey();
                String key = rowKey.getRow().toString();
                Text cf = new Text("entity:" + rowKey.getColumnQualifier().toString());
                try (CloseableScanner xmlscanner = new CloseableScanner(dbConnector,
                                                                        AccumuloDataConnector.coalesceTable,
                                                                        Authorizations.EMPTY))
                {
                    xmlscanner.setRange(new Range(key));
                    xmlscanner.fetchColumn(cf, new Text("entityxml"));
                    if (xmlscanner.iterator().hasNext())
                    {
                        xml = xmlscanner.iterator().next().getValue().toString();
                    }
                }
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return xml;
    }

    @Override
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        // Use are EWntityIndex to find the merged keys
        String indexcf = entityIdType + "\0" + entityId + "\0" + name + ".*";
        String xml = null;
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceEntityIndex,
                                                                Authorizations.EMPTY))
        {
            // Set up an IntersectingIterator for the values
            IteratorSetting iter = new IteratorSetting(1, "cfmatch", RegExFilter.class);
            RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
            keyscanner.addScanIterator(iter);
            // Just return the first entry
            if (keyscanner.iterator().hasNext())
            {
                Key rowKey = keyscanner.iterator().next().getKey();
                String key = rowKey.getRow().toString();
                Text cf = new Text("entity:" + rowKey.getColumnQualifier().toString());
                try (CloseableScanner xmlscanner = new CloseableScanner(dbConnector,
                                                                        AccumuloDataConnector.coalesceTable,
                                                                        Authorizations.EMPTY))
                {
                    xmlscanner.setRange(new Range(key));
                    xmlscanner.fetchColumn(cf, new Text("entityxml"));
                    if (xmlscanner.iterator().hasNext())
                    {
                        xml = xmlscanner.iterator().next().getValue().toString();
                    }
                }
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return xml;
    }

    @Override
    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        // TODO We will want to create a a table of just field values by key for
        // now we will scan the main table
        Object value = null;
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceTable,
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
                                                                          AccumuloDataConnector.coalesceTable,
                                                                          Authorizations.EMPTY))
                {
                    valuescanner.setRange(new Range(key));
                    valuescanner.fetchColumn(cf, new Text("value"));
                    if (valuescanner.iterator().hasNext())
                    {
                        value = valuescanner.iterator().next().getValue().toString();
                    }
                }
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return value;

    }

    @Override
    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        Connector dbConnector = connect.getDBConnector();

        String xpath = null;
        String entityKey = null;
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceTable,
                                                                Authorizations.EMPTY))
        {
            // Set up an RegEx Iterator to get the row with a field with the key
            IteratorSetting iter = new IteratorSetting(20, "objectkeymatch", RegExFilter.class);
            // Only get rows for fields that hold key values and match the key
            RegExFilter.setRegexs(iter, null, objectType + ".*", "key", key, false, true);
            keyscanner.addScanIterator(iter);
            // Just return the first entry
            if (keyscanner.iterator().hasNext())
            {
                Key rowKey = keyscanner.iterator().next().getKey();
                // The plus one is to also eat up the colon between the
                // objecttype and the namepath
                xpath = rowKey.getColumnFamily().toString().substring(objectType.length() + 1);
                entityKey = rowKey.getRow().toString();
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return entityKey != null && xpath != null ? new ElementMetaData(entityKey, xpath) : null;
    }

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource)
            throws CoalescePersistorException
    {
        // Use are EntityIndex to find the merged keys
        ArrayList<String> keys = new ArrayList<String>();
        // NOTE - Source can be null meaning find all sources so construct the
        // string carefully

        String indexcf = entityIdType + "\0" + entityId + "\0" + entityName;
        indexcf = indexcf.concat(entitySource != null ? "\0" + entitySource : ".*");
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceEntityIndex,
                                                                Authorizations.EMPTY))
        {
            // Set up an RegEx Iterator to get the row with a field with the key
            IteratorSetting iter = new IteratorSetting(20, "cfmatch", RegExFilter.class);
            // Only get rows for fields that hold key values and match the key
            RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
            keyscanner.addScanIterator(iter);

            // Return the list of keys
            for (Entry<Key, Value> entry : keyscanner)
            {
                keys.add(entry.getKey().getRow().toString());
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return keys;
    }

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        EntityMetaData metadata = null;
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceTable,
                                                                Authorizations.EMPTY))
        {
            // Set up an RegEx Iterator to get the row with a field with the key
            IteratorSetting iter = new IteratorSetting(20, "entitymatch", RegExFilter.class);
            // Only get rows for the entity with columnqualifiers for entityid,
            // entityidType
            RegExFilter.setRegexs(iter, null, "entity:.*", "(entityid)|(entityidtype)", null, false, true);
            keyscanner.addScanIterator(iter);
            keyscanner.setRange(Range.exact(key));

            // Now we should have two entries one for the entityid and one for
            // the entityidtype
            String entityid = null;
            String entityidtype = null;
            for (Entry<Key, Value> entry : keyscanner)
            {
                String cf = entry.getKey().getColumnQualifier().toString();
                String value = entry.getValue().toString();
                if (cf.equals("entityid"))
                {
                    entityid = new String(value);
                }
                if (cf.equals("entityidtype"))
                {
                    entityidtype = new String(value);
                }
            }
            if (entityid != null && entityidtype != null)
            {
                metadata = new EntityMetaData(entityid, entityidtype, key);
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return metadata;
    }

    @Override
    public byte[] getBinaryArray(String binaryFieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    private static String coalesceTemplateColumnFamily = "Coalesce:Template";
    private static String coalesceTemplateNameQualifier = "name";
    private static String coalesceTemplateSourceQualifier = "source";
    private static String coalesceTemplateXMLQualifier = "xml";
    private static String coalesceTemplateVersionQualifier = "version";
    private static String coalesceTemplateDateModifiedQualifier = "lastmodified";
    private static String coalesceTemplateDateCreatedQualifier = "datecreated";

    @Override
    protected void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {
        BatchWriter writer = null;

        for (CoalesceEntityTemplate template : templates)
        {
            try
            {
                Connector dbConnector = connect.getDBConnector();
                BatchWriterConfig config = new BatchWriterConfig();
                config.setMaxLatency(1, TimeUnit.SECONDS);
                config.setMaxMemory(10240);
                // config.setDurability(Durability.DEFAULT); // Requires
                // Accumulo
                // 1.7
                config.setMaxWriteThreads(10);
                writer = dbConnector.createBatchWriter(AccumuloDataConnector.coalesceTemplateTable, config);
                /*
                 * SQL we are eumulating return conn.executeProcedure(
                 * "CoalesceEntityTemplate_InsertOrUpdate", new
                 * CoalesceParameter(UUID.randomUUID().toString(), Types.OTHER),
                 * new CoalesceParameter(template.getName()), new
                 * CoalesceParameter(template.getSource()), new
                 * CoalesceParameter(template.getVersion()), new
                 * CoalesceParameter(template.toXml()), new
                 * CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(),
                 * Types.OTHER), new
                 * CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(),
                 * Types.OTHER));
                 */
                String xml = template.toXml();
                String name = template.getName();
                String source = template.getSource();
                String version = template.getVersion();
                String time = JodaDateTimeHelper.nowInUtc().toString();
                // See if a template with this name, source, version exists
                String templateId = getEntityTemplateKey(name, source, version);
                boolean newtemplate = false;

                if (templateId == null)
                {
                    templateId = UUID.randomUUID().toString();
                    newtemplate = true;
                }
                Mutation m = new Mutation(templateId);
                m.put(coalesceTemplateColumnFamily, coalesceTemplateXMLQualifier, new Value(xml.getBytes()));
                m.put(coalesceTemplateColumnFamily, coalesceTemplateDateModifiedQualifier, new Value(time.getBytes()));
                // Only update the name, source, version, created date if new.
                if (newtemplate)
                {
                    m.put(coalesceTemplateColumnFamily, coalesceTemplateNameQualifier, new Value(name.getBytes()));
                    m.put(coalesceTemplateColumnFamily, coalesceTemplateSourceQualifier, new Value(source.getBytes()));
                    m.put(coalesceTemplateColumnFamily, coalesceTemplateVersionQualifier, new Value(version.getBytes()));
                    m.put(coalesceTemplateColumnFamily, coalesceTemplateDateCreatedQualifier, new Value(time.getBytes()));
                    // Special Column Qualifier to so we can fetch key based on
                    // Name
                    // + Source + Version
                    m.put(coalesceTemplateColumnFamily,
                          template.getName() + template.getSource() + template.getVersion(),
                          new Value(templateId.getBytes()));
                }
                writer.addMutation(m);
                writer.flush();
                writer.close();
                // Create the associated search features for this template if it
                // is
                // new
                // TODO: Figure out what to do for updates to templates. What to
                // do
                // with the data?
                if (newtemplate)
                {
                    createSearchTables(template);
                }
            }
            catch (CoalescePersistorException | MutationsRejectedException | TableNotFoundException ex)
            {
                System.err.println(ex.getLocalizedMessage());
            }
        }
        if (writer != null)
        { // make sure writer closed even if exception happened before
            try
            {
                writer.close();
            }
            catch (MutationsRejectedException e)
            {
            }
        }
    }

    private boolean createSearchTables(CoalesceEntityTemplate template)
    {
        // Feature set name will be built as follows to avoid namespace
        // collisions
        // <template>: <name>_<source>_<version>
        // <featurename>: <template>.<section>.<recordset>
        //
        // All spaces in names will be converted to underscores.
        String templateName = (template.getName() + "_" + template.getSource() + "_"
                + template.getVersion()).replaceAll(" ", "_");

        // Document tempdoc = template.getCoalesceObjectDocument();
        // Confirm Values
        NodeList nodeList = template.getCoalesceObjectDocument().getElementsByTagName("*");
        int numnodes = nodeList.getLength();

        String sectionName = null;
        String recordName = null;
        ArrayList<Fielddefinition> fieldlist = new ArrayList<Fielddefinition>();

        // TODO - Deal with noindex sections and records.
        for (int jj = 0; jj < numnodes; jj++)
        {
            Node node = nodeList.item(jj);
            String nodeName = node.getNodeName();

            if (nodeName.compareTo("section") == 0)
            {
                sectionName = node.getAttributes().getNamedItem("name").getNodeValue().replaceAll(" ", "_");
                recordName = null;
            }

            if (nodeName.compareTo("recordset") == 0)
            {
                // If this is a new record write out the old one if it exists
                if (!fieldlist.isEmpty())
                {
                    // Now create the geomesa featureset
                    createFeatureSet(templateName + "." + sectionName + "." + recordName, fieldlist);
                }
                recordName = node.getAttributes().getNamedItem("name").getNodeValue().replaceAll(" ", "_");
                fieldlist.clear();
            }

            if (nodeName.compareTo("fielddefinition") == 0)
            {
                Node fieldNode = nodeList.item(jj);
                String datatype = fieldNode.getAttributes().getNamedItem("datatype").getNodeValue();
                String fieldname = fieldNode.getAttributes().getNamedItem("name").getNodeValue();
                Fielddefinition field = new Fielddefinition();

                field.setDatatype(datatype);
                field.setName(fieldname.replaceAll(" ", "_"));
                fieldlist.add(field);
            }

        }
        // Write out last set of fields
        if (!fieldlist.isEmpty())
        {
            createFeatureSet(templateName + "." + sectionName + "." + recordName, fieldlist);
        }
        return true;
    }

    private void createFeatureSet(String featurename, ArrayList<Fielddefinition> fields)
    {
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        boolean defaultGeometrySet = false;
        tb.setName(featurename);
        
        // TODO - Deal with no index fields
        tb.add("entityKey", getTypeForSimpleFeature(ECoalesceFieldDataTypes.STRING_TYPE));
        for (Fielddefinition field : fields)
        {
            String datatype = field.getDatatype();
            ECoalesceFieldDataTypes type = ECoalesceFieldDataTypes.getTypeForCoalesceType(datatype);
            Class<?> featuretype = getTypeForSimpleFeature(type);

            // Binary and File fields will return null so we do not persist them
            // for search
            if (featuretype != null)
            {
                tb.add(field.getName(), featuretype);
                // Index on the first geometry type in the recordset.
                if (!defaultGeometrySet && Geometry.class.isAssignableFrom(featuretype))
                {
                    defaultGeometrySet = true;
                    tb.setDefaultGeometry(field.getName());
                }
            }
        }
        SimpleFeatureType feature = tb.buildFeatureType();
        try
        {
            connect.getGeoDataStore().createSchema(feature);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            String msg = e.getMessage();
            e.printStackTrace();
        }
    }

    private static Class<?> getTypeForSimpleFeature(final ECoalesceFieldDataTypes type)
    {

        switch (type) {

        case BOOLEAN_TYPE:
            return Boolean.class;

        case DOUBLE_TYPE:
        case FLOAT_TYPE:
            return Double.class;

        case GEOCOORDINATE_LIST_TYPE:
            return MultiPoint.class;

        case GEOCOORDINATE_TYPE:
            return Point.class;

        case LINE_STRING_TYPE:
            return LineString.class;

        case POLYGON_TYPE:
            return Polygon.class;
        // Circles will be converted to polygons
        case CIRCLE_TYPE:
            return Polygon.class;

        case INTEGER_TYPE:
            return Integer.class;

        case STRING_TYPE:
        case URI_TYPE:
        case STRING_LIST_TYPE:
        case DOUBLE_LIST_TYPE:
        case INTEGER_LIST_TYPE:
        case LONG_LIST_TYPE:
        case FLOAT_LIST_TYPE:
        case GUID_LIST_TYPE:
        case BOOLEAN_LIST_TYPE:
            return String.class;

        case GUID_TYPE:
            return String.class;

        case DATE_TIME_TYPE:
            return Date.class;

        case LONG_TYPE:
            return Long.class;

        case FILE_TYPE:
        case BINARY_TYPE:
        default:
            return null;
        }
    }

    @Override
    public String getEntityTemplateXml(String key) throws CoalescePersistorException
    {
        Range range = new Range(key);
        String xml = null;
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceTemplateTable,
                                                                Authorizations.EMPTY))
        {
            keyscanner.setRange(range);
            keyscanner.fetchColumn(new Text(coalesceTemplateColumnFamily), new Text(coalesceTemplateXMLQualifier));

            // TODO Add error handling if more than one row returned.
            if (keyscanner.iterator().hasNext())
            {
                xml = keyscanner.iterator().next().getValue().toString();
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());

        }
        return xml;
    }

    @Override
    public String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        // TODO This can be optimized to not search twice
        return getEntityTemplateXml(getEntityTemplateKey(name, source, version));
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        String key = null;
        Connector dbConnector = connect.getDBConnector();
        try (CloseableScanner keyscanner = new CloseableScanner(dbConnector,
                                                                AccumuloDataConnector.coalesceTemplateTable,
                                                                Authorizations.EMPTY))
        {
            // Uses special columnqualifier that is a concat of
            // name+source+version
            keyscanner.fetchColumn(new Text(coalesceTemplateColumnFamily), new Text(name + source + version));
            // TODO Add error handling if more than one row returned.
            if (keyscanner.iterator().hasNext())
            {
                key = keyscanner.iterator().next().getValue().toString();
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return key;
    }

    // Utility method to strip the row key, visibility, and timestamp from the
    // SortedMap returned from decodeRow
    private static SortedMap<String, Value> columnMap(SortedMap<Key, Value> row)
    {
        TreeMap<String, Value> colMap = new TreeMap<>();
        for (Map.Entry<Key, Value> e : row.entrySet())
        {
            String cf = e.getKey().getColumnFamily().toString();
            String cq = e.getKey().getColumnQualifier().toString();
            colMap.put(cf + ":" + cq, e.getValue());
        }
        return colMap;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> results = new ArrayList<ObjectMetaData>();

        Connector dbConnector = connect.getDBConnector();

        try (CloseableScanner scanner = new CloseableScanner(dbConnector,
                                                             AccumuloDataConnector.coalesceTemplateTable,
                                                             Authorizations.EMPTY))
        {
            Text templateColumnFamily = new Text(coalesceTemplateColumnFamily);
            scanner.fetchColumn(templateColumnFamily, new Text(coalesceTemplateNameQualifier));
            scanner.fetchColumn(templateColumnFamily, new Text(coalesceTemplateSourceQualifier));
            scanner.fetchColumn(templateColumnFamily, new Text(coalesceTemplateVersionQualifier));
            scanner.fetchColumn(templateColumnFamily, new Text(coalesceTemplateDateCreatedQualifier));
            scanner.fetchColumn(templateColumnFamily, new Text(coalesceTemplateDateModifiedQualifier));
            IteratorSetting iter = new IteratorSetting(1,
                                                       "rowiterator",
                                                       (Class<? extends SortedKeyValueIterator<Key, Value>>) WholeRowIterator.class);
            scanner.addScanIterator(iter);
            // Create Document
            for (Entry<Key, Value> entry : scanner)
            {
                // Create New Template Element
                SortedMap<Key, Value> wholeRow = null;
                wholeRow = WholeRowIterator.decodeRow(entry.getKey(), entry.getValue());

                SortedMap<String, Value> colmap = columnMap(wholeRow);

                String key = entry.getKey().getRow().toString();
                String name = colmap.get(coalesceTemplateColumnFamily + ":" + coalesceTemplateNameQualifier).toString();
                String source = colmap.get(coalesceTemplateColumnFamily + ":" + coalesceTemplateSourceQualifier).toString();
                String version = colmap.get(coalesceTemplateColumnFamily + ":"
                        + coalesceTemplateVersionQualifier).toString();
                DateTime created = JodaDateTimeHelper.fromXmlDateTimeUTC(colmap.get(coalesceTemplateColumnFamily + ":"
                        + coalesceTemplateDateCreatedQualifier).toString());
                DateTime lastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(colmap.get(coalesceTemplateColumnFamily + ":"
                        + coalesceTemplateDateModifiedQualifier).toString());

                results.add(new ObjectMetaData(key, name, source, version, created, lastModified));
            }

        }
        catch (IOException | TableNotFoundException e)
        {
            throw new CoalescePersistorException("Error Getting Template Metadata", e);
        }

        return results;
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = true;
        for (CoalesceEntity entity : entities)
        {
            if ("entity".equalsIgnoreCase(entity.getType()))
            {
                try
                {
                    isSuccessful &= persistEntityObject(entity, connect);
                }
                catch (CoalesceDataFormatException | SQLException | SAXException | IOException e)
                {
                    throw new CoalescePersistorException("FlattenObject", e);
                }
            }
        }
        return isSuccessful;
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new AccumuloDataConnector(getConnectionSettings());
    }

    protected boolean persistEntityObject(CoalesceEntity entity, AccumuloDataConnector conn)
            throws SQLException, CoalescePersistorException, SAXException, IOException, CoalesceDataFormatException
    {
        boolean persisted = false;
        try
        {
            Connector dbConnector = connect.getDBConnector();
            BatchWriterConfig config = new BatchWriterConfig();
            config.setMaxLatency(1, TimeUnit.SECONDS);
            config.setMaxMemory(52428800);
            config.setTimeout(600, TimeUnit.SECONDS);
            // config.setDurability(Durability.DEFAULT); // Requires Accumulo
            // 1.7
            config.setMaxWriteThreads(10);
            persisted = persistBaseData(entity, dbConnector, config) && persistEntityIndex(entity, dbConnector, config);
            persistEntitySearchData(entity, dbConnector, config);
        }
        catch (CoalescePersistorException ex)
        {
            System.err.println(ex.getLocalizedMessage());
            persisted = false;
        }
        return persisted;
    }

    private boolean persistBaseData(CoalesceEntity entity, Connector connect, BatchWriterConfig config)
    {
        boolean persisted = false;
        try (CloseableBatchWriter writer = new CloseableBatchWriter(connect, AccumuloDataConnector.coalesceTable, config))
        {
            writer.addMutation(createMutation(entity));
            writer.close();
            persisted = true;
        }
        catch (MutationsRejectedException | TableNotFoundException e)
        {
            persisted = false;
        }
        return persisted;
    }

    private boolean persistEntityIndex(CoalesceEntity entity, Connector dbConnector, BatchWriterConfig config)
    {
        boolean persisted = false;
        try (CloseableBatchWriter writer = new CloseableBatchWriter(dbConnector,
                                                                    AccumuloDataConnector.coalesceEntityIndex,
                                                                    config))
        {
            Mutation m = new Mutation(entity.getKey());
            Text indexcf = new Text(entity.getEntityIdType() + "\0" + entity.getEntityId() + "\0" + entity.getName() + "\0"
                    + entity.getSource());
            m.put(indexcf, new Text(entity.getNamePath()), new Value(new byte[0]));
            writer.addMutation(m);
            writer.close();
            persisted = true;
        }
        catch (MutationsRejectedException | TableNotFoundException e)
        {
            persisted = false;
        }
        return persisted;
    }

    private boolean persistEntitySearchData(CoalesceEntity entity, Connector dbConnector, BatchWriterConfig config)
    {
        boolean persisted = false;
        CoalesceEntityTemplate template = null;
        CoalesceValidator validator = new CoalesceValidator();
        DataStore geoDataStore = connect.getGeoDataStore();

        String templatename = (entity.getName() + "_" + entity.getSource() + "_" + entity.getVersion()).replaceAll(" ", "_");
        // Find the template for this type
        try
        {
            String xml = getEntityTemplateXml(entity.getName(), entity.getSource(), entity.getVersion());

            if (xml != null)
            {
                template = new CoalesceEntityTemplate();
                template.initialize(xml);

                // Validate the entity against the template
                Map<String, String> errors = validator.validate(entity, template);
                if (errors.isEmpty())
                {
                    for (CoalesceSection section : entity.getSections().values())
                    {
                        String sectionname = section.getName();
                        for (CoalesceRecordset recordset : section.getRecordsets().values())
                        {
                            String recordname = recordset.getName();
                            String featuresetname = (templatename + "." + sectionname + "." + recordname).replaceAll(" ",
                                                                                                                     "_");

                            // Verify a featureset exists if not skip this
                            // record
                            SimpleFeatureType featuretype = geoDataStore.getSchema(featuresetname);
                            if (featuretype == null)
                            {
                                break;
                            }
                            // Do a feature collection for all records
                            DefaultFeatureCollection featurecollection = new DefaultFeatureCollection();
                            // Create a geo record for each record in the
                            // recordset
                            for (CoalesceRecord record : recordset.getRecords())
                            {
                                Object[] NO_VALUES = {};
                                SimpleFeature simplefeature = SimpleFeatureBuilder.build(featuretype, NO_VALUES, null);
                                setFeatureAttribute(simplefeature,
                                                    "entityKey",
                                                    ECoalesceFieldDataTypes.STRING_TYPE,
                                                    entity.getKey());
                                for (CoalesceField<?> field : record.getFields())
                                {
                                    String fieldname = field.getName();
                                    ECoalesceFieldDataTypes fieldtype = field.getDataType();
                                    Object fieldvalue = field.getValue();
                                    // If there is not a value do not set the
                                    // attribute.
                                    if (fieldvalue != null)
                                    {
                                        setFeatureAttribute(simplefeature, fieldname, fieldtype, fieldvalue);
                                    }
                                }
                                Object geomObj = simplefeature.getDefaultGeometry();
                                if (geomObj != null)
                                {
                                    featurecollection.add(simplefeature);
                                }
                            }
                            SimpleFeatureStore featureStore = (SimpleFeatureStore) geoDataStore.getFeatureSource(featuresetname);
                            featureStore.addFeatures(featurecollection);
                            persisted = true;
                        }
                    }
                }
            }
        }
        catch (CoalescePersistorException | SAXException | IOException | CoalesceDataFormatException e)
        {
            persisted = false;
        }
        return persisted;
    }

    private void setFeatureAttribute(SimpleFeature simplefeature,
                                     String fieldname,
                                     ECoalesceFieldDataTypes fieldtype,
                                     Object fieldvalue)
            throws CoalesceDataFormatException
    {
        String objclass = fieldvalue.getClass().getName();
        String liststring;

        switch (fieldtype) {

        // These types should be able to be handled directly
        case BOOLEAN_TYPE:
        case DOUBLE_TYPE:
        case FLOAT_TYPE:
        case INTEGER_TYPE:
        case LONG_TYPE:
        case STRING_TYPE:
        case URI_TYPE:
            simplefeature.setAttribute(fieldname, fieldvalue);
            break;

        case STRING_LIST_TYPE:
            liststring = Arrays.toString((String[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case DOUBLE_LIST_TYPE:
            liststring = Arrays.toString((double[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case INTEGER_LIST_TYPE:
            liststring = Arrays.toString((int[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case LONG_LIST_TYPE:
            liststring = Arrays.toString((long[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case FLOAT_LIST_TYPE:
            liststring = Arrays.toString((float[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case GUID_LIST_TYPE:
            liststring = Arrays.toString((UUID[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;
        case BOOLEAN_LIST_TYPE:

            liststring = Arrays.toString((boolean[]) fieldvalue);
            simplefeature.setAttribute(fieldname, liststring);
            break;

        case GUID_TYPE:
            String guid = ((UUID) fieldvalue).toString();
            simplefeature.setAttribute(fieldname, guid);
            break;

        case GEOCOORDINATE_LIST_TYPE:
            MultiPoint points = new GeometryFactory().createMultiPoint((Coordinate[]) fieldvalue);
            simplefeature.setAttribute(fieldname, points);
            break;

        case GEOCOORDINATE_TYPE:
            Point point = new GeometryFactory().createPoint((Coordinate) fieldvalue);
            simplefeature.setAttribute(fieldname, point);
            break;

        case LINE_STRING_TYPE:
            simplefeature.setAttribute(fieldname, fieldvalue);
            break;

        case POLYGON_TYPE:
            simplefeature.setAttribute(fieldname, fieldvalue);
            break;

        // Circles will be converted to polygons
        case CIRCLE_TYPE:
            // Create Polygon

            CoalesceCircle circle = (CoalesceCircle) fieldvalue;
            GeometricShapeFactory factory = new GeometricShapeFactory();
            factory.setSize(circle.getRadius());
            factory.setNumPoints(360); // 1 degree points
            factory.setCentre(circle.getCenter());
            Polygon shape = factory.createCircle();
            simplefeature.setAttribute(fieldname, shape);
            break;

        case DATE_TIME_TYPE:
            simplefeature.setAttribute(fieldname, ((DateTime) fieldvalue).toDate());
            break;
        case FILE_TYPE:
        case BINARY_TYPE:
        default:
            break;
        }
        /*
         * 
         * // accumulate this new feature in the collection
         * featureCollection.add(simpleFeature); }
         * 
         * return featureCollection; }
         * 
         * static void insertFeatures(String simpleFeatureTypeName, DataStore
         * dataStore, FeatureCollection featureCollection) throws IOException {
         * 
         * FeatureStore featureStore = (SimpleFeatureStore)
         * dataStore.getFeatureSource(simpleFeatureTypeName);
         * featureStore.addFeatures(featureCollection); }
         */
    }

    private Mutation createMutation(CoalesceEntity entity)
    {
        MutationWrapperFactory mfactory = new MutationWrapperFactory();
        MutationWrapper mutationGuy = mfactory.createMutationGuy(entity);
        return mutationGuy.getMutation();
    }

    protected String getValues(CoalesceObject coalesceObject, Map<?, ?> values)
    {
        if (coalesceObject.isActive())
        {
            switch (coalesceObject.getType().toLowerCase()) {
            case "field":
                CoalesceField<?> fieldObject = (CoalesceField<?>) coalesceObject;
                switch (fieldObject.getType().toUpperCase()) {
                case "BINARY":
                case "FILE":
                default: {
                    if (values == null)
                    {

                    }
                }
                    break;
                }
            }
        }
        return null;
    }

    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException, CoalescePersistorException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime lastModified = this.getCoalesceObjectLastModified(coalesceObject.getKey(), coalesceObject.getType(), conn);

        // DB Has Valid Time?
        if (lastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000
            // Ticks / ms)
            long objectTicks = coalesceObject.getLastModified().getMillis();
            long SQLRecordTicks = lastModified.getMillis();

            // TODO: Round Ticks for SQL (Not sure if this is required for .NET)
            // ObjectTicks = this.RoundTicksForSQL(ObjectTicks);

            if (objectTicks == SQLRecordTicks)
            {
                // They're equal; No Update Required
                isOutOfDate = false;
            }
        }

        return isOutOfDate;
    }

    private DateTime getCoalesceObjectLastModified(String key, String objectType, CoalesceDataConnectorBase conn)
            throws SQLException, CoalescePersistorException
    {

        DateTime lastModified = null;
        Connector dbConn = connect.getDBConnector();
        String rowRegex = null;
        String colfRegex = StringUtils.isNotBlank(objectType) ? objectType : null;
        String colqRegex = "key";
        String valueRegex = StringUtils.isNotBlank(key) ? key : null;
        // to use a filter, which is an iterator, you must create an
        // IteratorSetting
        // specifying which iterator class you are using
        IteratorSetting iter = new IteratorSetting(1, "modifiedFilter", RegExFilter.class);
        RegExFilter.setRegexs(iter, rowRegex, colfRegex, colqRegex, valueRegex, false, true);
        try (CloseableScanner scanner = new CloseableScanner(dbConn,
                                                             AccumuloDataConnector.coalesceTable,
                                                             Authorizations.EMPTY))
        {
            scanner.addScanIterator(iter);
            String objectkey = null;
            Text objectcf = null;
            // TODO Add error handling if more than one row returned.
            if (scanner.iterator().hasNext())
            {
                // Get the associated row Id and columnFamily for next search
                Key rowKey = scanner.iterator().next().getKey();
                objectkey = rowKey.getRow().toString();
                objectcf = rowKey.getColumnFamily();
                // Get the lastmodified for that row and columnFamily
                try (CloseableScanner keyscanner = new CloseableScanner(dbConn,
                                                                        AccumuloDataConnector.coalesceTable,
                                                                        Authorizations.EMPTY))
                {
                    keyscanner.setRange(new Range(objectkey));
                    keyscanner.fetchColumn(objectcf, new Text("lastmodified"));
                    // TODO Add error handling if more than one row returned.
                    // Throw CoalescePersistorException
                    if (keyscanner.iterator().hasNext())
                    {
                        String dateString = new String(keyscanner.iterator().next().getValue().get());
                        lastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(dateString);
                    }
                }
            }
        }
        catch (TableNotFoundException ex)
        {
            System.err.println(ex.getLocalizedMessage());
        }
        return lastModified;
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException
    {
        try
        {
            return getCoalesceObjectLastModified(key, objectType, connect);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
    }

    @Override
    public CachedRowSet search(Query query, CoalesceParameter... parameters) throws CoalescePersistorException
    {
        // TODO Not Implemented
        return null;
    }

    @Deprecated
    public List<CoalesceEntity> searchOld(Query query, CoalesceParameter... parameters) throws CoalescePersistorException
    {
        List<CoalesceEntity> coalesceEntities = new ArrayList<>();
        try
        {
            DataStore geoDataStore = ((AccumuloDataConnector) getDataConnector()).getGeoDataStore();
            FeatureSource<?, ?> featureSource = geoDataStore.getFeatureSource(query.getTypeName());
            FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();
            List<String> entityKeys = new ArrayList<>();
            while (featureItr.hasNext())
            {
                Feature feature = featureItr.next();
                entityKeys.add((String) feature.getProperty("entityKey").getValue());
            }
            featureItr.close();
            CoalesceEntity[] entities = getEntity(entityKeys.toArray(new String[entityKeys.size()]));
            coalesceEntities.addAll(Arrays.asList(entities));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return coalesceEntities;
    }

    public void close()
    {
        try
        {
            connect.getGeoDataStore().dispose();
            connect.close();
        }
        catch (CoalescePersistorException e)
        {
            e.printStackTrace();
        }
    }

}
