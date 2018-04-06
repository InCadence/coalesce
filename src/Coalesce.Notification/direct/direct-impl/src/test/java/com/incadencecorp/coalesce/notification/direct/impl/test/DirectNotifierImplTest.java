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

import com.incadencecorp.coalesce.notification.direct.impl.DirectNotifierImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Derek Clemenzi
 */
public class DirectNotifierImplTest {

    @Test
    public void testNotifier() throws Exception
    {
        String key = "myKey";
        String value = "myValue";
        String topic = "myTopic";

        DirectNotifierImpl notifier = new DirectNotifierImpl();
        notifier.subscribeTopic(topic, event -> {
            Assert.assertEquals(key, event.getKey());
            Assert.assertEquals(value, event.getValue());
        }, String.class);

        notifier.sendMessage(topic, key, value);
    }

}
