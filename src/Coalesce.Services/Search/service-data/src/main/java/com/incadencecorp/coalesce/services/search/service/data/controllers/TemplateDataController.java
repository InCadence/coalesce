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

/**
 * Provides details of the registered templates within a Coalesce database.
 * 
 * @author Derek Clemenzi
 */
public class TemplateDataController {

    private static final Map<String, CoalesceEntity> TEMPLATES = new HashMap<String, CoalesceEntity>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDataController.class);
    private static final String COALESCEENTITY_KEY = CoalesceEntity.class.getSimpleName();

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
                    TEMPLATES.put(meta.getKey(), template.createNewEntity());
                }
                catch (SAXException | IOException e)
                {
                    String errorMsg = String.format(CoalesceErrors.INVALID_OBJECT,
                                                    "Template",
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
        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error(String.format(CoalesceErrors.NOT_INITIALIZED, TemplateDataController.class.getSimpleName()), e);
        }
    }

    /**
     * @return a list of templates registered with this service.
     */
    public List<ObjectMetaData> getTemplates()
    {
        LOGGER.debug("Retrieving Templates");

        List<ObjectMetaData> results = new ArrayList<ObjectMetaData>();

        for (Map.Entry<String, CoalesceEntity> template : TEMPLATES.entrySet())
        {
            results.add(new ObjectMetaData(template.getKey(),
                                           template.getValue().getName(),
                                           template.getValue().getSource(),
                                           template.getValue().getVersion(),
                                           template.getValue().getDateCreated(),
                                           template.getValue().getLastModified()));

            LOGGER.trace("Including ({}) ({}) ({}) ({})",
                         template.getKey(),
                         template.getValue().getName(),
                         template.getValue().getSource(),
                         template.getValue().getVersion());

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

        List<ObjectData> results = new ArrayList<ObjectData>();

        results.add(new ObjectData(COALESCEENTITY_KEY, COALESCEENTITY_KEY));

        CoalesceEntity entity = TEMPLATES.get(key);

        if (entity != null)
        {
            for (CoalesceSection section : entity.getSectionsAsList())
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
            CoalesceEntity entity = TEMPLATES.get(key);

            if (entity != null)
            {
                CoalesceRecordset recordset = (CoalesceRecordset) entity.getCoalesceObjectForKey(recordsetKey);

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

}
