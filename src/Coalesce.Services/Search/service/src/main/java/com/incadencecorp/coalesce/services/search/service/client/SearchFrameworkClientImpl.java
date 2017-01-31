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

package com.incadencecorp.coalesce.services.search.service.client;

import java.rmi.RemoteException;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.JobRequest;
import com.incadencecorp.coalesce.services.api.common.MultipleResponse;
import com.incadencecorp.coalesce.services.api.common.StatusResponse;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.client.common.AbstractSearchClientImpl;
import com.incadencecorp.coalesce.services.search.service.SearchServiceImpl;

/**
 * This implementation uses the service directly w/o going through a WSDL.
 * 
 * @author Derek Clemenzi
 */
public class SearchFrameworkClientImpl extends AbstractSearchClientImpl {

    private SearchServiceImpl client;

    public SearchFrameworkClientImpl(CoalesceFramework framework)
    {
        client = new SearchServiceImpl(framework);
    }

    @Override
    protected StringResponse cancelJob(final JobRequest request) throws RemoteException
    {
        return client.cancelJob(request);
    }

    @Override
    protected MultipleResponse pickupJob(final JobRequest request) throws RemoteException
    {
        return client.pickupJobResults(request);
    }

    @Override
    protected StatusResponse getStatus(JobRequest request) throws RemoteException
    {
        return client.getJobStatus(request);
    }
    
    @Override
    protected SearchDataObjectResponse search(SearchDataObjectRequest request)
    {
        return client.searchDataObject(request);
    }

    @Override
    protected void processResponse(BaseResponse response)
    {
        // TODO Not Implemented
        // client.processResponse(response);
    }

    @Override
    protected void processFailedTask(BaseResponse response, int task, String reason)
    {
        // TODO Not Implemented
        // client.processFailedTask(response, task, reason);
    }

}
