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

package com.incadencecorp.coalesce.search;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.Query;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Clemenzi
 */
public abstract class AbstractQueryRewriter extends DuplicatingFilterVisitor {

    private final Set<String> features = new HashSet<>();
    private final ICoalesceNormalizer normalizer;

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueryRewriter.class);

    public AbstractQueryRewriter(ICoalesceNormalizer normalizer)
    {
        super(CoalescePropertyFactory.getFilterFactory());

        this.normalizer = normalizer;
    }

    @Override
    public Object visit(PropertyName expression, Object extraData)
    {
        // Adds PropertyName to features, but don't actually change the value as this will mess up the context in FilterToSql
        getNormalizedPropertyName(expression);

        return super.visit(expression, extraData);
    }

    public Query rewrite(Query original) throws CoalescePersistorException
    {
        features.clear();

        //create a new filter with the rewritten property names
        Query newQuery = new Query(original);

        if (newQuery.getStartIndex() == null)
        {
            newQuery.setStartIndex(0);
        }

        // See if a valid feature name was set in the query.  If so make sure it is the first in the list.
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
            else
            {
                properties.add(getIdColumn());
            }
        }

        newQuery.setPropertyNames(properties);
        newQuery.setHints(original.getHints());

        // Rewrite the any sorts also
        SortBy[] sorts = newQuery.getSortBy();

        if (sorts != null && sorts.length > 0)
        {
            for (int ii = 0; ii < sorts.length; ii++)
            {
                String name = getNormalizedPropertyName(sorts[ii].getPropertyName().getPropertyName());
                sorts[ii] = ff.sort(name, sorts[ii].getSortOrder());
            }

            newQuery.setSortBy(sorts);
        }

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
            newQuery.setTypeName(getCoalesceEntityTypeName());
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

        features.clear();

        return newQuery;
    }

    private String getTypeName(String feature)
    {
        if (feature.equalsIgnoreCase("coalesceentity"))
        {
            return getCoalesceEntityTypeName();
        }
        else if (feature.equalsIgnoreCase("coalescelinkage"))
        {
            return getCoalesceLinkageTypeName();
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

            return getCoalesceRecordTypeName(name);
        }
    }

    private String getNormalizedPropertyName(PropertyName name)
    {
        return getNormalizedPropertyName(name.getPropertyName());
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

            features.add(feature);

            normalized = normalizer.normalize(parts[0], parts[1]);
        }
        else
        {
            normalized = name;
        }

        return normalized;
    }

    abstract protected String getIdColumn();

    abstract protected String getCoalesceEntityTypeName();

    abstract protected String getCoalesceLinkageTypeName();

    abstract protected String getCoalesceRecordTypeName(String recordset);
}
