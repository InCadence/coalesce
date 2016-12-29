package com.incadencecorp.coalesce.services.crud.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.common.jobs.AbstractXSDJobBase;
import com.incadencecorp.coalesce.services.crud.tasks.UpdateDataObjectTask;

public class UpdateDataObjectJob extends AbstractXSDJobBase<DataObjectXmlRequest, StringResponse, ResultsType> {

    public UpdateDataObjectJob(DataObjectXmlRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractFrameworkTask<?, ResultsType>> getTasks(DataObjectXmlRequest params)
    {
        List<AbstractFrameworkTask<?, ResultsType>> tasks = new ArrayList<AbstractFrameworkTask<?, ResultsType>>();

        for (String xml : params.getDataObjectXmlList())
        {
            UpdateDataObjectTask task = new UpdateDataObjectTask(); 
            task.setParams(new String[] {xml});
            
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
