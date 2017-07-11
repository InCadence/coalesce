package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import com.incadencecorp.coalesce.services.search.service.data.model.ObjectData;
import com.incadencecorp.unity.common.CallResult;

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
    @Path("/{key}")
    @Produces("application/json")
    CoalesceEntityTemplate getTemplate(@PathParam("key") String key) throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/json")
    CallResult setTemplate(CoalesceEntityTemplate template) throws RemoteException;

    @GET
    @Path("/{key}/recordsets")
    @Produces("application/json")
    List<ObjectData> getRecordSets(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key}/recordsets/{recordsetKey}/fields")
    @Produces("application/json")
    List<FieldData> getRecordSetFields(@PathParam("key") String key, @PathParam("recordsetKey") String recordsetKey)
            throws RemoteException;

}
