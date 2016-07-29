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

package com.incadencecorp.coalesce.api;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;

/**
 * This interface is used for creating handlers of exceptions.
 * 
 * @author n78554
 */
public interface IExceptionHandler extends ICoalesceComponent {

    /**
     * Handles errors for operations.
     * 
     * @param keys
     * @param caller 
     * @param e
     * @return <code>true</code> if the error has been handled; otherwise
     *         <code>false</code> to indicate that the caller should handle the
     *         error.
     * @throws CoalesceException if additional information needs to be added to
     *             the exception.
     */
    boolean handle(String[] keys, ICoalesceComponent caller, Exception e) throws CoalesceException;

}
