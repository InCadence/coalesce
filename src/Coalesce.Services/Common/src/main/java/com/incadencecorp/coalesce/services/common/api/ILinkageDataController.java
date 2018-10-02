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

package com.incadencecorp.coalesce.services.common.api;

import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Defines the Linkage Controller API
 *
 * @author Derek Clemenzi
 */
public interface ILinkageDataController {

    /**
     * Specifies a list of tasks to unlink specified entities.
     *
     * @param links list of task
     * @throws RemoteException on error
     */
    void unlink(List<GraphLink> links) throws RemoteException;

    /**
     * Specifies a list of tasks to link specified entities.
     *
     * @param links list of task
     * @throws RemoteException on error
     */
    void link(List<GraphLink> links) throws RemoteException;

    /**
     * @param key of entity to return linkages from.
     * @return the linkages of the specified entity
     * @throws RemoteException on error
     */
    List<GraphLink> retrieveLinkages(String key) throws RemoteException;
}
