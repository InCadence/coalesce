package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.accumulo.core.data.Mutation;

import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationWrapper;
import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationRow;
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
* @author Matt Defazio
* May 13, 2016
*/
public class MutationWrapperFactory extends CoalesceIterator {

    private static String entityColumnFamily = "Coalesce:MetaData";
    private static String linkageColumnFamily = "Coalesce:Linkage";
    private static String linkColumnFamilyPrefix = "LinkID:";
    private static String sectionColumnFamilyPrefix = "SectionID:";
    private static String recordsetColumnFamilyPrefix = "RecordSetID:";
    private static String fielddefinitionColumnFamilyPrefix = "FieldDefinitionID:";
    private static String recordsetColumnFamily = "Coalesce:RecordSet";
    private static String fieldNameColumnQualifierPrefix = "Coalesce:FieldName";
    private static String fieldTypeColumnQualifierPrefix = "Coalesce:FieldType";
    //private static String fieldDefaultColumnQualifierPrefix = "Coalesce:FieldDefault";
    private static String recordColumnFamilyPrefix = "RecordID:";//
    private static String recordNameColumnFamilyPrefix = "RecordName:";//
    private static String entityTypeColumnQualifier = "Coalesce:EntityType";
    private static String entityNameColumnQualifier = "Coalesce:EntityName";
    private static String sectionNameColumnQualifier = "Coalesce:SectionName";
    private static String entityVersionColumnQualifier = "Coalesce:EntityVersion";
    private static String entityIdColumnQualifier = "Coalesce:EntityId";
    private static String entityIdTypeColumnQualifier = "Coalesce:EntityIdType";
    private static String entityTitleColumnQualifier = "Coalesce:EntityTitle";
    private static String entitySourceColumnQualifier = "Coalesce:EntitySource";
    private static String entityClassNameColumnQualifier = "Coalesce:EntityClassName";
    private static String entityXMLColumnQualifier = "Coalesce:EntityXML";
    private static String entityLastModifiedColumnQualifier = "Coalesce:EntityLastModified";
    private static String entityCreatedColumnQualifier = "Coalesce:EntityCreated";
    private static String linkTypeColumnQualifier = "Coalesce:";

    private MutationWrapper MutationGuy;
    
    public boolean useNamePath=true;

