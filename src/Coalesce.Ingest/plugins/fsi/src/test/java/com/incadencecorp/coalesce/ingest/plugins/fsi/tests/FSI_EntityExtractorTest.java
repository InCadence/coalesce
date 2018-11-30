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

package com.incadencecorp.coalesce.ingest.plugins.fsi.tests;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.FSI_EntityExtractor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * These tests verify proper behaviour of the {@link FSI_EntityExtractor}.
 *
 * @author Derek Clemenzi
 */
public class FSI_EntityExtractorTest {

    /**
     * This test verifies that a CSV can be converted into a {@link TestEntity}
     */
    @Test
    public void testExtraction() throws Exception
    {
        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(new DerbyPersistor());
        framework.saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(new TestEntity()));

        String json = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "format.json")));

        Map<String, String> params = new HashMap<>();
        params.put(FSI_EntityExtractor.PARAM_JSON, json);
        params.put(FSI_EntityExtractor.PARAM_SPLIT, ",");

        FSI_EntityExtractor extractor = new FSI_EntityExtractor();
        extractor.setFramework(framework);
        extractor.setProperties(params);

        // TODO This does not work
        extractor.extract("unknown", "1,2,3");

    }

}
