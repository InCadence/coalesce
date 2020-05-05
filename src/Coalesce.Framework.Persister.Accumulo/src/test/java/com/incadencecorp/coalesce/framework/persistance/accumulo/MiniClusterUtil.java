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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class MiniClusterUtil {

    private static final Path DB = Paths.get("src", "test", "resources", "db");
    private static final Object SYNC_CLUSTER = new Object();

    private static MiniAccumuloCluster accumulo;
    private static boolean isRunning = false;

    public static void startCluster() throws IOException, InterruptedException
    {
        synchronized (SYNC_CLUSTER)
        {
            if (!isRunning)
            {
                FileUtils.deleteDirectory(DB.toFile());
                Files.createDirectory(DB);

                accumulo = new MiniAccumuloCluster(DB.toFile(), "unit_test");
                accumulo.start();
                isRunning = true;
            }
        }
    }

    public static void stopCluster() throws IOException, InterruptedException
    {
        synchronized (SYNC_CLUSTER)
        {
            if (isRunning)
            {
                accumulo.stop();
                isRunning = false;

                FileUtils.deleteDirectory(DB.toFile());
                Files.createDirectory(DB);
            }
        }
    }

    public static Map<String, String> addClusterParameters(Map<String, String> parameters)
    {
        if (isRunning)
        {
            parameters.put(AccumuloDataConnector.INSTANCE_ID, accumulo.getInstanceName());
            parameters.put(AccumuloDataConnector.ZOOKEEPERS, accumulo.getZooKeepers());
            parameters.put(AccumuloDataConnector.USER, "root");
            parameters.put(AccumuloDataConnector.PASSWORD, "unit_test");
        }

        return parameters;
    }

}
