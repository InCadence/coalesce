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

package com.incadencecorp.coalesce.services.api;

import java.io.Closeable;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Map;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.ResultsType;

public interface IBaseClient<T extends ICoalesceEvents> extends Closeable {

    /**
     * Cancels the specified job(s).
     * 
     * @param jobId
     * @return Returns <code>true</code> if successful. If <code>false</code>
     *         call getLastResult() for additional details.
     * @throws RemoteException
     */
    boolean cancelJob(String... jobId) throws RemoteException;

    /**
     * Calls {@link #getJobStatus(String...)} with currently pending job IDs as
     * arguments.
     * 
     * @return Returns the statuses of the jobs pending. Does not update from
     *         the server.
     * @throws RemoteException
     */
    Map<String, EJobStatus> getAllJobStatus() throws RemoteException;

    /**
     * @param jobId
     * @return Returns the statuses of the jobs being requested. Updated from
     *         the server.
     * @throws RemoteException
     */
    Map<String, EJobStatus> getJobStatus(String... jobId) throws RemoteException;

    /**
     * Calls {@link #pickupJob(String...)} with currently pending job IDs as
     * arguments.
     * 
     * @throws RemoteException
     */
    void pickupAllJobs() throws RemoteException;

    /**
     * Returns the response of the specified job(s) or their current status if
     * not completed.
     * 
     * @param jobId
     * @throws RemoteException
     */
    void pickupJob(String... jobId) throws RemoteException;

    /**
     * @return Returns the complete results of the last synchronous request.
     *         Asynchronous results are handled by throwing events.
     */
    ResultsType[] getLastResult();

    /**
     * @return Returns a list of pending job IDs
     */
    String[] getPendingJob();

    /**
     * @param jobId
     * @return Returns the request for the given job ID.
     */
    BaseRequest getRequest(String jobId);

    /**
     * @param jobId
     * @return Returns the reason for the given job ID.
     */
    String getReason(String jobId);

    /**
     * Sets the principal to be used when making request to the server.
     * 
     * @param principal
     */
    void setPrincipal(Principal principal);

    /**
     * @return Returns the current user ID
     */
    Principal getPrincipal();

    /**
     * Registers an event listener
     * 
     * @param listener
     */
    void addListener(T listener);

    /**
     * Unregisters an event listener
     * 
     * @param listener
     */
    void removeListener(T listener);

    /**
     * @param listener
     * @return Returns <code>true</code> if already listening.
     */
    boolean isListening(T listener);

}
