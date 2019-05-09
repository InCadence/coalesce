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

package com.incadencecorp.coalesce.notification.direct.impl.test;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.notification.direct.impl.DirectSessions;
import com.incadencecorp.coalesce.notification.impl.NotificationSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Derek Clemenzi
 */
public class DirectSessionsTest {

    @Test
    public void testNotifier() throws Exception
    {
        Map<String, String> params = new HashMap<>();
        params.put(DirectSessions.PARAM_TOPIC, "requests");
        params.put(DirectSessions.PARAM_RESPONSE_TOPIC, "requests");

        DirectSessions<DirectRequest, DirectRequest> sessions = new DirectSessions<>(params, DirectRequest.class);

        String sessionKey = UUID.randomUUID().toString();

        sessions.sendMessage(sessionKey, new DirectRequest("Hello"));
        sessions.sendMessage(sessionKey, new DirectRequest("World"));
        NotificationSession<DirectRequest, DirectRequest> session;

        session = sessions.getSession(sessionKey);

        Assert.assertEquals(2, session.getResponses().size());
        Assert.assertEquals("Hello", session.getResponses().get(0).name);
        Assert.assertEquals("World", session.getResponses().get(1).name);
        Assert.assertEquals(EJobStatus.IN_PROGRESS, session.getStatus().getStatus());

        session.expire();

        Assert.assertEquals(EJobStatus.CANCELED, session.getStatus().getStatus());
        Assert.assertEquals(EJobStatus.CANCELED, sessions.getSession(sessionKey).getStatus().getStatus());

        sessions.expireAllSessions();

        session = sessions.getSession(sessionKey);

        Assert.assertEquals(0, session.getResponses().size());
        Assert.assertEquals(EJobStatus.NEW, session.getStatus().getStatus());
    }

    private class DirectRequest {

        private String name;

        private DirectRequest(String name) {
            this.name = name;
        }
    }

}
