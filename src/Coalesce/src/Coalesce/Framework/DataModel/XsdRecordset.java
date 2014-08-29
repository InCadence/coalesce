package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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

public class XsdRecordset extends XsdDataObject {

    // -----------------------------------------------------------------------//
    // Public Events
    // -----------------------------------------------------------------------//

    // TODO: Java Events
    // public Event ListChanged(ByVal sender As Object, ByVal e As System.ComponentModel.ListChangedEventArgs) Implements
    // System.ComponentModel.IBindingList.ListChanged

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private static String MODULE = "Coalesce.Framework.DataModel.XsdRecordSet";

    private Recordset _entityRecordset;

    protected ArrayList<XsdFieldDefinition> _fieldDefinitions;
    protected ArrayList<XsdRecord> _records;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdRecordset Create(XsdSection parent, String name)
    {
        return Create(parent, name, 0, 0);
    }

    public static XsdRecordset Create(XsdSection parent, String name, int MinRecords, int MaxRecords)
    {

        Recordset newEntityRecordset = new Recordset();
        parent.GetEntityRecordSets().add(newEntityRecordset);

        XsdRecordset newRecordset = new XsdRecordset();
        if (!newRecordset.Initialize(parent, newEntityRecordset)) return null;

        newRecordset.SetName(name);
        newRecordset.SetMinRecords(MinRecords);
        newRecordset.SetMaxRecords(MaxRecords);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newRecordset.GetKey()))
        {
            parent._childDataObjects.put(newRecordset.GetKey(), newRecordset);
        }

        return newRecordset;

    }

    public boolean Initialize(XsdSection parent, Recordset recordset)
    {

        // Set References
        _parent = parent;
        _entityRecordset = recordset;

        // Create Collections
        _fieldDefinitions = new ArrayList<XsdFieldDefinition>();
        _records = new ArrayList<XsdRecord>();

        for (Fielddefinition entityFieldDefinition : _entityRecordset.getFielddefinition())
        {
            XsdFieldDefinition newFieldDefinition = new XsdFieldDefinition();
            newFieldDefinition.Initialize(this, entityFieldDefinition);

        }

        for (Record entityRecord : _entityRecordset.getRecord())
        {
            XsdRecord newRecord = new XsdRecord();
            newRecord.Initialize(this, entityRecord);
        }

        return super.Initialize();

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entityRecordset.getKey();
    }

    @Override
    protected void SetObjectKey(String value)
    {
        _entityRecordset.setKey(value);
    }

    @Override
    public String GetName()
    {
        return _entityRecordset.getName();
    }

    @Override
    public void SetName(String value)
    {
        _entityRecordset.setName(value);
    }

    public ArrayList<XsdFieldDefinition> GetFieldDefinitions()
    {
        return this._fieldDefinitions;
    }

    public ArrayList<XsdRecord> GetRecords()
    {
        return this._records;
    }

    public int GetMaxRecords()
    {
        return Integer.parseInt(_entityRecordset.getMaxrecords());
    }

    public void SetMaxRecords(int value)
    {
        _entityRecordset.setMaxrecords(String.valueOf(value));
    }

    public int GetMinRecords()
    {
        return Integer.parseInt(_entityRecordset.getMinrecords());
    }

    public void SetMinRecords(int value)
    {
        _entityRecordset.setMinrecords(String.valueOf(value));
    }

    public boolean GetHasActiveRecords()
    {

        // Iterate Records
        // For Each Record As CoalesceRecord In this._Records
        for (XsdRecord record : GetRecords())
        {
            if (record.GetStatus() == ECoalesceDataObjectStatus.ACTIVE)
            {
                return true;
            }
        }

        // No Active Records
        return false;
    }

    public boolean GetHasRecords()
    {
        return (this.GetRecords().size() > 0);
    }

    public DateTime GetDateCreated()
    {
        try
        {

            // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getDatecreated());
            return _entityRecordset.getDatecreated();

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    @Override
    public void SetDateCreated(DateTime value)
    {
        // _entityRecordset.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecordset.setDatecreated(value);
    }

    @Override
    public DateTime GetLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getLastmodified());
        return _entityRecordset.getLastmodified();
    }

    @Override
    protected void SetObjectLastModified(DateTime value)
    {
        // _entityRecordset.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecordset.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    public XsdFieldDefinition CreateFieldDefinition(String name,
                                                    ECoalesceFieldDataTypes dataType,
                                                    String label,
                                                    String defaultClassificationMarking,
                                                    String defaultValue)
    {
        return XsdFieldDefinition.Create(this, name, dataType, label, defaultClassificationMarking, defaultValue);
    }

    public XsdFieldDefinition CreateFieldDefinition(String name, ECoalesceFieldDataTypes dataType)
    {
        return XsdFieldDefinition.Create(this, name, dataType);
    }

    public String ToXml()
    {
        return XmlHelper.Serialize(_entityRecordset);
    }

    public XsdFieldDefinition GetFieldDefinition(String fieldName)
    {
        try
        {

            // Find
            for (XsdFieldDefinition fieldDefinition : GetFieldDefinitions())
            {
                if (fieldDefinition.GetName().toUpperCase().equals(fieldName.toUpperCase()))
                {

                    return fieldDefinition;
                }
            }

            // Not found
            return null;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, XsdRecordset.MODULE);
            return null;
        }
    }

    public boolean GetAllowEdit()
    {
        return true;
    }

    public boolean GetAllowNew()
    {
        return true;
    }

    public boolean GetAllowRemove()
    {
        return true;
    }

    public int GetCount()
    {
        return GetRecords().size();
    }

    public boolean Contains(Object value)
    { // As Boolean Implements System.Collections.IList.Contains
        try
        {

            return this.GetRecords().contains(value);

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return false;
        }
    }

    public int IndexOf(Object value)
    { // As Integer Implements System.Collections.IList.IndexOf
        try
        {
            // Call on the from the Records Collection
            return this.GetRecords().indexOf(value);

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return -1;
        }
    }

    public XsdRecord AddNew()
    { // As Object Implements System.ComponentModel.IBindingList.AddNew
        try
        {

            // Create new Record
            XsdRecord newRecord = XsdRecord.Create(this, GetName() + " Record");

            // TODO: Raise the Changed Event
            // RaiseEvent ListChanged(this, new
            // ListChangedEventArgs(ListChangedType.ItemAdded,
            // this.GetRecords().Count - 1));

            return newRecord;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return null;
        }
    }

    public XsdRecord GetItem(int index)
    {
        try
        {
            // Iterate List
            if (index >= 0 && index < this.GetRecords().size())
            {
                return GetRecords().get(index);
            }
            else
            {
                return null;
            }

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return null;
        }
    }

    public void RemoveAt(Integer index)
    { // Implements System.Collections.IList.RemoveAt
        try
        {
            // Get Record
            XsdRecord record = (XsdRecord) this.GetItem(index);

            // Evaluate
            if (record != null)
            {

                // Set as Status as Deleted
                record.SetStatus(ECoalesceDataObjectStatus.DELETED);

                // Remove from the Records Collection
                GetRecords().remove(record);

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
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public void Remove(String key)
    {
        try
        {
            XsdRecord recordToRemove = null;

            // Find
            // For Each Record As XsdRecord In this.Records
            for (XsdRecord record : GetRecords())
            {
                if (record.GetKey().equals(key))
                {
                    recordToRemove = record;
                    break;
                }
            }

            // Evaluate
            if (recordToRemove != null)
            {
                // Set as Status as Deleted
                recordToRemove.SetStatus(ECoalesceDataObjectStatus.DELETED);

                // Remove from the Records Collection
                this.GetRecords().remove(recordToRemove);
            }

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectStatus()
    {
        return _entityRecordset.getStatus();
    }

    @Override
    protected void SetObjectStatus(String status)
    {
        _entityRecordset.setStatus(status);
    }

    protected List<Record> GetEntityRecords()
    {
        return _entityRecordset.getRecord();
    }

    protected List<Fielddefinition> GetEntityFieldDefinitions()
    {
        return _entityRecordset.getFielddefinition();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityRecordset.getOtherAttributes();
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