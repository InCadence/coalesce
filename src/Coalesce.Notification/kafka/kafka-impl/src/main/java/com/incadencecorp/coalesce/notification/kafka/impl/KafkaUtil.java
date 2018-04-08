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

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Derek Clemenzi
 */
public class KafkaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtil.class);

    /**
     * Creates a topic within Kafka.
     *
     * @param topic to be created.
     */
    public static void createTopic(String topic,
                                   String zookeeper,
                                   int sessionTimeout,
                                   int connectionTimeout,
                                   boolean isSecure,
                                   int partitions,
                                   int replication,
                                   Properties config)
    {
        ZkClient zkClient = new ZkClient(zookeeper, sessionTimeout, connectionTimeout, ZKStringSerializer$.MODULE$);

        try
        {
            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeper), isSecure);

            if (!AdminUtils.topicExists(zkUtils, topic))
            {
                LOGGER.info("Creating Topic ({}) w/ {} partitions", topic, partitions);

                AdminUtils.createTopic(zkUtils, topic, partitions, replication, config, AdminUtils.createTopic$default$6());
            }
        }
        finally
        {
            zkClient.close();
        }
    }

}
