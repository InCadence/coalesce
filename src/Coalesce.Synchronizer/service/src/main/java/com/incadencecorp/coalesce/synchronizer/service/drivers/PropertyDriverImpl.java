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
package com.incadencecorp.coalesce.synchronizer.service.drivers;

import java.util.Map;

import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;

/**
 * This implementation extends {@link IntervalDriverImpl} and adds a criteria
 * requiring a property to be true for it to execute.
 * 
 * @author n78554
 * @see SynchronizerParameters#PARAM_DRIVER_DELAY
 * @see SynchronizerParameters#PARAM_DRIVER_INTERVAL
 * @see SynchronizerParameters#PARAM_DRIVER_INTERVAL_UNITS
 * @see SynchronizerParameters#PARAM_DRIVER_MAX_THREADS
 * @see SynchronizerParameters#PARAM_DRIVER_EXECUTE
 */
public class PropertyDriverImpl extends IntervalDriverImpl {

    @Override
    public void run()
    {
        if (Boolean.parseBoolean(loader.getProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE)))
        {
            super.run();

            loader.setProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE, Boolean.FALSE.toString());
        }
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        if (params.containsKey(SynchronizerParameters.PARAM_DRIVER_EXECUTE))
        {
            loader.setProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE,
                               params.get(SynchronizerParameters.PARAM_DRIVER_EXECUTE));
        }
    }

}
