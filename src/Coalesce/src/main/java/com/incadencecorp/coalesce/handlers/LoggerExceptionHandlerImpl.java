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

package com.incadencecorp.coalesce.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;

/**
 * This implementation will log the failure as a warning along with the stack
 * trace if DEBUG is enabled. It report that the error was handled and allows
 * the operation to continue.
 * 
 * @author n78554
 */
public class LoggerExceptionHandlerImpl extends CoalesceComponentImpl implements IExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerExceptionHandlerImpl.class);

    @Override
    public boolean handle(String[] keys, ICoalesceComponent caller, Exception e) throws CoalesceException
    {
        LOGGER.warn("Failed to Process: {}", e.getMessage());
        for (String key : keys)
        {
            LOGGER.warn("\t{}", key);
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Exception", e);
        }

        return true;
    }

}
