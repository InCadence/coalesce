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

package com.incadencecorp.coalesce.services.common.client.jaxrs;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.incadencecorp.coalesce.datamodel.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.services.api.IEnumerationDataController;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;
import com.incadencecorp.coalesce.services.common.client.jaxrs.datamodel.AsscociatedValueMap;
import com.incadencecorp.coalesce.services.common.client.jaxrs.datamodel.EnumerationList;
import com.incadencecorp.coalesce.services.common.client.jaxrs.datamodel.EnumerationValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class EnumerationClientJaxRS implements IEnumerationDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationClientJaxRS.class);

    private final String root;

    public EnumerationClientJaxRS(String uri)
    {
        root = uri;
    }

    @Override
    public List<EnumMetadataPojoRecord> getEnumerations() throws RemoteException
    {
        Class<List<String>> clazz;
        return get(root, EnumerationList.class);
    }

    @Override
    public EnumerationPojoEntity getEnumeration(String key) throws RemoteException
    {

        return get(root + "/" + key, EnumerationPojoEntity.class);
    }

    @Override
    public List<EnumValuesRecord> getEnumerationValues(String key) throws RemoteException
    {
        return get(root + "/" + key + "/values", EnumerationValueList.class);
    }

    @Override
    public Map<String, String> getEnumerationAssociatedValues(String key, String valuekey) throws RemoteException
    {
        return get(root + "/" + key + "/" + "values" + "/" + valuekey + "/" + "associatedvalues", AsscociatedValueMap.class);
    }

    private <T> T get(String uri, Class<T> clazz)
    {
        T results;

        Client client = ClientBuilder.newClient();

        try
        {
            LOGGER.debug("Requesting: {}", uri);

            WebTarget target = client.target(uri);
            target.register(JacksonJsonProvider.class);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            results = response.readEntity(clazz);
        }
        finally
        {
            client.close();
        }

        return results;
    }

}
