package Coalesce.Framework.DataModel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Coalesce.Common.Exceptions.CoalesceException;
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

        if (!super.initialize()) return false;

        if (!initializeChildren()) return false;

        return initializeReferences();

    }

    protected boolean initializeChildren()
    {

        XsdLinkageSection linkageSection = new XsdLinkageSection();

        if (!linkageSection.initialize(this)) return false;

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
                    pathTitle += field.getValue() + ", ";
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

    @Override
    public DateTime getDateCreated()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getDatecreated());
        return _entity.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entity.getLastmodified());
        return _entity.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entity.setLastmodified(value);
    }

    @Override
    protected String getObjectStatus()
    {
        return _entity.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entity.setStatus(status.getLabel());
    }

    public Map<String, XsdLinkage> getLinkages()
    {
        return getLinkages((String) null);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    public CoalesceEntityTemplate createNewEntityTemplate() throws SAXException, IOException
    {

        CoalesceEntityTemplate entTemp = new CoalesceEntityTemplate();

        // Initialize the EntityTemplate from this
        if (entTemp.initialize(this))
        {
            return entTemp;
        }
        else
        {
            return null;
        }
    }

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
                if (forEntityName == null || linkage.getEntity2Name().equalsIgnoreCase(forEntityName))
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
        if (typeParam == null) throw new NullArgumentException("typeParam");
        if (value == null) throw new NullArgumentException("value");
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

    public static XsdEntity mergeSyncEntity(XsdEntity myEntity, XsdEntity syncEntity) throws CoalesceException
    {
        try
        {
            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom2.Document syncEntityDoc;
            syncEntityDoc = saxBuilder.build(new InputSource(new StringReader(syncEntity.toXml())));
            org.jdom2.Document myEntityDoc = saxBuilder.build(new InputSource(new StringReader(myEntity.toXml())));

            mergeSyncEntityDataObject(myEntityDoc.getRootElement(), syncEntityDoc.getRootElement());

            // Convert back to entity object
            XMLOutputter xmlOutPutter = new XMLOutputter();
            String output = xmlOutPutter.outputString(myEntityDoc);

            return XsdEntity.create(output);
        }
        catch (JDOMException | IOException e)
        {
            throw new CoalesceException("mergeSyncEntity", e);
        }
    }

    protected static void mergeSyncEntityDataObject(Element myEntity, Element syncEntity)
    {
        // Get Attributes
        List<Attribute> myEntityDocAttributes = myEntity.getAttributes();
        List<Attribute> syncEntityDocAttributes = syncEntity.getAttributes();

        // Get Time Stamps
        DateTime myLastModified = JodaDateTimeHelper.FromXmlDateTimeUTC(myEntityDocAttributes.get(2).getValue());
        DateTime updateLastModified = JodaDateTimeHelper.FromXmlDateTimeUTC(syncEntityDocAttributes.get(2).getValue());

        // Compare Timestamps
        switch (myLastModified.compareTo(updateLastModified)) {

        case -1:
            for (Attribute syncAttribute : syncEntityDocAttributes)
            {
                // Overwrite myAttribute with syncAttribute
                boolean attributeReplaced = false;
                for (Attribute myAttribute : myEntityDocAttributes)
                {
                    if (myAttribute.getName().equals(syncAttribute.getName()))
                    {
                        myAttribute.setValue(syncAttribute.getValue());
                        attributeReplaced = true;
                    }
                }
                // Add syncAttribute if it is not there
                if (!attributeReplaced)
                {
                    myEntityDocAttributes.add(syncAttribute.detach());
                }
            }
        }

        // Get Children
        List<Element> myEntityDocChildren = myEntity.getChildren();
        List<Element> syncEntityDocChildren = syncEntity.getChildren();

        // Merge Required Node's Children
        for (Element syncElement : syncEntityDocChildren)
        {

            // get child element to update
            String syncKey = syncElement.getAttributes().get(0).getValue();
            Element myEntityElement = null;

            // Compare keys
            for (Element myElement : myEntityDocChildren)
            {

                String myKey = myElement.getAttributes().get(0).getValue();
                if (myKey.equals(syncKey))
                {
                    myEntityElement = myElement;
                }
            }

            // Evaluate
            if (myEntityElement == null)
            {
                // We don't have this child; add the entire Child data object from updatechild
                myEntityDocChildren.add(syncElement.detach());

            }
            else
            {
                // We have this child; Call MergeRequiredNode
                mergeSyncEntityDataObject(myEntityElement, syncElement);
            }
        }
    }

    @Override
    public String toXml()
    {
        return toXml(false);
    }

    public String toXml(String setSQLServer)
    {
        if (setSQLServer.trim().toLowerCase() == "true")
            return toXml(true).replace("UTF-8", "UTF-16");
        else
            return toXml();
    }

    public String toXml(Boolean removeBinary)
    {

        String entityXml = XmlHelper.Serialize(_entity, "UTF-8");

        if (removeBinary)
        {

            // Set a copy of the Xml without the Binary data in it.
            Document NoBinaryXmlDoc;
            try
            {
                NoBinaryXmlDoc = XmlHelper.loadXMLFrom(entityXml);

                // Get all Binary Field Nodes. Ensures that the 'binary' attribute value is handled in a case insensitive
                // way.
                clearFieldTypeValue("binary", NoBinaryXmlDoc);

                // Get all File Field Nodes. Ensures that the 'file' attribute value is handled in a case insensitive way.
                clearFieldTypeValue("file", NoBinaryXmlDoc);

                // Get Xml
                entityXml = XmlHelper.FormatXml(NoBinaryXmlDoc);

            }
            catch (SAXException e)
            {
                // loadXmlFrom failed
            }
            catch (IOException e)
            {
                // loadXmlFrom failed
            }
        }

        return entityXml;

    }

    /*--------------------------------------------------------------------------
    Private and Protected Functions
    --------------------------------------------------------------------------*/

    private void clearFieldTypeValue(String fieldType, Document xmlDoc)
    {
        try
        {
            String expression = "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                    + fieldType + "']";

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xPath.evaluate(expression, xmlDoc.getDocumentElement(), XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node childNode = (Node) nodes.item(i);

                XmlHelper.SetAttribute(xmlDoc, childNode, "value", "");
            }

        }
        catch (XPathExpressionException xee)
        {
            // Xpath failed. Do nothing
        }
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
                if ((forEntityName == null || linkage.getEntity2Name().equalsIgnoreCase(forEntityName))
                        && forLinkTypes.contains(linkage.getLinkType())
                        && (forEntitySource == null || linkage.getEntity2Source().equalsIgnoreCase(forEntitySource))
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
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entity.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entity.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entity.getLastmodified()));
        map.put(new QName("name"), _entity.getName());
        map.put(new QName("source"), _entity.getSource());
        map.put(new QName("version"), _entity.getVersion());
        map.put(new QName("entityid"), _entity.getEntityid());
        map.put(new QName("entityidtype"), _entity.getEntityidtype());
        map.put(new QName("title"), _entity.getTitle());
        map.put(new QName("status"), _entity.getStatus());
        return map;
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name) {
        case "key":
            _entity.setKey(value);
            return true;
        case "datecreated":
            _entity.setDatecreated(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entity.setLastmodified(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "name":
            _entity.setName(value);
            return true;
        case "source":
            _entity.setSource(value);
            return true;
        case "version":
            _entity.setVersion(value);
            return true;
        case "entityid":
            _entity.setEntityid(value);
            return true;
        case "title":
            _entity.setTitle(value);
            return true;
        case "status":
            _entity.setStatus(value);
            return true;
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    protected Map<QName, String> getOtherAttributes()
    {
        return this._entity.getOtherAttributes();
    }

}
