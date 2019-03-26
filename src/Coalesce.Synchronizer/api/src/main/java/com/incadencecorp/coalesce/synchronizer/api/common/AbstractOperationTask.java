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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

import javax.sql.rowset.CachedRowSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Abstract task which is the base for all task created by the Synchronizer's
 * operations.
 *
 * @author n78554
 */
public abstract class AbstractOperationTask implements Callable<Boolean> {

    protected ICoalescePersistor source;
    protected ICoalescePersistor[] targets;
    protected Map<String, String> params = new HashMap<>();
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
     * @param params configuration parameters
     */
    public void setParameters(Map<String, String> params)
    {
        this.params.putAll(params);
    }

    /**
     * Sets the source used to obtain entities.
     *
     * @param source used to obtain entities.
     */
    public final void setSource(ICoalescePersistor source)
    {
        this.source = source;
    }

    /**
     * Sets the target that the results of this operation should be stored.
     *
     * @param targets of this operation
     */
    public final void setTarget(ICoalescePersistor[] targets)
    {
        this.targets = targets;
    }

    /**
     * Sets the row set which contains the result of a scan.
     *
     * @param rowset contains the result of a scan.
     */
    public final void setRowset(CachedRowSet rowset)
    {
        this.rowset = rowset;
    }

    /**
     * Sets the subset of keys this task is responsible for.
     *
     * @param subset keys this task is responsible for.
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

    /**
     * @return the subset of keys which failed to process. Defaults to the full subset and the extending operation needs to
     * override this to change that behaviour.
     */
    public String[] getErrorSubset()
    {
        return getSubset();
    }

    /**
     * Calls {@link ICoalescePersistor#saveEntity(boolean, CoalesceEntity...)} on each target.
     *
     * @see ICoalescePersistor#saveEntity(boolean, CoalesceEntity...)
     */
    protected boolean saveWork(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean result = true;

        for (ICoalescePersistor target : targets)
        {
            result = result && target.saveEntity(allowRemoval, entities);
        }

        return result;
    }

    protected abstract Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException;

}
