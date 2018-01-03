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

package com.incadencecorp.coalesce.notification.tests;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityJob;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import com.incadencecorp.coalesce.framework.util.CoalesceNotifierUtil;
import org.junit.Test;

/**
 * Because of the nature of how notification works these tests are primarily to
 * ensure that exceptions are not thrown and require a visual inspection to
 * ensure proper behavior.
 *
 * @author Derek Clemenzi
 */
public abstract class AbstractCoalesceNotifierTests {

    private String name;

    public AbstractCoalesceNotifierTests(ICoalesceNotifier notifier)
    {
        CoalesceNotifierUtil.setNotifier(notifier);

        name = notifier.getClass().getSimpleName();
    }

    @Test
    public void sendMetrics() throws Exception
    {
        StopWatch watch = new StopWatch();
        watch.start();
        watch.finish();

        MetricResults<CoalesceStringResponseType> metric;
        CoalesceStringResponseType result;

        result = new CoalesceStringResponseType();

        metric = new MetricResults<>("TEST");
        metric.setWatch(watch);
        metric.setResults(result);

        CoalesceNotifierUtil.sendMetrics(name, metric);

        // Test Failure w/ No Reason
        result.setStatus(EResultStatus.FAILED);

        metric = new MetricResults<>("TEST");
        metric.setWatch(watch);
        metric.setResults(result);

        CoalesceNotifierUtil.sendMetrics(name, metric);

        // Test Failure w/ Reason
        result.setError("Hello World");

        metric = new MetricResults<>("TEST");
        metric.setWatch(watch);
        metric.setResults(result);

        CoalesceNotifierUtil.sendMetrics(name, metric);

    }

    @Test
    public void sendCrud() throws Exception
    {
        CoalesceEntity entity = new TestEntity();
        entity.initialize();

        for (ECrudOperations operation : ECrudOperations.values())
        {
            CoalesceNotifierUtil.sendCrud(name, operation, entity);
        }
    }

    @Test
    public void sendLinkage() throws Exception
    {
        CoalesceEntity entity1 = new TestEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new TestEntity();
        entity2.initialize();

        for (ECrudOperations operation : ECrudOperations.values())
        {
            CoalesceNotifierUtil.sendLinkage(name, operation, entity1, ELinkTypes.CREATED, entity2);
        }
    }

    @Test
    public void sendAudit() throws Exception
    {
        for (EAuditCategory category : EAuditCategory.values())
        {
            for (EAuditLevels level : EAuditLevels.values())
            {
                CoalesceNotifierUtil.sendAudit(name, category, level, category.toString() + level.toString());
            }
        }
    }

    @Test
    public void sendJobComplete() throws Exception
    {
        CoalesceSaveEntityJob job = new CoalesceSaveEntityJob(null);

        CoalesceNotifierUtil.sendJobComplete(job);
    }

    @Test
    public void sendMessage() throws Exception
    {
        CoalesceNotifierUtil.sendMessage("unittest", "test", "test");
    }

}
