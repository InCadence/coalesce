package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

public class ElasticSearchMetadata {
	
    private String name;
    private String typeName;
    private String dataType;
	
	public ElasticSearchMetadata(String name, String typeName, String dataType)
    {
        this.name = name;
        this.typeName = typeName;
        this.dataType = dataType;
    }

    public String getName()
    {
        return name;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public String getDataType()
    {
        return dataType;
    }

    public String toString()
    {
        return name + ":" + typeName;
    }
}
