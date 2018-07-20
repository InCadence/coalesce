package com.incadencecorp.coalesce.services.search.service.data.model;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.services.api.search.SortByType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SearchQuery {

    private SearchGroup group;
    private int pageSize;
    private int pageNumber;
    private List<SortByType> sortBy;
    private List<String> propertyNames;
    private final EnumSet<EPersistorCapabilities> capabilities = EnumSet.of(EPersistorCapabilities.SEARCH);

    public SearchGroup getGroup()
    {
        return group;
    }

    public void setGroup(SearchGroup group)
    {
        this.group = group;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public List<SortByType> getSortBy()
    {
        return sortBy != null ? sortBy : new ArrayList<SortByType>();
    }

    public void setSortBy(List<SortByType> sortBy)
    {
        this.sortBy = sortBy;
    }

    public List<String> getPropertyNames()
    {
        return propertyNames != null ? propertyNames : new ArrayList<String>();
    }

    public void setPropertyNames(List<String> propertyNames)
    {
        this.propertyNames = propertyNames;
    }

    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(EnumSet<EPersistorCapabilities> values)
    {
        this.capabilities.clear();
        this.capabilities.addAll(values);
    }

}
