package com.incadencecorp.coalesce.services.search.service.rest.controllers.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.service.data.model.Option;

@RestController
public interface IOptionsDataSpring {

    @RequestMapping(value = "/options/{key}", method = RequestMethod.GET)
    @ResponseBody List<Option> loadOptions(@PathVariable String key);

    @RequestMapping(value = "/options/{key}", method = RequestMethod.POST)
    @ResponseBody void saveOptions(@PathVariable String key, @RequestBody List<Option> options);

    @RequestMapping(value = "/options", method = RequestMethod.GET)
    @ResponseBody List<String> getOptions();

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody SearchDataObjectResponse search(@RequestBody List<Option> options);

}
