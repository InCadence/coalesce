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
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Derek Clemenzi
 */
public class NotificationSession<I, R> {

    /**
     * Synchronizes the result queue between session polls to avoid data lost when clearing.
     */
    private final Object SYNC_REQUEST = new Object();
    private final Object SYNC_RESPONSE = new Object();
    /**
     * Synchronizes the sessions status to avoid race conditions.
     */
    private final Object SYNC_STATUS = new Object();
    /**
     * Synchronizes the lastTouched to avoid race conditions when determining if the session has expired.
     */
    private final Object SYNC_TOUCH = new Object();

    private final String key = UUID.randomUUID().toString();
    private final List<I> requests = Collections.synchronizedList(new ArrayList<>());
    private final List<R> responses = Collections.synchronizedList(new ArrayList<>());

    private DateTime lastTouched = JodaDateTimeHelper.nowInUtc();
    private NotificationSessionStatus status = new NotificationSessionStatus();

    public String getKey()
    {
        return key;
    }

    public NotificationSessionStatus getStatus()
    {
        return status;
    }

    public List<I> getRequests()
    {
        synchronized (SYNC_REQUEST)
        {
            return Collections.unmodifiableList(requests);
        }
    }

    public void addRequest(I request)
    {
        status.incrementRequests();

        synchronized (SYNC_STATUS)
        {
            if (this.status.getStatus() == EJobStatus.NEW)
            {
                this.status.setStatus(EJobStatus.IN_PROGRESS);
            }
        }

        synchronized (SYNC_REQUEST)
        {
            requests.add(request);
        }
    }

    public List<R> getResponses()
    {
        return getResponses(false);
    }

    public List<R> getResponses(boolean clear)
    {
        synchronized (SYNC_RESPONSE)
        {
            List<R> results = new ArrayList<>();
            results.addAll(responses);

            if (clear)
            {
                responses.clear();
            }

            return results;
        }
    }

    public void addResponse(R response)
    {
        status.increateResponses();

        synchronized (SYNC_RESPONSE)
        {
            responses.add(response);
        }
    }

    public void touch()
    {
        synchronized (SYNC_TOUCH)
        {
            lastTouched = JodaDateTimeHelper.nowInUtc();
        }
    }

    /**
     * Expires the session
     */
    public void expire()
    {
        synchronized (SYNC_STATUS)
        {
            if (isRunning())
            {
                this.status.setStatus(EJobStatus.CANCELED);
            }
        }
    }

    public boolean isExpired(int minutes)
    {
        synchronized (SYNC_TOUCH)
        {
            return lastTouched.isBefore(JodaDateTimeHelper.nowInUtc().minusMinutes(minutes));
        }
    }

    private boolean isRunning()
    {
        EJobStatus currentStatus = status.getStatus();

        return currentStatus == EJobStatus.IN_PROGRESS || currentStatus == EJobStatus.PENDING
                || currentStatus == EJobStatus.NEW;
    }
}
