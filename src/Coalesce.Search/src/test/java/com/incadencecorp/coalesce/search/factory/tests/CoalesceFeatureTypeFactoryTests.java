/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.search.factory.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;

/**
 * These test ensure proper creation of feature types used during searching.
 * 
 * @author n78554
 */
public class CoalesceFeatureTypeFactoryTests {

    /**
     * Attempts to create a feature for each of Coalesce data types
     * 
     * @throws Exception
     */
    @Test
    public void testFeatureCreation() throws Exception
    {
        Map<String, ECoalesceFieldDataTypes> fields = new HashMap<String, ECoalesceFieldDataTypes>();

        for (ECoalesceFieldDataTypes type : ECoalesceFieldDataTypes.values())
        {
            fields.put(type.toString(), type);
        }

        // Create Feature
        CoalesceFeatureTypeFactory.createSimpleFeatureType(fields);

    }

}
