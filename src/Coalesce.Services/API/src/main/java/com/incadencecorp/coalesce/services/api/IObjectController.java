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

package com.incadencecorp.coalesce.services.api;

import java.rmi.RemoteException;

/**
 * @author Derek Clemenzi
 */
public interface IObjectController<T> {

    /**
     * Saves the Object.
     *
     * @param object to save
     */
    String save(final T object) throws RemoteException;

    /**
     * Updates the Object
     *
     * @param object to update
     * @param key    of Object
     */
    void update(final String key, final T object) throws RemoteException;

    /**
     * @param key of Object
     * @return specified Object
     */
    T load(final String key) throws RemoteException;

    /**
     * Deletes the specified Object.
     *
     * @param key of Object
     */
    void delete(final String key) throws RemoteException;

}
