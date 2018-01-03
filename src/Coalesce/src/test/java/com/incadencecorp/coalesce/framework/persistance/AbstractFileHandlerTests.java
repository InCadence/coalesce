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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.incadencecorp.coalesce.common.helpers.GUIDHelper;

/**
 * Any class that uses directories to store entities should extend this class to
 * ensure proper cleanup.
 * 
 * @author n78554
 */
public class AbstractFileHandlerTests {

    private final static Set<Path> roots = new HashSet<Path>();

    /**
     * Configures the handler to use the resource folder as the default directory
     */
    public AbstractFileHandlerTests()
    {
        roots.add(Paths.get("src", "test", "resources"));
    }

    /**
     * Adds a path that should be cleaned up at the end of the unit test.
     * 
     * @param path
     */
    public void addPath(Path path)
    {
        roots.add(path);
    }

    /**
     * Cleans up directories specified by {@link #addPath(Path)}.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static final void setup() throws Exception
    {
        cleanup();
    }

    /**
     * Cleans up directories specified by {@link #addPath(Path)}.
     * 
     * @throws Exception
     */
    @AfterClass
    public static final void teardown() throws Exception
    {
        cleanup();
    }

    /**
     * Cleans up directories specified by {@link #addPath(Path)}.
     * 
     * @throws Exception
     */
    public static final void cleanup() throws Exception
    {
        for (final Path root : roots)
        {

            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    String filename = file.getFileName().toString();

                    if (GUIDHelper.isValid(filename))
                    {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    File file = new File(dir.toString());
                    if (file.list().length == 0 && !Files.isSameFile(dir, root))
                    {
                        Files.delete(dir);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
