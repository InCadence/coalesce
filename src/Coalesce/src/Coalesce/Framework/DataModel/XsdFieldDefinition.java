package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

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
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Fielddefinition _entityFieldDefinition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            boolean defaultValue)
    {
        return XsdFieldDefinition.Create(parent, name, dataType, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            boolean defaultValue,
                                            boolean noIndex)
    {
        return XsdFieldDefinition.Create(parent,
                                         name,
                                         dataType,
                                         label,
                                         defaultClassificationMarking,
                                         Boolean.toString(defaultValue),
                                         noIndex);
    }

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            int defaultValue)
    {
        return XsdFieldDefinition.Create(parent, name, dataType, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            int defaultValue,
                                            boolean noIndex)
    {
        return XsdFieldDefinition.Create(parent,
                                         name,
                                         dataType,
                                         label,
                                         defaultClassificationMarking,
                                         Integer.toString(defaultValue),
                                         noIndex);
    }

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            String defaultValue)
    {
        return XsdFieldDefinition.Create(parent, name, dataType, label, defaultClassificationMarking, defaultValue, false);
    }

    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            String defaultValue,
                                            boolean noIndex)
    {

        XsdFieldDefinition fieldDefinition = Create(parent, name, dataType);

        // Set Additional Properties
        fieldDefinition.SetLabel(label);
        fieldDefinition.SetDefaultClassificationMarking(defaultClassificationMarking);
        fieldDefinition.SetDefaultValue(defaultValue);
        fieldDefinition.setNoIndex(noIndex);

        return fieldDefinition;

    }

    public static XsdFieldDefinition Create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType)
    {
        return Create(parent, name, dataType, false);
    }

    /*
     * public static XsdFieldDefinition Create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType, boolean
     * noIndex) { return Create(parent, name, ECoalesceFieldDataTypes.GetELinkTypeForLabel(dataType), noIndex); }
     */
    /*
     * public static XsdFieldDefinition Create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType) { return
     * Create(parent, name, dataType, false); }
     */
    public static XsdFieldDefinition Create(XsdRecordset parent,
                                            String name,
                                            ECoalesceFieldDataTypes dataType,
                                            boolean noIndex)
    {

        Fielddefinition newEntityFieldDefinition = new Fielddefinition();
        parent.GetEntityFieldDefinitions().add(newEntityFieldDefinition);

        XsdFieldDefinition fieldDefinition = new XsdFieldDefinition();

        if (!fieldDefinition.Initialize(parent, newEntityFieldDefinition)) return null;

        fieldDefinition.setName(name);
        fieldDefinition.SetDefaultClassificationMarking("U");
        fieldDefinition.SetDefaultValue("");
        fieldDefinition.setNoIndex(noIndex);
        fieldDefinition.SetDataType(dataType);

        return fieldDefinition;
    }

    public boolean Initialize(XsdRecordset parent, Fielddefinition fieldDefinition)
    {

        // Set References
        _parent = parent;
        _entityFieldDefinition = fieldDefinition;

        super.initialize();
        
        // Add to Parent Collections
        if (getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            parent._childDataObjects.put(this.getKey(), this);
            parent.GetFieldDefinitions().add(this);
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

    public String GetLabel()
    {
        return _entityFieldDefinition.getLabel();
    }

    public void SetLabel(String value)
    {
        _entityFieldDefinition.setLabel(value);
    }

    public ECoalesceFieldDataTypes GetDataType()
    {
        return ECoalesceFieldDataTypes.GetTypeForCoalesceType(_entityFieldDefinition.getDatatype());
    }

    public void SetDataType(ECoalesceFieldDataTypes value)
    {
        _entityFieldDefinition.setDatatype(value.getLabel());
    }

    public String GetDefaultClassificationMarking()
    {
        return _entityFieldDefinition.getDefaultclassificationmarking();
    }

    public void SetDefaultClassificationMarking(String value)
    {
        _entityFieldDefinition.setDefaultclassificationmarking(value);
    }

    public String GetDefaultValue()
    {
        return _entityFieldDefinition.getDefaultvalue();
    }

    public void SetDefaultValue(String value)
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
    protected void setObjectStatus(String status)
    {
        _entityFieldDefinition.setStatus(status);
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityFieldDefinition.getOtherAttributes();
    }

}
