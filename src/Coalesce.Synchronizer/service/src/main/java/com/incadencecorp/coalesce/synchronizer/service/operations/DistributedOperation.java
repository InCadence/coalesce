/*-----------------------------------------------------------------------------'
 Copyright 2019 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.synchronizer.service.operations;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;

/**
 * This implementation distributes the workload and needs to be paired with a {@link com.incadencecorp.coalesce.synchronizer.service.drivers.DistibutedDriver} using the same topic.
 *
 * @author derek
 */
public class DistributedOperation extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedOperation.class);

    private ICoalesceNotifier notifier;
    private String topic = DistributedOperation.class.getSimpleName();

    /**
     * Sets the notifier to use for distributing the workload.
     *
     * @param value notifier to use for distributing the workload.
     */
    public void setNotifier(ICoalesceNotifier value)
    {
        notifier = value;
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        topic = params.getOrDefault(SynchronizerParameters.PARAM_OP_TOPIC, DistributedOperation.class.getSimpleName());
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        if (notifier == null)
        {
            throw new IllegalStateException(String.format(CoalesceErrors.NOT_INITIALIZED, "Notifier"));
        }

        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset)
            {

                LOGGER.debug("Topic: {} Keys: {}", topic, keys);

                notifier.sendMessage(topic, null, keys);

                return true;
            }
        };
    }
}
