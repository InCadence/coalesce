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

package com.incadencecorp.coalesce.framework.persistance.derby;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.geotools.data.Query;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;

import java.util.Collections;

/**
 * These test ensure proper mapping behavior using the {@link DerbyPersistor}.
 *
 * @author Derek Clemenzi
 */
public class DerbyMappingTest {

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();

    /**
     * This test creates two derby databases and ensures that a mapping can be defined to redirect the submitted queries.
     */
    @Test
    public void testMapping() throws Exception
    {
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();
        TestRecord record = entity2.addRecord1();
        record.getStringField().setValue("Hello World");

        DerbyPersistor persistor1 = new DerbyPersistor();
        persistor1.setSchema("coalesce1");

        DerbyPersistor persistor2 = new DerbyPersistor();
        persistor2.setSchema("coalesce2");

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(persistor1);
        framework.setSecondaryPersistors(persistor2);
        framework.registerTemplates(CoalesceEntityTemplate.create(entity1));

        persistor1.saveEntity(false, entity1);
        persistor2.saveEntity(false, entity2);

        Query query = new Query();
        query.setFilter(CoalescePropertyFactory.getEntityKey(entity1.getKey()));

        Assert.assertEquals(1, framework.search(query).getTotal());

        query.setFilter(CoalescePropertyFactory.getEntityKey(entity2.getKey()));
        Assert.assertEquals(0, framework.search(query).getTotal());

        query.setTypeName(entity2.getName());

        framework.setMapping(Collections.singletonMap(entity2.getName(), persistor2));
        Assert.assertEquals(1, framework.search(query).getTotal());

        query.setTypeName(entity2.getName());
        query.setFilter(FF.equals(CoalescePropertyFactory.getFieldProperty(record.getStringField()),
                                  FF.literal(record.getStringField().getValue())));

        Assert.assertEquals(1, framework.search(query).getTotal());
    }

}
