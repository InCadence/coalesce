package com.incadencecorp.coalesce.services.search.service.data.model;

import java.util.ArrayList;
import java.util.List;

public class SearchGroup {

    private String operator = "AND";
    private List<SearchCriteria> criteria;
    private List<SearchGroup> groups;

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String booleanComparer)
    {
        this.operator = booleanComparer;
    }

    public List<SearchCriteria> getCriteria()
    {
        return criteria != null ? criteria : new ArrayList<>();
    }

    public void setCriteria(List<SearchCriteria> criteria)
    {
        this.criteria = criteria;
    }

    public List<SearchGroup> getGroups()
    {
        return groups != null ? groups : new ArrayList<>();
    }

    public void setGroups(List<SearchGroup> groups)
    {
        this.groups = groups;
    }
}
