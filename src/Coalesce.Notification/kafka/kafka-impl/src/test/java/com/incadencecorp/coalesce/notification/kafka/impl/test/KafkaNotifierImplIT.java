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

package com.incadencecorp.coalesce.notification.kafka.impl.test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.notification.kafka.impl.KafkaNotifierImpl;
import com.incadencecorp.coalesce.notification.tests.AbstractCoalesceNotifierTests;
import org.junit.BeforeClass;

import java.nio.file.Paths;

/**
 * This test ensures that using this implementation outside of an OSGi
 * environment wont throw an exception.
 *
 * @author Derek Clemenzi
 */
public class KafkaNotifierImplIT extends AbstractCoalesceNotifierTests {

    @BeforeClass
    public static void initialize()
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY,
                           Paths.get("src", "test", "resources").toString());
    }

    public KafkaNotifierImplIT()
    {
        super(new KafkaNotifierImpl());
    }

}
