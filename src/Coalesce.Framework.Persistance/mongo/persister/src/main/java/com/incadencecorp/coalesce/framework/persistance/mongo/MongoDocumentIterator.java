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

package com.incadencecorp.coalesce.framework.persistance.mongo;

import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.filter.expression.PropertyName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Iterator used by Coalesce to convert Mongo search results into {@link com.incadencecorp.coalesce.search.api.SearchResults}.
 *
 * @author Derek Clemenzi
 */
public class MongoDocumentIterator implements Iterator<Object[]> {

    private final MongoCursor<Document> featureIterator;
    private final List<String> properties;
    private final GeometryJSON jsonReader = new GeometryJSON();

    public MongoDocumentIterator(MongoCursor<Document> featureIterator, List<PropertyName> properties)
    {
        this.featureIterator = featureIterator;
        this.properties = new ArrayList<>();

        // Normalize
        for (PropertyName property : properties)
        {
            this.properties.add(MongoConstants.normalize(property));
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

                if (value instanceof ArrayList)
                {
                    String[] values = new String[((ArrayList) value).size()];

                    int idx = 0;

                    for (Object item : ((ArrayList) value))
                    {
                        values[idx++] = item.toString();
                    }

                    value = StringUtils.join(Arrays.stream(values).parallel().map(StringEscapeUtils::escapeCsv).collect(
                            Collectors.toList()), ",");
                }
                else if (value instanceof Document)
                {
                    Document doc = (Document) value;
                    try
                    {
                        value = jsonReader.read(doc.toJson());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("(FAILED) Parsing GeoJson: " + doc.toJson(), e);
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
                value = feature.get(MongoConstants.COLUMN_ID);
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
