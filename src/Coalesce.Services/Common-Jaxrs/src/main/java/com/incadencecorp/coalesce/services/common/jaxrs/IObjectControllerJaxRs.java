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

import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.services.api.IObjectController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.rmi.RemoteException;

/**
 * @author Derek Clemenzi
 */
public interface IObjectControllerJaxRs<T> extends IObjectController<T> {

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Override
    String save(final T object) throws RemoteException;

    @POST
    @Path("/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Override
    void update(@PathParam("key") final String key, final T object) throws RemoteException;

    @GET
    @Path("/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Override
    T load(@PathParam("key") final String key) throws RemoteException;

    @DELETE
    @Path("/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Override
    void delete(@PathParam("key") final String key) throws RemoteException;

}
