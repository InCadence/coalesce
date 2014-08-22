package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.*;

public class XsdRecordset extends XsdDataObject {
    
    //-----------------------------------------------------------------------//
    // Public Events
    //-----------------------------------------------------------------------//

    // TODO: Java Events
    //public Event ListChanged(ByVal sender As Object, ByVal e As System.ComponentModel.ListChangedEventArgs) Implements System.ComponentModel.IBindingList.ListChanged

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    private static String MODULE = "Coalesce.Framework.DataModel.XsdRecordSet";
    
    private Recordset _entityRecordset;
    
    protected ArrayList<XsdFieldDefinition> _fieldDefinitions;
    protected ArrayList<XsdRecord> _records;

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//
    
    public static CallResult Create(XsdSection parent, XsdRecordset newRecordset, String name) {
        try{
            CallResult rst;
            
            rst = Create(parent, newRecordset, name, 0, 0);

            return rst;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, XsdRecordset.MODULE);
        }
    }

    public static CallResult Create(XsdSection parent,
                                    XsdRecordset newRecordset,
                                    String name,
                                    int MinRecords,
                                    int MaxRecords) {
        try{
            CallResult rst;

            Recordset newEntityRecordset = new Recordset();
            parent.GetEntityRecordSets().add(newEntityRecordset);

            rst = newRecordset.Initialize(parent, newEntityRecordset);
            if (!rst.getIsSuccess()) return rst;
            
            newRecordset.SetName(name);
            newRecordset.SetMinRecords(MinRecords);
            newRecordset.SetMaxRecords(MaxRecords);

            // Add to parent's child collection
            if (!parent._childDataObjects.containsKey(newRecordset.GetKey())) {
                parent._childDataObjects.put(newRecordset.GetKey(), newRecordset);
            }
            
            return CallResult.successCallResult;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, XsdRecordset.MODULE);
        }
    }

    public CallResult Initialize(XsdSection parent, Recordset recordset) {
        try{
            CallResult rst;

            // Set References
            _parent = parent;
            
            _entityRecordset = recordset;

            // Create Collections
            _fieldDefinitions = new ArrayList<XsdFieldDefinition>();
            _records = new ArrayList<XsdRecord>();

            List<Object> FieldDefs = _entityRecordset.getFielddefinition();
            while (FieldDefs.iterator().hasNext()){
            	Fielddefinition entityFieldDefinition = (Fielddefinition)FieldDefs.iterator().next();

                XsdFieldDefinition newFieldDefinition = new XsdFieldDefinition();
                rst = newFieldDefinition.Initialize(this, entityFieldDefinition);
            }

//            for (Fielddefinition entityFieldDefinition : _entityRecordset.fielddefinition) {
//                XsdFieldDefinition newFieldDefinition = new XsdFieldDefinition();
//                rst = newFieldDefinition.Initialize(this, entityFieldDefinition);
//                
//            }
            
            List<Object> Records = _entityRecordset.getRecord();
            while (Records.iterator().hasNext()){
            	Record entityRecord = (Record)Records.iterator().next();

                XsdRecord newRecord = new XsdRecord();
                rst = newRecord.Initialize(this, entityRecord);
            }

//            for (Record entityRecord : _entityRecordset.record) {
//                XsdRecord newRecord = new XsdRecord();
//                rst = newRecord.Initialize(this, entityRecord);
//            }
            
            rst = InitializeEntity();
            
            return rst;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    protected String GetObjectKey()
    {
        return _entityRecordset.getKey();
    }

    public void SetKey(String value)
    {
        _entityRecordset.setKey(value);
    }

    public String GetName()
    {
        return _entityRecordset.getName();
    }

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
        for (XsdRecord record : GetRecords()) {
            if (record.GetStatus() == ECoalesceDataObjectStatus.ACTIVE) {
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
        try {

            //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getDatecreated());
            return _entityRecordset.getDatecreated();

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult SetDateCreated(DateTime value)
    {
        try {
            //_entityRecordset.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityRecordset.setDatecreated(value);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public DateTime GetLastModified()
    {
        try {

            //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecordset.getLastmodified());
            return _entityRecordset.getLastmodified();

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    protected CallResult SetObjectLastModified(DateTime value)
    {
        try {
            //_entityRecordset.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityRecordset.setLastmodified(value);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // Public Methods
    //-----------------------------------------------------------------------//

    public CallResult CreateFieldDefinition(XsdFieldDefinition newFieldDefinition,
                                            String name,
                                            String dataType,
                                            String label,
                                            String defaultClassificationMarking,
                                            String defaultValue)
    {
        return XsdFieldDefinition.Create(this,
                                         newFieldDefinition,
                                         name,
                                         dataType,
                                         label,
                                         defaultClassificationMarking,
                                         defaultValue);
    }

    public CallResult CreateFieldDefinition(XsdFieldDefinition newFieldDefinition,
                                            String name,
                                            String dataType,
                                            String label)
    {
        return XsdFieldDefinition.Create(this, newFieldDefinition, name, dataType);
    }

    public CallResult ToXml(StringBuilder xml)
    {
        try {
            CallResult rst;

            rst = XmlHelper.Serialize(_entityRecordset, xml);

            return rst;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public XsdFieldDefinition GetFieldDefinition(String fieldName)
    {
        try {

            // Find
            for (XsdFieldDefinition fieldDefinition : GetFieldDefinitions()) {
                if (fieldDefinition.GetName().toUpperCase().equals(fieldName.toUpperCase())) {

                    return fieldDefinition;
                }
            }

            // Not found
            return null;
            
        } catch (Exception ex) {
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
        try {

            return this.GetRecords().contains(value);

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return false;
        }
    }

    public int IndexOf(Object value)
    { // As Integer Implements System.Collections.IList.IndexOf
        try {
            // Call on the from the Records Collection
            return this.GetRecords().indexOf(value);

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return -1;
        }
    }

    public Object AddNew()
    { // As Object Implements System.ComponentModel.IBindingList.AddNew
        try {
            CallResult rst;

            // Create new Record
            XsdRecord newRecord = new XsdRecord();
            rst = XsdRecord.Create(this, newRecord, GetName() + " Record");
            if (!rst.getIsSuccess()) return rst;

            // TODO: Raise the Changed Event
            // RaiseEvent ListChanged(this, new
            // ListChangedEventArgs(ListChangedType.ItemAdded,
            // this.GetRecords().Count - 1));

            return newRecord;

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return null;
        }
    }

    public Object GetItem(int index)
    {
        try {
            // Iterate List
            if (index >= 0 && index < this.GetRecords().size()) {
                return GetRecords().get(index);
            } else {
                return null;
            }

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return null;
        }
    }

    public void RemoveAt(Integer index)
    { // Implements System.Collections.IList.RemoveAt
        try {
            // Get Record
            XsdRecord record = (XsdRecord) this.GetItem(index);

            // Evaluate
            if (record != null) {

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

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public void Remove(String key)
    {
        try {
            XsdRecord recordToRemove = null;

            // Find
            // For Each Record As XsdRecord In this.Records
            for (XsdRecord record : GetRecords()) {
                if (record.GetKey().equals(key)) {
                    recordToRemove = record;
                    break;
                }
            }

            // Evaluate
            if (recordToRemove != null) {
                // Set as Status as Deleted
                recordToRemove.SetStatus(ECoalesceDataObjectStatus.DELETED);

                // Remove from the Records Collection
                this.GetRecords().remove(recordToRemove);
            }

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }
    
    //-----------------------------------------------------------------------//
    // Protected Methods
    //-----------------------------------------------------------------------//

    protected CallResult GetObjectStatus(String status)
    {
        try {
            status = _entityRecordset.getStatus();
            
            return CallResult.successCallResult;
        
        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR,ex,this);            
        }
    }
    protected CallResult SetObjectStatus(String status)
    {
        try {
            _entityRecordset.setStatus(status);
            
            return CallResult.successCallResult;
            
        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
    
    protected List<Record> GetEntityRecords() {
    	List<Record> RecordList = new ArrayList<Record>();
    	
    	while (_entityRecordset.getRecord().iterator().hasNext()){
    		Record FieldItem = (Record) _entityRecordset.getRecord().iterator().next();
    		RecordList.add(FieldItem);
    	}
    	return RecordList;
    }
    
    protected List<Fielddefinition> GetEntityFieldDefinitions() {
    	List<Fielddefinition> FieldDefList = new ArrayList<Fielddefinition>();
    	
    	while (_entityRecordset.getFielddefinition().iterator().hasNext()){
    		Fielddefinition FieldDef = (Fielddefinition) _entityRecordset.getFielddefinition().iterator().next();
    		FieldDefList.add(FieldDef);
    	}
    	return FieldDefList;
    }
}

//TODO: CoalesceFieldDefinitionMemberDescriptor class
//public Class CoalesceFieldDefinitionMemberDescriptor
//    Inherits MemberDescriptor
//
//    public Sub New(String Name)
//        MyBase.New(Name)
//    }
//End Class

//TODO: CoalesceFieldDefinitionPropertyDescriptor class
//public Class CoalesceFieldDefinitionPropertyDescriptor
//    Inherits PropertyDescriptor
//
//    public Sub New(CoalesceFieldDefinitionMemberDescriptor MemberDescriptor)
//        MyBase.New(MemberDescriptor)
//    }
//
//    public Overrides Function CanResetValue(Object component) As Boolean
//        // return true; If we're asked to reset the value, we can reset to 
//        // Empty String or CoalesceFielDefinition's Default Value
//        return true
//    }
//
//    public Overrides ReadOnly Property ComponentType As System.Type
//        Get
//            // For this PropertyDescriptor, the Component Type is a CoalesceRecord
//            return GetType(CoalesceRecord)
//        }
//    
//
//    public Overrides Function GetValue(Object component) As Object
//        try{
//            // Cast to CoalesceRecord and Get Field Value
//            return CType(component, CoalesceRecord).GetFieldByName(this.Name).Value
//
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, this)
//
//            // return null
//            return null
//        }
//    }
//
//    public Overrides ReadOnly Property IsReadOnly As Boolean
//        Get
//            // We're not Read Only
//            return false
//        }
//    
//
//    public Overrides ReadOnly Property PropertyType As System.Type
//        Get
//            // For binding, all types are treated as String.
//            return GetType(String)
//        }
//    
//
//    public Overrides Sub ResetValue(Object component){
//        try{
//            if (component != null) {
//                CallResult rst;
//
//                Dim Record As CoalesceRecord = CType(component, CoalesceRecord)
//                Dim Recordset As CoalesceRecordset = CType(Record.Parent, CoalesceRecordset)
//                Dim FieldDefinition As CoalesceFieldDefinition = null
//
//                rst = Recordset.GetFieldDefinition(this.Name, FieldDefinition)
//
//                // Evaluate
//                if (rst.getIsSuccess()) {
//                    Dim Field As CoalesceField = Record.GetFieldByName(this.Name)
//                    Field.ClassificationMarking = FieldDefinition.DefaultClassificationMarking
//                    Field.Value = FieldDefinition.DefaultValue
//                }
//            }
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, this);
//        }
//    }
//
//    @Override
//    public SetValue(Object component, Object value)
//        try{
//            ((CoalesceRecord) component).GetFieldByName(this.Name).Value = value;
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, this);
//        }
//    }
//
//    @Override
//    public boolean ShouldSerializeValue(Object component) {
//        return false;
//    }
//
//}