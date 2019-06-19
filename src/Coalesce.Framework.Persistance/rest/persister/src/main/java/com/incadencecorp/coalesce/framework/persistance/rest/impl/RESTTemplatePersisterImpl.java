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

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * This implementation uses the REST data controllers.
 *
 * @author Derek Clemenzi
 */
public class RESTTemplatePersisterImpl implements ICoalesceTemplatePersister {

    private static Logger LOGGER = LoggerFactory.getLogger(RESTTemplatePersisterImpl.class);

    private final String url;

    /**
     * Default constructor pulling properties from {@link RESTPersisterImplSettings}.
     */
    public RESTTemplatePersisterImpl()
    {
        this(RESTPersisterImplSettings.getProperties());
    }

    /**
     * Default constructor with user defined configuration.
     *
     * @param props configuration
     * @see RESTPersisterImplSettings
     */
    public RESTTemplatePersisterImpl(Map<String, String> props)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Properties");
            for (Map.Entry<String, String> entry : props.entrySet())
            {
                LOGGER.debug("\t{}={}", entry.getKey(), entry.getValue());
            }
        }

        url = props.getOrDefault(RESTPersisterImplSettings.PARAM_TEMPLATE_URL,
                                 RESTPersisterImplSettings.getTemplateUrlAsString());

    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target;
            Response response;

            for (CoalesceEntityTemplate template : templates)
            {
                target = client.target(url + "/xml");

                LOGGER.debug("URI: PUT {}", target.getUri());

                response = target.request().post(Entity.xml(template.toXml()));

                if (response.getStatus() != 200)
                {
                    throw new CoalescePersistorException(
                            "(FAILED) Code: " + response.getStatus() + " PUT " + target.getUri());
                }
            }
        }
        finally
        {
            client.close();
        }
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target;
            Response response;

            for (String key : keys)
            {
                target = client.target(url + "/" + key);

                LOGGER.debug("URI: POST {}", target.getUri());

                response = target.request(MediaType.APPLICATION_XML).delete();

                if (response.getStatus() != 204)
                {
                    throw new CoalescePersistorException(
                            "(FAILED) Code: " + response.getStatus() + " GET " + target.getUri());
                }
            }
        }
        finally
        {
            client.close();
        }
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target;
            Response response;

            for (CoalesceEntityTemplate template : templates)
            {
                target = client.target(url + "/" + template.getKey() + "/register");

                LOGGER.debug("URI: PUT {}", target.getUri());

                response = target.request(MediaType.TEXT_PLAIN).put(Entity.text(""));

                if (response.getStatus() != 204)
                {
                    throw new CoalescePersistorException(
                            "(FAILED) Code: " + response.getStatus() + " PUT " + target.getUri());
                }
            }
        }
        finally
        {
            client.close();
        }
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new NotImplementedException("unregisterTemplate");
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target = client.target(url + "/" + key + "/xml");

            LOGGER.debug("URI: GET {}", target.getUri());

            Response response = target.request(MediaType.APPLICATION_XML).get();

            if (response.getStatus() != 200)
            {
                throw new CoalescePersistorException("(FAILED) Code: " + response.getStatus() + " GET " + target.getUri());
            }

            try
            {
                return CoalesceEntityTemplate.create(response.readEntity(String.class));
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(e);
            }
        }
        finally
        {
            client.close();
        }
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target = client.target(url + "/" + name + "/" + source + "/" + version + "/xml");

            LOGGER.debug("URI: GET {}", target.getUri());

            Response response = target.request(MediaType.APPLICATION_XML).get();

            if (response.getStatus() != 200)
            {
                throw new CoalescePersistorException("(FAILED) Code: " + response.getStatus() + " GET " + target.getUri());
            }

            try
            {
                return CoalesceEntityTemplate.create(response.readEntity(String.class));
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(e);
            }
        }
        finally
        {
            client.close();
        }
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return getEntityTemplate(name, source, version).getKey();
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        Client client = ClientBuilder.newClient();

        try
        {
            WebTarget target = client.target(url);
            target.register(JacksonJaxbJsonProvider.class);

            LOGGER.debug("URI: GET {}", target.getUri());

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() != 200)
            {
                throw new CoalescePersistorException("(FAILED) Code: " + response.getStatus() + " GET " + target.getUri());
            }

            return response.readEntity(List.class);
        }
        finally
        {
            client.close();
        }
    }
}
