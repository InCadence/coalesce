package com.incadencecorp.coalesce.services.common.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Vertex;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

public class BlueprintControllerTest {

    /**
     * This test was used for troubleshooting the blueprint controller however it does not do any validation.
     */
    @Test
    public void testBlueprintLoad() throws Exception
    {
        BlueprintController controller = new BlueprintController();

        controller.setDirectory(Paths.get("src", "test", "resources").toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        Graph result = controller.getBlueprint("rest-blueprint.xml");

        System.out.println(mapper.writeValueAsString(result));

        for (String filename : controller.getBlueprints())
        {
            System.out.println(filename);
        }

        for (Vertex vertex : result.getVertices())
        {
            for (Map.Entry<String, Object> entry : vertex.entrySet())
            {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }

    }

    @Test public void testEditBlueprint() throws Exception {
        BlueprintController controller = new BlueprintController();
        controller.setDirectory(Paths.get("src", "test", "resources").toString());


        String string = "{ \"bean\": { \"$\": { \"id\": \"test\", \"class\": \"com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersiseter\" }, \"argument\": { \"map\": { \"entry\": [ { \"$\": { \"key\": \"elastic.isAuthoritative\", \"value\": \"true\" } }, { \"$\": { \"key\": \"elastic.clustername\", \"value\": \"elasticsearch\" } }, { \"$\": { \"key\": \"elastic.hosts\", \"value\": \"localhost:9300\" } }, { \"$\": { \"key\": \"elastic.http.host\", \"value\": \"localhost\" } }, { \"$\": { \"key\": \"elastic.http.port\", \"value\": \"9200\" } }, { \"$\": { \"key\": \"ssl.enabled\", \"value\": \"false\" } }, { \"$\": { \"key\": \"ssl.reject_unauthorized\", \"value\": \"true\" } } ] } } } }";
        JSONObject json = new JSONObject(string);
        String word =controller.jsonToXML(json);
        controller.editBlueprint("service-blueprint.xml", string);

    }

}
