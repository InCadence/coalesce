package Coalesce.Framework.DataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Coalesce.Common.Helpers.DateTimeHelper;
//import Coalesce.Common.Helpers.LinkTypes;
import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceEntity extends CoalesceDataObject {

	// Factory and Initialization
	XmlHelper _XmlHelper = new XmlHelper();

	/*
	 * Create a new CoalesceEntity instance from a CoalesceEntityTemplate.  This method is used
	 * to create a new unpopulated CoalesceEntity.
	 * 
	 * @param CoalesceEntity 			NewEntity 		
	 * @param CoalesceEntityTemplate 	EntityTemplate 	
	 * 
	 * @return CallResult
	 * @throws
	 */
	//shared
    public CallResult Create(CoalesceEntity NewEntity, CoalesceEntityTemplate EntityTemplate){ 
	    try{
	        // Create from EntityTemplate
	    	//Document EntDoc = EntityTemplate.GetDataObjectDocument();
	        //TODO: make sure getTextContent() is same as vb's OuterXml
	    	//return Create(NewEntity, EntityTemplate.DataObjectDocument.OuterXml, null);
	        //return new CallResult(CallResults.FAILED, "Incomplete code in CoalesceEntity.Create", "Coalesce.Framework.DataModel.CoalesceEntity");
	    	return Create(NewEntity, EntityTemplate.GetDataObjectDocument().getTextContent(), null);
	    }catch (Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
    }
    
    /* 
    * Create a new CoalesceEntity instance from CoalesceEntity Xml.  This method is used to
    * create a populated CoalesceEntity if the Xml is for an already populated instance, or
    * a new unpopulated CoalesceEntity if the Xml is from a empty template.
    * 
    * @param CoalesceEntity NewEntity
    * @param String 		EntityXml
    * @return String
    * @throws
    */
    public CallResult Create(CoalesceEntity NewEntity, String EntityXml){
        try{
        	return this.Create(NewEntity,  EntityXml, null);
        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
        }
    }
    
    /* 
    * Create a new CoalesceEntity instance from CoalesceEntity Xml.  This method is used to
    * create a populated CoalesceEntity if the Xml is for an already populated instance, or
    * a new unpopulated CoalesceEntity if the Xml is from a empty template.
    * 
    * @param CoalesceEntity NewEntity
    * @param String 		EntityXml
    * @param String			Title
    * @return CallResult
    * @throws
    */
    public CallResult Create(CoalesceEntity NewEntity, String EntityXml, String Title){
        try{
        	//TODO: Make sure the LoadXML works
            // Create from Entity Xml
        	Document TemplateDoc = null;
	    	this.LoadXML(EntityXml, TemplateDoc);
            //TemplateDoc.LoadXml(EntityXml);

            NewEntity = new CoalesceEntity();
            if (Title != null) NewEntity.Title = Title;

            return NewEntity.Initialize(TemplateDoc);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
        }
    }
    
    /*
    * Create a new CoalesceEntity instance without any child sections.  This method is
    * used to create an empty CoalesceEntity instance which only contains the base
    * entity node.
    * 
    * @param NewEntity
    * @param Name
    * @param Source
    * @param Version
    * @returns
    * @throws
    */ 
    public CallResult Create(CoalesceEntity NewEntity, String Name, String Source, String Version, String EntityId, String EntityIdType){
        try{
        	String Title = null;
        	return this.Create(NewEntity, Name, Source, Version, EntityId, EntityIdType, Title);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
        }
    }

    /*
    * Create a new CoalesceEntity instance without any child sections.  This method is
    * used to create an empty CoalesceEntity instance which only contains the base
    * entity node.
    * 
    * @param NewEntity
    * @param Name
    * @param Source
    * @param Version
    * @param Title
    * @returns
    * @throws
    */ 
    public CallResult Create(CoalesceEntity NewEntity, String Name, String Source, String Version, String EntityId, String EntityIdType, String Title){
        try{
        	CallResult rst;
        	Document NewDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(); //new Document();
            //Node NewNode = null;

            // Create the Entity
            if (NewEntity == null) NewEntity = new CoalesceEntity();

        	//TODO: confirm this is how to set the xml encoding and version properties
//            // Append the Encoding Node
            System.setProperty("file.encoding", "UTF-16");
            NewDoc.setXmlVersion("1.0");
            
//            Node EncodingNode = NewDoc.CreateXmlDeclaration("1.0", "UTF-16", "");
//            Call NewDoc.AppendChild(EncodingNode);

            //TODO: make sure this actually creates and appends a linkagesection node
            // Create CoalesceDocument Node
            //NewNode = NewDoc.CreateNode(XmlNodeType.Element, "entity", "");
            Node NewNode = (Node) NewDoc.createElement("entity");
            //.GetDataObjectDocument().createElement("entity");
            NewDoc.appendChild(NewNode);

            // Initialize the CoalesceEntity Object
            rst = NewEntity.Initialize(NewDoc, NewNode);
            if (!(rst.getIsSuccess())) return rst;

            // Set Default Values
            NewEntity.Name = Name;
            NewEntity.Source = Source;
            NewEntity.Version = Version;
            NewEntity.EntityId = EntityId;
            NewEntity.EntityIdType = EntityIdType;
            if (!(Title==null)) NewEntity.Title = Title;

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
        }
    }

    //Overridable Overloads Function
    public CallResult Initialize(){
    	return CallResult.successCallResult;
    }
	
	/*
	* Initialize a new CoalesceEntity instance from a CoalesceEntityTemplate object.
	* 
	* @param EntityTemplate"></param>
	* @return
	* @throws
	*/
	public CallResult Initialize(CoalesceEntityTemplate EntityTemplate){
	    try{
	
	    	CallResult rst;
	
	    	Document DataObjectDoc = EntityTemplate.DataObjectDocument.getOwnerDocument();
//	    	Document DataObjectDoc = new Document();
//	
//	        // Create a new DataObjectDocument from the Template's Document
//	        DataObjectDoc.LoadXml(EntityTemplate.DataObjectDocument.OuterXml);
	
	        // Call Peer
	        rst = this.Initialize(DataObjectDoc);
	        if (!(rst.getIsSuccess())) return rst;
	
	        return this.InitializeReferences();
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	/*
	* Initialize a CoalesceEntity instance from CoalesceEntity Xml.  The Xml can either be a populated entity or
	* an entity template.
	* 
	* @param EntityXml"></param>
	* @return
	*/
	public CallResult Initialize(String EntityXml){
	    try{
	
	    	CallResult rst;
	
	    	Document DataObjectDoc = null; //= new Document();
	
	    	//TODO: make sure LoadXML works
	        // Create a new DataObjectDocument from the Xml
	    	this.LoadXML(EntityXml, DataObjectDoc);
	        //DataObjectDoc.LoadXml(EntityXml);
	
	        // Call Peer
	        rst = this.Initialize(DataObjectDoc);
	        if (!(rst.getIsSuccess())) return rst;
	
	        return this.InitializeReferences();
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED, ex, this);
	    }
	}
	
	/*
	* Initialize a CoalesceEntity instance from an Xml Document.  The Xml Document should be loaded with the
	* Xml from either an entity or an entity template.
	* 
	* @param DataObjectDocument"></param>
	* @return
	*/
	public CallResult Initialize(Document DataObjectDocument){
	    try{
	
	    	CallResult rst;
	
	    	//TODO: make sure getElementsByTagName through rst is what was intended
	        // Call on Peer Initialization Method
	    	NodeList nodes = DataObjectDocument.getElementsByTagName("/entity");
	    	Node node = nodes.item(0);
	    	rst = this.Initialize(DataObjectDocument, node);
	        //rst = this.Initialize(DataObjectDocument, DataObjectDocument.SelectSingleNode("/entity"));
	        if (!(rst.getIsSuccess())) return rst;
	
	        return this.InitializeReferences();
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	protected CallResult InitializeReferences(){
	    return CallResult.successCallResult;
	}
	
	//-----------------------------------------------------------------------//
	// public Properties
	//-----------------------------------------------------------------------//
	
	public String Source;
	public String GetSource(){
		return _XmlHelper.GetAttribute(this._DataObjectNode, "source");
	}
	public void SetSource(String value){
		_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "source", value);
	}
	
	public String Version;
	public String GetVersion(){
	        return _XmlHelper.GetAttribute(this._DataObjectNode, "version");
	}
	public void SetVersion(String value){
		_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "version", value);
	}
	
	public String EntityId;
	public String GetEntityId(){
	        return _XmlHelper.GetAttribute(this._DataObjectNode, "entityid");
	}
    public void SetEntityId(String value){
        _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "entityid", value);
    }

	public String EntityIdType;
	public String GetEntityIdType(){
	        return _XmlHelper.GetAttribute(this._DataObjectNode, "entityidtype");
	}
	public void SetEntityIdType(String value){
	        _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "entityidtype", value);
	}

	public String Title;
	public String GetTitle(){
        try{

        	CallResult rst;
            _Title = _XmlHelper.GetAttribute(this._DataObjectNode, "title");

            // To accomidate MARS (BIMA / BCC Version)
            if (_Title == null || _Title.trim() == "") {
                _Title = _XmlHelper.GetAttribute(this._DataObjectNode, "Title");
            }

            String _Title_= "";

            //Check if value contains an XPath
            if (_Title.contains("/") && _Title.length() > 50){
            	String[] paths = _Title.split(",");
                //String[] trimChars = {",", " "};

                for(String path : paths){
                    
                    CoalesceField CDO = null;
                    rst = this.GetCoalesceDataObjectForNamePath(path, CDO);
                    if (rst.getIsSuccess()) _Title_ += CDO.Value + ", ";
                }

                //this._Title = _Title_.trimEnd(trimChar);
                String LastChar = _Title.substring(_Title.length()-1);
                while(LastChar == "," || LastChar == " "){
                	if (LastChar.matches("\\s|,")) this._Title = this._Title.substring(0, this._Title.length()-2); 
                	LastChar = _Title.substring(_Title.length()-1);
                }
                
            }

            if (this._Title == null || this._Title.trim() == "")
                return this.Source;
            else
                return this._Title;

        }catch(Exception ex){
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return this.Source;
        }
	}
	
    public void SetTitle(String value){

        if (value != this.Title) {

        	CallResult rst;
            rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "title", value);

            //TODO: Confirm GMT date is correct
            if (rst.getIsSuccess()) {
                // Set LastModified
    	    	SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ");
    	    	formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date utcDate = new Date();
    	    	rst = DateTimeHelper.ConvertDateToGMT(utcDate);//formatUTC.parse(formatUTC.format(new Date()));
                this.SetLastModified(utcDate);
            }

        }
    }

	//readonly
	public Map<String, CoalesceLinkage> Linkages;
	public Map<String, CoalesceLinkage> GetLinkages(){
		
		CallResult rst;
		Map<String, CoalesceLinkage> d = null;
	
		// Get All Linkages
		rst = this.GetMyLinkages(d);
	
		// Evaluate
		if (!(rst.getIsSuccess())) {
			// Failed; return Empty Collection
			return new HashMap<String, CoalesceLinkage>();
	        }else{
	            // Success; return Collectino
	            return d;
	        }
	}

	//-----------------------------------------------------------------------//
	// Private and protected Objects
	//-----------------------------------------------------------------------//
	protected String _Title;
	
	//-----------------------------------------------------------------------//
	// Private and protected Methods
	//-----------------------------------------------------------------------//
	
	protected CallResult Initialize(Document DataObjectDocument, Node DataObjectNode){
	    try{
	    	CallResult rst;
	
	        // Set References
	        this._DataObjectDocument = DataObjectDocument;
	        this._DataObjectNode = DataObjectNode;
	        this.SetParent(null);
	
	        // Check Keys and Timestamps
	        //TODO: GUIDHelper, Ticks and UTCDate
	        if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
	        //if (this.GetKey() == "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.key);
	        
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;
	
	        // Iterate Child Nodes
	        for(int i=0; i<this._DataObjectNode.getChildNodes().getLength(); i++){
	        	Node Child = this._DataObjectNode.getChildNodes().item(i);
	            // Case on Element
	        	switch(Child.getNodeName())
	            {
	                case "linkagesection":
	                    // Create a LinkageSection Object
	                	CoalesceLinkageSection NewLinkageSection = new CoalesceLinkageSection();
	                    rst = NewLinkageSection.Initialize(this, Child);
	                    if (!(rst.getIsSuccess())) return rst;
	
	                    // Add to Child Collection
	                    this.GetChildDataObjects().put(NewLinkageSection.GetKey(), NewLinkageSection);
	
	                case "section":
	                    // Create a Section Object
	                	CoalesceSection NewSection = new CoalesceSection();
	                    rst = NewSection.Initialize(this, Child);
	                    if (!(rst.getIsSuccess())) return rst;
	
	                    // Add to Child Collection
	                    this.GetChildDataObjects().put(NewSection.GetKey(), NewSection);
	
	                default:
	                    // Unexpected; Ignore
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
	// public Methods
	//-----------------------------------------------------------------------//
	
	public CallResult CreateNewEntityTemplate(CoalesceEntityTemplate EntityTemplate){
	    try{
	    	CallResult rst;
	    	CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();
	
	        // Initialize the EntityTemplate from this
	        rst = EntTemp.InitializeFromEntity(this);
	
	        // Evaluate
	        if (rst.getIsSuccess()) 
	            EntityTemplate = EntTemp;
	        else
	            EntityTemplate = null;
	
	        // return
	        return rst;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult CreateLinkageSection(CoalesceLinkageSection NewLinkageSection){
	    try{
	    	CallResult rst;
	        // Create LinkageSection
	        rst = NewLinkageSection.Create(this, NewLinkageSection);
	        if (!(rst.getIsSuccess())) return rst;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult CreateSection(CoalesceSection NewSection, String Name){
	    try{
	    	CallResult rst;
	
//	        // Create Section
	        rst = NewSection.Create(this, NewSection, Name);
	        if (!(rst.getIsSuccess())) return rst;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkageSection(CoalesceLinkageSection LinkageSection){
	    try{
	        // Iterate through Child DataObjects to find the LinkageSection
            for(ICoalesceDataObject IChild : this._ChildDataObjects.values()){
            	CoalesceDataObject Child = (CoalesceDataObject) IChild;
            	//TODO: does Child.GetName() do what Child.GetType.Name did in VB?
	            if (Child.GetName() == "CoalesceLinkageSection") {
	                // Found it; return
	                LinkageSection = (CoalesceLinkageSection) IChild;
	                return CallResult.successCallResult;
	            }
            }
	
	        // Not Found
	        LinkageSection = null;
	        return new CallResult(CallResults.FAILED, "Object not found.", "Coalesce.Framework.Helpers.GraphicsHelper");
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
    public CallResult GetMyLinkages(Map<String, CoalesceLinkage> Linkages) {
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = new CoalesceLinkageSection();
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
		        //for (CoalesceDataObject Obj : LinkageSection.GetChildDataObjects().values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Add to the Dictionary
	                    Results.put(Obj.key, (CoalesceLinkage) Obj);
	                }
	            }
	        }
	        // Set return Value
	        Linkages = Results;
	
	        // return SUCCESS
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}

	public CallResult GetMyLinkages(String ForEntityName, Map<String, CoalesceLinkage> Linkages) {
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = new CoalesceLinkageSection();
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
		        //for (CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	if (((CoalesceLinkage) Obj).Entity2Name.toLowerCase() == ForEntityName.toLowerCase()) {
	                        // Yes; Add to the Dictionary
		                    Results.put(Obj.key, (CoalesceLinkage) Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return SUCCESS
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetMyLinkages(ELinkTypes ForLinkType, String ForEntityName, Map<String, CoalesceLinkage> Linkages) {
		ArrayList<ELinkTypes> ForLinkTypes = new ArrayList<ELinkTypes>(); 
		ForLinkTypes.add(ForLinkType);
	
	    return GetMyLinkages(ForLinkTypes, ForEntityName, Linkages);
	}
	
	public CallResult GetMyLinkages(ArrayList<ELinkTypes> ForLinkTypes, String ForEntityName, Map<String, CoalesceLinkage> Linkages) {
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = new CoalesceLinkageSection();
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && LinkageSection != null) {
		        //for (CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                    if (((CoalesceLinkage) Obj).Entity2Name.toLowerCase() == ForEntityName.toLowerCase()){ //&& 
	                    	if	( ForLinkTypes.contains(((CoalesceLinkage) Obj).LinkType) ) {
		                        // Yes; Add to the Dictionary
		                        if (Obj.GetDataObjectStatus() != ECoalesceDataObjectStatus.DELETED) {
		    	                    Results.put(Obj.key, (CoalesceLinkage) Obj);
		                        }
	                    	}
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return SUCCESS
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetMyLinkages(ELinkTypes ForLinkType, String ForEntityName, String ForEntitySource, Map<String, CoalesceLinkage> Linkages) {
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = new CoalesceLinkageSection();;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && LinkageSection != null) {
		        //for (CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                    if (((CoalesceLinkage) Obj).Entity2Name == ForEntityName &&
	                        ((CoalesceLinkage) Obj).LinkType == ForLinkType.GetELinkTypeLabelForType(ForLinkType) &&
	                        ((CoalesceLinkage) Obj).Entity2Source == ForEntitySource &&
	                        ((CoalesceLinkage) Obj).DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
	                        // Yes; Add to the Dictionary
	                    	Results.put(Obj.key, (CoalesceLinkage) Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return SUCCESS
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetMyLinkages(ELinkTypes ForLinkType, Map<String, CoalesceLinkage> Linkages) {
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = new CoalesceLinkageSection();
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
			if (rst.getIsSuccess() && LinkageSection != null) {
				//for (CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                    if (((CoalesceLinkage) Obj).LinkType == ForLinkType.GetELinkTypeLabelForType(ForLinkType)) {
	                        // Yes; Add to the Dictionary
	                        if (Obj.DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
								Results.put(Obj.key, (CoalesceLinkage) Obj);
	                        }
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return SUCCESS
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}

	public CallResult GetSection(String NamePath, CoalesceSection Section){
		try{
			CallResult rst;
			CoalesceDataObject DataObject = new CoalesceDataObject();
		
		    // Find by Path
		    rst = this.GetCoalesceDataObjectForNamePath(NamePath, DataObject);
		
		    // Evaluate
		    if (rst.getIsSuccess()) {
		    	//TODO: make sure GetName does what VB's GetType.Name does
		        if (DataObject.GetName() == "CoalesceSection") {
		        	ICoalesceDataObject iCDO =(ICoalesceDataObject)DataObject;
		        	Section = (CoalesceSection) iCDO;
		            return CallResult.successCallResult;
		        }else{
		            Section = null;
		            return new CallResult(CallResults.FAILED, "Object not found.", "Coalesce.Framework.DataModel.CoalesceEntity");
		        }
		    }else{
		        return rst;
		    }
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	public CallResult GetEntityId(String Param, String Value){
		try{
		
		    // EntityID Type Contain Param?
			int Idx = Arrays.asList(this.EntityIdType.split(",")).indexOf(Param);
			//Integer Idx = System.Array.IndexOf(this.EntityIdType.split(","), Param);
		    if (Idx == -1) return new CallResult(CallResults.FAILED, "Not Found", this);
		
		    // Get Value
		    String[] IdArray = this.EntityId.split(",");
		    Value = IdArray[Idx]; //.GetValue(Idx);
		
		    return new CallResult(CallResults.SUCCESS);
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	public CallResult SetEntityId(String Param, String Value){
	
		try{
		
		    if ( (Param == null || Param.trim() == "") || (Value == null || Value.trim() == "") ) return new CallResult(CallResults.FAILED, "Invalid", this);
		
		    // Collection Already have Unique ID?
		    if (this.EntityId == null || this.EntityId.trim() == "") {
		        //No; Add 
		        this.EntityIdType = Param;
		        this.EntityId = Value;
		    }else{
		        //Yes; Append (CSV)
		        this.EntityIdType = this.EntityIdType + "," + Param;
		        this.EntityId = this.EntityId + "," + Value;
		    }
		
		    return new CallResult(CallResults.SUCCESS);
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	public CallResult MarkDeleted(){
	
		this.DataObjectStatus = ECoalesceDataObjectStatus.DELETED;
		
		return new CallResult(CallResults.SUCCESS);
	}
	
	public CallResult GetSyncEntity(CoalesceEntitySyncShell SyncShell, CoalesceEntity SyncEntity){
		try{
			CallResult rst;
		
		    // Make the SyncEntity a Clone of this
		    SyncEntity = new CoalesceEntity();
		    //TODO: Verifiy this.GetDataObjectDocument() is good substitute for this._DataObjectDocument.Clone
		    rst = SyncEntity.Initialize(this.GetDataObjectDocument());
		    if (!(rst.getIsSuccess())) return rst;
		
		    // Get Required Changes Keys
		    String XPath = "//@key";
		    NodeList Nodes = null;
		    ArrayList<String> SyncKeys = new ArrayList<String>();
		
		    //TODO: make sure this is how to select nodes by xpath
		    //Nodes = SyncShell.GetDataObjectDocument().SelectNodes(XPath);
		    Nodes = SyncShell.GetDataObjectDocument().getElementsByTagName(XPath);
		    if (Nodes == null){
		    	// do nothing
		    }else{
	            //for(Node KeyNode : Nodes){
    		    for(int i=0; i<Nodes.getLength(); i++){
    		    	Node KeyNode = Nodes.item(i);
		            // Add Key to the RequiredChangesKeys
		            //SyncKeys.Add(KeyNode.Value);
	            	SyncKeys.add(KeyNode.getNodeValue());
	            }
		    }
		
		    // Prune Non-Required Nodes
		    rst = PruneNonRequiredNodes(SyncKeys, SyncEntity.GetDataObjectDocument());
		
		    // return Success
		    return CallResult.successCallResult;
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	protected CallResult PruneNonRequiredNodes(ArrayList SyncKeys, Node TheNode){
		try{
			CallResult rst;
		
		    // Recurse Child Nodes (Important: Because this us up front, we check leaf nodes first, which is necessary for
		    // correct pruning.)  Since we're disturbing the Node.ChildNodes collection because we may remove children from
		    // it, it's necessary to create a temporary collection of the initial children before we prune.  If we
		    // don't do this, we don't get the correct behavior.
		    for(int i=0; i<TheNode.getChildNodes().getLength(); i++){
	        	Node Child = TheNode.getChildNodes().item(i);
		        rst = PruneNonRequiredNodes(SyncKeys, Child);
            }
		
		    // Check to see if Node needs to be Pruned
		    String Key = _XmlHelper.GetAttribute(TheNode, "key");
		    if (Key != "") {
		        if (!(SyncKeys.contains(Key))) {
		            // Prune
		            if (TheNode.getParentNode() != null) 
		                TheNode.getParentNode().removeChild(TheNode);
		        }
		    }
		
		    // return Success
		    return CallResult.successCallResult;
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	public CallResult MergeSyncEntity(CoalesceEntity SyncEntity){
		try{
		    // Merge Recursively, Starting With the Entity Node
		    return MergeSyncEntityNode(this._DataObjectNode, SyncEntity._DataObjectNode);
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	protected CallResult MergeSyncEntityNode(Node MyNode, Node SyncEntityNode){
		try{
			CallResult rst;
		
		    // Get Timestamps
		    Date MyLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
		    Date UpdateLastModified = _XmlHelper.GetAttributeAsDate(SyncEntityNode, "lastmodified");
		
		    //TODO Attribute, getAttributes & SelectSingleNode(xpath)?
		    // Compare Timestamps
		    switch (MyLastModified.compareTo(UpdateLastModified)){
		        case -1:
		            // Mine is Older; Update Each Attribute.
		            //for(Attribute UpdateAttribute : SyncEntityNode.getAttributes()){
        		    for(int i=0; i<SyncEntityNode.getAttributes().getLength(); i++){
        		    	Node UpdateAttribute = SyncEntityNode.getAttributes().item(i);
		                // Set Attribute
		                _XmlHelper.SetAttribute(this._DataObjectDocument, MyNode, UpdateAttribute.getNodeName(), UpdateAttribute.getNodeValue());
		            }
		    }
		
		    // Merge Required Node's Children
		    for(int i=0; i<SyncEntityNode.getChildNodes().getLength(); i++){
	        	Node UpdateChildNode = SyncEntityNode.getChildNodes().item(i);
		        // Get Node To Update
            	String Key = _XmlHelper.GetAttribute(UpdateChildNode, "key");
            	String XPath = UpdateChildNode.getNodeName() + "[@key='" + Key + "']";
            	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
            	//Node MyChildNode = MyNode.SelectSingleNode(XPath);
            	Node MyChildNode = MyNode.getOwnerDocument().getElementsByTagName(XPath).item(0);
		
		        // Evaluate
		        if (MyChildNode == null) {
		            // We don't have this child; add the entire ChildNode
		            MyNode.appendChild(this._DataObjectDocument.importNode(UpdateChildNode, true));
		        }else{
		            // We have this child; Call MergeRequiredNode
		            rst = MergeSyncEntityNode(MyChildNode, UpdateChildNode);
		        }
		    }
		
		    // return Success
		    return CallResult.successCallResult;
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult ToXml(String Xml){
		try{
			return ToXml(Xml, false);
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult ToXml(String Xml, Boolean RemoveBinary){
		try{
		    // Examine XmlNode
		    if (this._DataObjectDocument != null) {
		
		        if (RemoveBinary) {
		            // Set a copy of the Xml without the Binary data in it.
		            Document NoBinaryXmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		            //TODO: How to get the OuterXml (3 places in this function)?
		            //NoBinaryXmlDoc.LoadXml(this.GetDataObjectDocument().OuterXml);
		            this.LoadXML(this.GetDataObjectDocument().getTextContent(), NoBinaryXmlDoc);
		            
		            // Get all Binary Field Nodes.  Ensures that the 'binary' attribute value is handled in a case
		            // insensitive way.
		            String Xpath = "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='binary']";
		            //for(Node ChildNode : NoBinaryXmlDoc.SelectNodes(Xpath)){
        		    for(int i=0; i<NoBinaryXmlDoc.getElementsByTagName(Xpath).getLength(); i++){
        		    	Node ChildNode = NoBinaryXmlDoc.getElementsByTagName(Xpath).item(i);
		                _XmlHelper.SetAttribute(NoBinaryXmlDoc, ChildNode, "value", "");
		            }
		
		            // Get all File Field Nodes.  Ensures that the 'file' attribute value is handled in a case
		            // insensitive way.
		            Xpath = "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='file']";
		            //for(Node ChildNode : NoBinaryXmlDoc.SelectNodes(Xpath)){
        		    for(int i=0; i<NoBinaryXmlDoc.getElementsByTagName(Xpath).getLength(); i++){
        		    	Node ChildNode = NoBinaryXmlDoc.getElementsByTagName(Xpath).item(i);
		                _XmlHelper.SetAttribute(NoBinaryXmlDoc, ChildNode, "value", "");
		            }
		
		            // Get Xml
		            Xml = NoBinaryXmlDoc.getTextContent(); //.OuterXml;
		        }else{
		            // Get Xml
		            Xml = this.GetDataObjectDocument().getTextContent(); //.OuterXml;
		        }
		    }else{
		        // Nothing
		        Xml = "";
		    }
		
		    // return Success
		    return CallResult.successCallResult;
		
		}catch(Exception ex){
		    // return Failed Error
		    return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}
	
	/* *************************************** Moved to EntityLinkHelper ********************************************** 
	public CallResult LinkEntities(CoalesceEntity Entity1, ELinkTypes LinkType, CoalesceEntity Entity2, Boolean UpdateExisting){ 
		
		CallResult rst = new CallResult();

	    try{
	    	
	        return LinkEntities(Entity1, LinkType, Entity2, "U", "", "en-US", UpdateExisting);
	      
	    }catch (Exception ex){
	        // return Failed Error
	    	rst = new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.EntityLinkHelper");
	        return rst;
	    }
	}

	public CallResult LinkEntities(CoalesceEntity Entity1, ELinkTypes LinkType, CoalesceEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang, Boolean UpdateExisting) {
	    try{
			CallResult rst = new CallResult();
			CoalesceLinkageSection LinkageSection1 = new CoalesceLinkageSection();
			CoalesceLinkage Linkage1 = new CoalesceLinkage();
			CoalesceLinkageSection LinkageSection2 = new CoalesceLinkageSection();
			CoalesceLinkage Linkage2 = new CoalesceLinkage();
			Boolean Linkage1AlreadyExists = false;
			Boolean Linkage2AlreadyExists = false;
	
	        // Get the LinkageSections for each Entity.  Create if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	
	        // Evaluate
	        if (rst.getIsFailed()){
	            if (rst.getMessage() == "Object not found."){
	                // Create
	                rst = Entity1.CreateLinkageSection(LinkageSection1);
	            }
	            else{
	                // return Failed
	                return rst;
	            }
	        }
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	
	        // Evaluate
	        if (rst.getIsFailed()){
	            if (rst.getMessage() == "Object not found."){
	                // Create
	                rst = Entity2.CreateLinkageSection(LinkageSection2);
	            }
	            else{
	                // return Failed
	                return rst;
	            }
	        }

	        //for (ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage"){
	            	if (((CoalesceLinkage)cdo).Entity1Key == Entity1.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == LinkType &&
	            			((CoalesceLinkage)cdo).Entity2Key == Entity2.GetKey()) {
	            		// Found; Use Existing Linkage
	            		Linkage1AlreadyExists = true;
	            		Linkage1 = (CoalesceLinkage) cdo;
	            		break;
		            }
	            }
	        }
	        
	        // Do we already have the Reciprocal Linkage made? (Same Entities and Same LinkType)?                
	        Linkage2AlreadyExists = false;
	        //for (ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
                // Switch Entities since we're looking at the Reciprocal.
	            	if (((CoalesceLinkage)cdo).Entity1Key == Entity2.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == ELinkTypes.GetReciprocalLinkType(LinkType) &&
	                		//((CoalesceLinkage)cdo).LinkType == ELinkTypes.GetReciprocalLinkType(LinkType) &&
	                		((CoalesceLinkage)cdo).Entity2Key == Entity1.GetKey()) {
	                    // Found; Use Existing Linkage
	                    Linkage2AlreadyExists = true;
	                    Linkage2 = (CoalesceLinkage)cdo;
	                    break;
	                }
	            }
	        }
	
	        // Update/Populate Linkage 1         
	        if (Linkage1AlreadyExists){
	            // Update/Populate Existing
	            rst = Linkage1.EstablishLinkage(Entity1, LinkType, Entity2, ClassificationMarking, ModifiedBy, InputLang);
	        	
	        }else{
	            // Create
	            rst = LinkageSection1.CreateLinkage(Linkage1);
	
	            // Update/Populate
	            rst = Linkage1.EstablishLinkage(Entity1, LinkType, Entity2, ClassificationMarking, ModifiedBy, InputLang);
	        }
	        	
	
	        // Update/Populate Linkage 2
	        if (Linkage2AlreadyExists){
	            if (UpdateExisting){
	            	// Update/Populate Existing
	                rst = Linkage2.EstablishLinkage(Entity2, ELinkTypes.GetReciprocalLinkType(LinkType), Entity1, ClassificationMarking, ModifiedBy, InputLang);
	            }
	        }else{
	            // Create
	            rst = LinkageSection2.CreateLinkage(Linkage2);
	
	            // Update/Populate
	            rst = Linkage2.EstablishLinkage(Entity2, ELinkTypes.GetReciprocalLinkType(LinkType), Entity1, ClassificationMarking, ModifiedBy, InputLang);
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch (Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
	}

    public CallResult UnLinkEntities(CoalesceEntity Entity1, CoalesceEntity Entity2){
	    try{
	        return UnLinkEntities(Entity1, Entity2, "U", "", "en-US");
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
	}

	public CallResult UnLinkEntities(CoalesceEntity Entity1, CoalesceEntity Entity2, ELinkTypes LinkType) {
	    try{
	        return UnLinkEntities(Entity1, Entity2, "U", "", "en-US", LinkType);
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
	}

	public CallResult UnLinkEntities(CoalesceEntity Entity1, CoalesceEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection1 = null;
	        CoalesceLinkageSection LinkageSection2 = null;
	
	        // Get the LinkageSections for each Entity.  Exit if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // Mark linkage as deleted
            //for(ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity1.GetKey() &&
	            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity2.GetKey()) {
	            		((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // Mark linkage as deleted
            //for(ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	                // Switch Entities since we're looking at the Reciprocal.
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity2.GetKey() &&
	            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity1.GetKey()) {
	            		((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
	}

	public CallResult UnLinkEntities(CoalesceEntity Entity1, CoalesceEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang, ELinkTypes LinkType){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection1 = null;
	        CoalesceLinkageSection LinkageSection2 = null;
	
	
	        // Get the LinkageSections for each Entity.  Exit if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // Mark linkage as deleted
	        //for(ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
        			//((CoalesceLinkage) cdo).GetLinkType() == LinkType && 
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity1.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == LinkType &&
		            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity2.GetKey() ) {
		            	((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // Mark linkage as deleted
	        //for(ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	                // Switch Entities since we're looking at the Reciprocal.
        			//((CoalesceLinkage) cdo).GetLinkType() == ELinkTypes.GetReciprocalLinkType(LinkType) && 
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity2.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == ELinkTypes.GetReciprocalLinkType(LinkType) &&
		            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity1.GetKey() ) {
	                    cdo.SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntity");
	    }
	}

    public CallResult GetLinkages(Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection  = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.GetChildDataObjects().values()){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Add to the Dictionary
	                    Results.put(Obj.GetKey(), (CoalesceLinkage) Obj);
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}

	public CallResult GetLinkages(String ForEntityName, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                    if (((CoalesceLinkage)Obj).GetEntity2Name().toLowerCase() == ForEntityName.toLowerCase()) {
	                        // Yes; Add to the Dictionary
	                        Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(ELinkTypes ForLinkType, String ForEntityName, Map<String, CoalesceLinkage> Linkages){
		ELinkTypes[] ForLinkTypes = {ForLinkType};
	
	    return GetLinkages(ForLinkTypes, ForEntityName, Linkages);
	}
	
	public CallResult GetLinkages(ELinkTypes[] ForLinkTypes, String ForEntityName, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection= null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	if( ((CoalesceLinkage) Obj).GetEntity2Name() == ForEntityName ) {
	                		boolean typeContained = false;
	                		String type = ((CoalesceLinkage) Obj).GetObjectType();
	                		for (int i = 0; i < ForLinkTypes.length; i++){
	                			if(ELinkTypes.GetELinkTypeLabelForType(ForLinkTypes[i]) == type){
	                				typeContained = true;
	                				break;
	                			}
	                		}
	                		//if (ForLinkTypes.contains(((CoalesceLinkage) Obj).GetLinkType()) ){
	                		if(typeContained){
		                        // Yes; Add to the Dictionary
		                        if (Obj.GetDataObjectStatus() != ECoalesceDataObjectStatus.DELETED) {
		                            Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
		                        }
	                		}
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(ELinkTypes ForLinkType, String ForEntityName, String ForEntitySource, Map<String, CoalesceLinkage> Linkages){ 
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	//((CoalesceLinkage) Obj).LinkType == ForLinkType &&
	                	if ( ((CoalesceLinkage) Obj).Entity2Name == ForEntityName &&
	                		ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)Obj).GetLinkType())) == ForLinkType &&
	                		((CoalesceLinkage) Obj).Entity2Source == ForEntitySource &&
	                		((CoalesceLinkage) Obj).DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
	                			// Yes; Add to the Dictionary
	                			Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(ELinkTypes ForLinkType, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = this.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	if (ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)Obj).GetLinkType())) == ForLinkType){

	                        // Yes; Add to the Dictionary
	                        if (Obj.DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
	                            Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                        }
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	        //Linkages = new HashMap<String, CoalesceLinkage>();
	        //Linkages.putAll(Results);
	        		//= Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	 ***************************************** Moved to EntityLinkHelper ******************************************** */

}
