package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;

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

public class XsdEntity extends XsdDataObject {

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    private Entity _entity;

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    public static XsdEntity Create(String entityXml)
    {

        // Create Entity
        XsdEntity entity = new XsdEntity();

        boolean passed = false;
        if (entityXml == null || StringHelper.IsNullOrEmpty(entityXml.trim()))
        {
            passed = entity.Initialize();

        }
        else
        {

            passed = entity.Initialize(entityXml);

        }

        if (!passed) return null;

        return entity;

    }

    public static XsdEntity Create(String entityXml, String title)
    {

        // Create Entity
        XsdEntity entity = XsdEntity.Create(entityXml);

        // Set Title
        entity.SetTitle(title);

        return entity;

    }

    public static XsdEntity Create(String name, String source, String version, String entityId, String entityIdType)
    {
        return XsdEntity.Create(name, source, version, entityId, entityIdType, null);
    }

    public static XsdEntity Create(String name,
                                   String source,
                                   String version,
                                   String entityId,
                                   String entityIdType,
                                   String title)
    {

        XsdEntity entity = new XsdEntity();
        if (!entity.Initialize()) return null;

        // Set Default Values
        entity.SetName(name);
        entity.SetSource(source);
        entity.SetVersion(version);
        entity.SetEntityId(entityId);
        entity.SetEntityIdType(entityIdType);
        if (title != null) entity.SetTitle(title);

        return entity;
    }

    public boolean Initialize(String entityXml)
    {

        Object deserializedObject = XmlHelper.Deserialize(entityXml, Entity.class);

        if (deserializedObject == null || !(deserializedObject instanceof Entity))
        {
            return false;
        }
        this._entity = (Entity) deserializedObject;

        if (!super.Initialize()) return false;

        if (!InitializeChildren()) return false;

        return InitializeReferences();

    }

    public boolean Initialize()
    {

        this._entity = new Entity();
        // this.CreateLinkageSection();
        // this._entity.getSection();

        if (!super.Initialize()) return false;

        if (!InitializeChildren()) return false;

        return InitializeReferences();

    }

    protected boolean InitializeChildren()
    {

        XsdLinkageSection linkageSection = new XsdLinkageSection();

        if (!linkageSection.Initialize(this)) return false;

        _childDataObjects.put(linkageSection.GetKey(), linkageSection);

        for (Section entitySection : _entity.getSection())
        {
            XsdSection section = new XsdSection();

            if (!section.Initialize(this, entitySection)) return false;

            _childDataObjects.put(section.GetKey(), section);

        }

        return true;

    }

    protected boolean InitializeReferences()
    {
        return true;
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entity.getKey();
    }

    @Override
    public void SetObjectKey(String value)
    {
        _entity.setKey(value);
    }

    @Override
    public String GetName()
    {
        return _entity.getName();
    }

    @Override
    public void SetName(String value)
    {
        _entity.setName(value);
    }

    @Override
    public String getType()
    {
        return "entity";
    }

    public String GetSource()
    {
        return _entity.getSource();
    }

    public void SetSource(String value)
    {
        _entity.setSource(value);
    }

    public String GetVersion()
    {
        return _entity.getVersion();
    }

    public void SetVersion(String value)
    {
        _entity.setVersion(value);
    }

    public String GetEntityId()
    {
        return _entity.getEntityid();
    }

    public void SetEntityId(String value)
    {
        _entity.setEntityid(value);
    }

    public String GetEntityIdType()
    {
        return _entity.getEntityidtype();
    }

    public void SetEntityIdType(String value)
    {
        _entity.setEntityidtype(value);
    }

    public String GetTitle()
    {
        try
        {

            String title = _entity.getTitle();

            // Check if value contains an XPath
            if (title != null && title.contains("/") && title.length() > 50)
            {

                String pathTitle = "";

                String[] paths = title.split(",");
                for (String path : paths)
                {

                    XsdDataObject dataObject = GetDataObjectForNamePath(path);

                    if (dataObject != null && dataObject instanceof XsdField)
                    {
                        XsdField field = (XsdField) dataObject;
                        pathTitle += field.GetValue() + ", ";
                    }
                }

                title = StringUtils.strip(pathTitle, ", ");

            }

            if (title == null || title.trim().equals(""))
            {
                return this.GetSource();
            }
            else
            {
                return title;
            }

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return this.GetSource();
        }
    }

