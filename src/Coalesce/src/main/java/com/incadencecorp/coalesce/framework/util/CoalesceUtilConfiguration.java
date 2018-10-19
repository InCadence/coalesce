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

package com.incadencecorp.coalesce.framework.util;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class provides access to Coalesce utility classes as an instance to be
 * used within blueprints for initializing them.
 *
 * @author Derek Clemenzi
 */
public final class CoalesceUtilConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceUtilConfiguration.class);

    /**
     * @param value
     * @see CoalesceNotifierUtil#setNotifier(ICoalesceNotifier)
     */
    public final void setNotifier(final ICoalesceNotifier value)
    {
        CoalesceNotifierUtil.setNotifier(value);
    }

    /**
     * @param entities
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     */
    public static void setTemplates(CoalesceEntity... entities)
    {
        for (CoalesceEntity entity : entities)
        {
            try
            {
                CoalesceTemplateUtil.addTemplates(CoalesceEntityTemplate.create(entity));
            }
            catch (CoalesceException e)
            {
                LOGGER.error("(FAILED) Processing Template ({}) ({})", entity.getName(), entity.getSource(), e);
            }
        }
    }

    /**
     * @param persistors
     * @see CoalesceTemplateUtil#addTemplates(ICoalescePersistor)
     */
    public static void setTemplates(ICoalescePersistor... persistors)
    {
        for (ICoalescePersistor persistor : persistors)
        {
            try
            {
                CoalesceTemplateUtil.addTemplates(persistor);
            }
            catch (CoalescePersistorException e)
            {
                LOGGER.error("(FAILED) Processing Templates from ({})", persistor.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * @param values
     * @see EnumerationProviderUtil#setEnumerationProviders(IEnumerationProvider...)
     */
    public static void setEnumerationProviders(IEnumerationProvider... values)
    {
        EnumerationProviderUtil.setEnumerationProviders(values);
    }

    /**
     * @param values
     * @see EnumerationProviderUtil#setLookupEntries(Map)
     */
    public static void setLookupEntries(Map<String, String> values)
    {
        EnumerationProviderUtil.setLookupEntries(values);
    }

    /**
     * Initializes the framework with persistor configuration.
     * Do NOT use this within an OSGi container.
     *
     * @param framework     to be initialized
     * @param configuration property file containing a list of persistors to use.
     */
    public static void initializeFramework(CoalesceFramework framework, Path configuration) throws IOException
    {
        File file = new File(configuration.toString());

        if (file.exists())
        {
            LineIterator iterator = FileUtils.lineIterator(file, "UTF-8");

            try
            {
                List<ICoalescePersistor> secondaryPersisters = new ArrayList<>();
                while (iterator.hasNext())
                {
                    String persisterClassName = iterator.nextLine();
                    try
                    {
                        if (!persisterClassName.startsWith("#"))
                        {
                            Object persister = Thread.currentThread().getContextClassLoader().loadClass(persisterClassName).newInstance();
                            //Object persister = ClassLoader.getSystemClassLoader().loadClass(persisterClassName).newInstance();

                            if (persister instanceof ICoalescePersistor)
                            {
                                if (!framework.isInitialized())
                                {
                                    framework.setAuthoritativePersistor((ICoalescePersistor) persister);
                                }
                                else
                                {
                                    secondaryPersisters.add((ICoalescePersistor) persister);
                                }
                            }
                            else
                            {
                                LOGGER.debug("(FAILED) Loading Persister {} Invalid Implementation", persisterClassName);
                            }
                        }
                    }
                    catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
                    {
                        LOGGER.error("(FAILED) Loading Persister ({}) : {}", persisterClassName, e);
                    }
                }

                framework.setSecondaryPersistors(secondaryPersisters.toArray(new ICoalescePersistor[secondaryPersisters.size()]));
            }
            finally
            {
                iterator.close();
            }

        }
        else
        {
            throw new IOException(String.format(CoalesceErrors.NOT_FOUND, "File", configuration));
        }
    }

}
