package com.incadencecorp.coalesce.services.search.service.rest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.service.data.controllers.OptionsDataController;
import com.incadencecorp.coalesce.services.search.service.data.model.Option;

@RestController
public class OptionsDataControllerSpring {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsDataController.class);

    private static final Map<String, List<Option>> OPTIONS = new HashMap<String, List<Option>>();;
    
    private ISearchClient client;
    
    /**
     * Default Constructor
     */
    public OptionsDataControllerSpring() {
        
    }
    
    public OptionsDataControllerSpring(ISearchClient value) {
        client = value;
    }

    @RequestMapping(value = "/options/{key}", method = RequestMethod.GET)
    public @ResponseBody List<Option> loadOptions(@PathVariable String key)
    {
        List<Option> results = new ArrayList<Option>();

        LOGGER.trace("Loading Options");

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

    @RequestMapping(value = "/options/{key}", method = RequestMethod.POST)
    public @ResponseBody void saveOptions(@PathVariable String key, @RequestBody List<Option> options)
    {
        LOGGER.trace("Saving Options");

        OPTIONS.put(key, options);
    }

    @RequestMapping(value = "/options", method = RequestMethod.GET)
    public @ResponseBody List<String> getOptions()
    {
        LOGGER.trace("Quering Options");

        List<String> results = new ArrayList<String>();

        results.addAll(OPTIONS.keySet());

        return results;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public @ResponseBody SearchDataObjectResponse search(@RequestBody List<Option> options)
    {
        LOGGER.trace("Submitting Options");

        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        List<Filter> filters = new ArrayList<Filter>();
        
        for (Option option : options)
        {
            PropertyName property = CoalescePropertyFactory.getFieldProperty(option.getRecordset(), option.getField());
                       
            
            switch (option.getComparer()) {
            case "=":
                filters.add(ff.equal(property, ff.literal(option.getValue()), option.isMatchCase()));
                break;
            case "!=":
                filters.add(ff.notEqual(property, ff.literal(option.getValue()), option.isMatchCase()));
                break;
            }

        }


        try
        {
            Filter filter = ff.and(filters);
            
            LOGGER.debug("Filter: {}", filter.toString());
            
            return client.search(filter, 1);
            
        }
        catch (CoalesceException e)
        {
            throw new RuntimeException(e);
        }
        

    }

}