    public void SetTitle(String value)
    {
        try
        {

            if (!value.equals(GetTitle()))
            {

                _entity.setTitle(value);

                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);
            }

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public DateTime GetDateCreated()
    {
        try
        {

            // return new
            // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getDatecreated());
            return _entity.getDatecreated();

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public void SetDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setDatecreated(value);
    }

    public DateTime GetLastModified()
    {
        try
        {

            // return new
            // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getLastmodified());
            return _entity.getLastmodified();

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    protected void SetObjectLastModified(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setLastmodified(value);
    }

    protected String GetObjectStatus()
    {
        return _entity.getStatus();
    }

    protected void SetObjectStatus(String status)
    {
        _entity.setStatus(status);
    }

    public Map<String, XsdLinkage> GetLinkages()
    {
        return GetLinkages((String) null);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    // TODO: Is this needed anymore
    /*
     * public CallResult CreateNewEntityTemplate(CoalesceEntityTemplate EntityTemplate){ try{ CallResult rst;
     * CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();
     * 
     * // Initialize the EntityTemplate from this rst = EntTemp.InitializeFromEntity(this);
     * 
     * // Evaluate if (rst.getIsSuccess()) EntityTemplate = EntTemp; else EntityTemplate = null;
     * 
     * // return return rst;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    public XsdLinkageSection CreateLinkageSection()
    {

        // TODO: Check that this actually maps to the Entity.LinkSection
        return XsdLinkageSection.Create(this);

    }

    public XsdSection CreateSection(String name, boolean noIndex)
    {
        return XsdSection.Create(this, name, noIndex);
    }

    public XsdSection CreateSection(String name)
    {
        // TODO: Check that this actually maps to the Entity.Section

        return XsdSection.Create(this, name);
    }

    public Map<String, XsdSection> GetSections()
    {

        Map<String, XsdSection> sections = new HashMap<String, XsdSection>();

        for (XsdDataObject child : _childDataObjects.values())
        {
            if (child instanceof XsdSection)
            {
                sections.put(child.GetKey(), (XsdSection) child);
            }
        }

        return sections;

    }

    public XsdLinkageSection GetLinkageSection()
    {

        for (XsdDataObject child : _childDataObjects.values())
        {
            if (child instanceof XsdLinkageSection)
            {
                return (XsdLinkageSection) child;
            }
        }

        return null;

    }

    public Map<String, XsdLinkage> GetLinkages(String forEntityName)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = GetLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if (forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                {
                    linkages.put(cdo.GetKey(), linkage);
                }
            }
        }

        return linkages;

    }

    public Map<String, XsdLinkage> GetLinkages(ELinkTypes forLinkType, String forEntityName)
    {

        List<ELinkTypes> forLinkTypes = new ArrayList<ELinkTypes>();
        forLinkTypes.add(forLinkType);

        return GetLinkages(forLinkTypes, forEntityName);
    }

    public Map<String, XsdLinkage> GetLinkages(List<ELinkTypes> forLinkTypes, String forEntityName)
    {
        return GetLinkages(forLinkTypes, forEntityName, null);
    }

    public Map<String, XsdLinkage> GetLinkages(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
    {

        List<ELinkTypes> forLinkTypes = new ArrayList<ELinkTypes>();
        forLinkTypes.add(forLinkType);

        return GetLinkages(forLinkTypes, forEntityName, forEntitySource);

    }

    public Map<String, XsdLinkage> GetLinkages(ELinkTypes forLinkType)
    {
        return GetLinkages(forLinkType, null);
    }

    // TODO: Need to test this
    public XsdSection GetSection(String NamePath)
    {
        try
        {

            XsdDataObject dataObject = GetDataObjectForNamePath(NamePath);

            if (dataObject != null && dataObject instanceof XsdSection)
            {
                return (XsdSection) dataObject;
            }

            return null;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult GetEntityId(String Param, String Value)
    {
        try
        {

            // EntityID Type Contain Param?
            int Idx = Arrays.asList(this.GetEntityIdType().split(",")).indexOf(Param);
            if (Idx == -1) return new CallResult(CallResults.FAILED, "Not Found", this);

            // Get Value
            String[] IdArray = this.GetEntityId().split(",");
            Value = IdArray[Idx];

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetEntityId(String Param, String Value)
    {

        try
        {

            if (Param == null || Param.trim() == "" || Value == null || Value.trim() == "") return new CallResult(CallResults.FAILED,
                                                                                                                  "Invalid",
                                                                                                                  this);

            // Collection Already have Unique ID?
            if (this.GetEntityId() == null || this.GetEntityId().trim() == "")
            {
                // No; Add
                this.SetEntityIdType(Param);
                this.SetEntityId(Value);
            }
            else
            {
                // Yes; Append (CSV)
                this.SetEntityIdType(this.GetEntityIdType() + "," + Param);
                this.SetEntityId(this.GetEntityId() + "," + Value);
            }

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public void MarkAsDeleted()
    {
        this.SetStatus(ECoalesceDataObjectStatus.DELETED);
    }

    // TODO: Implement Syncshell
    /*
     * public CallResult GetSyncEntity(CoalesceEntitySyncShell SyncShell, CoalesceEntity SyncEntity){ try{ CallResult rst;
     * 
     * // Make the SyncEntity a Clone of this SyncEntity = new CoalesceEntity(); //TODO: Verifiy this.GetDataObjectDocument()
     * is good substitute for this._DataObjectDocument.Clone rst = SyncEntity.Initialize(this.GetDataObjectDocument()); if
     * (!(rst.getIsSuccess())) return rst;
     * 
     * // Get Required Changes Keys String XPath = "//@key"; NodeList Nodes = null; ArrayList<String> SyncKeys = new
     * ArrayList<String>();
     * 
     * //TODO: figure out how to select nodes by xpath // Nodes = SyncShell._DataObjectDocument.SelectNodes(XPath); // if
     * (Nodes == null){ // // do nothing // }else{ // for(Node KeyNode : Nodes){ // // Add Key to the RequiredChangesKeys //
     * SyncKeys.Add(KeyNode.Value); // } // }
     * 
     * // Prune Non-Required Nodes rst = PruneNonRequiredNodes(SyncKeys, SyncEntity.GetDataObjectDocument());
     * 
     * // return Success return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    // How to prune an Entity
    /*
     * protected CallResult PruneNonRequiredNodes(ArrayList SyncKeys, Node TheNode){ try{ CallResult rst;
     * 
     * // Recurse Child Nodes (Important: Because this us up front, we check leaf nodes first, which is necessary for //
     * correct pruning.) Since we're disturbing the Node.ChildNodes collection because we may remove children from // it,
     * it's necessary to create a temporary collection of the initial children before we prune. If we // don't do this, we
     * don't get the correct behavior. for(int i=0; i<TheNode.getChildNodes().getLength(); i++){ Node Child =
     * TheNode.getChildNodes().item(i); rst = PruneNonRequiredNodes(SyncKeys, Child); }
     * 
     * // Check to see if Node needs to be Pruned String Key = _XmlHelper.GetAttribute(TheNode, "key"); if (Key != "") { if
     * (!(SyncKeys.contains(Key))) { // Prune if (TheNode.getParentNode() != null)
     * TheNode.getParentNode().removeChild(TheNode); } }
     * 
     * // return Success return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    /*
     * public CallResult MergeSyncEntity(CoalesceEntity SyncEntity){ try{ // Merge Recursively, Starting With the Entity Node
     * return MergeSyncEntityNode(this._DataObjectNode, SyncEntity._DataObjectNode);
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     * 
     * protected CallResult MergeSyncEntityNode(Node MyNode, Node SyncEntityNode){ try{ CallResult rst;
     * 
     * // Get Timestamps Date MyLastModified = _XmlHelper.GetAttributeAsDate(MyNode, "lastmodified"); Date UpdateLastModified
     * = _XmlHelper.GetAttributeAsDate(SyncEntityNode, "lastmodified");
     * 
     * //TODO Attribute, getAttributes & SelectSingleNode(xpath)? // // Compare Timestamps // switch
     * (MyLastModified.compareTo(UpdateLastModified)){ // case -1: // // Mine is Older; Update Each Attribute. //
     * for(Attribute UpdateAttribute : SyncEntityNode.getAttributes()){ // // Set Attribute //
     * _XmlHelper.SetAttribute(this._DataObjectDocument, MyNode, UpdateAttribute.Name, UpdateAttribute.Value); // } // } //
     * // // Merge Required Node's Children // for(int i=0; i<SyncEntityNode.getChildNodes().getLength(); i++){ // Node
     * UpdateChildNode = SyncEntityNode.getChildNodes().item(i); // // Get Node To Update // String Key =
     * _XmlHelper.GetAttribute(UpdateChildNode, "key"); // String XPath = UpdateChildNode.getNodeName() + "[@key='" + Key +
     * "']"; // Node MyChildNode = MyNode.SelectSingleNode(XPath); // // // Evaluate // if (MyChildNode == null) { // // We
     * don't have this child; add the entire ChildNode //
     * MyNode.appendChild(this._DataObjectDocument.importNode(UpdateChildNode, true)); // }else{ // // We have this child;
     * Call MergeRequiredNode // rst = MergeSyncEntityNode(MyChildNode, UpdateChildNode); // } // }
     * 
     * // return Success return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    public String ToXml()
    {
        return this.ToXml(false);
    }

    public String ToXml(Boolean removeBinary)
    {

        String entityXml = XmlHelper.Serialize(_entity);

        if (removeBinary)
        {

            // TODO: How to get the OuterXml? And SelectNodes(Xpath)?
            // // Set a copy of the Xml without the Binary data in it.
            // Document NoBinaryXmlDoc = new Document();
            // NoBinaryXmlDoc.LoadXml(this._DataObjectDocument.OuterXml);
            //
            // // Get all Binary Field Nodes. Ensures that the 'binary'
            // attribute value is handled in a case
            // // insensitive way.
            // String Xpath =
            // "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='binary']";
            // for(Node ChildNode : NoBinaryXmlDoc.SelectNodes(Xpath)){
            // _XmlHelper.SetAttribute(NoBinaryXmlDoc, ChildNode,
            // "value", "");
            // }
            //
            // // Get all File Field Nodes. Ensures that the 'file'
            // attribute value is handled in a case
            // // insensitive way.
            // Xpath =
            // "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='file']";
            // for(Node ChildNode : NoBinaryXmlDoc.SelectNodes(Xpath)){
            // _XmlHelper.SetAttribute(NoBinaryXmlDoc, ChildNode,
            // "value", "");
            // }
            //
            // // Get Xml
            // Xml = NoBinaryXmlDoc.OuterXml;
            // }else{
            // // Get Xml
            // Xml = this._DataObjectDocument.OuterXml;

        }

        return entityXml;

    }

    private Map<String, XsdLinkage> GetLinkages(List<ELinkTypes> forLinkTypes, String forEntityName, String forEntitySource)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = GetLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.GetChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if ((forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                        && forLinkTypes.contains(linkage.GetLinkType())
                        && (forEntitySource == null || linkage.GetEntity2Source().equalsIgnoreCase(forEntitySource))
                        && linkage.GetStatus() != ECoalesceDataObjectStatus.DELETED)
                {
                    linkages.put(linkage.GetKey(), linkage);
                }
            }
        }

        return linkages;

    }

    protected Entity.Linkagesection GetEntityLinkageSection()
    {
        Linkagesection linkageSection = _entity.getLinkagesection();

        if (linkageSection == null)
        {
            linkageSection = new Entity.Linkagesection();
            this._entity.setLinkagesection(linkageSection);
        }

        return linkageSection;
    }

    protected List<Section> GetEntitySections()
    {
        return _entity.getSection();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entity.getOtherAttributes();
    }
}
