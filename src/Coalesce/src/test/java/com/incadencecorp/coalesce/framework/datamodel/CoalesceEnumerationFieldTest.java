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

package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceAttributes;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.ResultType;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.PropertyEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;

/**
 * These unit test ensure that the enumeration field works as expected.
 * 
 * @author n78554
 *
 */
public class CoalesceEnumerationFieldTest {

    private enum Test1
    {
        ZERO, ONE, TWO;
    }

    private enum Test2
    {
        HELLO, WORLD;
    }

    private enum Test3
    {
        TWO, ONE, ZERO;
    }

    /**
     * This test ensures that the enumeration field definition is created
     * corrected initially and when represents via a template.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateEnumerationField() throws Exception
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");
        CoalesceFieldDefinition def = CoalesceFieldDefinition.create(recordset,
                                                                     "enum",
                                                                     ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        CoalesceConstraint.createEnumeration(def, "enum", "MyEnum");

        // Verify Creation
        Assert.assertEquals("MyEnum", def.getConstraints("enum").getValue());
        Assert.assertEquals("enum", def.getName());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE, def.getDataType());

        // Create Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        CoalesceEntity created = template.createNewEntity();

        CoalesceFieldDefinition object = (CoalesceFieldDefinition) created.getCoalesceObjectForNamePath(def.getNamePath());

        // Verify Creation
        Assert.assertEquals("MyEnum", object.getConstraints("enum").getValue());
        Assert.assertEquals("enum", object.getName());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            ECoalesceFieldDataTypes.getTypeForCoalesceType(object.getAttribute(CoalesceAttributes.ATTR_DATA_TYPE)));
    }

    /**
     * This test ensures that the enumeration field definition is created
     * corrected initially and when represents via a template.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateEnumerationListField() throws Exception
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");
        CoalesceFieldDefinition def = CoalesceFieldDefinition.create(recordset,
                                                                     "enum",
                                                                     ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);
        CoalesceConstraint.createEnumeration(def, "enum", "MyEnum");

        // Verify Creation
        Assert.assertEquals("MyEnum", def.getConstraints("enum").getValue());
        Assert.assertEquals("enum", def.getName());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE, def.getDataType());

        // Create Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        CoalesceEntity created = template.createNewEntity();

        CoalesceFieldDefinition object = (CoalesceFieldDefinition) created.getCoalesceObjectForNamePath(def.getNamePath());

        // Verify Creation
        Assert.assertEquals("MyEnum", object.getConstraints("enum").getValue());
        Assert.assertEquals("enum", object.getName());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE,
                            ECoalesceFieldDataTypes.getTypeForCoalesceType(object.getAttribute(CoalesceAttributes.ATTR_DATA_TYPE)));
    }

    /**
     * Ensures that the java enumeration implementation functions as intended.
     * 
     * @throws Exception
     */
    @Test
    public void testJavaEnumerationProviderImpl() throws Exception
    {
        JavaEnumerationProviderImpl provider = new JavaEnumerationProviderImpl();

        Assert.assertTrue(provider.handles(null, Test1.class.getName()));
        Assert.assertEquals(0, provider.toPosition(null, Test1.class.getName(), Test1.ZERO.toString()));
        Assert.assertEquals(1, provider.toPosition(null, Test1.class.getName(), Test1.ONE.toString()));
        Assert.assertEquals(2, provider.toPosition(null, Test1.class.getName(), Test1.TWO.toString()));

        Assert.assertEquals(Test1.ZERO.toString(), provider.toString(null, Test1.class.getName(), 0));
        Assert.assertEquals(Test1.ONE.toString(), provider.toString(null, Test1.class.getName(), 1));
        Assert.assertEquals(Test1.TWO.toString(), provider.toString(null, Test1.class.getName(), 2));

        Assert.assertTrue(provider.isValid(null, Test1.class.getName(), 0));
        Assert.assertTrue(provider.isValid(null, Test1.class.getName(), 1));
        Assert.assertTrue(provider.isValid(null, Test1.class.getName(), 2));
        Assert.assertTrue(!provider.isValid(null, Test1.class.getName(), 6));

        Assert.assertTrue(provider.isValid(null, Test1.class.getName(), Test1.ZERO.toString()));
        Assert.assertTrue(!provider.isValid(null, Test1.class.getName(), "HELLO WORLD"));

        Assert.assertTrue(provider.isValid(null, Test2.class.getName(), Test2.HELLO.toString()));

    }

