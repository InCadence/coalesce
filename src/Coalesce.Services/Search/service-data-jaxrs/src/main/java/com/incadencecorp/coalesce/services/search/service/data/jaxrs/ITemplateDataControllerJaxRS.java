package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
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
    @Produces(MediaType.APPLICATION_JSON)
    List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException;

    @GET
    @Path("/{name}/{source}/{version}{ext:(/json)?}")
    @JsonView(Views.Template.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceEntity getTemplate(@PathParam("name") String name, @PathParam("source") String source, @PathParam("version") String version) throws RemoteException;

    @GET
    @Path("/{name}/{source}/{version}/xml")
    @Produces(MediaType.APPLICATION_XML)
    String getTemplateXml(@PathParam("name") String name, @PathParam("source") String source, @PathParam("version") String version) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}{ext:(/json)?}")
    @JsonView(Views.Template.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceEntity getTemplate(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/xml")
    @JsonView(Views.Template.class)
    @Produces(MediaType.APPLICATION_XML)
    String getTemplateXml(@PathParam("key") String key) throws RemoteException;

    @PUT
    @Path("/{key:" + GUIDHelper.REGEX_UUID + "}{ext:(/json)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateTemplateJson(@PathParam("key") String key, String json) throws RemoteException;

    @PUT
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/xml")
    @Consumes(MediaType.APPLICATION_XML)
    void updateTemplateXml(@PathParam("key") String key, String xml) throws RemoteException;

    @POST
    @Path("/{ext:(json)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createTemplateJson(String json) throws RemoteException;

    @POST
    @Path("/xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    String createTemplateXml(String xml) throws RemoteException;

    @PUT
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/register")
    void registerTemplate(@PathParam("key") String key) throws RemoteException;

    @DELETE
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}")
    void deleteTemplate(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/new{ext:(/json)?}")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_JSON)
    CoalesceEntity getNewEntity(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/new/xml")
    @JsonView(Views.Entity.class)
    @Produces(MediaType.APPLICATION_XML)
    String getNewEntityXml(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/recordsets")
    @JsonView(Views.Template.class)
    @Produces(MediaType.APPLICATION_JSON)
    List<CoalesceObjectImpl> getRecordSets(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/recordsets/{recordsetKey}/fields")
    @JsonView(Views.Template.class)
    @Produces(MediaType.APPLICATION_JSON)
    List<FieldData> getRecordSetFields(@PathParam("key") String key, @PathParam("recordsetKey") String recordsetKey)
            throws RemoteException;


}
