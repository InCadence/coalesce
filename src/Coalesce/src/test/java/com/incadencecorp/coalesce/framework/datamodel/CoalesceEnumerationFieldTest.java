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

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceAttributes;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;
import com.vividsolutions.jts.util.Assert;

/**
 * These unit test ensure that the enumeration field works as expected.
 * 
 * @author n78554
 *
 */
public class CoalesceEnumerationFieldTest {

    private enum test
    {
        ZERO, ONE, TWO;
    }

    private enum test2
    {
        HELLO, WORLD;
    }

    private enum test3
    {
        TWO, ONE, ZERO;
    }

    /**
     * Initializes required utility classes.
     */
    @BeforeClass
    public static void initialize()
    {
        EnumerationProviderUtil.setEnumerationProviders(new ConstraintEnumerationProviderImpl());
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
        Assert.equals("MyEnum", def.getConstraints("enum").getValue());
        Assert.equals("enum", def.getName());
        Assert.equals(ECoalesceFieldDataTypes.ENUMERATION_TYPE, def.getDataType());

        // Create Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        CoalesceEntity created = template.createNewEntity();

        CoalesceFieldDefinition object = (CoalesceFieldDefinition) created.getCoalesceObjectForNamePath(def.getNamePath());

        // Verify Creation
        Assert.equals("MyEnum", object.getConstraints("enum").getValue());
        Assert.equals("enum", object.getName());
        Assert.equals(ECoalesceFieldDataTypes.ENUMERATION_TYPE,
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
        Assert.equals("MyEnum", def.getConstraints("enum").getValue());
        Assert.equals("enum", def.getName());
        Assert.equals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE, def.getDataType());

        // Create Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);
        CoalesceEntity created = template.createNewEntity();

        CoalesceFieldDefinition object = (CoalesceFieldDefinition) created.getCoalesceObjectForNamePath(def.getNamePath());

        // Verify Creation
        Assert.equals("MyEnum", object.getConstraints("enum").getValue());
        Assert.equals("enum", object.getName());
        Assert.equals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE,
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

        Assert.isTrue(provider.handles(null, test.class.getName()));
        Assert.equals(0, provider.toPosition(null, test.class.getName(), test.ZERO.toString()));
        Assert.equals(1, provider.toPosition(null, test.class.getName(), test.ONE.toString()));
        Assert.equals(2, provider.toPosition(null, test.class.getName(), test.TWO.toString()));

        Assert.equals(test.ZERO.toString(), provider.toString(null, test.class.getName(), 0));
        Assert.equals(test.ONE.toString(), provider.toString(null, test.class.getName(), 1));
        Assert.equals(test.TWO.toString(), provider.toString(null, test.class.getName(), 2));

        Assert.isTrue(provider.isValid(null, test.class.getName(), 0));
        Assert.isTrue(provider.isValid(null, test.class.getName(), 1));
        Assert.isTrue(provider.isValid(null, test.class.getName(), 2));
        Assert.isTrue(!provider.isValid(null, test.class.getName(), 6));

        Assert.isTrue(provider.isValid(null, test.class.getName(), test.ZERO.toString()));
        Assert.isTrue(!provider.isValid(null, test.class.getName(), "HELLO WORLD"));

        Assert.isTrue(provider.isValid(null, test2.class.getName(), test2.HELLO.toString()));

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
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum1", test.class);
        fd = CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum2", test2.class);
        fd = CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "enum3", test3.class);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationField field1 = (CoalesceEnumerationField) record.getFieldByName("enum1");
        CoalesceEnumerationField field2 = (CoalesceEnumerationField) record.getFieldByName("enum2");
        CoalesceEnumerationField field3 = (CoalesceEnumerationField) record.getFieldByName("enum3");

        field1.setValue(2);
        field2.setValue(1);
        field3.setValue(2);

        Assert.equals(2, field1.getValue());
        Assert.equals("2", field1.getBaseValue());

        Assert.equals(1, field2.getValue());
        Assert.equals("1", field2.getBaseValue());

        Assert.equals(2, field3.getValue());
        Assert.equals("2", field3.getBaseValue());

        CoalesceValidator validator = new CoalesceValidator();
        Map<String, String> results = validator.validate(null, entity, template);

        Assert.equals(0, results.size());

        field1.setValue(10);

        results = validator.validate(null, entity, template);

        Assert.equals(1, results.size());
        Assert.equals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "10", test.class.getName()),
                      results.get(field1.getKey()));

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
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "test");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "enum", test2.class);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceRecord record = recordset.addNew();

        CoalesceEnumerationListField field = (CoalesceEnumerationListField) record.getFieldByName("enum");

        field.setValue(new int[] {
                0, 2
        });

        Assert.equals(0, field.getValue()[0]);
        Assert.equals(2, field.getValue()[1]);
        Assert.equals("0,2", field.getBaseValue());

        CoalesceValidator validator = new CoalesceValidator();
        Map<String, String> results = validator.validate(null, entity, template);

        Assert.equals(1, results.size());
        Assert.equals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "2", test2.class.getName()),
                      results.get(field.getKey()));

        field.setValue(new int[] {
                0, 1
        });

        results = validator.validate(null, entity, template);

        Assert.equals(0, results.size());

    }

}
