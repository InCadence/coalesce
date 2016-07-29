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

import org.geotools.data.Query;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;

/**
 * This interface is used for creating different scanning logic to retrieve a
 * result set.
 * 
 * @author n78554
 */
public interface IPersistorScan extends ICoalesceComponent {

    /**
     * This method bust be called before starting the scanner. If running from a
     * blueprint add <code>init-method="setup"</code> to the bean.
     */
    void setup();

    /**
     * Sets the source that the scan will be executed against to create a result
     * set.
     * 
     * @param source
     */
    void setSource(ICoalesceSearchPersistor source);

    /**
     * Sets the columns required by operations that will use the results of this
     * scan. These columns should be included.
     * 
     * @param columns
     */
    void setReturnedColumns(Set<String> columns);

    /**
     * Scan using default filters.
     * 
     * @return the results of the scan with a minimal of a objectkey column.
     * @throws CoalesceException
     */
    CachedRowSet scan() throws CoalesceException;

    /**
     * Scan using a defined filter.
     * 
     * @param query
     * @param parameters
     * @return the results of the scan with a minimal of a objectkey column.
     * @throws CoalesceException
     */
    CachedRowSet scan(Query query, CoalesceParameter... parameters) throws CoalesceException;

    /**
     * Called when the driver running the scan completes to indicate whether is
     * successfully completed. This is used to persist data such as the last
     * successful run.
     * 
     * @param successful
     * @param rows
     */
    void finished(boolean successful, CachedRowSet rows);

}
