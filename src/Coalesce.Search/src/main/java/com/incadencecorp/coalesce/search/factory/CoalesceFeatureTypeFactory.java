/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.search.factory;

import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.mapper.impl.JavaMapperImpl;

/**
 * This factory creates different feature types used by geo tools for searching.
 * 
 * @author n78554
 */
public class CoalesceFeatureTypeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceFeatureTypeFactory.class);

    /**
     * @param fields
     * @return {@link SimpleFeatureType} created from the map of fields passed
     *         in.
     * @throws CoalesceException
     */
    public static SimpleFeatureType createSimpleFeatureType(Map<String, ECoalesceFieldDataTypes> fields)
            throws CoalesceException
    {

        StringBuilder sb = new StringBuilder();

        JavaMapperImpl mapper = new JavaMapperImpl();

        // Create Content
        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : fields.entrySet())
        {
            if (sb.length() != 0)
            {
                sb.append(",");
            }

            sb.append(entry.getKey() + ":" + mapper.map(entry.getValue()).getName());
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Create Coalesce Feature w/ {} Fields", fields.size());
        }

        try
        {
            return DataUtilities.createType("coalesce", sb.toString());
        }
        catch (SchemaException e)
        {
            throw new CoalesceException("Creating Feature Type", e);
        }

    }

    /**
     * @return {@link SimpleFeatureType} created from templates that have been
     *         registered with {@link CoalesceTemplateUtil}.
     * @throws CoalesceException
     * @see CoalesceTemplateUtil#addTemplates(com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor)
     */
    public static SimpleFeatureType createSimpleFeatureType() throws CoalesceException
    {
        return createSimpleFeatureType(CoalesceTemplateUtil.getDataTypes());
    }

}
