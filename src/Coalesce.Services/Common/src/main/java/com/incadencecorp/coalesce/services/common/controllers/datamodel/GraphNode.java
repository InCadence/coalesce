package com.incadencecorp.coalesce.services.common.controllers.datamodel;

import java.util.HashMap;
import java.util.Map;

public class GraphNode {

    private Map<String, String> settings = new HashMap<>();
    private String id;
    private String label;
    private String classname;
    private String type;
    private EGraphNodeType nodeType;
    private int size;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public Map<String, String> getSettings()
    {
        return settings;
    }

    public void setSettings(Map<String, String> settings)
    {
        this.settings = settings;
    }

    public String getClassname()
    {
        return classname;
    }

    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    public EGraphNodeType getNodeType()
    {
        return nodeType;
    }

    public void setNodeType(EGraphNodeType nodeType)
    {
        this.nodeType = nodeType;
    }
}
