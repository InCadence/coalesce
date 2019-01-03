package com.incadencecorp.coalesce.ingest.plugins.fsi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Pojo {

    List<Pojo2> templates;
    String templateKey;

    public String getTemplateKey()
    {
        return templateKey;
    }

    public void setTemplateKey(String templateKey)
    {
        this.templateKey = templateKey;
    }

    private class Pojo2 {
        String templateUri;

    }
}
