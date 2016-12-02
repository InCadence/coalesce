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

package com.incadencecorp.coalesce.handlers.tests;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.persistance.AbstractFileHandlerTests;
import com.incadencecorp.coalesce.handlers.FileExceptionHandlerImpl;

/**
 * These unit tests test {@link FileExceptionHandlerImpl}.
 * 
 * @author n78554
 */
public class FileExceptionHandlerImplTest extends AbstractFileHandlerTests {

    private static final int SUB_DIR_LEN = 2;

    /**
     * This test ensures files are created when handling the exception.
     * 
     * @throws Exception
     */
    @Test
    public void testFileHandler() throws Exception
    {
        String key1 = UUID.randomUUID().toString();
        String key2 = UUID.randomUUID().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, Paths.get("src", "test", "resources").toUri().toString());
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, Integer.toString(SUB_DIR_LEN));

        String keys[] = new String[] {
                key1, key2
        };

        CoalesceComponentImpl caller = new CoalesceComponentImpl();
        caller.setName("test");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);
        Assert.assertTrue(handler.handle(keys, caller, new CoalesceException("Hello World")));

        // Verify the files were created
        Assert.assertTrue(Files.exists(Paths.get("src",
                                                 "test",
                                                 "resources",
                                                 caller.getName(),
                                                 key1.substring(0, SUB_DIR_LEN),
                                                 key1)));
        Assert.assertTrue(Files.exists(Paths.get("src",
                                                 "test",
                                                 "resources",
                                                 caller.getName(),
                                                 key2.substring(0, SUB_DIR_LEN),
                                                 key2)));

    }

    /**
     * This test ensures an exception is thrown if the directory was not set.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testFileHandlerFailure() throws Exception
    {
        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.handle(new String[] {
            UUID.randomUUID().toString()
        }, new CoalesceComponentImpl(), new CoalesceException("Hello World"));
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeSubDir() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "-1");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumberSubDir() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "A");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDirectorySchema() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "file:/HelloWorld");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDirectory() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "unknown");

        FileExceptionHandlerImpl handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);
    }

}
