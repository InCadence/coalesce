package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.Map.Entry;
import javax.xml.namespace.QName;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
// import org.apache.accumulo.core.client.Durability; // Accumulo 1.7 depenency
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.apache.accumulo.core.data.Key;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
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
			
			//storeLinkageSection(entity,m);
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

    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException, TableNotFoundException
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
    
    //have not pass the section test
    private DateTime getCoalesceObjectLastModified(String key, String objectType, CoalesceDataConnectorBase conn)
            throws SQLException, TableNotFoundException
    {
        DateTime lastModified = null;
        //DateTime lastModified = JodaDateTimeHelper.nowInUtc();

        String dateValue = null;
        System.err.println("AccumloPersistor.getCoalesceObjectLastModified");
        Connector dbConnector = null;
        try {
            dbConnector = AccumuloDataConnector.getDBConnector();
        } catch (CoalescePersistorException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        }

        //initialize a scanner
        Scanner scanner = dbConnector.createScanner(coalesceTable, Authorizations.EMPTY);
        System.err.println("created Scanner");
        IteratorSetting iter = new IteratorSetting(10, "lastModFltr", RegExFilter.class);

//        String rowRegex =  null;
//        String colfRegex = null;
//        String colqRegex = null;
//        String valueRegex = null;
//        boolean orFields = false;
//        
//        if(objectType =="entity"){
//            rowRegex =  key;
//            colfRegex = objectType+".*";
//            colqRegex = "lastmod.*";
//        }
//        else{
//        }
//        RegExFilter.setRegexs(iter, rowRegex, colfRegex, colqRegex, valueRegex, orFields);
        RegExFilter.setRegexs(iter, key,  objectType+".*", "lastmod.*", null, false); 
        
        scanner.addScanIterator(iter);
        
        for(Entry<Key,Value> entry : scanner) {
            Text row = entry.getKey().getRow();
            Value value = entry.getValue();
            System.err.println("key: " + entry.getKey().toString() + " value: " + entry.getValue());
            dateValue = entry.getValue().toString();
            if (dateValue != null)
            {
                lastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(dateValue);
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
