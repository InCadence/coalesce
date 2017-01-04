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

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.services.api.ICoalesceEvents;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusActionType;

/**
 * This empty class does not implement any methods. It should be extended and only needed methods
 * should be overridden and implemented by the client.
 *
 * @author bearyman
 */
public interface ICrudEvents extends ICoalesceEvents {

    /**
     * Event raised when a job retrieving entities has completed..
     *
     * @param jobId
     * @param results
     */
    void retrieveDataObjectsJobCallback(String jobId, Results<CoalesceEntity>[] results);


    /*--------------------------------------------------------------------------
    Failed Events
    --------------------------------------------------------------------------*/

    /**
     * Event raised when a job creating objects fails.
     *
     * @param jobId
     *            ID of the job.
     * @param key
     *            Object's key.
     * @param reason
     */
    void createDataObjectsFailed(String jobId, String key, String reason);

    /**
     * Event raised when a job linking objects fails.
     *
     * @param jobId
     * @param task
     * @param reason
     *            Reason for the failure.
     */
    void updateLinkagesFailed(String jobId, DataObjectLinkType task, String reason);

    /**
     * Event raised when a job retrieving objects fails.
     *
     * @param jobId
     *            ID of the job.
     * @param key
     *            Object's key.
     * @param version
     *            Object's version.
     * @param reason
     *            Reason for the failure.
     */
    void retrieveDataObjectsByKeyFailed(String jobId, String key, int version, String reason);

    /**
     * Event raised when a job un-hiding objects fails.
     *
     * @param jobId
     *            ID of the job.
     * @param key
     *            Object's key.
     * @param action
     * @param reason
     *            Reason for the failure.
     */
    void updateDataObjectStatusFailed(String jobId, String key, DataObjectStatusActionType action,
            String reason);

    
    /**
     * Event raised when a job updating objects fails.
     *
     * @param jobId
     *            ID of the job.
     * @param xml
     *            Object's XML.
     * @param reason
     *            Reason for the failure.
     */
    void updateDataObjectsFailed(String jobId, String xml, String reason);


}
