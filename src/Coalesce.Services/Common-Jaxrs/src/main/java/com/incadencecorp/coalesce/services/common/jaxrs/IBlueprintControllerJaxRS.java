/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.common.jaxrs;

import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;
import com.incadencecorp.coalesce.services.common.api.IBlueprintController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Extends {@link IBlueprintController} and appends JAX-RS annotations.
 *
 * @author Derek Clemenzi
 */
@Path("blueprints")
interface IBlueprintControllerJaxRS extends IBlueprintController {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getBlueprints();

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    Graph getBlueprint(@PathParam("name") String name) throws RemoteException;


    @POST
    @Path("edit/{name}/")
    @Consumes(MediaType.APPLICATION_JSON)
    void editBlueprint(@PathParam("name") String name, String changes) throws Exception;
}
