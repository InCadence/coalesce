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

package com.incadencecorp.coalesce.synchronizer.api;

import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import com.incadencecorp.coalesce.api.ICoalesceJob;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

/**
 * This interface is used for creating operations that read entities from a
 * source, does some processing, and stores the results in the target(s).
 * 
 * @author n78554
 *
 */
public interface IPersistorOperation extends ICoalesceJob {

    /**
     * Sets the source used to obtain entities.
     * 
     * @param source
     */
    void setSource(ICoalescePersistor source);

    /**
     * @return a list of columns required for this operation. Scanners should
     *         include them in their row sets.
     */
    Set<String> getRequiredColumns();

    /**
     * Retrieves entities based on the passed in rows from the source, does some
     * processing, and stores the results in the targets.
     * 
     * @param service
     * @param rows
     * @return the results of the query.
     * @throws CoalesceException
     */
    CachedRowSet execute(ICoalesceExecutorService service, CachedRowSet rows) throws CoalesceException;

}
