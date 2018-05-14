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

package com.incadencecorp.coalesce.synchronizer.service.operations;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * This implementation writes the keys of entities that need to be updated to a
 * file system.
 *
 * @author n78554
 */
public class RecordOperationImpl extends AbstractOperation<AbstractOperationTask> {

    private Path directory;
    private int subDirLen = 0;

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                for (String key : keys)
                {
                    Path dir = directory.resolve(getName()).resolve(key.substring(0, subDirLen));
                    Path file = dir.resolve(key);

                    try
                    {
                        if (!Files.exists(dir))
                        {
                            Files.createDirectories(dir);
                        }

                        if (!Files.exists(file))
                        {
                            Files.createFile(file);
                        }
                    }
                    catch (IOException e)
                    {
                        throw new CoalescePersistorException("Failed to Record Entity", e);
                    }
                }

                return true;
            }

        };
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        // Last Successful Scan Configured?
        if (parameters.containsKey(CoalesceParameters.PARAM_DIRECTORY))
        {
            try
            {
                directory = Paths.get(FileHelper.getFullPath(parameters.get(CoalesceParameters.PARAM_DIRECTORY)));

                if (!Files.exists(directory))
                {
                    throw new IllegalArgumentException("Invalid Directory: " + directory);
                }
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(CoalesceParameters.PARAM_DIRECTORY, e);
            }
        }

        if (parameters.containsKey(CoalesceParameters.PARAM_SUBDIR_LEN))
        {
            subDirLen = Integer.parseInt(parameters.get(CoalesceParameters.PARAM_SUBDIR_LEN));

            if (subDirLen < 0)
            {
                throw new IllegalArgumentException("Invalid Sub Directory Length: " + subDirLen);
            }
        }

    }
}
