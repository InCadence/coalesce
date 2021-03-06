package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import org.joda.time.DateTime;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * Wrapper for field definitions.
 */
public class CoalesceFieldDefinition extends CoalesceObject implements ICoalesceFieldDefinition {

    private static final boolean NO_INDEX_DEFAULT = CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT;

    public static final String ATTRIBUTE_DEFAULT_CLASSIFICATION_MARKING = "defaultclassificationmarking";
    public static final String ATTRIBUTE_DEFAULT_VALUE = "defaultvalue";
    public static final String ATTRIBUTE_LABEL = "label";
    public static final String ATTRIBUTE_DESCRIPTION = "description";
    public static final String ATTRIBUTE_DATA_TYPE = "datatype";
    public static final String ATTRIBUTE_DISABLE_HISTORY = "disablehistory";

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Fielddefinition _entityFieldDefinition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @return a field definition for a enumeration list type.
     */
    public static <E extends Enum<E>> CoalesceFieldDefinition createEnumerationListFieldDefinition(CoalesceRecordset parent,
                                                                                                   String name,
                                                                                                   Class<E> enumeration)
    {
        CoalesceFieldDefinition fd = create(parent, name, ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        CoalesceConstraint.createEnumeration(fd, fd.getName() + "enumeration", enumeration);

        return fd;
    }

    /**
     * @param parent
     * @param name
     * @return a field definition for a enumeration list type with the
     * enumeration mapping to the field name.
     */
    public static CoalesceFieldDefinition createEnumerationListFieldDefinition(CoalesceRecordset parent, String name)
    {
        return createEnumerationListFieldDefinition(parent, name, name);
    }

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @return a field definition for a enumeration list type.
     */
    public static CoalesceFieldDefinition createEnumerationListFieldDefinition(CoalesceRecordset parent,
                                                                               String name,
                                                                               String enumeration)
    {
        CoalesceFieldDefinition fd = create(parent, name, ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        CoalesceConstraint.createEnumeration(fd, fd.getName() + "enumeration", enumeration);

        return fd;
    }

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @return a field definition for a enumeration type.
     */
    public static <E extends Enum<E>> CoalesceFieldDefinition createEnumerationFieldDefinition(CoalesceRecordset parent,
                                                                                               String name,
                                                                                               Class<E> enumeration)
    {
        return createEnumerationFieldDefinition(parent, name, enumeration, null);
    }

    /**
     * @param parent
     * @param name
     * @return a field definition for a enumeration type with the enumeration
     * mapping to the field name.
     */
    public static CoalesceFieldDefinition createEnumerationFieldDefinition(CoalesceRecordset parent, String name)
    {
        return createEnumerationFieldDefinition(parent, name, name);
    }

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @param defaultValue
     * @return a field definition for a enumeration type that specifies a
     * default value.
     */
    public static <E extends Enum<E>> CoalesceFieldDefinition createEnumerationFieldDefinition(CoalesceRecordset parent,
                                                                                               String name,
                                                                                               Class<E> enumeration,
                                                                                               E defaultValue)
    {
        String value = null;

        if (defaultValue != null)
        {
            value = Integer.toString(defaultValue.ordinal());
        }

        CoalesceFieldDefinition fd = create(parent, name, ECoalesceFieldDataTypes.ENUMERATION_TYPE, null, "U", value);

        CoalesceConstraint.createEnumeration(fd, fd.getName() + "enumeration", enumeration);

        return fd;
    }

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @return a field definition for a enumeration type.
     */
    public static CoalesceFieldDefinition createEnumerationFieldDefinition(CoalesceRecordset parent,
                                                                           String name,
                                                                           String enumeration)
    {
        return createEnumerationFieldDefinition(parent, name, enumeration, null);
    }

    /**
     * @param parent
     * @param name
     * @param enumeration
     * @param defaultValue
     * @return a field definition for a enumeration type that specifies a
     * default value.
     */
    public static CoalesceFieldDefinition createEnumerationFieldDefinition(CoalesceRecordset parent,
                                                                           String name,
                                                                           String enumeration,
                                                                           Integer defaultValue)
    {
        String value = null;

        if (defaultValue != null)
        {
            value = Integer.toString(defaultValue);
        }

        CoalesceFieldDefinition fd = create(parent, name, ECoalesceFieldDataTypes.ENUMERATION_TYPE, null, "U", value);

        CoalesceConstraint.createEnumeration(fd, fd.getName() + "enumeration", enumeration);

        return fd;
    }

    /**
     * Creates a
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters of name and datatype provided.
     *
     * @param parent   {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                 that the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 belongs to.
     * @param name     String name of the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 .
     * @param dataType datatype that the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 will contain.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent, String name, ECoalesceFieldDataTypes dataType)
    {
        return CoalesceFieldDefinition.create(parent, name, dataType, NO_INDEX_DEFAULT);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters of name, datatype and noindex provided.
     *
     * @param parent   {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                 that the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 belongs to.
     * @param name     String name of the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 .
     * @param dataType datatype that the
     *                 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                 will contain.
     * @param noIndex  boolean.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 ECoalesceFieldDataTypes dataType,
                                                 boolean noIndex)
    {
        return CoalesceFieldDefinition.create(parent, name, dataType, null, "U", null, noIndex);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a boolean default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 boolean defaultValue)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              label,
                                              defaultClassificationMarking,
                                              defaultValue,
                                              NO_INDEX_DEFAULT);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a boolean default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @param noIndex                      boolean.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 boolean defaultValue,
                                                 boolean noIndex)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                                              label,
                                              defaultClassificationMarking,
                                              Boolean.toString(defaultValue),
                                              noIndex);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a int default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 int defaultValue)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              label,
                                              defaultClassificationMarking,
                                              defaultValue,
                                              NO_INDEX_DEFAULT);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a int default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @param noIndex                      boolean.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 int defaultValue,
                                                 boolean noIndex)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              ECoalesceFieldDataTypes.INTEGER_TYPE,
                                              label,
                                              defaultClassificationMarking,
                                              Integer.toString(defaultValue),
                                              noIndex);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a string default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param dataType                     datatype that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     will contain.
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 ECoalesceFieldDataTypes dataType,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 String defaultValue)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              dataType,
                                              label,
                                              defaultClassificationMarking,
                                              defaultValue,
                                              NO_INDEX_DEFAULT);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a string default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to.
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     .
     * @param dataType                     datatype that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     will contain.
     * @param label                        The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue                 the default value.
     * @param noIndex                      boolean.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 ECoalesceFieldDataTypes dataType,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 String defaultValue,
                                                 boolean noIndex)
    {
        return CoalesceFieldDefinition.create(parent,
                                              name,
                                              dataType,
                                              label,
                                              defaultClassificationMarking,
                                              defaultValue,
                                              noIndex,
                                              false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a string default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     *
     * @param parent                       {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                                     that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     belongs to
     * @param name                         String name of the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * @param dataType                     datatype that the
     *                                     {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                                     will contain
     * @param label                        The label to be displayed with the field
     * @param defaultClassificationMarking the default classification
     * @param defaultValue                 the default value
     * @param noIndex                      boolean
     * @param disableHistory               the value defining if a field should track history
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 ECoalesceFieldDataTypes dataType,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 String defaultValue,
                                                 boolean noIndex,
                                                 boolean disableHistory)
    {

        if (parent == null || StringHelper.isNullOrEmpty(name) || dataType == null)
            return null;

        Fielddefinition newEntityFieldDefinition = new Fielddefinition();
        parent.getEntityFieldDefinitions().add(newEntityFieldDefinition);

        CoalesceFieldDefinition fieldDefinition = new CoalesceFieldDefinition();

        if (!fieldDefinition.initialize(parent, newEntityFieldDefinition))
            return null;

        fieldDefinition.setName(name);
        fieldDefinition.setNoIndex(noIndex);
        fieldDefinition.setDisableHistory(disableHistory);
        fieldDefinition.setDataType(dataType);

        fieldDefinition.setLabel(label);
        fieldDefinition.setDefaultClassificationMarkingAsString(defaultClassificationMarking);
        fieldDefinition.setDefaultValue(defaultValue);

        return fieldDefinition;

    }

    /**
     * Initializes a previously new Fielddefinition and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}.
     *
     * @param parent          {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *                        that the
     *                        {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *                        belongs to.
     * @param fieldDefinition the Fielddefinition being initialized.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecordset parent, Fielddefinition fieldDefinition)
    {

        // Set References
        setParent(parent);
        _entityFieldDefinition = fieldDefinition;

        super.initialize(_entityFieldDefinition);

        if (isActive())
        {
            // Add to Parent Collections
            parent.addChildCoalesceObject(this);
        }

        for (Constraint constraint : _entityFieldDefinition.getConstraint())
        {
            CoalesceConstraint coalesceConstraint = new CoalesceConstraint();
            coalesceConstraint.initialize(this, constraint);

        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    /**
     * Modify the JSON annotation
     */
    @JsonView(Views.Entity.class)
    @Override
    public String getKey()
    {
        return super.getKey();
    }

    @JsonView(Views.Entity.class)
    @Override
    public DateTime getDateCreated()
    {
        return super.getDateCreated();
    }

    @JsonView(Views.Entity.class)
    @Override
    public DateTime getLastModified()
    {
        return super.getLastModified();
    }

    protected List<Constraint> getDefinitionConstraints()
    {
        return _entityFieldDefinition.getConstraint();
    }

    /**
     * @return a list of constraints of this field.
     */
    public List<CoalesceConstraint> getConstraints()
    {

        List<CoalesceConstraint> results = new ArrayList<>();

        for (CoalesceObject xdo : getChildCoalesceObjects().values())
        {
            if (xdo instanceof CoalesceConstraint)
            {
                results.add((CoalesceConstraint) xdo);
            }
        }

        return results;
    }

    /**
     * @param name
     * @return the constraint that matches the provided name.
     */
    public CoalesceConstraint getConstraints(String name)
    {
        CoalesceConstraint result = null;

        for (CoalesceConstraint constraint : getConstraints())
        {
            if (constraint.getName().equalsIgnoreCase(name))
            {
                result = constraint;
                break;
            }
        }

        return result;
    }

    @Override
    public String getLabel()
    {
        return _entityFieldDefinition.getLabel();
    }

    @Override
    public void setLabel(String value)
    {
        _entityFieldDefinition.setLabel(value);
    }

    @Override
    public String getDescription()
    {
        return _entityFieldDefinition.getDescription();
    }

    @Override
    public void setDescription(String value)
    {
        _entityFieldDefinition.setDescription(value);
    }

    @Override
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.getTypeForCoalesceType(_entityFieldDefinition.getDatatype());
    }

    /**
     * @return whether this field is a list type.
     */
    @JsonIgnore
    public boolean isListType()
    {
        return getDataType().toString().endsWith(CoalesceSettings.VAR_IS_LIST_TYPE)
                && getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE;
    }

    @Override
    public void setDataType(ECoalesceFieldDataTypes value)
    {
        _entityFieldDefinition.setDatatype(value.getLabel());
    }

    @Override
    public String getDefaultClassificationMarkingAsString()
    {
        return _entityFieldDefinition.getDefaultclassificationmarking();
    }

    @JsonIgnore
    public Marking getDefaultClassificationMarking()
    {
        return new Marking(getDefaultClassificationMarkingAsString());
    }

    @Override
    public void setDefaultClassificationMarkingAsString(String value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value);
    }

