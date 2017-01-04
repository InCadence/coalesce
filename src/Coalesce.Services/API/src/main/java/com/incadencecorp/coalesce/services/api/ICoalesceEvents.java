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

import java.util.EventListener;

import com.incadencecorp.coalesce.api.EResultStatus;

/**
 * Interface defines the events that can be raised when executing jobs asynchronously on a service
 * that extends {@link EventListener}.
 * 
 * @author n78554
 */
public interface ICoalesceEvents extends EventListener {

    /**
     * Event raised when a job has failed to execute.
     * 
     * @param jobId
     *            ID of the job that has failed to execute.
     */
    void failedCallback(String jobId);

    /**
     * Event raised when querying the status of a job or picking up the response of an incomplete
     * job.
     * 
     * @param jobId
     * @param status
     *            {@link EResultStatus}
     */
    void statusCallback(String jobId, EResultStatus status);

}
