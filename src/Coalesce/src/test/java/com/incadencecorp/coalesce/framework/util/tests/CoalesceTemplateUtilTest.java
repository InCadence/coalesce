/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.util.tests;

import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * @author Derek Clemenzi
 */
public class CoalesceTemplateUtilTest {

    private static final DefaultNormalizer NORMALIZER = new DefaultNormalizer();
    private static final String FIELD1_NAME = "field1";
    private static final String FIELD2_NAME = "field2";

    private static final String SECTION_NAME = "unit_test";
    private static final String COMMON_RECORDSET_NAME = "common";
    private static final String ENTITY1_RECORDSET_NAME = "Entity 1 Recordset";
    private static final String ENTITY2_RECORDSET_NAME = "Entity 2 Recordset";

    private static final String FIELD1_E1_KEY = NORMALIZER.normalize(ENTITY1_RECORDSET_NAME, FIELD1_NAME);
    private static final String FIELD2_E1_KEY = NORMALIZER.normalize(ENTITY1_RECORDSET_NAME, FIELD2_NAME);
    private static final String FIELD1_COMMON_KEY = NORMALIZER.normalize(COMMON_RECORDSET_NAME, FIELD1_NAME);
    private static final String FIELD2_COMMON_KEY = NORMALIZER.normalize(COMMON_RECORDSET_NAME, FIELD2_NAME);
    private static final String FIELD1_E2_KEY = NORMALIZER.normalize(ENTITY2_RECORDSET_NAME, FIELD1_NAME);

    private static String template1Key;
    private static String template2Key;

