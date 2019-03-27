/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Walks through a Filter, re-writing any property names removing the tablename from the property along with the /
 */
class ElasticSearchQueryRewriter extends DuplicatingFilterVisitor {

    private final Set<String> features = new HashSet<>();
    private final Set<String> highlights = new HashSet<>();
    private final ICoalesceNormalizer normalizer;
    private final Set<String> keywords = new HashSet<>();

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchQueryRewriter.class);

    ElasticSearchQueryRewriter()
    {
        this(new ElasticSearchNormalizer());
    }

    ElasticSearchQueryRewriter(ICoalesceNormalizer normalizer)
    {
        super(CommonFactoryFinder.getFilterFactory2());

        this.normalizer = normalizer;
    }

    public void setKeywords(Set<String> keywords)
    {
        this.keywords.clear();
        this.keywords.addAll(keywords);
    }

    @Override
    public Object visit(PropertyName expression, Object extraData)
    {
        PropertyName name;

        if (extraData instanceof Boolean)
        {
            try
            {
                if (isStringField(expression))
                {
                    name = ff.property(getNormalizedPropertyName(expression.getPropertyName()) + ".keyword");

                    LOGGER.info("Rewriting ({}) => ({})", expression.getPropertyName(), name.getPropertyName());
                }
                else
                {
                    name = ff.property(getNormalizedPropertyName(expression.getPropertyName()));
                }
            }
            catch (IllegalArgumentException e)
            {
                // TODO Need to address, for now do nothing
                name = expression;
            }
        }
        else
        {
            name = ff.property(getNormalizedPropertyName(expression.getPropertyName()));
        }

        return super.visit(name, extraData);
    }

    private boolean isStringField(PropertyName name)
    {
        String property = name.getPropertyName().toLowerCase();

        ECoalesceFieldDataTypes type = CoalesceTemplateUtil.getDataType(property);

        if (type == null)
        {
            throw new RuntimeException("Unknown Parameter Specified: " + property);
        }

        LOGGER.trace("({}): ({})", property, type);

        return (type == ECoalesceFieldDataTypes.STRING_TYPE) && !keywords.contains(property);
    }

    @Override
    public Object visit(PropertyIsLike filter, Object extraData)
    {
        if (filter.getExpression() instanceof PropertyName)
        {
            highlights.add(((PropertyName) filter.getExpression()).getPropertyName());
        }

        return super.visit(filter, extraData);
    }

    @Override
    public Object visit(PropertyIsEqualTo filter, Object extraData)
    {
        return super.visit(filter, Boolean.TRUE);
    }

    @Override
    public Object visit(PropertyIsNotEqualTo filter, Object extraData)
    {
        return super.visit(filter, Boolean.TRUE);
    }

    public String getFeatureType(Query query) throws CoalescePersistorException
    {

        String featureType;

        features.clear();

        if (!StringHelper.isNullOrEmpty(query.getTypeName()) && !query.getTypeName().equalsIgnoreCase("coalesce"))
        {
            features.add(normalizer.normalize(query.getTypeName()));
        }

        query.getFilter().accept(this, null);

        // Convert to Index Names
        Set<String> types = new HashSet<>();

        for (String feature : features)
        {
            if (!feature.equalsIgnoreCase("coalesceentity"))
            {
                types.add(getTypeName(feature));
            }
        }

        // If there are none use coalesceentity
        if (types.isEmpty())
        {
            featureType = ElasticSearchPersistor.COALESCE_ENTITY_INDEX;
        }
        else if (types.size() == 1)
        {
            featureType = types.iterator().next();
        }
        else
        {
            LOGGER.debug("Features:");
            for (String type : types)
            {
                LOGGER.debug("\t{}", type);
            }
            throw new CoalescePersistorException("Multiple featuretypes in query is not supported");
        }

        features.clear();

        return featureType;
    }

    public Query rewrite(Query original) throws CoalescePersistorException
    {
        features.clear();
        highlights.clear();

        //create a new filter with the rewritten property names
        Query newQuery = new Query(original);

        // See if a valid feature name was set in the query.  If
        // so make sure it is the first in the list
        if (!StringHelper.isNullOrEmpty(newQuery.getTypeName()) && !newQuery.getTypeName().equalsIgnoreCase("coalesce"))
        {
            features.add(normalizer.normalize(newQuery.getTypeName()));
        }

        // Clear the type name from the query
        newQuery.setTypeName(null);

        Filter f = original.getFilter();

        // Rewrite the filter
        newQuery.setFilter((Filter) f.accept(this, null));

        List<String> properties = new ArrayList<>();

        // Normalize properties and remove any duplicates
        for (String property : newQuery.getPropertyNames())
        {
            String normalized = getNormalizedPropertyName(property);

            if (!CoalescePropertyFactory.isRecordPropertyName(property))
            {
                if (!properties.contains(normalized))
                {
                    properties.add(normalized);
                }
            }
        }

        newQuery.setPropertyNames(properties);

        // Rewrite the any sorts also
        SortBy[] sorts = newQuery.getSortBy();
        if (sorts != null && sorts.length > 0)
        {
            for (int i = 0; i < sorts.length; i++)
            {
                String name = getNormalizedPropertyName(sorts[i].getPropertyName().getPropertyName());

                if (isStringField(sorts[i].getPropertyName()))
                {
                    name = name + ".keyword";
                }

                sorts[i] = ff.sort(name, sorts[i].getSortOrder());
            }

            newQuery.setSortBy(sorts);
        }

        newQuery.setHints(original.getHints());

        // Now go through the features used and figure out what is the key feature

        // Convert to Index Names
        Set<String> types = new HashSet<>();

        for (String feature : features)
        {
            if (!feature.equalsIgnoreCase("coalesceentity"))
            {
                types.add(getTypeName(feature));
            }
        }

        // If there are none use coalesceentity
        if (types.isEmpty())
        {
            newQuery.setTypeName(ElasticSearchPersistor.COALESCE_ENTITY_INDEX);
        }
        else if (types.size() == 1)
        {
            newQuery.setTypeName(types.iterator().next());
        }
        else
        {
            LOGGER.debug("Features:");
            for (String type : types)
            {
                LOGGER.debug("\t{}", type);
            }
            throw new CoalescePersistorException("Multiple featuretypes in query is not supported");
        }

        if (!highlights.isEmpty())
        {
            StringBuilder sb = new StringBuilder("{ \"fields\" : { ");

            for (String field : highlights)
            {
                String normalized = getNormalizedPropertyName(field);

                sb.append("\"" + normalized + "\":{}");
                LOGGER.debug("Highlighting {} => {}", field, normalized);
            }

            sb.append("}}");

            LOGGER.debug("{}", sb);

            newQuery.getHints().put(Hints.VIRTUAL_TABLE_PARAMETERS, Collections.singletonMap("highlight", sb.toString()));

        }

        features.clear();
        highlights.clear();

        return newQuery;
    }

    private String getTypeName(String feature) throws CoalescePersistorException
    {
        if (feature.equalsIgnoreCase("coalesceentity"))
        {
            return ElasticSearchPersistor.COALESCE_ENTITY_INDEX;
        }
        else if (feature.equalsIgnoreCase("coalescelinkage"))
        {
            return ElasticSearchPersistor.COALESCE_LINKAGE_INDEX;
        }
        else
        {
            String name;
            Set<ObjectMetaData> templates = CoalesceTemplateUtil.getTemplatesContainingRecordset(feature);

            if (templates.size() == 0)
            {
                name = feature;
            }
            else
            {
                name = templates.iterator().next().getName();

                if (templates.size() > 1)
                {
                    LOGGER.warn("(WARN) Could not determine index; recordset ({}) exists in multiple templates", feature);
                    templates.iterator().forEachRemaining(meta -> LOGGER.warn("\t{} : {}", meta.getKey(), meta.getName()));
                }
            }

            return ElasticSearchPersistor.COALESCE_ENTITY_INDEX + "-" + normalizer.normalize(name);
        }
    }

    private String getNormalizedPropertyName(String name)
    {
        String[] parts = name.split("[/.]");
        String normalized;

        if (parts.length == 1)
        {
            normalized = normalizer.normalize(parts[0]);
        }
        else if (parts.length == 2)
        {
            String feature = normalizer.normalize(parts[0]);

            if (!features.contains(feature))
            {
                features.add(feature);
            }

            normalized = normalizer.normalize(parts[0], parts[1]);
        }
        else
        {
            normalized = name;
        }

        return normalized;
    }

}
