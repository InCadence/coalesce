package com.incadencecorp.coalesce.framework.tasks;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseTypeBase;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;

public class MetricResults<T extends ICoalesceResponseTypeBase> {

    private T results;
    private StopWatch watch;

    public StopWatch getWatch()
    {
        return watch;
    }

    public void setWatch(StopWatch watch)
    {
        this.watch = watch;
    }

    
    public T getResults()
    {
        return results;
    }

    
    public void setResults(T results)
    {
        this.results = results;
    }
    
    public boolean isSuccessful() {
        return results.getStatus() == EResultStatus.SUCCESS;
    }

}
