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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.common.jobs.AbstractServiceJob;
import com.incadencecorp.coalesce.services.crud.service.tasks.UpdateDataObjectTask;

public class UpdateDataObjectJob extends AbstractServiceJob<DataObjectXmlRequest, StringResponse, ResultsType> {

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
