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

package com.incadencecorp.coalesce.framework.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.CodeEnumerationProvider;

/**
 * Verifies the exclusion logic in the validator.
 * 
 * @author n78554
 */
public class CoalesceValidatorExclusionsTest {

    /**
     * Verifies that if a field's data type is changed from the template it will
     * fail validation. Excluding the field will allow it to pass.
     * 
     * @throws Exception
     */
    @Test
    public void testExclusion() throws Exception
    {

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        CoalesceStringField field = record.getStringField();
        field.setValue("Hello World");

        // Create Constraint
        CoalesceConstraint.createMandatory(entity.getRecordset1().getFieldDefinition(field.getName()), "test");

        // Create Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        // Create Validator
        CoalesceValidator validator = new CoalesceValidator();

        // Verify (Success)
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Change Data Type
        field.setAttribute("datatype", ECoalesceFieldDataTypes.BOOLEAN_TYPE.getLabel());

        Map<String, String> results = validator.validate(null, entity, template);

        // Verify (Failure)
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(String.format(CoalesceErrors.INVALID_DATA_TYPE,
                                          field.getKey(),
                                          field.getDataType(),
                                          field.getEntity().getKey(),
                                          entity.getRecordset1().getFieldDefinition(field.getName()).getDataType()),
                            results.get(field.getKey()));

        // Create Validator w/ Exclusions
        validator = new CoalesceValidator(Arrays.asList(new String[] {
            field.getParent().getParent().getName() + "." + field.getName()
        }));

        // Verify (Success)
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Create Enumeration
        Map<String, List<String>> enumerations = new HashMap<String, List<String>>();

        enumerations.put(CoalesceValidator.ENUM_EXCLUSIONS, Arrays.asList(new String[] {
            field.getParent().getParent().getName() + "." + field.getName()
        }));

        EnumerationProviderUtil.setEnumerationProviders(new CodeEnumerationProvider(enumerations));

        validator = new CoalesceValidator();

        // Verify (Success)
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Create Enumeration
        enumerations = new HashMap<String, List<String>>();

        enumerations.put(CoalesceValidator.ENUM_EXCLUSIONS, Arrays.asList(new String[] {
            field.getParent().getParent().getName() + "." + CoalesceValidator.WILD_CARD
        }));

        EnumerationProviderUtil.setEnumerationProviders(new CodeEnumerationProvider(enumerations));

        validator = new CoalesceValidator();

        // Verify (Success)
        Assert.assertEquals(0, validator.validate(null, entity, template).size());

        // Create Enumeration
        enumerations = new HashMap<String, List<String>>();

        enumerations.put(CoalesceValidator.ENUM_EXCLUSIONS, Arrays.asList(new String[] {
            CoalesceValidator.WILD_CARD
        }));

        EnumerationProviderUtil.setEnumerationProviders(new CodeEnumerationProvider(enumerations));

        validator = new CoalesceValidator();

        // Verify (Success)
        Assert.assertEquals(0, validator.validate(null, entity, template).size());
        
        // Create Enumeration
        enumerations = new HashMap<String, List<String>>();

        enumerations.put(CoalesceValidator.ENUM_EXCLUSIONS, Arrays.asList(new String[] {
            "test." + CoalesceValidator.WILD_CARD
        }));

        EnumerationProviderUtil.setEnumerationProviders(new CodeEnumerationProvider(enumerations));

        validator = new CoalesceValidator();

        // Verify (Failure)
        Assert.assertEquals(1, validator.validate(null, entity, template).size());

    }

}
