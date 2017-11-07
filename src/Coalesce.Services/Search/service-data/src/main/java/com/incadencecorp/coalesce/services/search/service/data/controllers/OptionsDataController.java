/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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
package com.incadencecorp.coalesce.services.search.service.data.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;

/**
 * Responsible for storing and retrieving user search options.
 *
 * @author Derek Clemenzi
 */
public class OptionsDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsDataController.class);

    private static final Map<String, List<SearchCriteria>> OPTIONS = new HashMap<>();

    /**
     * Default Constructor
     */
    public OptionsDataController()
    {

    }

    /**
     * @param key
     * @return a list of selected options that were saved with the provided key.
     */
    public List<SearchCriteria> loadOptions(String key)
    {
        List<SearchCriteria> results = new ArrayList<SearchCriteria>();

        LOGGER.debug("Loading Options [Key: ({})]", key);

        if (OPTIONS.containsKey(key))
        {
            results.addAll(OPTIONS.get(key));
        }
        else
        {
            LOGGER.warn(String.format(CoalesceErrors.NOT_FOUND, "Saved Options", key));
        }

        return results;
    }

    /**
     * Saves options selected by a user.
     *
     * @param key
     * @param options
     */
    public void saveOptions(String key, List<SearchCriteria> options)
    {
        LOGGER.debug("Saving Options [Key: ({})]", key);

        OPTIONS.put(key, options);
    }

    /**
     * @return a list of options saved.
     */
    public List<String> getOptions()
    {
        LOGGER.debug("Retrieving Options");

        List<String> results = new ArrayList<String>();

        results.addAll(OPTIONS.keySet());

        return results;
    }

}
