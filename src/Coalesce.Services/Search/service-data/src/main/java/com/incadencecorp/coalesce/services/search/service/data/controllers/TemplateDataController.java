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
     * Default Constructor (Used for Testing)
     */
    public TemplateDataController()
    {
        // TODO Remove this code
        if (TEMPLATES.size() == 0)
        {
            LOGGER.warn("Creating Mock Templates");

            CoalesceEntity template1 = CoalesceEntity.create("Template1", "Test", "1.0", null, null);

            CoalesceSection section = CoalesceSection.create(template1, "Test1 Section");
            CoalesceRecordset recordset = CoalesceRecordset.create(section, "Test1 RS");

            CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_TYPE);

            TEMPLATES.put(template1.getKey(), template1);

            CoalesceEntity template2 = CoalesceEntity.create("Template2", "Test", "1.0", null, null);
            CoalesceSection section2 = CoalesceSection.create(template2, "Test2 Section");
            CoalesceRecordset recordset2 = CoalesceRecordset.create(section2, "Test2 RS");

            CoalesceFieldDefinition.create(recordset2, "field3", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset2, "field4", ECoalesceFieldDataTypes.STRING_TYPE);

            CoalesceRecordset recordset3 = CoalesceRecordset.create(section2, "Test3 RS");

            CoalesceFieldDefinition.create(recordset3, "field5", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset3, "field6", ECoalesceFieldDataTypes.STRING_TYPE);

            TEMPLATES.put(template2.getKey(), template2);
        }

    }

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

        for (CoalesceEntity template : TEMPLATES.values())
        {
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
