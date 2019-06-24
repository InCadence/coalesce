/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;
import com.incadencecorp.coalesce.services.common.jobs.AbstractFrameworkServiceJob;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.tasks.RetrieveDataObjectTask;

import java.util.ArrayList;
import java.util.List;

public class RetrieveDataObjectJob extends
        AbstractFrameworkServiceJob<DataObjectKeyRequest, ICoalesceResponseType<List<ICoalesceResponseType<CoalesceEntity>>>, ICoalesceResponseType<CoalesceEntity>> {

    public RetrieveDataObjectJob(DataObjectKeyRequest request)
    {
        super(request);
    }

    @Override
    protected List<AbstractTask<?, ICoalesceResponseType<CoalesceEntity>, CoalesceFramework>> getTasks(DataObjectKeyRequest params)
    {
        List<AbstractTask<?, ICoalesceResponseType<CoalesceEntity>, CoalesceFramework>> tasks = new ArrayList<>();

        for (DataObjectKeyType type : params.getKeyList())
        {
            RetrieveDataObjectTask task = new RetrieveDataObjectTask();
            task.setParams(new DataObjectKeyType[] { type
            });

            tasks.add(task);
        }

        return tasks;
    }

    @Override
    protected ICoalesceResponseType<List<ICoalesceResponseType<CoalesceEntity>>> createResponse()
    {
        ICoalesceResponseType<List<ICoalesceResponseType<CoalesceEntity>>> response = new CoalesceResponseType<>();
        response.setResult(new ArrayList<>());
        return response;
    }

    @Override
    protected ICoalesceResponseType<CoalesceEntity> createResults()
    {
        return new CoalesceResponseType<>();
    }
}
