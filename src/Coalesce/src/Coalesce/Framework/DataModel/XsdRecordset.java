package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Fielddefinition;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record;

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

public class XsdRecordset extends XsdDataObject implements ICoalesceRecordset {

    // -----------------------------------------------------------------------//
    // Public Events
    // -----------------------------------------------------------------------//

    // TODO: Java Events
    // public Event ListChanged(ByVal sender As Object, ByVal e As System.ComponentModel.ListChangedEventArgs) Implements
    // System.ComponentModel.IBindingList.ListChanged

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Recordset _entityRecordset;

    protected ArrayList<XsdFieldDefinition> _fieldDefinitions;
    protected ArrayList<XsdRecord> _records;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an XsdRecordset, by name, and ties it to its parent XsdSection with default max and min records of 0.
     * 
     * @param parent XsdSection
     * @param name String
     * @return XsdRecordset
     */
    public static XsdRecordset create(XsdSection parent, String name)
    {
        return create(parent, name, 0, 0);
    }

    /**
     * Creates an XsdRecordset, by name, and ties it to its parent XsdSection with max and min records assignments.
     * 
     * @param parent XsdSection
     * @param name
     * @param minRecords
     * @param maxRecords
     * @return XsdRecordset
     */
    public static XsdRecordset create(XsdSection parent, String name, int minRecords, int maxRecords)
    {

        if (parent == null) throw new NullArgumentException("parent");
        if (name == null) throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name.trim())) throw new IllegalArgumentException("name cannot be empty");

        if (minRecords < 0 || maxRecords < minRecords) return null;

        // Check that a recordset with the same name doesn't already exist
        for (XsdRecordset recordset : parent.getRecordsets().values())
        {
            if (recordset.getName().equalsIgnoreCase(name))
            {
                return recordset;
            }
        }

        Recordset newEntityRecordset = new Recordset();
        parent.getEntityRecordSets().add(newEntityRecordset);

        XsdRecordset newRecordset = new XsdRecordset();
        if (!newRecordset.initialize(parent, newEntityRecordset)) return null;

        newRecordset.setName(name);
        newRecordset.setMinRecords(minRecords);
        newRecordset.setMaxRecords(maxRecords);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newRecordset.getKey()))
        {
            parent._childDataObjects.put(newRecordset.getKey(), newRecordset);
        }

        return newRecordset;

    }

    /**
     * Initializes a this XsdRecordset based on a Recordset and ties it to its parent XsdSection.
     * 
     * @param parent XsdSection
     * @param recordset Recordset
     * @return boolean
     */
    public boolean initialize(XsdSection parent, Recordset recordset)
    {

        if (parent == null) throw new NullArgumentException("parent");
        if (recordset == null) throw new NullArgumentException("recordset");

        // Set References
        _parent = parent;
        _entityRecordset = recordset;

        super.initialize();

        // Create Collections
        _fieldDefinitions = new ArrayList<XsdFieldDefinition>();
        _records = new ArrayList<XsdRecord>();

        for (Fielddefinition entityFieldDefinition : _entityRecordset.getFielddefinition())
        {
            XsdFieldDefinition newFieldDefinition = new XsdFieldDefinition();
            newFieldDefinition.initialize(this, entityFieldDefinition);

        }

        for (Record entityRecord : _entityRecordset.getRecord())
        {
            XsdRecord newRecord = new XsdRecord();
            newRecord.initialize(this, entityRecord);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entityRecordset.getKey();
    }

    @Override
    protected void setObjectKey(String value)
    {
        _entityRecordset.setKey(value);
    }

    @Override
    public String getName()
    {
        return _entityRecordset.getName();
    }

    @Override
    public void setName(String value)
    {
        _entityRecordset.setName(value);
    }

    @Override
    public String getType()
    {
        return "recordset";
    }

    /**
     * Returns this XsdRecordset's ArrayList of XsdFieldDefinitions
     * 
     * @return ArrayList<XsdFieldDefinition>
     */
    public ArrayList<XsdFieldDefinition> getFieldDefinitions()
    {
        return _fieldDefinitions;
    }

    /**
     * Returns this XsdRecordset's ArrayList of XsdRecords
     * 
     * @return ArrayList<XsdRecord>
     */
    public ArrayList<XsdRecord> getRecords()
    {
        return _records;
    }

    @Override
    public int getMaxRecords()
    {
        return Integer.parseInt(_entityRecordset.getMaxrecords());
    }

    @Override
    public void setMaxRecords(int value)
    {
        _entityRecordset.setMaxrecords(String.valueOf(value));
    }

    @Override
    public int getMinRecords()
    {
        return Integer.parseInt(_entityRecordset.getMinrecords());
    }

    @Override
    public void setMinRecords(int value)
    {
        _entityRecordset.setMinrecords(String.valueOf(value));
    }

    /**
     * Returns boolean indicator of the existence of this XsdRecordset's active XsdRecords
     * 
     * @return boolean
     */
    public boolean getHasActiveRecords()
    {

        // Iterate Records
        // For Each Record As CoalesceRecord In this._Records
        for (XsdRecord record : getRecords())
        {
            if (record.getStatus() == ECoalesceDataObjectStatus.ACTIVE)
            {
                return true;
            }
        }

        // No Active Records
        return false;
    }

    /**
     * Returns boolean indicator of the existence of this XsdRecordset's XsdRecords
     * 
     * @return boolean
     */
    public boolean getHasRecords()
    {
        return !_entityRecordset.getRecord().isEmpty();
    }

    @Override
    public DateTime getDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getDatecreated());
        return _entityRecordset.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // _entityRecordset.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecordset.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getLastmodified());
        return _entityRecordset.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // _entityRecordset.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecordset.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates an XsdFieldDefinition for this XsdRecordset, with the name, datatype, label, default classification and
     * default value specified.
     * 
     * @param name
     * @param dataType
     * @param label
     * @param defaultClassificationMarking
     * @param defaultValue
     * @return
     */
    public XsdFieldDefinition createFieldDefinition(String name,
                                                    ECoalesceFieldDataTypes dataType,
                                                    String label,
                                                    String defaultClassificationMarking,
                                                    String defaultValue)
    {
        return XsdFieldDefinition.create(this, name, dataType, label, defaultClassificationMarking, defaultValue);
    }

    /**
     * Creates an XsdFieldDefinition for this XsdRecordset, with the name and datatype specified.
     * 
     * @param name
     * @param dataType
     * @return
     */
    public XsdFieldDefinition createFieldDefinition(String name, ECoalesceFieldDataTypes dataType)
    {
        return XsdFieldDefinition.create(this, name, dataType);
    }

    @Override
    public String toXml()
    {
        return XmlHelper.serialize(_entityRecordset);
    }

    /**
     * Returns the XsdRecordset's XsdFieldDefinition that matches the String fieldName parameter
     * 
     * @param fieldName
     * @return
     */
    public XsdFieldDefinition getFieldDefinition(String fieldName)
    {
        for (XsdFieldDefinition fieldDefinition : getFieldDefinitions())
        {
            if (fieldDefinition.getName().toUpperCase().equals(fieldName.toUpperCase()))
            {

                return fieldDefinition;
            }
        }

        return null;
    }

    /**
     * Returns boolean indicator of XsdRecordset's ability to be edited
     * 
     * @return
     */
    public boolean getAllowEdit()
    {
        return true;
    }

    /**
     * Returns boolean indicator of XsdRecordset's ability to add new records
     * 
     * @return
     */
    public boolean getAllowNew()
    {
        return true;
    }

    /**
     * Returns boolean indicator of XsdRecordset's ability to add delete records
     * 
     * @return
     */
    public boolean getAllowRemove()
    {
        return true;
    }

    /**
     * Returns the number of XsdRecords contained within this XsdRecordset
     * 
     * @return
     */
    public int getCount()
    {
        return getRecords().size();
    }

    /**
     * Returns a boolean indicator of the existence of the XsdRecord parameter within this XsdRecordset
     * 
     * @param value
     * @return
     */
    public boolean contains(Object value)
    {
        return getRecords().contains(value);
    }

    /**
     * Returns Index of the XsdRecord parameter within this XsdRecordset
     * 
     * @param value
     * @return
     */
    public int IndexOf(Object value)
    {
        return getRecords().indexOf(value);
    }

    /**
     * Adds a new XsdRecord to this XsdRecordset and returns the XsdRecord
     * 
     * @return
     */
    public XsdRecord addNew()
    {
        XsdRecord newRecord = XsdRecord.create(this, getName() + " Record");

        // TODO: Raise the Changed Event
        // RaiseEvent ListChanged(this, new
        // ListChangedEventArgs(ListChangedType.ItemAdded,
        // this.GetRecords().Count - 1));

        return newRecord;
    }

    /**
     * Returns the XsdRecord at the specified index within this XsdRecordset
     * 
     * @param index
     * @return
     */
    public XsdRecord GetItem(int index)
    {

        // Iterate List
        if (index >= 0 && index < this.getRecords().size())
        {
            return getRecords().get(index);
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Removes the XsdRecord at the specified index within this XsdRecordset
     * 
     * @param index
     */
    public void RemoveAt(Integer index)
    { // Implements System.Collections.IList.RemoveAt
      // Get Record
        XsdRecord record = (XsdRecord) this.GetItem(index);

        // Evaluate
        if (record != null)
        {

            getRecords().remove(record);

            // Set as Status as Deleted
            record.setStatus(ECoalesceDataObjectStatus.DELETED);

            // // Determine new Index
            // int NewIndex;
            // if (index == 0) {
            // if (this.Count > 0) {
            // NewIndex = 0;
            // }else{
            // NewIndex = -1;
            // }
            // }else{
            // NewIndex = index - 1;
            // }

            // TODO: Raise ListChanged Event
            // RaiseEvent ListChanged(this, new
            // ListChangedEventArgs(ListChangedType.ItemDeleted, NewIndex));
        }
    }

    /**
     * Removes the XsdRecord, from within this XsdRecordset, that has the matching key passed by parameter
     * 
     * @param key
     */
    public void Remove(String key)
    {
        XsdRecord recordToRemove = null;

        // Find
        // For Each Record As XsdRecord In this.Records
        for (XsdRecord record : getRecords())
        {
            if (record.getKey().equals(key))
            {
                recordToRemove = record;
                break;
            }
        }

        // Evaluate
        if (recordToRemove != null)
        {
            // Remove from the Records Collection
            this.getRecords().remove(recordToRemove);

            // Set as Status as Deleted
            recordToRemove.setStatus(ECoalesceDataObjectStatus.DELETED);

        }
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectStatus()
    {
        return _entityRecordset.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entityRecordset.setStatus(status.getLabel());
    }

    /**
     * Returns the Record list contained by the Recordset
     * 
     * @return
     */
    protected List<Record> GetEntityRecords()
    {
        return _entityRecordset.getRecord();
    }

    /**
     * Returns the Fielddefinition list contained by the Recordset
     * 
     * @return
     */
    protected List<Fielddefinition> GetEntityFieldDefinitions()
    {
        return _entityRecordset.getFielddefinition();
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return this._entityRecordset.getOtherAttributes();
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name) {
        case "key":
            _entityRecordset.setKey(value);
            return true;
        case "datecreated":
            _entityRecordset.setDatecreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entityRecordset.setLastmodified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "name":
            _entityRecordset.setName(value);
            return true;
        case "minrecords":
            _entityRecordset.setMinrecords(value);
            return true;
        case "maxrecords":
            _entityRecordset.setMaxrecords(value);
            return true;
        case "status":
            _entityRecordset.setStatus(value);
            return true;
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entityRecordset.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityRecordset.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityRecordset.getLastmodified()));
        map.put(new QName("name"), _entityRecordset.getName());
        map.put(new QName("minrecords"), _entityRecordset.getMinrecords());
        map.put(new QName("maxrecords"), _entityRecordset.getMaxrecords());
        map.put(new QName("status"), _entityRecordset.getStatus());
        return map;

    }
}

