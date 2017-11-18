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

package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

import java.util.*;

/**
 * This mock implementation uses a memory to store and retrieve entities.
 *
 * @author n78554
 */
public class MockPersister extends CoalescePersistorBase {

    protected List<String> keys = new ArrayList<String>();

    private boolean causeError = false;
    private final Map<String, CoalesceEntityTemplate> templateMap = new HashMap<String, CoalesceEntityTemplate>();

    /**
     * Default Constructor; Creates a mock cacher.
     */
    public MockPersister()
    {
        setCacher(new MockCacher());
    }

    /**
     * Sets whether when persisting an error should be thrown for testing error
     * cases in unit tests.
     *
     * @param value
     */
    public void setThrowException(boolean value)
    {
        causeError = value;
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        return new String[0];
    }

    @Override
    public String getEntityTemplateXml(String key) throws CoalescePersistorException
    {
        if (templateMap.containsKey(key))
        {
            return templateMap.get(key).toXml();
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> results = new ArrayList<ObjectMetaData>();

        for (CoalesceEntityTemplate template : templateMap.values())
        {
            results.add(new ObjectMetaData(template.getKey(),
                                           template.getName(),
                                           template.getSource(),
                                           template.getVersion()));
        }

        return results;
    }

    @Override
    protected void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {
        for (CoalesceEntityTemplate template : templates)
        {
            if (template != null)
            {
                templateMap.put(template.getKey(), template);
            }
        }

        if (causeError)
        {
            throw new CoalescePersistorException("Hello World", null);
        }
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        return flattenCore(allowRemoval, entities);
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        if (!causeError)
        {
            for (CoalesceEntity entity : entities)
            {
                keys.add(entity.getKey());
            }
        }
        else
        {
            throw new CoalescePersistorException("Hello World", null);
        }

        return true;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = super.getCapabilities();
        EnumSet<EPersistorCapabilities> newCapabilities = EnumSet.of(EPersistorCapabilities.READ_TEMPLATES,
                                                                     EPersistorCapabilities.UPDATE,
                                                                     EPersistorCapabilities.DELETE);
        if (enumSet != null)
        {
            enumSet.addAll(newCapabilities);
        }
        else
        {
            enumSet = newCapabilities;
        }
        return enumSet;
    }

}
