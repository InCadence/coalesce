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
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.ingest.api.IExtractor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private List<Template> templatesArray;
    private List<Linkage> linkagesArray;

    @Override
    public void setFramework(CoalesceSearchFramework framework) {} //does nothing

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

        for (Template template : this.templatesArray)
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
                String column =  fieldsMap.get(fieldsIndex);

                ECoalesceFieldDataTypes type;

                int index = Integer.parseInt(fieldsIndex);

                type = typesMap.get(recordName + "." + column.toLowerCase());

                if (type == null)
                {
                    throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                                                              fieldsMapKey,
                                                              "Unknown Type"));
                }

                setFieldValue(type, cr, column, fields, index);

            }
            entities.add(entity);
        }

        //set linkages if any now
        for(int i = 0; i < entities.size(); i++) {
            if(this.linkagesArray == null || this.linkagesArray.size() == 0) {
                break;
            }
            for(Linkage link : this.linkagesArray) {
                String templateUri1 = this.templatesArray.get(Integer.parseInt(link.getEntity1())).getTemplateUri();
                String templateKey1 = this.entityTemplates.get(templateUri1).getKey();

                if(templateKey1.equals(entities.get(i).createNewEntityTemplate().getKey())) {
                    String templateUri2 = this.templatesArray.get(Integer.parseInt(link.getEntity2())).getTemplateUri();
                    String templateKey2 = this.entityTemplates.get(templateUri2).getKey();
                    for(int j = 0; j < entities.size(); j++) {
                        if(j != i) {
                            if(templateKey2.equals(entities.get(j).createNewEntityTemplate().getKey())) {
                                EntityLinkHelper.linkEntitiesUniDirectional(entities.get(i), ELinkTypes.getTypeForLabel(link.getLinkType()), entities.get(j));
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
                ((CoalesceIntegerField)cr.getFieldByName(column)).setValue(Integer.parseInt(value));
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
                // TODO Remove the JSON parser and instead create a POJO and use ObjectMapper to deserialize

                ObjectMapper mapper = new ObjectMapper();
                TemplateJson json = mapper.readValue(jsonString, TemplateJson.class);
                this.templatesArray = json.getTemplates();
                this.linkagesArray = json.getLinkages();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        this.separator = params.getOrDefault(PARAM_SPLIT, ",");

    }

    @Deprecated //TODO: Remove because it's in the JsonCsvExtractor class
    private String getTemplateXml(String templateUri) {
        try {
            URI uri = new URI(templateUri);
            switch(uri.getScheme()) {
            case "file":
                return IOUtils.toString(uri, StandardCharsets.UTF_8);
            case "http":
            case "https":
                HttpResponse response = getResponse(new HttpGet(templateUri));
                switch(response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    return EntityUtils.toString(response.getEntity());
                default:
                    break;
                }
            }
        }

        catch(URISyntaxException e) {
            LOGGER.error("URISyntaxException: ", e);
        }
        catch(IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return "ERROR";
    }

    // TODO This was a copy and paste need to refactor to a common library
    private HttpResponse getResponse(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            response = client.execute(request);
        }
        catch(IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return response;
    }

    @Deprecated
    public void setTemplates(HashMap<String, String> templates) {
        //this.entityTemplates = new HashMap<>();
        for (Object oTemplateUri : templates.keySet().toArray())
        {
            String templateUri = (String) oTemplateUri;
            String templateXml = templates.get(templateUri);
            try {
                CoalesceEntityTemplate entityTemplate = CoalesceEntityTemplate.create(templateXml);
                entityTemplates.put(templateUri, entityTemplate);
                CoalesceTemplateUtil.addTemplates(entityTemplate);
            }
            catch(CoalesceException e) {
                LOGGER.debug("Template Specified: {}", templateUri);
            }


        }
    }

}
