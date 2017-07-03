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

package com.incadencecorp.coalesce.services.search.service.rest;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.MockPersister;
import com.incadencecorp.coalesce.services.search.service.data.controllers.TemplateDataController;
import com.incadencecorp.unity.common.CallResult.CallResults;

public class TemplateDataControllerTest {

    @Test
    public void testSavingTemplate() throws Exception
    {

        ICoalescePersistor persister = new MockPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceEntityTemplate template1 = CoalesceEntityTemplate.create(entity);
        entity.setName("HelloWorld");
        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity);

        TemplateDataController controller = new TemplateDataController(persister);
        Assert.assertEquals(CallResults.SUCCESS, controller.setTemplate(template1).getCallResults());

        Assert.assertEquals(1, controller.getEntityTemplateMetadata().size());

        Assert.assertEquals(CallResults.SUCCESS, controller.setTemplate(template2).getCallResults());

        Assert.assertEquals(2, controller.getEntityTemplateMetadata().size());

        Assert.assertEquals(2, controller.getEntityTemplateMetadata().size());
        Assert.assertEquals(template1.getKey(), controller.getTemplate(template1.getKey()).getKey());
        Assert.assertEquals(template2.getKey(), controller.getTemplate(template2.getKey()).getKey());
    }

}
