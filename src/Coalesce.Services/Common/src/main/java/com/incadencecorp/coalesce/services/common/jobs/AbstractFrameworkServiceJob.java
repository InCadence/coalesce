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
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;

/**
 * Abstract base class that extends {@link Abstrac
 *
 * @author Derek C.
 * @param <INPUT> Request that this job handles; must extend {@link BaseRequest}.
 * @param <OUTPUT> Response that this job produces; must extend {@link BaseResponse}.
 */
public abstract class AbstractFrameworkServiceJob<INPUT extends BaseRequest, OUTPUT extends ICoalesceResponseType<List<TASKOUTPUT>>, TASKOUTPUT extends ICoalesceResponseType<?>>
        extends AbstractServiceJob<INPUT, OUTPUT, TASKOUTPUT, CoalesceFramework> {

    /**
     * Creates a job based off of the request and initializes the response
     * object.
     *
     * @param request the request
     */
    public AbstractFrameworkServiceJob(INPUT request)
    {
        super(request);
    }

}
