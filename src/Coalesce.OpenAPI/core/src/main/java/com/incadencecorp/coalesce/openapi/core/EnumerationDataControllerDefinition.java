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

import com.incadencecorp.coalesce.datamodel.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;
import com.incadencecorp.coalesce.services.common.jaxrs.IEnumerationDataControllerJaxRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("enumerations")
@Tag(name = "Enumerations", description = "This endpoint is used for retrieving details about enumerations used with the Data Fabric")
public interface EnumerationDataControllerDefinition extends IEnumerationDataControllerJaxRS {

    @Override
    @Operation(summary = "Get Enumerations", description = "Returns a list of enumerations that can be used within the Data Fabric")
    List<EnumMetadataPojoRecord> getEnumerations() throws RemoteException;

    @Override
    @Operation(summary = "Get Enumeration", description = "Returns the specified enumeration")
    EnumerationPojoEntity getEnumeration(String key) throws RemoteException;

    @Override
    @Operation(summary = "Get Values", description = "Returns the values allowed for the specified enumeration")
    List<EnumValuesRecord> getEnumerationValues(String key) throws RemoteException;

    @Override
    @Operation(summary = "Get Associated Values", description = "Returns the associated values allowed for the enumeration's value")
    Map<String, String> getEnumerationAssociatedValues(String key, String valuekey) throws RemoteException;
}
