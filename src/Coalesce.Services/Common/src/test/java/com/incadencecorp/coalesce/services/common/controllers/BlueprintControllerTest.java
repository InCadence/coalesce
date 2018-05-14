package com.incadencecorp.coalesce.services.common.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Vertex;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Map;

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

}
