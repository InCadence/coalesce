package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    @Path("/{entityKey}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceEntity getEntity(@PathParam("entityKey") String entityKey) throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/json")
    void setEntity(CoalesceEntity entity) throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/json")
    void setFields(String entityKey, Map<String, String> values) throws RemoteException;
    
    @GET
    @Path("/{entityKey}/section/{key}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceSection getSection(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey}/recordset/{key}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecordset getRecordset(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey}/field/{key}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    CoalesceRecord getRecord(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{entityKey}/field/{key}")
    @JsonView(Views.Entity.class)
    @Produces("application/json")
    ICoalesceField<?> getField(@PathParam("entityKey") String entityKey, @PathParam("key") String key) throws RemoteException;

}
