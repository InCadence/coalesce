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

package com.incadencecorp.coalesce.framework.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIteratorDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;

/**
 * This class provides utility functions for handling templates.
 * 
 * @author n78554
 *
 */
public final class CoalesceTemplateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceTemplateUtil.class);

    private static ICoalesceNormalizer normalizer = new DefaultNormalizer();
    private static Map<String, ECoalesceFieldDataTypes> types = new HashMap<String, ECoalesceFieldDataTypes>();
    private static boolean isInitialized = false;

    private CoalesceTemplateUtil()
    {
        // Do Nothing
    }

    /**
     * Sets the normalizer to use for generating keys.
     * 
     * @param value
     */
    public static void setNormalizer(ICoalesceNormalizer value)
    {
        normalizer = value;
    }

    /**
     * Adds the data types for the given template.
     * 
     * @param templates
     */
    public static void addTemplates(CoalesceEntityTemplate... templates)
    {
        initializeDefaults();

        CoalesceIteratorDataTypes iterator = new CoalesceIteratorDataTypes(normalizer);

        for (CoalesceEntityTemplate template : templates)
        {

            try
            {
                for (Map.Entry<String, ECoalesceFieldDataTypes> entry : iterator.getDataTypes(template).entrySet())
                {
                    if (types.containsKey(entry.getKey()) && types.get(entry.getKey()) != entry.getValue())
                    {
                        LOGGER.warn(String.format("Template: (%s) (%s) (%s); Expected (%s) for (%s) not (%s)",
                                                  template.getName(),
                                                  template.getSource(),
                                                  template.getVersion(),
                                                  types.get(entry.getKey()),
                                                  entry.getKey(),
                                                  entry.getValue()));
                    }

                    types.put(entry.getKey(), entry.getValue());
                }
            }
            catch (CoalesceException e)
            {
                LOGGER.error(String.format("Failed to Add Template: (%s) (%s) (%s)",
                                           template.getName(),
                                           template.getSource(),
                                           template.getVersion()));
            }

        }

    }

    /**
     * Adds the data types for every template registered by the given persistor.
     * 
     * @param peristor
     * @throws CoalescePersistorException
     */
    public static void addTemplates(ICoalescePersistor peristor) throws CoalescePersistorException
    {
        List<CoalesceEntityTemplate> templates = new ArrayList<CoalesceEntityTemplate>();

        // Iterate Through All Templates
        for (ObjectMetaData metadata : peristor.getEntityTemplateMetadata())
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace(String.format("Processing Template: (%s) (%s) (%s)",
                                           metadata.getName(),
                                           metadata.getSource(),
                                           metadata.getVersion()));
            }

            try
            {
                templates.add(CoalesceEntityTemplate.create(peristor.getEntityTemplateXml(metadata.getKey())));

                if (StringHelper.isNullOrEmpty(metadata.getName()) || StringHelper.isNullOrEmpty(metadata.getSource())
                        || StringHelper.isNullOrEmpty(metadata.getVersion()))
                {
                    LOGGER.warn(String.format("Invalid Template (%s): (%s) (%s) (%s)",
                                              metadata.getKey(),
                                              metadata.getName(),
                                              metadata.getSource(),
                                              metadata.getVersion()));
                }
            }
            catch (SAXException | IOException e)
            {
                LOGGER.error(String.format(CoalesceErrors.TEMPLATE_LOAD,
                                           metadata.getName(),
                                           metadata.getSource(),
                                           metadata.getVersion()),
                             e);
            }
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace(String.format("Total Templates: %s", templates.size()));
        }

        addTemplates(templates.toArray(new CoalesceEntityTemplate[templates.size()]));

    }

    /**
     * @return a map of all the fields registered and their data types.
     */
    public static Map<String, ECoalesceFieldDataTypes> getDataTypes()
    {
        initializeDefaults();

        Map<String, ECoalesceFieldDataTypes> results = new HashMap<String, ECoalesceFieldDataTypes>();
        results.putAll(types);

        return results;
    }

    /**
     * Logs how many fields of each type exists.
     */
    public static void logDataTypeBreakout()
    {

        int[] totals = new int[ECoalesceFieldDataTypes.values().length];
        Arrays.fill(totals, 0);

        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : types.entrySet())
        {
            totals[entry.getValue().ordinal()]++;
        }

        for (int ii = 0; ii < totals.length; ii++)
        {
            LOGGER.info(ECoalesceFieldDataTypes.values()[ii].toString() + " Totals: " + totals[ii]);
        }

    }

    private static void initializeDefaults()
    {
        if (!isInitialized)
        {
            types.put("coalesceentity.name", ECoalesceFieldDataTypes.STRING_TYPE);
            types.put("coalesceentity.source", ECoalesceFieldDataTypes.STRING_TYPE);
            types.put("coalesceentity.version", ECoalesceFieldDataTypes.STRING_TYPE);
            types.put("coalesceentity.title", ECoalesceFieldDataTypes.STRING_TYPE);
            types.put("coalesceentity.deleted", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            types.put("coalesceentity.creator", ECoalesceFieldDataTypes.STRING_TYPE);
            types.put("coalesceentity.objectkey", ECoalesceFieldDataTypes.GUID_TYPE);
            types.put("coalesceentity.datecreated", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
            types.put("coalesceentity.lastmodified", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        }
    }

}
