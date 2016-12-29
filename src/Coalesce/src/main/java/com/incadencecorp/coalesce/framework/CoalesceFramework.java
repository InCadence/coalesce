package com.incadencecorp.coalesce.framework;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceRegisterTemplateJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityJob;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveEntityProperties;
import com.incadencecorp.coalesce.framework.jobs.CoalesceSaveTemplateJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.persistance.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.EntityMetaData;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;

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
public class CoalesceFramework implements ICoalesceExecutorService, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceFramework.class);

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _persistors[];
    private ICoalescePersistor _authoritativePersistor;
    private IExceptionHandler _handler;
    private ExecutorService _pool;
    private boolean _isAsyncUpdates = true;

    private ICoalescePersistor[] getSecondaryPersistors()
    {
        return _persistors;
    }

    private boolean hasSecondaryPersistors()
    {
        return _persistors != null && _persistors.length > 0;
    }

    private ICoalescePersistor getAuthoritativePersistor()
    {
        return _authoritativePersistor;
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
        if (service == null)
        {
            LOGGER.info("Using Default ExecutorService; Cores: {} Max Threads / Core: {}",
                        CoalesceSettings.getNumberOfCores(),
                        CoalesceSettings.getMaxThreadsPerCore());

            service = new ThreadPoolExecutor(CoalesceSettings.getMinThreads(),
                                             CoalesceSettings.getMaxThreads(),
                                             CoalesceSettings.getKeepAliveTime(),
                                             TimeUnit.SECONDS,
                                             new SynchronousQueue<Runnable>(),
                                             new CoalesceThreadFactoryImpl(),
                                             new ThreadPoolExecutor.CallerRunsPolicy());
        }

        _pool = service;

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Executor Service ({})", service.getClass().getName());
        }
    }

    /**
     * @return the service used to execute threads.
     */
    public ExecutorService getExecutorService()
    {
        return _pool;
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
        return getAuthoritativePersistor().getEntity(key);
    }

    /**
     * @param key
     * @return a single Coalesce Entity
     * @throws CoalescePersistorException
     */
    public CoalesceEntity getCoalesceEntity(String key) throws CoalescePersistorException
    {
        CoalesceEntity result = null;
        CoalesceEntity[] results = getAuthoritativePersistor().getEntity(key);

        if (results != null && results.length == 1)
        {
            result = results[0];
        }

        return result;

    }

    /**
     * @see ICoalescePersistor#getEntity(String, String)
     * @param entityId
     * @param entityIdType
     * @return {@link ICoalescePersistor#getEntity(String, String)}
     * @throws CoalescePersistorException
     */
    public CoalesceEntity getEntity(String entityId, String entityIdType) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntity(entityId, entityIdType);
    }

    /**
     * @see ICoalescePersistor#getEntity(String, String, String)
     * @param name
     * @param entityId
     * @param entityIdType
     * @return {@link ICoalescePersistor#getEntity(String, String, String)}
     * @throws CoalescePersistorException
     */
    public CoalesceEntity getEntity(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntity(name, entityId, entityIdType);
    }

    /**
     * @param key
     * @return an Array of Coalesce Entity's XML
     * @throws CoalescePersistorException
     */
    public String[] getEntityXmls(String... key) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityXml(key);
    }

    /**
     * @param key
     * @return the XML of a single CoalesceEnttiy
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String key) throws CoalescePersistorException
    {
        String result = null;
        String[] results = getAuthoritativePersistor().getEntityXml(key);

        if (results != null && results.length > 1)
        {
            result = results[0];
        }

        return result;
    }

    /**
     * @see ICoalescePersistor#getEntityXml(String, String)
     * @param entityId
     * @param entityIdType
     * @return {@link ICoalescePersistor#getEntityXml(String, String)}
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityXml(entityId, entityIdType);
    }

    /**
     * @see ICoalescePersistor#getEntityXml(String, String, String)
     * @param name
     * @param entityId
     * @param entityIdType
     * @return {@link ICoalescePersistor#getEntityXml(String, String, String)}
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityXml(name, entityId, entityIdType);
    }

    /*--------------------------------------------------------------------------
    	EntityID Functions
    --------------------------------------------------------------------------*/

    public String getCoalesceEntityKeyForEntityId(String entityId, String entityIdType, String entityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeyForEntityId(entityId, entityIdType, entityName, null);
    }

    public String getCoalesceEntityKeyForEntityId(String entityId,
                                                  String entityIdType,
                                                  String entityName,
                                                  String entitySource)
            throws CoalescePersistorException
    {

        String entityKey = null;

        List<String> list = this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName, entitySource);

        if (!list.isEmpty())
        {

            entityKey = list.get(0);

        }

        return entityKey;
    }

    public List<String> getCoalesceEntityKeysForEntityId(String entityId, String entityIdType, String entityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName, null);
    }

    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource)
            throws CoalescePersistorException
    {

        List<String> list = new ArrayList<String>();

        String[] entityIdList = entityId.split(",");
        String[] entityIdTypeList = entityIdType.split(",");

        if (entityIdList.length == entityIdTypeList.length)
        {

            for (int i = 0; i < entityIdTypeList.length; i++)
            {

                list.addAll(getAuthoritativePersistor().getCoalesceEntityKeysForEntityId(entityIdList[i],
                                                                                         entityIdTypeList[i],
                                                                                         entityName,
                                                                                         entitySource));

            }

        }

        return list;

    }

    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getCoalesceEntityIdAndTypeForKey(key);
    }

    /*--------------------------------------------------------------------------
    	Other Entity Functions
    --------------------------------------------------------------------------*/

    public DateTime getCoalesceEntityLastModified(String key, String objectType) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getCoalesceObjectLastModified(key, objectType);
    }

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

    public String getCoalesceFieldValue(String fieldKey) throws CoalescePersistorException
    {
        return (String) getAuthoritativePersistor().getFieldValue(fieldKey);
    }

    public CoalesceRecord getCoalesceRecord(String key) throws CoalescePersistorException
    {
        CoalesceRecord record = null;

        ElementMetaData metaData = getAuthoritativePersistor().getXPath(key, "record");
        if (metaData != null)
        {

            CoalesceEntity[] results = getAuthoritativePersistor().getEntity(metaData.getEntityKey());

            if (results != null && results.length == 1 && results[0] != null)
            {
                record = (CoalesceRecord) results[0].getCoalesceObjectForNamePath(metaData.getElementXPath());
            }

        }

        return record;
    }

    public CoalesceStringField getCoalesceFieldByFieldKey(String key) throws CoalescePersistorException
    {
        CoalesceStringField field = null;

        ElementMetaData metaData = getAuthoritativePersistor().getXPath(key, "field");

        if (metaData != null)
        {

            CoalesceEntity[] results = getAuthoritativePersistor().getEntity(metaData.getEntityKey());

            if (results != null && results.length == 1 && results[0] != null)
            {
                field = (CoalesceStringField) results[0].getCoalesceObjectForKey(key);
            }

        }

        return field;
    }

    /*--------------------------------------------------------------------------
    	Template Functions
    --------------------------------------------------------------------------*/

    /**
     * Save the template as well as updates {@link CoalesceTemplateUtil}.
     * 
     * @see ICoalescePersistor#saveTemplate(CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     * @param templates
     * @throws CoalescePersistorException
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

    /**
     * Save the template as well as updates {@link CoalesceTemplateUtil}.
     * 
     * @see ICoalescePersistor#saveTemplate(CoalesceEntityTemplate...)
     * @see CoalesceTemplateUtil#addTemplates(CoalesceEntityTemplate...)
     * @param templates
     * @throws CoalescePersistorException
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

    /**
     * @param name
     * @param source
     * @param version
     * @return <code>null</code> is the template is not found.
     * @throws SAXException
     * @throws IOException
     * @throws CoalescePersistorException
     */
    public CoalesceEntityTemplate getCoalesceEntityTemplate(String name, String source, String version)
            throws SAXException, IOException, CoalescePersistorException
    {

        CoalesceEntityTemplate template = null;

        // Retrieve Template
        String xml = this.getCoalesceEntityTemplateXml(name, source, version);

        if (xml != null)
        {
            // Initialize Template
            template = new CoalesceEntityTemplate();
            template.initialize(xml);
        }

        return template;

    }

    public String getCoalesceEntityTemplateXml(String key) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplateXml(key);
    }

    public String getCoalesceEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        return getAuthoritativePersistor().getEntityTemplateXml(name, source, version);
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

        String xml = this.getCoalesceEntityTemplateXml(name, source, version);

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(xml);

        return entity;

    }

    /*--------------------------------------------------------------------------
    	Sync Shell Functions
    --------------------------------------------------------------------------*/

    public CoalesceEntitySyncShell getCoalesceEntitySyncShell(String key)
            throws CoalescePersistorException, SAXException, IOException
    {
        return CoalesceEntitySyncShell.create(this.getCoalesceEntity(key));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Invoking Tasks");
        }

        if (_pool.isShutdown())
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Pool is Shutdown");
            }
            return null;
        }
        else
        {
            return _pool.invokeAll(tasks);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) throws CoalescePersistorException
    {
        return _pool.submit(task);
    }

    public <T, Y extends ICoalesceResponseType<?>> Future<Y> submit(AbstractCoalesceJob<T, Y> job) throws CoalescePersistorException
    {

        Future<Y> result = null;

        if (_isAsyncUpdates)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Submitting {} job", job.getClass().getName());
            }

            result = _pool.submit(job);
        }
        else
        {
            try
            {
                job.call();
            }
            catch (Exception e)
            {
                throw new CoalescePersistorException("Failed to Update Secondary Persistors", e);
            }
        }

        return result;
    }

    @Override
    public final void execute(Runnable command)
    {
        _pool.execute(command);
    }

    @Override
    public final boolean isShutdown()
    {
        return _pool.isShutdown();
    }

    @Override
    public final boolean isTerminated()
    {
        return _pool.isTerminated();
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return _pool.invokeAll(tasks, timeout, unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return _pool.invokeAny(tasks);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return _pool.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void close()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Closing Framework");
        }

        try
        {
            // Graceful shutdown (Allow 1 minute for jobs to finish)
            _pool.shutdown();

            if (!_pool.awaitTermination(1, TimeUnit.MINUTES))
            {

                LOGGER.warn("(FAILED) Graceful Pool Termination");

                logFailedJobs(_pool.shutdownNow());

                if (!_pool.awaitTermination(1, TimeUnit.MINUTES))
                {
                    LOGGER.error(" (FAILED) Pool Terminate");
                }
            }
        }
        catch (InterruptedException e)
        {

            LOGGER.warn("(FAILED) Graceful Pool Termination", e);

            logFailedJobs(_pool.shutdownNow());

            // Preserve Interrupt Status
            Thread.currentThread().interrupt();
        }
    }

    private void logFailedJobs(List<Runnable> jobList)
    {
        for (Runnable job : jobList)
        {
            if (job instanceof AbstractCoalesceJob<?, ?>)
            {
                LOGGER.warn("Job Failed {}", job.getClass().getName());
            }
        }
    }

}
