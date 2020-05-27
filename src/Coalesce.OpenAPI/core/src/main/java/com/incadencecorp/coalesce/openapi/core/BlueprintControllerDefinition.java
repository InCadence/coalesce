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

package com.incadencecorp.coalesce.openapi.core;

import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.common.jaxrs.IBlueprintControllerJaxRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("blueprints")
@Tag(name = "Blueprints", description = "This endpoint is used for managing the blueprint configurations.")
public interface BlueprintControllerDefinition extends IBlueprintControllerJaxRS {

    @Override
    @Operation(summary = "Get Blueprints", description = "Returns a list of installed blueprints which can be modified.")
    List<String> getBlueprints();

    @Override
    @Operation(summary = "Get Blueprint", description = "Returns the XML of the specified blueprint.")
    String getXML(String filename, String id) throws Exception;

    @Override
    @Operation(summary = "Edit Bean", description = "Updates the bean based on te id within the blueprint. A backup is made allowing the change to be reverted.")
    void editBlueprint(String name, String changes) throws Exception;

    @Override
    @Operation(summary = "Delete Bean", description = "Delete a bean based on te id within the blueprint. A backup is made allowing the change to be reverted.")
    void removeBean(String name, String json) throws Exception;

    @Override
    @Operation(summary = "Get Blueprint Graph", description = "Returns a graph of the blueprint for rendering within the Manager front-end.")
    Graph getBlueprint(String name) throws RemoteException;

    @Override
    @Operation(summary = "Undo", description = "Reverts a blueprint back to the previous version.")
    void undo(String filename) throws Exception;
}
