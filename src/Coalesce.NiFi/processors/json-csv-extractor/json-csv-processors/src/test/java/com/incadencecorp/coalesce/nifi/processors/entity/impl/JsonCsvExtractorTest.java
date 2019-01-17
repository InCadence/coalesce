package com.incadencecorp.coalesce.nifi.processors.entity.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.ingest.plugins.fsi.Template;
import com.incadencecorp.coalesce.ingest.plugins.fsi.TemplateJson;
import com.incadencecorp.coalesce.nifi.processors.entity.csv_extractor.JsonCsvExtractor;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonCsvExtractorTest {

    @Test
    public void testOnTrigger() throws Exception {

        final String JSON_FORMAT = "format.json";
        TestRunner jce = TestRunners.newTestRunner(new JsonCsvExtractor());
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String jsonString = new String(Files.readAllBytes(Paths.get(rootPath,"src", "test", "resources", "format.json")));


        ObjectMapper mapper = new ObjectMapper();
        TemplateJson json = mapper.readValue(jsonString, TemplateJson.class);
        List<Template> templates = json.getTemplates();

        // Replace relative path with absolute
        String newUri = Paths.get(".", new URI(templates.get(0).getTemplateUri()).getPath()).toUri().toString();
        templates.get(0).setTemplateUri(newUri);


        jsonString = new JSONObject(json).toString();
        jce.setProperty(JsonCsvExtractor.TEMPLATE_JSON, jsonString);

        jce.setProperty(JsonCsvExtractor.CSV_SEPARATOR, ",");
        jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor");
        //jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor");
        MockFlowFile flowfile = new MockFlowFile(1);

        Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "testFile.csv");

        attrs.put("absolute.path", rootPath + "/src/test/resources/");
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