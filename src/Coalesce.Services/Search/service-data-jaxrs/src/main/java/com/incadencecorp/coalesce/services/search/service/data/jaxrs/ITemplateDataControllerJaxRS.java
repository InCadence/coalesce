package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.model.CoalesceObjectImpl;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;

/**
 * JaxRs configuration for {@link TemplateDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("templates")
public interface ITemplateDataControllerJaxRS {

    @GET
    @Path("/")
    @Produces("application/json")
    List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException;

    @GET
    @Path("/{name}/{source}/{version}")
    @Produces("application/json")
    CoalesceEntity getTemplate(@PathParam("name") String name, @PathParam("source") String source, @PathParam("version") String version) throws RemoteException;
    
    @GET
    @Path("/{key}")
    @JsonView(Views.Template.class)
    @Produces("application/json")
    CoalesceEntity getTemplate(@PathParam("key") String key) throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/xml")
    boolean setTemplate(String xml) throws RemoteException;
    
    @POST
    @Path("/")
    @Consumes("application/json")
    boolean setTemplateJson(String json) throws RemoteException;

    @GET
    @Path("/{key}/recordsets")
    @Produces("application/json")
    List<CoalesceObjectImpl> getRecordSets(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key}/recordsets/{recordsetKey}/fields")
    @Produces("application/json")
    List<FieldData> getRecordSetFields(@PathParam("key") String key, @PathParam("recordsetKey") String recordsetKey)
            throws RemoteException;

}
