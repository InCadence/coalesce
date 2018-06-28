package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
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
import java.util.List;
import java.util.function.Predicate;

/**
 * Walks through a Filter, re-writing any property names removing the tablename from the property along with the /
 */
class AccumuloQueryRewriter2 extends DuplicatingFilterVisitor {

    private final ArrayList<String> features = new ArrayList<>();
    private final ICoalesceNormalizer normalizer;

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloQueryRewriter2.class);

    AccumuloQueryRewriter2(ICoalesceNormalizer normalizer)
    {
        super(CommonFactoryFinder.getFilterFactory2());

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
        if (sorts != null)
        {
            for (int i = 0; i < sorts.length; i++)
            {
                sorts[i] = ff.sort(getNormalizedPropertyName(sorts[i].getPropertyName().getPropertyName()),
                                   sorts[i].getSortOrder());
            }

            newQuery.setSortBy(sorts);
        }

        // Now go through the features used and figure
        // out what is the key feature

        // If there are none use coalesceentity
        if (features.isEmpty())
        {
            newQuery.setTypeName(AccumuloPersistor.ENTITY_FEATURE_NAME);
        }
        else if (features.size() == 1)
        {
            newQuery.setTypeName(normalizer.normalize(features.get(0)));
        }
        else
        {
            // Remove ENTITY_FEATURE_NAME if it is also used.
            Predicate<String> namePredicate = s -> s.equalsIgnoreCase(AccumuloPersistor.ENTITY_FEATURE_NAME);
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
            newQuery.setTypeName(normalizer.normalize(features.get(0)));
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

    private String getNormalizedPropertyName(PropertyName name)
    {
        return getNormalizedPropertyName(name.getPropertyName());
    }

}
