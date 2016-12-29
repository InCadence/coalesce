package com.incadencecorp.coalesce.services.crud.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;
import com.incadencecorp.coalesce.services.common.jobs.AbstractXSDJobBase;
import com.incadencecorp.coalesce.services.crud.tasks.RetrieveDataObjectTask;

public class RetrieveDataObjectJob extends AbstractXSDJobBase<DataObjectKeyRequest, StringResponse, ResultsType> {

    public RetrieveDataObjectJob(DataObjectKeyRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractFrameworkTask<?, ResultsType>> getTasks(DataObjectKeyRequest params)
    {
        List<AbstractFrameworkTask<?, ResultsType>> tasks = new ArrayList<AbstractFrameworkTask<?, ResultsType>>();

        for (DataObjectKeyType type : params.getKeyList())
        {
            RetrieveDataObjectTask task = new RetrieveDataObjectTask(); 
            task.setParams(new DataObjectKeyType[] {type});
            
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
