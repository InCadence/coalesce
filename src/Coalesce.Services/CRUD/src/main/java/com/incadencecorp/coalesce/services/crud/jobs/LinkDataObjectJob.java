package com.incadencecorp.coalesce.services.crud.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.common.jobs.AbstractXSDJobBase;
import com.incadencecorp.coalesce.services.crud.tasks.LinkDataObjectTask;

public class LinkDataObjectJob extends AbstractXSDJobBase<DataObjectLinkRequest, StringResponse, ResultsType> {

    public LinkDataObjectJob(DataObjectLinkRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractFrameworkTask<?, ResultsType>> getTasks(DataObjectLinkRequest params)
    {
        List<AbstractFrameworkTask<?, ResultsType>> tasks = new ArrayList<AbstractFrameworkTask<?, ResultsType>>();

        for (DataObjectLinkType type : params.getLinkagelist())
        {
            LinkDataObjectTask task = new LinkDataObjectTask(); 
            task.setParams(new DataObjectLinkType[] {type});
            
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
