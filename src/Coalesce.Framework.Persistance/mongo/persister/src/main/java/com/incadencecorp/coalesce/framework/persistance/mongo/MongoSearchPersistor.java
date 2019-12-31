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

package com.incadencecorp.coalesce.framework.persistance.mongo;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;

import java.util.Collections;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class MongoSearchPersistor extends MongoPersistor implements ICoalesceSearchPersistor {

    /**
     * Default Constructor
     */
    public MongoSearchPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link MongoSettings}
     */
    public MongoSearchPersistor(Map<String, String> params)
    {
        super(params);
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        return null;
    }
}
