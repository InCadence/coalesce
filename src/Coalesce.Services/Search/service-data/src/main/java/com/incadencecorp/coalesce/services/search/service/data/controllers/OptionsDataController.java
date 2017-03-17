package com.incadencecorp.coalesce.services.search.service.data.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.services.search.service.data.model.Option;

/**
 * Responsible for storing and retrieving user search options.
 * 
 * @author Derek Clemenzi
 */
public class OptionsDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsDataController.class);

    private static final Map<String, List<Option>> OPTIONS = new HashMap<String, List<Option>>();;

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
    public List<Option> loadOptions(String key)
    {
        List<Option> results = new ArrayList<Option>();

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
    public void saveOptions(String key, List<Option> options)
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
