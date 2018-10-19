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

package com.incadencecorp.coalesce.synchronizer.service.tests;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.persistance.AbstractFileHandlerTests;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.service.operations.CopyOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.operations.ExceptionOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.scanners.FileScanImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * These test exercise the file scanner implementation.
 *
 * @author n78554
 */
public class FileScanImplTest extends AbstractFileHandlerTests {

    private static final String FILE1 = UUID.randomUUID().toString();
    private static final String FILE2 = UUID.randomUUID().toString();
    private static final int SUB_DIR_LEN = 2;

    private static final Path ROOT = Paths.get("src", "test", "resources");
    private static final Path FILE1_DIR = ROOT.resolve(FILE1.substring(0, SUB_DIR_LEN));
    private static final Path FILE2_DIR = ROOT.resolve(FILE2.substring(0, SUB_DIR_LEN));

    /**
     * Creates files needed for these unit tests.
     */
    @BeforeClass
    public static void initialzie() throws Exception
    {
        // Create Files
        if (!Files.exists(FILE1_DIR))
        {
            Files.createDirectory(FILE1_DIR);
        }

        if (!Files.exists(FILE2_DIR))
        {
            Files.createDirectory(FILE2_DIR);
        }

        Files.createFile(FILE1_DIR.resolve(FILE1));
        Files.createFile(FILE2_DIR.resolve(FILE2));

        if (Files.exists(ROOT.resolve(CopyOperationImpl.class.getSimpleName())))
        {
            FileUtils.cleanDirectory(new File(ROOT.resolve(CopyOperationImpl.class.getSimpleName()).toString()));
            Files.deleteIfExists(ROOT.resolve(CopyOperationImpl.class.getSimpleName()));
        }

        if (Files.exists(ROOT.resolve(ExceptionOperationImpl.class.getSimpleName())))
        {
            FileUtils.cleanDirectory(new File(ROOT.resolve(ExceptionOperationImpl.class.getSimpleName()).toString()));
            Files.deleteIfExists(ROOT.resolve(ExceptionOperationImpl.class.getSimpleName()));
        }
    }

    /**
     * Ensures the scanner picks up the files created in initialization.
     */
    @Test
    public void testFileScan() throws Exception
    {
        // Create Params
        Map<String, String> params = new HashMap<>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "/src/test/resources");

        // Test Scan
        FileScanImpl scanner = new FileScanImpl();
        scanner.setProperties(params);
        CachedRowSet results = scanner.scan();

        Assert.assertEquals(2, results.size());

        List<String> keys = new ArrayList<>();

        results.first();

        do
        {
            keys.add(results.getString(CoalescePropertyFactory.getEntityKey().getPropertyName()));
        }
        while (results.next());

        Assert.assertTrue(keys.contains(FILE1));
        Assert.assertTrue(keys.contains(FILE2));

        Assert.assertTrue(Files.exists(FILE1_DIR));
        Assert.assertTrue(Files.exists(FILE2_DIR));

        Assert.assertTrue(Files.exists(FILE1_DIR.resolve(FILE1)));
        Assert.assertTrue(Files.exists(FILE2_DIR.resolve(FILE2)));

        // This scanner should clean up its files.
        scanner.finished(true, results);

        Assert.assertFalse(Files.exists(FILE1_DIR));
        Assert.assertFalse(Files.exists(FILE2_DIR));

        Assert.assertFalse(Files.exists(FILE1_DIR.resolve(FILE1)));
        Assert.assertFalse(Files.exists(FILE2_DIR.resolve(FILE2)));

    }

}
