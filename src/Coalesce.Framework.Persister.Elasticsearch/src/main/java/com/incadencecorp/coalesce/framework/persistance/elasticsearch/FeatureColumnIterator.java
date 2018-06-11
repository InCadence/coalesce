package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.apache.commons.lang.StringUtils;
import org.geotools.feature.FeatureIterator;
import org.json.JSONArray;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a FeatureIterator to iterate over columns
 */
public class FeatureColumnIterator implements Iterator<Object[]> {

    private FeatureIterator<?> featureIterator;
    private List<String> properties;
    private ICoalesceNormalizer normalizer = new ElasticSearchNormalizer();

    public FeatureColumnIterator(FeatureIterator<?> featureIterator, List<PropertyName> properties)
    {
        this.featureIterator = featureIterator;
        this.properties = new ArrayList<>();

        // Normalize
        for (int i = 0; i < properties.size(); i++)
        {
            this.properties.add(CoalescePropertyFactory.getColumnName(normalizer, properties.get(i)));
        }
    }

    @Override
    public boolean hasNext()
    {
        return featureIterator.hasNext();
    }

    @Override
    public Object[] next()
    {
        Feature feature = featureIterator.next();

        Object[] row = new Object[properties.size()];

        for (int i = 0; i < properties.size(); i++)
        {
            Object value = feature.getProperty(properties.get(i)).getValue();

            if (value instanceof ArrayList)
            {
                value = StringUtils.join((ArrayList) value, ",");
            }
            else if (value instanceof String && ((String) value).startsWith("[") && ((String) value).endsWith("]"))
            {
                value = StringUtils.join(new JSONArray((String) value).iterator(), ",");
            }

            row[i] = value;
        }

        return row;
    }

    @Override
    public void remove()
    {
        return;
    }
}
