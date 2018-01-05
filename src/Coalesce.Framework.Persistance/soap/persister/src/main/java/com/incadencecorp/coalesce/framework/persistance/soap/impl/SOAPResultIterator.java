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

package com.incadencecorp.coalesce.framework.persistance.soap.impl;

import com.incadencecorp.coalesce.search.resultset.CoalesceColumnMetadata;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResultType;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;

/**
 * This iterator processing {@link QueryResultType} which is the return type used by {@link com.incadencecorp.coalesce.services.search.api.ISearchClient}.
 *
 * @author Derek Clemenzi
 */
public class SOAPResultIterator implements Iterator<Object[]> {

    private final Iterator<HitType> hits;
    private final List<CoalesceColumnMetadata> columns;

    /**
     * Default Constructor
     *
     * @param results obtained by {@link com.incadencecorp.coalesce.services.search.api.ISearchClient}
     * @param columns column metadata for the supplied results.
     */
    public SOAPResultIterator(QueryResultType results, List<CoalesceColumnMetadata> columns)
    {
        this.hits = results.getHits().iterator();
        this.columns = columns;
    }

    @Override
    public boolean hasNext()
    {
        return hits.hasNext();
    }

    @Override
    public Object[] next()
    {
        HitType hit = hits.next();

        int ii = 0;

        Object[] result = new Object[hit.getValues().size() + 1];
        result[ii++] = hit.getEntityKey();
        //result[ii++] = hit.getName();
        //result[ii++] = hit.getSource();
        //result[ii++] = hit.getTitle();

        if (hit.getValues() != null)
        {
            for (; ii <= hit.getValues().size(); ii++)
            {
                String value = hit.getValues().get(ii - 1);

                switch (columns.get(ii).getDataType())
                {
                case Types.BOOLEAN:
                    result[ii] = Boolean.parseBoolean(value);
                    break;
                default:
                    result[ii] = value;
                }
            }
        }

        return result;
    }
}
