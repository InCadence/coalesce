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

package com.incadencecorp.coalesce.services.network.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.network.api.datamodel.yaml.Hosts;
import com.incadencecorp.coalesce.services.network.api.datamodel.yaml.Node;
import com.incadencecorp.coalesce.services.network.api.datamodel.yaml.Roles;
import com.incadencecorp.coalesce.services.network.controller.impl.NetworkController;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class NetworkControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkControllerTest.class);
    private static final Path ROOT = Paths.get("src", "test", "resources");

    @Test
    public void testRoles() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        Roles roles = mapper.readValue(new File(ROOT.resolve("roles.yaml").toString()), Roles.class);

        for (Map.Entry<String, List<String>> role : roles.entrySet())
        {
            LOGGER.info("Role: {}", role.getKey());
            for (String node : role.getValue())
            {
                LOGGER.info("\t{}", node);
            }
        }
    }

    @Test
    public void testHosts() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        Hosts hosts = mapper.readValue(new File(ROOT.resolve("hosts.yaml").toString()), Hosts.class);

        for (Map.Entry<String, Node> role : hosts.getHosts().entrySet())
        {
            LOGGER.info("Host: {} {}", role.getKey(), role.getValue().getIPs());
        }
    }

    @Test
    public void testController() throws Exception
    {
        NetworkController controller = new NetworkController();
        controller.setProperties(Collections.singletonMap(CoalesceParameters.PARAM_DIRECTORY, ROOT.toString()));

        Graph graph = controller.getNetworkDiagram();
        ObjectMapper mapper = new ObjectMapper();
        LOGGER.info(mapper.writeValueAsString(graph));
    }

/*
    @Test
    public void testGraphJSON() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        Roles roles = mapper.readValue(new File(ROOT.resolve("roles.yaml").toString()), Roles.class);
        Hosts hosts = mapper.readValue(new File(ROOT.resolve("hosts.yaml").toString()), Hosts.class);

        Graph graph = new TinkerGraph();

        Map<String, Vertex> vertices = new HashMap<>();

        for (Map.Entry<String, List<String>> role : roles.entrySet())
        {
            Vertex hostVertex;

            if (!vertices.containsKey(role.getKey()))
            {
                LOGGER.info("Role: {}", role.getKey());
                hostVertex = GraphHelper.addVertex(graph, role.getKey());

                vertices.put(role.getKey(), hostVertex);
            }
            else
            {
                hostVertex = vertices.get(role.getKey());
            }

            for (String node : role.getValue())
            {
                Vertex nodeVertex;

                if (!vertices.containsKey(node))
                {
                    List<String> ips = hosts.getHosts().get(node).getIPs();
                    LOGGER.info("\t{} : {}", node, ips);

                    nodeVertex = GraphHelper.addVertex(graph, node);
                    nodeVertex.setProperty("ips", ips);

                    vertices.put(node, nodeVertex);
                }
                else
                {
                    nodeVertex = vertices.get(node);
                }

                GraphHelper.addEdge(graph, null, nodeVertex, hostVertex, "running");
            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GraphSONWriter.outputGraph(graph, stream);

        LOGGER.info("{}", new String(stream.toByteArray()));
    }
*/
}
