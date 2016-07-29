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

package com.incadencecorp.coalesce.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;

/**
 * This implementation creates a file with the name as the entity's key that
 * caused the error. The content of the file is the full stack trace. If the
 * error is successfully written to a file it will report that the error was
 * handled and allows the operation to continue.
 * 
 * @author n78554
 * @see CoalesceParameters#PARAM_DIRECTORY
 * @see CoalesceParameters#PARAM_SUBDIR_LEN
 */
public class FileExceptionHandlerImpl extends CoalesceComponentImpl implements IExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileExceptionHandlerImpl.class);

    private URI directory;
    private int subDirLen = 0;

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        // Last Successful Scan Configured?
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

        if (parameters.containsKey(CoalesceParameters.PARAM_SUBDIR_LEN))
        {
            subDirLen = Integer.parseInt(parameters.get(CoalesceParameters.PARAM_SUBDIR_LEN));

            if (subDirLen < 0)
            {
                throw new IllegalArgumentException("Invalid Sub Directory Length: " + subDirLen);
            }
        }

    }

    @Override
    public boolean handle(String[] keys, ICoalesceComponent caller, Exception e) throws CoalesceException
    {
        boolean logged = true;

        if (directory == null)
        {
            throw new CoalesceException("Path not configured", e);
        }

        for (String key : keys)
        {

            Path dir = Paths.get(directory.getPath(), caller.getName(), key.substring(0, subDirLen));
            Path file = dir.resolve(key); 

            try
            {
                if (!Files.exists(dir))
                {
                    Files.createDirectories(dir);
                }

                try (FileWriter fw = new FileWriter(file.toString(), true))
                {
                    e.printStackTrace(new PrintWriter(fw));

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Created {}", file.toString());
                    }
                }

            }
            catch (IOException e1)
            {
                // Log Error
                LOGGER.error("Logging Failed", e1);

                // Let Caller Handle Error
                logged = false;

                // Exit For
                break;
            }
        }

        return logged;
    }

}
