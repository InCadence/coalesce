/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;

/**
 * @author Derek Clemenzi
 */
public class AccumuloPerformanceIT {

    private static final int COUNT = 100;
    private static final int LINK_COUNT = 0;
    private static final boolean USE_MOCK = false;

    @Before
    public void registerEntities()
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        try
        {
            CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

            createPersister().registerTemplate(template);
            new AccumuloPersistor().saveTemplate(template);
        }
        catch (CoalescePersistorException | SAXException | IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void computeMetrics() throws Exception
    {
        AccumuloPersistor2 persister = createPersister();

        AccumuloSettings.setPersistFieldAttr(false);
        AccumuloSettings.setPersistRecordAttr(false);
        AccumuloSettings.setPersistFieldDefAttr(false);
        AccumuloSettings.setPersistSectionAttr(false);
        AccumuloSettings.setPersistRecordsetAttr(false);
        AccumuloSettings.setPersistLinkageAttr(false);

        long[][] results1 = run(persister);
        long[][] results2 = run(new AccumuloPersistor());

        for (int ii = 0; ii < COUNT; ii++)
        {
            System.out.println(results1[ii][0] + " " + results2[ii][0] + " " + results1[ii][1] + " " + results2[ii][1]);
        }


    }

    private long[][] run(ICoalescePersistor persister) throws Exception
    {
        long[][] results = new long[COUNT][2];
        List<CoalesceEntity> entities = new ArrayList<>();

        StopWatch watch = new StopWatch();

        for (int ii = 0; ii < COUNT; ii++)
        {
            TestEntity entity = new TestEntity();
            entity.initialize();
            entity.addRecord1();
            entity.addRecord1();
            TestRecord record1 = entity.addRecord1();
            record1.getBooleanField().setValue(false);
            record1.getGeoField().setValue(new Coordinate(1, 1));
            record1.getGeoListField().setValue(new Coordinate[] { new Coordinate(1, 1) });

            createLinkages(entity, LINK_COUNT);

            entities.add(entity);

            watch.start();
            persister.saveEntity(true, entity);
            watch.finish();
            results[ii][0] = watch.getTotalLife();
            watch.reset();
        }

        //*
        for (int ii = 0; ii < COUNT; ii++)
        {
            CoalesceEntity entity = entities.get(ii);
            entity.markAsDeleted();

            watch.start();
            persister.saveEntity(true, entity);
            watch.finish();
            results[ii][1] = watch.getTotalLife();
            watch.reset();
        }
        //*/

        return results;
    }

    private void createLinkages(CoalesceEntity entity, int count)
    {
        for (int ii = 0; ii < count; ii++)
        {
            CoalesceLinkage linkage = CoalesceLinkage.create(entity.getLinkageSection());
            linkage.establishLinkage(ELinkTypes.IS_PARENT_OF,
                                     ECoalesceObjectStatus.ACTIVE,
                                     UUID.randomUUID().toString(),
                                     "AA",
                                     "AA",
                                     "AA",
                                     1,
                                     new Marking("U"),
                                     "",
                                     "",
                                     "",
                                     Locale.ENGLISH);

        }
    }

    private AccumuloPersistor2 createPersister()
    {
        return new AccumuloPersistor2(getParameters());
    }

    protected Map<String, String> getParameters()
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(AccumuloDataConnector.INSTANCE_ID, AccumuloSettings.getDatabaseName());
        parameters.put(AccumuloDataConnector.ZOOKEEPERS, AccumuloSettings.getZookeepers());
        parameters.put(AccumuloDataConnector.USER, USE_MOCK ? "aa" : AccumuloSettings.getUserName());
        parameters.put(AccumuloDataConnector.PASSWORD, AccumuloSettings.getUserPassword());
        parameters.put(AccumuloDataConnector.TABLE_NAME, AccumuloDataConnector.COALESCE_SEARCH_TABLE);
        parameters.put(AccumuloDataConnector.QUERY_THREADS, Integer.toString(AccumuloSettings.getQueryThreads()));
        parameters.put(AccumuloDataConnector.RECORD_THREADS, Integer.toString(AccumuloSettings.getRecordThreads()));
        parameters.put(AccumuloDataConnector.WRITE_THREADS, Integer.toString(AccumuloSettings.getWriteThreads()));
        parameters.put(AccumuloDataConnector.GENERATE_STATS, "false");
        parameters.put(AccumuloDataConnector.COLLECT_USAGE_STATS, "false");
        parameters.put(AccumuloDataConnector.CACHING, "false");
        parameters.put(AccumuloDataConnector.LOOSE_B_BOX, "false");
        parameters.put(AccumuloDataConnector.USE_MOCK, Boolean.toString(USE_MOCK));

        return parameters;
    }

}
