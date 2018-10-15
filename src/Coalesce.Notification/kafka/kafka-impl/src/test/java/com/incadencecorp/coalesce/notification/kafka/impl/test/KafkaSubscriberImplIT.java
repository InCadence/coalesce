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

package com.incadencecorp.coalesce.notification.kafka.impl.test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaNotifierImpl;
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaSubscriberImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Derek Clemenzi
 */
public class KafkaSubscriberImplIT {

    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY,
                           Paths.get("src", "test", "resources").toString());
    }

    @Test
    public void testSendMessage() throws Exception
    {
        ICoalesceNotifier notifier = new KafkaNotifierImpl();
        ICoalesceSubscriber subscriber = new KafkaSubscriberImpl();

        subscriber.subscribeTopic("unitTest", event -> System.out.println(event.getValue()), String.class);
        subscriber.subscribeToCRUD(event -> System.out.println(event.getMeta().getKey()));

        while (true)
        {
            notifier.sendMessage("unitTest", "Test", "Test-Value");
            notifier.sendCrud("unitTest", ECrudOperations.UPDATE, new ObjectMetaData("A", "B", "C", "D"));
            Thread.sleep(30 * 1000);
        }

    }

    @Test
    public void testIntegerKVP() throws Exception
    {
        ICoalesceNotifier notifier = new KafkaNotifierImpl();
        KafkaSubscriberImpl subscriber = new KafkaSubscriberImpl();

        subscriber.setProperties(Collections.singletonMap(CoalesceParameters.PARAM_INTERVAL, "1"));
        subscriber.subscribeTopic("unitTest2", event -> {

            Assert.assertEquals("Test", event.getKey());
            Assert.assertEquals(1, event.getValue(), 0);

        }, Integer.class);

        while (true)
        {
            notifier.sendMessage("unitTest2", "Test", 1);
            Thread.sleep(10 * 1000);
        }

    }

        @Test
    public void testSendTabs() throws Exception
    {
        ICoalesceNotifier notifier = new KafkaNotifierImpl();
        KafkaSubscriberImpl subscriber = new KafkaSubscriberImpl();

        subscriber.setProperties(Collections.singletonMap(CoalesceParameters.PARAM_INTERVAL, "1"));
        subscriber.subscribeTopic("unitTest2", event -> {

            String[] values = event.getValue().split("\t");

            Assert.assertEquals("Test", event.getKey());
            Assert.assertEquals("Hello", values[0]);
            Assert.assertEquals("World", values[1]);

        }, String.class);

        while (true)
        {
            notifier.sendMessage("unitTest2", "Test", "Hello\tWorld");
            Thread.sleep(10 * 1000);
        }

    }

    @Test
    public void testSendArrays() throws Exception
    {
        ICoalesceNotifier notifier = new KafkaNotifierImpl();
        KafkaSubscriberImpl subscriber = new KafkaSubscriberImpl();

        subscriber.setProperties(Collections.singletonMap(CoalesceParameters.PARAM_INTERVAL, "1"));
        subscriber.subscribeTopic("unitTest3", event -> {

            Assert.assertEquals("Test", event.getKey());
            Assert.assertEquals("Hello", event.getValue().get(0));
            Assert.assertEquals("World", event.getValue().get(1));

        }, new ArrayList<String>().getClass());

        while (true)
        {
            notifier.sendMessage("unitTest3", "Test", new String[] { "Hello", "World" });
            Thread.sleep(10 * 1000);
        }
    }

}
