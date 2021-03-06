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

package com.incadencecorp.coalesce.framework.jobs;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceJob;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import com.incadencecorp.coalesce.framework.util.CoalesceNotifierUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Abstract base for jobs in Coalesce.
 *
 * @param <INPUT>      input parameter type
 * @param <OUTPUT>     output type which contains a list of TASKOUTPUT.
 * @param <TASKOUTPUT> task result type.
 * @author Derek
 */
public abstract class AbstractCoalesceJob<INPUT, OUTPUT extends ICoalesceResponseType<List<TASKOUTPUT>>, TASKOUTPUT extends ICoalesceResponseType<?>>
        extends CoalesceComponentImpl implements ICoalesceJob, Callable<OUTPUT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCoalesceJob.class);

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private INPUT params;
    private StopWatch watch;
    private String id;
    private EJobStatus status;
    private Future<OUTPUT> future;
    private ICoalescePrincipal principal;

    private ICoalesceExecutorService service;
    private List<MetricResults<TASKOUTPUT>> taskMetrics = new ArrayList<>();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * @param params parameters to pass to the task.
     */
    public AbstractCoalesceJob(INPUT params)
    {
        this.params = params;
        this.id = UUID.randomUUID().toString();
        this.watch = new StopWatch();
        this.status = EJobStatus.NEW;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final OUTPUT call()
    {
        OUTPUT results;

        // Set Start Time
        watch.start();

        // Set Status to In Progress
        status = EJobStatus.IN_PROGRESS;

        try
        {
            results = this.doWork(principal, params);
        }
        catch (CoalesceException | InterruptedException e)
        {
            TASKOUTPUT result = createResults();
            result.setError(e.getMessage());

            if (e instanceof InterruptedException || e.getCause() instanceof InterruptedException)
            {
                LOGGER.info("(FAILED) Job Interrupted");
                result.setStatus(EResultStatus.INTERRUPTED);
                status = EJobStatus.INTERRUPTED;
            }
            else
            {
                LOGGER.error("(FAILED) Job", e);
                result.setStatus(EResultStatus.FAILED);
                status = EJobStatus.FAILED;
            }

            results = createResponse();
            results.setStatus(result.getStatus());
            results.setError(e.getMessage());
            results.getResult().add(result);
        }
        finally
        {
            watch.finish();
        }

        if (results != null && results.getStatus() != EResultStatus.SUCCESS)
        {
            // All other states are treated as failures because this is a
            // synchronous call.
            status = EJobStatus.FAILED;
        }
        else
        {
            status = EJobStatus.COMPLETE;
        }

        CoalesceNotifierUtil.sendJobComplete(this);

        return results;
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    @Override
    public final void setExecutor(ICoalesceExecutorService value)
    {
        service = value;
    }

    /**
     * @return the job's status.
     */
    public final EJobStatus getJobStatus()
    {
        return status;
    }

    /**
     * @return the job's ID
     */
    public final String getJobId()
    {
        return id;
    }

    /**
     * @return the parameters.
     */
    public final INPUT getParams()
    {
        return params;
    }

    /**
     * @return the metrics for running the job.
     */
    public final StopWatch getMetrics()
    {
        return watch;
    }

    /**
     * @return <code>true</code> if the job is complete.
     * @see java.util.concurrent.Future#isDone()
     */
    public final boolean isDone()
    {
        return future != null && future.isDone();
    }

    /**
     * @return <code>true</code> if the job is canceled.
     * @see java.util.concurrent.Future#isCancelled()
     */
    public final boolean isCanceled()
    {
        return future != null && future.isCancelled();
    }

    /**
     * @param future the future to set
     */
    public final void setFuture(Future<OUTPUT> future)
    {
        this.future = future;
    }

    /**
     * @return the {@link Future} for this job
     */
    public final Future<OUTPUT> getFuture()
    {
        return this.future;
    }

    /**
     * @return <code>True</code> if the job is to be ran non-blocking, otherwise
     * <code>False</code>.
     */
    public boolean isAsync()
    {
        return false;
    }

    /**
     * Sets the principal that created this job.
     */
    public void setPrincipal(ICoalescePrincipal principal)
    {
        this.principal = principal;
    }

    /**
     * @return the metrics of any task performed by this job.
     */
    public final MetricResults<TASKOUTPUT>[] getTaskMetrics()
    {
        return (MetricResults<TASKOUTPUT>[]) taskMetrics.toArray(new MetricResults<?>[taskMetrics.size()]);
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    /**
     * Sets the job's status.
     */
    protected final void setJobStatus(EJobStatus value)
    {
        status = value;
    }

    /**
     * Sets the job's ID
     */
    protected final void setJobId(String value)
    {
        id = value;
    }

    protected final ICoalesceExecutorService getService()
    {
        return service;
    }

    protected final void addResult(MetricResults<TASKOUTPUT> value)
    {
        taskMetrics.add(value);
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    /**
     * Performs the work of the job.
     */
    protected abstract OUTPUT doWork(ICoalescePrincipal principal, INPUT params)
            throws CoalesceException, InterruptedException;

    protected abstract OUTPUT createResponse();

    protected abstract TASKOUTPUT createResults();

}
