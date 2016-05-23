package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
// import org.apache.accumulo.core.client.Durability; // Accumulo 1.7 depenency
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

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
* @author Jing Yang
* May 13, 2016
*/

/**
* 
*/

public class AccumuloPersistor extends CoalescePersistorBase {

    private ServerConn _serCon;
    private boolean useNamepath=false;
    private AccumuloSettings settings;
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
    private static String coalesceTable = "Coalesce";

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
        //settings = new AccumuloSettings();
        /* Set URL, User, Pass */
    }


    @Override
    public void initialize(ServerConn svConn)
    {
        _serCon = svConn;
        settings = (AccumuloSettings) svConn;
        useNamepath = settings.isUseNamePath();
    }

    @Override
    public boolean initialize(ICoalesceCacher cacher, ServerConn svConn)
    {
        _serCon = svConn;

        return super.initialize(cacher);
    }


    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBinaryArray(String binaryFieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean persistEntityTemplate(CoalesceEntityTemplate entityTemplate, CoalesceDataConnectorBase conn)
            throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getEntityTemplateXml(String key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateMetadata() throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        System.err.println("AccumloPersistor.flattenObject");
    	boolean isSuccessful = true;

        //AccumuloDataConnector conn = new AccumuloDataConnector(_serCon);
        AccumuloDataConnector conn = new AccumuloDataConnector((AccumuloSettings) _serCon);
        ;

        // Create a Database Connection
        try
        {
            conn.openConnection(false);

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
        return new AccumuloDataConnector((AccumuloSettings) getConnectionSettings());
    }

    protected boolean persistEntityObject(CoalesceEntity entity, AccumuloDataConnector conn) throws SQLException
    {
        System.err.println("AccumloPersistor.persistEntityObject");
        Connector dbConnector = null;
        try {
        	dbConnector = AccumuloDataConnector.getDBConnector();
        } catch (CoalescePersistorException ex) {
			System.err.println(ex.getLocalizedMessage());
			return false;
        }
        // Return true if no update is required.
        //if (!this.checkLastModified(entity, conn)) return true;
        //map.put(new QName("source"), _entity.getSource());
        //map.put(new QName("version"), _entity.getVersion());
        //map.put(new QName("entityid"), _entity.getEntityid());
        //map.put(new QName("entityidtype"), _entity.getEntityidtype());
       // map.put(new QName("title"), _entity.getTitle());
    	
   	
    	
    	try {
	
			System.err.println("creating BatchWriter");
			BatchWriterConfig config = new BatchWriterConfig();
			config.setMaxLatency(1, TimeUnit.SECONDS);
			config.setMaxMemory(10240);
			//config.setDurability(Durability.DEFAULT);  // Requires Accumulo 1.7
			config.setMaxWriteThreads(10);
			
			BatchWriter writer = dbConnector.createBatchWriter(coalesceTable, config);
			System.err.println("created BatchWriter");
			
			// Hack to make sure we have a valid key at all times
			if (entity.getEntityId() == "") {
				entity.setEntityId("GUID", entity.getKey());
			}
			//Mutation m = new Mutation(entity.getEntityId());
			//storeEntityMetadata(entity,m);
			
			//boolean useNamePath = (AccumuloSettings) _serCon).i.useNamePath
			Mutation m = createMutation(entity,useNamepath);
			
			storeLinkageSection(entity,m);
			writer.addMutation(m);
			
			writer.close();
			System.err.println("Successfully wrote EntityMetaData");
			
			//TODO Add code to store sections
	
		} catch (MutationsRejectedException ex) {
			
			System.err.println(ex.getLocalizedMessage()); // see Error Handling Example
			
		} catch (AccumuloException | TableNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
		}	

    	
        return true;
    }
    
    private Mutation createMutation(CoalesceEntity entity, boolean useNamepath){
    	
        MutationWrapperFactory mfactory = new MutationWrapperFactory();
        MutationWrapper mutationGuy= mfactory.createMutationGuy(entity, useNamepath);
        return mutationGuy.getMutation();
    }
    
    
    
    
    //this method is replaced by createMutation(CoalesceEntity entity)
    private void storeEntityMetadata(CoalesceEntity entity,Mutation m) {
    	
    	System.err.println("storeEntityMetadata");
    	String entityIdType = entity.getEntityIdType();
    	String entityName = entity.getName();
    	String entityVersion = entity.getVersion();
    	String entityClassName = entity.getClassName();
    	String entityTitle = entity.getTitle();
    	String entityType = entity.getType();
    	String entitySource = entity.getSource();
    	String entityXML = entity.toXml();
    	DateTime entityDateCreated = entity.getDateCreated();
    	DateTime entityDateModified = entity.getLastModified();
		m.put(
				entityColumnFamily, 
				entityIdTypeColumnQualifier, 
				new Value(entityIdType.getBytes()));

		m.put(
				entityColumnFamily, 
				entityTypeColumnQualifier, 
				new Value(entityType.getBytes()));
		
		m.put(
				entityColumnFamily, 
				entityNameColumnQualifier, 
				new Value(entityName.getBytes()));

		m.put(
				entityColumnFamily, 
				entityVersionColumnQualifier, 
				new Value(entityVersion.getBytes()));

		m.put(
				entityColumnFamily, 
				entitySourceColumnQualifier, 
				new Value(entitySource.getBytes()));
		
		m.put(
				entityColumnFamily, 
				entityTitleColumnQualifier, 
				new Value(entityTitle.getBytes()));

		m.put(
				entityColumnFamily, 
				entityLastModifiedColumnQualifier, 
				new Value(entityDateModified.toDateTimeISO().toString().getBytes()));

		m.put(
				entityColumnFamily, 
				entityCreatedColumnQualifier, 
				new Value(entityDateCreated.toDateTimeISO().toString().getBytes()));

		m.put(
				entityColumnFamily, 
				entityXMLColumnQualifier, 
				new Value(entityXML.getBytes()));

		if (entityClassName == null) {
			System.err.println("Null Entity ClassName");
		} else {
			m.put(
					entityColumnFamily, 
					entityClassNameColumnQualifier, 
					new Value(entityClassName.getBytes()));				
		}   	
    }
    
    private void storeLinkageSection(CoalesceEntity entity,Mutation m) {
    	System.err.println("storeLinkages");
    	CoalesceLinkageSection linkage = entity.getLinkageSection();
    	String linkagekey = linkage.getKey();
    	String linkColumnFamily = linkColumnFamilyPrefix+linkagekey;
		m.put(
				linkageColumnFamily, 
				"LinkID:"+linkagekey, 
				new Value());
		
		Map<String,CoalesceLinkage> linkages = linkage.getLinkages();
		for (Map.Entry<String,CoalesceLinkage> link : linkages.entrySet()) {
			m.put(linkColumnFamily, link.getValue().getLinkType().toString(), new Value(link.getValue().getEntity2Key().getBytes()) );
		}
    	
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

    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
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
            throws SQLException
    {
        DateTime lastModified = JodaDateTimeHelper.nowInUtc();

        // Determine the Table Name
        // String tableName =
        // CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("?", new CoalesceParameter(key.trim()));
        ResultSetMetaData resultsmd = results.getMetaData();

        // JODA Function DateTimeFormat will adjust for the Server timezone when
        // converting the time.
        if (resultsmd.getColumnCount() <= 1)
        {
            while (results.next())
            {
                dateValue = results.getString("LastModified");
                if (dateValue != null)
                {
                    lastModified = JodaDateTimeHelper.getPostGresDateTim(dateValue);
                }
            }
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
        //try (CoalesceDataConnectorBase conn = new AccumuloDataConnector(_serCon))
        try (CoalesceDataConnectorBase conn = new AccumuloDataConnector((AccumuloSettings) _serCon))
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
