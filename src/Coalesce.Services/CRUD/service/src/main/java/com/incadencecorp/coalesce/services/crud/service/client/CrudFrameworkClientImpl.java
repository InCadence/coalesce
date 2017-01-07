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

package com.incadencecorp.coalesce.services.crud.service.client;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.JobRequest;
import com.incadencecorp.coalesce.services.api.common.MultipleResponse;
import com.incadencecorp.coalesce.services.api.common.StatusResponse;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.crud.client.common.AbstractCrudClientImpl;
import com.incadencecorp.coalesce.services.crud.service.CrudServiceImpl;

/**
 * This implementation uses the service directly w/o going through a WSDL.
 * 
 * @author Derek Clemenzi
 */
public class CrudFrameworkClientImpl extends AbstractCrudClientImpl {

    private CrudServiceImpl client;

    public CrudFrameworkClientImpl(ExecutorService pool, CoalesceFramework framework)
    {
        client = new CrudServiceImpl(pool, framework);
    }

    @Override
    protected StringResponse createDataObject(final DataObjectXmlRequest request)
    {
        return client.createDataObject(request);
    }

    @Override
    protected StringResponse updateDataObject(final DataObjectXmlRequest request)
    {
        return client.updateDataObject(request);
    }

    @Override
    protected StringResponse retrieveDataObject(final DataObjectKeyRequest request)
    {
        return client.retrieveDataObject(request);
    }

    @Override
    protected StringResponse updateDataObjectStatus(final DataObjectUpdateStatusRequest request)
    {
        return client.updateDataObjectStatus(request);
    }

    @Override
    protected StringResponse updateLinkages(final DataObjectLinkRequest request)
    {
        return client.updateDataObjectLinkages(request);
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
