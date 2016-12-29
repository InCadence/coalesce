package com.incadencecorp.coalesce.services.crud.tasks;

import java.util.Map;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.MetricsResultsType;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;

public class UpdateDataObjectStatusTask extends AbstractFrameworkTask<DataObjectStatusType[], ResultsType> {

    @Override
    protected MetricsResultsType doWork(CoalesceFramework framework, DataObjectStatusType[] params)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectStatusType[] params, boolean isTrace)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
