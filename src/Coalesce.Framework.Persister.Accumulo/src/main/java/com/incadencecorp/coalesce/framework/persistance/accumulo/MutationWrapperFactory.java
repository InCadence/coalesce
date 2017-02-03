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
 * @author Matt Defazio May 13, 2016
 */
public class MutationWrapperFactory extends CoalesceIterator {

    private MutationWrapper MutationGuy;

    public MutationWrapperFactory()
    {
    };

    public MutationWrapper createMutationGuy(CoalesceEntity entity)
    {

        Mutation m = new Mutation(entity.getKey());

        MutationGuy = new MutationWrapper(m);

        processAllElements(entity);

        return MutationGuy;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {
        // If the entity is marked not to be flattened do not persist it or any children
        if (!entity.getFlatten()) return false;

        addRow(entity);
        String entity_xml = entity.toXml();
        // add the entity xml
        MutationRow row = new MutationRow(entity.getType() + ":" + entity.getNamePath(),
                                          "entityxml",
                                          entity_xml.getBytes(),
                                          entity.getNamePath());
        MutationGuy.addRow(row);

        // Process Children
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {
        // If the section is marked not to be flattened then do not persist it or any children
        if (!section.getFlatten()) return false;

        addRow(section);

        // skip
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
        // If the linkage is marked not to be flattened then do not persist it or any children
        if (!linkage.getFlatten()) return false;

        addRow(linkage);

        return true;
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section)
    {
        // If the section is marked not to be flattened then do not persist it or any children
        if (!section.getFlatten()) return false;

        if (AccumuloSettings.getPersistSectionAttr())
        {
            addRow(section);
        }

        return true;

    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
        // If the recordset is marked not to be flattened then do not persist it or any children
        if (!recordset.getFlatten()) return false;

        if (AccumuloSettings.getPersistRecordsetAttr())
        {
            addRow(recordset);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
        // If the record is marked not to be flattened then do not persist it or any children
        if (!record.getFlatten()) return false;

        if (AccumuloSettings.getPersistRecordAttr())
        {
            addRow(record);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
        // If the field is marked not to be flattened then do not persist it or any children
        if (!field.getFlatten()) return false;

        if (AccumuloSettings.getPersistFieldAttr())
        {
            addRow(field);
        }

        // Don't visit children
        return false;
    }

    @Override
    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition)
    {
        // If the definition is marked not to be flattened then do not persist it or any children
        if (!definition.getFlatten()) return false;

        if (AccumuloSettings.getPersistFieldDefAttr())
        {
            addRow(definition);
        }

        return true;
    }

    private void addRow(CoalesceObject object)
    {
        Map<QName, String> attributes = getAttributes(object);
        String type = object.getType() + ":";

        StringBuilder stringBuilder = new StringBuilder();

        for (Entry<QName, String> set : attributes.entrySet())
        {
            String attrName = set.getKey().toString();
            String value = set.getValue();

            if (persistAttr(attrName))
            {

                if (value == null)
                {
                    value = "NULL";
                }

                MutationRow row = new MutationRow(type + object.getNamePath(),
                                                  attrName,
                                                  value.getBytes(),
                                                  object.getNamePath());
                MutationGuy.addRow(row);

            }
            else
            {
                // add to others
                stringBuilder.append(attrName).append("=").append(value).append(",");
            }

        }

        // add a single row for the other attributes
        MutationRow row = new MutationRow(type + object.getNamePath(),
                                          "Other Attributes",
                                          stringBuilder.toString().replaceAll(",$", "").getBytes(),
                                          object.getNamePath());
        MutationGuy.addRow(row);
    }

    private boolean persistAttr(String attrName)
    {

        String[] attrFieldsArray = AccumuloSettings.getAttributeFields().split(",");

        for (String attrField : attrFieldsArray)
        {
            if (attrName.equals(attrField)) return true;
        }
        
        return false;
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
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
               InvocationTargetException e)
        {
            // do nothing
        }

        return attributeMap;
    }
}
