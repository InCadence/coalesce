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

package com.incadencecorp.coalesce.ingest.plugins.fsi.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.FSI_EntityExtractor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.Template;
import com.incadencecorp.coalesce.ingest.plugins.fsi.TemplateJson;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * These tests verify proper behaviour of the {@link FSI_EntityExtractor}.
 *
 * @author Derek Clemenzi
 */
public class FSI_EntityExtractorTest {

    /**
     * This test verifies that a CSV can be converted into a {@link TestEntity}
     */
    @Test
    public void testExtraction() throws Exception
    {
        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(new DerbyPersistor());
        framework.saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(new TestEntity()));

        String jsonString = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "format.json")));

        // TODO Replace this with a Java POJO
        ObjectMapper mapper = new ObjectMapper();
        TemplateJson json = mapper.readValue(jsonString, TemplateJson.class);
        List<Template> templates = json.getTemplates();
        String newUri = Paths.get(".", new URI(templates.get(0).getTemplateUri()).getPath()).toUri().toString();
        templates.get(0).setTemplateUri(newUri);

        Map<String, String> params = new HashMap<>();
        params.put(FSI_EntityExtractor.PARAM_JSON, new JSONObject(json).toString());
        params.put(FSI_EntityExtractor.PARAM_SPLIT, ",");

        FSI_EntityExtractor extractor = new FSI_EntityExtractor();
        extractor.setFramework(framework);
        HashMap<String, String> templatesMap = new HashMap<>();
        templatesMap.put(newUri, IOUtils.toString(new URI(newUri), StandardCharsets.UTF_8));
        extractor.setTemplates(templatesMap);
        extractor.setProperties(params);

        List<CoalesceEntity> entities = extractor.extract("unknown", "1,false,3.0");
        assertEquals(1, entities.size());
    }

}
