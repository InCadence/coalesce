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

package com.incadencecorp.coalesce.framework.persistance.rest.impl;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class RESTPersisterImpl extends RESTTemplatePersisterImpl implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTPersisterImpl.class);

    private final String url;

    /**
     * Default constructor with user defined configuration.
     *
     * @param props configuration
     * @see RESTPersisterImplSettings
     */
    public RESTPersisterImpl(Map<String, String> props)
    {
        super(props);

        url = props.getOrDefault(RESTPersisterImplSettings.PARAM_ENTITY_URL,
                                 RESTPersisterImplSettings.getEntityUrlAsString());
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target;
            Response response;

            for (CoalesceEntity entity : entities)
            {
                if (entity.isNew())
                {
                    target = client.target(url + "/" + entity.getKey() + ".xml");
                    LOGGER.debug("URI: PUT {}", target.getUri());
                    response = target.request(MediaType.APPLICATION_XML).put(Entity.xml(entity.toXml()));
                    entity.setStatus(ECoalesceObjectStatus.ACTIVE);
                }
                else if (entity.isMarkedDeleted() && allowRemoval)
                {
                    target = client.target(url + "/" + entity.getKey());
                    LOGGER.debug("URI: DELETE {}", target.getUri());
                    response = target.request(MediaType.APPLICATION_XML).delete();
                }
                else
                {
                    target = client.target(url + "/" + entity.getKey() + ".xml");
                    LOGGER.debug("URI: POST {}", target.getUri());
                    response = target.request(MediaType.APPLICATION_XML).post(Entity.xml(entity.toXml()));
                }

                if (response.getStatus() != 204)
                {
                    throw new CoalescePersistorException(
                            "(FAILED) Code: " + response.getStatus() + " " + target.getUri());
                }
            }
        }
        finally
        {
            client.close();
        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        List<CoalesceEntity> results = new ArrayList<>();

        for (String xml : getEntityXml(keys))
        {
            results.add(CoalesceEntity.create(xml));
        }

        return results.toArray(new CoalesceEntity[results.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> results = new ArrayList<>();

        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target;
            Response response;

            for (String key : keys)
            {
                target = client.target(url + "/" + key + ".xml");

                LOGGER.debug("URI: GET {}", target.getUri());

                response = target.request(MediaType.APPLICATION_XML).get();

                if (response.getStatus() == 200)
                {
                    results.add(response.readEntity(String.class));
                }
                else
                {
                    LOGGER.warn("(FAILED) Code: {} GET {}", response.getStatus(), target.getUri());
                }
            }
        }
        finally
        {
            client.close();
        }

        return results.toArray(new String[results.size()]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = EnumSet.of(EPersistorCapabilities.CREATE,
                                                             EPersistorCapabilities.READ,
                                                             EPersistorCapabilities.UPDATE,
                                                             EPersistorCapabilities.READ_TEMPLATES);

        return enumSet;
    }
}
