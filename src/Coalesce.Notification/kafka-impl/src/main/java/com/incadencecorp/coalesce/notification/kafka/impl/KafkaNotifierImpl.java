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

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.ShutdownAutoCloseable;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * This implementation uses Kafka to push notifications.
 *
 * @author Derek Clemenzi
 */
public class KafkaNotifierImpl implements ICoalesceNotifier, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaNotifierImpl.class);

    public static final String TOPIC_METRICS = "com/incadence/metrics";
    public static final String TOPIC_CRUD = "com/incadence/crud";
    public static final String TOPIC_LINKAGE = "com/incadence/linkage";
    public static final String TOPIC_AUDIT = "com/incadence/audit";
    public static final String TOPIC_JOB_COMPLETE = "com/incadence/job";

    public static final String PROP_ZOOKEEPER = "zookeepers";

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private KafkaProducer<String, Object> producer;
    private String zookeeper;

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    public KafkaNotifierImpl()
    {
        // TODO Not Implemented
        throw new NotImplementedException();
    }

    /**
     * Default Constructor
     *
     * @param props configuration properties
     */
    public KafkaNotifierImpl(Properties props)
    {
        zookeeper = props.getProperty(PROP_ZOOKEEPER);
        producer = new KafkaProducer<>(props);

        ShutdownAutoCloseable.createShutdownHook(this);
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

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

        sendRecord(new ProducerRecord<>(TOPIC_METRICS, properties));
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("name", task);
        properties.put("operation", operation.toString());
        properties.put("entitykey", data.getKey());
        properties.put("entityname", data.getName());
        properties.put("entitysource", data.getSource());
        properties.put("entityversion", data.getVersion());

        sendRecord(new ProducerRecord<>(TOPIC_CRUD, properties));
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        Dictionary<String, Object> properties = new Hashtable<>();
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

        sendRecord(new ProducerRecord<>(TOPIC_LINKAGE, properties));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("name", task);
        properties.put("category", category.toString());
        properties.put("level", level.toString());
        properties.put("message", message);

        sendRecord(new ProducerRecord<>(TOPIC_AUDIT, properties));
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("name", job.getName());
        properties.put("id", job.getJobId());
        properties.put("status", job.getJobStatus().toString());

        sendRecord(new ProducerRecord<>(TOPIC_JOB_COMPLETE, properties));
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

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void sendRecord(ProducerRecord<String, Object> record)
    {
        creatTopic(record.topic());
        producer.send(record);
    }

    private void creatTopic(String topic)
    {
        // TODO Consider caching whether the topic exixts.

        String zookeeperConnect = zookeeper;
        // TODO move these to properties
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;
        int partitions = 20;
        int replication = 1;
        boolean isSecureKafkaCluster = false;

        ZkClient zkClient = null;

        try
        {
            zkClient = new ZkClient(zookeeperConnect, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$);
            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);

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
            if (zkClient != null)
            {
                zkClient.close();
            }
        }
    }

}
