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
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.services.crud.service.data.jaxrs.IEntityDataControllerJaxRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("entity")
@Tag(name = "Entity", description = "This endpoint is used for CRUD operations on Coalesce data objects.")
public interface EntityDataControllerDefinition extends IEntityDataControllerJaxRS {

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}")
    @Operation(summary = "Retrieve", description = "Retrieves an entity in the specified format.")
    CoalesceEntity getEntity(String entityKey) throws RemoteException;

    @Operation(summary = "Retrieve", description = "Retrieves an entity in the specified format.")
    String getEntityAsXml(String entityKey) throws RemoteException;

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}")
    @Operation(summary = "Update", description = "Updates an existing entity merging the changes to preserve history.")
    void updateEntityAsJson(String entityKey, String json) throws RemoteException;

    @Operation(summary = "Update", description = "Updates an existing entity merging the changes to preserve history.")
    void updateEntityAsXml(String entityKey, String xml) throws RemoteException;

    @Path("/")
    @Operation(summary = "Create", description = "Creates a new entity.")
    String createEntityAsJson(String json) throws RemoteException;

    @Operation(summary = "Create", description = "Creates a new entity.")
    String createEntityAsXml(String xml) throws RemoteException;

    @Operation(summary = "Delete", description = "Marks the entities as deleted.")
    void deleteEntities(String[] keys) throws RemoteException;

    @Operation(summary = "Delete", description = "Marks the entity as deleted.")
    void deleteEntity(String entityKey) throws RemoteException;

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/section/{key}")
    @Operation(summary = "Get Section", description = "Returns the specified section of an entity.")
    CoalesceSection getSection(String entityKey, String key) throws RemoteException;

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/recordset/{key}")
    @Operation(summary = "Get Recordset", description = "Returns the specified recordset of an entity.")
    CoalesceRecordset getRecordset(String entityKey, String key) throws RemoteException;

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/record/{key}")
    @Operation(summary = "Get Record", description = "Returns the specified record of an entity.")
    CoalesceRecord getRecord(String entityKey, String key) throws RemoteException;

    @Path("/{entityKey:" + GUIDHelper.REGEX_UUID +  "}/field/{key}")
    @Operation(summary = "Get Fields", description = "Returns the fields of the specified record of an entity.")
    ICoalesceField<?> getField(String entityKey, String key) throws RemoteException;
}
