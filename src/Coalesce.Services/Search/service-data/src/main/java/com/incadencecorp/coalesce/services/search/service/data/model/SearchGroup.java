package com.incadencecorp.coalesce.services.search.service.data.model;

import java.util.ArrayList;
import java.util.List;

public class SearchGroup {

    private String booleanComparer = "AND";
    private List<SearchCriteria> criteria;
    private List<SearchGroup> groups;

    public String getBooleanComparer()
    {
        return booleanComparer;
    }

    public void setBooleanComparer(String booleanComparer)
    {
        this.booleanComparer = booleanComparer;
    }

    public List<SearchCriteria> getCriteria()
    {
        return criteria != null ? criteria : new ArrayList<SearchCriteria>();
    }

    public void setCriteria(List<SearchCriteria> criteria)
    {
        this.criteria = criteria;
    }

    public List<SearchGroup> getGroups()
    {
        return groups != null ? groups : new ArrayList<SearchGroup>();
    }

    public void setGroups(List<SearchGroup> groups)
    {
        this.groups = groups;
    }
}
