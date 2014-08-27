package Coalesce.Framework.DataModel;

import java.util.List;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;

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

public class XsdSection extends XsdDataObject {

	private static String MODULE = "Coalesce.Framework.DataModel.CoalesceSection";
	
	private Section _entitySection;
	
	//-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

	// TODO: Nested sections are not currently part of the Entity object
/*    public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name) {
    try{
    	return Create(Parent, NewSection, Name, false);
    }catch(Exception ex){
        // return Failed Error
        return new CallResult(CallResults.FAILED_ERROR, ex, XsdSection.MODULE);
    }
}
	
    public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name, boolean NoIndex) {
        try{
            CallResult rst;
            Node NewNode = null;

            // Create CoalesceSection Node
            NewSection = new CoalesceSection();

            //TODO:Node creation (remove null assignment above)
            // Create the DataObjectNode
            //.DOCUMENT_NODE  //NewNode = Parent.GetDataObjectDocument().CreateNode(Node.ELEMENT_NODE, "section", "");
            //NewNode = Parent.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "section", "");
            Parent.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceField Object
            rst = NewSection.Initialize(Parent, NewNode);
            if ( !(rst.getIsSuccess())) return rst;

            Date UTCDate = new Date();
            DateTimeHelper dth = new DateTimeHelper();
            dth.ConvertDateToGMT(UTCDate);

            // Set Default Values
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewSection.Key)
            NewSection.SetKey(java.util.UUID.randomUUID().toString());
            NewSection.SetName(Name);
            NewSection.SetDateCreated(UTCDate);
            NewSection.SetLastModified(UTCDate);
            if (NoIndex) NewSection.NoIndex = true;

            // Add to Parent's Child Collection
            if ( !(Parent.GetChildDataObjects().containsKey(NewSection.GetKey())) ) {
                Parent.GetChildDataObjects().put(NewSection.GetKey(), NewSection);
            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, XsdSection.MODULE);
        }
    }
*/
	
	public static CallResult Create(XsdEntity parent, XsdSection newSection, String name)
	{
		try {
			
			return XsdSection.Create(parent, newSection, name, false);
			
		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdSection.MODULE);
		}
	}

    public static CallResult Create(XsdEntity parent, XsdSection newSection, String name, boolean noIndex) {
        try{
            CallResult rst;

            Section newEntitySection = new Section();
            parent.GetEntitySections().add(newEntitySection);

            rst = newSection.Initialize(parent, newEntitySection);
            if (!rst.getIsSuccess()) return rst;

            newSection.SetName(name);
            
            newSection.SetNoIndex(noIndex);

            // Add to parent's child collection
            if (!parent._childDataObjects.containsKey(newSection.GetKey())) {
            	parent._childDataObjects.put(newSection.GetKey(), newSection);
            }
            
            return CallResult.successCallResult;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, XsdSection.MODULE);
        }
    }

    // TODO: Need to get Entity with nested sections
 /*   public CallResult Initialize(CoalesceSection Parent, Node DataObjectNode) { 
        try{
            // Since the Section can be initialized with an Entity as a parent or
            // another Section as a parent, we have a private method that takes in
            // an ICoalesceDataObject, and these public methods expose the strongly
            // typed interface to outside callers.
            return this.Initialize((ICoalesceDataObject)Parent, DataObjectNode);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
*/
    public CallResult Initialize(XsdEntity parent, Section section) {
        try{
            CallResult rst;

            _parent = parent;
           
            _entitySection = section;
            
            rst = InitializeEntity();

            for (Recordset childRecordSet : _entitySection.getRecordset()) {
	            
            	XsdRecordset newRecordSet = new XsdRecordset();
            	rst = newRecordSet.Initialize(this, childRecordSet);
            	if (!rst.getIsSuccess()) continue;
            	
            	if (!_childDataObjects.containsKey(newRecordSet.GetKey())) {
            		_childDataObjects.put(newRecordSet.GetKey(), newRecordSet);
            	}
            }

            // TODO: Need to add another loop child Sections if they are added

            return CallResult.successCallResult;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    protected String GetObjectKey() {
    	return _entitySection.getKey();
    }
    public void SetKey(String value) {
    	_entitySection.setKey(value);
    }
    
    public String GetName() {
    	return _entitySection.getName();
    }
    public void SetName(String value) {
    	_entitySection.setName(value);
    }
    
    // TODO: Need nested sections
/*    public CallResult CreateSection(XsdSection newSection, String name) {
        try{
            CallResult rst;

            // Create new Section
            rst = XsdSection.Create(this, newSection, name);
            if (!rst.getIsSuccess()) return rst;

            return CallResult.successCallResult;

        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
*/
    
	public CallResult CreateRecordset(XsdRecordset newRecordset, String name)
	{
		try {
			CallResult rst;

			// Create new Record
			rst = XsdRecordset.Create(this, newRecordset, name);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult ToXml(StringBuilder xml)
	{
		try {
			CallResult rst;

			rst = XmlHelper.Serialize(_entitySection, xml);
			
			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

    public DateTime GetDateCreated() {
    	try {
    		
	        //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getDatecreated());
    		return _entitySection.getDatecreated();

    	} catch (Exception ex) {
        	CallResult.log(CallResults.FAILED_ERROR, ex, this);
        	return null;
        } 
    }
    public CallResult SetDateCreated(DateTime value) {
    	try {
    		//_entitySection.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
    		_entitySection.setDatecreated(value);
    		
    		return CallResult.successCallResult;
    		
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR, ex, this);
    	}
    }

    public DateTime GetLastModified() {
    	try {
    		
	        //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getLastmodified());
    		return _entitySection.getLastmodified();

    	} catch (Exception ex) {
        	CallResult.log(CallResults.FAILED_ERROR, ex, this);
        	return null;
        } 
    }
    protected CallResult SetObjectLastModified(DateTime value) {
    	try {
    		//_entitySection.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
    		_entitySection.setLastmodified(value);
    		
    		return CallResult.successCallResult;
    		
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR, ex, this);
    	}
    }

    //-----------------------------------------------------------------------//
    // Protected Methods
    //-----------------------------------------------------------------------//

    protected CallResult GetObjectStatus(String status)
    {
    	try {
	    	status = _entitySection.getStatus();
	    	
	    	return CallResult.successCallResult;
    	
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR,ex,this);    		
    	}
    }
    protected CallResult SetObjectStatus(String status)
    {
    	try {
    		_entitySection.setStatus(status);
    		
    		return CallResult.successCallResult;
    		
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR, ex, this);
    	}
    }
    
    protected CallResult GetObjectNoIndex(String value)
    {
    	try {
    		value = _entitySection.getNoindex();
    	
    		return CallResult.successCallResult;
    		
        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
    protected CallResult SetObjectNoIndex(String value) {
    	try {
    		_entitySection.setNoindex(value);
    	
    		return CallResult.successCallResult;
    		
        }catch(Exception ex){
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
    
    protected List<Recordset> GetEntityRecordSets() {
    	return _entitySection.getRecordset();
    }

}
