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
package com.incadencecorp.coalesce.services.search.service.data.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import com.incadencecorp.coalesce.services.search.service.data.model.ObjectData;
import com.incadencecorp.unity.common.CallResult;
import com.incadencecorp.unity.common.CallResult.CallResults;

/**
 * Provides details of the registered templates within a Coalesce database.
 * 
 * @author Derek Clemenzi
 */
public class TemplateDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDataController.class);
    private static final String COALESCEENTITY_KEY = CoalesceEntity.class.getSimpleName();

    private final Map<String, TemplateNode> templates = new HashMap<String, TemplateNode>();
    private ICoalescePersistor persister;

    /**
     * Production Constructor
     * 
     * @param persister
     */
    public TemplateDataController(ICoalescePersistor persister)
    {
        try
        {
            for (ObjectMetaData meta : persister.getEntityTemplateMetadata())
            {
                CoalesceEntityTemplate template;
                try
                {
                    template = CoalesceEntityTemplate.create(persister.getEntityTemplateXml(meta.getKey()));

                    if (template != null)
                    {
                        templates.put(template.getKey(), new TemplateNode(template));
                    }
                }
                catch (SAXException | IOException e)
                {
                    String errorMsg = String.format(CoalesceErrors.INVALID_OBJECT,
                                                    CoalesceEntityTemplate.class.getSimpleName(),
                                                    meta.getKey(),
                                                    e.getMessage());

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.error(errorMsg, e);
                    }
                    else
                    {
                        LOGGER.error(errorMsg);
                    }
                }
            }

            this.persister = persister;
        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error(String.format(CoalesceErrors.NOT_INITIALIZED, TemplateDataController.class.getSimpleName()), e);
        }
    }

    /**
     * @return a list of templates registered with this service.
     */
    public List<ObjectMetaData> getEntityTemplateMetadata()
    {
        LOGGER.debug("Retrieving Templates");

        List<ObjectMetaData> results = new ArrayList<ObjectMetaData>();

        for (TemplateNode node : templates.values())
        {
            CoalesceEntityTemplate template = node.template;

            results.add(new ObjectMetaData(template.getKey(),
                                           template.getName(),
                                           template.getSource(),
                                           template.getVersion(),
                                           template.getDateCreated(),
                                           template.getLastModified()));

            LOGGER.trace("Including ({}) ({}) ({}) ({})",
                         template.getKey(),
                         template.getName(),
                         template.getSource(),
                         template.getVersion());

        }

        return results;
    }

    /**
     * @param key
     * @return a list of record sets for the provided template key.
     */
    public List<ObjectData> getRecordSets(String key)
    {
        LOGGER.debug("Retrieving Record Sets [Key: ({})]", key);

        // Also include the key
        List<ObjectData> results = new ArrayList<ObjectData>();

        if (templates.containsKey(key))
        {
            results.add(new ObjectData(COALESCEENTITY_KEY, COALESCEENTITY_KEY));

            for (CoalesceSection section : templates.get(key).entity.getSectionsAsList())
            {
                results.addAll(getRecordsets(section));
            }
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return results;
    }

    /**
     * @param key
     * @param recordsetKey
     * @return a list of fields for the provided record set key.
     */
    public List<FieldData> getRecordSetFields(String key, String recordsetKey)
    {
        LOGGER.debug("Retrieving Fields [Key: ({}) Recordset: ({})]", key, recordsetKey);

        List<FieldData> results = new ArrayList<FieldData>();

        if (recordsetKey.equalsIgnoreCase(COALESCEENTITY_KEY))
        {
            results.add(getField(CoalescePropertyFactory.getEntityKey(), ECoalesceFieldDataTypes.GUID_TYPE));
            results.add(getField(CoalescePropertyFactory.getEntityTitle(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getName(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getSource(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getDateCreated(), ECoalesceFieldDataTypes.DATE_TIME_TYPE));
            results.add(getField(CoalescePropertyFactory.getLastModified(), ECoalesceFieldDataTypes.DATE_TIME_TYPE));
        }
        else
        {
            if (templates.containsKey(key))
            {
                CoalesceRecordset recordset = (CoalesceRecordset) templates.get(key).entity.getCoalesceObjectForKey(recordsetKey);

                if (recordset != null)
                {
                    for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
                    {
                        results.add(new FieldData(fd.getKey(), fd.getName(), fd.getDataType()));
                    }
                }
                else
                {
                    LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Record Set", recordsetKey));
                }

            }
            else
            {
                LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
            }
        }

        return results;
    }

    /**
     * @param key
     * @return the {@link CoalesceEntityTemplate} for the specified key.
     */
    public CoalesceEntityTemplate getTemplate(String key)
    {
        CoalesceEntityTemplate result = null;

        if (templates.containsKey(key))
        {
            result = templates.get(key).template;
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return result;
    }

    /**
     * Saves the specified template.
     * 
     * @param template
     * @return whether or not it was successfully saved.
     */
    public CallResult setTemplate(CoalesceEntityTemplate template)
    {
        CallResult result;

        try
        {
            if (template != null)
            {
                persister.saveTemplate(template);
                templates.put(template.getKey(), new TemplateNode(template));

                result = new CallResult(CallResults.SUCCESS);
            }
            else
            {
                result = new CallResult(CallResults.FAILED);
            }

        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error(String.format(CoalesceErrors.NOT_SAVED,
                                       template.getKey(),
                                       CoalesceEntityTemplate.class.getSimpleName(),
                                       e.getMessage()),
                         e);
            result = new CallResult(CallResults.FAILED, e);
        }

        return result;
    }

    private FieldData getField(PropertyName property, ECoalesceFieldDataTypes type)
    {
        return new FieldData(property.getPropertyName(), property.getPropertyName().split("\\.")[1], type);
    }

    private List<ObjectData> getRecordsets(CoalesceSection section)
    {
        List<ObjectData> results = new ArrayList<ObjectData>();

        for (CoalesceSection childSection : section.getSectionsAsList())
        {
            results.addAll(getRecordsets(childSection));
        }

        for (CoalesceRecordset recordset : section.getRecordsetsAsList())
        {
            results.add(new ObjectData(recordset.getKey(), recordset.getName()));
        }

        return results;
    }

    private class TemplateNode {

        private CoalesceEntity entity;
        private CoalesceEntityTemplate template;

        public TemplateNode(CoalesceEntityTemplate template)
        {
            this.entity = template.createNewEntity();
            this.template = template;
        }

    }

}
