/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.synchronizer.api.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.rowset.CachedRowSet;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

/**
 * Abstract task which is the base for all task created by the Synchronizer's
 * operations.
 * 
 * @author n78554
 *
 */
public abstract class AbstractOperationTask implements Callable<Boolean> {

    protected ICoalescePersistor source;
    protected ICoalescePersistor target;
    protected Map<String, String> params = new HashMap<String, String>();
    protected String keys[];
    protected CachedRowSet rowset;

    @Override
    public final Boolean call() throws Exception
    {
        return doWork(keys, rowset);
    }

    /**
     * Sets the parameters.
     * 
     * @param params
     */
    public void setParameters(Map<String, String> params)
    {
        this.params.putAll(params);
    }

    /**
     * Sets the source used to obtain entities.
     * 
     * @param source
     */
    public final void setSource(ICoalescePersistor source)
    {
        this.source = source;
    }

    /**
     * Sets the target that the results of this operation should be stored.
     * 
     * @param target
     */
    public final void setTarget(ICoalescePersistor target)
    {
        this.target = target;
    }

    /**
     * Sets the row set which contains the result of a scan.
     * 
     * @param rowset
     */
    public final void setRowset(CachedRowSet rowset)
    {
        this.rowset = rowset;
    }

    /**
     * Sets the subset of keys this task is responsible for.
     * 
     * @param subset
     */
    public final void setSubset(String[] subset)
    {
        this.keys = subset;
    }

    /**
     * @return the subset of keys this task is responsible for.
     */
    public final String[] getSubset()
    {
        return this.keys;
    }

    protected abstract Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException;

}
