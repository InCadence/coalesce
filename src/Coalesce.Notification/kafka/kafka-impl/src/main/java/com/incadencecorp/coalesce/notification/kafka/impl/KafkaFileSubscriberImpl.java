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

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSchedulerServiceImpl;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Not a reusable class as is. This was implemented for {@link KafkaSubscriberPartitioner}
 *
 * @author Derek Clemenzi
 * @see CoalesceParameters#PARAM_DIRECTORY
 * @see CoalesceParameters#PARAM_INTERVAL
 * @see CoalesceParameters#PARAM_INTERVAL_UNIT
 */
public class KafkaFileSubscriberImpl implements ICoalesceSubscriber, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaFileSubscriberImpl.class);

    private final CoalesceSchedulerServiceImpl service = new CoalesceSchedulerServiceImpl();
    private final Path rootDir;
    private final Integer interval;
    private final TimeUnit unit;

    public KafkaFileSubscriberImpl()
    {
        this(loadProperties());
    }

    public KafkaFileSubscriberImpl(Map<String, String> params)
    {
        rootDir = Paths.get(params.getOrDefault(CoalesceParameters.PARAM_DIRECTORY, "notifications"));
        interval = Integer.parseInt(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL, "1"));
        unit = TimeUnit.valueOf(params.getOrDefault(CoalesceParameters.PARAM_INTERVAL_UNIT, TimeUnit.MINUTES.toString()));

        LOGGER.info("Monitoring ({}) every {} {}", rootDir.toAbsolutePath(), interval, unit);
    }

    @Override
    public void setContext(BundleContext context)
    {
        // Not Implemented
    }

    @Override
    public void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler)
    {
        throw new NotImplementedException();
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        throw new NotImplementedException();
    }

    @Override
    public void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler)
    {
        throw new NotImplementedException();
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        throw new NotImplementedException();
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        throw new NotImplementedException();
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler)
    {
        // Seed
        handleTopic(topic, handler);

        // Update on Interval
        service.scheduleAtFixedRate(() -> handleTopic(topic, handler), interval, interval, unit);
    }

    private <V> void handleTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler)
    {
        List<Integer> results = new ArrayList<>();
        String path = rootDir.resolve(topic).toString();

        try
        {
            File file = new File(path);

            if (file.exists())
            {
                LineIterator iterator = FileUtils.lineIterator(file, "UTF-8");

                while (iterator.hasNext())
                {
                    String value = iterator.nextLine();
                    if (!StringHelper.isNullOrEmpty(value))
                    {
                        results.add(Integer.parseInt(value));
                    }
                }

                LOGGER.debug("Updating {}", topic);
            }
            else
            {
                LOGGER.warn("File Not Found: {}", path);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("{}: {}", e.getClass().getSimpleName(), path, e);
        }

        KeyValuePairEvent event = new KeyValuePairEvent<List<Integer>>();
        event.setKey("content");
        event.setValue(results);

        handler.handle(event);
    }

    @Override
    public void close() throws IOException
    {
        service.close();
    }

    private static Map<String, String> loadProperties()
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector(Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION)),
                                                   KafkaFileSubscriberImpl.class.getName() + ".properties");

        return loader.getSettings();
    }
}
