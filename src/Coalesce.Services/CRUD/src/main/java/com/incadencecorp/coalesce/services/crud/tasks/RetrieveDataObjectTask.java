package com.incadencecorp.coalesce.services.crud.tasks;

import java.util.Map;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;

public class RetrieveDataObjectTask extends AbstractFrameworkTask<DataObjectKeyType[], ResultsType> {

    @Override
    protected ResultsType doWork(CoalesceFramework framework, DataObjectKeyType[] params)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectKeyType[] params, boolean isTrace)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
