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

import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.api.search.SearchManager;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.search.service.jobs.SearchDataObjectJob;
import org.geotools.filter.Capabilities;

import java.util.concurrent.ExecutorService;

public class SearchServiceImpl extends ServiceBase<ICoalesceSearchPersistor> implements SearchManager {

    public SearchServiceImpl(ICoalesceSearchPersistor persister)
    {
        super(persister, null);
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

    public Capabilities getCapabilities()
    {
        return getTarget().getSearchCapabilities();
    }

}