    @BeforeClass
    public static void initialize() throws Exception
    {
        // Template 1
        CoalesceEntity entity1 = CoalesceEntity.create("entity1", "unit_test", "1");
        CoalesceSection section = CoalesceSection.create(entity1, SECTION_NAME);
        CoalesceRecordset recordset1 = CoalesceRecordset.create(section, ENTITY1_RECORDSET_NAME);
        CoalesceRecordset recordset2 = CoalesceRecordset.create(section, COMMON_RECORDSET_NAME);

        CoalesceFieldDefinition.create(recordset1, FIELD1_NAME, ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset1, FIELD2_NAME, ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceFieldDefinition.create(recordset2, FIELD1_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordset2, FIELD2_NAME, ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceEntityTemplate template1 = CoalesceEntityTemplate.create(entity1);

        template1Key = template1.getKey();

        // Template 2
        CoalesceEntity entity2 = CoalesceEntity.create("entity2", "unit_test", "1");
        CoalesceSection section2 = CoalesceSection.create(entity2, SECTION_NAME);

        CoalesceRecordset recordset = CoalesceRecordset.create(section2, ENTITY2_RECORDSET_NAME);
        CoalesceFieldDefinition.create(recordset, FIELD1_NAME, ECoalesceFieldDataTypes.DOUBLE_TYPE);

        CoalesceRecordset recordset3 = CoalesceRecordset.create(section2, COMMON_RECORDSET_NAME);
        CoalesceFieldDefinition.create(recordset3, FIELD1_NAME, ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordset3, FIELD2_NAME, ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity2);

        template2Key = template2.getKey();

        // Add Templates
        CoalesceTemplateUtil.setNormalizer(NORMALIZER);
        CoalesceTemplateUtil.addTemplates(template1, template2);

        CoalesceTemplateUtil.logDataTypeBreakout();
    }

    @Test
    public void testRecordsetDataTypes() throws Exception
    {
        Map<String, ECoalesceFieldDataTypes> types;

        // Verify Recordset 1
        types = CoalesceTemplateUtil.getRecordsetDataTypes(ENTITY1_RECORDSET_NAME);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.containsKey(FIELD1_E1_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, types.get(FIELD1_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, types.get(FIELD2_E1_KEY));

        // Verify Recordset 2
        types = CoalesceTemplateUtil.getRecordsetDataTypes(COMMON_RECORDSET_NAME);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.containsKey(FIELD1_COMMON_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, types.get(FIELD1_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, types.get(FIELD2_COMMON_KEY));

        // Verify Recordset 2
        types = CoalesceTemplateUtil.getRecordsetDataTypes(ENTITY2_RECORDSET_NAME);
        Assert.assertEquals(1, types.size());
        Assert.assertTrue(types.containsKey(FIELD1_E2_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DOUBLE_TYPE, types.get(FIELD1_E2_KEY));

        // Verify Invalid Key
        types = CoalesceTemplateUtil.getRecordsetDataTypes("Invalid Name");
        Assert.assertEquals(0, types.size());
    }

    @Test
    public void testTemplateDataTypes() throws Exception
    {
        Map<String, ECoalesceFieldDataTypes> types;

        // Verify Template 1
        types = CoalesceTemplateUtil.getTemplateDataTypes(template1Key);
        Assert.assertEquals(4, types.size());
        Assert.assertTrue(types.containsKey(FIELD1_E1_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_E1_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, types.get(FIELD1_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, types.get(FIELD2_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, types.get(FIELD1_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, types.get(FIELD2_COMMON_KEY));

        // Verify Template 2
        types = CoalesceTemplateUtil.getTemplateDataTypes(template2Key);
        Assert.assertEquals(3, types.size());
        Assert.assertTrue(types.containsKey(FIELD1_E2_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DOUBLE_TYPE, types.get(FIELD1_E2_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, types.get(FIELD1_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, types.get(FIELD2_COMMON_KEY));

        // Verify Invalid Key
        types = CoalesceTemplateUtil.getTemplateDataTypes("Invalid Key");
        Assert.assertEquals(0, types.size());
    }

    @Test
    public void testAllDataTypes() throws Exception
    {
        Map<String, ECoalesceFieldDataTypes> types;

        // Verify All
        types = CoalesceTemplateUtil.getDataTypes();
        Assert.assertTrue(types.containsKey(FIELD1_E1_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_E1_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertTrue(types.containsKey(FIELD2_COMMON_KEY));
        Assert.assertTrue(types.containsKey(FIELD1_E2_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, types.get(FIELD1_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, types.get(FIELD2_E1_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, types.get(FIELD1_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.BOOLEAN_TYPE, types.get(FIELD2_COMMON_KEY));
        Assert.assertEquals(ECoalesceFieldDataTypes.DOUBLE_TYPE, types.get(FIELD1_E2_KEY));
    }

    @Test
    public void testTemplateKey() throws Exception
    {
        Set<String> types;

        // Verify Template 1
        types = CoalesceTemplateUtil.getTemplateKey(COMMON_RECORDSET_NAME);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(template1Key));
        Assert.assertTrue(types.contains(template2Key));

        // Verify Template 2
        types = CoalesceTemplateUtil.getTemplateKey(ENTITY1_RECORDSET_NAME);
        Assert.assertEquals(1, types.size());
        Assert.assertTrue(types.contains(template1Key));

        // Verify Template 2
        types = CoalesceTemplateUtil.getTemplateKey(ENTITY2_RECORDSET_NAME);
        Assert.assertEquals(1, types.size());
        Assert.assertTrue(types.contains(template2Key));

        // Verify Invalid Key
        types = CoalesceTemplateUtil.getTemplateKey("Invalid Name");
        Assert.assertEquals(0, types.size());
    }

    @Test
    public void testRecordsets() throws Exception
    {
        Set<String> types;

        // Verify Template 1
        types = CoalesceTemplateUtil.getRecordsets(template1Key);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(COMMON_RECORDSET_NAME));
        Assert.assertTrue(types.contains(NORMALIZER.normalize(ENTITY1_RECORDSET_NAME)));

        // Verify Template 2
        types = CoalesceTemplateUtil.getRecordsets(template2Key);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(COMMON_RECORDSET_NAME));
        Assert.assertTrue(types.contains(NORMALIZER.normalize(ENTITY2_RECORDSET_NAME)));

        // Verify All
        types = CoalesceTemplateUtil.getRecordsets();
        Assert.assertEquals(3, types.size());
        Assert.assertTrue(types.contains(COMMON_RECORDSET_NAME));
        Assert.assertTrue(types.contains(NORMALIZER.normalize(ENTITY1_RECORDSET_NAME)));
        Assert.assertTrue(types.contains(NORMALIZER.normalize(ENTITY2_RECORDSET_NAME)));

        // Verify Invalid Key
        types = CoalesceTemplateUtil.getRecordsets("Invalid Key");
        Assert.assertEquals(0, types.size());
    }
}
