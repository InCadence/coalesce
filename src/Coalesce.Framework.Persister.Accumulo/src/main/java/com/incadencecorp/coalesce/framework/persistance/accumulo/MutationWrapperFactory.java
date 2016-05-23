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

public class MutationWrapperFactory extends CoalesceIterator {

    private static String entityColumnFamily = "Coalesce:MetaData";
    private static String linkageColumnFamily = "Coalesce:Linkage";
    private static String linkColumnFamilyPrefix = "LinkID:";
    private static String sectionColumnFamilyPrefix = "SectionID:";
    private static String recordsetColumnFamilyPrefix = "RecordSetID:";
    private static String fielddefinitionColumnFamilyPrefix = "FieldDefinitionID:";
    private static String fieldNameColumnQualifierPrefix = "Coalesce:FieldName";
    private static String fieldTypeColumnQualifierPrefix = "Coalesce:FieldType";
    private static String fieldDefaultColumnQualifierPrefix = "Coalesce:FieldDefault";
    private static String recordsetColumnQualifierPrefix = "Coalesce:RecordName";
    private static String recordColumnFamilyPrefix = "RecordID:";//
    private static String recordNameColumnFamilyPrefix = "RecordName:";//
    private static String entityIdColumnQualifier = "Coalesce:EntityId";
    private static String entityTypeColumnQualifier = "Coalesce:EntityType";
    private static String entityNameColumnQualifier = "Coalesce:EntityName";
    private static String sectionNameColumnQualifier = "Coalesce:SectionName";
    private static String entityVersionColumnQualifier = "Coalesce:EntityVersion";
    private static String entityIdTypeColumnQualifier = "Coalesce:EntityIdType";
    private static String entityTitleColumnQualifier = "Coalesce:EntityTitle";
    private static String entitySourceColumnQualifier = "Coalesce:EntitySource";
    private static String entityClassNameColumnQualifier = "Coalesce:EntityClassName";
    private static String entityXMLColumnQualifier = "Coalesce:EntityXML";
    private static String entityLastModifiedColumnQualifier = "Coalesce:EntityLastModified";
    private static String entityCreatedColumnQualifier = "Coalesce:EntityCreated";
    private static String linkTypeColumnQualifier = "Coalesce:";

    private MutationWrapper MutationGuy;
    
    private boolean useNamePath=false;

    public MutationWrapper createMutationGuy(CoalesceEntity entity, boolean _useNamepath)
    {
        useNamePath= _useNamepath;
        Mutation m = new Mutation(entity.getEntityId());

        MutationGuy = new MutationWrapper(m);

        processAllElements(entity);

        // add the entity xml
        MutationRow row = new MutationRow(entityColumnFamily, entityXMLColumnQualifier, entity.toXml().getBytes(), entity.getNamePath());
        MutationGuy.addRow(row);
        
        return MutationGuy;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {

        Map<QName, String> attributes = getAttributes(entity);

        for (Entry<QName, String> set : attributes.entrySet())
        {

            String attrName = set.getKey().toString();
            String value = set.getValue();

            if (value == null)
            {
                value = "NULL";
            }

            String columnQualifier = "";
            String namePathColmnFamily = attrName;
            
            switch (attrName) {
            case "entityidtype":
                columnQualifier = entityTypeColumnQualifier;
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
            case "entitytype":
                columnQualifier = entityIdTypeColumnQualifier;    
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
            
            MutationRow row =null;
            if(useNamePath)
            {
                row = new MutationRow(namePathColmnFamily+entityColumnFamily, columnQualifier, value.getBytes(), entity.getNamePath());
            }
            else
            {
                row = new MutationRow(entityColumnFamily, columnQualifier, value.getBytes(), entity.getNamePath());
            }
            MutationGuy.addRow(row);

        }

        // Process Children
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {

        // skip
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
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
        if(useNamePath){
            addRow(recordset);
        } else {
        //RecordSetID:<GUID>, Coalesce:RecordName,Record Name
        MutationRow row = new MutationRow(recordsetColumnFamilyPrefix+recordset.getKey(), recordsetColumnQualifierPrefix + recordset.getKey(), recordset.getName().getBytes(), recordset.getNamePath());
        MutationGuy.addRow(row);
        //RecordSetID:<GUID>,  RecordID:<GUID>, null         
        MutationRow row2 = new MutationRow(sectionColumnFamilyPrefix + recordset.getParent().getKey(), recordsetColumnFamilyPrefix + recordset.getKey(), ("NULL").getBytes(), recordset.getNamePath());
        MutationGuy.addRow(row2);
        }
        return true;
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
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

        if(useNamePath){
            addRow(definition);
        } else {
            String columnfamilyName = fielddefinitionColumnFamilyPrefix + definition.getKey();

            MutationRow row = new MutationRow(columnfamilyName, fieldNameColumnQualifierPrefix+definition.getName(), definition.getName().getBytes(), definition.getNamePath());
            MutationGuy.addRow(row);

            MutationRow row2 = new MutationRow(columnfamilyName, fieldTypeColumnQualifierPrefix+definition.getType(), definition.getType().getBytes(), definition.getNamePath());
            MutationGuy.addRow(row2);

            MutationRow row3 = new MutationRow(columnfamilyName, fieldDefaultColumnQualifierPrefix+definition.getDefaultValue(), ("NULL").getBytes(), definition.getNamePath());
            MutationGuy.addRow(row3);
        }
       
        
        return true;
    }

    private void addRow(CoalesceObject object)
    {
        Map<QName, String> attributes = getAttributes(object);

        for (Entry<QName, String> set : attributes.entrySet())
        {
            String attrName = set.getKey().toString();
            String value = set.getValue();

            if (value == null)
            {
                value = "NULL";
            }

            MutationRow row = new MutationRow(object.getNamePath(), attrName, value.getBytes(), object.getNamePath());
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
