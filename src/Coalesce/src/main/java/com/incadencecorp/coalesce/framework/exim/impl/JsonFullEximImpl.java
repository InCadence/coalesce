/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.exim.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceExim;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This implementation of the {@link CoalesceExim} interface creates
 * {@link JSONObject}s
 *
 * @author n78554
 */
public class JsonFullEximImpl implements CoalesceExim<JSONObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFullEximImpl.class);
    private Class<?> view = Views.Entity.class;

    /**
     * @param clazz the JSON view to use.
     */
    public void setView(Class<?> clazz)
    {
        this.view = clazz;
    }

    @Override
    public JSONObject exportValues(CoalesceEntity entity, boolean includeEntityType) throws CoalesceException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        try
        {
            return new JSONObject(mapper.writerWithView(view).writeValueAsString(entity));
        }
        catch (IOException e)
        {
            throw new CoalesceException(e);
        }
    }

    @Override
    public CoalesceEntity importValues(JSONObject values, CoalesceEntityTemplate template) throws CoalesceException
    {
        CoalesceEntity entity = template.createNewEntity();
        entity.initialize();

        importValues(values, entity);
        return entity;
    }

    private static final List<String> OMMIT = Arrays.asList("namepath",
                                                            "sectionsaslist",
                                                            "otherattributes",
                                                            "recordsetsaslist",
                                                            "linkagesection",
                                                            "tag",
                                                            "fielddefinitions",
                                                            "allrecords",
                                                            "fields",
                                                            "linkagesaslist",
                                                            "datatype",
                                                            "type",
                                                            "classname",
                                                            "portionmarking",
                                                            "classificationmarkingasstring",
                                                            "allowedit",
                                                            "allownew",
                                                            "allowremove",
                                                            "count",
                                                            "hasactiverecords",
                                                            "hasrecords",
                                                            "createdby");

    @Override
    public void importValues(JSONObject values, CoalesceEntity entity) throws CoalesceException
    {
        copyAttributes(values, entity);

        copyLinkageSection(values.getJSONObject("linkageSection"), entity.getLinkageSection());

        JSONArray jsonSections = values.getJSONArray("sectionsAsList");

        for (int i = 0; i < jsonSections.length(); i++)
        {
            copySection(jsonSections.getJSONObject(i), entity);
        }
    }

    private void copyLinkageSection(JSONObject json, CoalesceLinkageSection section)
    {
        copyAttributes(json, section);

        JSONArray jsonLinkages = json.getJSONArray("linkagesAsList");

        for (int ii = 0; ii < jsonLinkages.length(); ii++)
        {
            copyAttributes(jsonLinkages.getJSONObject(ii), section.createLinkage());
        }
    }

    private void copySection(JSONObject json, CoalesceObject parent) throws CoalesceException
    {
        String name = json.getString(CoalesceObject.ATTRIBUTE_NAME);

        CoalesceSection section = parent.getCoalesceSectionForNamePath(parent.getName(), name);

        // Exists?
        if (section != null)
        {
            // Copy Attributes
            copyAttributes(json, section);

            // Create Nested Sections
            JSONArray jsonSections = json.getJSONArray("sectionsAsList");

            for (int ii = 0; ii < jsonSections.length(); ii++)
            {
                copySection(jsonSections.getJSONObject(ii), section);
            }

            // Create Record Sets
            JSONArray jsonRecordSets = json.getJSONArray("recordsetsAsList");

            for (int ii = 0; ii < jsonRecordSets.length(); ii++)
            {
                copyRecordset(jsonRecordSets.getJSONObject(ii), section);
            }
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.INVALID_OBJECT, "Section", name));
        }
    }

    private void copyRecordset(JSONObject json, CoalesceSection section) throws CoalesceException
    {
        String name = json.getString(CoalesceSection.ATTRIBUTE_NAME);

        CoalesceRecordset recordset = section.getCoalesceRecordsetForNamePath(section.getName(), name);

        // Exists?
        if (recordset != null)
        {
            copyAttributes(json, recordset);

            JSONArray jsonRecords = json.getJSONArray("allRecords");

            if (recordset.getMaxRecords() == 1 && recordset.getMinRecords() == 1)
            {
                copyRecord(jsonRecords.getJSONObject(0), recordset.getAllRecords().get(0));
            }
            else
            {
                for (int ii = 0; ii < jsonRecords.length(); ii++)
                {
                    copyRecord(jsonRecords.getJSONObject(ii), recordset.addNew());
                }
            }
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.INVALID_OBJECT, "Recordset", name));
        }

    }

    private void copyRecord(JSONObject json, CoalesceRecord record)
    {
        // Copy Attributes
        copyAttributes(json, record);

        JSONArray jsonFields = json.getJSONArray("fields");

        for (int ii = 0; ii < jsonFields.length(); ii++)
        {
            JSONObject jsonField = jsonFields.getJSONObject(ii);

            String name = jsonField.getString("name");
            CoalesceField<?> field = record.getFieldByName(name);

            if (field != null)
            {
                copyAttributes(jsonField, field);
            }
            else
            {
                LOGGER.warn("Field {} Does not Exists", name);
            }
        }
    }

    private void copyAttributes(JSONObject json, CoalesceObject node)
    {
        for (String name : JSONObject.getNames(json))
        {
            if (!OMMIT.contains(name.toLowerCase()))
            {
                Object value = json.get(name);

                if (value instanceof String)
                {
                    node.setAttribute(name, (String) value);
                }
                else if (value instanceof Integer)
                {
                    node.setAttribute(name, String.valueOf(value));
                }
                else if (value instanceof Boolean)
                {
                    node.setAttribute(name, String.valueOf(value));
                }
                else if (value.getClass().getSimpleName().equalsIgnoreCase("Null"))
                {
                    node.setAttribute(name, "");
                }
                else if (value instanceof JSONArray && node instanceof CoalesceField
                        && ((CoalesceField<?>) node).isListType())
                {
                    JSONArray values = (JSONArray) value;
                    List<String> baseValue = new ArrayList<>();

                    switch (((CoalesceField<?>) node).getDataType())
                    {
                    case STRING_LIST_TYPE:
                        for (int ii = 0; ii < values.length(); ii++)
                        {
                            baseValue.add((String) values.get(ii));
                        }
                        break;
                    default:
                        LOGGER.warn("Attribute ({}) from ({}) was an unhandled list type ({})",
                                    name,
                                    json.getString("name"),
                                    ((CoalesceField<?>) node).getDataType());
                        break;

                    }

                    node.setAttribute(CoalesceField.ATTRIBUTE_VALUE,
                                      StringUtils.join(baseValue.toArray(new String[baseValue.size()]), ","));
                }
                else
                {
                    LOGGER.warn("Attribute ({}) from ({}) was an unhandled type ({})",
                                name,
                                json.getString("name"),
                                value.getClass().getSimpleName());
                }
            }
        }
    }
}
