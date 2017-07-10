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

package com.incadencecorp.coalesce.search.api;

import org.geotools.data.Query;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;

/**
 * This interface is used to provide searching capability to persistors.
 * 
 * @author n78554
 */
public interface ICoalesceSearchPersistor {

    /**
     * Performs a search.
     * 
     * @param query
     * @return the results of the search
     * @throws CoalescePersistorException
     */
    SearchResults search(Query query) throws CoalescePersistorException;

}
