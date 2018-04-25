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

package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.exim.impl.JsonFullEximImpl;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.support.AbstractClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchTemplatePersister implements ICoalesceTemplatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTemplatePersister.class);

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {

    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {

    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {

    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        return null;
    }

    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        JsonFullEximImpl converter = new JsonFullEximImpl();

        for (CoalesceEntityTemplate template : templates)
        {
            try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
            {
                AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());
                XmlMapper mapper = new XmlMapper();
                JsonNode node = mapper.readTree(template.toXml());

                ObjectMapper jsonMapper = new ObjectMapper();
                String json = jsonMapper.writeValueAsString(node);

                // persist entity template to ElasticSearch
                //String fakeJson = "{\"template\": \"te*\",\"classname\":\"com.incadencecorp.coalesce.framework.datamodel.TestEntity\",\"datecreated\":\"\",\"entityid\":\"\",\"entityidtype\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"UNIT_TEST\",\"source\":\"DSS\",\"title\":\"\",\"version\":1,\"linkagesection\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"Linkages\"},\"section\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"test section\",\"noindex\":\"false\",\"recordset\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"maxrecords\":\"0\",\"minrecords\":\"0\",\"name\":\"test1\",\"fielddefinition\":{\"datatype\":\"booleanlist\",\"datecreated\":\"\",\"defaultclassificationmarking\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"booleanlist\"}}}}";

                coalesceTemplateToESTemplate(template, client);
                //client.admin().indices().preparePutTemplate(template.getName().toLowerCase()).setSource(coalesceTemplateToESTemplate(template, client)).get();
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                                   "Template",
                                                                   template.getKey(),
                                                                   e.getMessage()), e);
            }
            catch (Exception e)
            {
                LOGGER.warn("Failed to register templates: " + e.getMessage());
            }
        }
    }

    protected Map<String, Object> coalesceTemplateToESTemplate(CoalesceEntityTemplate template, AbstractClient client)
    {
        Map<String, Object> esTemplate = new HashMap<>();
        Map<String, Object> mappingMap = new HashMap<>();
        Map<String, Object> propertiesMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        ElasticSearchMapperImpl mapper = new ElasticSearchMapperImpl();

        CoalesceTemplateUtil.addTemplates(template);

        CoalesceTemplateUtil.getRecordsets(template.getKey());

        Map<String, ECoalesceFieldDataTypes> typeMap = CoalesceTemplateUtil.getTemplateDataTypes(template.getKey());

        esTemplate.put("template", "*");

        mappingMap.put("properties", propertiesMap);

        for (String typeName : typeMap.keySet())
        {
            if (mapper.mapToString(typeMap.get(typeName)) != null)
            {
                innerMap = new HashMap<String, Object>();

                innerMap.put("type", mapper.mapToString(typeMap.get(typeName)));
                propertiesMap.put(typeName.replace(template.getName(), ""), innerMap);
            }
        }
        
        /*
        if(ElasticSearchSettings.getStoreXML())
        {
            innerMap = new HashMap<String, Object>();

            innerMap.put("enabled", false);
            propertiesMap.put("entityXML", innerMap);
        }
        */

        client.admin().indices().preparePutTemplate("coalesce-" + template.getName().toLowerCase()).setSource(esTemplate).addMapping(
                "mapping",
                mappingMap).get();

        LOGGER.debug("Saved template named: " + template.getName());

        return esTemplate;
    }
}
