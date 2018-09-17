package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.framework.datamodel.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceEntity getEntity(
            @PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/xml")
    @Produces(MediaType.APPLICATION_XML)
    String getEntityAsXml(@PathParam("entityKey") String entityKey) throws RemoteException;

    @PUT
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}{ext:(/json)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateEntityAsJson(@PathParam("entityKey") String entityKey, String json) throws RemoteException;

    @PUT
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/xml")
    @Consumes(MediaType.APPLICATION_XML)
    void updateEntityAsXml(@PathParam("entityKey") String entityKey, String xml) throws RemoteException;

    @POST
    @Path("/{ext:(json)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createEntityAsJson(String json) throws RemoteException;

    @POST
    @Path("/xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    String createEntityAsXml(String xml) throws RemoteException;

    @DELETE
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    void deleteEntities(String[] keys) throws RemoteException;

    @DELETE
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}")
    void deleteEntity(@PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/section/{key}{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceSection getSection(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/recordset/{key}{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceRecordset getRecordset(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/record/{key}{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceRecord getRecord(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/field/{key}{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    ICoalesceField<?> getField(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

}
