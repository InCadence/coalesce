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
import com.incadencecorp.coalesce.framework.ShutdownAutoCloseable;
import com.incadencecorp.unity.common.IConfigurationsConnector;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 * @see CoalesceParameters#PARAM_INTERVAL
 * @see CoalesceParameters#PARAM_INTERVAL_UNIT
 */
public class KafkaSubscriberImpl extends CoalesceComponentImpl implements ICoalesceSubscriber, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberImpl.class);

    private static final long DEFAULT_INTERVAL = 100;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private long interval = DEFAULT_INTERVAL;
    private TimeUnit intervalUnit = DEFAULT_TIME_UNIT;
    private IConfigurationsConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);

    private final ObjectMapper mapper = new ObjectMapper();
    private final CoalesceSchedulerServiceImpl service;
    private final List<KafkaConsumer> consumers = Collections.synchronizedList(new ArrayList<>());

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

        setProperties(connector.getSettings(KafkaSubscriberImpl.class.getName() + ".properties"));

        ShutdownAutoCloseable.createShutdownHook(this);
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        interval = Long.parseLong(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL, Long.toString(DEFAULT_INTERVAL)));
        intervalUnit = TimeUnit.valueOf(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL_UNIT,
                                                            DEFAULT_TIME_UNIT.toString()));

        if (params.containsKey(KafkaNotifierImpl.PROP_CONFIG_DIR)
                && !params.get(KafkaNotifierImpl.PROP_CONFIG_DIR).equalsIgnoreCase(CoalesceParameters.COALESCE_CONFIG_LOCATION))
        {
            connector = new FilePropertyConnector(params.get(KafkaNotifierImpl.PROP_CONFIG_DIR));
        }

        Map<String, String> settings = connector.getSettings(KafkaNotifierImpl.class.getName() + ".properties");
        settings.putAll(params);

        super.setProperties(settings);

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
            kvp.setKey(event.key() != null ? event.key().toString() : null);
            kvp.setValue(event.value() != null ? readValue(event.value().toString(), clazz) : null);

            handler.handle(kvp);

        }, ConsumerRecord.class);
    }

    @Override
    public void close() throws Exception
    {
        for (KafkaConsumer consumer : consumers)
        {
            try
            {
                if (LOGGER.isDebugEnabled())
                {
                    for (Object topic : consumer.subscription())
                    {
                        LOGGER.debug("Closing ({})", topic);
                    }
                }

                consumer.unsubscribe();
                consumer.close();
            }
            catch (Throwable e)
            {
                LOGGER.warn("(FAILED) Stopping Consumer", e);
            }
        }

        consumers.clear();
    }

    private <V> void subscribe(String topic, ICoalesceEventHandler<V> handler, Class<V> clazz)
    {
        LOGGER.debug("Subscribing to Topic: ({}) Type: {}", topic, clazz.getSimpleName());

        createTopic(topic);

        KafkaConsumer<String, String> consumer;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(null);//KafkaConsumer.class.getClassLoader());

            consumer = new KafkaConsumer<>(Collections.unmodifiableMap(parameters));
            consumer.subscribe(Collections.singleton(KafkaUtil.normalizeTopic(topic)));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }

        consumers.add(consumer);

        LOGGER.info("Polling Topic ({}) Every {} {}", topic, interval, intervalUnit);

        service.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, String> records = consumer.poll(0);

            LOGGER.trace("Polling: {}", topic);

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

                            if (clazz == ConsumerRecord.class)
                            {
                                handler.handle((V) record);
                            }
                            else
                            {
                                handler.handle(readValue(record.value(), clazz));
                            }

                            LOGGER.trace("Handled Topic ({})", record.topic());

                        }
                        catch (Throwable e)
                        {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }

                    try
                    {
                        LOGGER.trace("Committing Offset");

                        long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));

                        LOGGER.trace("Committed Offset");
                    }
                    catch (Throwable e)
                    {
                        LOGGER.error("(FAILED) Committing Offset", e);
                    }

                }
            }

        }, interval, interval, intervalUnit);

    }

    private void createTopic(String topic)
    {
        KafkaUtil.createTopic(topic, parameters, connector);
    }

    private <V> V readValue(String json, Class<V> clazz)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            if (clazz == String.class)
            {
                return (V) json;
            }
            else
            {
                return mapper.readValue(json, clazz);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
