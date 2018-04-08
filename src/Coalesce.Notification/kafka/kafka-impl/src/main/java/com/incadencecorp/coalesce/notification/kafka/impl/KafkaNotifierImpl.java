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
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.PropertyLoader;
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
import java.nio.file.Paths;
import java.util.*;

/**
 * This implementation uses Kafka to push notifications.
 *
 * @author Derek Clemenzi
 * @see #PROP_CONFIG_DIR
 * @see #PROP_ZOOKEEPER
 * @see #PROP_SESSION_TIMEOUT
 * @see #PROP_CONN_TIMEOUT
 * @see #PROP_TOPICS
 * @see #PROP_PARTITIONS
 * @see #PROP_REPLICATION
 * @see #PROP_IS_SECURE
 */
public class KafkaNotifierImpl implements ICoalesceNotifier, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaNotifierImpl.class);

    // Topic Definitions
    public static final String TOPIC_METRICS = "com.incadence.metrics";
    public static final String TOPIC_CRUD = "com.incadence.crud";
    public static final String TOPIC_LINKAGE = "com.incadence.linkage";
    public static final String TOPIC_AUDIT = "com.incadence.audit";
    public static final String TOPIC_JOB_COMPLETE = "com.incadence.job";

    // Property Definitions
    /**
     * (Path) Directory location containing the topic configurations. Default {@value DEFAULT_CONFIG_DIR}
     */
    public static final String PROP_CONFIG_DIR = "kafka.config.dir";
    public static final String PROP_ZOOKEEPER = "zookeepers";
    public static final String PROP_SESSION_TIMEOUT = "kafka.session.timeout";
    public static final String PROP_CONN_TIMEOUT = "kafka.connection.timeout";
    public static final String PROP_TOPICS = "kafka.topics";
    public static final String PROP_PARTITIONS = "kafka.partitions";
    public static final String PROP_REPLICATION = "kafka.replication";
    public static final String PROP_IS_SECURE = "kafka.isSecure";

    private static final String DEFAULT_CONFIG_DIR = "config";

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private final KafkaProducer<String, Object> producer;
    private final String zookeeper;
    private final List<String> topics = new ArrayList<>();

    private final int sessionTimeoutMs;
    private final int connectionTimeoutMs;
    private final int partitions;
    private final int replication;
    private final boolean isSecureKafkaCluster;

    private final IConfigurationsConnector connector;
    private final Map<String, String> defaultTopicConfig = new HashMap<>();

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public KafkaNotifierImpl()
    {
        this(loadProperties());
    }

    /**
     * Default Constructor w/ user properties
     *
     * @param props configuration properties
     */
    public KafkaNotifierImpl(Map<String, String> props)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try
        {
            Thread.currentThread().setContextClassLoader(null);

            Map<String, Object> parameters = new HashMap<>();
            parameters.putAll(props);

            zookeeper = props.get(PROP_ZOOKEEPER);

            producer = new KafkaProducer<>(parameters);

            ShutdownAutoCloseable.createShutdownHook(this);

            // Set Properties
            connector = new FilePropertyConnector(props.getOrDefault(PROP_CONFIG_DIR, DEFAULT_CONFIG_DIR));
            sessionTimeoutMs = Integer.parseInt(props.getOrDefault(PROP_SESSION_TIMEOUT, "10000"));
            connectionTimeoutMs = Integer.parseInt(props.getOrDefault(PROP_CONN_TIMEOUT, "8000"));
            partitions = Integer.parseInt(props.getOrDefault(PROP_PARTITIONS, "20"));
            replication = Integer.parseInt(props.getOrDefault(PROP_REPLICATION, "1"));
            isSecureKafkaCluster = props.containsKey(PROP_IS_SECURE) && Boolean.parseBoolean(props.get(PROP_IS_SECURE));

            createTopic(TOPIC_AUDIT);
            createTopic(TOPIC_CRUD);
            createTopic(TOPIC_JOB_COMPLETE);
            createTopic(TOPIC_LINKAGE);
            createTopic(TOPIC_METRICS);

            for (String topic : (props.getOrDefault(PROP_TOPICS, "")).split(","))
            {
                if (!StringHelper.isNullOrEmpty(topic))
                {
                    createTopic(normalize(topic));
                }
            }

            defaultTopicConfig.putAll(new PropertyLoader(connector, "default.properties").getSettings());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
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

        sendRecord(new ProducerRecord<>(TOPIC_METRICS, toJSONString(event)));
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        CrudEvent event = new CrudEvent();
        event.setName(task);
        event.setOperation(operation);
        event.setMeta(new ObjectMetaData(data.getKey(), data.getName(), data.getSource(), data.getVersion()));

        sendRecord(new ProducerRecord<>(TOPIC_CRUD, toJSONString(event)));
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

        sendRecord(new ProducerRecord<>(TOPIC_LINKAGE, toJSONString(event)));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        AuditEvent event = new AuditEvent();
        event.setName(task);
        event.setCategory(category);
        event.setLevel(level);
        event.setMessage(message);

        sendRecord(new ProducerRecord<>(TOPIC_AUDIT, toJSONString(event)));
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        JobEvent event = new JobEvent();
        event.setName(job.getName());
        event.setId(job.getJobId());
        event.setStatus(job.getJobStatus());

        sendRecord(new ProducerRecord<>(TOPIC_JOB_COMPLETE, toJSONString(event)));
    }

    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        sendRecord(new ProducerRecord<>(normalize(topic), key, toJSONString(value)));
    }

    @Override
    public void close() throws Exception
    {
        producer.close();
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void createTopic(String topic)
    {
        if (topics.add(topic))
        {
            Properties config = new Properties();
            config.putAll(defaultTopicConfig);
            config.putAll(new PropertyLoader(connector, topic + ".properties").getSettings());

            KafkaUtil.createTopic(topic,
                                  zookeeper,
                                  sessionTimeoutMs,
                                  connectionTimeoutMs,
                                  isSecureKafkaCluster,
                                  partitions,
                                  replication,
                                  config);
        }
    }

    private String normalize(String value)
    {
        return value.replaceAll("[^\\w._-]", ".");
    }

    private String toJSONString(Object value)
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

    private void sendRecord(ProducerRecord<String, Object> record)
    {
        if (!topics.contains(record.topic()))
        {
            createTopic(record.topic());
        }

        producer.send(record);
    }

    private static Map<String, String> loadProperties()
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector(Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION)),
                                                   KafkaNotifierImpl.class.getName() + ".properties");

        return loader.getSettings();
    }
}
