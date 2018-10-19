package com.incadencecorp.coalesce.framework;

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
        assertEquals("coalesce.properties", CoalesceSettings.getConfigurationFileName());

        getDefaultApplicationRootNotSetTest();

        setDefaultApplicationRootSetTest();

        CoalesceUnitTestSettings.initialize();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException
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
        assertEquals("Coalesce.UnitTest.coalesce.properties", CoalesceSettings.getConfigurationFileName());
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

        CoalesceUnitTestSettings.setSubDirectoryLength(-1);
        assertEquals(0, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(-10);
        assertEquals(0, CoalesceSettings.getSubDirectoryLength());

        CoalesceUnitTestSettings.setSubDirectoryLength(2);
        assertEquals(2, CoalesceSettings.getSubDirectoryLength());

    }

    @Test
    public void binaryFileStoreBasePathTest()
    {
        assertEquals(FilenameUtils.concat(CoalesceSettings.getDefaultApplicationRoot(), "files"),
                     CoalesceSettings.getBinaryFileStoreBasePath());

        CoalesceSettings.setBinaryFileStoreBasePath("C:\\UnitTesting.Coalesce.Testing.config");

        assertEquals("C:\\UnitTesting.Coalesce.Testing.config", CoalesceSettings.getBinaryFileStoreBasePath());

        CoalesceSettings.setBinaryFileStoreBasePath(FilenameUtils.concat(CoalesceSettings.getDefaultApplicationRoot(),
                                                                         "files"));
        
    }

    @Test
    public void useEncryption()
    {
        assertFalse(CoalesceSettings.getUseEncryption());
        
        CoalesceSettings.setUseEncryption(true);
        
        assertTrue(CoalesceSettings.getUseEncryption());
        
        CoalesceSettings.setUseEncryption(false);
        
        assertFalse(CoalesceSettings.getUseEncryption());
        
    }
    
    @Test
    public void passPhraseTest()
    {
        assertEquals("9UFAF8FI98BDLQEZ", CoalesceSettings.getPassPhrase());
        
        CoalesceSettings.setPassPhrase("ABCDEFG12345");
        
        assertEquals("ABCDEFG12345", CoalesceSettings.getPassPhrase());
        
        CoalesceSettings.setPassPhrase("9UFAF8FI98BDLQEZ");
        
    }
    
    @Test
    public void auditSelectStatementsTest()
    {
        assertTrue(CoalesceSettings.getAuditSelectStatements());
        
        CoalesceSettings.setAuditSelectStatements(false);
        
        assertFalse(CoalesceSettings.getAuditSelectStatements());
        
        CoalesceSettings.setAuditSelectStatements(true);
        
        assertTrue(CoalesceSettings.getAuditSelectStatements());
        
    }

    @Test
    public void imageFormatTest()
    {
        assertEquals("jpg", CoalesceSettings.getImageFormat());
        
        CoalesceSettings.setImageFormat("raw");
        
        assertEquals("raw", CoalesceSettings.getImageFormat());
        
        CoalesceSettings.setImageFormat(".GiF");
        
        assertEquals(".GiF", CoalesceSettings.getImageFormat());
        
        CoalesceSettings.setImageFormat("jpg");
        
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
