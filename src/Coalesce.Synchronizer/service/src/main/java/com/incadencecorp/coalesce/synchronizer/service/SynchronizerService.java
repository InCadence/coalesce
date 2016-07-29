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

package com.incadencecorp.coalesce.synchronizer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;

/**
 * This service is responsible for starting and stopping drivers.
 * 
 * @author n78554
 */
public class SynchronizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    private List<IPersistorDriver> drivers = new ArrayList<IPersistorDriver>();

    /**
     * Sets the drivers handled by this service.
     * 
     * @param drivers
     */
    public void setDrivers(IPersistorDriver... drivers)
    {
        this.drivers.addAll(Arrays.asList(drivers));
    }

    /**
     * Start the drivers.
     */
    public void start()
    {
        for (IPersistorDriver driver : drivers)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Starting {}", driver.getName());
            }

            driver.start();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Started {}", driver.getName());
            }
        }
    }

    /**
     * Stop the drivers.
     */
    public void stop()
    {
        for (IPersistorDriver driver : drivers)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Stopping {}", driver.getName());
            }

            driver.stop();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Stopped {}", driver.getName());
            }
        }
    }

}
