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

package com.incadencecorp.coalesce.services.search.client.jaxws;

import com.incadencecorp.coalesce.services.api.common.*;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.api.search.SearchManager;
import com.incadencecorp.coalesce.services.client.common.jaxws.util.JAXWSUtil;
import com.incadencecorp.coalesce.services.search.client.common.AbstractSearchClientImpl;
import org.geotools.filter.Capabilities;

import java.net.URL;
import java.rmi.RemoteException;

public class SearchJaxwsClientImpl extends AbstractSearchClientImpl {

    private SearchManager client;

    /**
     * Constructs the client for the provided URL.
     *
     * @param url
     */
    public SearchJaxwsClientImpl(URL url)
    {
        client = JAXWSUtil.createClient(SearchManager.class, url);
    }

    @Override
    protected StringResponse cancelJob(JobRequest request) throws RemoteException
    {
        return client.cancelJob(request);
    }

    @Override
    protected StatusResponse getStatus(JobRequest request) throws RemoteException
    {
        return client.getJobStatus(request);
    }

    @Override
    protected MultipleResponse pickupJob(JobRequest request) throws RemoteException
    {
        return client.pickupJobResults(request);
    }

    @Override
    protected SearchDataObjectResponse search(SearchDataObjectRequest request)
    {
        return client.searchDataObject(request);
    }

    @Override
    public Capabilities getCapabilities()
    {
        // TODO Need to read this via the SOAP API
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }

    @Override
    protected void processResponse(BaseResponse response)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processFailedTask(BaseResponse response, int task, String reason)
    {
        // TODO Auto-generated method stub

    }

}
