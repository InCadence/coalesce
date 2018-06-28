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

import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.services.api.IBaseClient;
import com.incadencecorp.coalesce.services.api.ICoalesceEvents;
import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.JobRequest;
import com.incadencecorp.coalesce.services.api.common.MultipleResponse;
import com.incadencecorp.coalesce.services.api.common.ResponseResultsType;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StatusResponse;
import com.incadencecorp.coalesce.services.api.common.StatusType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;

public abstract class AbstractBaseClient<T extends ICoalesceEvents> extends BaseEventHandler<T> implements IBaseClient<T> {

    private ResultsType[] lastResults;
    private Principal principal;

    private Map<String, AsyncRequest<?>> asyncJobMap;

    /**
     * Default Constructor
     */
    public AbstractBaseClient()
    {
        asyncJobMap = new HashMap<>();
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public void setPrincipal(Principal principal)
    {
        this.principal = principal;
    }

    @Override
    public Principal getPrincipal()
    {
        return principal;
    }

    @Override
    final public ResultsType[] getLastResult()
    {
        return lastResults;
    }

    /*--------------------------------------------------------------------------
    Interface Methods
    --------------------------------------------------------------------------*/

    @Override
    public boolean cancelJob(String... jobId) throws RemoteException
    {

        // Create Request
        JobRequest request = new JobRequest();
        request.getJobIdList().addAll(Arrays.asList(jobId));
        request.setAsyncCall(false);

        // Verify Response
        return verifyResults(cancelJob(request), request, "");

    }

    @Override
    public Map<String, EJobStatus> getAllJobStatus() throws RemoteException
    {
        return getJobStatus(getPendingJob());
    }

    @Override
    public Map<String, EJobStatus> getJobStatus(String... jobId) throws RemoteException
    {

        // Create Request
        JobRequest request = new JobRequest();
        request.getJobIdList().addAll(Arrays.asList(jobId));
        request.setAsyncCall(false);

        return getStatusCallback(getStatus(request));

    }

    @Override
    public void pickupAllJobs() throws RemoteException
    {
        pickupJob(getPendingJob());
    }

    @Override
    public void pickupJob(String... jobId) throws RemoteException
    {

        // List<BaseResponse> results = new ArrayList<BaseResponse>();

        // Create Request
        JobRequest request = new JobRequest();
        request.getJobIdList().addAll(Arrays.asList(jobId));
        request.setAsyncCall(false);

        // Get Response
        MultipleResponse response = pickupJob(request);

        if (response != null && response.getResult() != null)
        {

            // Add Responses to List
            for (ResponseResultsType result : response.getResult())
            {

                if (result.getStatus() == EResultStatus.SUCCESS)
                {

                    if (result.getResult().getStatus() == EResultStatus.SUCCESS)
                    {
                        completeCallback(result.getResult());
                    }
                    else
                    {
                        statusCallback(result.getResult());
                    }

                }
                else
                {
                    failedCallback(result.getResult());
                }

            }
        }

    }

    @Override
    public String[] getPendingJob()
    {
        return asyncJobMap.keySet().toArray(new String[asyncJobMap.size()]);
    }

    @Override
    public BaseRequest getRequest(String jobId)
    {

        BaseRequest request = null;

        if (asyncJobMap.containsKey(jobId))
        {
            request = asyncJobMap.get(jobId).getRequest();
        }

        return request;

    }

    @Override
    public String getReason(String jobId)
    {

        String reason = "";

        if (asyncJobMap.containsKey(jobId))
        {
            reason = asyncJobMap.get(jobId).getReason();
        }

        return reason;

    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    /**
     * Cancels a submitted job.
     *
     * @param request containing the ID of the job being canceled.
     * @return the response containing the job's status.
     */
    protected abstract StringResponse cancelJob(JobRequest request) throws RemoteException;

    /**
     * @param request containing the ID of the job being inquired about.
     * @return the response containing the job's current status.
     */
    protected abstract StatusResponse getStatus(JobRequest request) throws RemoteException;

    /**
     * @param request containing the ID of the job being inquired about.
     * @return the response of the job indicated by the request if completed.
     *         Otherwise it returns the current status of job not found.
     */
    protected abstract MultipleResponse pickupJob(JobRequest request) throws RemoteException;

    protected abstract void processResponse(BaseResponse response);

    protected abstract void processFailedTask(BaseResponse response, int task, String reason);

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    /**
     * Calls procesFailedTask for any task in a failed state.
     *
     * @param response {@link StringResponse}
     */
    protected void verifyCallback(StringResponse response)
    {

        // Verify Results
        for (int ii = 0; ii < response.getResult().size(); ii++)
        {

            ResultsType result = response.getResult().get(ii);

            // Failed?
            if (result.getStatus() == EResultStatus.FAILED)
            {
                // Yes; Raise Event
                processFailedTask(response, ii, result.getResult());
            }

        }

    }

    /**
     * Calls {@link ICoalesceEvents#statusCallback(String, EResultStatus)} on each
     * listener.
     *
     * @param response {@link BaseResponse}
     */
    protected void statusCallback(BaseResponse response)
    {

        for (ICoalesceEvents listener : getListeners())
        {
            listener.statusCallback(response.getId(), response.getStatus());
        }

    }

    /**
     * Calls {@link ICoalesceEvents#failedCallback(String)} on each listener.
     *
     * @param response {@link BaseResponse}
     */
    protected void failedCallback(BaseResponse response)
    {

        for (ICoalesceEvents listener : getListeners())
        {
            listener.failedCallback(response.getId());
        }

    }

    protected String addAsyncResponse(BaseResponse response, BaseRequest request)
    {
        return addAsyncResponse(response, request, "");
    }

    protected String addAsyncResponse(BaseResponse response, BaseRequest request, String reason)
    {

        String jobId = null;

        if (response != null)
        {

            asyncJobMap.put(response.getId(), new AsyncRequest<BaseRequest>(reason, request));
            jobId = response.getId();

        }

        return jobId;
    }

    protected boolean verifyResults(StringResponse response, BaseRequest request) throws RemoteException
    {
        return verifyResults(response, request, "");
    }

    protected boolean verifyResults(StringResponse response, BaseRequest request, String reason) throws RemoteException
    {
        boolean isSuccessful = false;

        if (response != null)
        {
            if (request.isAsyncCall())
            {
                addAsyncResponse(response, request, reason);
                isSuccessful = true;
            }
            else
            {
                isSuccessful = verifyResults(response.getResult());
            }
        }

        return isSuccessful;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void completeCallback(BaseResponse response)
    {

        // There are no base jobs. Therefore all processing is done by child.
        processResponse(response);

        // Remove JobId
        asyncJobMap.remove(response.getId());

    }

    private boolean verifyResults(List<ResultsType> results) throws RemoteException
    {
        boolean isSuccessful = false;

        if (results != null)
        {
            lastResults = results.toArray(new ResultsType[results.size()]);

            isSuccessful = true;

            for (ResultsType result : results)
            {
                if (result == null || result.getStatus() != EResultStatus.SUCCESS)
                {
                    isSuccessful = false;

                    /*
                    if (result != null && !StringHelper.isNullOrEmpty(result.getError()))
                    {
                        throw new RemoteException(result.getError());
                    }
                    */
                    break;
                }
            }
        }

        return isSuccessful;
    }

    private Map<String, EJobStatus> getStatusCallback(StatusResponse response)
    {
        Map<String, EJobStatus> results = new HashMap<String, EJobStatus>();

        if (response != null && response.getResult() != null)
        {
            // Add Response Results to Map
            for (StatusType result : response.getResult())
            {
                results.put(result.getId(), result.getResult());
            }
        }

        return results;
    }

}
