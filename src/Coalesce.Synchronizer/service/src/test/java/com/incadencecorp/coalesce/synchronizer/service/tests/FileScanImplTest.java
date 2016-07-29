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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.rowset.CachedRowSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.framework.persistance.AbstractFileHandlerTests;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.service.scanners.FileScanImpl;

/**
 * These test exercise the file scanner implementation.
 * 
 * @author n78554
 *
 */
public class FileScanImplTest extends AbstractFileHandlerTests {

    private static final String FILE1 = UUID.randomUUID().toString();
    private static final String FILE2 = UUID.randomUUID().toString();
    private static final int SUB_DIR_LEN = 2;

    /**
     * Creates files needed for these unit tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void initialzie() throws Exception
    {
        // Create Files
        Path path1 = Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN), FILE1);
        Path path2 = Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN), FILE2);

        Files.createDirectory(Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN)));
        Files.createDirectory(Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN)));

        Files.createFile(path1);
        Files.createFile(path2);
    }

    /**
     * Ensures the scanner picks up the files created in initialization.
     * 
     * @throws Exception
     */
    @Test
    public void testFileScan() throws Exception
    {
        // Create Params
        Map<String, String> params = new HashMap<String, String>();
        params.put(CoalesceParameters.PARAM_DIRECTORY, "/src/test/resources");

        // Test Scan
        FileScanImpl scanner = new FileScanImpl();
        scanner.setProperties(params);
        CachedRowSet results = scanner.scan();

        Assert.assertEquals(2, results.size());

        List<String> keys = new ArrayList<String>();

        results.first();

        do
        {
            keys.add(results.getString(CoalescePropertyFactory.getEntityKey().getPropertyName()));
        }
        while (results.next());

        Assert.assertTrue(keys.contains(FILE1));
        Assert.assertTrue(keys.contains(FILE2));

        Assert.assertTrue(Files.exists(Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN))));
        Assert.assertTrue(Files.exists(Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN))));

        Assert.assertTrue(Files.exists(Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN), FILE1)));
        Assert.assertTrue(Files.exists(Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN), FILE2)));

        // This scanner should clean up its files.
        scanner.finished(true, results);

        Assert.assertFalse(Files.exists(Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN))));
        Assert.assertFalse(Files.exists(Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN))));

        Assert.assertFalse(Files.exists(Paths.get("src", "test", "resources", FILE1.substring(0, SUB_DIR_LEN), FILE1)));
        Assert.assertFalse(Files.exists(Paths.get("src", "test", "resources", FILE2.substring(0, SUB_DIR_LEN), FILE2)));

    }

}
