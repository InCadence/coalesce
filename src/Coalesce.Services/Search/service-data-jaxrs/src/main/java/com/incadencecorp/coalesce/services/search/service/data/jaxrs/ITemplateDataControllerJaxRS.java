package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import com.incadencecorp.coalesce.services.search.service.data.model.ObjectData;

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
    List<ObjectMetaData> getTemplates();

    @GET
    @Path("/{key}")
    @Produces("application/json")
    List<ObjectData> getRecordSets(@PathParam("key") String key);

    @GET
    @Path("/{key}/{recordsetKey}")
    @Produces("application/json")
    List<FieldData> getRecordSetFields(@PathParam("key") String key, @PathParam("recordsetKey") String recordsetKey);

}
