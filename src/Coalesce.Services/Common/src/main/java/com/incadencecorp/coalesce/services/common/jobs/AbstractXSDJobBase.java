/*-----------------------------------------------------------------------------'
Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.common.jobs;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.ICoalesceResponseTypeBase;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceFrameworkJob;
import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;

/**
 * Abstract base class to be extended to create jobs.
 *
 * @author Derek C.
 * @param <T> Request that this job handles; must extend {@link BaseRequest}.
 * @param <Y> Response that this job produces; must extend {@link BaseResponse}.
 */
public abstract class AbstractXSDJobBase<T extends BaseRequest, Y extends BaseResponse, X extends ICoalesceResponseType<?>>
        extends AbstractCoalesceFrameworkJob<T, Y, X> {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXSDJobBase.class);

    private T request;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Creates a job based off of the request and initializes the response
     * object.
     *
     * @param request the request
     */
    public AbstractXSDJobBase(T request)
    {
        super(request);
    }

    // ----------------------------------------------------------------------//
    // Public Properties
    // ----------------------------------------------------------------------//

    @Override
    public final boolean isAsync()
    {
        return request.isAsyncCall();
    }

    /**
     * @return the original request that spawned the job
     */
    public final T getRequest()
    {
        return request;
    }

    /**
     * @return the response with the job ID and status. Also includes the
     *         results if completed or an error message if an exception was
     *         thrown with the status of JOB_FAILED.
     */
    public final BaseResponse getResponse()
    {

        ICoalesceResponseTypeBase response = null;

        if (isDone())
        {
            try
            {
                // Get Response
                response = getFuture().get();
                if (getJobStatus() == EJobStatus.COMPLETE)
                {
                    response.setStatus(EResultStatus.SUCCESS);
                }
                else
                {
                    response.setStatus(EResultStatus.FAILED_PENDING);
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                LOGGER.error(e.getMessage(), e);

                // Create Response w/ Error Message
                response = createResponse();
                response.setStatus(EResultStatus.FAILED);
            }
        }
        else
        {
            // Create Response w/o Results
            response = createResponse();
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                response.setStatus(EResultStatus.SUCCESS);
            }
            else
            {
                response.setStatus(EResultStatus.FAILED_PENDING);
            }

            // Check for Race Condition (Job completed after isDone() check and
            // before response
            // creation)
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                setJobStatus(EJobStatus.IN_PROGRESS);
            }
        }

        // TODO UNDO
//         response.setJobId(getJobID().toString());

        return (BaseResponse) response;
    }
}
