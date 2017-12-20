package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.framework.datamodel.*;

import javax.ws.rs.*;
import java.rmi.RemoteException;
import java.util.List;

/**
 * JaxRS configuration used on {@link EnumerationDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("entity")
public interface IEntityDataControllerJaxRS {

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceEntity getEntity(
            @PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}.xml")
    @Produces("application/xml")
    String getEntityAsXml(@PathParam("entityKey") String entityKey) throws RemoteException;

    @POST
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}{ext:(.json)?}")
    @Consumes("application/json")
    void updateEntity(@PathParam("entityKey") String entityKey, String json) throws RemoteException;

    @POST
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}.xml")
    @Consumes("application/xml")
    void updateEntityAsXml(@PathParam("entityKey") String entityKey, String xml) throws RemoteException;

    @PUT
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}{ext:(.json)?}")
    @Consumes("application/json")
    void createEntity(@PathParam("entityKey") String entityKey, String json) throws RemoteException;

    @PUT
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}.xml")
    @Consumes("application/xml")
    void createEntityAsXml(@PathParam("entityKey") String entityKey, String xml) throws RemoteException;

    @DELETE
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}")
    void deleteEntity(@PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/section/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceSection getSection(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/recordset/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecordset getRecordset(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/record/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecord getRecord(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/field/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    ICoalesceField<?> getField(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

}
