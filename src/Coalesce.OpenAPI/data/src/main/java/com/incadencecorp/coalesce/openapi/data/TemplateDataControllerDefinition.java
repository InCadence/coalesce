/*-----------------------------------------------------------------------------'
 Copyright 2020 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.openapi.data;

import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.services.search.service.data.jaxrs.ITemplateDataControllerJaxRS;
import com.incadencecorp.coalesce.services.search.service.data.model.CoalesceObjectImpl;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("templates")
@Tag(name = "Templates", description = "This endpoint is used for sotring, retrieving, and registering Coalesce templates.")
public interface TemplateDataControllerDefinition extends ITemplateDataControllerJaxRS {

    @Operation(summary = "List", description = "Retrieves a list of saved templates.")
    List<ObjectMetaData> getEntityTemplateMetadata() throws RemoteException;

    @Path("/{name}/{source}/{version}")
    @Operation(summary = "Retrieve", description = "Retrieves an entity template.")
    CoalesceEntity getTemplate(String name, String source, String version) throws RemoteException;

    @Operation(summary = "Retrieve", description = "Retrieves an entity template.")
    String getTemplateXml(String name, String source, String version) throws RemoteException;

    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}")
    @Operation(summary = "Retrieve", description = "Retrieves an entity template.")
    CoalesceEntity getTemplate(String key) throws RemoteException;

    @Operation(summary = "Retrieve", description = "Retrieves an entity template.")
    String getTemplateXml(String key) throws RemoteException;

    @Path("/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Operation(summary = "Update", description = "Updates an existing entity template.")
    void updateTemplateJson(String key, String json) throws RemoteException;

    @Operation(summary = "Update", description = "Updates an existing entity template")
    void updateTemplateXml(String key, String xml) throws RemoteException;

    @Path("/")
    @Operation(summary = "Create", description = "Creates a new entity template")
    String createTemplateJson(String json) throws RemoteException;

    @Operation(summary = "Create", description = "Creates a new entity template")
    String createTemplateXml(String xml) throws RemoteException;

    @Operation(summary = "Register", description = "Will register the specified template creating indexes within the configured datastores.")
    void registerTemplate(String key) throws RemoteException;

    @Operation(summary = "Delete", description = "Marks an entity template as deleted.")
    void deleteTemplate(String key) throws RemoteException;

    @Path("/{key:" + GUIDHelper.REGEX_UUID +  "}/new")
    @Operation(summary = "Create New Entity", description = "Creates a new Entity from the specified template.")
    CoalesceEntity getNewEntity(String key) throws RemoteException;

    @Operation(summary = "Create New Entity", description = "Creates a new Entity from the specified template.")
    String getNewEntityXml(String key) throws RemoteException;

    @Operation(summary = "Get Recordsets", description = "Returns the recordsets defined in the specified template.")
    List<CoalesceObjectImpl> getRecordSets(String key) throws RemoteException;

    @Operation(summary = "Get Fields", description = "Returns the Fields defined in the specified template's recordset.")
    List<FieldData> getRecordSetFields(String key, String recordsetKey) throws RemoteException;
}
