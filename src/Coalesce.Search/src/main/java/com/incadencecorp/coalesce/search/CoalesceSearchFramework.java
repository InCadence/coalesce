/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.search;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import org.geotools.data.Query;

import java.util.concurrent.ExecutorService;

/**
 * This extension exposes the {@link ICoalesceSearchPersistor} interface.
 *
 * @author Derek Clemenzi
 */
public class CoalesceSearchFramework extends CoalesceFramework {

    /**
     * Creates this framework with the default ThreadPoolExecutor based on
     * {@link com.incadencecorp.coalesce.framework.CoalesceSettings}.
     */
    public CoalesceSearchFramework()
    {
        super();
    }

    /**
     * Creates this framework with the provided executor service.
     *
     * @param service
     */
    public CoalesceSearchFramework(ExecutorService service)
    {
        super(service);
    }

    public SearchResults search(Query query) throws CoalescePersistorException
    {
        SearchResults results = null;
        ICoalesceSearchPersistor persistor = getSearchPersistor();

        if (persistor != null)
        {
            results = persistor.search(query);
        }

        return results;
    }

    /**
     * @return the authoritative persistor if it implements {@link ICoalesceSearchPersistor} or the first secondary persistor that does if available.
     */
    protected ICoalesceSearchPersistor getSearchPersistor()
    {
        ICoalesceSearchPersistor result = null;
        ICoalescePersistor persistor = getAuthoritativePersistor();

        if (persistor instanceof ICoalesceSearchPersistor)
        {
            result = (ICoalesceSearchPersistor) persistor;
        }
        else
        {
            for (ICoalescePersistor secondary : getSecondaryPersistors())
            {
                if (secondary instanceof ICoalesceSearchPersistor)
                {
                    result = (ICoalesceSearchPersistor) secondary;
                    break;
                }
            }
        }

        return result;
    }

}
