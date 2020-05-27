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

import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.services.common.jaxrs.IPropertyControllerJaxRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("property")
@Tag(name = "Property", description = "This endpoint is used for sharing properties between different data")
public interface PropertyControllerDefinition extends IPropertyControllerJaxRS {

    @Override
    @Operation(summary = "Get JSON", description = "GET a JSON document.")
    String getJsonConfiguration(String name) throws RemoteException;

    @Override
    @Operation(summary = "Save JSON", description = "Saves a JSON document.")
    void setJsonConfiguration(String name, String json) throws RemoteException;

    @Override
    @Operation(summary = "Get Property", description = "Gets property's value")
    String getProperty(String name) throws RemoteException;

    @Override
    @Operation(summary = "Set Property", description = "Updates the property's value")
    void setProperty(String name, String value) throws RemoteException;

    @Override
    @Operation(summary = "Get Properties", description = "Gets all properties as key / value pairs.")
    Map<String, String> getProperties() throws RemoteException;

    @Override
    @Operation(summary = "Get Properties", description = "Gets properties as key / value pairs of specified properties.")
    Map<String, String> getProperties(String[] names) throws RemoteException;

    @Override
    @Operation(summary = "Set Properties", description = "Updates the values of multiple properties.")
    void setProperties(Map<String, String> values) throws RemoteException;

    @Override
    @Operation(summary = "Get Principal", description = "Returns user's principal used by front-ends for queries or to determine what to display.")
    ICoalescePrincipal whoami();
}
