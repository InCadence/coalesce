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

package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * These tests ensure proper behavior of the {@link PropertyController}.
 *
 * @author Derek Clemenzi
 */
public class PropertyControllerTest {

    private static final Path CONFIG_LOCATION = Paths.get("src", "test", "resources");
    private static final String TEST_JSON = "test";

    /**
     * Sets up the configuration path.
     */
    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY, CONFIG_LOCATION.toString());
    }

    /**
     * This test ensures that json properties can be created and loaded.
     */
    @Test
    public void testJsonConfiguration() throws Exception
    {
        PropertyController controller = new PropertyController();

        JSONArray groups = new JSONArray();
        JSONArray cards = new JSONArray();

        JSONObject config = new JSONObject();
        config.put("groups", groups);

        JSONObject group = new JSONObject();
        group.put("name", "Application");
        group.put("cards", cards);
        groups.put(group);

        JSONObject card = new JSONObject();
        card.put("name", "Search");
        card.put("description", "Hello World");
        card.put("img", "search.ico");
        card.put("url", "Search");
        cards.put(card);

        Files.deleteIfExists(CONFIG_LOCATION.resolve(TEST_JSON + ".json"));

        controller.setJsonConfiguration(TEST_JSON, config.toString());

        Assert.assertTrue(Files.exists(CONFIG_LOCATION.resolve(TEST_JSON + ".json")));

        JSONObject json = new JSONObject(controller.getJsonConfiguration(TEST_JSON));
        Assert.assertEquals(1, ((JSONArray) json.get("groups")).length());
        Assert.assertEquals(group.get("name"), ((JSONObject) ((JSONArray) json.get("groups")).get(0)).get("name"));

        Files.deleteIfExists(CONFIG_LOCATION.resolve(TEST_JSON + ".json"));
    }

}
