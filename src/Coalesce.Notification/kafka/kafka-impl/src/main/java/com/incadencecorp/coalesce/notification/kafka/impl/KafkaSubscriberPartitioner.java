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

package com.incadencecorp.coalesce.notification.kafka.impl;

import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.KeyValuePairEvent;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This partitioner determine available partitions from a subscriber. If the topic does not have an entry it will fall back
 * to using the {@link DefaultPartitioner}.
 *
 * @author Derek Clemenzi
 * @see #PARAM_SUBSCRIBER
 * @see #PARAM_TOPIC
 */
public class KafkaSubscriberPartitioner extends DefaultPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberPartitioner.class);
    private final ConcurrentMap<String, List<Integer>> topicAvailableMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> topicIdxMap = new ConcurrentHashMap<>();

    /**
     * Param that specifies what topic to subscribe to.
     */
    public static final String PARAM_TOPIC = "topic";
    /**
     * Param that specifies the subscriber to update the partition information.
     */
    public static final String PARAM_SUBSCRIBER = "subscriber.class";

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster)
    {
        try
        {
            if (topicAvailableMap.containsKey(topic))
            {
                return topicAvailableMap.get(topic).get(getNextValue(topic));
            }
            else
            {
                LOGGER.warn("Using DefaultPartitioner");
                return super.partition(topic, key, keyBytes, value, valueBytes, cluster);
            }
        }
        catch (Throwable e)
        {
            LOGGER.error("Failed Determining Partition", e);
            return 0;
        }
    }

    @Override
    public void close()
    {
        super.close();
    }

    @Override
    public void configure(Map<String, ?> configs)
    {
        super.configure(configs);

        if (!configs.containsKey(PARAM_TOPIC))
        {
            throw new IllegalArgumentException(PARAM_TOPIC + " not Specified");
        }

        if (!configs.containsKey(PARAM_SUBSCRIBER))
        {
            throw new IllegalArgumentException(PARAM_SUBSCRIBER + " not Specified");
        }

        try
        {
            String topic = (String) configs.get(PARAM_TOPIC);

            ICoalesceSubscriber subscriber = (ICoalesceSubscriber) Class.forName((String) configs.get(PARAM_SUBSCRIBER)).newInstance();
            subscriber.subscribeTopic(topic,
                                      (ICoalesceEventHandler<KeyValuePairEvent<List<Integer>>>) event -> setPartition(topic,
                                                                                                                      event.getValue()));
        }
        catch (Throwable e)
        {
            throw new IllegalArgumentException(PARAM_SUBSCRIBER + " is an invalid Subscriber", e);
        }
    }

    private void setPartition(String topic, List<Integer> values)
    {
        if (values.size() > 0)
        {
            topicAvailableMap.put(topic, values);
        }
        else
        {
            topicAvailableMap.remove(topic);
        }
    }

    private int getNextValue(String topic)
    {
        int result = topicIdxMap.getOrDefault(topic, -1);

        if (++result >= topicAvailableMap.get(topic).size())
        {
            result = 0;
        }

        topicIdxMap.put(topic, result);
        return result;
    }
}
