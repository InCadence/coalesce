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

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistorSearch;
import com.incadencecorp.coalesce.search.AbstractSearchTest;
import org.junit.BeforeClass;

/**
 * @author Derek Clemenzi
 */
public class ElasticSearchPersisterSearch2IT extends AbstractSearchTest<ElasticSearchPersistorSearch> {

    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY, "src/test/resources");
    }

    @Override
    protected ElasticSearchPersistorSearch createPersister() throws CoalescePersistorException
    {
        return new ElasticSearchPersistorSearch();
    }
}
