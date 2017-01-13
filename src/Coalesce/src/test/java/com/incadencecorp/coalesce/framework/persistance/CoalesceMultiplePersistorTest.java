/**
 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
 * /// Copyright 2016 - Lockheed Martin Corporation, All Rights Reserved /// ///
 * Notwithstanding any contractor copyright notice, the government has ///
 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
 * 252.227-7014. Use of this work other than as specifically authorized by ///
 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
 * Rights. The Government has the right to use, modify, /// reproduce, perform,
 * display, release or disclose this computer software /// in whole or in part,
 * in any manner, and for any purpose whatsoever, /// and to have or authorize
 * others to do so. /// /// Distribution Statement D. Distribution authorized to
 * the Department of /// Defense and U.S. DoD contractors only in support of US
 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
 * Program /// Management under the Director of the Office of Naval
 * Intelligence. ///
 * -------------------------------UNCLASSIFIED---------------------------------
 */

package com.incadencecorp.coalesce.framework.persistance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityProperties;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveTemplateJob;
import com.incadencecorp.coalesce.handlers.FileExceptionHandlerImpl;

/**
 * These unit test ensure that the framework can support multiple persistors.
 * 
 * @author n78554
 *
 */
public class CoalesceMultiplePersistorTest extends AbstractFileHandlerTests {

    /**
     * Creates an entity with the MockPersistor to simulate an error and
     * confirms that the error is written out by the handler.
     * 
     * @throws Exception
     */
    @Test
    public void testJobFailure() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1", null, null);

        CoalesceSaveEntityProperties parameters = new CoalesceSaveEntityProperties();
        parameters.setEntities(entity);

        MockPersister target = new MockPersister();
        target.setThrowException(true);

        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "src/test/resources");
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "2");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);

        CoalesceSaveEntityJob job = new CoalesceSaveEntityJob(parameters);
        job.setHandler(handler);
        job.setExecutor(new CoalesceFramework());
        job.setTarget(target);
        job.call();

        // Verify File
        Assert.assertTrue(Files.exists(Paths.get("src",
                                                 "test",
                                                 "resources",
                                                 CoalesceSaveEntityJob.class.getName(),
                                                 entity.getKey().substring(0, 2),
                                                 entity.getKey())));
    }

    /**
     * Uses the framework to test the creation of an error file indicating there
     * was an issue creating an entity.
     * 
     * @throws Exception
     */
    @Test
    public void testFrameworkFailure() throws Exception
    {
        MockPersister authoritative = new MockPersister();

        MockPersister secondary = new MockPersister();
        secondary.setThrowException(true);

        // Configure the error handler
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "src/test/resources");
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "2");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);

        // Create the Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1", null, null);

        Path file = Paths.get("src",
                              "test",
                              "resources",
                              CoalesceSaveEntityJob.class.getName(),
                              entity.getKey().substring(0, 2),
                              entity.getKey());

        try (CoalesceFramework framework = new CoalesceFramework())
        {
            // Save Entity
            framework.setAuthoritativePersistor(authoritative);
            framework.setSecondaryPersistors(secondary);
            framework.setHandler(handler);
            framework.saveCoalesceEntity(entity);

            // Authoritative Should Succeed
            Assert.assertFalse(Files.exists(file));

            // Allow enough time for the job to submit the tasks. (This is a
            // potential race condition)
            Thread.sleep(2);
        }

        // Secondary Should Fail
        Assert.assertTrue(Files.exists(file));

    }

    /**
     * Uses the framework to test the creation of an error file indicating there
     * was an issue creating an entity.
     * 
     * @throws Exception
     */
    @Test
    public void testFramework() throws Exception
    {
        MockPersister authoritative = new MockPersister();
        MockPersister secondary1 = new MockPersister();
        MockPersister secondary2 = new MockPersister();

        // Create the Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1", null, null);

        try (CoalesceFramework framework = new CoalesceFramework())
        {
            // Save Entity
            framework.setAuthoritativePersistor(authoritative);
            framework.setSecondaryPersistors(secondary1, secondary2);
            framework.saveCoalesceEntity(entity);

            // Allow enough time for the job to submit the tasks. (This is a
            // potential race condition)
            Thread.sleep(2);
        }

        // Verify
        Assert.assertEquals(1, secondary1.getEntity(entity.getKey()).length);
        Assert.assertEquals(1, secondary2.getEntity(entity.getKey()).length);

    }

     @Test
    public void testTemplates() throws Exception
    {
        MockPersister authoritative = new MockPersister();

        MockPersister secondary = new MockPersister();
        secondary.setThrowException(true);

        // Configure the error handler
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "src/test/resources");
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "2");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);

        // Create the Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1", null, null);

        Path file = Paths.get("src",
                              "test",
                              "resources",
                              CoalesceSaveTemplateJob.class.getName(),
                              "UN",
                              "UNIT_TEST_UNIT_TEST_1");

        try (CoalesceFramework framework = new CoalesceFramework())
        {
            // Save Entity
            framework.setAuthoritativePersistor(authoritative);
            framework.setSecondaryPersistors(secondary);
            framework.setHandler(handler);
            framework.saveCoalesceEntityTemplate(entity.createNewEntityTemplate());

            // Authoritative Should Succeed
            Assert.assertFalse(Files.exists(file));

            // Allow enough time for the job to submit the tasks. (This is a
            // potential race condition)
            Thread.sleep(2);
        }

        // Secondary Should Fail
        Assert.assertTrue(Files.exists(file));

        // Cleanup
        Files.delete(file);
    }

}
