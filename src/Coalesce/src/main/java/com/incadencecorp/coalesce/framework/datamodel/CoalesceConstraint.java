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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * Implementation of ICoalesceConstraint.
 * 
 * @author n78554
 *
 */
public class CoalesceConstraint extends CoalesceObject implements ICoalesceConstraint {

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private Constraint _constraint;

    /*--------------------------------------------------------------------------
    Factory Methods
    --------------------------------------------------------------------------*/

    /**
     * Creates a constraint restricting the specified fields (within the same
     * record) to all contain list of the same length.
     * 
     * @param name
     * @param definitions fields to be constrained; must be of a list type.
     * @return the new constraint
     */
    public static CoalesceConstraint createListSizeEquals(String name, CoalesceFieldDefinition... definitions)
    {
        return create(definitions[0],
                      name,
                      ConstraintType.SIZE,
                      verifyListType(definitions[0].getParent().getKey(), definitions));
    }

    /**
     * Creates a constraint restricting the specified field to a given length.
     * The field's data type must be a list type.
     * 
     * @param parent
     * @param name
     * @param size
     * @return the new constraint
     */
    public static CoalesceConstraint createListSize(CoalesceFieldDefinition parent, String name, int size)
    {
        if (!parent.isListType())
        {
            throw new ClassCastException("Type mismatch");
        }

        return create(parent, name, ConstraintType.SIZE, Integer.toString(size));
    }

    /**
     * Creates a constraint restricting the specified fields (within the same
     * record) to be of a length specified by another field.
     * 
     * @param parent (integer) field specifying the length.
     * @param name
     * @param definitions fields to be constrained; must be of a list type.
     * @return the new constraint
     */
    public static CoalesceConstraint createListSize(CoalesceFieldDefinition parent,
                                                    String name,
                                                    CoalesceFieldDefinition... definitions)
    {
        if (parent.getDataType() != ECoalesceFieldDataTypes.INTEGER_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        return create(parent, name, ConstraintType.SIZE, verifyListType(parent.getParent().getKey(), definitions));
    }

    /**
     * Creates a constraint restricting the field to values within the specify
     * enumeration.
     * 
     * @param parent
     * @param enumeration Enumeration type
     * @param name
     * @return the new constraint.
     */
    public static CoalesceConstraint createEnumeration(CoalesceFieldDefinition parent, String name, String enumeration)
    {
        return create(parent, name, ConstraintType.ENUMERATION, enumeration);
    }

    /**
     * Creates a constraint restricting the field to values within the specify
     * enumeration.
     * 
     * @param parent
     * @param clazz enumeration that should be used to constrain the field.
     * @param name
     * @return the new constraint.
     */
    public static <E extends Enum<E>> CoalesceConstraint createEnumeration(CoalesceFieldDefinition parent,
                                                                           String name,
                                                                           Class<E> clazz)
    {
        CoalesceConstraint constraint;

        switch (parent.getDataType()) {
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            constraint = create(parent, name, ConstraintType.ENUMERATION, clazz.getName());
            constraint.setAttribute("regex", enumToRegEx(clazz));
            break;
        case STRING_TYPE:
        case STRING_LIST_TYPE:
            constraint = create(parent, name, ConstraintType.ENUMERATION, enumToRegEx(clazz));
            break;
        default:
            throw new IllegalArgumentException(String.format(CoalesceErrors.INVALID_TYPE_CONSTRAINT,
                                                             ConstraintType.ENUMERATION,
                                                             parent.getDataType()));
        }

        return constraint;
    }

    /**
     * @param clazz
     * @return a RegEx representation of a enumeration.
     */
    public static <E extends Enum<E>> String enumToRegEx(Class<E> clazz)
    {
        StringBuilder sb = new StringBuilder("");

        for (Iterator<E> it = EnumSet.allOf(clazz).iterator(); it.hasNext();)
        {
            if (sb.length() != 0)
            {
                sb.append("|");
            }

            sb.append(it.next());
        }

        return "(" + sb.toString() + ")";
    }

    /**
     * @param regex
     * @return a list of values from a regular expression that was created by
     *         {@link #enumToRegEx}
     */
    public static List<String> regExToValues(String regex)
    {
        List<String> results = null;

        if (regex.startsWith("(") && regex.endsWith(")"))
        {
            results = Arrays.asList(regex.substring(1, regex.length() - 1).split("[|]"));
        }

        return results;
    }

    /**
     * Creates a mandatory constraint. An empty string is not a valid value.
     * 
     * @param parent
     * @param name
     * @return the new constraint.
     */
    public static CoalesceConstraint createMandatory(CoalesceFieldDefinition parent, String name)
    {
        return create(parent, name, ConstraintType.MANDATORY, null);
    }

    /**
     * Creates a mandatory constraint. Where you can specify whether empty is a
     * valid value or not.
     * 
     * @param parent
     * @param name
     * @param allowEmpty sets whether an empty string is valid.
     * @return the new constraint.
     */
    public static CoalesceConstraint createMandatory(CoalesceFieldDefinition parent, String name, boolean allowEmpty)
    {
        CoalesceConstraint mandatory = create(parent, name, ConstraintType.MANDATORY, null);

        mandatory.setAttribute("allowEmpty", Boolean.toString(allowEmpty));

        return mandatory;
    }

    /**
     * Creates a custom constraint.
     * 
     * @param parent
     * @param name
     * @param value the class that implements the constraint.
     * @return the new constraint.
     */
    public static CoalesceConstraint createCustom(CoalesceFieldDefinition parent, String name, Class<?> value)
    {
        return create(parent, name, ConstraintType.CUSTOM, value.getName());
    }

    /**
     * Creates a custom constraint.
     * 
     * @param parent
     * @param name
     * @param value the name of the implementation to use to validate this
     *            field.
     * @return the new constraint.
     */
    public static CoalesceConstraint createCustom(CoalesceFieldDefinition parent, String name, String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Validator not specified");
        }

        return create(parent, name, ConstraintType.CUSTOM, value);
    }

