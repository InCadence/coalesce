package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a FeatureIterator to iterate over columns
 *
 * @author Matthew Defazio
 */
public class FeatureColumnIterator implements Iterator<Object[]> {

    private FeatureIterator<?> featureIterator;
    private List<String> properties;
    private ICoalesceNormalizer normalizer = new AccumuloNormalizer();

    public FeatureColumnIterator(FeatureIterator<?> featureIterator, List<PropertyName> properties)
    {
        this.featureIterator = featureIterator;
        this.properties = new ArrayList<>();

        // Normalize
        for (PropertyName property : properties)
        {
            if (!CoalescePropertyFactory.isRecordPropertyName(property.getPropertyName()))
            {
                this.properties.add(CoalescePropertyFactory.getColumnName(normalizer, property));
            }
            else
            {
                this.properties.add("recordkey");
            }
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

        for (int ii = 0; ii < properties.size(); ii++)
        {
            String property = properties.get(ii);

            if (!property.equals("recordkey"))
            {
                row[ii] = feature.getProperty(property).getValue();
            }
            else
            {
                row[ii] = feature.getIdentifier().getID();
            }
        }

        return row;
    }

    @Override
    public void remove()
    {
    }
}
