package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
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
import java.util.function.Predicate;

/**
 * Walks through a Filter, re-writing any property names removing the tablename from the property along with the /
 */
class AccumuloQueryRewriter2 extends DuplicatingFilterVisitor {

    private final Set<String> features = new HashSet<>();
    private final ICoalesceNormalizer normalizer;

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloQueryRewriter2.class);

    AccumuloQueryRewriter2(ICoalesceNormalizer normalizer)
    {
        super(CoalescePropertyFactory.getFilterFactory());

        this.normalizer = normalizer;
    }

    @Override
    public Object visit(PropertyName expression, Object extraData)
    {
        return super.visit(ff.property(getNormalizedPropertyName(expression.getPropertyName())), extraData);
    }

    public Query rewrite(Query original) throws CoalescePersistorException
    {
        features.clear();

        //create a new filter with the rewritten property names
        Query newQuery = new Query(original);

        // See if a valid feature name was set in the query.  If
        // so make sure it is the first in the list
        if ((newQuery.getTypeName() != null) && (!newQuery.getTypeName().equalsIgnoreCase("coalesce")))
        {
            // Reference a recordset?
            if (CoalesceTemplateUtil.getTemplateKey(newQuery.getTypeName()).size() == 1)
            {
                // Yes; Add to feature list
                features.add(normalizer.normalize(newQuery.getTypeName()));
            }
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
        if (newQuery.getSortBy() != null && newQuery.getSortBy().length > 0)
        {
            SortBy[] sorts = new SortBy[newQuery.getSortBy().length];

            int ii=0;

            for (SortBy sortby : newQuery.getSortBy())
            {
                String name = getNormalizedPropertyName(sortby.getPropertyName().getPropertyName());
                sorts[ii++] = ff.sort(name, sortby.getSortOrder());
            }

            newQuery.setSortBy(sorts);
        }

        // Now go through the features used and figure
        // out what is the key feature

        // If there are none use coalesceentity
        if (features.isEmpty())
        {
            newQuery.setTypeName(AccumuloDataConnector.ENTITY_FEATURE_NAME);
        }
        else if (features.size() == 1)
        {
            newQuery.setTypeName(normalizer.normalize(features.iterator().next()));
        }
        else
        {
            // Remove ENTITY_FEATURE_NAME if it is also used.
            Predicate<String> namePredicate = s -> s.equalsIgnoreCase(AccumuloDataConnector.ENTITY_FEATURE_NAME);
            features.removeIf(namePredicate);

            // Now if there is more than one feature throw an exception
            if (features.size() > 1)
            {
                LOGGER.debug("Features:");
                for (String feature : features)
                {
                    LOGGER.debug("\t{}", feature);
                }
                throw new CoalescePersistorException("Multiple featuretypes in query is not supported");
            }
            newQuery.setTypeName(normalizer.normalize(features.iterator().next()));
        }

        features.clear();

        return newQuery;
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
