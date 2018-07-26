package com.incadencecorp.coalesce.services.common.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Vertex;
import org.json.JSONArray;
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

    @Test public void testGetXML() throws Exception {
        BlueprintController controller = new BlueprintController();
        controller.setDirectory(Paths.get("src", "test", "resources").toString());

        String xml = controller.getXML("rest-blueprint.xml", "someRestService");
        System.out.println(xml);
        Assert.assertNotNull(xml);
    }

      @Test public void testRemoveBean() throws Exception {
        BlueprintController controller = new BlueprintController();
        controller.setDirectory(Paths.get("src", "test", "resources").toString());

        JSONObject json = new JSONObject();
        String xml = "<bean id=\"test\" class=\"com.incadencecorp.coalesce.framework.persistance.elasticsearch.Tester\">\n"
                + "<argument>\n" + "<map>\n" + "<entry key=\"elastic.isAuthoritative\" value=\"true\"/>\n"
                + "<entry key=\"elastic.clustername\" value=\"elasticsearch\"/>\n"
                + "<entry key=\"elastic.hosts\" value=\"localhost:9300\"/>\n"
                + "<entry key=\"elastic.http.host\" value=\"localhost\"/>\n"
                + "<entry key=\"elastic.http.port\" value=\"9200\"/>\n"
                + "<entry key=\"elastic.datastore.cache.enabled\" value=\"false\"/>\n"
                + "<entry key=\"ssl.enabled\" value=\"false\"/>\n"
                + "<entry key=\"ssl.reject_unauthorized\" value=\"true\"/>\n" + "</map>\n" + "</argument>\n" + "</bean>";
        json.append("xml", xml);
        String help = "";
        json.accumulate("oldId", help);

        String filename = "rest-blueprint.xml";
        controller.editBlueprint(filename, json.toString());

        //Remove node with no "links"
        String rem = "";
        String id = "test";
        JSONObject remove = new JSONObject();
        remove.append("xml", rem);
        remove.accumulate("oldId", id);

        controller.removeBean(filename, remove.toString());
      }
}






