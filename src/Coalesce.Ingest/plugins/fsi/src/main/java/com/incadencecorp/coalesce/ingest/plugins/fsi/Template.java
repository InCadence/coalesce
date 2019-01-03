package com.incadencecorp.coalesce.ingest.plugins.fsi;

public class Template {
    private String templateUri;
    private Record record;

    public String getTemplateUri()
    {
        return templateUri;
    }

    public void setTemplateUri(String templateUri)
    {
        this.templateUri = templateUri;
    }

    public Record getRecord()
    {
        return record;
    }

    public void setRecord(Record record)
    {
        this.record = record;
    }
}
