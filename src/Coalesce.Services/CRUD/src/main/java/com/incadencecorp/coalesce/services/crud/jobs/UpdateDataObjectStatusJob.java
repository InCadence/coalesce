package com.incadencecorp.coalesce.services.crud.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.common.jobs.AbstractXSDJobBase;
import com.incadencecorp.coalesce.services.crud.tasks.UpdateDataObjectStatusTask;

public class UpdateDataObjectStatusJob extends AbstractXSDJobBase<DataObjectUpdateStatusRequest, StringResponse, ResultsType> {

    public UpdateDataObjectStatusJob(DataObjectUpdateStatusRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractFrameworkTask<?, ResultsType>> getTasks(DataObjectUpdateStatusRequest params)
    {
        List<AbstractFrameworkTask<?, ResultsType>> tasks = new ArrayList<AbstractFrameworkTask<?, ResultsType>>();

        for (DataObjectStatusType type : params.getTaskList())
        {
            UpdateDataObjectStatusTask task = new UpdateDataObjectStatusTask(); 
            task.setParams(new DataObjectStatusType[] {type});
            
            tasks.add(task);
        }

        return tasks;
    }

    @Override
    protected ICoalesceResponseType<List<ResultsType>> createResponse()
    {
        return new StringResponse();
    }

    @Override
    protected ResultsType createFailedResults(Exception e)
    {
        return new ResultsType();
    }
}
