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

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.microsoft.azure.documentdb.Document;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.filter.expression.PropertyName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Derek Clemenzi
 */
public class CosmosDocumentIterator implements Iterator<Object[]> {

    private final Iterator<Document> featureIterator;
    private final List<String> properties;
    private final GeometryJSON jsonReader = new GeometryJSON();

    public CosmosDocumentIterator(Iterator<Document> featureIterator, List<PropertyName> properties)
    {
        this.featureIterator = featureIterator;
        this.properties = new ArrayList<>();

        // Normalize
        for (PropertyName property : properties)
        {
            this.properties.add(CoalescePropertyFactory.getColumnName(new CosmosNormalizer(), property));
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
        Document feature = featureIterator.next();

        Object[] row = new Object[properties.size()];

        for (int i = 0; i < properties.size(); i++)
        {
            String property = properties.get(i);
            Object value;

            if (!CoalescePropertyFactory.isRecordPropertyName(property))
            {
                value = feature.get(property);

                if (value instanceof JSONArray)
                {

                    String[] values = new String[((JSONArray) value).length()];

                    Iterator<Object> it = ((JSONArray) value).iterator();
                    int idx = 0;

                    while (it.hasNext())
                    {
                        values[idx++] = it.next().toString();
                    }

                    value = StringUtils.join(Arrays.stream(values).parallel().map(StringEscapeUtils::escapeCsv).collect(
                            Collectors.toList()), ",");
                }
                else if (value instanceof JSONObject)
                {
                    JSONObject json = ((JSONObject) value);

                    String type = json.getString("type").toLowerCase();

                    try
                    {
                        switch (type)
                        {
                        case "polygon":
                        case "point":
                            break;
                        case "linestring":
                            json.put("type", "LineString");
                            break;
                        case "multipoint":
                            json.put("type", "MultiPoint");
                            break;
                        default:
                            throw new RuntimeException("Unknown Geometry: " + type);
                        }

                        value = jsonReader.read(value.toString());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("(FAILED) Parsing GeoJson: " + type + " - " + value.toString());
                    }
                }
                else if (value instanceof String && ((String) value).startsWith("[") && ((String) value).endsWith("]"))
                {
                    String[] values = ((String) value).substring(1, ((String) value).length() - 1).split("(, )");
                    value = StringUtils.join(Arrays.stream(values).parallel().map(StringEscapeUtils::escapeCsv).collect(
                            Collectors.toList()), ",");
                }
            }
            else
            {
                value = feature.getId();
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
