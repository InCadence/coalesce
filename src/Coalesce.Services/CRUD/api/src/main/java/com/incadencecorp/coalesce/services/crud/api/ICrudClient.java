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

package com.incadencecorp.coalesce.services.crud.api;

import java.rmi.RemoteException;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.services.api.IBaseClient;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;

public interface ICrudClient extends IBaseClient<ICrudEvents>{

    /**
     * Updates the status of the specified object(s).
     *
     * @param tasks
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws RemoteException
     */
    boolean updateDataObjectStatus(DataObjectStatusType... tasks) throws RemoteException;

    /**
     * Asynchronously updates the status of the specified object(s).
     *
     * @param tasks
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String updateDataObjectStatusAsync(DataObjectStatusType... tasks) throws RemoteException;

    /**
     * Updates the linkages between objects.
     *
     * @param tasks
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws RemoteException
     */
    boolean updateLinkages(DataObjectLinkType... tasks) throws RemoteException;

    /**
     * Asynchronously updates the linkages between objects.
     *
     * @param tasks
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String updateLinkagesAsync(DataObjectLinkType... tasks) throws RemoteException;

    /**
     * Retrieves data objects from DSS at the specified versions.
     *
     * @param keys
     * @return Returns a list of objects retrieved from the database.
     * @throws RemoteException
     */
    Results<CoalesceEntity>[] retrieveDataObjects(String... keys) throws RemoteException;

    /**
     * Asynchronously retrieves data objects from DSS at the specified versions.
     *
     * @param keys
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String retrieveDataObjectsAsync(String... keys) throws RemoteException;

    /**
     * Retrieves data objects from DSS at the specified versions.
     *
     * @param tasks
     * @return Returns a list of objects retrieved from the database.
     * @throws RemoteException
     */
    Results<CoalesceEntity>[] retrieveDataObjects(DataObjectKeyType... tasks) throws RemoteException;

    /**
     * Asynchronously retrieves data objects from DSS at the specified versions.
     *
     * @param tasks
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String retrieveDataObjectsAsync(DataObjectKeyType... tasks) throws RemoteException;

    /**
     * Stores the objects to the DSS database.
     *
     * @param objects
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws RemoteException
     */
    boolean createDataObject(CoalesceEntity... objects) throws RemoteException;

    /**
     * Asynchronously stores the objects to the DSS database.
     *
     * @param objects
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String createDataObjectAsync(CoalesceEntity... objects) throws RemoteException;

    /**
     * Stores the changes to the objects to the DSS database.
     *
     * @param objects
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws RemoteException
     */
    boolean updateDataObject(CoalesceEntity... objects) throws RemoteException;

    /**
     * Asynchronously stores the changes to the objects to the DSS database.
     *
     * @param objects
     * @return the job ID. Call {@link #pickupJob(String...)} to get the
     *         response.
     * @throws RemoteException
     */
    String updateDataObjectAsync(CoalesceEntity... objects) throws RemoteException;

}
