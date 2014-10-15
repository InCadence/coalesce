package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section.Recordset.Fielddefinition;

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

public class CoalesceFieldDefinition extends CoalesceDataObject implements ICoalesceFieldDefinition {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Fielddefinition _entityFieldDefinition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters of name and datatype
     * provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param dataType datatype that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} will
     *            contain.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent, String name, ECoalesceFieldDataTypes dataType)
    {
        return CoalesceFieldDefinition.create(parent, name, dataType, false);
    }

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} based for an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters of name, datatype and
     * noindex provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param dataType datatype that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} will
     *            contain.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     */
    public static CoalesceFieldDefinition create(CoalesceRecordset parent,
                                                 String name,
                                                 ECoalesceFieldDataTypes dataType,
                                                 boolean noIndex)
    {
        return CoalesceFieldDefinition.create(parent, name, dataType, null, "U", null, noIndex);
    }

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a boolean default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a boolean default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a int default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a int default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a string default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param dataType datatype that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} will
     *            contain.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a string default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
     * @param dataType datatype that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} will
     *            contain.
     * @param label The label to be displayed with the field.
     * @param defaultClassificationMarking the default classification.
     * @param defaultValue the default value.
     * @param noIndex boolean.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}.
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} with a string default value
     * based for an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} with the parameters provided
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to
     * @param name String name of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * @param dataType datatype that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} will
     *            contain
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

        if (parent == null || StringHelper.isNullOrEmpty(name) || dataType == null) return null;

        Fielddefinition newEntityFieldDefinition = new Fielddefinition();
        parent.getEntityFieldDefinitions().add(newEntityFieldDefinition);

        CoalesceFieldDefinition fieldDefinition = new CoalesceFieldDefinition();

        if (!fieldDefinition.initialize(parent, newEntityFieldDefinition)) return null;

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
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} belongs to.
     * @param fieldDefinition the Fielddefinition being initialized.
     * 
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecordset parent, Fielddefinition fieldDefinition)
    {

        // Set References
        setParent(parent);
        _entityFieldDefinition = fieldDefinition;

        super.initialize();

        // Add to Parent Collections
        if (getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            parent.setChildDataObject(getKey(), this);
            parent.getFieldDefinitions().add(this);
        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entityFieldDefinition.getKey();
    }

    @Override
    public void setObjectKey(String value)
    {
        _entityFieldDefinition.setKey(value);
    }

    @Override
    public String getName()
    {
        return getStringElement(_entityFieldDefinition.getName());
    }

    @Override
    public void setName(String value)
    {
        _entityFieldDefinition.setName(value);
    }

    @Override
    public String getType()
    {
        return "fielddefinition";
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
     * @param value String, the default classification marking for the field type.
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
    public DateTime getDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getDatecreated());
        return _entityFieldDefinition.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // _entityFieldDefinition.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityFieldDefinition.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getLastmodified());
        return _entityFieldDefinition.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // _entityFieldDefinition.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityFieldDefinition.setLastmodified(value);
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
    // Public Methods
    // -----------------------------------------------------------------------//

    @Override
    public String toXml()
    {
        return XmlHelper.serialize(_entityFieldDefinition);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectStatus()
    {
        return _entityFieldDefinition.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entityFieldDefinition.setStatus(status.getLabel());
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entityFieldDefinition.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityFieldDefinition.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityFieldDefinition.getLastmodified()));
        map.put(new QName("status"), _entityFieldDefinition.getStatus());
        map.put(new QName("name"), getStringElement(_entityFieldDefinition.getName()));
        map.put(new QName("defaultclassificationmarking"), _entityFieldDefinition.getDefaultclassificationmarking());
        map.put(new QName("defaultvalue"), _entityFieldDefinition.getDefaultvalue());
        map.put(new QName("datatype"), _entityFieldDefinition.getDatatype());
        return map;
    }

    @Override
    public boolean setAttribute(String name, String value)
    {

        switch (name.toLowerCase()) {
        case "key":
            _entityFieldDefinition.setKey(value);
            return true;
        case "datecreated":
            _entityFieldDefinition.setDatecreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entityFieldDefinition.setLastmodified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "status":
            _entityFieldDefinition.setStatus(value);
            return true;
        case "name":
            _entityFieldDefinition.setName(value);
            return true;
        case "defaultclassificationmarking":
            _entityFieldDefinition.setDefaultclassificationmarking(value);
            return true;
        case "defaultvalue":
            _entityFieldDefinition.setDefaultvalue(value);
            return true;
        case "datatype":
            _entityFieldDefinition.setDatatype(value);
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return _entityFieldDefinition.getOtherAttributes();
    }

}
