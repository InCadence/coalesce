package com.incadencecorp.coalesce.services.search.service.rest.controllers.api;

import java.rmi.RemoteException;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import com.incadencecorp.coalesce.services.search.service.data.model.ObjectData;

@RestController
public interface ITemplateDataSpring {

    @RequestMapping("/templates")
    @ResponseBody List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException;

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody CoalesceEntityTemplate getTemplate(@PathVariable String key) throws RemoteException;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody boolean setTemplate(String xml) throws RemoteException;
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody boolean setTemplateJson(String json) throws RemoteException;

    @RequestMapping(value = "/{key}/recordsets", method = RequestMethod.GET)
    @ResponseBody List<ObjectData> getRecordSets(@PathVariable String key) throws RemoteException;

    @RequestMapping(value = "/{key}/recordsets/{recordsetKey}/fields", method = RequestMethod.GET)
    @ResponseBody List<FieldData> getRecordSetFields(@PathVariable("key") String key, @PathVariable("recordsetKey") String recordsetKey)
            throws RemoteException;
    
}
