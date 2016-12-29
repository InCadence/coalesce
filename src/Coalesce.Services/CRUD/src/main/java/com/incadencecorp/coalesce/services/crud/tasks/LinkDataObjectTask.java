package com.incadencecorp.coalesce.services.crud.tasks;

import java.util.Map;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;

public class LinkDataObjectTask extends AbstractFrameworkTask<DataObjectLinkType[], ResultsType> {

    @Override
    protected ResultsType doWork(CoalesceFramework framework, DataObjectLinkType[] params)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectLinkType[] params, boolean isTrace)
    {
        // TODO Auto-generated method stub
        return null;
    }


}
