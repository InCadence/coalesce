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
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.CoalesceSchedulerServiceImpl;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.coalesce.framework.ShutdownAutoCloseable;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import com.incadencecorp.unity.common.connectors.MemoryConnector;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 * @see CoalesceParameters@PARAM_TIMEOUT
 * @see CoalesceParameters@PARAM_INTERVAL
 * @see CoalesceParameters@PARAM_INTERVAL_UNIT
 */
public class KafkaSubscriberImpl extends CoalesceComponentImpl implements ICoalesceSubscriber, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberImpl.class);

    private static final long DEFAULT_TIMEOUT = 100;
    private static final long DEFAULT_INTERVAL = 100;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private long timeout = DEFAULT_TIMEOUT;
    private long interval = DEFAULT_INTERVAL;
    private TimeUnit intervalUnit = DEFAULT_TIME_UNIT;
    private IConfigurationsConnector connector = new MemoryConnector();

    private final ObjectMapper mapper = new ObjectMapper();
    private final CoalesceSchedulerServiceImpl service;

    private static final List<KafkaConsumer> CONSUMERS = Collections.synchronizedList(new ArrayList<>());

    /**
     * Default Constructor
     */
    public KafkaSubscriberImpl()
    {
        this(null);
    }

    /**
     * @param service override the default service.
     */
    public KafkaSubscriberImpl(ScheduledExecutorService service)
    {
        this.service = new CoalesceSchedulerServiceImpl(service);

        setProperties(loadProperties());

        ShutdownAutoCloseable.createShutdownHook(this);
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        timeout = Long.parseLong(params.getOrDefault(CoalesceParameters.PARAM_TIMEOUT, Long.toString(DEFAULT_TIMEOUT)));
        interval = Long.parseLong(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL, Long.toString(DEFAULT_INTERVAL)));
        intervalUnit = TimeUnit.valueOf(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL_UNIT,
                                                            DEFAULT_TIME_UNIT.toString()));
        connector = new FilePropertyConnector(params.getOrDefault(KafkaNotifierImpl.PROP_CONFIG_DIR,
                                                                  KafkaNotifierImpl.DEFAULT_CONFIG_DIR));

        // Create Topics Specified
        for (String topic : (params.getOrDefault(KafkaNotifierImpl.PROP_TOPICS, "")).split(","))
        {
            if (!StringHelper.isNullOrEmpty(topic))
            {
                createTopic(topic);
            }
        }
    }

    @Override
    public void setContext(BundleContext context)
    {
        // Do Nothing
    }

    @Override
    public void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler)
    {
        subscribe(KafkaNotifierImpl.TOPIC_METRICS, handler, MetricsEvent.class);
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        subscribe(KafkaNotifierImpl.TOPIC_CRUD, handler, CrudEvent.class);
    }

    @Override
    public void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler)
    {
        subscribe(KafkaNotifierImpl.TOPIC_LINKAGE, handler, LinkageEvent.class);
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        subscribe(KafkaNotifierImpl.TOPIC_AUDIT, handler, AuditEvent.class);
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        subscribe(KafkaNotifierImpl.TOPIC_JOB_COMPLETE, handler, JobEvent.class);
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler, Class<V> clazz)
    {
        subscribe(topic, event -> {

            KeyValuePairEvent<V> kvp = new KeyValuePairEvent<>();
            kvp.setValue(event);
            handler.handle(kvp);

        }, clazz);
    }

    @Override
    public void close() throws Exception
    {
        for (KafkaConsumer consumer : CONSUMERS)
        {
            consumer.close();
        }

        CONSUMERS.clear();
    }

    private <V> void subscribe(String topic, ICoalesceEventHandler<V> handler, Class<V> clazz)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Subscribing to Topic: ({}) Type: {}", topic, clazz.getSimpleName());
        }

        createTopic(topic);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(null);//KafkaConsumer.class.getClassLoader());

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Collections.unmodifiableMap(parameters));
            consumer.subscribe(Collections.singleton(KafkaUtil.normalizeTopic(topic)));

            CONSUMERS.add(consumer);

            LOGGER.debug("Polling Topic ({}) Every {} {}", topic, interval, intervalUnit);

            service.scheduleAtFixedRate(() -> {
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
                                if (LOGGER.isTraceEnabled())
                                {
                                    LOGGER.trace("Handling Topic: ({}) Type: {} Data: {}",
                                                 record.topic(),
                                                 clazz.getSimpleName(),
                                                 record.value());
                                }
                                else
                                {
                                    LOGGER.debug("Handling Topic: ({}) Type: {}", record.topic(), clazz.getSimpleName());
                                }

                                handler.handle(readValue(record.value(), clazz));

                                LOGGER.trace("Handled Topic ({})", record.topic());

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
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    private void createTopic(String topic)
    {
        KafkaUtil.createTopic(topic, parameters, connector);
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

    private static Map<String, String> loadProperties()
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector(Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION)),
                                                   KafkaSubscriberImpl.class.getName() + ".properties");

        return loader.getSettings();
    }
}
