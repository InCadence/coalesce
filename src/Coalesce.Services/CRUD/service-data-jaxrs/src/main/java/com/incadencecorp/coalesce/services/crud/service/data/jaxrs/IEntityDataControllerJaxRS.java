package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import java.rmi.RemoteException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ICoalesceField;

/**
 * JaxRS configuration used on {@link EnumerationDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("entity")
public interface IEntityDataControllerJaxRS {

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceEntity getEntity(@PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}.xml")
    @Produces("application/xml")
    String getEntityAsXml(@PathParam("entityKey") String entityKey) throws RemoteException;

    @POST
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}{ext:(.json)?}")
    @Consumes("application/json")
    void updateEntity(@PathParam("entityKey") String entityKey, String json) throws RemoteException;

    @POST
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}.xml")
    @Consumes("application/xml")
    void updateEntityAsXml(@PathParam("entityKey") String entityKey, String xml) throws RemoteException;

    @PUT
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}{ext:(.json)?}")
    @Consumes("application/json")
    void createEntity(@PathParam("entityKey") String entityKey, String json) throws RemoteException;

    @PUT
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}.xml")
    @Consumes("application/xml")
    void createEntityAsXml(@PathParam("entityKey") String entityKey, String xml) throws RemoteException;

    @DELETE
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    void deleteEntity(@PathParam("entityKey") String entityKey) throws RemoteException;

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}/section/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceSection getSection(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}/recordset/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecordset getRecordset(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}/record/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecord getRecord(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}/field/{key}{ext:(.json)?}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    ICoalesceField<?> getField(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

}