// TODO: CoalesceFieldDefinitionMemberDescriptor class
// public Class CoalesceFieldDefinitionMemberDescriptor
// Inherits MemberDescriptor
//
// public Sub New(String Name)
// MyBase.New(Name)
// }
// End Class

// TODO: CoalesceFieldDefinitionPropertyDescriptor class
// public Class CoalesceFieldDefinitionPropertyDescriptor
// Inherits PropertyDescriptor
//
// public Sub New(CoalesceFieldDefinitionMemberDescriptor MemberDescriptor)
// MyBase.New(MemberDescriptor)
// }
//
// public Overrides Function CanResetValue(Object component) As Boolean
// // return true; If we're asked to reset the value, we can reset to
// // Empty String or CoalesceFielDefinition's Default Value
// return true
// }
//
// public Overrides ReadOnly Property ComponentType As System.Type
// Get
// // For this PropertyDescriptor, the Component Type is a CoalesceRecord
// return GetType(CoalesceRecord)
// }
//
//
// public Overrides Function GetValue(Object component) As Object
// try{
// // Cast to CoalesceRecord and Get Field Value
// return CType(component, CoalesceRecord).GetFieldByName(this.Name).Value
//
// }catch(Exception ex){
// // Log
// CallResult.log(CallResults.FAILED_ERROR, ex, this)
//
// // return null
// return null
// }
// }
//
// public Overrides ReadOnly Property IsReadOnly As Boolean
// Get
// // We're not Read Only
// return false
// }
//
//
// public Overrides ReadOnly Property PropertyType As System.Type
// Get
// // For binding, all types are treated as String.
// return GetType(String)
// }
//
//
// public Overrides Sub ResetValue(Object component){
// try{
// if (component != null) {
// CallResult rst;
//
// Dim Record As CoalesceRecord = CType(component, CoalesceRecord)
// Dim Recordset As CoalesceRecordset = CType(Record.Parent, CoalesceRecordset)
// Dim FieldDefinition As CoalesceFieldDefinition = null
//
// rst = Recordset.GetFieldDefinition(this.Name, FieldDefinition)
//
// // Evaluate
// if (rst.getIsSuccess()) {
// Dim Field As CoalesceField = Record.GetFieldByName(this.Name)
// Field.ClassificationMarking = FieldDefinition.DefaultClassificationMarking
// Field.Value = FieldDefinition.DefaultValue
// }
// }
// }catch(Exception ex){
// // Log
// CallResult.log(CallResults.FAILED_ERROR, ex, this);
// }
// }
//
// @Override
// public SetValue(Object component, Object value)
// try{
// ((CoalesceRecord) component).GetFieldByName(this.Name).Value = value;
// }catch(Exception ex){
// // Log
// CallResult.log(CallResults.FAILED_ERROR, ex, this);
// }
// }
//
// @Override
// public boolean ShouldSerializeValue(Object component) {
// return false;
// }
//
// }