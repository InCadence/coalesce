package Coalesce.Framework.DataModel;

//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.StringReader;
import java.util.*;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;

//import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceDataObject implements ICoalesceDataObject {
	
	//-----------------------------------------------------------------------
    // Protected Member Variables
    //-----------------------------------------------------------------------

    protected Document _DataObjectDocument;
    protected Node _DataObjectNode;
    protected ICoalesceDataObject _ParentDataObject;
    protected Map<String, ICoalesceDataObject> _ChildDataObjects;
	XmlHelper _XmlHelper = new XmlHelper();

    //-----------------------------------------------------------------------
    // Constructor and Initialization
    //-----------------------------------------------------------------------

    public void New(){
        // Initialize DataObjects Collection
    	//_ChildDataObjects = Maps.newHashMap(ImmutableMap.<String, Integer>
    	//_ChildDataObjects = new Dictionary<String, ICoalesceDataObject>(){};
    	_ChildDataObjects = new HashMap<String, ICoalesceDataObject>();

    }

    //-----------------------------------------------------------------------
    // Public Properties
    //-----------------------------------------------------------------------
    @Override
    public Document GetDataObjectDocument() {
        return _DataObjectDocument;
    }
    public void SetDataObjectDocument(Document value){
        _DataObjectDocument = value;
    }
    
    @Override
    public Node GetDataObjectNode(){
        return _DataObjectNode;
    }
    public void SetDataObjectNode(Node value){
        _DataObjectNode = value;
    }
    
    @Override
    public ICoalesceDataObject GetParent(){
        return _ParentDataObject;
	}
    
	public void SetParent(ICoalesceDataObject value){
	        _ParentDataObject = value;
	}

    //TODO (?) set read only
    @Override
    public Map<String, ICoalesceDataObject> GetChildDataObjects(){
            return _ChildDataObjects;
    }

    public String key;
    @Override
    public String GetKey(){
        return _XmlHelper.GetAttribute(_DataObjectNode, "key");
    }
	@Override
    public void SetKey(String value){
		_XmlHelper.SetAttribute(_DataObjectDocument, _DataObjectNode, "key", value);
    }
    
    public String Tag;
    @Override
    public String GetTag(){
        return _XmlHelper.GetAttribute(_DataObjectNode, "tag");
    }
	@Override
    public void SetTag(String value){
		_XmlHelper.SetAttribute(_DataObjectDocument, _DataObjectNode, "tag", value);
    }
    
    public String Name;
    @Override
    public String GetName(){
    	return _XmlHelper.GetAttribute(_DataObjectNode, "name");
    }
    @Override
    public void SetName(String value){
    	_XmlHelper.SetAttribute(_DataObjectDocument, _DataObjectNode, "name", value);
    }
    
    public java.util.Date DateCreated;
    @Override
    public java.util.Date GetDateCreated(){
    	return (Date)_XmlHelper.GetAttributeAsDate(_DataObjectNode, "datecreated");
    }
    @Override
    public void SetDateCreated(java.util.Date value){
    	_XmlHelper.SetAttributeAsDate(_DataObjectDocument, _DataObjectNode, "datecreated", value);
    }

    public java.util.Date LastModified;
    @Override
    public java.util.Date GetLastModified(){
    	return _XmlHelper.GetAttributeAsDate(_DataObjectNode, "lastmodified");
    }
    @Override
    public void SetLastModified(java.util.Date value){
    	CallResult rst;
        rst = SetAttributeAsDate("lastmodified", value);

        if (rst.getIsSuccess()) {
        	// Percolate LastModified Up Parent Object Chain
            if (_ParentDataObject == null){
            	// do nothing else
            }else{
            	_ParentDataObject.SetLastModified(value);
            	//Parent.LastModified = value;
            }
        }
    }

    public boolean NoIndex;
    @Override
    public boolean GetNoIndex(){
    	try{
    		// Try-Parse the "noindex" attribute.  If it's the empty string the result is considered false.
    		boolean Bool = false;
    		String Val = _XmlHelper.GetAttribute(_DataObjectNode, "noindex");

    		if (Val == "") 
    			return false;
    		else
    			Bool = Boolean.parseBoolean(Val);

            return Bool;
    	}catch (Exception ex){
    		return false;
    	}
    }
    @Override
    public void SetNoIndex(boolean value){
    	String val = "false";
    	if(value)
    		val = "true";
    	
    	_XmlHelper.SetAttribute(_DataObjectDocument, _DataObjectNode, "noindex", val);
    }

    public ECoalesceDataObjectStatus DataObjectStatus;
    @Override
    public ECoalesceDataObjectStatus GetDataObjectStatus(){
    	// Get
    	String StatusString = "";
    	StatusString = GetAttribute("status");

    	// Evaluate
        if (StatusString == "") {
            // Set to Active (Default)
            DataObjectStatus = ECoalesceDataObjectStatus.ACTIVE;

            // Return Active
            return ECoalesceDataObjectStatus.ACTIVE;
        }else{
            // Return Status
            return (this.GetDataObjectStatusForString(StatusString));
        }

    }
    @Override
    public void SetDataObjectStatus(ECoalesceDataObjectStatus value){
    	// Set
    	boolean rst; 
    	rst = SetAttribute("status", this.GetStringForDataObjectStatus(value));

    	// Evaluate
    	if (rst){
    		// Touch LastModified
    		java.util.Date CurDate = new java.util.Date();
    		LastModified = CurDate;
    	}
    }

    //TODO (?) set read only
    public String ObjectType;
    @Override
    public String GetObjectType(){
        if (_DataObjectNode == null){
            return "";
        }else{
            return _DataObjectNode.getNodeName();
        }
    }

    //TODO (?) set read only
    public String NamePath;
    @Override
    public String GetNamePath(){
        // Recurse Up Through Parent's
        if (_ParentDataObject == null)
            return Name;
        else
            return _ParentDataObject.GetNamePath() + "/" + Name;
    }

    //-----------------------------------------------------------------------
    // Public Methods
    //-----------------------------------------------------------------------

    /// <summary>
    /// GetCoalesceDataObjectForNamePath traverses the child object graph recursively
    /// following the name path until reaching the Coalesce Data Object matching the
    /// last name in the path.
    /// </summary>
    /// <param name="NamePath">A name path string with DataObject names separated with period (.) characters</param>
    /// <param name="DataObject"></param>
    /// <returns></returns>
    /// <remarks></remarks>
    public CallResult GetCoalesceDataObjectForNamePath(String NamePath, CoalesceDataObject DataObject){
        try{
            String[] Names = NamePath.split("/");

            switch (Names.length){
                case 0:
                    // No Path; Object Not Found.
                    DataObject = null;
                    return new CallResult(CallResults.FAILED, "Object not found.", "Coalesce.Framework.DataModel.CoalesceDataObject");
			case 1:
                    // End of the Path, is our Base Object named the Name Path?
                    if (this.GetName() == Names[0]){
                        // Yes; Found
                        DataObject = this;
                        return CallResult.successCallResult;
                    }else{
                        // No; Not Object Not Found
                        DataObject = null;
                        return new CallResult(CallResults.FAILED, "Object not found.", "Coalesce.Framework.DataModel.CoalesceDataObject");
                    }
			default:
                    // Find Next Child
                	CoalesceDataObject FoundChild = null;
                	
                	for ( Map.Entry<String, ICoalesceDataObject> ChildObject : _ChildDataObjects.entrySet() ){
                		
                		CoalesceDataObject cdo = (CoalesceDataObject) ChildObject;
                        if (cdo.Name == Names[1]){
                            FoundChild = cdo;
                            break;
                        }
                        
                	}
                        
                    // Did we find the child?
                    if (FoundChild == null) {
	                    // No; Return Object Not Found
	                    DataObject = null;
	                    return new CallResult(CallResults.FAILED, "Object not found.", "Coalesce.Framework.DataModel.CoalesceDataObject");
                    }else{
                        // Yes; Recurse
                        String NewPath = NamePath.substring(NamePath.indexOf("/") + 1);
                        return FoundChild.GetCoalesceDataObjectForNamePath(NewPath, DataObject);
                    }
        	}

        }catch (Exception ex){
            // Return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    /// <summary>
    /// GetCoalesceDataObjectForKey traverses the child object graph recursively looking
    /// for the Data Object with a matching key.
    /// </summary>
    /// <param name="Key"></param>
    /// <param name="DataObject"></param>
    /// <returns></returns>
    /// <remarks></remarks>
    public CallResult GetCoalesceDataObjectForKey(String Key, CoalesceDataObject DataObject){
        try{
            if (this.GetKey() == Key) {
                // Success; Return Me
                DataObject = this;

                // Return Success
                return CallResult.successCallResult;
            }else{
            	CallResult rst;

            	for ( Map.Entry<String, ICoalesceDataObject> ChildObject : _ChildDataObjects.entrySet() ){
                    // Find in Child Object
            		CoalesceDataObject cdo = (CoalesceDataObject) ChildObject;

                    rst = cdo.GetCoalesceDataObjectForKey(Key, DataObject);
                    if (rst.getIsSuccess() == false) return rst;

                    // Did we find it?
                    if (DataObject == null) {
                    	//Do nothing
                    }else{
                        // Return Success
                        return CallResult.successCallResult;
                    }
    	        }

                // Return Success
                return CallResult.successCallResult;
            }

        }catch (Exception ex){
            // Return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    /// <summary>
    /// GetTopCoalesceDataObject traverses up the parent chain until it
    /// reaches the top level and then returns the top-most parent.
    /// </summary>
    /// <param name="DataObject"></param>
    /// <returns>CallResult</returns>
    /// <remarks></remarks>
    public CallResult GetTopCoalesceDataObject(CoalesceDataObject DataObject) {
        try{
            // Do we have a parent?
            if (_ParentDataObject == null) {
                // No; we are the top.
                DataObject = this;
                return CallResult.successCallResult;
            }else{
                // Yes; recurse up the chain
                return GetTopCoalesceDataObject((CoalesceDataObject) _ParentDataObject);
            }

        }catch (Exception ex){
            // Return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    /// <summary>
    /// SetAttribute sets a named attribute for the Coalesce Data Object.
    /// The attribute can have any name and any value.
    /// </summary>
    /// <param name="Name">Name of the attribute</param>
    /// <param name="Value">Value of the attribute</param>
    /// <returns>CallResult</returns>
    /// <remarks></remarks>
    public boolean SetAttribute(String Name, String Value) {
        
        boolean retVal = false;
        
        try{
            // Do we have a Document?
            if (_DataObjectDocument != null) {
                // Yes; Do we have a Node?
                if (_DataObjectNode != null) {
                    // Yes; Set the Attribute
                    XmlHelper xmlHelper = new XmlHelper();
                    retVal = xmlHelper.SetAttribute(_DataObjectDocument, _DataObjectNode, Name, Value);
                }
            }
            
            return retVal;
            
        }catch (Exception ex){
            // Return Failed Error
            return retVal;
        }
    }

    /// <summary>
    /// SetAttributeAsDate sets the named attribute for a Coalesce Data Object as
    /// a date.  The value is persisted in the Coalesce Data Object's xml node
    /// as an XML UTC encoded datetime.
    /// </summary>
    /// <param name="Name">Name of the attribute</param>
    /// <param name="Value">Value as a date object</param>
    /// <returns>CallResult</returns>
    /// <remarks></remarks>
    public CallResult SetAttributeAsDate(String Name, java.util.Date Value) {
        try{
            // Do we have a Document?
            if (_DataObjectDocument == null) {
		        // No; Return Failed
		        return new CallResult(CallResults.FAILED, "No DataObjectDocument.", this);
            }else{
                // Yes; Do we have a Node?
                if (_DataObjectNode == null) {
	                // No; Return Failed
	                return new CallResult(CallResults.FAILED, "No DataObjectNode.", this);
                }else{
                    // Yes; Set the Attribute
                    return _XmlHelper.SetAttributeAsDate(_DataObjectDocument, _DataObjectNode, Name, Value);
                }
            }

        }catch (Exception ex){
            // Return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    /// <summary>
    /// GetAttribute sets a named attribute for the Coalesce Data Object.
    /// The attribute can have any name and any value.
    /// </summary>
    /// <param name="Name">Name of the attribute</param>
    /// <param name="Value">Value of the attribute</param>
    /// <returns>CallResult</returns>
    /// <remarks></remarks>
    public String GetAttribute(String Name){
        String Value = "";
        try{
            
            // Do we have a Document?
            if (_DataObjectDocument != null) {
                // Yes; Do we have a Node?
                if (_DataObjectNode != null) {
                    // Yes; Get the Attribute
                    XmlHelper xmlHelper = new XmlHelper();
                    Value = xmlHelper.GetAttribute(_DataObjectNode, Name);
                }
            }
            
            return Value;
            
        }catch (Exception ex){
            // Return Failed Error
            return Value;
        }
    }

    /// <summary>
    /// GetAttributeAsDate sets the named attribute for a Coalesce Data Object as
    /// a date.  The value is persisted in the Coalesce Data Object's xml node
    /// as an XML UTC encoded datetime.
    /// </summary>
    /// <param name="Name">Name of the attribute</param>
    /// <param name="Value">Value as a date object</param>
    /// <returns>CallResult</returns>
    /// <remarks></remarks>
    public CallResult GetAttributeAsDate(String Name, String Value){ 
        try{
            // Do we have a Document?
            if (_DataObjectDocument == null) {
                // No; Return Failed
                return new CallResult(CallResults.FAILED, "No DataObjectDocument.", "Coalesce.Framework.DataModel.CoalesceDataObject");
            }else{
                // Yes; Do we have a Node?
                if (_DataObjectNode == null) {
                    // No; Return Failed
                    return new CallResult(CallResults.FAILED, "No DataObjectNode.", "Coalesce.Framework.DataModel.CoalesceDataObject");
                }else{
                    // Yes; Get the Attribute
                	XmlHelper xmlHelper = new XmlHelper();
                    Value = xmlHelper.GetAttributeAsDate(_DataObjectNode, Name).toGMTString();

                    // Return Success
                    return CallResult.successCallResult;
                }
            }

        }catch (Exception ex){
            // Return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: make sure this is "shared"
    public ECoalesceDataObjectStatus GetDataObjectStatusForString(String StatusString) {
        try{
            // Case
            switch (StatusString.toUpperCase())
            {
                case "ACTIVE":
                    return ECoalesceDataObjectStatus.ACTIVE;
			case "DELETED":
                    return ECoalesceDataObjectStatus.DELETED;
			default:
                    return ECoalesceDataObjectStatus.UNKNOWN;
            }

        }catch (Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceDataObject");

            // Return Unknown
            return ECoalesceDataObjectStatus.UNKNOWN;
        }
    }

    //TODO: make sure this is "shared"
    public String GetStringForDataObjectStatus(ECoalesceDataObjectStatus Status) { 
        try{
            // Case
            switch (Status)
            {
                case ACTIVE:
                    return "active";
			case DELETED:
                    return "deleted";
			default:
                    return "unknown";
            }

        }catch (Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceDataObject");

            // Return Unknown
            return "unknown";
        }
    }
    
    public Document LoadXML(String xml, Document document){
    	/*
    	try{
    	//StringReader stringReader=new StringReader(xml);
    	DOMParser parser=new DOMParser();
    	parser.parse(xml);
    	//parser.parse(stringReader);
    	document = parser.getDocument();
    	
    	//} catch (SAXException e) {
		//e.printStackTrace();
    	//} catch (IOException e) {
		//e.printStackTrace();
    	}catch(Exception e){
    		CallResult.log(CallResults.FAILED_ERROR, e, "Coalesce.Common.Helpers.CoalesceDataObject");
    	}
    	*/
    	return document;
    }
	
}
