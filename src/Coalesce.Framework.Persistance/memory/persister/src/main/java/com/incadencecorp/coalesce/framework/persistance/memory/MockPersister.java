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

package com.incadencecorp.coalesce.framework.persistance.memory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This mock implementation uses a memory to store and retrieve entities.
 *
 * @author n78554
 */
public class MockPersister extends CoalescePersistorBase {

    protected List<String> keys = new ArrayList<String>();

    private boolean causeError = false;

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
        String[] results = new String[keys.length];

        for (int ii = 0; ii < keys.length; ii++)
        {
            CoalesceEntity entity = getCacher().retrieveEntity(keys[ii]);

            if (entity != null)
            {
                results[ii] = entity.toXml();
            }
        }

        return results;
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        return null;
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version) throws CoalescePersistorException
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
        return null;
    }

    @Override
    protected void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {
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

}
