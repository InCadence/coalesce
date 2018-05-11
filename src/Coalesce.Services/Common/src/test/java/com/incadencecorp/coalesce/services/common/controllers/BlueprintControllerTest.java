package com.incadencecorp.coalesce.services.common.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import org.junit.Test;

import java.nio.file.Paths;

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

        Graph result = controller.getBlueprint("service-blueprint.xml");

        System.out.println(mapper.writeValueAsString(result));

        for (String filename : controller.getBlueprints())
        {
            System.out.println(filename);
        }

    }

}
