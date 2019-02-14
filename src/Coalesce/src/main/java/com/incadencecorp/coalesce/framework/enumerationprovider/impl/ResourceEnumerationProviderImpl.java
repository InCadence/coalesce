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

package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation will load the values of the enumeration from resources
 * with the following format "&lt;enumeration name&gt;.values".
 *
 * @author n78554
 */
public class ResourceEnumerationProviderImpl extends AbstractEnumerationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceEnumerationProviderImpl.class);

    private Class<?> context;

    /**
     * Default Constructor
     */
    public ResourceEnumerationProviderImpl()
    {
        context = ResourceEnumerationProviderImpl.class;
    }

    /**
     * Default Constructor
     */
    public ResourceEnumerationProviderImpl(Class<?> context)
    {
        this.context = context;
    }

    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        List<String> results = new ArrayList<>();

        try
        {
            InputStream stream = null;

            // Is OSGI Environment?
            Bundle bundle;

            try
            {
                bundle = FrameworkUtil.getBundle(context);
            }
            catch (NoClassDefFoundError e)
            {
                bundle = null;
            }

            if (bundle != null)
            {
                LOGGER.trace("Loading enumeration ({}) from OSGi", enumeration);
                URL url = bundle.getEntry("/" + enumeration + ".values");

                if (url != null)
                {
                    stream = url.openStream();
                }
            }
            else
            {
                LOGGER.trace("Loading enumeration ({}) from classpath", enumeration);
                stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(enumeration + ".values");
            }

            if (stream != null)
            {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
                {
                    String line;
                    while ((line = in.readLine()) != null)
                    {
                        results.add(line);
                    }
                    LOGGER.trace("Enumeration ({}) loaded", enumeration);
                }
                finally
                {
                    stream.close();
                }
            }
        }
        catch (IOException e)
        {
            results = null;
            LOGGER.debug(CoalesceErrors.NOT_FOUND, "Enumeration", enumeration, e);
        }

        return results;
    }

}
