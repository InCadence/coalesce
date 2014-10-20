package com.incadencecorp.coalesce.common.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

public class CoalesceSettingTest {

    @BeforeClass
    public static void setUpBeforeClass() throws IOException
    {
        assertEquals("Coalesce.config", CoalesceSettings.getConfigurationFileName());

        getDefaultApplicationRootNotSetTest();

        setDefaultApplicationRootSetTest();

        CoalesceUnitTestSettings.initialize();
    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    /*
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void getConfiguationFileNameTest()
    {
        assertEquals("Coalesce.UnitTest.Coalesce.config", CoalesceSettings.getConfigurationFileName());
    }

    @Test
    public void getUseBinaryFileStoreTest()
    {
        assertTrue(CoalesceSettings.getUseBinaryFileStore());

        CoalesceSettings.setUseBinaryFileStore(false);

        assertFalse(CoalesceSettings.getUseBinaryFileStore());

        CoalesceSettings.setUseBinaryFileStore(true);

    }

    @Test
    public void getUseIndexingTest()
    {
        assertTrue(CoalesceSettings.getUseIndexing());

        CoalesceSettings.setUseIndexing(false);

        assertFalse(CoalesceSettings.getUseIndexing());

        CoalesceSettings.setUseIndexing(true);

    }

    @Test
    public void getSubDirectoryLength()
    {
        assertEquals(2, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        assertEquals(5, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(6);

        assertEquals(5, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(2);

        CoalesceUnitTestSettings.setSubDirectoryLength(-1);

        assertEquals(0, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(-10);

        assertEquals(0, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(2);
        
    }

    @Test
    public void getBinaryFileStoreBasePathTest()
    {
        assertEquals(FilenameUtils.concat(CoalesceSettings.getDefaultApplicationRoot(), "..\\images\\uploads\\"),
                     CoalesceSettings.getBinaryFileStoreBasePath());
    }

    private static void getDefaultApplicationRootNotSetTest()
    {
        String root = CoalesceSettings.getDefaultApplicationRoot();

        // TODO: Uses Unit test runner as the main path which is based on the individual IDE installation location
        // Should somehow be changed to a testable assert. Verified manually that it does return bin path
        // of a standalone application.
        assertFalse(StringHelper.isNullOrEmpty(root));
    }

    private static void setDefaultApplicationRootSetTest()
    {
        CoalesceSettings.setDefaultApplicationRoot("C:\\Program Files\\Java\\jre7\\bin");

        assertEquals("C:\\Program Files\\Java\\jre7\\bin", CoalesceSettings.getDefaultApplicationRoot());
    }

}
