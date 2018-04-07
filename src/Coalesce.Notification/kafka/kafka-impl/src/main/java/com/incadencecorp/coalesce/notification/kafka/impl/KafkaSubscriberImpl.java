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
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 * @see CoalesceParameters@PARAM_TIMEOUT
 * @see CoalesceParameters@PARAM_INTERVAL
 * @see CoalesceParameters@PARAM_INTERVAL_UNIT
 */
public class KafkaSubscriberImpl extends CoalesceSchedulerServiceImpl implements ICoalesceSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberImpl.class);

    private Map<String, Object> params = new HashMap<>();

    private final long timeout;
    private final long interval;
    private final TimeUnit intervalUnit;
    private final ObjectMapper mapper = new ObjectMapper();

    public KafkaSubscriberImpl()
    {
        this(loadProperties());
    }

    public KafkaSubscriberImpl(Map<String, String> params)
    {
        this.params.putAll(params);

        timeout = Long.parseLong(params.getOrDefault(CoalesceParameters.PARAM_TIMEOUT, "100"));
        interval = Integer.parseInt(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL, "5"));
        intervalUnit = TimeUnit.valueOf(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL_UNIT,
                                                            TimeUnit.SECONDS.toString()));

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
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), MetricsEvent.class));
            }

        }, interval, interval, intervalUnit);
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_CRUD));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), CrudEvent.class));
            }

        }, interval, interval, intervalUnit);
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
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), LinkageEvent.class));
            }

        }, interval, interval, intervalUnit);
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_AUDIT));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), AuditEvent.class));
            }

        }, interval, interval, intervalUnit);
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(KafkaNotifierImpl.TOPIC_JOB_COMPLETE));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for (ConsumerRecord<String, String> record : records)
            {
                handler.handle(readValue(record.value(), JobEvent.class));
            }

        }, interval, interval, intervalUnit);
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler, Class<V> clazz)
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(params);
        consumer.subscribe(Collections.singleton(normalize(topic)));

        this.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            if (records.count() > 0)
            {
                for (TopicPartition partition : records.partitions())
                {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);

                    for (ConsumerRecord<String, String> record : partitionRecords)
                    {
                        try
                        {
                            LOGGER.debug("Received Topic {}", record.topic());

                            KeyValuePairEvent<V> kvp = new KeyValuePairEvent<>();
                            kvp.setKey(record.key());
                            kvp.setValue(readValue(record.value(), clazz));

                            if (LOGGER.isTraceEnabled())
                            {
                                LOGGER.trace("Handling Topic {} : {}", record.topic(), record.value());
                            }
                            else
                            {
                                LOGGER.debug("Handling Topic {}", record.topic());
                            }

                            handler.handle(kvp);
                        }
                        catch (Exception e)
                        {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }

                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                }
            }

        }, interval, interval, intervalUnit);
    }

    private String normalize(String value)
    {
        return value.replaceAll("[^\\w._-]", ".");
    }

    private static Map<String, String> loadProperties()
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector(Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION)),
                                                   KafkaSubscriberImpl.class.getName() + ".properties");

        return loader.getSettings();
    }
}
