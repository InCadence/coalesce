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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.framework.CoalesceSchedulerServiceImpl;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 */
public class KafkaSubscriberImpl extends CoalesceSchedulerServiceImpl implements ICoalesceSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberImpl.class);

    private Map<String, Object> params = new HashMap<>();

    private static final long TIMEOUT = 100;
    private static final long INTERVAL = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private final ObjectMapper mapper = new ObjectMapper();

    public KafkaSubscriberImpl()
    {
        this(loadProperties());
    }

    public KafkaSubscriberImpl(Map<String, String> params)
    {
        this.params.putAll(params);

        for (Map.Entry<String, String> param : params.entrySet())
        {
            LOGGER.info("{} = {}", param.getKey(), param.getValue());
        }
    }

    @Override
    public void setContext(BundleContext context)
    {

    }

    @Override
    public void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_METRICS));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), MetricsEvent.class));
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_CRUD));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), CrudEvent.class));
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    private <V> V readValue(String json, Class<V> clazz)
    {
        try
        {
            return mapper.readValue(json, clazz);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_LINKAGE));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), LinkageEvent.class));
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_AUDIT));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), AuditEvent.class));
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_JOB_COMPLETE));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), JobEvent.class));
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler, Class<V> clazz)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(topic));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);

            for (ConsumerRecord<String, String> record : records)
            {
                KeyValuePairEvent<V> kvp = new KeyValuePairEvent<>();
                kvp.setKey(record.key());
                kvp.setValue(readValue(record.value(), clazz));

                handler.handle(kvp);
            }

        }, INTERVAL, INTERVAL, TIME_UNIT);
    }

    private static Map<String, String> loadProperties()
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector(Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION)),
                                                   KafkaSubscriberImpl.class.getName() + ".properties");

        return loader.getSettings();
    }
}
