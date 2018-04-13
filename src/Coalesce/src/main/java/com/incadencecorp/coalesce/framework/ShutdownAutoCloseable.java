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

package com.incadencecorp.coalesce.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Clemenzi
 */
public class ShutdownAutoCloseable<T extends AutoCloseable> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownAutoCloseable.class);
    private static final CoalesceThreadFactoryImpl THREAD_FACTORY = new CoalesceThreadFactoryImpl();

    private AutoCloseable service;

    public ShutdownAutoCloseable(T service)
    {
        this.service = service;
        LOGGER.info("Shutdown Hook Created for {}", service.getClass().getSimpleName());
    }

    public static void createShutdownHook(AutoCloseable object)
    {
        Runtime.getRuntime().addShutdownHook(THREAD_FACTORY.newThread(new ShutdownAutoCloseable<>(object)));
    }

    @Override
    public void run()
    {
        try
        {
            LOGGER.info("Shutdown Hook Invoked for {}", service.getClass().getSimpleName());
            service.close();
        }
        catch (Exception e)
        {
            LOGGER.error("(FAILED) Closing", e);
        }
    }
}
