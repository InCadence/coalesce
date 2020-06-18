package com.incadencecorp.coalesce.services.search.service.data.model;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.services.api.search.SortByType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Model used for submitting queries. Specify either a group of criteria or a CQL statement.
 */
public class SearchQuery {

    @Schema(description = "Unique Identifier")
    private String key;
    @Schema(description = "Specifies the name of the template being targeted by this query.")
    private String type;
    @Schema(description = "(Optional) Criteria groups")
    private SearchGroup group;
    @Schema(description = "(Optional) If specified will be used over groups.")
    private String cql;
    private int pageSize;
    private int pageNumber;
    private List<SortByType> sortBy;
    @Schema(description = "Property names that should be returned from the query.")
    private List<String> propertyNames;
    @Schema(description = "Required capabilities of this query which will determine which datastore is targeted.")
    private final EnumSet<EPersistorCapabilities> capabilities = EnumSet.of(EPersistorCapabilities.SEARCH);
    @Schema(description = "Whether or not to restrict the results to entities created by the current user.")
    private boolean isUserLimited;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SearchGroup getGroup()
    {
        return group;
    }

    public void setGroup(SearchGroup group)
    {
        this.group = group;
    }

    public String getCql()
    {
        return cql;
    }

    public void setCql(String cql)
    {
        this.cql = cql;
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

    public boolean isUserLimited()
    {
        return isUserLimited;
    }

    public void setUserLimited(boolean userLimited)
    {
        isUserLimited = userLimited;
    }
}
