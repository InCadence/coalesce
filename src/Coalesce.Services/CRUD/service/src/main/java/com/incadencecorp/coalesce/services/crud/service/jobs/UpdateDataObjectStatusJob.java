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

package com.incadencecorp.coalesce.services.crud.service.jobs;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.common.jobs.AbstractFrameworkServiceJob;
import com.incadencecorp.coalesce.services.crud.service.tasks.UpdateDataObjectStatusTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateDataObjectStatusJob
        extends AbstractFrameworkServiceJob<DataObjectUpdateStatusRequest, StringResponse, ResultsType> {

    private static final int DEFAULT_BLOCK_SIZE = 100;
    private int blockSize = DEFAULT_BLOCK_SIZE;

    public UpdateDataObjectStatusJob(DataObjectUpdateStatusRequest request)
    {
        super(request);
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        if (params.containsKey(CoalesceParameters.PARAM_BLOCK_SIZE))
        {
            blockSize = Integer.parseInt(params.get(CoalesceParameters.PARAM_BLOCK_SIZE));
        }
    }

    @Override
    protected Collection<AbstractTask<?, ResultsType, CoalesceFramework>> getTasks(DataObjectUpdateStatusRequest params)
    {
        List<AbstractTask<?, ResultsType, CoalesceFramework>> tasks = new ArrayList<>();

        DataObjectStatusType[][] chunks = ArrayHelper.createChunks(params.getTaskList().toArray(new DataObjectStatusType[params.getTaskList().size()]),
                                                                   blockSize,
                                                                   DataObjectStatusType[][]::new);

        for (DataObjectStatusType[] chunk : chunks)
        {
            UpdateDataObjectStatusTask task = new UpdateDataObjectStatusTask();
            task.setParams(chunk);

            tasks.add(task);
        }

        return tasks;
    }

    @Override
    protected StringResponse createResponse()
    {
        return new StringResponse();
    }

    @Override
    protected ResultsType createResults()
    {
        return new ResultsType();
    }
}
