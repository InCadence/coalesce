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

package com.incadencecorp.coalesce.framework.jobs.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple collection using the StopWatch to record the time it took during each stage of a pipeline.
 *
 * @author Derek Clemenzi
 */
public class PipelineMetrics {

    private StopWatch watch = new StopWatch();
    private LinkedHashMap<String, Long> metrics = new LinkedHashMap<>();

    /**
     * Default Constructor
     */
    public PipelineMetrics()
    {
        watch.start();
    }

    /**
     * Records amount of time (ms) from the last time finish was called.
     *
     * @param name of pipeline's stage
     */
    public void finish(String name)
    {
        watch.finish();
        metrics.put(name, watch.getTotalLife());
        watch.reset();
        watch.start();
    }

    /**
     * @return the time spent on each stage of the pipeline.
     */
    public Map<String, Long> getMeterics()
    {
        return metrics;
    }

}
