/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.services.search.service.data.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.SearchQueryCoalesceEntity;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.record.SearchQueryCoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.api.QueryHelper;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResult;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.api.search.SortByType;
import com.incadencecorp.coalesce.services.common.CoalesceRemoteException;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchGroup;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQueryDetails;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.CRS;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
import org.locationtech.jts.io.ParseException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.Operator;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.During;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.sql.rowset.CachedRowSet;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts a list of options into an OGC filter and passes it along to a search
 * persister returning the results to the caller.
 *
 * @author Derek Clemenzi
 */
public class SearchDataController extends SearchQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDataController.class);

    private static final String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();
    private static final PropertyName PROP_QUERY_SAVED = getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.SAVED);

    private final CoalesceSearchFramework framework;
    private final CoordinateReferenceSystem crs;
    private final FilterCapabilities capabilities;

    public SearchDataController(CoalesceSearchFramework framework)
    {
        super(framework);

        this.framework = framework;
        this.capabilities = framework.getCapabilities().getContents();

        try
        {
            this.crs = CRS.parseWKT(EPSG4326);
        }
        catch (FactoryException e)
        {
            throw new RuntimeException(e);
        }

    }

    /**
     * This simple interface ANDs all the provided criteria together and calls {@link #searchComplex(SearchQuery)}.
     *
     * @param options list of criteria
     * @return search results for the provided list of criteria.
     */
    public QueryResult search(List<SearchCriteria> options) throws RemoteException
    {
        List<String> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getName().getPropertyName());
        properties.add(CoalescePropertyFactory.getSource().getPropertyName());
        properties.add(CoalescePropertyFactory.getEntityTitle().getPropertyName());

        SearchGroup group = new SearchGroup();
        group.setOperator("AND");
        group.setCriteria(options);

        SearchQuery query = new SearchQuery();
        query.setPageSize(200);
        query.setPageNumber(1);
        query.setGroup(group);
        query.setPropertyNames(properties);
        query.setSortBy(Collections.emptyList());

        return searchComplex(query);
    }

    /**
     * Expands on
     *
     * @param searchQuery list of criteria
     * @return search results for the provided list of criteria.
     */
    public QueryResult searchComplex(SearchQuery searchQuery) throws RemoteException
    {
        Filter filter;

        if (StringHelper.isNullOrEmpty(searchQuery.getCql()))
        {
            filter = getFilter(FF, searchQuery.getGroup());
            searchQuery.setCql(CQL.toCQL(filter));
        }
        else
        {
            try
            {
                filter = CQL.toFilter(searchQuery.getCql());
            }
            catch (CQLException e)
            {
                throw new RemoteException("(FAILED) Parsing CQL", e);
            }
        }

        PropertyVisitor visitor = new PropertyVisitor();
        Set<String> props = visitor.getProperties(filter);

        // Don't persist queries that are only used to pull entity attributes
        boolean saveQuery =
                props.size() >= 2 || !props.contains(CoalescePropertyFactory.getEntityKey().getPropertyName().toLowerCase());

        if (searchQuery.isUserLimited())
        {
            filter = FF.and(filter, CoalescePropertyFactory.getCreatedBy(getPrincipal().getName()));
        }

        // Convert Sort
        SortBy[] sortBy = new SortBy[searchQuery.getSortBy().size()];
        for (int ii = 0; ii < searchQuery.getSortBy().size(); ii++)
        {
            SortByType sort = searchQuery.getSortBy().get(ii);

            sortBy[ii] = FF.sort(sort.getPropertyName(), SortOrder.valueOf(sort.getSortOrder().toString()));
        }

        LOGGER.info(searchQuery.getCql());

        Query query = new Query(searchQuery.getType());
        query.setFilter(filter);
        query.setPropertyNames(searchQuery.getPropertyNames());
        query.setSortBy(sortBy);
        query.setStartIndex(
                searchQuery.getPageNumber() > 0 ? (searchQuery.getPageNumber() - 1) * searchQuery.getPageSize() : 0);
        query.setMaxFeatures(searchQuery.getPageSize());

        if (searchQuery.getCapabilities().contains(EPersistorCapabilities.HIGHLIGHT))
        {
            QueryHelper.setHighlightingEnabled(query, true);
        }

        try
        {
            QueryResult result = createResponse(framework.searchBulk(searchQuery.getCapabilities(), query).get(0),
                                                searchQuery.getPropertyNames());

            if (saveQuery)
            {
                SearchQueryDetails details = new SearchQueryDetails();
                details.setQuery(searchQuery);

                save(details);
            }
            else
            {
                LOGGER.debug("Not persisting query: {}", searchQuery.getCql());
            }

            return result;
        }
        catch (CoalesceException | InterruptedException e)
        {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    public QueryResult searchOGC(QueryType searchQuery) throws RemoteException
    {
        // Convert Sort
        SortBy[] sortBy = new SortBy[searchQuery.getSortBy().size()];
        for (int ii = 0; ii < searchQuery.getSortBy().size(); ii++)
        {
            SortByType sort = searchQuery.getSortBy().get(ii);

            sortBy[ii] = FF.sort(sort.getPropertyName(), SortOrder.valueOf(sort.getSortOrder().toString()));
        }

        // Convert Filter
        Filter filter;
        try
        {
            filter = FilterUtil.fromXml(searchQuery.getFilter());
        }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
            throw new RemoteException("(FAILED) Parsing Filter", e);
        }

        // Execute Query
        Query query = new Query();
        query.setFilter(filter);
        query.setPropertyNames(searchQuery.getPropertyNames());
        query.setSortBy(sortBy);
        query.setStartIndex(searchQuery.getPageNumber());
        query.setMaxFeatures(searchQuery.getPageSize());

        try
        {
            return createResponse(framework.search(query), searchQuery.getPropertyNames());
        }
        catch (CoalesceException | InterruptedException e)
        {
            throw new RemoteException("(FAILED) Executing query", e);
        }
    }

    public QueryResult requery(String key) throws RemoteException
    {
        List<SearchQueryDetails> history = getHistory(CoalescePropertyFactory.getEntityKey(key), 1, 1);

        if (history.isEmpty())
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND, SearchQueryCoalesceEntity.NAME, key));
        }

        return searchComplex(history.get(0).getQuery());
    }

    private QueryResult createResponse(SearchResults searchResults, List<String> properties) throws RemoteException
    {
        if (!searchResults.isSuccessful())
        {
            throw new RemoteException(searchResults.getError());
        }

        try (CachedRowSet rowset = searchResults.getResults())
        {
            QueryResult results = new QueryResult();
            results.setTotal(BigInteger.valueOf(searchResults.getTotal()));

            if (rowset.first())
            {
                // Obtain list of keys
                do
                {
                    int idx = 1;

                    HitType hit = new HitType();
                    hit.setEntityKey(rowset.getString(idx++));

                    if (properties != null)
                    {
                        for (int ii = idx; ii < properties.size() + idx; ii++)
                        {
                            hit.getValues().add(rowset.getString(ii));
                        }
                    }

                    results.getHits().add(hit);
                }
                while (rowset.next());
            }
            return results;
        }
        catch (SQLException e)
        {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    public List<SearchQueryDetails> getHistory(int page, int pagesize) throws RemoteException
    {
        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getCreatedBy(getPrincipal().getName()));

        return getHistory(FF.and(filters), page, pagesize);
    }

    public List<SearchQueryDetails> getSavedHistory(int page, int pagesize) throws RemoteException
    {
        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getCreatedBy(getPrincipal().getName()));
        filters.add(FF.equal(PROP_QUERY_SAVED, FF.literal(true), false));

        return getHistory(FF.and(filters), page, pagesize);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static PropertyName getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields field)
    {
        return CoalescePropertyFactory.getFieldProperty(SearchQueryCoalesceEntity.RECORDSET_SEARCHQUERY, field);
    }

    private List<SearchQueryDetails> getHistory(Filter filter, int page, int size) throws RemoteException
    {
        List<String> properties = new ArrayList<>();
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.CQL).getPropertyName());
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.INDEXTYPE).getPropertyName());
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.PAGESIZE).getPropertyName());
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.CAPABILITIES).getPropertyName());
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.PROPERTYNAMES).getPropertyName());
        properties.add(getPropertyName(SearchQueryCoalesceRecord.ESearchQueryFields.CRITERIA).getPropertyName());
        properties.add(CoalescePropertyFactory.getEntityTitle().getPropertyName());
        properties.add(CoalescePropertyFactory.getLastModified().getPropertyName());

        // Execute Query
        Query query = new Query(SearchQueryCoalesceEntity.NAME);
        query.setFilter(filter);
        query.setPropertyNames(properties);
        query.setSortBy(new SortBy[] {
                FF.sort(CoalescePropertyFactory.getLastModified().getPropertyName(), SortOrder.DESCENDING)
        });
        query.setStartIndex(page > 0 ? (page - 1) * size : 0);
        query.setMaxFeatures(size);

        try
        {
            SearchResults results = framework.search(query);

            if (!results.isSuccessful())
            {
                throw new CoalesceException(results.getError());
            }

            List<SearchQueryDetails> history = new ArrayList<>();

            try (CachedRowSet rowset = results.getResults())
            {
                if (rowset.first())
                {
                    // Obtain list of keys
                    do
                    {
                        SearchQuery record = new SearchQuery();

                        int idx = 1;

                        record.setKey(rowset.getString(idx++));
                        record.setCql(rowset.getString(idx++));
                        record.setType(rowset.getString(idx++));
                        record.setPageSize(rowset.getInt(idx++));

                        List<String> capabilities = Arrays.asList(rowset.getString(idx++).split("[,]"));

                        EnumSet<EPersistorCapabilities> set = capabilities.stream().map(EPersistorCapabilities::valueOf).collect(
                                Collectors.toCollection(() -> EnumSet.noneOf(EPersistorCapabilities.class)));

                        record.setCapabilities(set);

                        String value = rowset.getString(idx++);

                        if (!StringHelper.isNullOrEmpty(value))
                        {
                            record.setPropertyNames(Arrays.asList(value.split("[,]")));
                        }

                        value = rowset.getString(idx++);

                        if (!StringHelper.isNullOrEmpty(value))
                        {
                            record.setGroup(MAPPER.readValue(value, SearchGroup.class));
                        }

                        SearchQueryDetails details = new SearchQueryDetails();
                        details.setTitle(rowset.getString(idx++));
                        details.setLastModified(JodaDateTimeHelper.parseDateTime(rowset.getString(idx)));
                        details.setQuery(record);

                        history.add(details);
                    }
                    while (rowset.next());
                }
            }
            catch (SQLException | IOException e)
            {
                throw new CoalesceException(e.getMessage(), e);
            }

            return history;
        }
        catch (CoalesceException | InterruptedException e)
        {
            throw new RemoteException("(FAILED) Executing query", e);
        }
    }

    public String save(SearchQueryDetails value) throws RemoteException
    {
        if (StringHelper.isNullOrEmpty(value.getQuery().getCql()))
        {
            value.getQuery().setCql(CQL.toCQL(getFilter(FF, value.getQuery().getGroup())));
        }

        return super.save(value);
    }

    public void update(String key, SearchQueryDetails value) throws RemoteException
    {
        if (StringHelper.isNullOrEmpty(value.getQuery().getCql()))
        {
            value.getQuery().setCql(CQL.toCQL(getFilter(FF, value.getQuery().getGroup())));
        }

        super.update(key, value);
    }

    public Map<ECoalesceFieldDataTypes, Map<String, String>> getCapabilities() throws RemoteException
    {
        Map<ECoalesceFieldDataTypes, Map<String, String>> results = new HashMap<>();

        // TODO Not Implemented

        return results;
    }

    public Collection<Operator> getAllCapabilities() throws RemoteException
    {
        Collection<Operator> results = new HashSet<>();

        if (capabilities.getScalarCapabilities() != null)
        {
            results.addAll(capabilities.getScalarCapabilities().getArithmeticOperators().getFunctions().getFunctionNames());
            results.addAll(capabilities.getScalarCapabilities().getComparisonOperators().getOperators());
        }

        if (capabilities.getTemporalCapabilities() != null)
        {
            results.addAll(capabilities.getTemporalCapabilities().getTemporalOperators().getOperators());
        }

        if (capabilities.getSpatialCapabilities() != null)
        {
            results.addAll(capabilities.getSpatialCapabilities().getSpatialOperators().getOperators());
        }

        return results;
    }

    /**
     * @return the capabilities of the underlying persister
     * @throws RemoteException on error
     */
    public Collection<Operator> getComparisonCapabilities() throws RemoteException
    {
        Collection<Operator> results = new HashSet<>();

        if (capabilities.getScalarCapabilities() != null)
        {
            results.addAll(capabilities.getScalarCapabilities().getComparisonOperators().getOperators());
        }

        return results;
    }

    public Collection<Operator> getFunctionCapabilities() throws RemoteException
    {
        Collection<Operator> results = new HashSet<>();

        if (capabilities.getScalarCapabilities() != null)
        {
            results.addAll(capabilities.getScalarCapabilities().getArithmeticOperators().getFunctions().getFunctionNames());
        }

        return results;
    }

    public Collection<Operator> getTemporalCapabilities() throws RemoteException
    {
        Collection<Operator> results = new HashSet<>();

        if (capabilities.getTemporalCapabilities() != null)
        {
            results.addAll(capabilities.getTemporalCapabilities().getTemporalOperators().getOperators());
        }

        return results;
    }

    public Collection<Operator> getSpatialCapabilities() throws RemoteException
    {
        Collection<Operator> results = new HashSet<>();

        if (capabilities.getSpatialCapabilities() != null)
        {
            results.addAll(capabilities.getSpatialCapabilities().getSpatialOperators().getOperators());
        }

        return results;
    }

    public Collection<GeometryOperand> getGeometryCapabilities() throws RemoteException
    {
        Collection<GeometryOperand> results;

        if (capabilities.getSpatialCapabilities() != null)
        {
            results = capabilities.getSpatialCapabilities().getGeometryOperands();
        }
        else
        {
            results = Collections.emptyList();
        }

        return results;
    }

    private Filter getFilter(FilterFactory2 ff, SearchGroup group) throws RemoteException
    {
        List<Filter> filters = new ArrayList<>();

        for (SearchGroup subgroup : group.getGroups())
        {
            filters.add(getFilter(ff, subgroup));
        }

        LOGGER.debug("Criteria:");
        Filter criteriaFilter;

        for (SearchCriteria criteria : group.getCriteria())
        {
            PropertyName property = CoalescePropertyFactory.getFieldProperty(criteria.getRecordset(), criteria.getField());

            LOGGER.debug("\t{}.{} {} {}",
                         criteria.getRecordset(),
                         criteria.getField(),
                         criteria.getOperator(),
                         criteria.getValue());

            criteriaFilter = null;

            switch (criteria.getOperator())
            {
            case "=": // TODO Remove this legacy support
            case PropertyIsEqualTo.NAME:
                criteriaFilter = ff.equal(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case PropertyIsGreaterThan.NAME:
                criteriaFilter = ff.greater(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case PropertyIsGreaterThanOrEqualTo.NAME:
                criteriaFilter = ff.greaterOrEqual(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case PropertyIsLessThan.NAME:
                criteriaFilter = ff.less(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case PropertyIsLessThanOrEqualTo.NAME:
                criteriaFilter = ff.lessOrEqual(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case "!=": // TODO Remove this legacy support
            case PropertyIsNotEqualTo.NAME:
                criteriaFilter = ff.notEqual(property, ff.literal(criteria.getValue()), criteria.isMatchCase());
                break;
            case PropertyIsLike.NAME:
                criteriaFilter = ff.like(property, criteria.getValue());
                break;
            case PropertyIsBetween.NAME:
                String[] values = criteria.getValues().toArray(new String[0]);
                if (values.length != 2)
                {
                    values = criteria.getValue().split(" ");
                    if (values.length != 2)
                    {
                        throw new RemoteException(
                                "Expected an array of two values ('[\"<from>\", \"<to>\"]'), or two values separated by a space; Actual: "
                                        + criteria.getValue());
                    }
                }
                criteriaFilter = ff.between(property, ff.literal(values[0]), ff.literal(values[1]));
                break;
            case During.NAME:
                String[] times = criteria.getValues().toArray(new String[0]);
                if (times.length != 2)
                {
                    times = criteria.getValue().split(" ");
                    if (times.length != 2)
                    {
                        throw new RemoteException(
                                "Expected an array of two values ('[\"<from>\", \"<to>\"]'), or two values separated by a space; Actual: "
                                        + criteria.getValue());
                    }
                }

                times[0] = times[0].replaceAll("\"", "");
                times[1] = times[1].replaceAll("\"", "");

                Instant start = new DefaultInstant(new DefaultPosition(JodaDateTimeHelper.parseDateTime(times[0]).toDate()));
                Instant end = new DefaultInstant(new DefaultPosition(JodaDateTimeHelper.parseDateTime(times[1]).toDate()));

                criteriaFilter = ff.during(property, ff.literal(new DefaultPeriod(start, end)));
                break;
            case After.NAME:
                DefaultInstant after = new DefaultInstant(new DefaultPosition(JodaDateTimeHelper.parseDateTime(criteria.getValue()).toDate()));

                criteriaFilter = ff.after(property, ff.literal(after));
                break;
            case Before.NAME:
                DefaultInstant before = new DefaultInstant(new DefaultPosition(JodaDateTimeHelper.parseDateTime(criteria.getValue()).toDate()));

                criteriaFilter = ff.before(property, ff.literal(before));
                break;
            case BBOX.NAME:
                try
                {
                    WKTReader2 reader = new WKTReader2();
                    ReferencedEnvelope bbox = new ReferencedEnvelope(reader.read(criteria.getValue()).getEnvelopeInternal(),
                                                                     crs);
                    criteriaFilter = ff.bbox(property, bbox);
                }
                catch (ParseException e)
                {
                    throw new RemoteException(e.getMessage(), e);
                }
                break;
            case PropertyIsNull.NAME:
                criteriaFilter = ff.isNull(property);
                break;
            default:
                throw new RemoteException(String.format(CoalesceErrors.INVALID_INPUT, criteria.getOperator()));
            }

            if (criteriaFilter != null)
            {
                if (criteria.isNot())
                {
                    filters.add(ff.not(criteriaFilter));
                }
                else
                {
                    filters.add(criteriaFilter);
                }
            }
        }

        Filter filter;

        switch (group.getOperator().toLowerCase())
        {
        case "or":
            filter = ff.or(filters);
            break;
        case "and":
            filter = ff.and(filters);
            break;
        default:
            throw new RemoteException("Invalid Operand: " + group.getOperator() + "; Expected (AND | OR)");
        }

        return filter;
    }

    protected ICoalescePrincipal getPrincipal()
    {
        return new CoalesceSimplePrincipal();
    }

}
