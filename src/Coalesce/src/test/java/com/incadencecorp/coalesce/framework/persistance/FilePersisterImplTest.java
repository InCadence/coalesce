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

package com.incadencecorp.coalesce.framework.persistance;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.unity.common.SettingType;
import com.incadencecorp.unity.common.connectors.MemoryConnector;

/**
 * These test verify the file persister implementation.
 * 
 * @author n78554
 */
public class FilePersisterImplTest extends AbstractCoalescePersistorTest<FilePersistorImpl> {

    private static final Path DB = Paths.get("src", "test", "resources", "db");

    /**
     * Ensures the directory used by these tests exists.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        Files.createDirectories(DB);
    }

    /**
     * Deletes the directory used by these tests.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void cleanup() throws Exception
    {
        FileUtils.deleteDirectory(new File(DB.toString()));
    }

    /**
     * This test verifies basic CRUD operations.
     * 
     * @throws Exception
     */
    @Test
    public void testCRUD() throws Exception
    {
        MemoryConnector connector = new MemoryConnector();
        connector.setSetting("test", CoalesceParameters.PARAM_DIRECTORY, DB.toUri().toString(), SettingType.ST_STRING);
        connector.setSetting("test", CoalesceParameters.PARAM_SUBDIR_LEN, "2", SettingType.ST_INTEGER);

        // You can also pass in a map of properties instead of using a property
        // loader.
        FilePersistorImpl persister = new FilePersistorImpl();
        persister.setPropertyLoader(new PropertyLoader(connector, "test"));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertNull(persister.getEntity(entity.getKey())[0]);

        persister.saveEntity(false, entity);

        // Verify Creation
        Assert.assertTrue(Files.exists(DB.resolve(entity.getKey().substring(0, 2))));
        Assert.assertTrue(Files.exists(DB.resolve(entity.getKey().substring(0, 2)).resolve(entity.getKey())));
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, persister.getEntity(entity.getKey())[0].getStatus());

        // Test Deleting
        entity.markAsDeleted();

        persister.saveEntity(false, entity);

        // Verify Update (Marked as Deleted)
        Assert.assertTrue(Files.exists(DB.resolve(entity.getKey().substring(0, 2))));
        Assert.assertTrue(Files.exists(DB.resolve(entity.getKey().substring(0, 2)).resolve(entity.getKey())));
        Assert.assertEquals(ECoalesceObjectStatus.DELETED, persister.getEntity(entity.getKey())[0].getStatus());

        persister.saveEntity(true, entity);

        // Verify Deletion
        Assert.assertFalse(Files.exists(DB.resolve(entity.getKey().substring(0, 2))));
        Assert.assertFalse(Files.exists(DB.resolve(entity.getKey().substring(0, 2)).resolve(entity.getKey())));
    }

    @Override
    protected FilePersistorImpl createPersister() throws Exception
    {
        MemoryConnector connector = new MemoryConnector();
        connector.setSetting("test", CoalesceParameters.PARAM_DIRECTORY, DB.toUri().toString(), SettingType.ST_STRING);
        connector.setSetting("test", CoalesceParameters.PARAM_SUBDIR_LEN, "2", SettingType.ST_INTEGER);

        // You can also pass in a map of properties instead of using a property
        // loader.
        FilePersistorImpl persister = new FilePersistorImpl();
        persister.setPropertyLoader(new PropertyLoader(connector, "test"));

        return persister;
    }

}
