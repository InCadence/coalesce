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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This implementation will load the values of the enumeration from a property
 * file with the following format "<enumeration name>.values".
 * 
 * @author n78554
 */
public class PropertyEnumerationProviderImpl extends AbstractEnumerationProvider {

    private List<String> paths = new ArrayList<String>();

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

        try
        {
            // Load Resource
            ResourceBundle config = ResourceBundle.getBundle(enumeration);

            // Read Keys as Values
            results = new ArrayList<String>();
            results.addAll(config.keySet());
        }
        catch (MissingResourceException e)
        {
            // Failed to Load Resource
            Properties props;

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
                    try
                    {
                        // Load Property File
                        props = new Properties();
                        props.load(new FileInputStream(file));

                        // Read Keys as Values
                        results = new ArrayList<String>();
                        for (Object key : props.keySet())
                        {
                            results.add((String) key);
                        }
                    }
                    catch (IOException e1)
                    {
                        // Failed; Do Nothing
                    }
                    break;
                }
            }
        }

        return results;
    }

}
