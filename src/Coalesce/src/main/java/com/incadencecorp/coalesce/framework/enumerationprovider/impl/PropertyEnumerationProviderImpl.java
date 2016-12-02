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

package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;

/**
 * This implementation will load the values of the enumeration from a property
 * file with the following format "<enumeration name>.values".
 * 
 * @author n78554
 */
public class PropertyEnumerationProviderImpl extends AbstractEnumerationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyEnumerationProviderImpl.class);
    private List<String> paths = new ArrayList<String>();

    /**
     * Default Constructor; defaults to src/main/resources
     */
    public PropertyEnumerationProviderImpl()
    {
        this(Paths.get("src", "main", "resources").toString());
    }
    
    /**
     * Constructs provider with paths.
     * 
     * @param paths
     */
    public PropertyEnumerationProviderImpl(String... paths)
    {
        setPaths(paths);
    }

    /**
     * Sets the directory this implementation will attempt to find property
     * files.
     * 
     * @param paths
     */
    public void setPaths(String... paths)
    {
        this.paths.addAll(Arrays.asList(paths));
    }

    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        List<String> results = null;

        // Attempt File System
        for (String path : paths)
        {
            if (!path.endsWith(File.separator))
            {
                path += File.separator;
            }

            File file = new File(path + enumeration + ".values");

            if (file.exists())
            {
                LineIterator iterator;
                try
                {
                    iterator = FileUtils.lineIterator(file, "UTF-8");

                    try
                    {
                        // Read Keys as Values
                        results = new ArrayList<String>();

                        while (iterator.hasNext())
                        {

                            results.add(iterator.nextLine());

                        }
                    }
                    finally
                    {
                        iterator.close();
                    }
                }
                catch (IOException e)
                {
                    LOGGER.warn(String.format(CoalesceErrors.INVALID_ENUMERATION, enumeration), e);
                }

            }
        }

        return results;
    }

}
