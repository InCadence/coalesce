package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Fielddefinition;

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

public class XsdFieldDefinition extends XsdDataObject {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Fielddefinition _entityFieldDefinition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdFieldDefinition create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType)
    {
        return XsdFieldDefinition.create(parent, name, dataType, false);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            boolean noIndex)
    {
        return XsdFieldDefinition.create(parent, name, dataType, "", "U", "", noIndex);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            String label,
                                            String defaultClassificationMarking,
                                            boolean defaultValue)
    {
        return XsdFieldDefinition.create(parent, name, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            String label,
                                            String defaultClassificationMarking,
                                            boolean defaultValue,
                                            boolean noIndex)
    {
        return XsdFieldDefinition.create(parent,
                                         name,
                                         ECoalesceFieldDataTypes.BooleanType,
                                         label,
                                         defaultClassificationMarking,
                                         Boolean.toString(defaultValue),
                                         noIndex);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            String label,
                                            String defaultClassificationMarking,
                                            int defaultValue)
    {
        return XsdFieldDefinition.create(parent, name, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            String label,
                                            String defaultClassificationMarking,
                                            int defaultValue,
                                            boolean noIndex)
    {
        return XsdFieldDefinition.create(parent,
                                         name,
                                         ECoalesceFieldDataTypes.IntegerType,
                                         label,
                                         defaultClassificationMarking,
                                         Integer.toString(defaultValue),
                                         noIndex);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            String defaultValue)
    {
        return XsdFieldDefinition.create(parent, name, dataType, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            String defaultValue,
                                            boolean noIndex)
    {

        if (parent == null || name == null || StringHelper.IsNullOrEmpty(name.trim()) || dataType == null || label == null
                || defaultClassificationMarking == null || defaultValue == null) return null;

        Fielddefinition newEntityFieldDefinition = new Fielddefinition();
        parent.GetEntityFieldDefinitions().add(newEntityFieldDefinition);

        XsdFieldDefinition fieldDefinition = new XsdFieldDefinition();

        if (!fieldDefinition.initialize(parent, newEntityFieldDefinition)) return null;

        fieldDefinition.setName(name.trim());
        fieldDefinition.setNoIndex(noIndex);
        fieldDefinition.setDataType(dataType);

        fieldDefinition.setLabel(label.trim());
        fieldDefinition.setDefaultClassificationMarking(defaultClassificationMarking);
        fieldDefinition.setDefaultValue(defaultValue);

        return fieldDefinition;

    }

    public boolean initialize(XsdRecordset parent, Fielddefinition fieldDefinition)
    {

        // Set References
        _parent = parent;
        _entityFieldDefinition = fieldDefinition;

        super.initialize();

        // Add to Parent Collections
        if (getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            parent._childDataObjects.put(getKey(), this);
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
        return _entityFieldDefinition.getName();
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

    public String getLabel()
    {
        return _entityFieldDefinition.getLabel();
    }

    public void setLabel(String value)
    {
        _entityFieldDefinition.setLabel(value);
    }

    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.GetTypeForCoalesceType(_entityFieldDefinition.getDatatype());
    }

    public void setDataType(ECoalesceFieldDataTypes value)
    {
        _entityFieldDefinition.setDatatype(value.getLabel());
    }

    public String getDefaultClassificationMarking()
    {
        return _entityFieldDefinition.getDefaultclassificationmarking();
    }

    public void setDefaultClassificationMarking(String value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value);
    }

    public void setDefaultClassificationMarking(Marking value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value.ToPortionString());
    }

    public String getDefaultValue()
    {
        return _entityFieldDefinition.getDefaultvalue();
    }

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

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    public String toXml()
    {
        return XmlHelper.Serialize(_entityFieldDefinition);
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
        _entityFieldDefinition.setStatus(status.toLabel());
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityFieldDefinition.getOtherAttributes();
    }

}
