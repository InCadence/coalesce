package com.incadencecorp.coalesce.services.common.controllers.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * https://danielcaldas.github.io/react-d3-graph/docs/index.html
 */
public class GraphObj {

    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphLink> links = new ArrayList<>();

    public List<GraphNode> getNodes()
    {
        return nodes;
    }

    public void setNodes(List<GraphNode> nodes)
    {
        this.nodes = nodes;
    }

    public List<GraphLink> getLinks()
    {
        return links;
    }

    public void setLinks(List<GraphLink> links)
    {
        this.links = links;
    }

}
