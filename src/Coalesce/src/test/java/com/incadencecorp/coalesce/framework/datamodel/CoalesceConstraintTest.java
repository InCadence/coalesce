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

package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;
import com.incadencecorp.coalesce.framework.validation.CustomValidatorImpl;

/**
 * These unit test ensure that defining constraints work properly.
 * 
 * @author n78554
 */
public class CoalesceConstraintTest {

    /**
     * Ensures that specifying definitions from different records will throw an
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testListSizeEqualsDifferentParentFailure() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset1 = CoalesceRecordset.create(section, "rs1");
        CoalesceRecordset recordset2 = CoalesceRecordset.create(section, "rs2");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd1;
        CoalesceFieldDefinition fd2;

        fd1 = CoalesceFieldDefinition.create(recordset1, "field1", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        fd2 = CoalesceFieldDefinition.create(recordset2, "field2", ECoalesceFieldDataTypes.STRING_LIST_TYPE);

        CoalesceConstraint.createListSizeEquals("Size Constraint", fd1, fd2);

    }

    /**
     * Ensures that specifying non-list type definitions will throw an
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = ClassCastException.class)
    public void testListSizeEqualsNonListTypeFailure() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset1 = CoalesceRecordset.create(section, "rs1");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd1;
        CoalesceFieldDefinition fd2;

        fd1 = CoalesceFieldDefinition.create(recordset1, "field1", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        fd2 = CoalesceFieldDefinition.create(recordset1, "field2", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceConstraint.createListSizeEquals("Size Constraint", fd1, fd2);

    }

    /**
     * Ensures that specifying non-list type definitions will throw an
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = ClassCastException.class)
    public void testListSizeNonListTypeFailure() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset1 = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset1, "field1", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceConstraint.createListSize(fd, "Size Constraint", 5);

    }

    /**
     * Ensures that specifying non-integer type definitions will throw an
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = ClassCastException.class)
    public void testListSizeNonIntegerTypeFailure() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset1 = CoalesceRecordset.create(section, "rs1");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd1;
        CoalesceFieldDefinition fd2;

        fd1 = CoalesceFieldDefinition.create(recordset1, "field1", ECoalesceFieldDataTypes.STRING_TYPE);
        fd2 = CoalesceFieldDefinition.create(recordset1, "field2", ECoalesceFieldDataTypes.STRING_LIST_TYPE);

        CoalesceConstraint.createListSize(fd1, "Size Constraint", fd2);

    }

    /**
     * This unit test exercises
     * {@link CoalesceConstraint#createListSize(CoalesceFieldDefinition, String, int)}
     * 
     * @throws Exception
     */
    @Test
    public void testListSize() throws Exception
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;
        fd = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        CoalesceConstraint.createListSize(fd, "Size Constraint", 3);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceRecord record = recordset.addNew();

        CoalesceValidator validator = new CoalesceValidator();

        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        CoalesceStringListField field = (CoalesceStringListField) record.getFieldByName("field1");

        field.setArray(new String[] {
                "A", "B"
        });

