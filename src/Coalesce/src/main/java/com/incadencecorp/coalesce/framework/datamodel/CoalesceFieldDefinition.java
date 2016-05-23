package com.incadencecorp.coalesce.framework.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

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

public class CoalesceFieldDefinition extends CoalesceObject implements ICoalesceFieldDefinition {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Fielddefinition _entityFieldDefinition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates a
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters of name and datatype provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param dataType datatype that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            will contain.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent, String name, ECoalesceFieldDataTypes dataType)
    {
        return CoalesceFieldDefinition.create(parent, name, dataType, false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters of name, datatype and noindex provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param dataType datatype that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            will contain.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
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
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 boolean defaultValue)
    {
        return CoalesceFieldDefinition.create(parent, name, label, defaultClassificationMarking, defaultValue, false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a boolean default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
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
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 String label,
                                                 String defaultClassificationMarking,
                                                 int defaultValue)
    {
        return CoalesceFieldDefinition.create(parent, name, label, defaultClassificationMarking, defaultValue, false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a int default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
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
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param dataType datatype that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            will contain.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
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
                                              false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * with a string default value based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            .
     * @param dataType datatype that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            will contain.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *         .
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
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to
     * @param name String name of the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * @param dataType datatype that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            will contain
     * @param label The label to be displayed with the field
     * @param defaultClassificationMarking the default classification
     * @param defaultValue the default value
     * @param noIndex boolean
     * @param disableHistory the value defining if a field should track history
     * 
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
        fieldDefinition.setDefaultClassificationMarking(defaultClassificationMarking);
        fieldDefinition.setDefaultValue(defaultValue);

        return fieldDefinition;

    }

    /**
     * Initializes a previously new Fielddefinition and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            belongs to.
     * @param fieldDefinition the Fielddefinition being initialized.
     * 
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecordset parent, Fielddefinition fieldDefinition)
    {

        // Set References
        setParent(parent);
        _entityFieldDefinition = fieldDefinition;

        super.initialize(_entityFieldDefinition);

        // Add to Parent Collections
        switch (getStatus()) {
        case READONLY:
        case ACTIVE:
            parent.addChildCoalesceObject(getKey(), this);
            break;
        default:
            // Skip
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

    protected List<Constraint> getDefinitionConstraints()
    {
        return _entityFieldDefinition.getConstraint();
    }

    /**
     * @return a list of constraints of this field.
     */
    public List<CoalesceConstraint> getConstraints()
    {

        List<CoalesceConstraint> results = new ArrayList<CoalesceConstraint>();

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
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.getTypeForCoalesceType(_entityFieldDefinition.getDatatype());
    }

    @Override
    public void setDataType(ECoalesceFieldDataTypes value)
    {
        _entityFieldDefinition.setDatatype(value.getLabel());
    }

    @Override
    public Marking getDefaultClassificationMarking()
    {
        return new Marking(_entityFieldDefinition.getDefaultclassificationmarking());
    }

    /**
     * Sets the Fielddefinition's DefaultClassificationMarking.
     * 
     * @param value String, the default classification marking for the field
     *            type.
     */
    public void setDefaultClassificationMarking(String value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value);
    }

    @Override
    public void setDefaultClassificationMarking(Marking value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value.toPortionString());
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

        map.put(new QName("defaultclassificationmarking"), _entityFieldDefinition.getDefaultclassificationmarking());
        map.put(new QName("label"), _entityFieldDefinition.getLabel());
        map.put(new QName("defaultvalue"), _entityFieldDefinition.getDefaultvalue());
        map.put(new QName("datatype"), _entityFieldDefinition.getDatatype());

        if (_entityFieldDefinition.isDisablehistory() != null)
        {
            map.put(new QName("disablehistory"), Boolean.toString(_entityFieldDefinition.isDisablehistory()));
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

        switch (name.toLowerCase()) {
        case "label":
            setLabel(value);
            return true;
        case "defaultclassificationmarking":
            setDefaultClassificationMarking(value);
            return true;
        case "defaultvalue":
            setDefaultValue(value);
            return true;
        case "datatype":
            setDataType(ECoalesceFieldDataTypes.getTypeForCoalesceType(value));
            return true;
        case "disablehistory":
            setDisableHistory(Boolean.valueOf(value));
            return true;
        default:
            return setOtherAttribute(name, value);
        }
    }

    @SuppressWarnings("unused")
    private CoalesceRecordset getCastParent()
    {
        return (CoalesceRecordset) getParent();
    }

}
