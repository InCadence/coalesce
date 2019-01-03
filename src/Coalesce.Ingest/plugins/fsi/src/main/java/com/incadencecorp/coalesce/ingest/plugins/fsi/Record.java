package com.incadencecorp.coalesce.ingest.plugins.fsi;

import java.util.HashMap;

public class Record {
    private String name;
    private HashMap<String, String> fields;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public HashMap<String, String> getFields()
    {
        return fields;
    }

    public void setFields(HashMap<String, String> fields)
    {
        this.fields = fields;
    }
}