    public MutationWrapper createMutationGuy(CoalesceEntity entity)
    {

        Mutation m = new Mutation(entity.getKey());

        MutationGuy = new MutationWrapper(m);

        processAllElements(entity);

        // add the entity xml
 //       MutationRow row = new MutationRow(entityColumnFamily, entityXMLColumnQualifier, entity.toXml().getBytes(), entity.getNamePath());
 //       MutationGuy.addRow(row);
        
        return MutationGuy;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {
    	// If the entity is marked not to be flattened do not persist it or any children
    	if (!entity.getFlatten())
    		return false;
    	 if(useNamePath){
             addRow(entity);
             String entity_xml = entity.toXml();
             // add the entity xml
             MutationRow row = new MutationRow(entity.getType() + ":" + entity.getNamePath(), 
            		 "entityxml", entity_xml.getBytes(), entity.getNamePath());
             MutationGuy.addRow(row);
            
             
         } else {
	        Map<QName, String> attributes = getAttributes(entity);
	        // add the entity xml
	        MutationRow row = new MutationRow(entityColumnFamily, entityXMLColumnQualifier, entity.toXml().getBytes(), entity.getNamePath());
	        MutationGuy.addRow(row);
	
	        for (Entry<QName, String> set : attributes.entrySet())
	        {
	
	            String attrName = set.getKey().toString();
	            String value = set.getValue();
	
	            if (value == null)
	            {
	                value = "NULL";
	            }
	
	            String columnQualifier = "";
	
	            switch (attrName) {
	            case "entitytype":
	                columnQualifier = entityTypeColumnQualifier;
	                break;
	            case "entityidtype":
	                columnQualifier = entityIdTypeColumnQualifier;
	                break;
	            case "name":
	                columnQualifier = entityNameColumnQualifier;                
	                break;
	            case "version":
	                columnQualifier = entityVersionColumnQualifier;
	                break;
	            case "entityid":
	                columnQualifier = entityIdColumnQualifier;
	                break;
	            case "title":
	                columnQualifier = entityTitleColumnQualifier;
	                break;
	            case "source":
	                columnQualifier = entitySourceColumnQualifier;
	                break;    
	            case "classname":
	                columnQualifier = entityClassNameColumnQualifier;
	                break;     
	            case "lastmodified":
	                columnQualifier = entityLastModifiedColumnQualifier;
	                break;     
	            case "datecreated":
	                columnQualifier = entityCreatedColumnQualifier;
	                break;     
	
	            default:
	                // skip this guy
	                continue;
	            }
	
	            row = new MutationRow(entityColumnFamily, columnQualifier, value.getBytes(), entity.getNamePath());
	            MutationGuy.addRow(row);
	
	        }
         }
        // Process Children
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {
    	// If the section is marked not to be flattened then do not persist it or any children
    	if (!section.getFlatten())
    		return false;
    	if (useNamePath) {
    		addRow(section);
    	}
        // skip
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
       	// If the linkage is marked not to be flattened then do not persist it or any children
    	if (!linkage.getFlatten())
    		return false;
        if(useNamePath){
            addRow(linkage);
        } else {
        
        MutationRow row = new MutationRow(linkageColumnFamily, linkColumnFamilyPrefix+ linkage.getKey(), ("NULL").getBytes(), linkage.getNamePath());
        MutationGuy.addRow(row);
        
        MutationRow row2 = new MutationRow(linkColumnFamilyPrefix+ linkage.getKey(), linkTypeColumnQualifier+ linkage.getLinkType(), linkage.getEntity2Key().getBytes(), linkage.getNamePath());
        MutationGuy.addRow(row2);
        }
        
        return true;
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section)
    {
       	// If the section is marked not to be flattened then do not persist it or any children
    	if(!section.getFlatten())
    		return false;
        if(useNamePath){
            addRow(section);
        } else {

        MutationRow row = new MutationRow(sectionColumnFamilyPrefix+ section.getKey(), sectionNameColumnQualifier, section.getName().getBytes(), section.getNamePath());
        MutationGuy.addRow(row);
        }
        
        return true;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
       	// If the recordset is marked not to be flattened then do not persist it or any children
    	if (!recordset.getFlatten())
    		return false;
        if(useNamePath){
            addRow(recordset);
        } else {

        MutationRow row = new MutationRow(recordsetColumnFamily, recordsetColumnFamilyPrefix + recordset.getKey(), recordset.getName().getBytes(), recordset.getNamePath());
        MutationGuy.addRow(row);
        MutationRow row2 = new MutationRow(sectionColumnFamilyPrefix + recordset.getParent().getKey(), recordsetColumnFamilyPrefix + recordset.getKey(), ("NULL").getBytes(), recordset.getNamePath());
        MutationGuy.addRow(row2);
        }
        return true;
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
       	// If the record is marked not to be flattened then do not persist it or any children
    	if (!record.getFlatten())
    		return false;
        if(useNamePath){
            addRow(record);
        } else {

        MutationRow row = new MutationRow(recordsetColumnFamilyPrefix+record.getParent().getKey(), recordColumnFamilyPrefix + record.getKey(), ("NULL").getBytes(), record.getNamePath());
        MutationGuy.addRow(row);
        MutationRow row2 = new MutationRow(recordsetColumnFamilyPrefix+record.getParent().getKey(), recordNameColumnFamilyPrefix , record.getName().getBytes(), record.getNamePath());
        MutationGuy.addRow(row2);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
       	// If the field is marked not to be flattened then do not persist it or any children
    	if (!field.getFlatten())
    		return false;
        if(useNamePath){
            addRow(field);
        } else {

        MutationRow row = new MutationRow(recordColumnFamilyPrefix + field.getParent().getKey(), field.getName(), field.getBaseValue().getBytes(), field.getNamePath());
        MutationGuy.addRow(row);
        }

        // Don't visit children
        return false;
    }

    @Override
    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition)
    {
       	// If the definition is marked not to be flattened then do not persist it or any children
    	if (!definition.getFlatten())
    		return false;

        if(useNamePath){
            addRow(definition);
        } else {
            String columnfamilyName = fielddefinitionColumnFamilyPrefix + definition.getKey();

            MutationRow row = new MutationRow(columnfamilyName, fieldNameColumnQualifierPrefix+definition.getName(), definition.getName().getBytes(), definition.getNamePath());
            MutationGuy.addRow(row);

            MutationRow row2 = new MutationRow(columnfamilyName, fieldTypeColumnQualifierPrefix+definition.getType(), definition.getType().getBytes(), definition.getNamePath());
            MutationGuy.addRow(row2);

//            MutationRow row3 = new MutationRow(columnfamilyName, fieldDefaultColumnQualifierPrefix+definition.getDefaultValue(), definition.getDefaultValue().getBytes(), definition.getNamePath());
//            MutationGuy.addRow(row3);
        }
       
        
        return true;
    }

    private void addRow(CoalesceObject object)
    {
        Map<QName, String> attributes = getAttributes(object);
        String type = object.getType() + ":";
        for (Entry<QName, String> set : attributes.entrySet())
        {
            String attrName = set.getKey().toString();
            String value = set.getValue();

            if (value == null)
            {
                value = "NULL";
            }

            MutationRow row = new MutationRow(type + object.getNamePath(), attrName, value.getBytes(), object.getNamePath());
            MutationGuy.addRow(row);

        }
    }

    private Map<QName, String> getAttributes(CoalesceObject object)
    {

        Map<QName, String> attributeMap = null;

        try
        {
            Method method = CoalesceObject.class.getDeclaredMethod("getAttributes", null);
            method.setAccessible(true);
            attributeMap = (Map<QName, String>) method.invoke(object, null);

        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
               | InvocationTargetException e)
        {
            // do nothing
        }

        return attributeMap;
    }
}
