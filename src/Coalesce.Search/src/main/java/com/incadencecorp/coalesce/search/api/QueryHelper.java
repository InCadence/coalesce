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

package com.incadencecorp.coalesce.search.api;

import org.geotools.data.Query;
import org.geotools.util.factory.Hints;

import java.util.HashMap;
import java.util.Map;

/**
 * This utility class provides methods for working with GeoTool queries that are common across all persister implementations.
 *
 * @author Derek Clemenzi
 */
public class QueryHelper {

    private static final String VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING = "highlighting";

    /**
     * @param query to check
     * @return whether or not highlighting is enabled for the specified query.
     */
    public static boolean isHighlightingEnabled(Query query)
    {
        Map<Object, Object> virtual = getHint(query, Hints.VIRTUAL_TABLE_PARAMETERS);
        return virtual != null && virtual.containsKey(VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING);
    }

    /**
     * @param query   to modify
     * @param enabled whether or not to enable highlighting
     */
    public static void setHighlightingEnabled(Query query, boolean enabled)
    {
        Map<Object, Object> virtual = getHint(query, Hints.VIRTUAL_TABLE_PARAMETERS);

        if (virtual == null)
        {
            virtual = new HashMap<>();
            query.getHints().put(Hints.VIRTUAL_TABLE_PARAMETERS, virtual);
        }

        if (!enabled && virtual.containsKey(VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING))
        {
            virtual.remove(VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING);
        }
        else if (enabled && !virtual.containsKey(VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING))
        {
            virtual.put(VIRTUAL_TABLE_PARAMETER_HIGHLIGHTING, null);
        }
    }

    private static <T> T getHint(Query query, Hints.Key classKey)
    {
        return (T) query.getHints().get(classKey);
    }
}
