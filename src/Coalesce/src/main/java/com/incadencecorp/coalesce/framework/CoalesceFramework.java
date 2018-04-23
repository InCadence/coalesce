package com.incadencecorp.coalesce.framework;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.jobs.CoalesceRegisterTemplateJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityProperties;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveTemplateJob;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

/**
 * Application using Coalesce should access the persistor (database) through
 * CoalesceFramework.
 */
public class CoalesceFramework extends CoalesceExecutorServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceFramework.class);

    /*--------------------------------------------------------------------------
        Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _persistors[];
    private ICoalescePersistor _authoritativePersistor;
    private IExceptionHandler _handler;
    private boolean _isAsyncUpdates = true;

    protected ICoalescePersistor[] getSecondaryPersistors()
    {
        return _persistors;
    }

    protected boolean hasSecondaryPersistors()
    {
        return _persistors != null && _persistors.length > 0;
    }

    protected ICoalescePersistor getAuthoritativePersistor()
    {
        return _authoritativePersistor;
    }

    /**
     * @return the authoritative persistor or if it does not support {@link EPersistorCapabilities#READ} the first secondary persistor that does if available.
     */
    protected ICoalescePersistor getXmlPersistor()
    {
        ICoalescePersistor persistor = getAuthoritativePersistor();

        if (!persistor.getCapabilities().contains(EPersistorCapabilities.READ))
        {
            for (ICoalescePersistor secondary : getSecondaryPersistors())
            {
                if (secondary.getCapabilities().contains(EPersistorCapabilities.READ))
                {
                    persistor = secondary;
                    break;
                }
            }
        }

        return persistor;
    }
    /*--------------------------------------------------------------------------
        Initialization Functions
    --------------------------------------------------------------------------*/

    /**
     * Creates this framework with the default ThreadPoolExecutor based on
     * {@link CoalesceSettings}.
     */
    public CoalesceFramework()
    {
        this(null);
    }

    /**
     * Creates this framework with the provided executor service.
     *
     * @param service
     */
    public CoalesceFramework(ExecutorService service)
    {
        super(service);
    }

    /**
     * Sets the authoritative persistor which is blocking.
     *
     * @param persistor
     */
    public void setAuthoritativePersistor(ICoalescePersistor persistor)
    {
        this._authoritativePersistor = persistor;

        if (LOGGER.isInfoEnabled() && persistor != null)
        {
            LOGGER.info("Authoritative Persistor ({})", persistor.getClass().getName());
        }
    }

    /**
     * Sets the secondary persistors which are not blocking.
     *
     * @param persistors
     */
    public void setSecondaryPersistors(ICoalescePersistor... persistors)
    {
        this._persistors = persistors;

        if (LOGGER.isInfoEnabled() && persistors != null)
        {
            for (ICoalescePersistor persistor : persistors)
            {
                if (persistor != null)
                {
                    LOGGER.info("Secondary Persistor ({})", persistor.getClass().getName());
                }
            }
        }
    }

    /**
     * Sets the handler in the event of an error.
     *
     * @param handler
     */
    public void setHandler(IExceptionHandler handler)
    {
        _handler = handler;

        if (LOGGER.isInfoEnabled() && handler != null)
        {
            LOGGER.info("Error Handler ({})", handler.getName());
        }
    }

    /**
     * Sets whether updates to secondary persistors should be performed
     * asynchronously.
     *
     * @param value
     */
    public void setIsAnsyncUpdates(boolean value)
    {
        _isAsyncUpdates = value;

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Asynchronous Updates ({})", value);
        }
    }

    /**
     * Initializes the {@link CoalesceTemplateUtil} with the authoritative
     * persistor.
     */
    public void refreshCoalesceTemplateUtil()
    {
        try
        {
            CoalesceTemplateUtil.addTemplates(getAuthoritativePersistor());
        }
        catch (CoalescePersistorException e)
        {
            LOGGER.error("Initializing Templates", e);
        }
    }

    /**
     * @return whether the framework has been initialized.
     */
    public boolean isInitialized()
    {
        return this._authoritativePersistor != null;
    }

    /*--------------------------------------------------------------------------
        Get Entity
    --------------------------------------------------------------------------*/

    /**
     * @param key
     * @return an array of Coalesce Entities.
     * @throws CoalescePersistorException
     */
    public CoalesceEntity[] getCoalesceEntities(String... key) throws CoalescePersistorException
    {
        return getXmlPersistor().getEntity(key);
    }

    /**
     * @param key
     * @return a single Coalesce Entity
     * @throws CoalescePersistorException
     */
    public CoalesceEntity getCoalesceEntity(String key) throws CoalescePersistorException
    {
        CoalesceEntity[] results = getCoalesceEntities(key);

        if (results == null || results.length == 0)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Entity", key));
        }

        return results[0];
    }

    /**
     * @param key
     * @return an Array of Coalesce Entity's XML
     * @throws CoalescePersistorException
     */
    public String[] getEntityXmls(String... key) throws CoalescePersistorException
    {
        return getXmlPersistor().getEntityXml(key);
    }

    /**
     * @param key
     * @return the XML of a single CoalesceEnttiy
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String key) throws CoalescePersistorException
    {
        String result = null;
        String[] results = getEntityXmls(key);

        if (results != null && results.length >= 1)
        {
            result = results[0];
        }

        return result;
    }

    /*--------------------------------------------------------------------------
        Other Entity Functions
    --------------------------------------------------------------------------*/

    /**
     * Calls {@link #saveCoalesceEntity(boolean, CoalesceEntity...)} passing
     * <code>false</code> for allowRemoval.
     *
     * @param entities
     * @return whether the authoritative was updated successfully.
     * @throws CoalescePersistorException
     */
    public boolean saveCoalesceEntity(CoalesceEntity... entities) throws CoalescePersistorException
    {
        return this.saveCoalesceEntity(false, entities);
    }

    /**
     * Updates the persistors with the provided entities blocking on the
     * authoritative persistor.
     *
     * @param allowRemoval
     * @param entities
     * @return whether the authoritative was updated successfully.
     * @throws CoalescePersistorException
     */
    public boolean saveCoalesceEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        for (CoalesceEntity entity : entities)
        {
            if (entity.isNew())
            {
                entity.setStatus(ECoalesceObjectStatus.ACTIVE);
            }
        }

        boolean isSuccessful = getAuthoritativePersistor().saveEntity(allowRemoval, entities);

        if (hasSecondaryPersistors())
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Creating Job {}", CoalesceSaveEntityJob.class.getName());
            }

            // Create Parameters
            CoalesceSaveEntityProperties params = new CoalesceSaveEntityProperties();
            params.setAllowRemoval(allowRemoval);
            params.setEntities(entities);

            // Create Job
            CoalesceSaveEntityJob job = new CoalesceSaveEntityJob(params);
            job.setHandler(_handler);
            job.setExecutor(this);
            job.setTarget(getSecondaryPersistors());

            // Update Secondary Persistors
            submit(job);
        }

        // Update Authoritative Persistor
        return isSuccessful;
    }

    /*--------------------------------------------------------------------------
        Template Functions
    --------------------------------------------------------------------------*/

    /**
     * Save the template as well as updates {@link CoalesceTemplateUtil}.
     *
     * @param templates
     * @throws CoalescePersistorException
     * @see ICoalescePersistor#saveTemplate(CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     */
    public void saveCoalesceEntityTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        // Update Authoritative Persistors
        getAuthoritativePersistor().saveTemplate(templates);

        // Update Date Types
        CoalesceTemplateUtil.addTemplates(templates);

        if (hasSecondaryPersistors())
        {
            // Create Job
            CoalesceSaveTemplateJob job = new CoalesceSaveTemplateJob(templates);
            job.setExecutor(this);
            job.setHandler(_handler);
            job.setTarget(getSecondaryPersistors());

            // Update Secondary Persistors
            submit(job);
        }
    }

    public void setTemplates(String... keys) throws CoalescePersistorException
    {
        List<CoalesceEntityTemplate> templateList = new ArrayList<>();

        for (String key : keys)
        {
            try
            {
                templateList.add(getCoalesceEntityTemplate(key));
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException("(FAILED) Template Registration: " + key, e);
            }
        }

        registerTemplates(templateList.toArray(new CoalesceEntityTemplate[templateList.size()]));
    }

    public void setTemplates(CoalesceEntity... templates) throws CoalescePersistorException
    {
        List<CoalesceEntityTemplate> templateList = new ArrayList<>();

        for (CoalesceEntity template : templates)
        {
            try
            {
                template.initialize();
                templateList.add(CoalesceEntityTemplate.create(template));
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException("(FAILED) Template Registration", e);
            }
        }

        registerTemplates(templateList.toArray(new CoalesceEntityTemplate[templateList.size()]));
    }

    /**
     * @see ICoalescePersistor#deleteTemplate(String...)
     */
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        getAuthoritativePersistor().deleteTemplate(keys);
    }

    /**
     * @see ICoalescePersistor#unregisterTemplate(String...)
     */
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        getAuthoritativePersistor().unregisterTemplate(keys);
    }

    /**
     * Save the template as well as updates {@link CoalesceTemplateUtil}.
     *
     * @param templates
     * @throws CoalescePersistorException
     * @see ICoalescePersistor#saveTemplate(CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     */
    public void registerTemplates(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        // Update Authoritative Persistors
        getAuthoritativePersistor().registerTemplate(templates);

        // Update Date Types
        CoalesceTemplateUtil.addTemplates(templates);

        if (hasSecondaryPersistors())
        {
            // Create Job
            CoalesceRegisterTemplateJob job = new CoalesceRegisterTemplateJob(templates);
            job.setExecutor(this);
            job.setHandler(_handler);
            job.setTarget(getSecondaryPersistors());

            // Update Secondary Persistors
            submit(job);
        }
    }

    public CoalesceEntityTemplate getCoalesceEntityTemplate(String key) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplate(key);
    }

    public CoalesceEntityTemplate getCoalesceEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplate(name, source, version);
    }

    public String getCoalesceEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplateKey(name, source, version);
    }

    public List<ObjectMetaData> getCoalesceEntityTemplateMetadata() throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplateMetadata();
    }

    public CoalesceEntity createEntityFromTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {

        CoalesceEntityTemplate template = this.getCoalesceEntityTemplate(name, source, version);

        return template.createNewEntity();

    }

    /*--------------------------------------------------------------------------
        Sync Shell Functions
    --------------------------------------------------------------------------*/

    public CoalesceEntitySyncShell getCoalesceEntitySyncShell(String key)
            throws CoalescePersistorException, SAXException, IOException
    {
        return CoalesceEntitySyncShell.create(this.getCoalesceEntity(key));
    }

    /**
     * (WARNING) This returns null if not running as async
     */
    public <T> Future<T> submit(Callable<T> task) throws CoalescePersistorException
    {
        Future<T> result = null;

        if (_isAsyncUpdates)
        {
            result = super.submit(task);
        }
        else
        {
            try
            {
                task.call();
            }
            catch (Exception e)
            {
                throw new CoalescePersistorException("Failed to Update Secondary Persistors", e);
            }
        }

        return result;
    }

}
