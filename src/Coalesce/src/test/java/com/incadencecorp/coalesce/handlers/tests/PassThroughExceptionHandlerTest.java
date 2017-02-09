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

package com.incadencecorp.coalesce.handlers.tests;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.handlers.PassThroughExceptionHandlerImpl;

/**
 * These test test the {@link PassThroughExceptionHandlerImpl} implementation.
 * 
 * @author n78554
 *
 */
public class PassThroughExceptionHandlerTest {

    /**
     * This implementation should always return false.
     * 
     * @throws Exception
     */
    @Test
    public void testPassThrough() throws Exception
    {

        PassThroughExceptionHandlerImpl handler = new PassThroughExceptionHandlerImpl();
        Assert.assertFalse(handler.handle(new String[] {
            UUID.randomUUID().toString()
        }, new CoalesceComponentImpl(), new CoalesceException("Hello World")));

    }

}