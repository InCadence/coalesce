package com.incadencecorp.coalesce.services.search.service.data.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class SearchGroup {

    @Schema(description = "Operator to be used within this group ( AND | OR )")
    private String operator = "AND";
    @Schema(description = "List of criteria which are transformed into a WHERE (or the equivalent) clause.")
    private List<SearchCriteria> criteria;
    @Schema(description = "Sub groups allowing to complex queries.")
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
