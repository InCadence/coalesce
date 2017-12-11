package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

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
public class MutationWrapperFactory extends CoalesceIterator<MutationWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MutationWrapperFactory.class);

    private int rowCount = 0;

    public MutationWrapper createMutationGuy(CoalesceEntity entity) throws CoalesceException
    {

        Mutation m = new Mutation(entity.getKey());

        MutationWrapper MutationGuy = new MutationWrapper(m);

        processAllElements(entity, MutationGuy);

        LOGGER.trace("Total Mutation Rows: ({})", rowCount);

        return MutationGuy;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, MutationWrapper param)
    {
        // If the entity is marked not to be flattened do not persist it or any children
        if (!entity.isFlatten())
        {
            return false;
        }

        addRow(entity, param);
        String entity_xml = entity.toXml();
        // add the entity xml
        MutationRow row = new MutationRow(entity.getType() + ":" + entity.getNamePath(),
                                          "entityxml",
                                          entity_xml.getBytes(),
                                          entity.getNamePath());
        param.addRow(row);

        // Process Children
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, MutationWrapper param)
    {
        // If the section is marked not to be flattened then do not persist it or any children
        if (!section.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistLinkageAttr())
        {
            addRow(section, param);
        }

        // skip
        return AccumuloSettings.getPersistLinkageAttr();
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage, MutationWrapper param)
    {
        // If the linkage is marked not to be flattened then do not persist it or any children
        if (!linkage.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistLinkageAttr())
        {
            addRow(linkage, param);
        }

        return false;
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section, MutationWrapper param)
    {
        // If the section is marked not to be flattened then do not persist it or any children
        if (!section.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistSectionAttr())
        {
            addRow(section, param);
        }

        return true;

    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, MutationWrapper param)
    {
        // If the recordset is marked not to be flattened then do not persist it or any children
        if (!recordset.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistRecordsetAttr())
        {
            addRow(recordset, param);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record, MutationWrapper param)
    {
        // If the record is marked not to be flattened then do not persist it or any children
        if (!record.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistRecordAttr())
        {
            addRow(record, param);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field, MutationWrapper param)
    {
        // If the field is marked not to be flattened then do not persist it or any children
        if (!field.isFlatten())
            return false;

        if (AccumuloSettings.getPersistFieldAttr())
        {
            addRow(field, param);
        }

        // Don't visit children
        return false;
    }

    @Override
    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition, MutationWrapper param)
    {
        // If the definition is marked not to be flattened then do not persist it or any children
        if (!definition.isFlatten())
        {
            return false;
        }

        if (AccumuloSettings.getPersistFieldDefAttr())
        {
            addRow(definition, param);
        }

        return true;
    }

    private void addRow(CoalesceObject object, MutationWrapper param)
    {
        LOGGER.trace("Add Row: ({})", object.getName());

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
                param.addRow(row);

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
        param.addRow(row);

        rowCount++;
    }

    private boolean persistAttr(String attrName)
    {
        return AccumuloSettings.getAttributeFields().contains(attrName);
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
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            // do nothing
        }

        return attributeMap;
    }
}
