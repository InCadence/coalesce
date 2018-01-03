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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.search.service.data.model.CoalesceObjectImpl;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides details of the registered templates within a Coalesce database.
 *
 * @author Derek Clemenzi
 */
public class TemplateDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDataController.class);
    private static final String COALESCEENTITY_KEY = CoalesceEntity.class.getSimpleName();

    private final Map<String, TemplateNode> templates = new HashMap<String, TemplateNode>();
    private CoalesceFramework framework;

    /**
     * Production Constructor
     *
     * @param framework
     */
    public TemplateDataController(CoalesceFramework framework)
    {
        try
        {
            for (ObjectMetaData meta : framework.getCoalesceEntityTemplateMetadata())
            {
                CoalesceEntityTemplate template;
                try
                {
                    template = framework.getCoalesceEntityTemplate(meta.getKey());

                    if (template != null)
                    {
                        templates.put(template.getKey(), new TemplateNode(template));
                    }
                }
                catch (CoalescePersistorException e)
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

            this.framework = framework;
        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error(String.format(CoalesceErrors.NOT_INITIALIZED, TemplateDataController.class.getSimpleName()), e);
        }
    }

    /**
     * @return a list of templates registered with this service.
     */
    public List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException
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
    public List<CoalesceObjectImpl> getRecordSets(String key) throws RemoteException
    {
        LOGGER.debug("Retrieving Record Sets [Key: ({})]", key);

        // Also include the key
        List<CoalesceObjectImpl> results = new ArrayList<CoalesceObjectImpl>();

        if (templates.containsKey(key))
        {
            results.add(new CoalesceObjectImpl(COALESCEENTITY_KEY, COALESCEENTITY_KEY));

            for (CoalesceSection section : templates.get(key).entity.getSectionsAsList())
            {
                results.addAll(getRecordsets(section));
            }
        }
        else
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return results;
    }

    /**
     * @param key
     * @param recordsetKey
     * @return a list of fields for the provided record set key.
     */
    public List<FieldData> getRecordSetFields(String key, String recordsetKey) throws RemoteException
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
                CoalesceRecordset recordset = (CoalesceRecordset) templates.get(key).entity.getCoalesceObjectForKey(
                        recordsetKey);

                if (recordset != null)
                {
                    for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
                    {
                        results.add(new FieldData(fd.getKey(), fd.getName(), fd.getDataType()));
                    }
                }
                else
                {
                    error(String.format(CoalesceErrors.NOT_FOUND, "Record Set", recordsetKey));
                }

            }
            else
            {
                error(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
            }
        }

        return results;
    }

    public CoalesceEntity getTemplate(String name, String source, String version) throws RemoteException
    {
        CoalesceEntity result = null;

        for (TemplateNode node : templates.values())
        {
            if (name.equalsIgnoreCase(node.template.getName()) && source.equalsIgnoreCase(node.template.getSource())
                    && version.equalsIgnoreCase(node.template.getVersion()))
            {
                result = node.entity;
            }
        }

        if (result == null)
        {
            error(String.format(CoalesceErrors.NOT_FOUND,
                                "Template",
                                "name=" + name + ", source=" + source + ", version=" + version));
        }

        return result;

    }

    public String getTemplateXml(String name, String source, String version) throws RemoteException
    {
        return getTemplate(name, source, version).toXml();
    }

    /**
     * @param key
     * @return the {@link CoalesceEntityTemplate} for the specified key.
     */
    public CoalesceEntity getTemplate(String key) throws RemoteException
    {
        CoalesceEntity result = null;

        if (templates.containsKey(key))
        {
            result = templates.get(key).entity;
        }
        else
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return result;
    }

    public String getTemplateXml(String key) throws RemoteException
    {
        return getTemplate(key).toXml();
    }

    public CoalesceEntity getNewEntity(String key) throws RemoteException
    {
        CoalesceEntity result = null;

        if (templates.containsKey(key))
        {
            result = templates.get(key).template.createNewEntity();
        }
        else
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return result;
    }

    public String getNewEntityXml(String key) throws RemoteException
    {
        return getNewEntity(key).toXml();
    }

    /**
     * Saves the specified template.
     *
     * @param entity
     * @return whether or not it was successfully saved.
     */
    public boolean setTemplate(String key, CoalesceEntity entity) throws RemoteException
    {
        boolean result = false;

        CoalesceEntityTemplate template = null;

        if (entity != null)
        {
            try
            {
                template = CoalesceEntityTemplate.create(entity);
            }
            catch (CoalesceException e)
            {
                error(String.format(CoalesceErrors.NOT_SAVED,
                                    key,
                                    CoalesceEntityTemplate.class.getSimpleName(),
                                    e.getMessage()), e);
            }

            setTemplate(template);

            result = true;
        }

        return result;
    }

    /**
     * Saves the specified template.
     *
     * @param key
     * @param json
     * @return whether or not it was successfully saved.
     */
    public boolean setTemplateJson(String key, String json) throws RemoteException
    {
        boolean result = false;

        try
        {
            if (json != null)
            {
                result = setTemplate(createTemplate(json));
            }
            else
            {
                result = false;
            }

        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, key, CoalesceEntityTemplate.class.getSimpleName(), e.getMessage()),
                  e);
        }

        return result;
    }

    public boolean setTemplateXml(String key, String xml) throws RemoteException
    {
        boolean results = false;
        try
        {
            results = setTemplate(CoalesceEntityTemplate.create(xml));
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, key, CoalesceEntityTemplate.class.getSimpleName(), e.getMessage()),
                  e);
        }

        return results;
    }

    public boolean registerTemplate(String key) throws RemoteException
    {
        String xml;
        try
        {
            framework.registerTemplates(framework.getCoalesceEntityTemplate(key));
        }
        catch (CoalescePersistorException e)
        {
            // TODO Use CoalesceErrors
            error("Registeration Failed", e);
        }

        return true;
    }

    public boolean deleteTemplate(String key) throws RemoteException
    {
        throw new RemoteException("Not Implemented");
    }

    private boolean setTemplate(CoalesceEntityTemplate template) throws RemoteException
    {
        boolean result = false;

        try
        {
            LOGGER.info("Saving template {}, Key: {}", template.getName(), template.getKey());
            framework.saveCoalesceEntityTemplate(template);
            templates.put(template.getKey(), new TemplateNode(template));

            result = true;
        }
        catch (CoalescePersistorException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED,
                                template.getKey(),
                                CoalesceEntityTemplate.class.getSimpleName(),
                                e.getMessage()), e);
        }

        return result;
    }

    private CoalesceEntityTemplate createTemplate(String json) throws CoalesceException
    {
        JSONObject obj = new JSONObject(json);
        String className = obj.getString("className");

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName(obj.getString("name"));
        entity.setSource(obj.getString("source"));
        entity.setVersion(obj.getString("version"));
        entity.setAttribute(CoalesceEntity.ATTRIBUTE_CLASSNAME, className);

        JSONArray jsonSections = obj.getJSONArray("sectionsAsList");

        for (int i = 0; i < jsonSections.length(); i++)
        {
            JSONObject jsonSection = jsonSections.getJSONObject(i);
            String SectionName = jsonSection.getString("name");

            CoalesceSection section = entity.createSection(SectionName);

            JSONArray jsonRecordSets = jsonSection.getJSONArray("recordsetsAsList");

            for (int j = 0; j < jsonRecordSets.length(); j++)
            {

                JSONObject jsonRecordSet = jsonRecordSets.getJSONObject(j);
                String recordsetName = jsonRecordSet.getString("name");
                CoalesceRecordset recordset = section.createRecordset(recordsetName);
                recordset.setMinRecords(jsonRecordSet.getInt("minRecords"));
                recordset.setMaxRecords(jsonRecordSet.getInt("maxRecords"));

                JSONArray jsonFields = jsonRecordSet.getJSONArray("fieldDefinitions");

                for (int k = 0; k < jsonFields.length(); k++)
                {
                    JSONObject jsonField = jsonFields.getJSONObject(k);
                    String fieldName = jsonField.getString("name");
                    String fieldType = jsonField.getString("dataType");
                    ECoalesceFieldDataTypes type = ECoalesceFieldDataTypes.getTypeForCoalesceType(fieldType);

                    recordset.createFieldDefinition(fieldName, type);
                }
            }
        }

        return CoalesceEntityTemplate.create(entity);
    }

    private FieldData getField(PropertyName property, ECoalesceFieldDataTypes type)
    {
        return new FieldData(property.getPropertyName(), property.getPropertyName().split("\\.")[1], type);
    }

    private List<CoalesceObjectImpl> getRecordsets(CoalesceSection section)
    {
        List<CoalesceObjectImpl> results = new ArrayList<CoalesceObjectImpl>();

        for (CoalesceSection childSection : section.getSectionsAsList())
        {
            results.addAll(getRecordsets(childSection));
        }

        for (CoalesceRecordset recordset : section.getRecordsetsAsList())
        {
            results.add(new CoalesceObjectImpl(recordset));
        }

        return results;
    }

    private class TemplateNode {

        private CoalesceEntity entity;
        private CoalesceEntityTemplate template;

        public TemplateNode(CoalesceEntityTemplate template)
        {
            this.entity = template.createNewEntity();
            this.entity.setKey(template.getKey());
            this.template = template;
        }

    }

    private void error(String msg) throws RemoteException
    {
        error(msg, null);
    }

    private void error(String msg, Exception e) throws RemoteException
    {
        if (e == null)
        {
            LOGGER.warn(msg);
        }
        else
        {
            LOGGER.error(msg, e);
        }

        throw new RemoteException(msg);
    }
}
