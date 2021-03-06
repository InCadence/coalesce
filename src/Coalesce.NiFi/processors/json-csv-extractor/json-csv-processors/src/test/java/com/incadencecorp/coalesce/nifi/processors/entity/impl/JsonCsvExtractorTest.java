package com.incadencecorp.coalesce.nifi.processors.entity.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.Template;
import com.incadencecorp.coalesce.ingest.plugins.fsi.TemplateJson;
import com.incadencecorp.coalesce.nifi.processors.entity.csv_extractor.JsonCsvExtractor;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonCsvExtractorTest {

    private static final Path TEST_RESOURCES = Paths.get("src", "test", "resources");

    @Test
    public void testOnTrigger() throws Exception
    {

        final String JSON_FORMAT = "format1.json";
        TestRunner jce = TestRunners.newTestRunner(new JsonCsvExtractor());
        String jsonString = new String(Files.readAllBytes(TEST_RESOURCES.resolve(JSON_FORMAT).toAbsolutePath()));

        ObjectMapper mapper = new ObjectMapper();
        TemplateJson json = mapper.readValue(jsonString, TemplateJson.class);
        List<Template> templates = json.getTemplates();

        // Replace relative path with absolute
        for (Template template : json.getTemplates())
        {
            String newUri = Paths.get(".", new URI(template.getTemplateUri()).getPath()).toUri().toString();
            template.setTemplateUri(newUri);
        }

        jsonString = mapper.writeValueAsString(json);
        jce.setProperty(JsonCsvExtractor.TEMPLATE_JSON, jsonString);
        jce.setProperty(JsonCsvExtractor.CSV_SEPARATOR, ",");
        jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, DerbyPersistor.class.getName());
        jce.setProperty(JsonCsvExtractor.PARAM_HAS_HEADERS, "true");
        //jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor");

        MockFlowFile flowfile = new MockFlowFile(1);

        Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "fsi-2018.csv");

        attrs.put("absolute.path", TEST_RESOURCES.toAbsolutePath().toString());
        //        attrs.put("elastic.clustername", "elasticsearch");
        //        attrs.put("elastic.datastore.cache.enabled", "false");
        //        attrs.put("elastic.hosts", "localhost:9300");
        //        attrs.put("elastic.http.host", "localhost");
        //        attrs.put("elastic.http.port", "9200");
        //        attrs.put("elastic.isAuthoritative", "true");
        //        attrs.put("ssl.enabled", "false");
        //        attrs.put("ssl.reject_unauthorized", "true");

        flowfile.putAttributes(attrs);

        jce.enqueue(flowfile);
        jce.run(1);

        List<MockFlowFile> results = jce.getFlowFilesForRelationship(JsonCsvExtractor.SUCCESS);
        assertEquals(1, results.size());

    }
}
