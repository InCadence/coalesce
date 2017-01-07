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

package com.incadencecorp.coalesce.services.crud.client.common;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.client.common.AbstractBaseClient;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.api.ICrudEvents;

public abstract class AbstractCrudClientImpl extends AbstractBaseClient<ICrudEvents> implements ICrudClient {

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public boolean updateDataObjectStatus(DataObjectStatusType... tasks) throws RemoteException
    {
        DataObjectUpdateStatusRequest request = createUpdateDataObjectStatusRequest(false, tasks);
        return verifyResults(updateDataObjectStatus(request), request);
    }

    @Override
    public String updateDataObjectStatusAsync(DataObjectStatusType... tasks) throws RemoteException
    {
        DataObjectUpdateStatusRequest request = createUpdateDataObjectStatusRequest(true, tasks);
        return addAsyncResponse(updateDataObjectStatus(request), request);
    }

    @Override
    public boolean updateLinkages(DataObjectLinkType... tasks) throws RemoteException
    {
        DataObjectLinkRequest request = createDataObjectLinkRequest(false, tasks);
        return verifyResults(updateLinkages(request), request);
    }

    @Override
    public String updateLinkagesAsync(DataObjectLinkType... tasks) throws RemoteException
    {
        DataObjectLinkRequest request = createDataObjectLinkRequest(true, tasks);
        return addAsyncResponse(updateLinkages(request), request);
    }

    public Results<CoalesceEntity>[] retrieveDataObjects(String... keys) throws RemoteException
    {
        DataObjectKeyRequest request = createDataObjectKeyRequest(false, keys);
        return retrieveEntityCallback(retrieveDataObject(request));
    }

    public String retrieveDataObjectsAsync(String... keys) throws RemoteException
    {
        DataObjectKeyRequest request = createDataObjectKeyRequest(true, keys);
        return addAsyncResponse(retrieveDataObject(request), request);
    }

    @Override
    public Results<CoalesceEntity>[] retrieveDataObjects(DataObjectKeyType... tasks) throws RemoteException
    {
        DataObjectKeyRequest request = createDataObjectKeyRequest(false, tasks);
        return retrieveEntityCallback(retrieveDataObject(request));
    }

    @Override
    public String retrieveDataObjectsAsync(DataObjectKeyType... tasks) throws RemoteException
    {
        DataObjectKeyRequest request = createDataObjectKeyRequest(true, tasks);
        return addAsyncResponse(retrieveDataObject(request), request);
    }

    @Override
    public boolean createDataObject(CoalesceEntity... objects) throws RemoteException
    {
        DataObjectXmlRequest request = createDataObjectXmlRequest(false, objects);
        return verifyResults(createDataObject(request), request);
    }

    @Override
    public String createDataObjectAsync(CoalesceEntity... objects) throws RemoteException
    {
        DataObjectXmlRequest request = createDataObjectXmlRequest(true, objects);
        return addAsyncResponse(createDataObject(request), request);
    }

    @Override
    public boolean updateDataObject(CoalesceEntity... objects) throws RemoteException
    {
        DataObjectXmlRequest request = createDataObjectXmlRequest(false, objects);
        return verifyResults(updateDataObject(request), request);
    }

    @Override
    public String updateDataObjectAsync(CoalesceEntity... objects) throws RemoteException
    {
        DataObjectXmlRequest request = createDataObjectXmlRequest(true, objects);
        return addAsyncResponse(updateDataObject(request), request);
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    protected abstract StringResponse updateDataObjectStatus(DataObjectUpdateStatusRequest request);

    protected abstract StringResponse createDataObject(DataObjectXmlRequest request);

    protected abstract StringResponse updateDataObject(DataObjectXmlRequest request);

    protected abstract StringResponse retrieveDataObject(DataObjectKeyRequest request);

    protected abstract StringResponse updateLinkages(DataObjectLinkRequest request);

    /*--------------------------------------------------------------------------
    Callbacks
    --------------------------------------------------------------------------*/

    private Results<CoalesceEntity>[] retrieveEntityCallback(final StringResponse response)
    {

        Results<CoalesceEntity>[] results = new Results[response.getResult().size()];

        int ii = 0;

        for (ResultsType result : response.getResult())
        {

            if (result.getStatus() == EResultStatus.SUCCESS)
            {

                CoalesceEntity entity = new CoalesceEntity();
                if (entity.initialize(result.getResult()))
                {
                    results[ii] = new Results<CoalesceEntity>(entity);
                }
                else
                {
                    results[ii] = new Results<CoalesceEntity>("Failed to initialize");

                    // Raise Event
                    processFailedTask(response, ii, result.getResult());
                }

            }
            else
            {

                results[ii] = new Results<CoalesceEntity>(result.getResult());

                // Raise Event
                processFailedTask(response, ii, result.getResult());

            }

            ii++;

        }

        return results;

    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private DataObjectKeyRequest createDataObjectKeyRequest(final boolean async, final String... keys)
    {

        DataObjectKeyType[] list = new DataObjectKeyType[keys.length];

        for (int ii = 0; ii < keys.length; ii++)
        {

            list[ii] = new DataObjectKeyType();
            list[ii].setKey(keys[ii]);
            list[ii].setVer(-1);

        }

        return createDataObjectKeyRequest(async, list);
    }

    private DataObjectKeyRequest createDataObjectKeyRequest(final boolean async, final DataObjectKeyType... tasks)
    {
        DataObjectKeyRequest request = new DataObjectKeyRequest();
        request.getKeyList().addAll(Arrays.asList(tasks));
        request.setAsyncCall(async);

        return request;
    }

    private DataObjectUpdateStatusRequest createUpdateDataObjectStatusRequest(final boolean async,
                                                                              final DataObjectStatusType... tasks)
    {
        DataObjectUpdateStatusRequest request = new DataObjectUpdateStatusRequest();
        request.getTaskList().addAll(Arrays.asList(tasks));
        request.setAsyncCall(async);

        return request;
    }

    private DataObjectXmlRequest createDataObjectXmlRequest(final boolean async, final CoalesceEntity... entities)
    {
        DataObjectXmlRequest request = new DataObjectXmlRequest();

        List<String> xmlList = new ArrayList<String>();

        for (CoalesceEntity entity : entities)
        {
            xmlList.add(entity.toXml());
        }

        request.getDataObjectXmlList().addAll(xmlList);
        request.setAsyncCall(async);

        return request;
    }

    private DataObjectLinkRequest createDataObjectLinkRequest(final boolean async, final DataObjectLinkType... tasks)
    {
        DataObjectLinkRequest request = new DataObjectLinkRequest();

        request.getLinkagelist().addAll(Arrays.asList(tasks));
        request.setAsyncCall(async);

        return request;
    }

}
