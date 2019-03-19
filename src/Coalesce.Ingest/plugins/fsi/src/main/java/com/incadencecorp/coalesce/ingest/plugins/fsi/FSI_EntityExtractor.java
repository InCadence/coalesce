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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.ingest.api.IExtractor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * methods that must be called before the extract method:
 * setFramework
 */
public class FSI_EntityExtractor extends CoalesceComponentImpl implements IExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FSI_EntityExtractor.class);

    public static final String PARAM_JSON = "json";
    public static final String PARAM_SPLIT = "split";

    private String separator;
    private final Map<String, CoalesceEntityTemplate> entityTemplates = new HashMap<>(); //uri to CoalesceEntityTemplate
    private TemplateJson configuration;

    @Override
    public void setFramework(CoalesceSearchFramework framework)
    {
    } //does nothing

    @Override
    public List<CoalesceEntityTemplate> getTemplatesUsed()
    {
        return new ArrayList<>();
    }

    @Override
    public List<CoalesceEntity> extract(String filename, String line) throws CoalesceException
    {

        List<CoalesceEntity> entities = new ArrayList<>();
        String[] fields = line.split(this.separator);

        for (Template template : configuration.getTemplates())
        {
            String templateUri = template.getTemplateUri();

            Record record = template.getRecord();
            String recordName = record.getName();

            HashMap<String, String> fieldsMap = record.getFields();

            CoalesceEntityTemplate entityTemplate = entityTemplates.get(templateUri);
            CoalesceEntity entity = entityTemplate.createNewEntity();

            CoalesceRecordset rs = entity.getCoalesceRecordsetForNamePath(entity.getName(),
                                                                          entity.getSectionsAsList().get(0).getName(),
                                                                          recordName);

            if (rs == null)
            {
                throw new CoalesceException(String.format(CoalesceErrors.NOT_FOUND, "Recordset", recordName));
            }

            CoalesceRecord cr = rs.getHasRecords() ? rs.getItem(0) : rs.addNew();

            String templateKey = entityTemplate.getKey();
            Map<String, ECoalesceFieldDataTypes> typesMap = CoalesceTemplateUtil.getTemplateDataTypes(templateKey);
            Object[] fieldsMapKeys = fieldsMap.keySet().toArray();
            for (Object fieldsMapKey : fieldsMapKeys)
            {
                String fieldsIndex = (String) fieldsMapKey;
                fieldsIndex = fieldsIndex.replace("\"", "");
                String column = fieldsMap.get(fieldsIndex);

                ECoalesceFieldDataTypes type;

                int index = Integer.parseInt(fieldsIndex);

                type = typesMap.get(recordName + "." + column.toLowerCase());

                if (type == null)
                {
                    throw new CoalesceException((String) typesMap.keySet().toArray()[index]);
                    //                    throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                    //                                                              fieldsIndex,
                    //                                                              "Unknown Type"));
                }

                setFieldValue(type, cr, column, fields, index);

            }
            entities.add(entity);
        }

        if (configuration.getLinkages() != null && !configuration.getLinkages().isEmpty())
        {
            //set linkages if any now
            for (Linkage link : configuration.getLinkages())
            {
                String templateUri1 = configuration.getTemplates().get(Integer.parseInt(link.getEntity1())).getTemplateUri();
                String templateClassname1 = this.entityTemplates.get(templateUri1).getClassName();
                String templateUri2 = configuration.getTemplates().get(Integer.parseInt(link.getEntity2())).getTemplateUri();
                String templateClassname2 = this.entityTemplates.get(templateUri2).getClassName();

                for (CoalesceEntity entity1 : entities)
                {
                    if (templateClassname1.equals(entity1.getClassName()))
                    {
                        for (CoalesceEntity entity2 : entities)
                        {
                            if (!entity1.getKey().equals(entity2.getKey()))
                            {
                                if (templateClassname2.equals(entity2.getClassName()))
                                {
                                    EntityLinkHelper.linkEntitiesUniDirectional(entity1,
                                                                                ELinkTypes.getTypeForLabel(link.getLinkType()),
                                                                                entity2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return entities;
    }

    //TODO: Parsing Polygons, Coordinates, Lists,
    private void setFieldValue(ECoalesceFieldDataTypes type, CoalesceRecord cr, String column, String[] fields, int index)
    {
        String value = fields[index].replace("\"", "");
        switch (type)
        {
        case STRING_TYPE:
        case URI_TYPE:
            ((CoalesceStringField) cr.getFieldByName(column)).setValue(value);
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
        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            ((CoalesceIntegerField) cr.getFieldByName(column)).setValue(Integer.parseInt(value));
            break;
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
            ((CoalesceDoubleField) cr.getFieldByName(column)).setValue(Double.parseDouble(value));
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

        if (params.containsKey(PARAM_JSON))
        {
            String jsonString = params.get(PARAM_JSON);

            try
            {
                ObjectMapper mapper = new ObjectMapper();
                configuration = mapper.readValue(jsonString, TemplateJson.class);

                for (Template template : configuration.getTemplates())
                {
                    String xml = IOUtils.toString(new URI(template.getTemplateUri()), StandardCharsets.UTF_8);
                    CoalesceEntityTemplate entityTemplate = CoalesceEntityTemplate.create(xml);

                    entityTemplates.put(template.getTemplateUri(), entityTemplate);

                    CoalesceTemplateUtil.addTemplates(entityTemplate);
                }
            }
            catch (IOException | URISyntaxException | CoalesceException e)
            {
                throw new RuntimeException(e);
            }
        }

        this.separator = params.getOrDefault(PARAM_SPLIT, ",");

    }

}
