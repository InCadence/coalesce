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

package com.incadencecorp.coalesce.services.crud.client.jaxws;

import java.net.URL;
import java.rmi.RemoteException;

import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.JobRequest;
import com.incadencecorp.coalesce.services.api.common.MultipleResponse;
import com.incadencecorp.coalesce.services.api.common.StatusResponse;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.CrudManager;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.client.common.jaxws.util.JAXWSUtil;
import com.incadencecorp.coalesce.services.crud.client.common.AbstractCrudClientImpl;

public class CrudJaxwsClientImpl extends AbstractCrudClientImpl {

    private CrudManager client;

    /**
     * Constructs the client for the provided URL.
     * 
     * @param url
     */
    public CrudJaxwsClientImpl(URL url)
    {
        client = JAXWSUtil.createClient(CrudManager.class, url);
    }

    @Override
    protected StringResponse updateDataObjectStatus(DataObjectUpdateStatusRequest request)
    {
        return client.updateDataObjectStatus(request);
    }

    @Override
    protected StringResponse createDataObject(DataObjectXmlRequest request)
    {
        return client.createDataObject(request);
    }

    @Override
    protected StringResponse updateDataObject(DataObjectXmlRequest request)
    {
        return client.updateDataObject(request);
    }

    @Override
    protected StringResponse retrieveDataObject(DataObjectKeyRequest request)
    {
        return client.retrieveDataObject(request);
    }

    @Override
    protected StringResponse updateLinkages(DataObjectLinkRequest request)
    {
        return client.updateDataObjectLinkages(request);
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