        Map<String, String> results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field.getName()),
                            results.get(field.getKey()));

        field.setArray(new String[] {
                "A", "B", "C"
        });

        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        field.setArray(new String[] {
                "A", "B", "C", "B"
        });

        results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field.getName()),
                            results.get(field.getKey()));

    }

    /**
     * This unit test exercises
     * {@link CoalesceConstraint#createListSizeEquals(String, CoalesceFieldDefinition...)}
     * 
     * @throws Exception
     */
    @Test
    public void testListSizeEquals() throws Exception
    {
        CoalesceValidator validator = new CoalesceValidator();

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd1;
        CoalesceFieldDefinition fd2;
        CoalesceFieldDefinition fd3;

        fd1 = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        fd2 = CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        fd3 = CoalesceFieldDefinition.create(recordset, "field3", ECoalesceFieldDataTypes.STRING_LIST_TYPE);

        CoalesceConstraint.createListSizeEquals("Size Constraint", fd1, fd2, fd3);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceStringListField field1 = (CoalesceStringListField) record.getFieldByName("field1");
        CoalesceStringListField field2 = (CoalesceStringListField) record.getFieldByName("field2");
        CoalesceStringListField field3 = (CoalesceStringListField) record.getFieldByName("field3");

        // Verify if not value is set it will pass
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Verify as long as the restraining field is not set validation will
        // pass.
        field2.setValue(new String[] {
                "1", "2"
        });
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Verify setting the restraining field will fail the validation
        field1.setValue(new String[] {
            "1"
        });

        Map<String, String> results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field2.getName()),
                            results.get(field1.getKey()));

        field2.setValue(new String[] {
            "B"
        });

        results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field3.getName()),
                            results.get(field1.getKey()));

        field3.setValue(new String[] {
            "A"
        });

        Assert.assertEquals(0, validator.validate(null, entity, template).size());

    }

    /**
     * This unit test exercises
     * {@link CoalesceConstraint#createListSize(CoalesceFieldDefinition, String, CoalesceFieldDefinition...)}
     * 
     * @throws Exception
     */
    @Test
    public void testListSizeField() throws Exception
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;
        CoalesceFieldDefinition fd1;
        CoalesceFieldDefinition fd2;

        fd = CoalesceFieldDefinition.create(recordset, "fieldConstraint", ECoalesceFieldDataTypes.INTEGER_TYPE);
        fd1 = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        fd2 = CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_LIST_TYPE);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceIntegerField field = (CoalesceIntegerField) record.getFieldByName("fieldConstraint");
        CoalesceStringListField field1 = (CoalesceStringListField) record.getFieldByName("field1");
        CoalesceStringListField field2 = (CoalesceStringListField) record.getFieldByName("field2");

        field.setValue(5);
        field1.setValue(new String[] {
            "1"
        });

        CoalesceConstraint.createListSize(fd, "Size Constraint", fd1, fd2);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        CoalesceValidator validator = new CoalesceValidator();

        Map<String, String> results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field1.getName()),
                            results.get(field.getKey()));

        field1.setValue(new String[] {
                "1", "2", "3", "4", "5"
        });

        results = validator.validate(null, entity, template);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field2.getName()),
                            results.get(field.getKey()));

        field2.setValue(new String[] {
                "1", "2", "3", "4", "5"
        });

        results = validator.validate(null, entity, template);

        Assert.assertEquals(0, results.size());

    }

    /**
     * This unit test adds regular express constraints and verifies that the
     * validator reports appropriate constraint violations.
     * 
     * @throws Exception
     */
    @Test
    public void testRegEx() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createRegEx(fd, "v1", "[D]");
        CoalesceConstraint.createRegEx(fd, "v2", "[d]");

        fd = CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createRegEx(fd, "v1", "[D]");
        CoalesceConstraint.createRegEx(fd, "v2", "[d]");

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field1 = record.getFieldByName("field1");
        CoalesceField<?> field2 = record.getFieldByName("field2");

        // Set Values
        field1.setTypedValue("D");
        field2.setTypedValue("d");

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(String.format(CoalesceErrors.INVALID_INPUT, field1.getValue()), results.get(field1.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_INPUT, field2.getValue()), results.get(field2.getKey()));

    }

    /**
     * This unit test adds min and max constraints and verifies that the
     * validator reports appropriate constraint violations.
     * 
     * @throws Exception
     */
    @Test
    public void testMinMax() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset, "testValid", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceConstraint.createMax(fd, "v1", 5, true);
        CoalesceConstraint.createMin(fd, "v2", -1, true);

        fd = CoalesceFieldDefinition.create(recordset, "testInvalidMin", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceConstraint.createMax(fd, "v1", 5, true);
        CoalesceConstraint.createMin(fd, "v2", -1, false); // Not Inclusive

        fd = CoalesceFieldDefinition.create(recordset, "testInvalidMax", ECoalesceFieldDataTypes.LONG_TYPE);
        CoalesceConstraint.createMax(fd, "v1", 5, true);
        CoalesceConstraint.createMin(fd, "v2", -1, true);

        fd = CoalesceFieldDefinition.create(recordset, "testListMax", ECoalesceFieldDataTypes.LONG_LIST_TYPE);
        CoalesceConstraint.createMax(fd, "v1", 5, true);
        CoalesceConstraint.createMin(fd, "v2", -1, true);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> fieldValid = record.getFieldByName("testValid");
        CoalesceField<?> fieldInvalidMin = record.getFieldByName("testInvalidMin");
        CoalesceField<?> fieldInvalidMax = record.getFieldByName("testInvalidMax");
        CoalesceField<?> fieldList = record.getFieldByName("testListMax");

        // Set Values
        fieldValid.setTypedValue(5);
        fieldInvalidMin.setTypedValue(-1.0);
        fieldInvalidMax.setTypedValue(10l);
        fieldList.setTypedValue(new long[] {
                1, 3, 4, 10
        });

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(fieldValid.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "min", fieldInvalidMin.getName()),
                     results.get(fieldInvalidMin.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "max", fieldInvalidMax.getName()),
                     results.get(fieldInvalidMax.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "max", fieldList.getName()),
                     results.get(fieldList.getKey()));

        fieldList.setTypedValue(new long[] {
                1, 3, 4, -2
        });

        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "min", fieldList.getName()),
                     results.get(fieldList.getKey()));

        fieldList.setTypedValue(new long[] {
                1, 3, 4
        });

        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(fieldList.getKey()));

    }

    /**
     * This test sets the field to null which creates an empty string.
     * 
     * @throws Exception
     */
    @Test
    public void testMandatoryDoubleWithEmptyValue() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        CoalesceConstraint.createMax(fd, "v1", 5, true);
        CoalesceConstraint.createMin(fd, "v2", -1, false); // Not Inclusive
        CoalesceConstraint.createMandatory(fd, "m1");

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field1 = record.getFieldByName("field1");

        field1.setValue(null);

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field1.getName()), results.get(field1.getKey()));

        // Set Values
        field1.setTypedValue(4.2);

        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field1.getKey()));

    }

    /**
     * This unit test adds mandatory constraints and verifies that the validator
     * reports appropriate constraint violations.
     * 
     * @throws Exception
     */
    @Test
    public void testMandatory() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createMandatory(fd, "v1");

        fd = CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createMandatory(fd, "v1");

        fd = CoalesceFieldDefinition.create(recordset, "field3", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createMandatory(fd, "v1", true);

        fd = CoalesceFieldDefinition.create(recordset, "field4", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceConstraint.createMandatory(fd, "v1", true);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field1 = record.getFieldByName("field1");
        CoalesceField<?> field2 = record.getFieldByName("field2");
        CoalesceField<?> field3 = record.getFieldByName("field3");
        CoalesceDateTimeField field4 = (CoalesceDateTimeField) record.getFieldByName("field4");

        // Set Values
        field1.setTypedValue("Hello World");

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field1.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field2.getName()), results.get(field2.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field3.getName()), results.get(field3.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field4.getName()), results.get(field4.getKey()));

        // Set Values
        field2.setValue(null);
        field3.setValue(null);
        field4.setValue(null);

        // Validate
        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field1.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field2.getName()), results.get(field2.getKey()));
        assertEquals(null, results.get(field3.getKey()));
        assertEquals(null, results.get(field4.getKey()));

    }

    /**
     * This unit test adds custom constraints and verifies that the validator
     * reports appropriate constraint violations.
     * 
     * @throws Exception
     */
    @Test
    public void testCustom() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createCustom(fd, "v1", "Hello World");

        fd = CoalesceFieldDefinition.create(recordset, "field3", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createCustom(fd, "v1", CustomValidatorImpl.class.getName());

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field2 = record.getFieldByName("field2");
        CoalesceField<?> field3 = record.getFieldByName("field3");

        // Set Values
        field2.setTypedValue("Hello World");
        field3.setTypedValue("Bad Value");

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals("Validator Not Found: Hello World", results.get(field2.getKey()));
        assertEquals("test", results.get(field3.getKey()));

    }

    /**
     * This unit test adds enumeration constraints and verifies that the
     * validator reports appropriate constraint violations.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumeration() throws Exception
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("Constraint", "UNIT_TEST", "1.0", "", "");
        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset recordset = CoalesceRecordset.create(section, "rs");

        // Create Field Definitions w/ Constraints
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field1", ETest.class, null);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field2", ETest.class, null);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field3", ETest.class, ETest.A);
        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "field4", ETest.class);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field1 = record.getFieldByName("field1");
        CoalesceField<?> field2 = record.getFieldByName("field2");
        CoalesceField<?> field3 = record.getFieldByName("field3");
        CoalesceField<?> field4 = record.getFieldByName("field4");

        Assert.assertNull(field1.getBaseValue());
        Assert.assertNull(field2.getBaseValue());
        Assert.assertEquals(ETest.A.ordinal(), Integer.parseInt(field3.getBaseValue()));
        Assert.assertNull(field4.getBaseValue());
        
        // Set Values
        field1.setTypedValue(1);
        field2.setTypedValue(10);
        field3.setTypedValue(11);
        field4.setTypedValue(new int[] {
                0, 1, 10
        });

        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl(),
                                                        new ConstraintEnumerationProviderImpl());

        // Validate
        Map<String, String> results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field1.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "10", ETest.class.getName()),
                     results.get(field2.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "11", ETest.class.getName()),
                     results.get(field3.getKey()));
        assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, "10", ETest.class.getName()),
                     results.get(field4.getKey()));

        field4.setTypedValue(new int[] {
                0, 1
        });

        // Validate
        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field4.getKey()));

    }

    /**
     * This test ensure that the conversion to regex back to a list of values
     * works as expected.
     * 
     * @throws Exception
     */
    @Test
    public void testHelperMethods() throws Exception
    {
        String regex = CoalesceConstraint.enumToRegEx(ETest.class);

        List<String> values = CoalesceConstraint.regExToValues(regex);

        Assert.assertEquals(ETest.values().length, values.size());

        int ii = 0;

        for (ETest value : ETest.values())
        {
            Assert.assertEquals(value.toString(), values.get(ii++));
        }
    }

    private enum ETest
    {
        A, B, C;
    }

}
