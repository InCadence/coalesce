package com.incadencecorp.coalesce.services.search.service.rest.controllers.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import com.incadencecorp.coalesce.services.search.service.data.model.ObjectData;

@RestController
public interface ITemplateDataSpring {

    @RequestMapping("/templates")
    List<ObjectMetaData> templates();

    @RequestMapping(value = "/templates/{key}", method = RequestMethod.GET)
    @ResponseBody List<ObjectData> getRecordSets(@PathVariable String key);

    @RequestMapping(value = "/templates/{key}/{recordsetKey}", method = RequestMethod.GET)
    @ResponseBody List<FieldData> getRecordSetFields(@PathVariable String key, @PathVariable String recordsetKey);

}
