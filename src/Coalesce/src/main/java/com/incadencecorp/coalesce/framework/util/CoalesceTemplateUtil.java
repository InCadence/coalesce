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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class provides utility functions for handling templates.
 *
 * @author Derek Clemenzi
 */
public final class CoalesceTemplateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceTemplateUtil.class);

    /**
     * Contains the fields for every record set specified by the templates.
     */
    private static final Map<String, Map<String, ECoalesceFieldDataTypes>> TYPES = new HashMap<>();
    /**
     * Contains the record sets specified within the templates
     */
    private static final Map<String, Set<String>> RECORDSETS = new HashMap<>();
    /**
     * Contains metadata
     */
    private static final Map<String, ObjectMetaData> META = new HashMap<>();

    private static ICoalesceNormalizer normalizer = new DefaultNormalizer();
    private static CoalesceIteratorDataTypes iterator = new CoalesceIteratorDataTypes(normalizer);

    private CoalesceTemplateUtil()
    {
        // Do Nothing
    }

    /**
     * Sets the normalizer to use for generating keys. Set this before adding any templates to this util.
     *
     * @param value normalizer used for generating the keys.
     */
    public static void setNormalizer(ICoalesceNormalizer value)
    {
        normalizer = value;
        iterator = new CoalesceIteratorDataTypes(normalizer);
    }

    /**
     * Adds the data types for the given template.
     *
     * @param templates templates to process.
     */
    public static void addTemplates(CoalesceEntityTemplate... templates)
    {
        initializeDefaults();

        for (CoalesceEntityTemplate template : templates)
        {
            try
            {
                Map<String, Map<String, ECoalesceFieldDataTypes>> values = iterator.getDataTypes(template);

                RECORDSETS.put(template.getKey(), values.keySet());
                META.put(template.getKey(),
                         new ObjectMetaData(template.getKey(),
                                            template.getName(),
                                            template.getSource(),
                                            template.getVersion()));

                // Iterate Over Record Sets
                for (Map.Entry<String, Map<String, ECoalesceFieldDataTypes>> recordset : values.entrySet())
                {
                    // Already Registered?
                    if (TYPES.containsKey(recordset.getKey()))
                    {
                        // Yes; Check for (and log) Differences
                        for (Map.Entry<String, ECoalesceFieldDataTypes> field : TYPES.get(recordset.getKey()).entrySet())
                        {
                            ECoalesceFieldDataTypes type = recordset.getValue().get(field.getKey());
                            if (type != field.getValue())
                            {
                                LOGGER.warn(String.format("Template: (%s) (%s) (%s); Expected (%s) for (%s) not (%s)",
                                                          template.getName(),
                                                          template.getSource(),
                                                          template.getVersion(),
                                                          field.getValue(),
                                                          field.getKey(),
                                                          type));
                            }
                        }

                        // Add Existing Entries
                        recordset.getValue().putAll(TYPES.get(recordset.getKey()));
                    }

                    TYPES.put(recordset.getKey(), recordset.getValue());
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
     * Adds the data types for every template saved by the given persistor.
     *
     * @param persistor to pull templates from.
     * @throws CoalescePersistorException on error
     */
    public static void addTemplates(ICoalescePersistor persistor) throws CoalescePersistorException
    {
        List<CoalesceEntityTemplate> templates = new ArrayList<>();

        LOGGER.debug("Refreshing Template Cache w/ ({})",
                     LOGGER.isTraceEnabled() ? persistor.getClass().getName() : persistor.getClass().getSimpleName());

        // Iterate Through All Templates
        for (ObjectMetaData metadata : persistor.getEntityTemplateMetadata())
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
                templates.add(persistor.getEntityTemplate(metadata.getKey()));

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
            catch (CoalescePersistorException e)
            {
                LOGGER.error(String.format(CoalesceErrors.TEMPLATE_LOAD,
                                           metadata.getName(),
                                           metadata.getSource(),
                                           metadata.getVersion()), e);
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

        Map<String, ECoalesceFieldDataTypes> results = new HashMap<>();

        for (Map<String, ECoalesceFieldDataTypes> types : TYPES.values())

        {
            results.putAll(types);
        }

        return results;
    }

    /**
     * @param key Template's key
     * @return a map of all the fields of the specified template and their data types.
     */
    public static Map<String, ECoalesceFieldDataTypes> getTemplateDataTypes(String key)
    {
        Map<String, ECoalesceFieldDataTypes> results = new HashMap<>();

        for (String recordset : getRecordsets(key))
        {
            results.putAll(TYPES.get(recordset));
        }

        return results;
    }

    /**
     * @return a set of all record sets defined by the templates
     */
    public static Set<String> getRecordsets()
    {
        Set<String> results = new HashSet<>();

        for (Set<String> recordsets : RECORDSETS.values())
        {
            results.addAll(recordsets);
        }

        return results;
    }

    /**
     * @param key Template's key
     * @return a map of all the fields of the specified template and their data types.
     */
    public static Set<String> getRecordsets(String key)
    {
        Set<String> results = new HashSet<>();
        Set<String> recordsets = RECORDSETS.get(key);

        if (recordsets != null)
        {
            results.addAll(recordsets);
        }

        return results;
    }

    /**
     * @param recordset of interest
     * @return a set of templates metadata that contains the specified recordset.
     */
    public static Set<ObjectMetaData> getTemplatesContainingRecordset(String recordset)
    {
        Set<ObjectMetaData> results = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : RECORDSETS.entrySet())
        {
            if (entry.getValue().contains(recordset))
            {
                results.add(META.get(entry.getKey()));
            }
        }

        return results;
    }

    /**
     * @param name of the recordset
     * @return a map of all the fields of the specified recordset and their data types.
     */
    public static Map<String, ECoalesceFieldDataTypes> getRecordsetDataTypes(String name)
    {
        Map<String, ECoalesceFieldDataTypes> results = new HashMap<>();
        Map<String, ECoalesceFieldDataTypes> types = TYPES.get(normalizer.normalize(name));

        if (types != null)
        {
            results.putAll(types);
        }

        return results;
    }

    /**
     * @param name of the recordset
     * @return all a list of templates that contain the specified record set.
     */
    public static Set<String> getTemplateKey(String name)
    {
        Set<String> results = new HashSet<>();
        String normalizedName = normalizer.normalize(name);

        for (Map.Entry<String, Set<String>> entry : RECORDSETS.entrySet())
        {
            if (entry.getValue().contains(normalizedName))
            {
                results.add(entry.getKey());
            }
        }

        return results;
    }

    /**
     * Logs how many fields of each type exists.
     */
    public static void logDataTypeBreakout()
    {

        int[] totals = new int[ECoalesceFieldDataTypes.values().length];
        Arrays.fill(totals, 0);

        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : getDataTypes().entrySet())
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
        if (TYPES.size() == 0)
        {
            Map<String, ECoalesceFieldDataTypes> coalesceentity = new HashMap<>();
            Map<String, ECoalesceFieldDataTypes> coalescelinkage = new HashMap<>();

            coalesceentity.put("coalesceentity.name", ECoalesceFieldDataTypes.STRING_TYPE);
            coalesceentity.put("coalesceentity.source", ECoalesceFieldDataTypes.STRING_TYPE);
            coalesceentity.put("coalesceentity.version", ECoalesceFieldDataTypes.STRING_TYPE);
            coalesceentity.put("coalesceentity.title", ECoalesceFieldDataTypes.STRING_TYPE);
            coalesceentity.put("coalesceentity.deleted", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            coalesceentity.put("coalesceentity.creator", ECoalesceFieldDataTypes.STRING_TYPE);
            coalesceentity.put("coalesceentity.objectkey", ECoalesceFieldDataTypes.GUID_TYPE);
            coalesceentity.put("coalesceentity.datecreated", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
            coalesceentity.put("coalesceentity.lastmodified", ECoalesceFieldDataTypes.DATE_TIME_TYPE);

            coalescelinkage.put("coalescelinkage.entity2key", ECoalesceFieldDataTypes.GUID_TYPE);
            coalescelinkage.put("coalescelinkage.entity2name", ECoalesceFieldDataTypes.STRING_TYPE);
            coalescelinkage.put("coalescelinkage.entity2source", ECoalesceFieldDataTypes.STRING_TYPE);
            coalescelinkage.put("coalescelinkage.entity2version", ECoalesceFieldDataTypes.STRING_TYPE);

            coalescelinkage.put("coalescelinkage.linkstatus", ECoalesceFieldDataTypes.STRING_TYPE);
            coalescelinkage.put("coalescelinkage.linktype", ECoalesceFieldDataTypes.STRING_TYPE);
            coalescelinkage.put("coalescelinkage.linklabel", ECoalesceFieldDataTypes.STRING_TYPE);

            TYPES.put("coalescelinkage", coalescelinkage);
            TYPES.put("coalesceentity", coalesceentity);
        }
    }

}
