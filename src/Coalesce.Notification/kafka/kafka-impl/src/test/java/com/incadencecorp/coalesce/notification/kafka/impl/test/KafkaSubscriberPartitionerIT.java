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
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaFileSubscriberImpl;
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaNotifierImpl;
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaSubscriberPartitioner;
import org.apache.kafka.clients.producer.Partitioner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class KafkaSubscriberPartitionerIT {

    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY,
                           Paths.get("src", "test", "resources").toString());
    }

    @Test
    public void testPartitioner() throws Exception
    {
        Map<String, Object> params2 = new HashMap<>();
        params2.put(KafkaSubscriberPartitioner.PARAM_TOPIC, "test");
        params2.put(KafkaSubscriberPartitioner.PARAM_SUBSCRIBER, KafkaFileSubscriberImpl.class.getName());

        Partitioner partitioner = new KafkaSubscriberPartitioner();
        partitioner.configure(params2);

        Assert.assertEquals(3, partitioner.partition("test", null, null, null, null, null));
        Assert.assertEquals(12, partitioner.partition("test", null, null, null, null, null));
        Assert.assertEquals(5, partitioner.partition("test", null, null, null, null, null));
        Assert.assertEquals(2, partitioner.partition("test", null, null, null, null, null));
        Assert.assertEquals(3, partitioner.partition("test", null, null, null, null, null));

        while (true)
        {
            Thread.sleep(1000);
            System.out.println(partitioner.partition("test", null, null, null, null, null));
        }
    }

    @Test
    public void testPartitionerWithNotifier() throws Exception
    {

        ICoalesceNotifier notifier = new KafkaNotifierImpl();
        notifier.sendMessage("GDELT_Ingest", "key", "value");

    }

}
