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

package com.incadencecorp.coalesce.synchronizer.service.tests;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.scanners.AfterLastModifiedScanImpl;
import com.incadencecorp.coalesce.synchronizer.service.scanners.AfterLastModifiedScanImpl2;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.util.HashMap;
import java.util.Map;

/**
 * These test ensure property behavior of {@link AfterLastModifiedScanImpl} and {@link AfterLastModifiedScanImpl2}.
 *
 * @author Derek Clemenzi
 */
public class AfterLastModifiedScanImplTest {

    /**
     * This test ensures that running the scanner twice will not return results the second time due to the last scan
     * parameter being updated.
     */
    @Test
    public void testAfterLastModifiedScanImpl() throws Exception
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName("OEEvent");

        Map<String, String> params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_SCANNER_DAYS, "3");
        params.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS,
                   JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusDays(2)));
        params.put(SynchronizerParameters.PARAM_SCANNER_CQL, "\"coalesceentity.name\" = 'OEEvent'");

        DerbyPersistor source = new DerbyPersistor();

        source.saveEntity(false, entity);
        source.saveTemplate(CoalesceEntityTemplate.create(entity));

        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(source);
        scan.setProperties(params);

        // Verify Hit
        CachedRowSet results = scan.scan();
        Assert.assertEquals(1, results.size());

        scan.finished(true, results);

        Thread.sleep(1000);

        // Verify No Hits
        results = scan.scan();
        Assert.assertEquals(0, results.size());

        entity.markAsDeleted();
        source.saveEntity(true, entity);

    }

    /**
     * TODO Due to the format Derby returns the time this test fails and needs to be resolved.
     */
    @Test
    public void testAfterLastModifiedScanImpl2() throws Exception
    {
        // Skip This test
        Assume.assumeFalse(true);

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName("OEEvent");

        Map<String, String> params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_SCANNER_DAYS, "3");
        params.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS,
                   JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusDays(2)));
        params.put(SynchronizerParameters.PARAM_SCANNER_CQL, "\"coalesceentity.name\" = 'OEEvent'");

        DerbyPersistor source = new DerbyPersistor();

        source.saveEntity(false, entity);
        source.saveTemplate(CoalesceEntityTemplate.create(entity));

        IPersistorScan scan = new AfterLastModifiedScanImpl2();
        scan.setSource(source);
        scan.setProperties(params);

        // Verify Hit
        CachedRowSet results = scan.scan();
        Assert.assertEquals(1, results.size());

        scan.finished(true, results);

        Thread.sleep(1000);

        // Verify No Hits
        results = scan.scan();
        Assert.assertEquals(0, results.size());

        entity.markAsDeleted();
        source.saveEntity(true, entity);
    }

}
