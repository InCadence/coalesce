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

package com.incadencecorp.coalesce.services.search.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.api.search.SearchManager;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.search.service.jobs.SearchDataObjectJob;

public class SearchServiceImpl extends ServiceBase<ICoalesceSearchPersistor> implements SearchManager {

    public SearchServiceImpl(ICoalesceSearchPersistor persister)
    {
        super(persister, new ThreadPoolExecutor(CoalesceSettings.getMinThreads(),
                                                CoalesceSettings.getMaxThreads(),
                                                CoalesceSettings.getKeepAliveTime(),
                                                TimeUnit.SECONDS,
                                                new SynchronousQueue<Runnable>(),
                                                new CoalesceThreadFactoryImpl(),
                                                new ThreadPoolExecutor.CallerRunsPolicy()));
    }
    
    public SearchServiceImpl(ICoalesceSearchPersistor persister, ExecutorService service)
    {
        super(persister, service);
    }

    @Override
    public SearchDataObjectResponse searchDataObject(SearchDataObjectRequest request)
    {
        return performJob(new SearchDataObjectJob(request));
    }

}
