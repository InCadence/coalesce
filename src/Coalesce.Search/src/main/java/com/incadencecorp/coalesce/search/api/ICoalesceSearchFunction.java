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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.opengis.filter.expression.VolatileFunction;

/**
 * Interface for creating Coalesce functions when generating SQL.
 * 
 * @author n78554
 */
public interface ICoalesceSearchFunction extends VolatileFunction {

    /**
     * Writes the WHERE clause condition to the provided {@link Writer}.
     *
     * @param contexts
     * @param vistor
     * @param out
     * @param databaseSchema
     * @throws IOException
     */
    public void write(List<?> contexts, ICoalesceExpressionVistor vistor, Writer out, String databaseSchema)
            throws IOException;
}
