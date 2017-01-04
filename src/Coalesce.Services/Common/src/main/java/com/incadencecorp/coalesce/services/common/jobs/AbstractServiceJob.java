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

package com.incadencecorp.coalesce.services.common.jobs;

import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
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
public abstract class AbstractServiceJob<T extends BaseRequest, Y extends ICoalesceResponseType<List<X>>, X extends ICoalesceResponseType<?>>
        extends AbstractCoalesceFrameworkJob<T, Y, X> {

    /**
     * Creates a job based off of the request and initializes the response
     * object.
     *
     * @param request the request
     */
    public AbstractServiceJob(T request)
    {
        super(request);
    }

    @Override
    public final boolean isAsync()
    {
        return getParams().isAsyncCall();
    }

}
