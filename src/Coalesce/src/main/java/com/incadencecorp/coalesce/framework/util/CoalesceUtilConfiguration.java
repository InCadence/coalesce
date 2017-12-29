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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

/**
 * This class provides access to Coalesce utility classes as an instance to be
 * used within blueprints for initializing them.
 * 
 * @author Derek Clemenzi
 */
public final class CoalesceUtilConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceUtilConfiguration.class);

    /**
     * @see CoalesceNotifierUtil#setNotifier(ICoalesceNotifier)
     * @param value
     */
    public final void setNotifier(final ICoalesceNotifier value)
    {
        CoalesceNotifierUtil.setNotifier(value);
    }

    /**
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     * @param entities
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
     * @see CoalesceTemplateUtil#addTemplates(ICoalescePersistor)
     * @param persistors
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
     * @see EnumerationProviderUtil#setEnumerationProviders(IEnumerationProvider...)
     * @param values
     */
    public void setEnumerationProviders(IEnumerationProvider... values)
    {
        EnumerationProviderUtil.setEnumerationProviders(values);
    }

    /**
     * @see EnumerationProviderUtil#setLookupEntries(Map)
     * @param values
     */
    public static void setLookupEntries(Map<String, String> values)
    {
        EnumerationProviderUtil.setLookupEntries(values);
    }

}
