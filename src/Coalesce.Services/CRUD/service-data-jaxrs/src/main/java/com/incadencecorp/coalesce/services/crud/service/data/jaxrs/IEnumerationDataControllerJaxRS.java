package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.MetadataPojoRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.ValuesPojoRecord;

/**
 * JaxRS configuration used on {@link EnumerationDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("enumerations")
public interface IEnumerationDataControllerJaxRS {

    @GET
    @Path("/")
    @Produces("application/json")
    List<MetadataPojoRecord> getEnumerations() throws RemoteException;

    @GET
    @Path("/{key}")
    @Produces("application/json")
    EnumerationPojoEntity getEnumeration(@PathParam("key") String key) throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/json")
    void setEnumeration(EnumerationPojoEntity value) throws RemoteException;
    
    @GET
    @Path("/{key}/values")
    @Produces("application/json")
    List<ValuesPojoRecord> getEnumerationValues(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key}/values/{valuekey}/associatedvalues")
    @Produces("application/json")
    Map<String, String> getEnumerationAssociatedValues(@PathParam("key") String key, @PathParam("valuekey") String valuekey) throws RemoteException;

}
