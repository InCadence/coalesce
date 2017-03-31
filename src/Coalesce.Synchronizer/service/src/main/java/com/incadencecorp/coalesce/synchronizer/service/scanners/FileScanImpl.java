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

package com.incadencecorp.coalesce.synchronizer.service.scanners;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;

/**
 * This implementation checks a directory for files that have been created with
 * their names being keys of entities. You can pair this with the
 * FileHandlerImpl.
 * 
 * @author n78554
 * @see CoalesceParameters#PARAM_DIRECTORY
 */
public class FileScanImpl extends AbstractScan {

    private static final String COLUMN_PATH = "fullpath";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanImpl.class);

    private long lastScanned;
    private URI directory;
    private int blockSize = -1; // Unlimited

    @Override
    public void setProperties(Map<String, String> properties)
    {
        super.setProperties(properties);

        // Last Successful Scan Configured?
        if (parameters.containsKey(CoalesceParameters.PARAM_DIRECTORY))
        {
            try
            {
                directory = FileHelper.getFullPath(parameters.get(CoalesceParameters.PARAM_DIRECTORY));
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(CoalesceParameters.PARAM_DIRECTORY, e);
            }
        }

        if (parameters.containsKey(CoalesceParameters.PARAM_BLOCK_SIZE))
        {
            blockSize = Integer.parseInt(parameters.get(CoalesceParameters.PARAM_BLOCK_SIZE));
        }

    }

    @Override
    public CachedRowSet scan() throws CoalesceException
    {
        return scan(new Query());
    }

    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {
        CachedRowSet results;

        lastScanned = JodaDateTimeHelper.nowInUtc().getMillis();

        final List<Object[]> rows = new ArrayList<Object[]>();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Scanning Directory: {}", directory);
        }

        try
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("File(s) Located:");
            }

            Files.walkFileTree(Paths.get(directory), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    String filename = file.getFileName().toString();

                    if (GUIDHelper.isValid(filename))
                    {
                        if (LOGGER.isTraceEnabled())
                        {
                            LOGGER.trace("\t{}", filename);
                        }

                        rows.add(new Object[] {
                                filename, file.toString()
                        });
                    }
                    else if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("\t(INVALID) {}", filename);
                    }

                    FileVisitResult result;

                    if (blockSize == -1 || rows.size() < blockSize)
                    {
                        result = FileVisitResult.CONTINUE;
                    }
                    else
                    {
                        if (LOGGER.isTraceEnabled())
                        {
                            LOGGER.trace("Max Results {} Reached", blockSize);
                        }

                        result = FileVisitResult.TERMINATE;
                    }

                    return result;
                }
            });
        }
        catch (IOException e)
        {
            throw new CoalesceException("Failed to read directory", e);
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Total Rows: {}", rows.size());
        }

        List<String> columns = new ArrayList<String>();
        columns.add(CoalescePropertyFactory.getEntityKey().getPropertyName());
        columns.add(COLUMN_PATH);

        try
        {
            results = RowSetProvider.newFactory().createCachedRowSet();
            results.populate(new CoalesceResultSet(rows.iterator(), columns.toArray(new String[columns.size()])));
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Scan Failed", e);
        }

        return results;
    }

    @Override
    public void finished(boolean successful, CachedRowSet rows)
    {
        if (successful)
        {
            try
            {
                if (rows.first())
                {
                    do
                    {
                        String fullPath = rows.getString(COLUMN_PATH);

                        File file = new File(fullPath);
                        if (lastScanned > file.lastModified())
                        {
                            if (LOGGER.isDebugEnabled())
                            {
                                LOGGER.debug("Deleting {}", fullPath);
                            }
                            file.delete();
                        }
                        else
                        {
                            if (LOGGER.isDebugEnabled())
                            {
                                LOGGER.debug("{} was modified since scan.", fullPath);
                            }
                        }
                    }
                    while (rows.next());
                }

                final Path root = Paths.get(directory);

                // Cleanup Empty Folders
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                    {
                        File file = new File(dir.toString());
                        if (file.list().length == 0 && !Files.isSameFile(dir, root))
                        {
                            if (LOGGER.isDebugEnabled())
                            {
                                LOGGER.debug("Deleting Empty Folder {}", dir);
                            }

                            Files.delete(dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

            }
            catch (SQLException | IOException e)
            {
                LOGGER.error("Failed to finish scan", e);
            }
        }
    }

}
