package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wraps a FeatureIterator to iterate over columns
 */
public class FeatureColumnIterator implements Iterator<Object[]> {

    private FeatureIterator<?> featureIterator;
    private List<String> properties;

    public FeatureColumnIterator(FeatureIterator<?> featureIterator, List<PropertyName> properties)
    {
        this.featureIterator = featureIterator;
        this.properties = new ArrayList<>();

        // Normalize
        for (PropertyName property : properties)
        {
            this.properties.add(CoalescePropertyFactory.getColumnName(new ElasticSearchNormalizer(), property));
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
            String property = properties.get(i);
            Object value;

            if (!CoalescePropertyFactory.isRecordPropertyName(property))
            {
                value = feature.getProperty(property).getValue();

                if (value instanceof ArrayList)
                {
                    value = StringUtils.join((ArrayList) value, ",");
                }
                else if (value instanceof String && ((String) value).startsWith("[") && ((String) value).endsWith("]"))
                {
                    String[] values = ((String) value).substring(1, ((String) value).length() - 1).split("(, )");
                    value = StringUtils.join(Arrays.stream(values).parallel().map(StringEscapeUtils::escapeCsv).collect(
                            Collectors.toList()), ",");
/** TODO this may not be needed
                    try{
                        value = org.apache.commons.lang.StringUtils.join(new JSONArray((String) value).iterator(), ",");
                    } catch (JSONException e){
                        //Fall Back to good old matt logic
                        value = ((String) value).substring(1).substring(0,((String) value).length()-2);

                        String[] values = ((String) value).split(", ");

                        for(int ii=0; ii < values.length; ii++){

                            if(values[ii].equals(",")){
                                values[ii] = Character.toString('"') + values[ii] + Character.toString('"');
                            }

                        }

                        value = String.join(",",values);

                    }**/

                }
            }
            else
            {
                String id = feature.getIdentifier().getID();
                value = id.substring(id.indexOf(".") + 1);
            }

            row[i] = value;
        }

        return row;
    }

    @Override
    public void remove()
    {
    }
}