    /**
     * This unit test ensures that the getters and setters of the enumeration
     * field work as intended.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationField() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new ConstraintEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum1", Test1.class);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum2", Test2.class);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum3", Test3.class);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationField field1 = (CoalesceEnumerationField) record.getFieldByName("enum1");
        CoalesceEnumerationField field2 = (CoalesceEnumerationField) record.getFieldByName("enum2");
        CoalesceEnumerationField field3 = (CoalesceEnumerationField) record.getFieldByName("enum3");

        field1.setValue(2);
        field2.setValue(1);
        field3.setValueAsEnumeration(Test3.ZERO);

        Assert.assertEquals(2, (int) field1.getValue());
        Assert.assertEquals(Test1.TWO, field1.getValueAsEnumeration(Test1.ONE));

        Assert.assertEquals(1, (int) field2.getValue());
        Assert.assertEquals("1", field2.getBaseValue());

        Assert.assertEquals(2, (int) field3.getValue());
        Assert.assertEquals("2", field3.getBaseValue());

        CoalesceValidator validator = new CoalesceValidator();
        Map<String, String> results = validator.validate(null, entity, template);

        Assert.assertEquals(0, results.size());

        field1.setValue(10);

        results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "10", Test1.class.getName()),
                            results.get(field1.getKey()));

        EnumerationProviderUtil.setEnumerationProviders();

    }

    /**
     * This unit test ensures that each getter / setter works as expected.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationFieldOptions() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum1", Test1.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationField field1 = (CoalesceEnumerationField) record.getFieldByName("enum1");

        field1.setValue(2);

        Assert.assertEquals(2, (int) field1.getValue());
        Assert.assertEquals(Test1.TWO, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertEquals(Test1.TWO, field1.getValueAsEnumeration(Test1.class));
        Assert.assertEquals(Test1.TWO.toString(), field1.getValueAsString(null));

        field1.setValueAsString(null, Test1.ONE.toString());

        Assert.assertEquals(1, (int) field1.getValue());
        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.class));
        Assert.assertEquals(Test1.ONE.toString(), field1.getValueAsString(null));

        field1.setValueAsEnumeration(Test1.ZERO);

        Assert.assertEquals(0, (int) field1.getValue());
        Assert.assertEquals(Test1.ZERO, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertEquals(Test1.ZERO, field1.getValueAsEnumeration(Test1.class));
        Assert.assertEquals(Test1.ZERO.toString(), field1.getValueAsString(null));

        field1.setValue(null);

        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertNull(field1.getValueAsEnumeration(Test1.class));
        Assert.assertNull(field1.getValueAsString(null));

        field1.setValueAsEnumeration(null);

        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertNull(field1.getValueAsEnumeration(Test1.class));
        Assert.assertNull(field1.getValueAsString(null));

        field1.setValueAsString(null, null);

        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.ONE));
        Assert.assertNull(field1.getValueAsEnumeration(Test1.class));
        Assert.assertNull(field1.getValueAsString(null));

        field1.setBaseValue("100");

        Assert.assertEquals(Test1.ONE, field1.getValueAsEnumeration(Test1.ONE));

        EnumerationProviderUtil.setEnumerationProviders();

    }

    /**
     * This unit test ensures a runtime exception is thrown when retrieving an
     * enumeration thats out of bounds.
     * 
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testEnumerationFieldFailureAsEnumeration() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum1", Test1.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationField field1 = (CoalesceEnumerationField) record.getFieldByName("enum1");

        field1.setBaseValue("100");
        field1.getValueAsEnumeration(Test1.class);

        EnumerationProviderUtil.setEnumerationProviders();

    }

    /**
     * This unit test ensures a runtime exception is thrown when retrieving an
     * enumeration as a string thats out of bounds.
     * 
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testEnumerationFieldFailureAsString() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum1", Test1.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationField field1 = (CoalesceEnumerationField) record.getFieldByName("enum1");

        field1.setBaseValue("100");
        field1.getValueAsString(null);

        EnumerationProviderUtil.setEnumerationProviders();

    }

    /**
     * This unit test ensures that the getters and setters of the enumeration
     * field work as intended.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationListField() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new ConstraintEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", Test2.class);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setValue(new int[] {
                0, 2
        });

        Assert.assertEquals(0, field.getValue()[0]);
        Assert.assertEquals(2, field.getValue()[1]);
        Assert.assertEquals("0,2", field.getBaseValue());

        CoalesceValidator validator = new CoalesceValidator();
        Map<String, String> results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "2", Test2.class.getName()),
                            results.get(field.getKey()));

        field.setValue(new int[] {
                0, 1
        });

        results = validator.validate(null, entity, template);

        Assert.assertEquals(0, results.size());

        field.addValue(new int[] {
                1, 1
        });

        Assert.assertEquals(4, field.getValue().length);

        field.addValueAsString(null, new String[] {
            Test2.HELLO.toString()
        });

        Assert.assertEquals(5, field.getValue().length);

        String[] values = field.getValueAsList(null);

        Assert.assertEquals(Test2.HELLO.toString(), values[0]);
        Assert.assertEquals(Test2.WORLD.toString(), values[1]);
        Assert.assertEquals(Test2.WORLD.toString(), values[2]);
        Assert.assertEquals(Test2.WORLD.toString(), values[3]);
        Assert.assertEquals(Test2.HELLO.toString(), values[4]);

        field.setValueAsEnumeration(new Test2[] {
            Test2.WORLD
        });

        Assert.assertEquals(1, field.getValue().length);

        values = field.getValueAsList(null);

        Assert.assertEquals(Test2.WORLD.toString(), values[0]);

        field.addValueAsEnumeration(new Test2[] {
            Test2.HELLO
        });

        Assert.assertEquals(2, field.getValue().length);

        Test2[] enums = field.getValueAsEnumeration(Test2.class);

        Assert.assertEquals(Test2.WORLD, enums[0]);
        Assert.assertEquals(Test2.HELLO, enums[1]);

        EnumerationProviderUtil.setEnumerationProviders();
    }

    /**
     * This unit test ensures a runtime exception is thrown when retrieving an
     * enumeration thats out of bounds.
     * 
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testEnumerationListFieldFailureAsEnumeration() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new ConstraintEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", Test2.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setBaseValue("100,102");
        field.getValueAsEnumeration(Test2.class);

    }

    /**
     * This unit test ensures a runtime exception is thrown when retrieving an
     * enumeration thats out of bounds.
     * 
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testEnumerationListFieldFailureAsString() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new ConstraintEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", Test2.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setBaseValue("100,102");
        field.getValueAsString(null);

    }

    /**
     * Ensures that adding and setting null values works correctly.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationListFieldNull() throws Exception
    {

        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl());

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", Test2.class);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setValueAsList(null, new String[] {
            Test2.HELLO.toString()
        });

        Assert.assertEquals(1, field.getValue().length);
        Assert.assertEquals(Test2.HELLO.ordinal(), field.getValue()[0]);

        field.addValueAsString(null, new String[] {
            Test2.WORLD.toString()
        });

        Assert.assertEquals(2, field.getValue().length);
        Assert.assertEquals(Test2.HELLO.ordinal(), field.getValue()[0]);
        Assert.assertEquals(Test2.WORLD.ordinal(), field.getValue()[1]);

        field.addValueAsString(null, null);

        Assert.assertEquals(2, field.getValue().length);
        Assert.assertEquals(Test2.HELLO.ordinal(), field.getValue()[0]);
        Assert.assertEquals(Test2.WORLD.ordinal(), field.getValue()[1]);

        field.addValue(null);

        Assert.assertEquals(2, field.getValue().length);
        Assert.assertEquals(Test2.HELLO.ordinal(), field.getValue()[0]);
        Assert.assertEquals(Test2.WORLD.ordinal(), field.getValue()[1]);

        field.setValueAsString(null, null);

        Assert.assertEquals(0, field.getValue().length);

        field.setValueAsString(null, Test2.HELLO.toString());

        Assert.assertEquals(1, field.getValue().length);
        Assert.assertEquals(Test2.HELLO.ordinal(), field.getValue()[0]);

        field.setValue(null);

        Assert.assertEquals(0, field.getValue().length);

        EnumerationProviderUtil.setEnumerationProviders();

    }

    /**
     * Verifies setting and getting enumeration list as strings.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationListAsString() throws Exception
    {
        EnumerationProviderUtil.setEnumerationProviders(new PropertyEnumerationProviderImpl("src/test/resources"));

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", "valid");

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setBaseValue("0,1");

        Assert.assertEquals("0,1", field.getBaseValue());
        Assert.assertEquals("HELLO" + CoalesceEnumerationListField.SEPERATOR + "WORLD", field.getValueAsString());

        field.setValueAsString("WORLD" + CoalesceEnumerationListField.SEPERATOR + "HELLO");

        Assert.assertEquals("1,0", field.getBaseValue());
        Assert.assertEquals("WORLD" + CoalesceEnumerationListField.SEPERATOR + "HELLO", field.getValueAsString());

    }

    /**
     * This test verifies validating a Enumeration List field with a mandatory
     * constraint.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationListFieldMandatory() throws Exception
    {

        EnumerationProviderUtil.setEnumerationProviders(new PropertyEnumerationProviderImpl("src/test/resources"));

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition fd = CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", "valid");
        CoalesceConstraint.createMandatory(fd, "mandatory", false);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        CoalesceValidator validator = new CoalesceValidator();

        Map<String, String> results;

        results = validator.validate(null, entity, template);
        Assert.assertTrue(results.containsKey(field.getKey()));
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field.getName()),
                            results.get(field.getKey()));

        // Sets two positions
        field.setValueAsString("HELLO WORLD");

        results = validator.validate(null, entity, template);
        Assert.assertEquals(2, field.getValue().length);
        Assert.assertFalse(results.containsKey(field.getKey()));

        // Setting the field to null (Valid entry but should trigger mandatory constraint)
        field.setValueAsString(null);

        results = validator.validate(null, entity, template);
        Assert.assertEquals(0, field.getValue().length);
        Assert.assertTrue(results.containsKey(field.getKey()));
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field.getName()),
                            results.get(field.getKey()));

        // Invalid Position
        field.setBaseValue("10");

        results = validator.validate(null, entity, template);
        Assert.assertEquals(1, field.getValue().length);
        Assert.assertTrue(results.containsKey(field.getKey()));
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, 10, "valid"),
                            results.get(field.getKey()));

        // Empty String (Valid)
        field.setValueAsString("");

        results = validator.validate(null, entity, template);
        Assert.assertEquals(1, field.getValue().length);
        Assert.assertFalse(results.containsKey(field.getKey()));

    }

}
