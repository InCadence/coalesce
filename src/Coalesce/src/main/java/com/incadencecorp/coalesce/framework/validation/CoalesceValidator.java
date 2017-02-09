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

package com.incadencecorp.coalesce.framework.validation;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;
import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ConstraintType;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ConstraintEnumerationProviderImpl;
import com.incadencecorp.coalesce.framework.validation.api.ICustomValidator;

/**
 * @author n78554
 *
 */
public class CoalesceValidator extends CoalesceIterator {

    private Map<String, String> violations = new HashMap<String, String>();
    private CoalesceEntity entity;
    private Principal principal;

    private final List<String> exclusions = new ArrayList<String>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceValidator.class);
    
    /**
     * Wild card used for specifying exclusions. '<record set name>.*' or '*'.  
     */
    public static final String WILD_CARD = "*";

    /**
     * If no exclusions are specified it will attempt to load them from this
     * enumeration.
     */
    public static final String ENUM_EXCLUSIONS = "VALIDATION_EXCLUSIONS";

    /**
     * Default Constructor
     */
    public CoalesceValidator()
    {
        this(loadExclusions());
    }

    private static List<String> loadExclusions()
    {
        List<String> results;

        try
        {
            results = EnumerationProviderUtil.getValues(null, ENUM_EXCLUSIONS);
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            results = null;
        }

        return results;
    }

    /**
     * Constructs a validator with a list of exclusions.
     * 
     * @param exclusions
     */
    public CoalesceValidator(List<String> exclusions)
    {
        if (exclusions != null)
        {
            this.exclusions.addAll(exclusions);
        }
    }

    /**
     * Calls {@link #validate(Principal, CoalesceEntity, Map)} passing
     * <code>null</coded> as the principle.
     * 
     * @param entity
     * @param constraints
     * @return a map of errors where the key is the field's key and the value is
     *         the error; If the map is empty then there are no violations.
     * @deprecated Use {@link #validate(Principal, CoalesceEntity, Map)}
     */
    public Map<String, String> validate(CoalesceEntity entity, Map<String, List<CoalesceFieldDefinition>> constraints)
    {
        return validate(null, entity, constraints);
    }

    /**
     * Validates an entity against a map of constraints; where the key is the
     * full path to the recordset and the value is a list of definitions that
     * contain constraints.
     * 
     * @param principal
     * @param entity
     * @param constraints
     * @return a map of errors where the key is the field's key and the value is
     *         the error; If the map is empty then there are no violations.
     */
    public Map<String, String> validate(Principal principal,
                                        CoalesceEntity entity,
                                        Map<String, List<CoalesceFieldDefinition>> constraints)
    {

        this.violations.clear();
        this.principal = principal;
        this.entity = entity;

        for (Map.Entry<String, List<CoalesceFieldDefinition>> entry : constraints.entrySet())
        {

            // Get Record Set from Actual Entity
            CoalesceRecordset actutal = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(entry.getKey());

            // Record Set Exists?
            if (actutal != null)
            {
                // Yes; Validate Each Record
                for (CoalesceRecord record : actutal.getRecords())
                {
                    validateRecord(record, entry.getValue());
                }
            }
            else
            {
                validateNoMandatory(entry.getKey(), entry.getValue());
            }

        }

        return violations;

    }

    /**
     * Calls
     * {@link #validate(Principal, CoalesceEntity, CoalesceEntityTemplate)}
     * passing <code>null</coded> as the principle.
     * 
     * @param entity
     * @param template
     * @return a map of errors where the key is the field's key and the value is
     *         the error; If the map is empty then there are no violations.
     * @deprecated Use
     *             {@link #validate(Principal, CoalesceEntity, CoalesceEntityTemplate)}
     */
    public Map<String, String> validate(CoalesceEntity entity, CoalesceEntityTemplate template)
    {
        return validate(null, entity, template);
    }

    /**
     * Validates the given entity against its template.
     * 
     * @param principal
     * @param entity
     * @param template
     * @return a map of errors where the key is the field's key and the value is
     *         the error; If the map is empty then there are no violations.
     */
    public Map<String, String> validate(Principal principal, CoalesceEntity entity, CoalesceEntityTemplate template)
    {

        this.violations.clear();
        this.principal = principal;

        if (template != null)
        {
            this.entity = entity;

            CoalesceEntity templateEntity = template.createNewEntity();

            super.processActiveElements(templateEntity);
        }

        return violations;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
        // Get Record Set from Actual Entity
        CoalesceRecordset actutal = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(recordset.getNamePath());

        if (actutal != null)
        {
            // For Each Record
            for (CoalesceRecord record : actutal.getRecords())
            {
                validateRecord(record, recordset.getFieldDefinitions());
            }
        }
        else
        {
            validateNoMandatory(recordset.getNamePath(), recordset.getFieldDefinitions());
        }

        // Don't Process Children
        return false;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void validateNoMandatory(String path, List<CoalesceFieldDefinition> definitions)
    {
        // For Each Definition
        for (CoalesceFieldDefinition definition : definitions)
        {
            for (CoalesceConstraint constraint : definition.getConstraints())
            {
                if (constraint.getConstraintType() == ConstraintType.MANDATORY)
                {
                    violations.put(path, String.format(CoalesceErrors.INVALID_MANDOTORY_RECORDSET,
                                                       definition.getParent().getName()));
                    break;
                }
            }
        }
    }

    private void validateRecord(CoalesceRecord record, List<CoalesceFieldDefinition> definitions)
    {
        // For Each Definition
        for (CoalesceFieldDefinition definition : definitions)
        {
            // Has Constraints?
            if (!definition.getConstraints().isEmpty())
            {
                // Get Field
                CoalesceField<?> field = record.getFieldByName(definition.getName());

                // Has Field?
                if (field != null)
                {
                    String result = null;

                    // Verify File Type = Definition
                    if (field.getDataType() != definition.getDataType())
                    {
                        result = String.format(CoalesceErrors.INVALID_DATA_TYPE,
                                               field.getKey(),
                                               field.getDataType(),
                                               field.getEntity().getKey(),
                                               definition.getDataType());
                    }
                    else
                    {
                        // Apply Each Constraint to Field
                        for (CoalesceConstraint constraint : definition.getConstraints())
                        {
                            switch (constraint.getConstraintType()) {
                            case MANDATORY:
                                result = validateMandatory(field,
                                                           Boolean.parseBoolean(constraint.getAttribute("allowEmpty")));
                                break;
                            case CUSTOM:
                                result = validateCustom(field, constraint);
                                break;
                            case REGEX:
                                result = validateRegEx(field, constraint);
                                break;
                            case MAX:
                                result = validateMax(field, constraint);
                                break;
                            case MIN:
                                result = validateMin(field, constraint);
                                break;
                            case ENUMERATION:
                                result = validateEnumeration(field, constraint);
                                break;
                            case SIZE:
                                result = validateSize(field, constraint);
                                break;
                            }

                            if (result != null)
                            {
                                break;
                            }
                        }
                    }

                    if (result != null)
                    {
                        if (LOGGER.isInfoEnabled())
                        {
                            LOGGER.warn("Field ({}.{}) is invalid beacuse ({})",
                                        record.getParent().getName(),
                                        field.getName(),
                                        result);
                        }

                        if (!excludeValidation(field))
                        {
                            violations.put(field.getKey(), result);
                        }
                    }
                }
            }
        }
    }

    private boolean excludeValidation(CoalesceField<?> field)
    {
        String recordsetName = field.getParent().getParent().getName();

        return exclusions.contains(WILD_CARD) || exclusions.contains(recordsetName + "." + WILD_CARD)
                || exclusions.contains(recordsetName + "." + field.getName());

    }

    private String validateEnumeration(CoalesceField<?> field, CoalesceConstraint constraint)
    {
        String result = null;

        switch (field.getDataType()) {
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            // Ensure Constraint Enumeration Exists
            ConstraintEnumerationProviderImpl provider = EnumerationProviderUtil.getProvider(ConstraintEnumerationProviderImpl.class);
            if (provider != null)
            {
                provider.add(constraint);
            }

            // Validate Values
            result = validateEnumeration(constraint.getValue(), ArrayHelper.toIntegerArray(field.getBaseValues()));
            break;
        case STRING_TYPE:
            result = validateRegEx(constraint.getValue(), field.getBaseValues());

            if (result != null)
            {
                result = String.format(CoalesceErrors.INVALID_ENUMERATION, result);
            }
            LOGGER.warn(String.format(CoalesceErrors.DEPRECATED_CONSTRAINT,
                                      constraint.getName(),
                                      field.getEntity().getKey(),
                                      field.getDataType()));
            break;
        default:
            // Do Nothing; Invalid Type
            LOGGER.warn(String.format(CoalesceErrors.INVALID_CONSTRAINT,
                                      constraint.getName(),
                                      field.getEntity().getKey(),
                                      field.getDataType()));

        }

        return result;
    }

    private String validateEnumeration(String type, int... values)
    {
        String result = null;

        for (int value : values)
        {
            try
            {
                if (value < 0 || !EnumerationProviderUtil.isValid(principal, type, value))
                {
                    result = String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION, value, type);
                    break;
                }
            }
            catch (IllegalStateException | IllegalArgumentException e)
            {
                result = e.getMessage();
                break;
            }
        }

        return result;
    }

    private String validateRegEx(CoalesceField<?> field, CoalesceConstraint constraint)
    {

        String result = validateRegEx(constraint.getValue(), field.getBaseValues());

        if (result != null)
        {
            result = String.format(CoalesceErrors.INVALID_INPUT, result);
        }

        return result;

    }

    private String validateRegEx(String regex, String... values)
    {
        String result = null;

        for (String value : values)
        {
            if (!StringHelper.isNullOrEmpty(value) && !Pattern.matches(regex, value))
            {
                // Not Valid
                result = value;
                break;
            }
        }

        return result;
    }

    private String validateSize(CoalesceField<?> field, CoalesceConstraint constraint)
    {
        String result = null;

        // Field Contains Data?
        if (!StringHelper.isNullOrEmpty(field.getBaseValue()))
        {
            // Yes; Is Constraint Numeric
            if (StringUtils.isNumeric(constraint.getValue()))
            {
                if (field.getBaseValues().length != Integer.parseInt(constraint.getValue()))
                {
                    result = String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, field.getName());
                }
            }
            else
            {
                int size;

                // No; Validate Based on Field Data Type
                if (field.getDataType() == ECoalesceFieldDataTypes.INTEGER_TYPE)
                {
                    // Field Specifies the Length
                    size = Integer.parseInt(field.getBaseValue());
                }
                else
                {
                    // All Field's Sizes Must Match
                    size = field.getBaseValues().length;
                }

                for (CoalesceField<?> fieldToCheck : getFields((CoalesceRecord) field.getParent(), constraint))
                {
                    if (fieldToCheck.getBaseValues().length != size)
                    {
                        result = String.format(CoalesceErrors.INVALID_CONSTRAINT_LIST_LENGTH, fieldToCheck.getName());
                    }
                }
            }

        }

        return result;
    }

    private List<CoalesceField<?>> getFields(CoalesceRecord record, CoalesceConstraint constraint)
    {
        List<CoalesceField<?>> fields = new ArrayList<CoalesceField<?>>();

        for (String name : constraint.getValue().split("[,]"))
        {
            CoalesceField<?> additionalField = record.getFieldByName(name);
            if (additionalField != null)
            {
                fields.add(additionalField);
            }
        }

        return fields;
    }

    private String validateMandatory(CoalesceField<?> field, boolean allowEmpty)
    {

        String result = null;
        String value = field.getBaseValue();

        if ((allowEmpty && value == null) || (!allowEmpty && StringHelper.isNullOrEmpty(value)))
        {
            // Not Valid
            result = String.format(CoalesceErrors.INVALID_MANDOTORY_FIELD, field.getName());
        }

        return result;
    }

    private String validateCustom(CoalesceField<?> field, CoalesceConstraint constraint)
    {

        String result;

        try
        {
            ICustomValidator validator = getValidator(constraint.getValue());

            result = validator.validate(principal, field, constraint);
        }
        catch (CoalesceException e)
        {
            result = e.getMessage();
        }

        return result;
    }

    private String validateMax(CoalesceField<?> field, CoalesceConstraint constraint)
    {

        String result = null;

        boolean isInclusive = Boolean.valueOf(constraint.getOtherAttribute("inclusive"));

        for (String value : field.getBaseValues())
        {
            int compareResult = compare(value, constraint.getValue(), field.getDataType());

            if (compareResult == Integer.MAX_VALUE)
            {
                // Invalid Data Type
                result = String.format(String.format(CoalesceErrors.INVALID_DATA_TYPE_NUMERIC, field.getName()));
                break;
            }
            else if (compareResult > 0 || (!isInclusive && compareResult == 0))
            {
                // Invalid Value
                result = String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "max", field.getName());
                break;
            }
        }

        return result;
    }

    private String validateMin(CoalesceField<?> field, CoalesceConstraint constraint)
    {

        String result = null;

        boolean inclusive = Boolean.valueOf(constraint.getOtherAttribute("inclusive"));

        for (String value : field.getBaseValues())
        {
            int compareResult = compare(value, constraint.getValue(), field.getDataType());

            if (compareResult == Integer.MAX_VALUE)
            {
                // Invalid Data Type
                result = String.format(String.format(CoalesceErrors.INVALID_DATA_TYPE_NUMERIC, field.getName()));
                break;
            }
            else if (compareResult < 0 || (!inclusive && compareResult == 0))
            {
                // Invalid Value
                result = String.format(CoalesceErrors.INVALID_INPUT_EXCEEDS, "min", field.getName());
                break;
            }
        }
        return result;
    }

    private int compare(String value, String max, ECoalesceFieldDataTypes dataType)
    {
        int result;

        switch (dataType) {
        case DOUBLE_TYPE:
        case DOUBLE_LIST_TYPE:
            result = Double.compare(Double.valueOf(value), Double.valueOf(max));
            break;
        case FLOAT_TYPE:
        case FLOAT_LIST_TYPE:
            result = Float.compare(Float.valueOf(value), Float.valueOf(max));
            break;
        case INTEGER_TYPE:
        case INTEGER_LIST_TYPE:
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            result = Integer.compare(Integer.valueOf(value), Integer.valueOf(max));
            break;
        case LONG_TYPE:
        case LONG_LIST_TYPE:
            result = Long.compare(Long.valueOf(value), Long.valueOf(max));
            break;
        default:
            result = Integer.MAX_VALUE;
            break;
        }

        return result;
    }

    /**
     * Attempts to load the validator from the OSGi container. If that fails it
     * checks the class path.
     *
     * @param service
     * @return
     * @throws CoalesceException
     */
    private static ICustomValidator getValidator(String name) throws CoalesceException
    {

        if (StringHelper.isNullOrEmpty(name))
        {
            throw new CoalesceException("Custom Validator Not Specified");
        }

        // Load provider from OSGi container
        ICustomValidator validator = getValidatorFromOSGi(name);

        // Succeeded?
        if (validator == null)
        {
            // No; Attempt Service Loader
            validator = getValidatorFromServiceLoader(name);

            // Succeeded?
            if (validator == null)
            {
                throw new CoalesceException("Validator Not Found: " + name);
            }
        }

        return validator;

    }

    /**
     * Attempts to load a validator from the Service Loader.
     *
     * @param service
     * @return
     */
    private static ICustomValidator getValidatorFromServiceLoader(String name)
    {

        ICustomValidator validator = null;

        ServiceLoader<ICustomValidator> loader = ServiceLoader.load(ICustomValidator.class);

        for (ICustomValidator serv : loader)
        {

            if (serv.getClass().getName().equalsIgnoreCase(name))
            {
                validator = serv;
                break;
            }

        }

        return validator;

    }

    /**
     * Attempts to load a validator from an OSGi container.
     *
     * @param service
     * @return
     * @throws CoalesceException
     */
    private static ICustomValidator getValidatorFromOSGi(String name) throws CoalesceException
    {

        ICustomValidator validator = null;

        Bundle bundle = FrameworkUtil.getBundle(CoalesceValidator.class);

        // Running in OSGi Container?
        if (bundle != null)
        {

            // Yes; Get Context
            BundleContext context = bundle.getBundleContext();

            ServiceReference[] refs = null;

            // Has Context?
            if (context != null)
            {

                try
                {

                    // Create Filter
                    String filter = "(type=" + name + ")";

                    // Get service references that matches filter
                    refs = context.getServiceReferences(ICustomValidator.class.getName(), filter);

                }
                catch (InvalidSyntaxException e)
                {
                    throw new CoalesceException("Error Loading Validator: " + e.getMessage(), e);
                }

            }

            // Service found?
            if (refs != null)
            {

                // More then 1 found?
                if (refs.length != 1)
                {
                    // Multiple Validators Found
                }

                // Get Service
                validator = (ICustomValidator) context.getService(refs[0]);

            }
            else
            {
                // Validator Not Found
            }
        }

        return validator;

    }
}
