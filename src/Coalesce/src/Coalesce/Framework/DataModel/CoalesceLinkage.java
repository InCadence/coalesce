package Coalesce.Framework.DataModel;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceLinkage extends CoalesceDataObject implements ICoalesceLinkage {

	XmlHelper _XmlHelper = new XmlHelper();
    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceLinkageSection ParentLinkageSection, CoalesceLinkage NewLinkage){
        try{
            CallResult rst;
            Node NewNode;

            // Create CoalesceLinkage Node
            NewLinkage = new CoalesceLinkage();

            //TODO: is this the right way to create a new node?
            // Create the DataObjectNode
            //NewNode = ParentLinkageSection.DataObjectDocument.CreateNode(XmlNodeType.Element, "linkage", "");
            NewNode = (Node) ParentLinkageSection.GetDataObjectDocument().createElement("linkage");
            ParentLinkageSection.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceLinkage Object
            rst = NewLinkage.Initialize(ParentLinkageSection, NewNode);
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Set Default Values
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewLinkage.Key);
            NewLinkage.SetKey(java.util.UUID.randomUUID().toString());
            NewLinkage.Name = "Linkage";
            NewLinkage.SetDateCreated(UTCDate);
            NewLinkage.SetLastModified(UTCDate);

            // Add to Parent's Child Collection
            if (! (ParentLinkageSection.GetChildDataObjects().containsKey(NewLinkage.GetKey()))) {
                ParentLinkageSection.GetChildDataObjects().put(NewLinkage.GetKey(), NewLinkage);
            }

            // return SUCCESS
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceLinkage");
        }
    }

    public CallResult Initialize(ICoalesceDataObject Parent, Node DataObjectNode) {
        try{
            //CallResult rst;

            // Set References
            this.SetDataObjectNode(Parent.GetDataObjectNode());
            this._DataObjectNode = DataObjectNode;
            this.SetParent(Parent);

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;

//            //TODO: Check Keys
//            if (this.Key = "") rst == GUIDHelper.GetGuidString(Guid.NewGuid, this.Key);
	        if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    public String Name;
    public String GetName(){
    	//TODO: confirm super is MyBase, for MyBase.Name;
    	//return MyBase.Name;
    	return super.GetName();
    }
    public void SetName(String value){
    	//TODO: confirm super is MyBase, for MyBase.Name;
    	//MyBase.Name = value
    	super.SetName(value);
    }
    
    public String ModifiedBy;
    public String GetModifiedBy(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "modifiedby");
    }
    public void SetModifiedBy(String value){
    	this.Change("modifiedby", value);
    	ModifiedBy = value;
    }
    
    public String ClassificationMarking;
    public String GetClassificationMarking(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "classificationmarking");
    }
    public void SetClassificationMarking(String value){
    	this.Change("classificationmarking", value);
    	ClassificationMarking = value;
    }
    
    public String Entity1Key;
    public String GetEntity1Key(){
        return _XmlHelper.GetAttribute(this._DataObjectNode, "entity1key");
    }
    public void SetEntity1Key(String value){
            this.Change("entity1key", value);
            Entity1Key = value;
        }
    
    public String Entity1Name;
    public String GetEntity1Name(){
        return _XmlHelper.GetAttribute(this._DataObjectNode, "entity1name");
        }
    public void SetEntity1Name(String value){
            this.Change("entity1name", value);
            Entity1Name = value;
        }
    
    public String Entity1Source;
    public String GetEntity1Source(){
		return _XmlHelper.GetAttribute(this._DataObjectNode, "entity1source");
    }
    public void SetEntity1Source(String value){
            this.Change("entity1source", value);
            Entity1Source = value;
        }
    
    public String Entity1Version;
    public String GetEntity1Version(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "entity1version");
    	}
    public void SetEntity1Version(String value){
            this.Change("entity1version", value);
            Entity1Version = value;
        }
    

    public String Entity2Key;
    public String GetEntity2Key(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "entity2key");
    }
    public void SetEntity2Key(String value){
        this.Change("entity2key", value);
        Entity2Key = value;
    }
    
    public String Entity2Name;
    public String GetEntity2Name(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "entity2name");
    }
    public void SetEntity2Name(String value){
        this.Change("entity2name", value);
        Entity2Name = value;
    }
    
    public String Entity2Source;
    public String GetEntity2Source(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "entity2source");
    }
    public void SetEntity2Source(String value){
        this.Change("entity2source", value);
        Entity2Source = value;
    }
    
    public String Entity2Version;
    public String GetEntity2Version(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "entity2version");
    }
    public void SetEntity2Version(String value){
        this.Change("entity2version", value);
        Entity2Version = value;
    }
    
    public String InputLang;
    public String GetInputLang(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "inputlang");
    }
    public void SetInputLang(String value){
        this.Change("inputlang", value);
        InputLang = value;
    }
    
    public String LinkType;
    public String GetLinkType(){
    	String Val = _XmlHelper.GetAttribute(this._DataObjectNode, "linktype");
    	ELinkTypes MyLinkType = ELinkTypes.Undefined; //0;

        // Switch to string english name if this is an integer. (Could be a bitwise combination of LinkType enumerations)
//        if (integer.tryParse(Val, MyLinkType)) {
//            return MyLinkType;
//        }else{
//            return GetLinkTypeForName(Val);
//        }
        int myVal = 0;
        try {
        	myVal = Integer.parseInt(Val);
          } catch (NumberFormatException e) {
        	  if(Val.length() > 0){
        		  myVal = MyLinkType.GetELinkTypeCodeForLabel(Val);
        	  }else{
        		  myVal = MyLinkType.GetELinkTypeCodeForLabel(MyLinkType.GetELinkTypeLabelForType(MyLinkType));
        	  }
            
          }
        return Integer.toString(myVal);
    }

    public void SetLinkType(String value){
    	ELinkTypes MyLinkType = ELinkTypes.Undefined; //0;

        // Switch to string english name if this is an integer. (Could be a bitwise combination of LinkType enumerations)
    	try{
    		int type = Integer.parseInt(value);
    		this.Change("linktype", MyLinkType.GetELinkTypeLabelForCode(type));
    	}catch(NumberFormatException e){
    		this.Change("linktype", MyLinkType.GetELinkTypeLabelForCode(MyLinkType.GetELinkTypeCodeForLabel(value)));
    	}
//        if (Integer.tryParse(value, MyLinkType)) {
//            this.Change("linktype", GetNameForLinkType(MyLinkType));
//        }else{
//            this.Change("linktype", GetNameForLinkType(GetLinkTypeForName(value)));
//        }
//
//        this.Change("linktype", value);
    }

        //readonly
        public boolean IsMarkedDeleted;
        public boolean GetIsMarkedDeleted(){
        	return (this.DataObjectStatus == ECoalesceDataObjectStatus.DELETED);
		}
    

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult EstablishLinkage(CoalesceEntity Entity1, ELinkTypes LinkType, CoalesceEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang) {
        try{
            // Set Values
            this.SetEntity1Key(Entity1.GetKey());
            this.SetEntity1Name(Entity1.GetName());
            this.SetEntity1Source(Entity1.GetSource());
            this.SetEntity1Version(Entity1.GetVersion());
            this.SetLinkType(GetNameForLinkType(LinkType));
            this.SetEntity2Key(Entity2.GetKey());
            this.SetEntity2Name(Entity2.GetName());
            this.SetEntity2Source(Entity2.GetSource());
            this.SetEntity2Version(Entity2.GetVersion());
            this.SetClassificationMarking(ClassificationMarking);
            this.SetModifiedBy(ModifiedBy);
            this.SetInputLang(InputLang);
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);
            this.SetLastModified(UTCDate);
            this.SetDataObjectStatus(ECoalesceDataObjectStatus.ACTIVE);

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult ToXml(String Xml) {
        try{
            // Examine XmlNode
            if (this._DataObjectNode != null) {
                // Get Xml
                //Xml = this._DataObjectDocument.OuterXml;
                
    			JAXBContext context = JAXBContext.newInstance(CoalesceLinkage.class);
    			Marshaller marshaller = context.createMarshaller();
    			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // pretty
    		    marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1"); // specify encoding
    		      
    			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
    			marshaller.marshal(this, out);
    						
    			Xml = new String (out.toByteArray());
    			
            }else{
                // Nothing
                Xml = "";
            }

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // Private and protected Methods
    //-----------------------------------------------------------------------//

    protected CallResult Change(String AttributeName, String Value) { 
        try{
            // Change Attribute Value
            _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, AttributeName, Value);

        	//TODO: verify utcdate is coming back from datetimehelper
            // Set LastModified
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);
            this.LastModified = UTCDate;

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Shared Methods
    //-----------------------------------------------------------------------//

    public ELinkTypes GetReciprocalLinkType(ELinkTypes LinkType) { 
        try{
            switch(LinkType){

                case Undefined:
                    return ELinkTypes.Undefined;

                case IsParentOf:
                    return ELinkTypes.IsChildOf;

                case IsChildOf:
                    return ELinkTypes.IsParentOf;

                case Created:
                    return ELinkTypes.WasCreatedBy;

                case WasCreatedBy:
                    return ELinkTypes.Created;

                case HasMember:
                    return ELinkTypes.IsAMemberOf;

                case IsAMemberOf:
                    return ELinkTypes.HasMember;

                case HasParticipant:
                    return ELinkTypes.IsAParticipantOf;

                case IsAParticipantOf:
                    return ELinkTypes.HasParticipant;

                case IsWatching:
                    return ELinkTypes.IsBeingWatchedBy;

                case IsBeingWatchedBy:
                    return ELinkTypes.IsWatching;

                case IsAPeerOf:
                    return ELinkTypes.IsAPeerOf;

                case IsOwnedBy:
                    return ELinkTypes.HasOwnershipOf;

                case HasOwnershipOf:
                    return ELinkTypes.IsOwnedBy;

                case IsUsedBy:
                    return ELinkTypes.HasUseOf;

                case HasUseOf:
                    return ELinkTypes.IsUsedBy;

            }

            return ELinkTypes.Undefined;

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinakge");

            // return Undefined
            return ELinkTypes.Undefined;
        }
    }

//    public ELinkTypes GetLinkTypeForName(String LinkTypeName) { 
//        try{
//        	ELinkTypes elt = new ELinkTypes();
//        	return elt.GetELinkTypeTypeForCode(elt.GetELinkTypeCodeForLabel(LinkTypeName));
//        	
////            // Parse String Name
////            return System.Enum.Parse(GetType(ELinkTypes), LinkTypeName);
//
//        }catch(Exception ex){
//            // Log
//            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinkage");
//
//            // Default to English
//            return ELinkTypes.Undefined;
//        }
//    }
//
    public String GetNameForLinkType(ELinkTypes LinkType) { 
        try{
            // Get String for Enumerator
        	return LinkType.GetELinkTypeLabelForType(LinkType);
            //return System.Enum.GetName(GetType(ELinkTypes), LinkType);

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinkage");

            return "Undefined";
        }
    }

}
