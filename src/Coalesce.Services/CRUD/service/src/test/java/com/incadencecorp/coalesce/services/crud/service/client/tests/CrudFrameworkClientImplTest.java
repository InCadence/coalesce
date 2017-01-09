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

package com.incadencecorp.coalesce.services.crud.service.client.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.persistance.memory.MockPersister;
import com.incadencecorp.coalesce.services.api.IBaseClient;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusActionType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.api.crud.ELinkAction;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;

/**
 * These unit test ensure correct behavior of the CRUD server.
 * 
 * @author Derek Clemenzi
 */
// TODO Refactor the core of this test into coalesce-crud-api. 
public class CrudFrameworkClientImplTest {

    private static ICrudClient client;

    @BeforeClass
    public static void initialize() throws Exception
    {
        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(new MockPersister());

        client = new CrudFrameworkClientImpl(null, framework);
    }

    @Test
    public void testRetrieveDataObjects() throws Exception
    {

        CoalesceEntity entity = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity), client, entity);

        Results<CoalesceEntity>[] results = client.retrieveDataObjects(entity.getKey());

        assertResult(results);

        System.out.println(results[0].getResult().toXml());

    }

    @Test
    public void testUpdateDataObject() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity), client, entity);

        entity.setTitle("HelloWorld");

        assertResult(client.updateDataObject(entity), client, entity);

        Results<CoalesceEntity>[] results = client.retrieveDataObjects(entity.getKey());

        assertResult(results);

        Assert.assertEquals(entity.getTitle(), results[0].getResult().getTitle());

    }

    @Test
    public void testUpdateLinkages() throws Exception
    {
        CoalesceEntity entity1 = CoalesceEntity.create("Hello", "World", "1", null, null);
        CoalesceEntity entity2 = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity1, entity2), client, entity1, entity2);

        DataObjectLinkType task = new DataObjectLinkType();
        task.setAction(ELinkAction.LINK);
        task.setDataObjectKeySource(entity1.getKey());
        task.setDataObjectKeyTarget(entity2.getKey());
        task.setLabel("HelloWorld");
        task.setLinkType(ELinkTypes.IS_PARENT_OF);

        assertResult(client.updateLinkages(task), client);

    }

    
    @Test
    public void testUpdateLinkagesFailure() throws Exception
    {
        CoalesceEntity entity1 = CoalesceEntity.create("Hello", "World", "1", null, null);
        CoalesceEntity entity2 = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity1, entity2), client, entity1, entity2);

        // Intentionally Not Specifying Linkage Action
        DataObjectLinkType task = new DataObjectLinkType();
        task.setDataObjectKeySource(entity1.getKey());
        task.setDataObjectKeyTarget(entity2.getKey());
        task.setLabel("HelloWorld");
        task.setLinkType(ELinkTypes.IS_PARENT_OF);

        Assert.assertFalse(client.updateLinkages(task));

    }
    @Test
    public void testUpdateDataObjectStatus() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity), client, entity);

        DataObjectStatusType task = new DataObjectStatusType();
        task.setAction(DataObjectStatusActionType.MARK_AS_READONLY);
        task.setKey(entity.getKey());
        
        assertResult(client.updateDataObjectStatus(task), client, entity);

        Results<CoalesceEntity>[] results = client.retrieveDataObjects(entity.getKey());

        assertResult(results);

        Assert.assertEquals(ECoalesceObjectStatus.READONLY, results[0].getResult().getStatus());
    }
    
    @Test
    public void testUpdateDataObjectStatusFailure() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("Hello", "World", "1", null, null);

        assertResult(client.createDataObject(entity), client, entity);

        DataObjectStatusType task = new DataObjectStatusType();
        task.setKey(entity.getKey());
        
        Assert.assertFalse(client.updateDataObjectStatus(task));
    }

    // TODO Move these into a common package
    protected static void assertResult(boolean isSuccessful, IBaseClient<?> client, CoalesceEntity... entities)
    {
        if (!isSuccessful)
        {
            ResultsType[] results = client.getLastResult();

            for (int ii = 0; ii < results.length; ii++)
            {
                ResultsType result = results[ii];

                if (result.getStatus() == EResultStatus.FAILED)
                {

                    // TODO Not Implemented
                    // if (ValidationUtil.isValidationFailure(result) && ii <
                    // entities.length)
                    // {
                    //
                    // LOGGER.info("Validation Failed");
                    //
                    // for (Map.Entry<CoalesceField<?>, String> entry :
                    // ValidationUtil.getErrors(entities[ii],
                    // result).entrySet())
                    // {
                    // LOGGER.info("{}: {}", entry.getKey().getName(),
                    // entry.getValue());
                    // }
                    //
                    // }

                    Assert.fail(result.getError());
                }
            }

            Assert.fail("unknown");
        }
    }

    private void assertResult(final Results<?>... results)
    {

        for (Results<?> result : results)
        {
            if (result.getStatus() == EResultStatus.FAILED)
            {
                Assert.fail(result.getError());
            }
        }
    }

}
