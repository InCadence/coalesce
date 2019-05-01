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
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
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
    private static final String COALESCELINKAGE_KEY = CoalesceLinkage.class.getSimpleName();

    private final Map<String, TemplateNode> templates = new HashMap<>();
    private final CoalesceFramework framework;

    /**
     * Production Constructor
     *
     * @param framework used for reading, saving, and registering templates.
     */
    public TemplateDataController(CoalesceFramework framework)
    {
        try
        {
            List<ObjectMetaData> metadata = framework.getCoalesceEntityTemplateMetadata();

            LOGGER.info("Loading {} Templates", metadata.size());

            for (ObjectMetaData meta : metadata)
            {
                CoalesceEntityTemplate template;
                try
                {
                    template = framework.getCoalesceEntityTemplate(meta.getKey());

                    if (template != null)
                    {
                        templates.put(template.getKey(), new TemplateNode(template));

                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("Including ({}) ({}) ({}) ({})",
                                         template.getKey(),
                                         template.getName(),
                                         template.getSource(),
                                         template.getVersion());
                        }

                    }
                    else
                    {
                        LOGGER.warn(String.format(CoalesceErrors.TEMPLATE_LOAD,
                                                  meta.getName(),
                                                  meta.getSource(),
                                                  meta.getVersion()));
                    }
                }
                catch (CoalescePersistorException e)
                {
                    String errorMsg = String.format(CoalesceErrors.INVALID_OBJECT, meta.getKey(), e.getMessage());

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

        this.framework = framework;
    }

    /**
     * @return a list of templates registered with this service.
     * @throws RemoteException on error
     */
    public List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException
    {
        LOGGER.debug("Retrieving Templates");

        List<ObjectMetaData> results = new ArrayList<>();

        for (TemplateNode node : templates.values())
        {
            CoalesceEntityTemplate template = node.template;

            results.add(new ObjectMetaData(template.getKey(),
                                           template.getName(),
                                           template.getSource(),
                                           template.getVersion(),
                                           template.getDateCreated(),
                                           template.getLastModified()));

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Including ({}) ({}) ({}) ({})",
                             template.getKey(),
                             template.getName(),
                             template.getSource(),
                             template.getVersion());
            }
        }

        return results;
    }

    /**
     * @param key recordset's key
     * @return a list of record sets for the provided template key.
     * @throws RemoteException on error
     */
    public List<CoalesceObjectImpl> getRecordSets(String key) throws RemoteException
    {
        LOGGER.debug("Retrieving Record Sets [Key: ({})]", key);

        // Also include the key
        List<CoalesceObjectImpl> results = new ArrayList<>();

        if (templates.containsKey(key))
        {
            results.add(new CoalesceObjectImpl(COALESCEENTITY_KEY, COALESCEENTITY_KEY));
            results.add(new CoalesceObjectImpl(COALESCELINKAGE_KEY, COALESCELINKAGE_KEY));

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
     * @param key          field's key
     * @param recordsetKey recordset's key
     * @return a list of fields for the provided record set key.
     * @throws RemoteException on error
     */
    public List<FieldData> getRecordSetFields(String key, String recordsetKey) throws RemoteException
    {
        LOGGER.debug("Retrieving Fields [Key: ({}) Recordset: ({})]", key, recordsetKey);

        List<FieldData> results = new ArrayList<>();

        if (recordsetKey.equalsIgnoreCase(COALESCEENTITY_KEY))
        {
            results.add(getField(CoalescePropertyFactory.getEntityKey(), ECoalesceFieldDataTypes.GUID_TYPE));
            results.add(getField(CoalescePropertyFactory.getEntityTitle(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getName(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getSource(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getDateCreated(), ECoalesceFieldDataTypes.DATE_TIME_TYPE));
            results.add(getField(CoalescePropertyFactory.getLastModified(), ECoalesceFieldDataTypes.DATE_TIME_TYPE));
            results.add(getField(CoalescePropertyFactory.getEntityStatus(), ECoalesceFieldDataTypes.ENUMERATION_TYPE));
            results.add(getField(CoalescePropertyFactory.getEntityId(), ECoalesceFieldDataTypes.STRING_TYPE));
        }
        else if (recordsetKey.equalsIgnoreCase(COALESCELINKAGE_KEY))
        {
            results.add(getField(CoalescePropertyFactory.getLinkageLabel(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageStatus(), ECoalesceFieldDataTypes.ENUMERATION_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageType(), ECoalesceFieldDataTypes.ENUMERATION_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageEntityKey(), ECoalesceFieldDataTypes.GUID_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageSource(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageName(), ECoalesceFieldDataTypes.STRING_TYPE));
            results.add(getField(CoalescePropertyFactory.getLinkageVersion(), ECoalesceFieldDataTypes.STRING_TYPE));
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
                        results.add(new FieldData(fd));
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

    /**
     * @param name    template's name
     * @param source  template's source
     * @param version template's version
     * @return the specified template
     * @throws RemoteException on error
     */
    public CoalesceEntity getTemplate(String name, String source, String version) throws RemoteException
    {
        return getTemplateNode(name, source, version).entity;
    }

    /**
     * @param name    template's name
     * @param source  template's source
     * @param version template's version
     * @return the specified template in XML format
     * @throws RemoteException on error
     */
    public String getTemplateXml(String name, String source, String version) throws RemoteException
    {
        return getTemplateNode(name, source, version).template.toXml();
    }

    /**
     * @param key template's key
     * @return the {@link CoalesceEntityTemplate} for the specified key.
     * @throws RemoteException on error
     */
    public CoalesceEntity getTemplate(String key) throws RemoteException
    {
        return getTemplateNode(key).entity;
    }

    /**
     * @param key template's key
     * @return the specified template in XML format
     * @throws RemoteException on error
     */
    public String getTemplateXml(String key) throws RemoteException
    {
        return getTemplateNode(key).template.toXml();
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
     * @param key    template's key
     * @param entity in which the template should be derived
     * @return whether or not it was successfully saved.
     * @throws RemoteException on error
     */
    public String setTemplate(String key, CoalesceEntity entity) throws RemoteException
    {
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

            if (template != null)
            {
                if (!key.equalsIgnoreCase("new") && !key.equalsIgnoreCase(template.getKey()))
                {
                    // We want to make sure the template key is hashed from name/source/version, not arbitrary.
                    error(String.format(CoalesceErrors.INVALID_KEY, key, template.getKey()));
                }

                return setTemplate(template);
            }
        }

        return null;
    }

    /**
     * Saves the specified template.
     *
     * @param json template in json format
     * @return whether or not it was successfully saved.
     * @throws RemoteException on error
     */
    public String createTemplateJson(String json) throws RemoteException
    {
        String result = null;

        try
        {
            result = setTemplate(createTemplate(json));
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED,
                                "NEW",
                                CoalesceEntityTemplate.class.getSimpleName(),
                                e.getMessage()), e);
        }

        return result;
    }

    public String createTemplateXml(String xml) throws RemoteException
    {
        String results = null;

        try
        {
            results = setTemplate(CoalesceEntityTemplate.create(xml));
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED,
                                "NEW",
                                CoalesceEntityTemplate.class.getSimpleName(),
                                e.getMessage()), e);
        }

        return results;
    }

    public void updateTemplateJson(String key, String json) throws RemoteException
    {
        try
        {
            setTemplate(key, createTemplate(json));
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, key, CoalesceEntityTemplate.class.getSimpleName(), e.getMessage()),
                  e);
        }
    }

    public void updateTemplateXml(String key, String xml) throws RemoteException
    {
        try
        {
            setTemplate(key, CoalesceEntityTemplate.create(xml));
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, key, CoalesceEntityTemplate.class.getSimpleName(), e.getMessage()),
                  e);
        }
    }

    public void registerTemplate(String key) throws RemoteException
    {
        try
        {
            framework.registerTemplates(framework.getCoalesceEntityTemplate(key));
        }
        catch (CoalescePersistorException e)
        {
            error("Registration Failed", e);
        }
    }

    public void deleteTemplate(String key) throws RemoteException
    {
        try
        {
            framework.deleteTemplate(key);
            templates.remove(key);
        }
        catch (CoalescePersistorException e)
        {
            error("Failed to delete the template; template deletion may not be implemented for the persistor in question.",
                  e);
        }
    }

    private void setTemplate(String key, CoalesceEntityTemplate template) throws RemoteException
    {
        if (!key.equalsIgnoreCase(template.getKey()))
        {
            // We want to make sure the template key is hashed from name/source/version, not arbitrary.
            error(String.format(CoalesceErrors.INVALID_KEY, key, template.getKey()));
        }

        setTemplate(template);
    }

    private String setTemplate(CoalesceEntityTemplate template) throws RemoteException
    {
        String result = null;

        try
        {
            if (template.getDateCreated() == null)
            {
                template.setDateCreated(JodaDateTimeHelper.nowInUtc());
            }

            template.setLastModified(JodaDateTimeHelper.nowInUtc());

            LOGGER.info("Saving template {}, Key: {}", template.getName(), template.getKey());
            framework.saveCoalesceEntityTemplate(template);
            templates.put(template.getKey(), new TemplateNode(template));

            result = template.getKey();
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
        entity.setName(obj.getString(CoalesceEntity.ATTRIBUTE_NAME));
        entity.setSource(obj.getString(CoalesceEntity.ATTRIBUTE_SOURCE));
        entity.setVersion(obj.getString(CoalesceEntity.ATTRIBUTE_VERSION));
        entity.setAttribute(CoalesceEntity.ATTRIBUTE_CLASSNAME, className);

        JSONArray jsonSections = obj.getJSONArray("sectionsAsList");

        for (int i = 0; i < jsonSections.length(); i++)
        {
            JSONObject jsonSection = jsonSections.getJSONObject(i);
            String SectionName = jsonSection.getString(CoalesceSection.ATTRIBUTE_NAME);

            CoalesceSection section = entity.createSection(SectionName);

            JSONArray jsonRecordSets = jsonSection.getJSONArray("recordsetsAsList");

            for (int j = 0; j < jsonRecordSets.length(); j++)
            {
                JSONObject jsonRecordSet = jsonRecordSets.getJSONObject(j);
                String recordsetName = jsonRecordSet.getString(CoalesceRecordset.ATTRIBUTE_NAME);
                CoalesceRecordset recordset = section.createRecordset(recordsetName);
                recordset.setMinRecords(jsonRecordSet.getInt("minRecords"));
                recordset.setMaxRecords(jsonRecordSet.getInt("maxRecords"));

                JSONArray jsonFields = jsonRecordSet.getJSONArray("fieldDefinitions");

                for (int k = 0; k < jsonFields.length(); k++)
                {
                    JSONObject jsonField = jsonFields.getJSONObject(k);
                    String fieldName = jsonField.getString(CoalesceFieldDefinition.ATTRIBUTE_NAME);
                    String fieldType = jsonField.getString("dataType");
                    String label = getString(jsonField, CoalesceFieldDefinition.ATTRIBUTE_LABEL);
                    String description = getString(jsonField, CoalesceFieldDefinition.ATTRIBUTE_DESCRIPTION);
                    String defaultValue = getString(jsonField, "defaultValue");
                    ECoalesceFieldDataTypes type = ECoalesceFieldDataTypes.getTypeForCoalesceType(fieldType);

                    CoalesceFieldDefinition fd = recordset.createFieldDefinition(fieldName, type, label, "U", defaultValue);
                    fd.setDescription(description);
                }
            }
        }

        return CoalesceEntityTemplate.create(entity);
    }

    /**
     * @return the value if its present and a String otherwise null.
     */
    private String getString(JSONObject json, String key)
    {
        if (json.has(key))
        {
            Object object = json.get(key);
            return object instanceof String ? (String) object : null;
        }
        else
        {
            return null;
        }
    }

    private FieldData getField(PropertyName property, ECoalesceFieldDataTypes type)
    {
        return new FieldData(property.getPropertyName(), property.getPropertyName().split("\\.")[1], type);
    }

    private List<CoalesceObjectImpl> getRecordsets(CoalesceSection section)
    {
        List<CoalesceObjectImpl> results = new ArrayList<>();

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

    private TemplateNode getTemplateNode(String key) throws RemoteException
    {
        TemplateNode result = null;

        if (templates.containsKey(key))
        {
            result = templates.get(key);
        }
        else
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return result;
    }

    private TemplateNode getTemplateNode(String name, String source, String version) throws RemoteException
    {
        TemplateNode result = null;

        for (TemplateNode node : templates.values())
        {
            if (name.equalsIgnoreCase(node.template.getName()) && source.equalsIgnoreCase(node.template.getSource())
                    && version.equalsIgnoreCase(node.template.getVersion()))
            {
                result = node;
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

    private static class TemplateNode {

        private CoalesceEntity entity;
        private CoalesceEntityTemplate template;

        private TemplateNode(CoalesceEntityTemplate template)
        {
            this.entity = new CoalesceEntity();
            this.entity.initialize(template.toXml());
            this.template = template;

            for (CoalesceSection section : this.entity.getSectionsAsList())
            {
                populateEnumerationOptions(section);
            }
        }

        private void populateEnumerationOptions(CoalesceSection section)
        {
            for (CoalesceSection subsection : section.getSectionsAsList())
            {
                populateEnumerationOptions(subsection);
            }

            for (CoalesceRecordset recordset : section.getRecordsetsAsList())
            {
                for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
                {
                    if (fd.getDataType() == ECoalesceFieldDataTypes.ENUMERATION_TYPE
                            || fd.getDataType() == ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE)
                    {
                        //fd.
                    }
                }
            }
        }

        /*
        private String getEnumerationName(CoalesceFieldDefinition fd)
        {
            String result = getName();

            for (CoalesceConstraint constraint : getFieldDefinition().getConstraints())
            {
                if (constraint.getConstraintType() == ConstraintType.ENUMERATION)
                {
                    result = constraint.getValue();
                }
            }

            return result;
        }
        */
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
