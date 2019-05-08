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

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.ShutdownAutoCloseable;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * This implementation uses Kafka to push notifications.
 *
 * @author Derek Clemenzi
 * @see #PROP_CONFIG_DIR
 * @see KafkaUtil#PROP_ZOOKEEPER
 * @see KafkaUtil#PROP_SESSION_TIMEOUT
 * @see KafkaUtil#PROP_CONN_TIMEOUT
 * @see #PROP_TOPICS
 * @see KafkaUtil#PROP_PARTITIONS
 * @see KafkaUtil#PROP_REPLICATION
 * @see KafkaUtil#PROP_IS_SECURE
 */
public class KafkaNotifierImpl extends CoalesceComponentImpl implements ICoalesceNotifier, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaNotifierImpl.class);

    // Topic Definitions
    public static final String TOPIC_METRICS = "com.incadence.metrics";
    public static final String TOPIC_CRUD = "com.incadence.crud";
    public static final String TOPIC_LINKAGE = "com.incadence.linkage";
    public static final String TOPIC_AUDIT = "com.incadence.audit";
    public static final String TOPIC_JOB_COMPLETE = "com.incadence.job";

    public static final String PROP_TOPICS = "kafka.topics";

    // Property Definitions
    /**
     * (Path) Directory location containing the topic configurations. Default {@link CoalesceParameters#COALESCE_CONFIG_LOCATION}
     */
    public static final String PROP_CONFIG_DIR = "kafka.config.dir";

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private KafkaProducer<String, Object> producer;
    private IConfigurationsConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public KafkaNotifierImpl()
    {
        setProperties(connector.getSettings(KafkaNotifierImpl.class.getName() + ".properties"));

        ShutdownAutoCloseable.createShutdownHook(this);
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        if (params.containsKey(PROP_CONFIG_DIR)
                && !params.get(PROP_CONFIG_DIR).equalsIgnoreCase(CoalesceParameters.COALESCE_CONFIG_LOCATION))
        {
            connector = new FilePropertyConnector(params.get(PROP_CONFIG_DIR));
        }

        Map<String, String> settings = connector.getSettings(KafkaNotifierImpl.class.getName() + ".properties");
        settings.putAll(params);

        super.setProperties(settings);

        // Create Topics Specified
        for (String topic : (params.getOrDefault(PROP_TOPICS, "")).split(","))
        {
            if (!StringHelper.isNullOrEmpty(topic))
            {
                createTopic(topic);
            }
        }
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void setContext(BundleContext context)
    {
        // Do Nothing
    }

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        MetricsEvent event = new MetricsEvent();
        event.setName(task);
        event.setPending(results.getWatch().getPendingLife());
        event.setWorking(results.getWatch().getWorkLife());
        event.setTotal(results.getWatch().getTotalLife());
        event.setSuccessful(results.isSuccessful());

        if (!results.isSuccessful() && !StringHelper.isNullOrEmpty(results.getResults().getError()))
        {
            event.setError(results.getResults().getError());
        }

        sendRecord(TOPIC_METRICS, event);
    }

    @Override
    public void sendMetrics(String task, Long duration)
    {
        MetricsEvent event = new MetricsEvent();
        event.setName(task);
        event.setPending(0);
        event.setWorking(duration);
        event.setTotal(duration);
        event.setSuccessful(true);

        sendRecord(TOPIC_METRICS, event);
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        CrudEvent event = new CrudEvent();
        event.setName(task);
        event.setOperation(operation);
        event.setMeta(new ObjectMetaData(data.getKey(), data.getName(), data.getSource(), data.getVersion()));

        sendRecord(TOPIC_CRUD, event);
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        LinkageEvent event = new LinkageEvent();
        event.setName(task);
        event.setRelationship(relationship);
        event.setOperation(operation);

        event.setEntity1(new ObjectMetaData(entity1.getKey(), entity1.getName(), entity1.getSource(), entity1.getVersion()));
        event.setEntity2(new ObjectMetaData(entity2.getKey(), entity2.getName(), entity2.getSource(), entity2.getVersion()));

        sendRecord(TOPIC_LINKAGE, event);
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        AuditEvent event = new AuditEvent();
        event.setName(task);
        event.setCategory(category);
        event.setLevel(level);
        event.setMessage(message);

        sendRecord(TOPIC_AUDIT, event);
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        JobEvent event = new JobEvent();
        event.setName(job.getName());
        event.setId(job.getJobId());
        event.setStatus(job.getJobStatus());

        sendRecord(TOPIC_JOB_COMPLETE, event);
    }

    /**
     * WARNING: Key is ignored.
     */
    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        sendRecord(topic, key, value);
    }

    @Override
    public synchronized void close() throws Exception
    {
        if (producer != null)
        {
            producer.close();
        }
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void createTopic(String topic)
    {
        KafkaUtil.createTopic(topic, parameters, connector);
    }

    private String toJSONString(Object value)
    {
        if (value instanceof String)
        {
            return (String) value;
        }
        else
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);

            try
            {
                return mapper.writeValueAsString(value);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendRecord(String topic, Object value)
    {
        sendRecord(topic, null, value);
    }

    private void sendRecord(String topic, String key, Object value)
    {
        LOGGER.debug("Sending Topic: ({}) Key: {}, Type: {}", topic, key, value.getClass().getSimpleName());

        sendRecord(new ProducerRecord<>(KafkaUtil.normalizeTopic(topic), key, toJSONString(value)));
    }

    private void sendRecord(ProducerRecord<String, Object> record)
    {
        createTopic(record.topic());

        if (producer == null)
        {
            LOGGER.debug("Creating Producer");

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            try
            {
                Thread.currentThread().setContextClassLoader(null);

                producer = new KafkaProducer<>(Collections.unmodifiableMap(parameters));
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(cl);
            }

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Data: {}", record.value());
            }
        }

        producer.send(record);
    }
}
