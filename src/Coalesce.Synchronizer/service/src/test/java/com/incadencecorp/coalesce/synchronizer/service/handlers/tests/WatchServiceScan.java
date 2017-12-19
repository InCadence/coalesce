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

package com.incadencecorp.coalesce.synchronizer.service.handlers.tests;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.geotools.data.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;

public class WatchServiceScan extends AbstractScan {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchServiceScan.class);

    private URI directory;
    private WatchService service;

    @Override
    public void doSetup()
    {
        Path path = Paths.get(directory);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try
        {
            service = fs.newWatchService();

            // Register directory and sub-directories
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Adding Watch On {}", dir.toString());
                    }

                    dir.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to Start", e);
        }
    }

    public void cleanup()
    {
        try
        {
            service.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to Stop Watch Service", e);
        }
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        super.setProperties(properties);

        if (parameters.containsKey(CoalesceParameters.PARAM_DIRECTORY))
        {
            try
            {
                directory = FileHelper.getFullPath(parameters.get(CoalesceParameters.PARAM_DIRECTORY));

                if (!Files.exists(Paths.get(directory)))
                {
                    throw new IllegalArgumentException("Invalid Directory: " + directory);
                }
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(CoalesceParameters.PARAM_DIRECTORY, e);
            }
        }
    }

    @Override
    public CachedRowSet scan() throws CoalesceException
    {
        return scan(null);
    }

    @Override
    protected CachedRowSet doScan(Query query) throws CoalesceException
    {
        CachedRowSet results;

        try
        {
            String columns[] = new String[] {
                    "objectkey", "fullpath"
            };

            final Map<String, String> keys = new HashMap<String, String>();

            WatchKey key = service.take();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Polling Events");
            }

            // Dequeueing events
            Kind<?> kind = null;
            for (WatchEvent<?> watchEvent : key.pollEvents())
            {
                // Get the type of the event
                kind = watchEvent.kind();

                if (ENTRY_CREATE == kind)
                {
                    // A new Path was created
                    Path newPath = Paths.get(directory.resolve(((WatchEvent<Path>) watchEvent).context().toString()));

                    // Directory?
                    if (Files.isDirectory(newPath))
                    {
                        // Yes; Create Watch
                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("Registering Directory {}", newPath);
                        }

                        // Register Directory
                        newPath.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

                        // Process Content
                        Files.walkFileTree(newPath, new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                            {
                                if (LOGGER.isDebugEnabled())
                                {
                                    LOGGER.debug("Adding Entity {}", file);
                                }

                                keys.put(file.getFileName().toString(), file.toString());
                                return FileVisitResult.CONTINUE;
                            }
                        });

                    }
                    else
                    {
                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("Adding Entity {}", newPath);
                        }

                        keys.put(newPath.getFileName().toString(), newPath.toString());
                    }
                }
                else if (ENTRY_MODIFY == kind)
                {
                    // modified
                    Path newPath = Paths.get(directory.resolve(((WatchEvent<Path>) watchEvent).context().toString()));

                    if (!Files.isDirectory(newPath))
                    {
                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("Adding Entity {}", newPath);
                        }

                        keys.put(newPath.getFileName().toString(), newPath.toString());
                    }
                }
            }

            key.reset();

            List<Object[]> rows = new ArrayList<Object[]>();

            for (Map.Entry<String, String> entry : keys.entrySet())
            {
                rows.add(new Object[] {
                        entry.getKey(), entry.getValue()
                });
            }

            results = RowSetProvider.newFactory().createCachedRowSet();
            results.populate(new CoalesceResultSet(rows.iterator(), columns));
        }
        catch (InterruptedException | IOException | SQLException e)
        {
            throw new CoalesceException("Watch Service Interrupted", e);
        }

        return results;
    }
}
