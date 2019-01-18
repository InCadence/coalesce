/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.ingest.api;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;

/**
 * Interface used for processing key / value pairs provided by a {@link IProducer}.
 *
 * @author Derek Clemenzi
 */
public interface IProcessor extends ICoalesceComponent {

    /**
     * Sets the notifier implementation to be used when processing key / value pairs.
     *
     * @param notifier
     */
    void setNotifier(ICoalesceNotifier notifier);

    /**
     * Process the key / value pair
     *
     * @param key
     * @param value
     * @throws CoalesceException on error
     */
    void process(String key, String value) throws CoalesceException;

}
