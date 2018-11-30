/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.ingest.plugins.fsi;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.ingest.api.IExtractor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import com.incadencecorp.oe.common.utils.PmesiiUtil;

import java.util.*;

public class FSI_EntityExtractor extends CoalesceComponentImpl implements IExtractor {

    /**
     * methods that must be called before the extract method:
     * setFramework
     */

    private CoalesceSearchFramework framework;
    private String separator;
    private Map<String, CoalesceEntityTemplate> entityTemplates;
    private JSONArray templatesArray;

    //

    ///CoalesceTemplateUtil.getRecordsets()
    @Override
    public void setFramework(CoalesceSearchFramework framework)
    {
        this.framework = framework;
    }

    @Override
    public List<CoalesceEntityTemplate> getTemplatesUsed()
    {
        return new ArrayList<>();
    }

    //TODO: Parsing Polygons, Coordinates, Lists,
    @Override
    public List<CoalesceEntity> extract(String filename, String line) throws CoalesceException
    {

        List<CoalesceEntity> entities = new ArrayList<>();
        String[] fields = line.split(this.separator);

        for (Object aTemplatesArray : this.templatesArray)
        {
            JSONObject template = (JSONObject) aTemplatesArray;
            String templateKey = (String) template.get("templateKey");

            JSONObject record = (JSONObject) template.get("record");
            String recordName = (String) record.get("name");

            JSONObject fieldsMap = (JSONObject) record.get("fields");

            CoalesceEntityTemplate entityTemplate = entityTemplates.get(templateKey);
            CoalesceEntity entity = entityTemplate.createNewEntity();
            System.out.println(entityTemplates.size());
            CoalesceRecordset rs = entity.getCoalesceRecordsetForNamePath(entity.getName(),
                                                                          entity.getSectionsAsList().get(0).getName(),
                                                                          recordName);
            assert rs != null;
            CoalesceRecord cr = rs.getItem(0);

            Map<String, ECoalesceFieldDataTypes> typesMap = CoalesceTemplateUtil.getTemplateDataTypes(templateKey);
            Object[] fieldsMapKeys = fieldsMap.keySet().toArray();
            for (Object fieldsMapKey : fieldsMapKeys)
            {
                String fieldsIndex = (String) fieldsMapKey;
                fieldsIndex = fieldsIndex.replace("\"", "");
                String column = (String) fieldsMap.get(fieldsIndex);

                ECoalesceFieldDataTypes type = typesMap.get(fieldsMapKey);  //Example return of .get: "source varchar(256)"
                int index = Integer.parseInt(fieldsIndex);

                setFieldValue(type, cr, column, fields, index);
                entities.add(entity);
            }
        }

        return entities;
    }

    public void setFieldValue(ECoalesceFieldDataTypes type, CoalesceRecord cr, String column, String[] fields, int index) {
        switch (type)
        {
        case STRING_TYPE:
        case URI_TYPE:
            ((CoalesceStringField) cr.getFieldByName(column)).setValue(fields[index]);
            break;
        //                    case STRING_LIST_TYPE:
        //                        ((CoalesceStringListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case DATE_TIME_TYPE:
        //                        break;
        //
        //                    case BOOLEAN_TYPE:
        //                        ((CoalesceBooleanField)cr.getFieldByName(column)).setValue(Boolean.valueOf(fields[index]));
        //                        break;
        //
        //                    case BOOLEAN_LIST_TYPE:
        //                        ((CoalesceBooleanListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case ENUMERATION_TYPE:
        //                    case INTEGER_TYPE:
        //                        ((CoalesceIntegerField)cr.getFieldByName(column)).setValue(Integer.parseInt(fields[index]));
        //                        break;
        //
        //                    case ENUMERATION_LIST_TYPE:
        //                    case INTEGER_LIST_TYPE:
        //                        ((CoalesceIntegerListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case GUID_TYPE:
        //                        ((CoalesceGUIDField)cr.getFieldByName(column)).setValue(GUIDHelper.getGuid(fields[index]));
        //                        break;
        //
        //                    case GUID_LIST_TYPE:
        //                        ((CoalesceGUIDListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case GEOCOORDINATE_TYPE:
        //                        ((CoalesceCoordinateField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case GEOCOORDINATE_LIST_TYPE:
        //                        ((CoalesceCoordinateListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case LINE_STRING_TYPE:
        //                        ((CoalesceLineStringField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case POLYGON_TYPE:
        //                        ((CoalescePolygonField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case CIRCLE_TYPE:
        //                        ((CoalesceCircleField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;

        case DOUBLE_TYPE:
            ((CoalesceDoubleField) cr.getFieldByName(column)).setValue(Double.parseDouble(fields[index]));
            break;

        //                    case DOUBLE_LIST_TYPE:
        //                        ((CoalesceDoubleListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case FLOAT_TYPE:
        //                        ((CoalesceFloatField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case FLOAT_LIST_TYPE:
        //                        ((CoalesceFloatListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;
        //
        //                    case LONG_TYPE:
        //                        ((CoalesceLongField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;

        //                    case LONG_LIST_TYPE:
        //                        ((CoalesceLongListField)cr.getFieldByName(column)).setValue(fields[index]);
        //                        break;

        default:
            break;
        }
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        if (params.containsKey("json"))
        {
            String jsonString = params.get("json");

            try
            {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(jsonString);
                this.templatesArray = (JSONArray) json.get("templates");
                entityTemplates = new HashMap<>();
                for (Object aTemplatesArray : this.templatesArray)
                {
                    JSONObject template = (JSONObject) aTemplatesArray;
                    String templateKey = (String) template.get("templateKey");
                    System.out.println(templateKey);

                    CoalesceEntityTemplate entityTemplate = this.framework.getCoalesceEntityTemplate(templateKey);
                    entityTemplates.put(templateKey, entityTemplate);
                    CoalesceTemplateUtil.addTemplates(entityTemplate);

                }
            }
            catch (ParseException | CoalescePersistorException e)
            {
                e.printStackTrace();

            }
        }
        if (params.containsKey("split"))
        {
            this.separator = params.get("split");
        }

        System.out.println("RECORDSETS WIT: " + CoalesceTemplateUtil.getRecordsets().size());
    }

}