    /**
     * Sets the FieldDefinitions's DefaultClassificationMarking attribute based
     * on the Marking class value parameter.
     *
     * @param value Marking class to be the FieldDefinition's default
     *              classification marking attribute.
     */
    public void setDefaultClassificationMarking(Marking value)
    {
        setDefaultClassificationMarkingAsString(value.toPortionString());
    }

    @Override
    public String getDefaultValue()
    {
        return _entityFieldDefinition.getDefaultvalue();
    }

    @Override
    public void setDefaultValue(String value)
    {
        _entityFieldDefinition.setDefaultvalue(value);
    }

    @Override
    public boolean isDisableHistory()
    {
        return getBooleanElement(_entityFieldDefinition.isDisablehistory());
    }

    @Override
    public void setDisableHistory(boolean disable)
    {
        if (disable)
        {
            _entityFieldDefinition.setDisablehistory(disable);
        }
        else
        {
            _entityFieldDefinition.setDisablehistory(null);
        }
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    // @Override
    // public void setStatus(ECoalesceObjectStatus status) {
    // if (status == getStatus())
    // return;
    //
    // _entityFieldDefinition.setStatus(status);
    //
    // switch (status) {
    // case READONLY:
    // case ACTIVE:
    // if (getCastParent().getFieldDefinition(getName()) == null)
    // {
    // getCastParent().addChildCoalesceObject(this);
    // }
    // break;
    // default:
    // getCastParent().pruneCoalesceObject(this);
    // break;
    // }
    //
    // }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName(ATTRIBUTE_DEFAULT_CLASSIFICATION_MARKING),
                _entityFieldDefinition.getDefaultclassificationmarking());
        map.put(new QName(ATTRIBUTE_LABEL), _entityFieldDefinition.getLabel());
        map.put(new QName(ATTRIBUTE_DEFAULT_VALUE), _entityFieldDefinition.getDefaultvalue());
        map.put(new QName(ATTRIBUTE_DATA_TYPE), _entityFieldDefinition.getDatatype());

        if (_entityFieldDefinition.isDisablehistory() != null)
        {
            map.put(new QName(ATTRIBUTE_DISABLE_HISTORY), Boolean.toString(_entityFieldDefinition.isDisablehistory()));
        }

        return map;
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof Constraint)
        {
            isSuccessful = _entityFieldDefinition.getConstraint().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {

        switch (name.toLowerCase())
        {
        case ATTRIBUTE_LABEL:
            setLabel(value);
            return true;
        case ATTRIBUTE_DEFAULT_CLASSIFICATION_MARKING:
            setDefaultClassificationMarkingAsString(value);
            return true;
        case ATTRIBUTE_DEFAULT_VALUE:
            setDefaultValue(value);
            return true;
        case ATTRIBUTE_DATA_TYPE:
            setDataType(ECoalesceFieldDataTypes.getTypeForCoalesceType(value));
            return true;
        case ATTRIBUTE_DISABLE_HISTORY:
            setDisableHistory(Boolean.valueOf(value));
            return true;
        default:
            return setOtherAttribute(name, value);
        }
    }

}
