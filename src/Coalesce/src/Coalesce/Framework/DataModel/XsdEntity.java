package Coalesce.Framework.DataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

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

    public static XsdEntity create(String entityXml)
    {

        // Create Entity
        XsdEntity entity = new XsdEntity();

        boolean passed = entity.initialize(entityXml);

        if (!passed) return null;

        return entity;

    }

    public static XsdEntity create(String entityXml, String title)
    {

        // Create Entity
        XsdEntity entity = XsdEntity.create(entityXml);

        // Set Title
        entity.setTitle(title);

        return entity;

    }

    public static XsdEntity create(String name, String source, String version, String entityId, String entityIdType)
    {
        return XsdEntity.create(name, source, version, entityId, entityIdType, null);
    }

    public static XsdEntity create(String name,
                                   String source,
                                   String version,
                                   String entityId,
                                   String entityIdType,
                                   String title)
    {

        XsdEntity entity = new XsdEntity();
        if (!entity.initialize()) return null;

        // Set Default Values
        entity.setName(name);
        entity.setSource(source);
        entity.setVersion(version);
        entity.setEntityId(entityId);
        entity.setEntityIdType(entityIdType);
        if (title != null) entity.setTitle(title);

        return entity;
    }

    public boolean initialize(String entityXml)
    {

        if (entityXml == null || StringHelper.IsNullOrEmpty(entityXml.trim()))
        {
            return initialize();
        }
        else
        {
            Object deserializedObject = XmlHelper.Deserialize(entityXml, Entity.class);

            if (deserializedObject == null || !(deserializedObject instanceof Entity))
            {
                return false;
            }
            this._entity = (Entity) deserializedObject;

            if (!super.initialize()) return false;

            if (!initializeChildren()) return false;

            return initializeReferences();

        }
    }

    public boolean initialize()
    {

        this._entity = new Entity();
        // this.CreateLinkageSection();
        // this._entity.getSection();

        if (!super.initialize()) return false;

        if (!initializeChildren()) return false;

        return initializeReferences();

    }

    protected boolean initializeChildren()
    {

        XsdLinkageSection linkageSection = new XsdLinkageSection();

        if (!linkageSection.Initialize(this)) return false;

        _childDataObjects.put(linkageSection.getKey(), linkageSection);

        for (Section entitySection : _entity.getSection())
        {
            XsdSection section = new XsdSection();

            if (!section.initialize(this, entitySection)) return false;

            _childDataObjects.put(section.getKey(), section);

        }

        return true;

    }

    protected boolean initializeReferences()
    {
        return true;
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entity.getKey();
    }

    @Override
    protected void setObjectKey(String value)
    {
        _entity.setKey(value);
    }

    @Override
    public String getName()
    {
        return getStringElement(_entity.getName());
    }

    @Override
    public void setName(String value)
    {
        _entity.setName(value);
    }

    @Override
    public String getType()
    {
        return "entity";
    }

    public String getSource()
    {
        return getStringElement(_entity.getSource());
    }

    public void setSource(String value)
    {
        _entity.setSource(value);
    }

    public String getVersion()
    {
        return getStringElement(_entity.getVersion());
    }

    public void setVersion(String value)
    {
        _entity.setVersion(value);
    }

    public String getEntityId()
    {
        return getStringElement(_entity.getEntityid());
    }

    public void setEntityId(String value)
    {
        _entity.setEntityid(value);
    }

    public String getEntityIdType()
    {
        return getStringElement(_entity.getEntityidtype());
    }

    public void setEntityIdType(String value)
    {
        _entity.setEntityidtype(value);
    }

    public String getTitle()
    {
        String title = _entity.getTitle();

        // Check if value contains an XPath
        if (title != null && title.contains("/") && title.length() > 50)
        {

            String pathTitle = "";

            String[] paths = title.split(",");
            for (String path : paths)
            {

                XsdDataObject dataObject = getDataObjectForNamePath(path);

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
            return this.getSource();
        }
        else
        {
            return title;
        }

    }

    public void setTitle(String value)
    {
        String currentTitle = _entity.getTitle();

        if ((currentTitle == null ^ value == null) || (value != null && !value.equals(getTitle())))
        {

            _entity.setTitle(value);

            // Set LastModified
            DateTime utcNow = JodaDateTimeHelper.NowInUtc();
            if (utcNow != null) setLastModified(utcNow);
        }

    }

    public DateTime getDateCreated()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getDatecreated());
        return _entity.getDatecreated();
    }

    public void setDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setDatecreated(value);
    }

    public DateTime getLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getLastmodified());
        return _entity.getLastmodified();
    }

    protected void setObjectLastModified(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setLastmodified(value);
    }

    protected String getObjectStatus()
    {
        return _entity.getStatus();
    }

    protected void setObjectStatus(String status)
    {
        _entity.setStatus(status);
    }

    public Map<String, XsdLinkage> getLinkages()
    {
        return getLinkages((String) null);
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

    public XsdSection createSection(String name, boolean noIndex)
    {
        return XsdSection.create(this, name, noIndex);
    }

    public XsdSection createSection(String name)
    {
        return XsdSection.create(this, name);
    }

    public Map<String, XsdSection> getSections()
    {

        Map<String, XsdSection> sections = new HashMap<String, XsdSection>();

        for (XsdDataObject child : _childDataObjects.values())
        {
            if (child instanceof XsdSection)
            {
                sections.put(child.getKey(), (XsdSection) child);
            }
        }

        return sections;

    }

    public XsdLinkageSection getLinkageSection()
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

    public Map<String, XsdLinkage> getLinkages(String forEntityName)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if (forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                {
                    linkages.put(cdo.getKey(), linkage);
                }
            }
        }

        return linkages;

    }

    public Map<String, XsdLinkage> getLinkages(ELinkTypes forLinkType)
    {
        return getLinkages(forLinkType, null);
    }

    public Map<String, XsdLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName)
    {
        return getLinkages(forLinkType, forEntityName, null);
    }

    public Map<String, XsdLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
    {
        return getLinkages(Arrays.asList(forLinkType), forEntityName, forEntitySource);
    }

    public Map<String, XsdLinkage> getLinkages(List<ELinkTypes> forLinkTypes, String forEntityName)
    {
        return getLinkages(forLinkTypes, forEntityName, null);
    }

    public XsdSection getSection(String NamePath)
    {
        XsdDataObject dataObject = getDataObjectForNamePath(NamePath);

        if (dataObject != null && dataObject instanceof XsdSection)
        {
            return (XsdSection) dataObject;
        }

        return null;
    }

    public List<String> getEntityId(String typeParam)
    {
        List<String> values = new ArrayList<String>();

        // EntityID Type Contain Param?
        String[] types = getEntityIdType().split(",");
        String[] ids = getEntityId().split(",");
        for (int i = 0; i < types.length; i++)
        {
            String type = types[i];
            if (type.equalsIgnoreCase(typeParam))
            {
                values.add(ids[i]);
            }
        }

        return values;

    }

    public boolean setEntityId(String typeParam, String value)
    {
        if (typeParam.trim() == "") throw new IllegalArgumentException("typeParam cannot be empty");
        if (value.trim() == "") throw new IllegalAccessError("value cannot be empty");

        // Collection Already have Unique ID?
        if (getEntityId() == null || getEntityId().trim() == "")
        {
            // No; Add
            setEntityIdType(typeParam);
            setEntityId(value);
        }
        else
        {
            // Yes; Append (CSV)
            setEntityIdType(getEntityIdType() + "," + typeParam);
            setEntityId(getEntityId() + "," + value);
        }

        return true;

    }

    public void markAsDeleted()
    {
        this.setStatus(ECoalesceDataObjectStatus.DELETED);
    }

    public CoalesceEntitySyncShell getSyncEntity() throws SAXException, IOException
    {
        return CoalesceEntitySyncShell.Create(this);
    }

    public void mergeSyncEntity(XsdEntity syncEntity)
    {
        // TODO: Implement Merging
    }

    public String toXml()
    {
        return toXml(false);
    }

    public String toXml(Boolean removeBinary)
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

    private Map<String, XsdLinkage> getLinkages(List<ELinkTypes> forLinkTypes, String forEntityName, String forEntitySource)
    {
        Map<String, XsdLinkage> linkages = new HashMap<String, XsdLinkage>();

        // Get Linkage Section
        XsdLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof XsdLinkage)
            {

                XsdLinkage linkage = (XsdLinkage) cdo;
                if ((forEntityName == null || linkage.GetEntity2Name().equalsIgnoreCase(forEntityName))
                        && forLinkTypes.contains(linkage.GetLinkType())
                        && (forEntitySource == null || linkage.GetEntity2Source().equalsIgnoreCase(forEntitySource))
                        && linkage.getStatus() != ECoalesceDataObjectStatus.DELETED)
                {
                    linkages.put(linkage.getKey(), linkage);
                }
            }
        }

        return linkages;

    }

    protected Entity.Linkagesection getEntityLinkageSection()
    {
        Linkagesection linkageSection = _entity.getLinkagesection();

        if (linkageSection == null)
        {
            linkageSection = new Entity.Linkagesection();
            this._entity.setLinkagesection(linkageSection);
        }

        return linkageSection;
    }

    protected List<Section> getEntitySections()
    {
        return _entity.getSection();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entity.getOtherAttributes();
    }
}
