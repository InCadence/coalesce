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

package com.incadencecorp.coalesce.notification.impl;

import com.incadencecorp.coalesce.api.EJobStatus;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Derek Clemenzi
 */
public class NotificationSessionStatus {

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger responseCount = new AtomicInteger(0);

    private EJobStatus status = EJobStatus.NEW;

    public EJobStatus getStatus()
    {
        return status;
    }

    public void setStatus(EJobStatus status)
    {
        this.status = status;
    }

    public int getRequestCount()
    {
        return requestCount.get();
    }

    public int incrementRequests()
    {
        return requestCount.incrementAndGet();
    }

    public int getResponseCount()
    {
        return responseCount.get();
    }

    public int increateResponses()
    {
        return responseCount.incrementAndGet();
    }

}
