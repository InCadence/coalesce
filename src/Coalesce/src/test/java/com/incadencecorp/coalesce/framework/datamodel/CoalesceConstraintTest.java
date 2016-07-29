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

        assertEquals("Invalid Input (D)", results.get(field1.getKey()));
        assertEquals("Invalid Input (d)", results.get(field2.getKey()));

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
        assertEquals("Invalid Input (Value excceeds the min)", results.get(fieldInvalidMin.getKey()));
        assertEquals("Invalid Input (Value excceeds the max)", results.get(fieldInvalidMax.getKey()));
        assertEquals("Invalid Input (Value excceeds the max)", results.get(fieldList.getKey()));

        fieldList.setTypedValue(new long[] {
                1, 3, 4, -2
        });

        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals("Invalid Input (Value excceeds the min)", results.get(fieldList.getKey()));

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

        assertEquals("Empty Mandatory Field", results.get(field1.getKey()));

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
        assertEquals("Empty Mandatory Field", results.get(field2.getKey()));
        assertEquals("Empty Mandatory Field", results.get(field3.getKey()));
        assertEquals("Empty Mandatory Field", results.get(field4.getKey()));

        // Set Values
        field2.setValue(null);
        field3.setValue(null);
        field4.setValue(null);

        // Validate
        results = new CoalesceValidator().validate(null, entity, CoalesceEntityTemplate.create(entity));

        assertEquals(null, results.get(field1.getKey()));
        assertEquals("Empty Mandatory Field", results.get(field2.getKey()));
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
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field1", ETest.class);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field2", ETest.class);
        CoalesceFieldDefinition.createEnumerationFieldDefinition(recordset, "field3", ETest.class);
        CoalesceFieldDefinition.createEnumerationListFieldDefinition(recordset, "field4", ETest.class);

        // Create New Record
        CoalesceRecord record = recordset.addNew();

        // Get Fields
        CoalesceField<?> field1 = record.getFieldByName("field1");
        CoalesceField<?> field2 = record.getFieldByName("field2");
        CoalesceField<?> field3 = record.getFieldByName("field3");
        CoalesceField<?> field4 = record.getFieldByName("field4");

        // Set Values
        field1.setTypedValue(1);
        field2.setTypedValue(10);
        field3.setTypedValue(11);
        field4.setTypedValue(new int[] {
                0, 1, 10
        });

        EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl(), new ConstraintEnumerationProviderImpl());

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
