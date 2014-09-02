package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceEntitySyncShell {

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    protected Document _DataObjectDocument;
    protected Node _EntityNode;
	XmlHelper _XmlHelper = new XmlHelper();

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(XsdEntity Entity, CoalesceEntitySyncShell EntitySyncShell){
        try{
            CallResult rst;

            // Create a new CoalesceEntitySyncShell
            CoalesceEntitySyncShell SyncShell = new CoalesceEntitySyncShell();

            // Initialize
            rst = SyncShell.InitializeFromEntity(Entity);

            // Evaluate
            if (rst.getIsSuccess()) 
                EntitySyncShell = SyncShell;
            else
                EntitySyncShell = null;
            

            // return
            return rst;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntitySyncShell");
        }
    }

    public CallResult Initialize(String EntitySyncShellXml){
        try{
        	//TODO: Need a LoadXml function
//            // Create DataObjectDocument
        	Document XmlDoc = null;
//            XmlDoc.LoadXml(EntitySyncShellXml);

            // Call Peer.
            return Initialize(XmlDoc);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Initialize(Document EntitySyncShellDataObjectDocument){
        try{
            // Set DataObjectDocument
            this.DataObjectDocument = EntitySyncShellDataObjectDocument;
            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult InitializeFromEntity(XsdEntity Entity){
        try{
            CallResult rst;

            // TODO: Not Implemented
            
            // Create a Clone of the Entity's DataObjectDocument
            //Document TemplateDoc = Entity.GetDataObjectDocument();

            // Prune Nodes
            //rst = PruneNodes(TemplateDoc);

            // Set Template Doc
            //this.SetDataObjectDocument(TemplateDoc);

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected CallResult PruneNodes(Node NodeToPrune){
        try{
            CallResult rst;

            //TODO: Make sure the Node/Attribute "switch" is ok
            // Prune Unnecessary Attributes
            if (NodeToPrune.getAttributes() != null) {
                if (NodeToPrune.getAttributes().getLength() > 0) {
                	ArrayList<Node> RemoveList = new ArrayList<Node>();

                    // Find Attributes to Remove
    		    	//for (javax.xml.bind.annotation.XmlAttribute ChildAttributeNode : NodeToPrune.getAttributes()){
        		    for(int i=0; i<NodeToPrune.getAttributes().getLength(); i++){
        		    	Node ChildAttributeNode = NodeToPrune.getAttributes().item(i);
                        switch (ChildAttributeNode.getNodeName().toUpperCase()){
                            case "KEY":
                            case "LASTMODIFIED":
                            case "MD5":
                                // Keep
                            	break;
                            default:
                                // Add to Remove List
                                RemoveList.add(ChildAttributeNode);
                        }
                    }

                    // Remove
        	        for (Node ChildAttributeNode : RemoveList){
                        NodeToPrune.removeChild(ChildAttributeNode);
                    }
                }
            }

            // Prune Unnecessary Nodes
            if (NodeToPrune.hasChildNodes()) {
                if (NodeToPrune.getChildNodes().getLength() > 0) {
                	ArrayList<Node> RemoveList = new ArrayList<Node>();
                	
                    // Find Nodes to Remove
        	        //for (Node ChildNode : NodeToPrune.getChildNodes()){
        		    for(int i=0; i<NodeToPrune.getChildNodes().getLength(); i++){
        	        	Node ChildNode = NodeToPrune.getChildNodes().item(i);

                        switch (ChildNode.getNodeType()){
                            //case XmlNodeType.Element:
                        	//case XmlNodeType.EndElement:
                        	case Node.ELEMENT_NODE:
                                // Keep
                            	break;
                            default:
                                // Add to Remove List
                                RemoveList.add(ChildNode);
                        }
                    }

                    // Remove
        	        for (Node ChildNode : RemoveList){
                        NodeToPrune.removeChild(ChildNode);
                    }
                }
            }

            // Recurse Child Nodes
		    for(int i=0; i<NodeToPrune.getChildNodes().getLength(); i++){
	        	Node ChildNode = NodeToPrune.getChildNodes().item(i);
                rst = PruneNodes(ChildNode);
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

    protected Document DataObjectDocument;
        public Document GetDataObjectDocument(){
            return this._DataObjectDocument;
        }
        public void SetDataObjectDocument(Document value){
            this._DataObjectDocument = value;
        	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
            //this._EntityNode = value.SelectSingleNode("entity");
            this._EntityNode = value.getElementsByTagName("entity").item(0);
        }

    protected Node EntityNode;
        public Node GetEnttiyNode(){
            return this._EntityNode;
        }
        public void SetEntityNode(Node value){
            this._EntityNode = value;
        }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult Clone(CoalesceEntitySyncShell SyncShell, CoalesceEntitySyncShell SyncShellClone){
        try{
            // Create new Instance
            SyncShellClone = new CoalesceEntitySyncShell();

            // TODO: Not Implemented
            

            // Initialize
            //TODO: make sure .Clone's are same between vb and java. Java required a boolean.
            //return SyncShellClone.Initialize(SyncShell.DataObjectDocument.Clone) //vb
            //return SyncShellClone.Initialize(SyncShell.GetDataObjectDocument()); //1st java thought
            return CallResult.failedCallResult; //SyncShellClone.InitializeFromEntity((CoalesceEntity) SyncShell.GetDataObjectDocument().cloneNode(true));

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntitySyncShell");
        }
    }

    public CallResult GetRequiredChangesSyncShell(CoalesceEntitySyncShell LocalFullSyncShell, CoalesceEntitySyncShell RemoteFullSyncShell, CoalesceEntitySyncShell RequiredChangesSyncShell){
        try{
            CallResult rst;

            // Create the RequiredChangesSyncShell as a Clone of the RemoteFullSyncShell. We will
            // then prune out nodes that aren't required recursively as we compare against
            // the nodes in LocalFullSyncShell.
            rst = this.Clone(RemoteFullSyncShell, RequiredChangesSyncShell);
            if (!(rst.getIsSuccess())) return rst;

            // Prune Unchanged Nodes
            return PruneUnchangedNodes(LocalFullSyncShell, RemoteFullSyncShell.DataObjectDocument, RequiredChangesSyncShell);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntitySyncShell");
        }
    }

    protected CallResult PruneUnchangedNodes(CoalesceEntitySyncShell LocalFullSyncShell, Node RemoteSyncShellNode, CoalesceEntitySyncShell RequiredChangesSyncShell){
        try{
            CallResult rst;
            String Key = "";
            // Recurse Child Nodes (Important: Because this us up front, we check leaf nodes first, which is necessary for
            // correct pruning.  We rely on whether or not a node has children remaining as one of the decision points on whether or not
            // the node itself needs to remain.)
		    for(int i=0; i<RemoteSyncShellNode.getChildNodes().getLength(); i++){
		    	Node Child = RemoteSyncShellNode.getChildNodes().item(i);
                rst = PruneUnchangedNodes(LocalFullSyncShell, Child, RequiredChangesSyncShell);
            }

            // Evaluate Based on the Coalesce Object Type
            switch (RemoteSyncShellNode.getNodeName().toUpperCase()){
                case "FIELD":
                    // Yes; Check

                    // Check RemoteSyncShellNode
                    Key = _XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = _XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell IF there are
                                    // no remaining FieldHistory entries below the node.  If there are FieldHistory entries,
                                    // then we keep the Field node even if it's older.
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                	//Node NodeToPrune= RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                                    if (NodeToPrune != null) {
                                        if (NodeToPrune.getParentNode() != null) {
                                            if (NodeToPrune.getChildNodes().getLength() == 0) {
                                                NodeToPrune.getParentNode().removeChild(NodeToPrune);
                                            }
                                        }
                                    }
                                    break;
                                case -1:
                                    // Remote Node is newer; Keep in the RequiredChangesSyncShell
                                	break;
                            }
                        }else{
                            // Remote Node not found in LocalFullSyncShell; Keep in the RequiredChangesSyncShell
                        }
                    }
                    break;
                case "FIELDHISTORY":
                    // Yes; Check

                    // Check RemoteSyncShellNode
                	Key = _XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                    	String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = _XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                                    if (NodeToPrune != null) {
                                        if (NodeToPrune.getParentNode() != null) {
                                            NodeToPrune.getParentNode().removeChild(NodeToPrune);
                                        }
                                    }
                                    break;
                                case -1:
                                    // Remote is newer; Keep in the RequiredChangesSyncShell
                                	break;
                            }
                        }else{
                            // Remote Node not found in LocalFullSyncShell; Keep in the RequiredChangesSyncShell
                        }
                    }
                    break;
                case "LINKAGE":
                    // Yes; Check

                    // Check RemoteSyncShellNode
                    Key = _XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = _XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                                    if (NodeToPrune != null) {
                                        if (NodeToPrune.getParentNode() != null) {
                                            NodeToPrune.getParentNode().removeChild(NodeToPrune);
                                        }
                                    }
                                break;
                                case -1:
                                    // Remote is newer; Keep in the RequiredChangesSyncShell
                                break;
                            }
                        }else{
                            // Remote Node not found in LocalFullSyncShell; Keep in the RequiredChangesSyncShell
                        }
                    }
                    break;
                case "FIELDDEFINITION":
                    // Yes; Check

                    // Check RemoteSyncShellNode
                    Key = _XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = _XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                                    if (NodeToPrune != null) {
                                        if (NodeToPrune.getParentNode() != null) {
                                            NodeToPrune.getParentNode().removeChild(NodeToPrune);
                                        }
                                    }
                                    break;
                                case -1:
                                    // Remote is newer; keep in the RequiredChangesSyncShell
                                	break;
                            }
                        }else{
                            //Remote Node not found in LocalFullSyncShell; Keep in the RequiredChangesSyncShell
                        }
                    }
                    break;
                case "LINKAGESECTION":
                case "RECORD":
                case "RECORDSET":
                case "SECTION":
                case "ENTITY":
                    // For these Coalesce Objects, we will check the RequiredChangesSyncShell
                    // for the presence of this object's node.  If the node is present, and 
                    // it still has children, then we will keep the node in the 
                    // RequiredChangesSyncShell. Since we prune leaves first, and work our way
                    // up the tree to the base Entity node, the presence of child nodes means 
                    // that a child object to this object required updating, therefore we have to keep
                    // this object's node. If there are no child nodes, then we can prune this
                    // object's node.
                    Key = _XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.getElementsByTagName(XPath).item(0);

                        if (NodeToPrune != null) {
                            if (NodeToPrune.getParentNode() != null) {
                                if (NodeToPrune.getParentNode() == null) {
                                    // Prune
                                    NodeToPrune.getParentNode().removeChild(NodeToPrune);
                                }
                            }
                        }
                    }
                    break;
            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntitySyncShell");
        }
    }
	
}
