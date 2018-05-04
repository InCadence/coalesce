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

package com.incadencecorp.coalesce.services.network.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.services.network.api.INetworkController;
import com.incadencecorp.coalesce.services.network.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.network.api.datamodel.graphson.Vertex;
import com.incadencecorp.coalesce.services.network.api.datamodel.yaml.Hosts;
import com.incadencecorp.coalesce.services.network.api.datamodel.yaml.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class NetworkController extends CoalesceComponentImpl implements INetworkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkController.class);
    private Path root = Paths.get("config");

    public Graph getNetworkDiagram() throws RemoteException
    {
        Graph graph = new Graph();

        try
        {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            Roles roles = mapper.readValue(new File(root.resolve("roles.yaml").toString()), Roles.class);
            Hosts hosts = mapper.readValue(new File(root.resolve("hosts.yaml").toString()), Hosts.class);

            for (Map.Entry<String, List<String>> role : roles.entrySet())
            {
                LOGGER.info("Role: {}", role.getKey());
                Vertex hostVertex = graph.addVertex(role.getKey());
                hostVertex.setType("role");

                for (String node : role.getValue())
                {
                    List<String> ips = hosts.getHosts().get(node).getIPs();
                    LOGGER.info("\t{} : {}", node, ips);

                    Vertex nodeVertex = graph.addVertex(node);
                    nodeVertex.put("ips", ips);
                    nodeVertex.setType("node");

                    graph.addEdge(nodeVertex, "running", hostVertex);
                }
            }
        }
        catch (IOException e)
        {
            throw new RemoteException(e.getMessage(), e);
        }

        return graph;
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        root = Paths.get(params.getOrDefault(CoalesceParameters.PARAM_DIRECTORY, "config"));
    }
}
