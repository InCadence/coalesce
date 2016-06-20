package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
// import org.apache.accumulo.core.client.Durability; // Accumulo 1.7 depenency
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.iterators.user.WholeRowIterator;
import org.apache.accumulo.core.iterators.user.IntersectingIterator;
import org.apache.accumulo.core.iterators.user.IndexedDocIterator;
import org.apache.hadoop.io.Text;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationWrapper;

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
* @author Dave Boyd
* May 13, 2016
*/

/**
* 
*/

public class AccumuloPersistor extends CoalescePersistorBase {

    private ServerConn _serCon;
    private static AccumuloDataConnector connect = null;
    /*
    private static String entityColumnFamily = "Coalesce:MetaData";
    private static String linkageColumnFamily = "Coalesce:Linkage";
    private static String linkColumnFamilyPrefix = "LinkID:";
    private static String sectionColumnFamilyPrefix = "SectionID:";
    private static String recordsetColumnFamilyPrefix = "RecordSetID:";
    private static String fielddefinitionColumnFamilyPrefix = "FieldDefinitionID:";
    private static String recordColumnFamilyPrefix = "RecordID:";
    private static String entityTypeColumnQualifier = "Coalesce:EntityType";
    private static String entityNameColumnQualifier = "Coalesce:EntityName";
    private static String entityVersionColumnQualifier = "Coalesce:EntityVersion";
    private static String entityIdTypeColumnQualifier = "Coalesce:EntityIdType";
    private static String entityTitleColumnQualifier = "Coalesce:EntityTitle";
    private static String entitySourceColumnQualifier = "Coalesce:EntitySource";
    private static String entityClassNameColumnQualifier = "Coalesce:EntityClassName";
    private static String entityXMLColumnQualifier = "Coalesce:EntityXML";
    private static String entityLastModifiedColumnQualifier = "Coalesce:EntityLastModified";
    private static String entityCreatedColumnQualifier = "Coalesce:EntityCreated";
    */
 
    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    public AccumuloPersistor()
    {
        /***********
         * Define the PostGresSQL Database Connection in the URL, change to
         * whatever the schema name is on your system
         ***********/
        _serCon = new ServerConn();
        /* Set URL, User, Pass */
    }

    @Override
    public void initialize(ServerConn svConn)
    {
        _serCon = svConn;
        try {
			connect = new AccumuloDataConnector(_serCon);
		} catch (CoalescePersistorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public boolean initialize(ICoalesceCacher cacher, ServerConn svConn)
    {
        _serCon = svConn;
        try {
			connect = new AccumuloDataConnector(_serCon);
		} catch (CoalescePersistorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return super.initialize(cacher);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
    	List<String> results = new ArrayList<String>();
        Connector dbConnector = null;
        ArrayList<Range> ranges = new ArrayList<Range>();
        for (String key : keys) {
        	ranges.add(new Range(key));
        }
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	BatchScanner keyscanner = dbConnector.createBatchScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY,4);
	    	keyscanner.setRanges(ranges);
			IteratorSetting iter = new IteratorSetting(1, "modifiedFilter", RegExFilter.class);
	        RegExFilter.setRegexs(iter, null, "entity:*", "entityxml", null, false, true);
		
	        
	        keyscanner.addScanIterator(iter);
	     
			for(Map.Entry<Key, Value> e : keyscanner) {
				String xml = new String(e.getValue().get());
//				System.err.println(
//						e.getKey().getRow().toString() + " " +
//						e.getKey().getColumnFamily().toString() + " " +
//						e.getKey().getColumnQualifier().toString() );
			    results.add(xml);
			}
			keyscanner.close();
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }
    
        return results.toArray(new String[results.size()]);
    }

    @Override
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        // Use are sharded term index to find the merged keys
        Connector dbConnector = null;
        
        String key = null;
        Text cf = null;
        String indexcf = entityIdType + "\0" +
				entityId + ".*";
        String xml = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceEntityIndex, Authorizations.EMPTY);
	    	