    /**
     * Creates a max constraint.
     * 
     * @param parent
     * @param name
     * @param value
     * @param isInclusive
     * @return the new constraint.
     */
    public static CoalesceConstraint createMax(CoalesceFieldDefinition parent, String name, double value, boolean isInclusive)
    {
        return createMinMax(parent, name, ConstraintType.MAX, value, isInclusive);
    }

    /**
     * Creates a min constraint.
     * 
     * @param parent
     * @param name
     * @param value
     * @param isInclusive
     * @return the new constraint.
     */
    public static CoalesceConstraint createMin(CoalesceFieldDefinition parent, String name, double value, boolean isInclusive)
    {
        return createMinMax(parent, name, ConstraintType.MIN, value, isInclusive);
    }

    /**
     * Creates a regular expression constraint.
     * 
     * @param parent
     * @param name
     * @param value a regular expression used to validate the field.
     * @return the new constraint.
     */
    public static CoalesceConstraint createRegEx(CoalesceFieldDefinition parent, String name, String value)
    {
        return create(parent, name, ConstraintType.REGEX, value.toString());
    }

    private static CoalesceConstraint createMinMax(CoalesceFieldDefinition parent,
                                                   String name,
                                                   ConstraintType type,
                                                   double value,
                                                   boolean isInclusive)
    {
        String valueString;

        switch (parent.getDataType()) {
        case DOUBLE_TYPE:
        case DOUBLE_LIST_TYPE:
            valueString = Double.toString(value);
            break;
        case FLOAT_TYPE:
        case FLOAT_LIST_TYPE:
            valueString = Float.toString((float) value);
            break;
        case LONG_TYPE:
        case LONG_LIST_TYPE:
            valueString = Long.toString((long) value);
            break;
        case INTEGER_TYPE:
        case INTEGER_LIST_TYPE:
        case ENUMERATION_TYPE:
        case ENUMERATION_LIST_TYPE:
            valueString = Integer.toString((int) value);
            break;
        default:
            throw new ClassCastException("Type mismatch");
        }

        CoalesceConstraint constraint = create(parent, name, type, valueString);

        if (isInclusive)
        {
            constraint.setAttribute("inclusive", Boolean.TRUE.toString());
        }

        return constraint;
    }

    /**
     * Creates a new constraint on the provided field definition.
     * 
     * @param parent
     * @param name
     * @param type
     * @param value
     * @return the new constraint.
     */
    private static CoalesceConstraint create(CoalesceFieldDefinition parent, String name, ConstraintType type, String value)
    {
        if (parent == null)
            throw new NullArgumentException("parent");
        if (name == null)
            throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name))
            throw new IllegalArgumentException("name cannot be an empty string");
        if (type == null)
            throw new NullArgumentException("type");

        Constraint newConstraint = new Constraint();
        parent.getDefinitionConstraints().add(newConstraint);

        // Create Constraint
        CoalesceConstraint constraint = new CoalesceConstraint();
        if (!constraint.initialize(parent, newConstraint))
            return null;

        constraint.setName(name);
        constraint.setConstraintType(type);
        constraint.setValue(value);

        // Add to Parent
        parent.addChildCoalesceObject(constraint);

        return constraint;
    }

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    protected boolean initialize(CoalesceFieldDefinition parent, Constraint constraint)
    {

        // Set References
        setParent(parent);
        _constraint = constraint;

        super.initialize(_constraint);

        // Add to Parent Collections
        if (!isMarkedDeleted())
        {
            parent.addChildCoalesceObject(this);
            parent.getConstraints().add(this);
        }

        return true;

    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public ConstraintType getConstraintType()
    {
        return _constraint.getType();
    }

    @Override
    public void setConstraintType(ConstraintType type)
    {
        _constraint.setType(type);
    }

    @Override
    public String getValue()
    {
        return _constraint.getValue();
    }

    @Override
    public void setValue(String value)
    {
        _constraint.setValue(value);
    }

    @Override
    public CoalesceFieldDefinition getFieldDefinition()
    {
        return getCastParent();
    }

    /*--------------------------------------------------------------------------
    Protected Overrides
    --------------------------------------------------------------------------*/

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        // This element has no children
        return false;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

    @Override
    public void setStatus(ECoalesceObjectStatus status)
    {
        if (status == getStatus())
            return;

        _constraint.setStatus(status);

        if (!isMarkedDeleted())
        {
            getCastParent().getConstraints().add(this);
        }
        else
        {
            getCastParent().getConstraints().remove(this);
        }
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private CoalesceFieldDefinition getCastParent()
    {
        return (CoalesceFieldDefinition) getParent();
    }

    /**
     * Verifies each definition is a list type and belongs to the specified
     * parent.
     * 
     * @param key
     * @param definitions
     * @return a comma separated list of fields. 
     */
    private static String verifyListType(String key, CoalesceFieldDefinition... definitions)
    {
        Set<String> xPaths = new HashSet<String>();

        for (CoalesceFieldDefinition definition : definitions)
        {
            if (!definition.isListType())
            {
                throw new ClassCastException("Type mismatch");
            }

            if (!definition.getParent().getKey().equalsIgnoreCase(key))
            {
                throw new IllegalArgumentException("All definitions must belong to the same record");
            }

            xPaths.add(definition.getName());
        }

        return StringUtils.join(xPaths, ",");
    }
}
