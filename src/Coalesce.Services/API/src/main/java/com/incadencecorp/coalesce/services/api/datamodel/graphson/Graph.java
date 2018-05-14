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

package com.incadencecorp.coalesce.services.api.datamodel.graphson;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Derek Clemenzi
 */
public class Graph {

    private String mode = "NORMAL";
    private List<Vertex> vertices = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Integer counter = 0;

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @JsonProperty("nodes")
    public List<Vertex> getVertices()
    {
        return vertices;
    }

    public Vertex addVertex(String id)
    {
        return this.addVertex(id, "vertex");
    }

    public Vertex addVertex(String id, String type)
    {
        for (Vertex vertex : vertices)
        {
            if (vertex.getId().equalsIgnoreCase(id))
            {
                return vertex;
            }
        }

        Vertex vertex = new Vertex();
        vertex.setId(id);
        vertex.setType(type);

        vertices.add(vertex);

        return vertex;
    }

    @JsonProperty("nodes")
    public void setVertices(List<Vertex> vertices)
    {
        this.vertices = vertices;
    }

    @JsonProperty("links")
    public List<Edge> getEdges()
    {
        return edges;
    }

    public Edge addEdge(String out, String label, String in)
    {
        return this.addEdge(null, out, label, in, null);
    }

    public Edge addEdge(Vertex out, String label, Vertex in)
    {
        return this.addEdge(out.getId(), label, in.getId());
    }

    public Edge addEdge(String id, Vertex out, String label, Vertex in, String type)
    {
        return this.addEdge(id, out.getId(), label, in.getId(), type);
    }

    public Edge addEdge(String id, String out, String label, String in, String type)
    {
        if (id != null)
        {
            for (Edge edge : edges)
            {
                if (edge.getId().equalsIgnoreCase(id))
                {
                    return edge;
                }
            }
        }

        Edge edge = new Edge();
        edge.setId(id != null ? id : (counter++).toString());
        edge.setType(type);
        edge.setOutV(out);
        edge.setLabel(label);
        edge.setInV(in);

        edges.add(edge);

        return edge;
    }

    @JsonProperty("links")
    public void setEdges(List<Edge> edges)
    {
        this.edges = edges;
    }
}
