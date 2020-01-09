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

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.RequestOptions;
import com.microsoft.azure.documentdb.ResourceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class CosmosPersistor extends CosmosTemplatePersistor implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CosmosPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link CosmosSettings}
     */
    public CosmosPersistor(Map<String, String> params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Implementations
    --------------------------------------------------------------------------*/

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        RequestOptions options = new RequestOptions();

        CosmosEntityIterator it = new CosmosEntityIterator(getClient(), options, new CosmosNormalizer(), isAuthoritative());
        try
        {
            it.iterate(allowRemoval, entities);
        }
        catch (CoalesceException e)
        {
            throw new CoalescePersistorException(e);
        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        return Arrays.stream(getEntityXml(keys)).map(CoalesceEntity::create).toArray(CoalesceEntity[]::new);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> entities = new ArrayList<>();

        for (String key : keys)
        {
            ResourceResponse<Document> response = CosmosHelper.readDocument(getClient(),
                                                                            CosmosConstants.COLLECTION_ENTITIES,
                                                                            key);
            if (response.getStatusCode() / 100 == 2)
            {
                entities.add(response.getResource().getString(CosmosConstants.FIELD_XML));
            }
        }

        return entities.toArray(new String[0]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = EnumSet.of(EPersistorCapabilities.CREATE,
                                                                  EPersistorCapabilities.UPDATE,
                                                                  EPersistorCapabilities.DELETE,
                                                                  EPersistorCapabilities.READ_TEMPLATES);

        if (isAuthoritative())
        {
            capabilities.add(EPersistorCapabilities.READ);
        }

        return capabilities;
    }

}
