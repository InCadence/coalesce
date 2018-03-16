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
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * This implementation uses Kafka to push notifications.
 *
 * @author Derek Clemenzi
 */
public class KafkaNotifierImpl implements ICoalesceNotifier, AutoCloseable {

    // Topic Definitions
    public static final String TOPIC_METRICS = "com.incadence.metrics";
    public static final String TOPIC_CRUD = "com.incadence.crud";
    public static final String TOPIC_LINKAGE = "com.incadence.linkage";
    public static final String TOPIC_AUDIT = "com.incadence.audit";
    public static final String TOPIC_JOB_COMPLETE = "com.incadence.job";

    // Property Definitions
    public static final String PROP_ZOOKEEPER = "zookeepers";
    public static final String PROP_SESSION_TIMEOUT = "kafka.session.timeout";
    public static final String PROP_CONN_TIMEOUT = "kafka.connection.timeout";
    public static final String PROP_PARTITIONS = "kafka.partitions";
    public static final String PROP_REPLICATION = "kafka.replication";
    public static final String PROP_IS_SECURE = "kafka.isSecure";


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
        Map<String, Object> parameters = new HashMap<>();
        parameters.putAll(props);

        zookeeper = props.get(PROP_ZOOKEEPER);
        producer = new KafkaProducer<>(parameters);

        ShutdownAutoCloseable.createShutdownHook(this);

        // Set Properties
        sessionTimeoutMs = props.containsKey(PROP_SESSION_TIMEOUT) ? Integer.parseInt(props.get(PROP_SESSION_TIMEOUT)) :
                10 * 1000;
        connectionTimeoutMs = props.containsKey(PROP_CONN_TIMEOUT) ? Integer.parseInt(props.get(PROP_CONN_TIMEOUT)) :
                8 * 1000;
        partitions = props.containsKey(PROP_PARTITIONS) ? Integer.parseInt(props.get(PROP_PARTITIONS)) : 20;
        replication = props.containsKey(PROP_REPLICATION) ? Integer.parseInt(props.get(PROP_REPLICATION)) : 1;
        isSecureKafkaCluster = props.containsKey(PROP_IS_SECURE) && Boolean.parseBoolean(props.get(PROP_IS_SECURE));

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
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", task);
        properties.put("pending", results.getWatch().getPendingLife());
        properties.put("working", results.getWatch().getWorkLife());
        properties.put("total", results.getWatch().getTotalLife());
        properties.put("successful", results.isSuccessful());
        if (!results.isSuccessful() && !StringHelper.isNullOrEmpty(results.getResults().getError()))
        {
            properties.put("error", results.getResults().getError());
        }

        sendRecord(new ProducerRecord<>(TOPIC_METRICS, toJSONString(properties)));
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", task);
        properties.put("operation", operation.toString());
        properties.put("entitykey", data.getKey());
        properties.put("entityname", data.getName());
        properties.put("entitysource", data.getSource());
        properties.put("entityversion", data.getVersion());

        sendRecord(new ProducerRecord<>(TOPIC_CRUD, toJSONString(properties)));
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", task);
        properties.put("operation", operation.toString());
        properties.put("entity1key", entity1.getKey());
        properties.put("entity1name", entity1.getName());
        properties.put("entity1source", entity1.getSource());
        properties.put("entity1version", entity1.getVersion());
        properties.put("relationship", relationship.toString());
        properties.put("entity2key", entity2.getKey());
        properties.put("entity2name", entity2.getName());
        properties.put("entity2source", entity2.getSource());
        properties.put("entity2version", entity2.getVersion());

        sendRecord(new ProducerRecord<>(TOPIC_LINKAGE, toJSONString(properties)));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", task);
        properties.put("category", category.toString());
        properties.put("level", level.toString());
        properties.put("message", message);

        sendRecord(new ProducerRecord<>(TOPIC_AUDIT, toJSONString(properties)));
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", job.getName());
        properties.put("id", job.getJobId());
        properties.put("status", job.getJobStatus().toString());

        sendRecord(new ProducerRecord<>(TOPIC_JOB_COMPLETE, toJSONString(properties)));
    }

    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        sendRecord(new ProducerRecord<>(topic, key, value));
    }

    @Override
    public void close() throws Exception
    {
        producer.close();
    }

    /**
     * Creates a topic within Kafka.
     *
     * @param topic to be created.
     */
    public void creatTopic(String topic)
    {
        ZkClient zkClient = new ZkClient(zookeeper, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$);

        try
        {
            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeper), isSecureKafkaCluster);

            if (!AdminUtils.topicExists(zkUtils, topic))
            {
                Properties topicConfig = new Properties(); // add per-topic configurations settings here
                AdminUtils.createTopic(zkUtils,
                                       topic,
                                       partitions,
                                       replication,
                                       topicConfig,
                                       AdminUtils.createTopic$default$6());
            }
        }
        finally
        {
            zkClient.close();
        }

        topics.add(topic);
    }
    
    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private String toJSONString(Map properties)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        try
        {
            return mapper.writeValueAsString(properties);
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
            creatTopic(record.topic());
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
