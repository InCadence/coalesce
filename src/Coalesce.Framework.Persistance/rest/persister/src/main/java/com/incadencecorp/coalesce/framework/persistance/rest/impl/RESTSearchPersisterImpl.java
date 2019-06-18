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
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import org.apache.commons.lang3.NotImplementedException;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class RESTSearchPersisterImpl extends RESTPersisterImpl implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTPersisterImpl.class);

    private final String url;

    /**
     * Default constructor with user defined configuration.
     *
     * @param props configuration
     * @see RESTPersisterImplSettings
     */
    public RESTSearchPersisterImpl(Map<String, String> props)
    {
        super(props);

        url = props.getOrDefault(RESTPersisterImplSettings.PARAM_SEARCH_URL,
                                 RESTPersisterImplSettings.getEntityUrlAsString());
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        throw new NotImplementedException("search");
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        // TODO Pull capabilities from API
        EnumSet<EPersistorCapabilities> capabilities = super.getCapabilities();
        //capabilities.addAll(EnumSet.of(EPersistorCapabilities.SEARCH));

        return capabilities;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();

        return capability;
    }
}
