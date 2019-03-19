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

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.mapper.impl.JavaMapperImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This factory creates different feature types used by geo tools for searching.
 *
 * @author n78554
 */
public class CoalesceFeatureTypeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceFeatureTypeFactory.class);

    public static SimpleFeatureType createSimpleFeatureType(Map<String, ECoalesceFieldDataTypes> fields)
            throws CoalesceException
    {
        return createSimpleFeatureType("coalesce", fields, null);
    }

    /**
     * @param fields
     * @return {@link SimpleFeatureType} created from the map of fields passed
     * in.
     * @throws CoalesceException
     */
    public static SimpleFeatureType createSimpleFeatureType(String name,
                                                            Map<String, ECoalesceFieldDataTypes> fields,
                                                            ICoalesceMapper<Class<?>> mapper) throws CoalesceException
    {
        boolean hasGeometry = false;

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(name);

        if (mapper == null)
        {
            mapper = new JavaMapperImpl();
        }

        // Create Content
        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : fields.entrySet())
        {
            Class<?> mappedType = mapper.map(entry.getValue());

            if (mappedType != null)
            {
                builder.add(entry.getKey(), mappedType);

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace(entry.getKey() + " => " + mappedType.getName());
                }

                if (!hasGeometry && Geometry.class.isAssignableFrom(mappedType))
                {
                    builder.setDefaultGeometry(entry.getKey());
                    hasGeometry = true;
                }
            }
            else
            {
                LOGGER.trace(entry.getKey() + "=> null (SKIPPING)");
            }
        }

        if (!hasGeometry)
        {
            builder.add("_geo", mapper.map(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE));
            builder.setDefaultGeometry("_geo");
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Created Feature ({}) w/ {} Fields (Default Geometry: {})",
                        name,
                        fields.size(),
                        builder.getDefaultGeometry());
        }

        return builder.buildFeatureType();
    }

    /**
     * @return {@link SimpleFeatureType} created from templates that have been
     * registered with {@link CoalesceTemplateUtil}.
     * @throws CoalesceException
     * @see CoalesceTemplateUtil#addTemplates(com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor)
     */
    public static SimpleFeatureType createSimpleFeatureType() throws CoalesceException
    {
        return createSimpleFeatureType(CoalesceTemplateUtil.getDataTypes());
    }

}
