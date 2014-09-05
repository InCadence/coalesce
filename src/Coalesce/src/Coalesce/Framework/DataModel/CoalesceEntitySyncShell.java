package Coalesce.Framework.DataModel;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import Coalesce.Common.Helpers.XmlHelper;

public class CoalesceEntitySyncShell {

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    protected Document _DataObjectDocument;
    protected Node _EntityNode;

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CoalesceEntitySyncShell Create(XsdEntity Entity){

        CoalesceEntitySyncShell EntitySyncShell = null;
        
        try{
            boolean isSuccess = false;

            // Create a new CoalesceEntitySyncShell
            CoalesceEntitySyncShell SyncShell = new CoalesceEntitySyncShell();

            // Initialize
            isSuccess = SyncShell.InitializeFromEntity(Entity);

            // Evaluate
            if (isSuccess) 
                EntitySyncShell = SyncShell;

            // return
            return EntitySyncShell;

        }catch(Exception ex){
            // return Failed Error
            return EntitySyncShell;
        }
    }

    public boolean Initialize(String EntitySyncShellXml){
        try{
        	//TODO: verify this (loadXMLFrom) is a replacement for LoadXml function
//            // Create DataObjectDocument
            
        	Document XmlDoc = null;
            XmlDoc = XmlHelper.loadXMLFrom(EntitySyncShellXml);

            // Call Peer.
            return Initialize(XmlDoc);

        }catch(Exception ex){
            // return Failed Error
            return false;
        }
    }
    
    public boolean Initialize(Document EntitySyncShellDataObjectDocument){
        try{
            // Set DataObjectDocument
            this.SetDataObjectDocument(EntitySyncShellDataObjectDocument);
            // return Success
            return true;

        }catch(Exception ex){
            // return Failed Error
            return false;
        }
    }

    public boolean InitializeFromEntity(XsdEntity Entity){
        try{
            // TODO: verify this works
            // Create a Clone of the Entity's DataObjectDocument
            Document TemplateDoc = XmlHelper.loadXMLFrom(Entity.ToXml());

            // Prune Nodes
            PruneNodes(TemplateDoc);

            // Set Template Doc
            this.SetDataObjectDocument(TemplateDoc);

            // return Success
            return true;

        }catch(Exception ex){
            // return Failed Error
            return false;
        }
    }

    protected boolean PruneNodes(Node NodeToPrune){
        try{
            boolean isSuccess = false;

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
	        	isSuccess = PruneNodes(ChildNode);
            }

            // return Success
            return isSuccess;

        }catch(Exception ex){
            // return Failed Error
            return false;
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

        public Document GetDataObjectDocument(){
            return this._DataObjectDocument;
        }
        public void SetDataObjectDocument(Document value){
            this._DataObjectDocument = value;
        	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
            //this._EntityNode = value.SelectSingleNode("entity");
            this._EntityNode = value.getElementsByTagName("entity").item(0);
        }

        public Node GetEntityNode(){
            return this._EntityNode;
        }
        public void SetEntityNode(Node value){
            this._EntityNode = value;
        }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CoalesceEntitySyncShell Clone(CoalesceEntitySyncShell SyncShell){
        
        try{
            // Create new Instance
            CoalesceEntitySyncShell SyncShellClone = new CoalesceEntitySyncShell();

            // Initialize
            //TODO: make sure .Clone's are same between vb and java. Java required a boolean.
            //return SyncShellClone.Initialize(SyncShell.DataObjectDocument.Clone) //vb
            //return SyncShellClone.Initialize(SyncShell.GetDataObjectDocument()); //1st java thought
            SyncShellClone.Initialize(SyncShell.GetDataObjectDocument());
            return SyncShellClone;
            //return CallResult.failedCallResult; //SyncShellClone.InitializeFromEntity((CoalesceEntity) SyncShell.GetDataObjectDocument().cloneNode(true));

        }catch(Exception ex){
            // return Failed Error
            return null;
        }
    }

    public CoalesceEntitySyncShell GetRequiredChangesSyncShell(CoalesceEntitySyncShell LocalFullSyncShell, CoalesceEntitySyncShell RemoteFullSyncShell, CoalesceEntitySyncShell RequiredChangesSyncShell){
        try{
            // Create the RequiredChangesSyncShell as a Clone of the RemoteFullSyncShell. We will
            // then prune out nodes that aren't required recursively as we compare against
            // the nodes in LocalFullSyncShell.
            RequiredChangesSyncShell = this.Clone(RemoteFullSyncShell);
            
            if (RequiredChangesSyncShell.equals(null)) return RequiredChangesSyncShell;

            // Prune Unchanged Nodes
            return PruneUnchangedNodes(LocalFullSyncShell, RemoteFullSyncShell.GetDataObjectDocument(), RequiredChangesSyncShell);

        }catch(Exception ex){
            // return Failed Error
            return null;
        }
    }

    protected CoalesceEntitySyncShell PruneUnchangedNodes(CoalesceEntitySyncShell LocalFullSyncShell, Node RemoteSyncShellNode, CoalesceEntitySyncShell RequiredChangesSyncShell){
        try{
            String Key = "";
            // Recurse Child Nodes (Important: Because this us up front, we check leaf nodes first, which is necessary for
            // correct pruning.  We rely on whether or not a node has children remaining as one of the decision points on whether or not
            // the node itself needs to remain.)
		    for(int i=0; i<RemoteSyncShellNode.getChildNodes().getLength(); i++){

                Node Child = RemoteSyncShellNode.getChildNodes().item(i);
		    	RequiredChangesSyncShell = PruneUnchangedNodes(LocalFullSyncShell, Child, RequiredChangesSyncShell);
            }

            // Evaluate Based on the Coalesce Object Type
            switch (RemoteSyncShellNode.getNodeName().toUpperCase()){
                case "FIELD":
                    // Yes; Check

                    // Check RemoteSyncShellNode
                    Key = XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell IF there are
                                    // no remaining FieldHistory entries below the node.  If there are FieldHistory entries,
                                    // then we keep the Field node even if it's older.
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                	//Node NodeToPrune= RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

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
                	Key = XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                    	String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

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
                    Key = XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

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
                    Key = XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node MyNode = LocalFullSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node MyNode = LocalFullSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

                        if (MyNode != null) {
                            // Compare Timestamps
                            DateTime LocalLastModified = XmlHelper.GetAttributeAsDate(MyNode, "lastmodified");
                            DateTime RemoteLastModified = XmlHelper.GetAttributeAsDate(RemoteSyncShellNode, "lastmodified");

                            switch (LocalLastModified.compareTo(RemoteLastModified)){
                                case 0:
                                case 1:
                                    // Local is newer or the same date; Prune from the RequiredChangesSyncShell
                                	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                                    //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                                    Node NodeToPrune = RequiredChangesSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

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
                    Key = XmlHelper.GetAttribute(RemoteSyncShellNode, "key");

                    if (Key != "") {
                        String XPath = "//*[@key='" + Key + "']";
                    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
                        //Node NodeToPrune = RequiredChangesSyncShell.DataObjectDocument.SelectSingleNode(XPath);
                        Node NodeToPrune = RequiredChangesSyncShell.GetDataObjectDocument().getElementsByTagName(XPath).item(0);

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
            return RequiredChangesSyncShell;

        }catch(Exception ex){
            // return Failed Error
            return null;
        }
    }
	
}
