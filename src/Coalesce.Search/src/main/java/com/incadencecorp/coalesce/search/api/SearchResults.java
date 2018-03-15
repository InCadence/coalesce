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

package com.incadencecorp.coalesce.search.api;

import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;

import javax.sql.rowset.CachedRowSet;

public class SearchResults extends CoalesceResponseType<CachedRowSet> {

    private CachedRowSet results;
    private int page;
    private int pageSize;
    private long total;

    /**
     * WARNING: The parent class CoalesceResponseType has a very similar looking method called "getResult"
     * which is used in most parts of Coalesce, such as in the EntityExistsUtil. Make sure you really want
     * to use this method and not that one
     * 
     */
    public CachedRowSet getResults()
    {
        return results;
    }

    /**
     * WARNING: The parent class CoalesceResponseType has a very similar looking method called "setResult"
     * which is used in most parts of Coalesce, such as in the EntityExistsUtil. Make sure you really want
     * to use this method and not that one
     * 
     */
    public void setResults(CachedRowSet results)
    {
        this.results = results;
    }

    public int getPage()
    {
        return page;
    }

    public void setPage(int page)
    {
        this.page = page;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long pageCount)
    {
        this.total = pageCount;
    }

}
