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
        TestRunner jce = TestRunners.newTestRunner(new JsonCsvExtractor());
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String json = new String(Files.readAllBytes(Paths.get(rootPath,"src", "test", "resources", "format.json")));

        // TODO Replace this with a Java POJO
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(json);
        JSONObject item = (JSONObject) ((JSONArray) root.get("templates")).get(0);

        // Replace relative path with absolute
        item.put("templateUri", Paths.get(".", new URI((String) item.get("templateUri")).getPath()).toUri().toString());

        json = root.toJSONString();

        jce.setProperty(JsonCsvExtractor.TEMPLATE_JSON, json);

        jce.setProperty(JsonCsvExtractor.CSV_SEPARATOR, ",");
        jce.setProperty(JsonCsvExtractor.PERSISTOR_CLASSPATHS, "com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor");

        MockFlowFile flowfile = new MockFlowFile(1);

        Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "testFile.csv");

        attrs.put("absolute.path", rootPath + "/src/test/resources/");

        flowfile.putAttributes(attrs);

        jce.enqueue(flowfile);
        jce.run(1);

        List<MockFlowFile> results = jce.getFlowFilesForRelationship(JsonCsvExtractor.SUCCESS);

        MockFlowFile result = results.get(0);


    }
}