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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResult;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.api.search.SortByType;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchGroup;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.CRS;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Converts a list of options into an OGC filter and passes it along to a search
 * persister returning the results to the caller.
 *
 * @author Derek Clemenzi
 */
public class SearchDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDataController.class);

    private static final String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";

    private final CoalesceSearchFramework framework;
    private final CoordinateReferenceSystem crs;
    private final FilterCapabilities capabilities;

    public SearchDataController(CoalesceSearchFramework value)
    {
        framework = value;
        capabilities = framework.getCapabilities().getContents();

        try
        {
            crs = CRS.parseWKT(EPSG4326);
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
        FilterFactory2 ff = CoalescePropertyFactory.getFilterFactory();

        try
        {
            Filter filter = getFilter(ff, searchQuery.getGroup());

            // Convert Properties
            List<PropertyName> properties = new ArrayList<>();
            for (int ii = 0; ii < searchQuery.getPropertyNames().size(); ii++)
            {
                properties.add(ff.property(searchQuery.getPropertyNames().get(ii)));
            }

            // Convert Sort
            SortBy[] sortBy = new SortBy[searchQuery.getSortBy().size()];
            for (int ii = 0; ii < searchQuery.getSortBy().size(); ii++)
            {
                SortByType sort = searchQuery.getSortBy().get(ii);

                sortBy[ii] = ff.sort(sort.getPropertyName(), SortOrder.valueOf(sort.getSortOrder().toString()));
            }

            Query query = new Query(searchQuery.getType());
            query.setFilter(filter);
            query.setProperties(properties);
            query.setSortBy(sortBy);
            query.setStartIndex(
                    searchQuery.getPageNumber() > 0 ? (searchQuery.getPageNumber() - 1) * searchQuery.getPageSize() : 0);
            query.setMaxFeatures(searchQuery.getPageSize());

            return createResponse(framework.searchBulk(searchQuery.getCapabilities(), query).get(0), properties);
        }
        catch (CoalesceException e)
        {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    private QueryResult createResponse(SearchResults searchResults, List<PropertyName> properties) throws RemoteException
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

    public QueryResult searchOGC(QueryType searchQuery) throws RemoteException
    {
        // Convert Properties
        List<PropertyName> properties = new ArrayList<>();
        for (int ii = 0; ii < searchQuery.getPropertyNames().size(); ii++)
        {
            properties.add(CoalescePropertyFactory.getFilterFactory().property(searchQuery.getPropertyNames().get(ii)));
        }

        // Convert Sort
        SortBy[] sortBy = new SortBy[searchQuery.getSortBy().size()];
        for (int ii = 0; ii < searchQuery.getSortBy().size(); ii++)
        {
            SortByType sort = searchQuery.getSortBy().get(ii);

            sortBy[ii] = CoalescePropertyFactory.getFilterFactory().sort(sort.getPropertyName(),
                                                                         SortOrder.valueOf(sort.getSortOrder().toString()));
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
        query.setProperties(properties);
        query.setSortBy(sortBy);
        query.setStartIndex(searchQuery.getPageNumber());
        query.setMaxFeatures(searchQuery.getPageSize());

        try
        {
            return createResponse(framework.search(query), properties);
        }
        catch (CoalesceException e)
        {
            throw new RemoteException("(FAILED) Executing query", e);
        }
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

    private Filter getFilter(FilterFactory2 ff, SearchGroup group) throws CoalesceException
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
                    throw new CoalesceException(
                            "Expected two values space separated '<from> <to>'; Actual: " + criteria.getValue());
                }
                criteriaFilter = ff.between(property, ff.literal(values[0]), ff.literal(values[1]));
                break;
            case During.NAME:
                String[] times = criteria.getValues().toArray(new String[0]);
                if (times.length != 2)
                {
                    throw new CoalesceException(
                            "Expected two values space separated '<from> <to>'; Actual: " + criteria.getValue());
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
                    throw new CoalesceException(e);
                }
                break;
            case PropertyIsNull.NAME:
                criteriaFilter = ff.isNull(property);
                break;
            default:
                throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT, criteria.getOperator()));
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
            throw new CoalesceException("Invalid Operand: " + group.getOperator() + "; Expected (AND | OR)");
        }

        return filter;
    }

}
