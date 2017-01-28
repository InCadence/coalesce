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

package com.incadencecorp.coalesce.services.search.client.common;

import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.client.common.AbstractBaseClient;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.api.ISearchEvents;

import net.opengis.fes._2.AbstractQueryExpressionType;

public abstract class AbstractSearchClientImpl extends AbstractBaseClient<ISearchEvents> implements ISearchClient {

    @Override
    public SearchDataObjectResponse search(AbstractQueryExpressionType query) {
        SearchDataObjectRequest request = new SearchDataObjectRequest(); 
        request.setQueryList(query);
        return searchDataObject(request);
    }
    
    abstract protected SearchDataObjectResponse searchDataObject(SearchDataObjectRequest request);

}
