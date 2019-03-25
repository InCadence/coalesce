/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.common.jaxrs;

import com.incadencecorp.coalesce.datamodel.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.services.api.IEnumerationDataController;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * JaxRS configuration used on {@link EnumerationDataControllerJaxRS}
 *
 * @author Derek Clemenzi
 */
@Path("enumerations")
public interface IEnumerationDataControllerJaxRS extends IEnumerationDataController {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    List<EnumMetadataPojoRecord> getEnumerations() throws RemoteException;

    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    EnumerationPojoEntity getEnumeration(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key}/values")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    List<EnumValuesRecord> getEnumerationValues(@PathParam("key") String key) throws RemoteException;

    @GET
    @Path("/{key}/values/{valuekey}/associatedvalues")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    Map<String, String> getEnumerationAssociatedValues(@PathParam("key") String key, @PathParam("valuekey") String valuekey)
            throws RemoteException;

}
