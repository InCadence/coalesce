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

package com.incadencecorp.coalesce.services.client.common;

import com.incadencecorp.coalesce.services.api.common.BaseRequest;

/**
 * Class that retains details about asynchronous request to be able to query for results and
 * resubmit failed request.
 * 
 * @author n78554
 * @param <T>
 */
public class AsyncRequest<T extends BaseRequest> {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private String reason;
    private T request;

    /*--------------------------------------------------------------------------
    Constructor
    --------------------------------------------------------------------------*/

    /**
     * @param reason
     *            Reason for the request.
     * @param request
     *            Original request being made.
     */
    public AsyncRequest(final String reason, final T request) {
        this.reason = reason;
        this.request = request;
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * @return Returns the reason the request was made.
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return Returns the original request.
     */
    public T getRequest() {
        return request;
    }

}
