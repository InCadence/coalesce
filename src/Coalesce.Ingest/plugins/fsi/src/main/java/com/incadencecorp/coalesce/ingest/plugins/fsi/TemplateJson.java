package com.incadencecorp.coalesce.ingest.plugins.fsi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateJson {

    private List<Template> templates;
    private List<Linkage> linkages;

    public List<Template> getTemplates()
    {
        return templates;
    }

    public void setTemplates(List<Template> templates)
    {
        this.templates = templates;
    }

    public List<Linkage> getLinkages()
    {
        return linkages;
    }

    public void setLinkages(List<Linkage> linkages)
    {
        this.linkages = linkages;
    }
}