	    	// Set up an IntersectingIterator for the values
			IteratorSetting iter = new IteratorSetting(1, "cfmatch", RegExFilter.class);
			RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
	        keyscanner.addScanIterator(iter);
	        
	        // Just return the first entry
	        if (keyscanner.iterator().hasNext()) {
	        	Key rowKey = keyscanner.iterator().next().getKey();
	        	key = rowKey.getRow().toString();
	        	cf = new Text("entity:" + rowKey.getColumnQualifier().toString());
	        	keyscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			Scanner xmlscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
			xmlscanner.setRange(new Range(key));
			xmlscanner.fetchColumn(cf, new Text("entityxml"));
		
			if (xmlscanner.iterator().hasNext()) {
	        	xml = xmlscanner.iterator().next().getValue().toString();
	        	xmlscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return xml;
    }

    @Override
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        // Use are EWntityIndex to find the merged keys
        Connector dbConnector = null;
        
        String key = null;
        Text cf = null;
        String indexcf = entityIdType + "\0" +
				entityId + "\0" +
				name + ".*";
        String xml = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceEntityIndex, Authorizations.EMPTY);
	    	
	    	// Set up an IntersectingIterator for the values
			IteratorSetting iter = new IteratorSetting(1, "cfmatch", RegExFilter.class);
			RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
	        keyscanner.addScanIterator(iter);
	        
     
	        // Just return the first entry
	        if (keyscanner.iterator().hasNext()) {
	        	Key rowKey = keyscanner.iterator().next().getKey();
	        	key = rowKey.getRow().toString();
	        	cf = new Text("entity:" + rowKey.getColumnQualifier().toString());
	        	keyscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			Scanner xmlscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
			xmlscanner.setRange(new Range(key));
			xmlscanner.fetchColumn(cf, new Text("entityxml"));

			if (xmlscanner.iterator().hasNext()) {
	        	xml = xmlscanner.iterator().next().getValue().toString();
	        	xmlscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return xml;
        
    }

    @Override
    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        // TODO We will want to create a a table of just field values by key for now we will scan the main table
        Connector dbConnector = null;
        Text cf = null;
        String key = null;
        Object value = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
	    	
	    	// Set up an RegEx Iterator to get the row with a field with the key
			IteratorSetting iter = new IteratorSetting(20, "fieldkeymatch", RegExFilter.class);
			// Only get rows for fields that hold key values and match the key
			RegExFilter.setRegexs(iter, null, "field.*", "key", fieldKey, false, true);
	        keyscanner.addScanIterator(iter);
	        
     
	        // Just return the first entry
	        if (keyscanner.iterator().hasNext()) {
	        	Key rowKey = keyscanner.iterator().next().getKey();
	        	key  = rowKey.getRow().toString();
	        	cf = rowKey.getColumnFamily();
	        	keyscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			Scanner valuescanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
			valuescanner.setRange(new Range(key));
			valuescanner.fetchColumn(cf, new Text("value"));

			if (valuescanner.iterator().hasNext()) {
				
	        	value = valuescanner.iterator().next().getValue().toString();
	        	valuescanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
			
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return value;
   	
        
    }

    @Override
    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
         Connector dbConnector = null;
        
        String xpath = null;
        String entityKey = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
	    	
	    	// Set up an RegEx Iterator to get the row with a field with the key
			IteratorSetting iter = new IteratorSetting(20, "objectkeymatch", RegExFilter.class);
			// Only get rows for fields that hold key values and match the key
			RegExFilter.setRegexs(iter, null, objectType+".*", "key", key, false, true);
	        keyscanner.addScanIterator(iter);
	            
	        // Just return the first entry
	        if (keyscanner.iterator().hasNext()) {
	        	Key rowKey = keyscanner.iterator().next().getKey();
	        	
	        	// The plus one is to also eat up the colon between the objecttype and the namepath
	        	xpath = rowKey.getColumnFamily().toString().substring(objectType.length()+1);
	        	entityKey = rowKey.getRow().toString();
	        	keyscanner.close();
	        } else {
	        	//throw new CoalescePersistorException("No rows found for EntityID: "+ entityId +
	        		//	" and EntityIdType: " + entityIdType, null);
	        	return null;
	        }
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
        // TODO Auto-generated method stub
        return new ElementMetaData(entityKey,xpath);
    }

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {
        // Use are EntityIndex to find the merged keys
        Connector dbConnector = null;
        
        ArrayList<String> keys = new ArrayList<String>();
        // NOTE - Source can be null meaning find all sources so construct the string carefully
        
        String indexcf = entityIdType + "\0" +
				entityId + "\0" +
				entityName;
        if (entitySource == null) {
        	indexcf = indexcf + ".*";
        } else {
        	indexcf = indexcf + "\0" + entitySource;
        }
        
        try {

        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceEntityIndex, Authorizations.EMPTY);
	    	
	    	// Set up an RegEx Iterator to get the row with a field with the key
	    	IteratorSetting iter = new IteratorSetting(20, "cfmatch", RegExFilter.class);
	    	// Only get rows for fields that hold key values and match the key
	    	RegExFilter.setRegexs(iter, null, indexcf, null, null, false, true);
	    	keyscanner.addScanIterator(iter);
	        
	               
	        // Return the list of keys
	        for(Entry<Key,Value> entry : keyscanner) {
	        	keys.add(entry.getKey().getRow().toString());
	        }
	        keyscanner.close();
	        
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return keys;
    }

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
    	String entityid = null;
    	String entityidtype = null;
        try {

        	Connector dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
	    	
	    	// Set up an RegEx Iterator to get the row with a field with the key
	    	IteratorSetting iter = new IteratorSetting(20, "entitymatch", RegExFilter.class);
	    	// Only get rows for the entity with columnqualifiers for entityid, entityidType
	    	RegExFilter.setRegexs(iter, null, "entity:.*", "(entityid)|(entityidtype)", null, false, true);
	    	keyscanner.addScanIterator(iter);
	        keyscanner.setRange(Range.exact(key));
	               
	        // Now we should have two entries one for the entityid and one for the entityidtype
	        for(Entry<Key,Value> entry : keyscanner) {
	        	String cf = entry.getKey().getColumnQualifier().toString();
	        	String value = entry.getValue().toString();
	        	if (cf.equals("entityid")) 
	        		entityid = new String(value);
	        	
	        	if (cf.equals("entityidtype")) 
	        		entityidtype = new String(value);
	        }
	        keyscanner.close();
	        
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
        // TODO Auto-generated method stub
        return new EntityMetaData(entityid,entityidtype,key);
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
    public boolean persistEntityTemplate(CoalesceEntityTemplate template, CoalesceDataConnectorBase conn)
            throws CoalescePersistorException
    {
        Connector dbConnector = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();
        } catch (CoalescePersistorException ex) {
			System.err.println(ex.getLocalizedMessage());
			return false;
        }
	
   	
    	
    	try {
	
			BatchWriterConfig config = new BatchWriterConfig();
			config.setMaxLatency(1, TimeUnit.SECONDS);
			config.setMaxMemory(10240);
			//config.setDurability(Durability.DEFAULT);  // Requires Accumulo 1.7
			config.setMaxWriteThreads(10);
			
			BatchWriter writer = dbConnector.createBatchWriter(AccumuloDataConnector.coalesceTemplateTable, config);
	/* SQL we are eumulating
			return conn.executeProcedure("CoalesceEntityTemplate_InsertOrUpdate",
                    new CoalesceParameter(UUID.randomUUID().toString(), Types.OTHER),
                    new CoalesceParameter(template.getName()),
                    new CoalesceParameter(template.getSource()),
                    new CoalesceParameter(template.getVersion()),
                    new CoalesceParameter(template.toXml()),
                    new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.OTHER),
                    new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.OTHER));
*/
			String xml = template.toXml();
			String name = template.getName();
			String source = template.getSource();
			String version = template.getVersion();
			String time = JodaDateTimeHelper.nowInUtc().toString();

			// See if a template with this name, source, version exists
			String templateId = getEntityTemplateKey(name,source,version);
			boolean newtemplate = false;
			
			if (templateId == null) {
				templateId = UUID.randomUUID().toString();
				newtemplate = true;
			}

			Mutation m = new Mutation(templateId);
			m.put(coalesceTemplateColumnFamily, coalesceTemplateXMLQualifier, 
					new Value(xml.getBytes()));
			m.put(coalesceTemplateColumnFamily, coalesceTemplateDateModifiedQualifier, 
					new Value(time.getBytes()));
			// Only update the name, source, version, created date if new.
			if (newtemplate) {
				m.put(coalesceTemplateColumnFamily, coalesceTemplateNameQualifier, 
						new Value(name.getBytes()));
				m.put(coalesceTemplateColumnFamily, coalesceTemplateSourceQualifier, 
						new Value(source.getBytes()));
				m.put(coalesceTemplateColumnFamily, coalesceTemplateVersionQualifier, 
						new Value(version.getBytes()));
				m.put(coalesceTemplateColumnFamily, coalesceTemplateDateCreatedQualifier, 
					new Value(time.getBytes()));
				// Special Column Qualifier to so we can fetch key based on Name + Source + Version
				m.put(coalesceTemplateColumnFamily, template.getName()+template.getSource()+template.getVersion(), 
					new Value(templateId.getBytes()));
			}	
			writer.addMutation(m);
			
			writer.close();
			
		} catch (MutationsRejectedException ex) {
			
			System.err.println(ex.getLocalizedMessage()); // see Error Handling Example
			
		} catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
		}	

    	
        return true;
        
    }

