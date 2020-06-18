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

import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.crud.service.data.jaxrs.ILinkageDataControllerJaxRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("linkage")
@Tag(name = "Linkages", description = "This endpoint is used for linking entities with the Coalesce Data Fabric.")
public interface LinkageDataControllerDefinition extends ILinkageDataControllerJaxRS {

    @Operation(summary = "Unlink Entities", description = "Will unlink the entities specified by the parameters.")
    void unlink(List<GraphLink> links) throws RemoteException;

    @Operation(summary = "Link Entities", description = "Will link the entities specified by the parameters.")
    void link(List<GraphLink> links) throws RemoteException;

    @Operation(summary = "Get Linkages", description = "Returns the existing linkage of the specified entity.")
    List<GraphLink> retrieveLinkages(String key) throws RemoteException;
}
