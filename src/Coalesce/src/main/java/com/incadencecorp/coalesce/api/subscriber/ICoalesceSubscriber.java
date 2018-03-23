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

package com.incadencecorp.coalesce.api.subscriber;

import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.osgi.framework.BundleContext;

/**
 * Interface for creating subscribers to the {@link com.incadencecorp.coalesce.api.ICoalesceNotifier} API.
 *
 * @author Derek Clemenzi
 */
public interface ICoalesceSubscriber {

    /**
     * Used to set the context in OSGi environments. May not be used by all implementations.
     *
     * @param context bundle's context
     */
    void setContext(BundleContext context);

    /**
     * Subscribe to metric events
     *
     * @param handler event's handler
     */
    void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler);

    /**
     * Subscribe to CRUD events
     *
     * @param handler event's handler
     */
    void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler);

    /**
     * Subscribe to linkage events
     *
     * @param handler event's handler
     */
    void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler);

    /**
     * Subscribe to audit events
     *
     * @param handler event's handler
     */
    void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler);

    /**
     * Subscribe to job events
     *
     * @param handler event's handler
     */
    void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler);

    /**
     * Subscribe to key / value pair events
     *
     * @param topic   to subscribe to
     * @param handler event's handler
     * @param <V>     the value object type
     */
    <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler);

}
