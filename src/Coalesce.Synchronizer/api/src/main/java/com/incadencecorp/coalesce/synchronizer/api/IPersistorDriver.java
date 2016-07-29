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

package com.incadencecorp.coalesce.synchronizer.api;

import com.incadencecorp.coalesce.api.ICoalesceComponent;

/**
 * This interface is for creating driver implementations to perform some
 * operation(s) based the result set of a scan.
 * 
 * @author n78554
 *
 */
public interface IPersistorDriver extends ICoalesceComponent {

    /**
     * This method bust be called before starting the driver. If running from a
     * blueprint add <code>init-method="setup"</code> to the bean.
     */
    void setup();

    /**
     * Starts the driver.
     */
    void start();

    /**
     * Stops the driver. Cleanup any threads or other memory that was used.
     */
    void stop();

    /**
     * Specifies what scanner to use for obtaining a result set.
     * 
     * @param scan
     */
    void setScan(IPersistorScan scan);

    /**
     * Specified a list of operations that should be performed on the result set
     * that was provided by the scanner.
     * 
     * @param operations
     */
    void setOperations(IPersistorOperation... operations);

}
