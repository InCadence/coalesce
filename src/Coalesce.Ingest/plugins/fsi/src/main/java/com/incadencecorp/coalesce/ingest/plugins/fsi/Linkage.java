package com.incadencecorp.coalesce.ingest.plugins.fsi;

public class Linkage {
    private String entity1;
    private String linkType;
    private String entity2;

    public String getEntity1()
    {
        return entity1;
    }

    public void setEntity1(String entity1)
    {
        this.entity1 = entity1;
    }

    public String getLinkType()
    {
        return linkType;
    }

    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }

    public String getEntity2()
    {
        return entity2;
    }

    public void setEntity2(String entity2)
    {
        this.entity2 = entity2;
    }
}
