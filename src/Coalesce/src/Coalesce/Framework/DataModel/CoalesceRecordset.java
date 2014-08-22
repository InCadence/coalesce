package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.soap.Node;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;

public class CoalesceRecordset extends CoalesceDataObject implements ICoalesceRecordset  {

    //-----------------------------------------------------------------------//
    // Pubilc Events
    //-----------------------------------------------------------------------//

    //public Event ListChanged(ByVal sender As Object, ByVal e As System.ComponentModel.ListChangedEventArgs) Implements System.ComponentModel.IBindingList.ListChanged

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    protected ArrayList<CoalesceFieldDefinition> _FieldDefinitions;
    protected ArrayList<CoalesceRecord> _Records;
    XmlHelper _XmlHelper = new XmlHelper();

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//
    
    public CallResult Create(CoalesceSection Parent, CoalesceRecordset NewRecordset, String Name) {
        try{
            // Call on Overloaded Create
            return Create(Parent, NewRecordset, Name, 0, 0);

            // return Success
            //return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceRecordSet");
        }
    }

    public CallResult Create(CoalesceSection Parent, CoalesceRecordset NewRecordset, String Name, int MinRecords, int MaxRecords) {
        try{
            CallResult rst;
            Node NewNode;

            // Create CoalesceRecordset Node
            NewRecordset = new CoalesceRecordset();

            //TODO: is this the right way to create a new node?
            // Create the DataObjectNode
            //NewNode = Parent.DataObjectDocument.CreateNode(XmlNodeType.Element, "recordset", "");
            NewNode = (Node) Parent.GetDataObjectDocument().createElement("recordset");
            Parent.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceRecord Object
            rst = NewRecordset.Initialize(Parent, NewNode);
            if ( !(rst.getIsSuccess()) ) return rst;

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Set Default Values
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewRecordset.Key);
            NewRecordset.SetKey(java.util.UUID.randomUUID().toString());
            NewRecordset.SetName(Name);
            NewRecordset.SetMinRecords(MinRecords);
            NewRecordset.SetMaxRecords(MaxRecords);
            NewRecordset.SetDateCreated(UTCDate);
            NewRecordset.SetLastModified(UTCDate);

            // Add to Parent's Child Collection
            if ( !(Parent.GetChildDataObjects().containsKey(NewRecordset.GetKey()))) {
                Parent.GetChildDataObjects().put(NewRecordset.GetKey(), NewRecordset);
            }

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceRecordSet");
        }
    }

    public CallResult Initialize(CoalesceSection Parent, Node DataObjectNode) {
        try{
            CallResult rst;

            // Set References
            this.SetDataObjectDocument(Parent.GetDataObjectDocument());
            this.SetDataObjectNode(DataObjectNode);
            this.SetParent(Parent);

            // Create Collections
            this._FieldDefinitions = new ArrayList<CoalesceFieldDefinition>();
            this._Records = new ArrayList<CoalesceRecord>();
            
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Check Keys and Timestamps
            //if (this.Key = "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.Key)
            if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
            if (DateTimeHelper.getDateTicks(this.DateCreated) == 0) this.DateCreated = UTCDate;
            if (DateTimeHelper.getDateTicks(this.LastModified) == 0) this.LastModified = UTCDate;

            // Iterate Child Nodes
            //For Each Node As XmlNode In this.DataObjectNode.ChildNodes
	        for(int i=0; i < this.GetDataObjectNode().getChildNodes().getLength(); i++){
	        	Node XmlNode = (Node) this.GetDataObjectNode().getChildNodes().item(i);

                // case on Element
	        	//TODO: is getNodeName() the right way to get the Name?
                switch (XmlNode.getNodeName()){

                    case "fielddefinition":
                        // Create a FieldDefinition Object
                    	CoalesceFieldDefinition NewFieldDefinition = new CoalesceFieldDefinition();
                        rst = NewFieldDefinition.Initialize(this, XmlNode);
                        if ( !(rst.getIsSuccess()) ) return rst;

                    case "record":
                        // Create a Record Object
                    	CoalesceRecord NewRecord = new CoalesceRecord();
                        rst = NewRecord.Initialize(this, XmlNode);
                        if ( !(rst.getIsSuccess()) ) return rst;

                }

            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    public String Name; //() As String Implements ICoalesceRecordset.Name
    public String GetName(){
    	return super.Name;
    }
    public void SetName(String value){
    	//vb MyBase = java super
    	super.Name = value;
    }

    //readonly
    public ArrayList<CoalesceFieldDefinition> FieldDefinitions;
    public ArrayList<CoalesceFieldDefinition> GetFieldDefinitions(){
    	return this._FieldDefinitions;
    }
    
    //readonly
    public ArrayList<CoalesceRecord> Records;
    public ArrayList<CoalesceRecord> GetRecords(){
    	return this._Records;
    }
    
    public int MaxRecords; //() As Integer Implements ICoalesceRecordset.MaxRecords
    @Override
    public int GetMaxRecords(){
    	return Integer.parseInt(_XmlHelper.GetAttribute(this.GetDataObjectNode(), "maxrecords"));
	}
    @Override
	public void SetMaxRecords(int value){
		_XmlHelper.SetAttribute(this.GetDataObjectDocument(), this.GetDataObjectNode(), "maxrecords", String.valueOf(value));
	}

    public int MinRecords; //() As Integer Implements ICoalesceRecordset.MinRecords
    @Override
    public int GetMinRecords(){
    	return Integer.parseInt(_XmlHelper.GetAttribute(this.GetDataObjectNode(), "minrecords"));
    }
    @Override
    public void SetMinRecords(int value){
    	_XmlHelper.SetAttribute(this.GetDataObjectDocument(), this.GetDataObjectNode(), "minrecords", String.valueOf(value));
    }

    //readonly
    public boolean HasActiveRecords;
    public boolean GetHasActiveRecords(){
        // Iterate Records
        //For Each Record As CoalesceRecord In this._Records
        for(int i=0; i < this.GetRecords().size(); i++){
        	CoalesceRecord Record = this.GetRecords().get(i);
            if (Record.GetDataObjectStatus() == ECoalesceDataObjectStatus.ACTIVE) {
                // Has at least one Active Record; return true
                return true;
            }
        }

        // No Active Records
        return false;
    }

    //readonly
    public boolean HasRecords;
    public boolean GetHasRecords(){
        // Do we have at least one record?
        return (this.GetRecords().size() > 0);
    }
    

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult CreateFieldDefinition(CoalesceFieldDefinition NewFieldDefinition, String Name, String DataType, String Label, String DefaultClassificationMarking, String DefaultValue){
        return NewFieldDefinition.Create(this, NewFieldDefinition, Name, DataType, Label, DefaultClassificationMarking, DefaultValue);
    }

    public CallResult CreateFieldDefinition(CoalesceFieldDefinition NewFieldDefinition, String Name, String DataType, String Label){
        return NewFieldDefinition.Create(this, NewFieldDefinition, Name, DataType);
    }

    public CallResult ToXml(String Xml) {
        try{
            // Examine XmlNode
            if (this.GetDataObjectNode() != null) {
                // Get Xml
            	//TODO: confirm getNodeValue IS replacement for OuterXml;
                Xml = this.GetDataObjectDocument().getNodeValue(); //.OuterXml;
            }else{
                // null
                Xml = "";
            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetFieldDefinition(String FieldName, CoalesceFieldDefinition Definition) {
        try{
            // Set to null
            Definition = null;

            // Find
            //For Each Def As CoalesceFieldDefinition In this.FieldDefinitions
            for(int i=0; i < this.GetFieldDefinitions().size(); i++){
            	CoalesceFieldDefinition Def = this.GetFieldDefinitions().get(i);
                if (Def.GetName().toUpperCase() == FieldName.toUpperCase()) {
                    Definition = Def;
                    break;
                }
            }

            // Evaluate
            if (Definition != null) {
                return CallResult.successCallResult;
            }else{
                return new CallResult(CallResults.FAILED, "CoalesceFieldDefinition not found for name " + FieldName, "Coalesce.Framework.DataModel.CoalesceRecordSet");
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceRecordSet");
        }
    }

    //readonly
    public boolean AllowEdit; //Implements System.ComponentModel.IBindingList.AllowEdit
    public boolean GetAllowEdit(){
    	return true;
    }
    
    //readonly
    public boolean AllowNew; //Implements System.ComponentModel.IBindingList.AllowNew
    public boolean GetAllowNew(){
        return true;
    }
    
//readonly
    public boolean AllowRemove; //Implements System.ComponentModel.IBindingList.AllowRemove
    public boolean GetAllowRemove(){
        return true;
    }
    

    /* <summary>
    * Count of the number of records in the recordset
    * </summary>
    * <value></value>
    * <returns></returns>
    * <remarks></remarks>
    */
    //readonly
    public int Count; //Implements System.Collections.ICollection.Count
    public int GetCount(){
        return this.Records.size();
    }
    
	//readonly
    public boolean IsFixedSize; //As Boolean Implements System.Collections.IList.IsFixedSize
    public boolean GetIsFixedSize(){
        return false;
    }
    
    //readonly
//    public boolean IsReadOnly; //As Boolean Implements System.Collections.IList.IsReadOnly
//    public boolean GetIsReadOnly(){
//        return false;
//    }
    
    //readonly
    public boolean IsSorted; //As Boolean Implements System.ComponentModel.IBindingList.IsSorted
    public boolean GetIsSorted(){
        return false;
    }
    
    //readonly
    public boolean IsSynchronized; //As Boolean Implements System.Collections.ICollection.IsSynchronized
    public boolean GetIsSynchronized(){
        return false;
    }
    
    //readonly
    public boolean SyncRoot; //As Object Implements System.Collections.ICollection.SyncRoot
    @SuppressWarnings("null")
	public boolean GetSyncRoot(){
    	return (Boolean) null;
    }

    //readonly
    public boolean SupportsChangeNotification; //As Boolean Implements System.ComponentModel.IBindingList.SupportsChangeNotification
    public boolean GetSupportsChangeNotification(){
        return true;
    }
    
    //readonly
    public boolean SupportsSearching; //Implements System.ComponentModel.IBindingList.SupportsSearching
    public boolean GetSupportsSearching(){
    	return false;
    }

    //readonly
    public boolean SupportsSorting; //Implements System.ComponentModel.IBindingList.SupportsSorting
    public boolean GetSupportsSorting(){
    	return false;
    }

    //readonly
    public boolean RaisesItemChangedEvents; //Implements System.ComponentModel.IRaiseItemChangedEvents.RaisesItemChangedEvents
    public boolean GetRaisesItemChangedEvents(){
    	return true;
    }
    
    //TODO: IEnumerator?
//    public Function GetEnumerator() As System.Collections.IEnumerator Implements System.Collections.IEnumerable.GetEnumerator
//        try{
//            // Call on the Records Collection
//            return this.Records.GetEnumerator
//
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, this)
//
//            // return null
//            return null
//        }
//    }

    public boolean Contains(Object value){ //As Boolean Implements System.Collections.IList.Contains
        try{
            // Call on the Records Collection
            return this.GetRecords().contains(value);

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            // return false
            return false;
        }
    }

    public int IndexOf(Object value){ //As Integer Implements System.Collections.IList.IndexOf
        try{
            // Call on the from the Records Collection
            return this.GetRecords().indexOf(value);

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            // return -1
            return -1;
        }
    }

    //TODO: IBindingList.AddNew
//    public Object AddNew(){ //As Object Implements System.ComponentModel.IBindingList.AddNew
//        try{
//            CallResult rst;
//
//            // Create new Record
//            CoalesceRecord NewRecord = null;
//            rst = CoalesceRecord.Create(this, NewRecord, Name + " Record");
//            if ( !(rst.getIsSuccess()) ) return rst;
//
//            // Raise the Changed Event
//            RaiseEvent ListChanged(this, new ListChangedEventArgs(ListChangedType.ItemAdded, this.GetRecords().Count - 1));
//
//            // return the new Record
//            return NewRecord;
//
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, this);
//
//            // return null
//            return null;
//        }
//    }

    //Default public Property Item(Integer index) As Object Implements System.Collections.IList.Item
    public Object Item;
    public Object GetItem(int index){
        try{
            // Iterate List
            if (index >= 0 && index < this.GetRecords().size()) {
                return this.GetRecords().get(index);
            }else{
                return null;
            }

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            // return null
            return null;
        }
    }
    public void SetItem(Object value){
        // Not Implemented for CoalesceRecordSet
    	System.out.println("CoalesceRecordset Set Item");
    }
    
    public void RemoveAt(Integer index){ //Implements System.Collections.IList.RemoveAt
        try{
            // Get Record
        	CoalesceRecord Record = (CoalesceRecord) this.GetItem(index);

            // Evaluate
            if (Record != null) {

                // Set as Status as Deleted
                Record.SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);

                // Remove from the Records Collection
                this.GetRecords().remove(Record);

//                // Determine new Index
//                int NewIndex;
//                if (index == 0) {
//                    if (this.Count > 0) {
//                        NewIndex = 0;
//                    }else{
//                        NewIndex = -1;
//                    }
//                }else{
//                    NewIndex = index - 1;
//                }

                // TODO: Raise ListChanged Event
                //RaiseEvent ListChanged(this, new ListChangedEventArgs(ListChangedType.ItemDeleted, NewIndex));
            }

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public void Remove(String Key){
        try{
        	CoalesceRecord RecordToRemove = null;

            // Find
            //For Each Record As CoalesceRecord In this.Records
            for(int i=0; i < this.GetRecords().size(); i++){
            	CoalesceRecord Record = this.GetRecords().get(i);
                if (Record.GetKey() == Key) {
                    // Found; Remove
                    RecordToRemove = Record;
                    break;
                }
            }
                        
            // Evaluate
            if (RecordToRemove != null) {
                // Set as Status as Deleted
                RecordToRemove.SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);

                // Remove from the Records Collection
                this.GetRecords().remove(RecordToRemove);
            }

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    private void Remove(Object value){ //Implements System.Collections.IList.Remove
        // Not Implemented for CoalesceRecordSet
    	System.out.println("CoalesceRecordset Remove");
    }

    //TODO: PropertyDescriptorCollection, CoalesceFieldDefinitionPropertyDescriptor
//    //public Function GetItemProperties(ByVal listAccessors() As System.ComponentModel.PropertyDescriptor) As System.ComponentModel.PropertyDescriptorCollection Implements System.ComponentModel.ITypedList.GetItemProperties
//    public System.ComponentModel.PropertyDescriptorCollection GetItemProperties(System.ComponentModel.PropertyDescriptor[] listAccessors) { //As System.ComponentModel.PropertyDescriptorCollection Implements System.ComponentModel.ITypedList.GetItemProperties
//    	PropertyDescriptorCollection pdc;
//
//    	CoalesceFieldDefinitionPropertyDescriptor props(this.GetFieldDefinitions().size() - 1);
//    	
//        //For i As Integer = 0 To (this.FieldDefinitions.Count - 1)
//        for(int i=0; i < this.GetFieldDefinitions().size(); i++){
//            props(i) = new CoalesceFieldDefinitionPropertyDescriptor(this.GetFieldDefinitions().get(i).GetName());
//        }
//
//        pdc = new PropertyDescriptorCollection(props);
//
//        return pdc;
//    }

    //PropertyDescriptor
//    public String GetListName(System.ComponentModel.PropertyDescriptor[] listAccessors) { //Implements System.ComponentModel.ITypedList.GetListName
//    	CoalesceRecord rec = new CoalesceRecord(); 
//        //return GetType(CoalesceRecord).Name;
//    	return rec.GetName();
//    }

    private void CopyTo(ArrayList array, Integer index){ //Implements System.Collections.ICollection.CopyTo
        // Not Implemented for CoalesceRecordSet
    	System.out.println("CoalesceRecordset CopyTo");
    }

    private void Insert(Integer index, Object value){ //Implements System.Collections.IList.Insert
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset Insert");
    }

    //TODO: ApplySort
//    private void ApplySort(System.ComponentModel.PropertyDescriptor [property], System.ComponentModel.ListSortDirection direction){ //Implements System.ComponentModel.IBindingList.ApplySort
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset ApplySort");
//    }

    //TODO: PropertyDescriptor
//    private int Find(System.ComponentModel.PropertyDescriptor [property], Object key) { //As Integer Implements System.ComponentModel.IBindingList.Find 
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset Find");
//    }

    //TODO: PropertyDescriptor
//    private void RemoveIndex(System.ComponentModel.PropertyDescriptor [property]) { //Implements System.ComponentModel.IBindingList.RemoveIndex
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset RemoveIndex");
//    }

    private void RemoveSort() { //Implements System.ComponentModel.IBindingList.RemoveSort
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset RemoveSort");
    }

    //TODO: System.ComponentModel.ListSortDirection
//    private System.ComponentModel.ListSortDirection SortDirection;  //As System.ComponentModel.ListSortDirection Implements System.ComponentModel.IBindingList.SortDirection
//    private System.ComponentModel.ListSortDirection GetSortDirection(){
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset SortDirection");
//        return ListSortDirection.Ascending;
//    }
    
    //TODO: PropertyDescriptor
//    private PropertyDescriptor SortProperty //As System.ComponentModel.PropertyDescriptor Implements System.ComponentModel.IBindingList.SortProperty
//    private PropertyDescriptor GetSortProperty(){
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset SortProperty");
//        return null;
//    }
    

    private int Add(Object value) { //As Integer Implements System.Collections.IList.Add
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset Add");
        return 0;
    }

    private void Clear() { //Implements System.Collections.IList.Clear
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset Clear");
    }

    //TODO: PropertyDescriptor
//    private void AddIndex(System.ComponentModel.PropertyDescriptor [property]) //Implements System.ComponentModel.IBindingList.AddIndex
//        // Not Implemented for CoalesceRecordSet
//        System.out.println("CoalesceRecordset AddIndex");
//    }

    private void CancelNew(int itemIndex){ //Implements System.ComponentModel.ICancelAddNew.CancelNew
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset CancelNew");
    }

    private void EndNew(int itemIndex){ //Implements System.ComponentModel.ICancelAddNew.EndNew
        // Not Implemented for CoalesceRecordSet
        System.out.println("CoalesceRecordset EndNew");
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