    @Override
    public String getEntityTemplateXml(String key) throws CoalescePersistorException
    {
        Connector dbConnector = null;
        Range range = new Range(key);
        String xml = null;
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTemplateTable, Authorizations.EMPTY);
	    	keyscanner.setRange(range);
			keyscanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
									new Text(coalesceTemplateXMLQualifier));
			
			// TODO Add error handling if more than one row returned.
			if (keyscanner.iterator().hasNext())
				xml = keyscanner.iterator().next().getValue().toString();  // should only be one entry
			keyscanner.close();
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return xml;
    }

    @Override
    public String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        //TODO This can be optimized to not search twice
        return getEntityTemplateXml(getEntityTemplateKey(name,source,version));
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        Connector dbConnector = null;

        String key = null;
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	Scanner keyscanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTemplateTable, Authorizations.EMPTY);
	    	// Uses special columnqualifier that is a concat of name+source+version
	    	keyscanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
									new Text(name+source+version));
			
			// TODO Add error handling if more than one row returned.
			if (keyscanner.iterator().hasNext() )
				key = keyscanner.iterator().next().getValue().toString();  // should only be one entry
			keyscanner.close();
        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }		
			
        return key;
    }

    @Override
    public String getEntityTemplateMetadata() throws CoalescePersistorException
    {
        // Execute Query
        Connector dbConnector = null;
        Scanner scanner = null;
        

        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();

	    	scanner = dbConnector.createScanner(AccumuloDataConnector.coalesceTemplateTable, Authorizations.EMPTY);
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
									new Text(coalesceTemplateNameQualifier));
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
					new Text(coalesceTemplateSourceQualifier));
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
					new Text(coalesceTemplateSourceQualifier));
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
					new Text(coalesceTemplateVersionQualifier));
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
					new Text(coalesceTemplateDateCreatedQualifier));
			scanner.fetchColumn(new Text(coalesceTemplateColumnFamily),
					new Text(coalesceTemplateDateModifiedQualifier));
	        IteratorSetting iter = new IteratorSetting(1, "rowiterator", 
	        		(Class<? extends SortedKeyValueIterator<Key, Value>>) WholeRowIterator.class);

			scanner.addScanIterator(iter);

        } catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
			return null;       	
        }				

        // Create Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();

	        Document doc = builder.newDocument();
	
	        // Create Root Node
	        Element rootElement = doc.createElement("coalescetemplates");
	        doc.appendChild(rootElement);
	
	        for (Entry<Key, Value> entry : scanner)
	        {
	            // Create New Template Element
	            Element templateElement = doc.createElement("coalescetemplate");
	            SortedMap<Key, Value> wholeRow = null;

				wholeRow = WholeRowIterator.decodeRow(entry.getKey(), entry.getValue());

	            
	            // Set Attributes
	            templateElement.setAttribute("templatekey", entry.getKey().toString());
	            
	            templateElement.setAttribute("name", 
	            		wholeRow.get("coalesceTemplateNameQualifier").toString());          		
	            templateElement.setAttribute("source", 
	            		wholeRow.get("coalesceTemplateSourceQualifier").toString());
	            templateElement.setAttribute("version", 
	            		wholeRow.get("coalesceTemplateVersionQualifier").toString());
	            templateElement.setAttribute("lastmodified", 
	            		wholeRow.get("coalesceTemplateDateModifiedQualifier").toString());
	            templateElement.setAttribute("datecreated", 
	            		wholeRow.get("coalesceTemplateDateCreatedQualifier").toString());
	
	            // Append Element
	            rootElement.appendChild(templateElement);
	        }
			scanner.close();

	        // Serialize to String
	        return XmlHelper.formatXml(doc);        
	    
		} catch (IOException | ParserConfigurationException e) {
			
			throw new CoalescePersistorException("Error Getting Template Metadata", e);
		}

    }
    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
    	boolean isSuccessful = true;

        AccumuloDataConnector conn = new AccumuloDataConnector(_serCon);
        ;

        // Create a Database Connection
        try
        {
            
            for (CoalesceEntity entity : entities)
            {
                //conn.executeCmd("CREATE CONSTRAINT ON (item:" + entity.getName() + ") ASSERT item.EntityKey IS UNIQUE");

                // Persist Entity Last to Include Changes
                switch (entity.getType().toLowerCase()) {
                case "entity":
                    isSuccessful &= persistEntityObject(entity, conn);
                }
            }
            conn.commit();
        }
        catch (Exception e)
        {
            conn.rollback();

            throw new CoalescePersistorException("FlattenObject", e);
        }
        finally
        {
            conn.close();
        }

        return isSuccessful;
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new AccumuloDataConnector(getConnectionSettings());
    }

    protected boolean persistEntityObject(CoalesceEntity entity, AccumuloDataConnector conn) throws SQLException
    {
        Connector dbConnector = null;
        
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();
        } catch (CoalescePersistorException ex) {
			System.err.println(ex.getLocalizedMessage());
			return false;
        }
	
   	
    	
    	try {
	
			BatchWriterConfig config = new BatchWriterConfig();
			config.setMaxLatency(1, TimeUnit.SECONDS);
			config.setMaxMemory(10240);
			//config.setDurability(Durability.DEFAULT);  // Requires Accumulo 1.7
			config.setMaxWriteThreads(10);
			
			persistBaseData(entity, dbConnector, config);
			
			persistEntityIndex(entity,dbConnector, config);
			

		} catch (MutationsRejectedException ex) {
			
			System.err.println(ex.getLocalizedMessage()); // see Error Handling Example
			
		} catch (TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
		}	

    	
        return true;
    }
    
    private void persistBaseData(CoalesceEntity entity, Connector dbConnector, BatchWriterConfig config) 
    		throws TableNotFoundException, MutationsRejectedException 
    {
		BatchWriter writer = dbConnector.createBatchWriter(AccumuloDataConnector.coalesceTable, config);
		
		
		
		Mutation m = createMutation(entity);
		
		
		writer.addMutation(m);
		
		writer.close();
	
    }
    
    private void persistEntityIndex(CoalesceEntity entity, Connector dbConnector, BatchWriterConfig config) 
    		throws TableNotFoundException, MutationsRejectedException
    {
		BatchWriter writer = dbConnector.createBatchWriter(AccumuloDataConnector.coalesceTable, config);
		// Now create a  entity index so we can retrieve based on entityId, entityIdType
		// entitySource, entityName
		writer = dbConnector.createBatchWriter(AccumuloDataConnector.coalesceEntityIndex, config);
		Mutation m = new Mutation(entity.getKey());
		Text indexcf = new Text(entity.getEntityIdType() + "\0" +
								entity.getEntityId() + "\0" +
								entity.getName() + "\0" +
								entity.getSource());
		m.put(indexcf, new Text(entity.getNamePath()), new Value(new byte[0]));
		writer.addMutation(m);
		
		
		writer.close();  	
    }
    
    private Mutation createMutation(CoalesceEntity entity){
    	
        MutationWrapperFactory mfactory = new MutationWrapperFactory();
        MutationWrapper mutationGuy= mfactory.createMutationGuy(entity);
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

    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException, CoalescePersistorException
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


        Connector dbConn;
        String rowRegex = null;
        String colfRegex = null;
        String colqRegex = "key";
        String valueRegex = null;
      //to use a filter, which is an iterator, you must create an IteratorSetting
      //specifying which iterator class you are using
        IteratorSetting iter = new IteratorSetting(1, "modifiedFilter", RegExFilter.class);
		if(key != null || key != "") {
			valueRegex = key;
		} 
		if(objectType != null || objectType != "") {
			colfRegex = objectType + ".*";
		}
        RegExFilter.setRegexs(iter, rowRegex, colfRegex, colqRegex, valueRegex, false, true);
        try {
        	dbConn = AccumuloDataConnector.getDBConnector();
        } catch (CoalescePersistorException ex) {
			System.err.println(ex.getLocalizedMessage());
			return lastModified;
        }		

        try {
			Scanner scanner = dbConn.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
		
			scanner.addScanIterator(iter);
			
			String objectkey = null;
			Text objectcf = null;
			// TODO Add error handling if more than one row returned.
			if (scanner.iterator().hasNext() ) {
				// Get the associated row Id and columnFamily for next search
				Key rowKey = scanner.iterator().next().getKey();
				objectkey = rowKey.getRow().toString();
				objectcf = rowKey.getColumnFamily();
			} else {
				scanner.close();
				return null;
			}
			scanner.close();
			
			// Get the lastmodified for that row and columnFamily
	        Scanner keyscanner = dbConn.createScanner(AccumuloDataConnector.coalesceTable, Authorizations.EMPTY);
	        keyscanner.setRange(new Range(objectkey));
	        keyscanner.fetchColumn(objectcf, new Text("lastmodified"));
	        
			// TODO Add error handling if more than one row returned. Throw CoalescePersistorException
			if (keyscanner.iterator().hasNext() ) {
				String dateString = new String(keyscanner.iterator().next().getValue().get());  // should only be one entry			//TODO Change code to just get the first entry returned in the map and print error if more than one returned
				lastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(dateString);
			} else {
				keyscanner.close();
				return null;
			}

			keyscanner.close();
		//	AccumuloException | AccumuloSecurityException 
		} catch (TableNotFoundException ex) {
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
        try (CoalesceDataConnectorBase conn = new AccumuloDataConnector(_serCon))
        {
            return this.getCoalesceObjectLastModified(key, objectType, conn);
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

}
