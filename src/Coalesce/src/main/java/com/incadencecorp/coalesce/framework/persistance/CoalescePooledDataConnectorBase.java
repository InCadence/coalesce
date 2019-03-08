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

package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Derek Clemenzi
 */
public abstract class CoalescePooledDataConnectorBase extends CoalesceDataConnectorBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalescePooledDataConnectorBase.class);

    private final BasicDataSource source = new BasicDataSource();
    private int init = 5;
    private int max = 100;
    private Class<?> driverClass;

    public CoalescePooledDataConnectorBase(ServerConn settings, int max, Class<?> clazz)
    {
        setSettings(settings);
        this.max = max;
        this.driverClass = clazz;
    }

    protected synchronized void initialize(String url, Properties props) throws SQLException, CoalescePersistorException
    {
        source.setDriverClassLoader(driverClass.getClassLoader());
        source.setDriverClassName(driverClass.getName());

        if (props != null && !props.isEmpty())
        {
            source.setConnectionProperties(formatProperties(props));
        }

        source.setUsername(getSettings().getUser());
        source.setPassword(getSettings().getPassword());
        source.setUrl(url);
        source.setMinIdle(init);
        source.setInitialSize(init);
        source.setMaxTotal(max);
        source.setMaxIdle(max);
        source.setMaxWaitMillis(-1);
        source.setTestOnBorrow(true);

        source.setRemoveAbandonedTimeout(30);
        source.setRemoveAbandonedOnBorrow(true);

        source.getConnection().close();
    }

    private String formatProperties(Properties props)
    {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Object, Object> prop : props.entrySet())
        {
            sb.append(prop.getKey()).append("=").append(prop.getValue()).append(";");
        }

        return sb.toString().trim();
    }

    @Override
    protected Connection getDBConnection() throws SQLException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("{} Active: {} Idle: {} Max: {}",
                         source.getDriverClassName(),
                         source.getNumActive(),
                         source.getNumIdle(),
                         source.getMaxIdle());
        }
        return source.getConnection();
    }
}
