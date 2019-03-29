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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This implementation checks a directory for files that have been created with
 * their names being keys of entities. If required columns are specified then the
 * source is used to retrieve the metadata. You can pair this with the FileHandlerImpl.
 *
 * @author n78554
 * @see CoalesceParameters#PARAM_DIRECTORY
 * @see SynchronizerParameters#PARAM_SCANNER_MAX
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

        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_MAX))
        {
            blockSize = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_SCANNER_MAX));
        }

    }

    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {

        lastScanned = JodaDateTimeHelper.nowInUtc().getMillis();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Scanning Directory: {}", directory);
        }

        final Map<String, String> keysToProcess = new HashMap<>();

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
                    Path filenamePath = file.getFileName();

                    if (filenamePath != null)
                    {
                        String filename = filenamePath.toString();

                        if (GUIDHelper.isValid(filename))
                        {
                            if (LOGGER.isTraceEnabled())
                            {
                                LOGGER.trace("\t{}", filename);
                            }

                            keysToProcess.put(filename, file.toString());
                        }
                        else if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("\t(INVALID) {}", filename);
                        }
                    }

                    FileVisitResult result;

                    if (blockSize == -1 || keysToProcess.size() < blockSize)
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
            LOGGER.trace("Total Rows: {}", keysToProcess.size());
        }

        return buildRowSet(query, keysToProcess);
    }

    private CachedRowSet buildRowSet(Query query, Map<String, String> keysToProcess) throws CoalesceException
    {
        CachedRowSet results;

        List<String> columns = new ArrayList<>();
        columns.add(CoalescePropertyFactory.getEntityKey().getPropertyName());

        List<Object[]> rows;

        if (query.getProperties().isEmpty())
        {
            rows = buildRows(keysToProcess);
        }
        else
        {
            columns.addAll(Arrays.asList(query.getPropertyNames()));
            rows = buildRows(query, keysToProcess);
        }

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

    private List<Object[]> buildRows(Map<String, String> keysToProcess)
    {
        List<Object[]> rows = new ArrayList<>();

        for (Map.Entry<String, String> entry : keysToProcess.entrySet())
        {
            rows.add(new Object[] { entry.getKey(), entry.getValue()
            });
        }

        return rows;
    }

    private List<Object[]> buildRows(Query query, Map<String, String> keysToProcess) throws CoalescePersistorException
    {
        if (getSource() == null)
        {
            throw new IllegalArgumentException(String.format(CoalesceErrors.NOT_INITIALIZED,
                                                             ICoalesceSearchPersistor.class.getSimpleName()));
        }

        List<Object[]> rows = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();

        for (String key : keysToProcess.keySet())
        {
            filters.add(CoalescePropertyFactory.getEntityKey(key));
        }

        query.setFilter(CoalescePropertyFactory.getFilterFactory().or(filters));

        SearchResults results = getSource().search(query);

        if (results.isSuccessful())
        {
            try (CachedRowSet rowset = results.getResults())
            {
                if (rowset.first())
                {
                    int columns = rowset.getMetaData().getColumnCount();

                    do
                    {
                        Object[] row = new Object[columns + 1];

                        for (int ii = 0; ii < columns; ii++)
                        {
                            row[ii] = rowset.getObject(ii + 1);
                        }

                        row[columns] = keysToProcess.get(rowset.getString(1));

                        rows.add(row);
                    }
                    while (rowset.next());
                }
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException(e);
            }
        }
        else
        {
            throw new CoalescePersistorException(results.getError());
        }

        return rows;
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
                            if (!file.delete())
                            {
                                LOGGER.warn("(FAILED) Deleting {}", fullPath);
                            }
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
                        String[] files = file.list();
                        if (files != null && files.length == 0 && !Files.isSameFile(dir, root))
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
