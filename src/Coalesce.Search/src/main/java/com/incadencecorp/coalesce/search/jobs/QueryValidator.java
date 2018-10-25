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

package com.incadencecorp.coalesce.search.jobs;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import org.geotools.data.Query;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Walks through a query to determine which templates are being referenced.
 */
public class QueryValidator extends DuplicatingFilterVisitor {

    private final Set<String> features = new HashSet<>();
    private static final ICoalesceNormalizer NORMALIZER = new DefaultNormalizer();

    /**
     * Standard java logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryValidator.class);

    @Override
    public Object visit(PropertyName expression, Object extraData)
    {
        addProperty(expression);

        return super.visit(expression, extraData);
    }

    /**
     * @param query to process
     * @return a list of template names referenced by the query.
     */
    public Set<String> getTemplateNames(final Query query) throws CoalescePersistorException
    {
        features.clear();

        Query copy = new Query(query);

        // Type name specified?
        if ((copy.getTypeName() != null) && (!copy.getTypeName().equalsIgnoreCase("coalesce")))
        {
            // Yes; Use the user defined type
            features.add(NORMALIZER.normalize(copy.getTypeName()));
        }
        else
        {
            // Add templates mentioned in the filters
            query.getFilter().accept(this, null);

            // Add templates mentioned by the properties
            if (copy.getPropertyNames() != null)
            {
                for (String property : copy.getPropertyNames())
                {
                    addProperty(property);
                }
            }

            // Add templates mentioned by the sort by
            if (copy.getSortBy() != null)
            {
                for (SortBy sort : copy.getSortBy())
                {
                    addProperty(sort.getPropertyName());
                }
            }
        }

        return new HashSet<>(features);
    }

    private void addProperty(PropertyName name)
    {
        addProperty(name.getPropertyName());
    }

    private void addProperty(String name)
    {
        String[] parts = name.split("[/.]");

        if (parts.length == 2)
        {
            addFeature(parts[0]);
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.INVALID_INPUT, name));
        }
    }

    private void addFeature(String feature)
    {
        feature = NORMALIZER.normalize(feature);

        if (!features.contains(feature))
        {
            if (feature.equalsIgnoreCase("coalesceentity"))
            {
                LOGGER.debug("Ignoring ({})", feature);
            }
            else if (feature.equalsIgnoreCase("coalescelinkage"))
            {
                features.add(feature);
            }
            else
            {
                Set<ObjectMetaData> templates = CoalesceTemplateUtil.getTemplatesContainingRecordset(feature);

                if (templates.size() == 1)
                {
                    features.add(NORMALIZER.normalize(templates.iterator().next().getName()));
                }
                else if (templates.size() == 0)
                {
                    LOGGER.error("(ERROR) Could not determine index; recordset ({}) does not exists in templates", feature);
                }
                else
                {
                    LOGGER.error("(ERROR) Could not determine index; recordset ({}) exists in multiple templates", feature);
                    templates.iterator().forEachRemaining(meta -> LOGGER.error("\t{} : {}", meta.getKey(), meta.getName()));
                }
            }
        }
    }

}
