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

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.services.search.service.data.controllers.TemplateDataController;
import com.incadencecorp.coalesce.services.search.service.data.model.CoalesceObjectImpl;
import com.incadencecorp.coalesce.services.search.service.data.model.FieldData;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public class TemplateDataControllerTest {

    @Test
    public void testSavingTemplate() throws Exception
    {
        TemplateDataController controller = createController();

        Assert.assertEquals(1, controller.getEntityTemplateMetadata().size());
        String key = controller.getEntityTemplateMetadata().get(0).getKey();

        CoalesceEntity template1 = controller.getTemplate(key);

        TestEntity entity = new TestEntity();
        entity.initialize(template1);
        entity.setName("HelloWorld");

        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity);

        Assert.assertEquals(true, controller.setTemplate(template2.getKey(), template2.createNewEntity()));

        Assert.assertEquals(2, controller.getEntityTemplateMetadata().size());

        Assert.assertEquals(2, controller.getEntityTemplateMetadata().size());
        Assert.assertEquals(template1.getKey(), controller.getTemplate(template1.getKey()).getKey());
        Assert.assertEquals(template2.getKey(), controller.getTemplate(template2.getKey()).getKey());
    }

    // @Test
    // public void testInvalidCases() throws Exception
    // {
    // TemplateDataController controller = createController();
    //
    // Assert.assertEquals(1, controller.getEntityTemplateMetadata().size());
    // String key = controller.getEntityTemplateMetadata().get(0).getKey();
    //
    // String randomKey = UUID.randomUUID().toString();
    //
    // // Test Invalid Keys
    // Assert.assertNull(controller.getTemplate(randomKey));
    // Assert.assertEquals(0, controller.getRecordSets(randomKey).size());
    // Assert.assertEquals(0, controller.getRecordSetFields(randomKey,
    // randomKey).size());
    // Assert.assertEquals(0, controller.getRecordSetFields(key,
    // randomKey).size());
    // Assert.assertEquals(false, controller.setTemplate(null));
    // }

    @Test(expected = RemoteException.class)
    public void testInValidTemplate() throws Exception
    {
        TemplateDataController controller = createController();
        controller.getTemplate(UUID.randomUUID().toString());
    }

    @Test(expected = RemoteException.class)
    public void testInValidNullTemplate() throws Exception
    {
        TemplateDataController controller = createController();
        controller.getTemplate(null);
    }

    @Test(expected = RemoteException.class)
    public void testInValidTemplateRecordset() throws Exception
    {
        TemplateDataController controller = createController();

        controller.getRecordSets(UUID.randomUUID().toString());
    }

    @Test(expected = RemoteException.class)
    public void testInValidTemplateRecordsetAndFields() throws Exception
    {
        TemplateDataController controller = createController();

        String randomKey = UUID.randomUUID().toString();
        Assert.assertEquals(0, controller.getRecordSetFields(randomKey, randomKey).size());
    }

    @Test(expected = RemoteException.class)
    public void testInValidTemplateFields() throws Exception
    {
        TemplateDataController controller = createController();

        Assert.assertEquals(1, controller.getEntityTemplateMetadata().size());
        String key = controller.getEntityTemplateMetadata().get(0).getKey();

        controller.getRecordSetFields(key, UUID.randomUUID().toString());
    }

    @Test
    public void testRecordsets() throws Exception
    {
        TemplateDataController controller = createController();

        CoalesceEntity entity = CoalesceEntity.create("template controller test", "unit test", "1");
        entity.initialize();

        CoalesceSection section = CoalesceSection.create(entity, "section");
        TestRecord.createCoalesceRecordset(section, "rs-1").setMaxRecords(1);
        TestRecord.createCoalesceRecordset(section, "rs-2");

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        Assert.assertEquals(true, controller.setTemplate(template.getKey(), template.createNewEntity()));

        List<CoalesceObjectImpl> results = controller.getRecordSets(template.getKey());

        // Verify Recordsets
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(CoalesceEntity.class.getSimpleName(), results.get(0).getName());
        Assert.assertEquals(CoalesceEntity.class.getSimpleName(), results.get(0).getKey());
        Assert.assertEquals("rs-1", results.get(1).getName());
        Assert.assertEquals("rs-2", results.get(2).getName());

        // Verify Fields
        List<FieldData> fieldResults = controller.getRecordSetFields(template.getKey(), results.get(0).getKey());

        Assert.assertEquals("objectkey", fieldResults.get(0).getName());
        Assert.assertEquals(CoalesceEntity.ATTRIBUTE_TITLE, fieldResults.get(1).getName());
        Assert.assertEquals(CoalesceEntity.ATTRIBUTE_NAME, fieldResults.get(2).getName());
        Assert.assertEquals(CoalesceEntity.ATTRIBUTE_SOURCE, fieldResults.get(3).getName());
        Assert.assertEquals(CoalesceEntity.ATTRIBUTE_DATECREATED, fieldResults.get(4).getName());
        Assert.assertEquals(CoalesceEntity.ATTRIBUTE_LASTMODIFIED, fieldResults.get(5).getName());

        Assert.assertEquals(ECoalesceFieldDataTypes.GUID_TYPE, fieldResults.get(0).getType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, fieldResults.get(1).getType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, fieldResults.get(2).getType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, fieldResults.get(3).getType());
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, fieldResults.get(4).getType());
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, fieldResults.get(5).getType());

        fieldResults = controller.getRecordSetFields(template.getKey(), results.get(1).getKey());

        Assert.assertTrue(fieldResults.size() > 0);

        controller.deleteTemplate(template.getKey());

    }

    private TemplateDataController createController() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceFramework framework = new CoalesceFramework();
        framework.setAuthoritativePersistor(new DerbyPersistor());
        framework.saveCoalesceEntityTemplate(template);

        TemplateDataController controller = new TemplateDataController(framework);
        Assert.assertNotNull(controller.getTemplate(template.getKey()));

        return controller;
    }
}
