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

import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Derek Clemenzi
 */
public class KafkaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtil.class);

    public static final String PROP_ZOOKEEPER = "zookeepers";
    public static final String PROP_SESSION_TIMEOUT = "kafka.session.timeout";
    public static final String PROP_CONN_TIMEOUT = "kafka.connection.timeout";
    public static final String PROP_PARTITIONS = "kafka.partitions";
    public static final String PROP_REPLICATION = "kafka.replication";
    public static final String PROP_IS_SECURE = "kafka.isSecure";

    private static final List<String> TOPICS = Collections.synchronizedList(new ArrayList<>());

    public static Properties loadProperties(IConfigurationsConnector connector, String topic)
    {
        Properties config = new Properties();
        config.putAll(new PropertyLoader(connector, "default.properties").getSettings());
        config.putAll(new PropertyLoader(connector, topic + ".properties").getSettings());

        return config;
    }

    public static String normalizeTopic(String value)
    {
        return value.replaceAll("[^\\w._-]", ".");
    }

    public static void createTopic(String topic, Map<String, String> props, IConfigurationsConnector connector)
    {
        String normalized = normalizeTopic(topic);

        if (!TOPICS.contains(normalized))
        {
            createTopic(normalized, props, loadProperties(connector, normalized));
        }
    }

    /**
     * Creates a topic within Kafka.
     *
     * @param topic to be created.
     */
    public static void createTopic(String topic, Map<String, String> props, Properties config)
    {
        String normalized = normalizeTopic(topic);

        if (TOPICS.add(normalized))
        {
            LOGGER.debug("Creating Topic: {}", normalized);

            String zookeeper = props.get(PROP_ZOOKEEPER);
            int sessionTimeout = Integer.parseInt(props.getOrDefault(PROP_SESSION_TIMEOUT, "10000"));
            int connectionTimeout = Integer.parseInt(props.getOrDefault(PROP_CONN_TIMEOUT, "8000"));
            boolean isSecure = props.containsKey(PROP_IS_SECURE) && Boolean.parseBoolean(props.get(PROP_IS_SECURE));
            int partitions = Integer.parseInt(props.getOrDefault(PROP_PARTITIONS, "20"));
            int replication = Integer.parseInt(props.getOrDefault(PROP_REPLICATION, "1"));

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            try
            {
                Thread.currentThread().setContextClassLoader(null);

                ZkClient zkClient = new ZkClient(zookeeper, sessionTimeout, connectionTimeout, ZKStringSerializer$.MODULE$);

                try
                {
                    ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeper), isSecure);

                    if (!AdminUtils.topicExists(zkUtils, normalized))
                    {
                        LOGGER.info("Creating Topic ({}) w/ {} partitions", normalized, partitions);

                        AdminUtils.createTopic(zkUtils,
                                               normalized,
                                               partitions,
                                               replication,
                                               config,
                                               AdminUtils.createTopic$default$6());
                    }
                    else
                    {
                        LOGGER.debug("Topic ({}) Already Exists", normalized);
                    }
                }
                finally
                {
                    zkClient.close();
                }
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

}
