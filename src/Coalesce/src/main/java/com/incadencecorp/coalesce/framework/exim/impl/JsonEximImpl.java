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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.incadencecorp.coalesce.api.CoalesceExim;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * This implementation of the {@link CoalesceExim} interface creates
 * {@link JSONObject}s
 * 
 * @author n78554
 *
 */
public class JsonEximImpl implements CoalesceExim<JSONObject> {

    @Override
    public JSONObject exportValues(CoalesceEntity entity, boolean includeEntityType) throws CoalesceException
    {
        JSONObject entityNode = new JSONObject();

        try
        {

            if (includeEntityType)
            {
                entityNode.put("_source", entity.getSource());
                entityNode.put("_version", entity.getVersion());
                entityNode.put("_class", entity.getClassName());
            }

            entityNode.put(entity.getName(), createJSON(entity, true));

        }
        catch (JSONException e)
        {
            throw new CoalesceException("Failed to export entity to JSON", e);
        }

        return entityNode;
    }

    @Override
    public void importValues(JSONObject values, CoalesceEntity entity) throws CoalesceException
    {
        JSONToCoalesceIterator iterator = new JSONToCoalesceIterator();
        iterator.createEntity(entity, values);
    }

    @Override
    public CoalesceEntity importValues(JSONObject values, CoalesceEntityTemplate template) throws CoalesceException
    {
        CoalesceEntity entity = template.createNewEntity();

        JSONToCoalesceIterator iterator = new JSONToCoalesceIterator();
        iterator.createEntity(entity, values);

        return entity;
    }

    /**
     * Converts a JSON object back into a Coalesce Entity
     * 
     * @author n78554
     *
     */
    private class JSONToCoalesceIterator extends CoalesceIterator {

        private JSONObject json;

        public void createEntity(CoalesceEntity entity, JSONObject pJSON)
        {

            json = pJSON;

            processAllElements(entity);

        }

        @Override
        protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
        {
            try
            {
                JSONArray recordsJSON = getRecordset(recordset.getNamePath().split("[/]"));

                if (recordsJSON != null)
                {
                    // For Each Record
                    for (int ii = 0; ii < recordsJSON.length(); ii++)
                    {
                        // Process Fields
                        JSONObject fieldsJSON = (JSONObject) recordsJSON.get(ii);

                        CoalesceRecord record;

                        if (ii < recordset.getCount())
                        {
                            // Get Existing Record
                            record = recordset.getItem(ii);
                        }
                        else
                        {
                            // Create New Record
                            record = recordset.addNew();
                        }

                        // Populate Fields
                        for (CoalesceField<?> field : record.getFields())
                        {
                            if (fieldsJSON.has(field.getName()))
                            {
                                field.setAttribute("value", fieldsJSON.getString(field.getName()));
                            }
                            else
                            {
                                field.setAttribute("value", null);
                            }
                        }

                    }
                }
            }
            catch (JSONException e)
            {
                throw new RuntimeException("Failed", e);
            }

            // Don't process child nodes
            return false;
        }

        /**
         * 
         * @param names
         * @return a JSONArray that represents the records of a record set.
         * @throws JSONException
         */
        private JSONArray getRecordset(String... names) throws JSONException
        {
            JSONArray result = null;
            JSONObject node = json;
            String recordsetName = names[names.length - 1];

            for (int ii = 0; ii < names.length - 1; ii++)
            {
                if (node.has(names[ii]))
                {
                    node = node.getJSONObject(names[ii]);
                }
                else
                {
                    node = null;
                    break;
                }
            }

            if (node != null && node.has(recordsetName))
            {
                result = node.getJSONArray(recordsetName);
            }

            return result;

        }
    }

    /**
     * Recursive method that converts a CoalesceObject into a JSON object.
     * 
     * @param coalesceObject
     * @param onlyActive
     * @return
     * @throws JSONException
     */
    private JSONObject createJSON(CoalesceObject coalesceObject, boolean onlyActive) throws JSONException
    {
        JSONObject node = new JSONObject();

        if (!onlyActive || !coalesceObject.isMarkedDeleted())
        {
            // Yes; Iterate Through Children
            for (CoalesceObject childObject : coalesceObject.getChildCoalesceObjects().values())
            {
                if (childObject instanceof CoalesceRecordset)
                {
                    JSONArray recordsetJSON = new JSONArray();

                    for (CoalesceRecord record : ((CoalesceRecordset) childObject).getRecords())
                    {
                        JSONObject recordJSON = new JSONObject();

                        // Add Fields w/ Data
                        for (CoalesceField<?> field : record.getFields())
                        {
                            if (!StringHelper.isNullOrEmpty(field.getBaseValue()))
                            {
                                recordJSON.put(field.getName(), field.getBaseValue());
                            }
                        }

                        if (recordJSON.length() > 0)
                        {
                            recordsetJSON.put(recordJSON);
                        }
                    }

                    if (recordsetJSON.length() > 0)
                    {
                        node.put(childObject.getName(), recordsetJSON);
                    }

                }
                else if (!(childObject instanceof CoalesceLinkageSection))
                {
                    // Recursively Process Children
                    JSONObject child = createJSON(childObject, onlyActive);

                    // Only Add Children w/ Data
                    if (child.length() > 0)
                    {
                        // children.put(child);
                        node.put(childObject.getName(), child);
                    }
                }

            }
        }

        return node;

    }
}
