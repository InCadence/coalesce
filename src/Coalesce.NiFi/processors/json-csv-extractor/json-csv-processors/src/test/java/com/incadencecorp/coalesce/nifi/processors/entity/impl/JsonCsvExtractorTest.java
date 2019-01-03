package com.incadencecorp.coalesce.nifi.processors.entity.impl;

import com.incadencecorp.coalesce.nifi.processors.entity.csv_extractor.JsonCsvExtractor;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonCsvExtractorTest {

    @Test
    public void testOnTrigger() throws Exception {

        final String JSON_FORMAT = "format1.json";
        TestRunner jce = TestRunners.newTestRunner(new JsonCsvExtractor());
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String json = new String(Files.readAllBytes(Paths.get(rootPath,"src", "test", "resources", "format1.json")));

        // TODO Replace this with a Java POJO
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(json);
        JSONObject item = (JSONObject) ((JSONArray) root.get("templates")).get(0);

        // Replace relative path with absolute
        //item.put("templateUri", Paths.get(".", new URI((String) item.get("templateUri")).getPath()).toUri().toString());

        json = root.toJSONString();

        jce.setProperty(JsonCsvExtractor.TEMPLATE_JSON, json);

        jce.setProperty(JsonCsvExtractor.CSV_SEPARATOR, ",");
        //jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor");
        jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor");
        MockFlowFile flowfile = new MockFlowFile(1);

        Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "fsi-2018.csv");

        attrs.put("absolute.path", rootPath + "/src/test/resources/");
        attrs.put("elastic.clustername", "elasticsearch");
        attrs.put("elastic.datastore.cache.enabled", "false");
        attrs.put("elastic.hosts", "localhost:9300");
        attrs.put("elastic.http.host", "localhost");
        attrs.put("elastic.http.port", "9200");
        attrs.put("elastic.isAuthoritative", "true");
        attrs.put("ssl.enabled", "false");
        attrs.put("ssl.reject_unauthorized", "true");

        flowfile.putAttributes(attrs);

        jce.enqueue(flowfile);
        jce.run(1);

        List<MockFlowFile> results = jce.getFlowFilesForRelationship(JsonCsvExtractor.SUCCESS);

        MockFlowFile result = results.get(0);
        attrs = result.getAttributes();
        for(String attr : attrs.keySet()) {
            System.out.println(attr);
            System.out.println(attrs.get(attr));
        }

    }
}