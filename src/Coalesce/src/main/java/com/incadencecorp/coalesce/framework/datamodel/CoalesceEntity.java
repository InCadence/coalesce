package com.incadencecorp.coalesce.framework.datamodel;

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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Linkagesection;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section;

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

public class CoalesceEntity extends CoalesceDataObject {

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    private Entity _entity;

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    /**
     * Creates an XsdEntity based off of an (XML) String.
     * 
     * @param entityXml (XML) String that the XsdEntity is to be created from
     * 
     * @return XsdEntity resulting from entityXml String parameter, null if failed
     */
    public static CoalesceEntity create(String entityXml)
    {

        // Create Entity
        CoalesceEntity entity = new CoalesceEntity();

        boolean passed = entity.initialize(entityXml);

        if (!passed) return null;

        return entity;

    }

    /**
     * Creates an XsdEntity based off of an (XML) String and sets the title.
     * 
     * @param entityXml (XML) String that the XsdEntity is to be created from.
     * @param title String that could be a a field namepath.
     * 
     * @return XsdEntity resulting from entityXml String parameter, null if failed
     */
    public static CoalesceEntity create(String entityXml, String title)
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        // Set Title
        entity.setTitle(title);

        return entity;

    }

    /**
     * Creates a new XsdEntity of the name, source and version specified for the entityid and entityidtype specified.
     * 
     * @param name String identifying the XsdEntity type to create
     * @param source String identifying the XsdEntity source
     * @param version String identifying the XsdEntity version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * 
     * @return XsdEntity resulting from entityXml String parameter, null if failed
     */
    public static CoalesceEntity create(String name, String source, String version, String entityId, String entityIdType)
    {
        return CoalesceEntity.create(name, source, version, entityId, entityIdType, null);
    }

    /**
     * Creates a new XsdEntity of the name, source and version specified for the entityid and entityidtype specified. Also
     * sets the title.
     * 
     * @param name String identifying the XsdEntity type to create
     * @param source String identifying the XsdEntity source
     * @param version String identifying the XsdEntity version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * @param title String that could be a a field namepath.
     * 
     * @return XsdEntity resulting from entityXml String parameter, null if failed
     */
    public static CoalesceEntity create(String name,
                                   String source,
                                   String version,
                                   String entityId,
                                   String entityIdType,
                                   String title)
    {

        CoalesceEntity entity = new CoalesceEntity();
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

    /**
     * Initializes core settings.
     *  
     * @param name String identifying the XsdEntity type to create
     * @param source String identifying the XsdEntity source
     * @param version String identifying the XsdEntity version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * @param title String that could be a a field namepath.
     */
    public boolean initialize(String name, String source, String version, String entityId, String entityIdType, String title)
    {
        if (!initialize()) return false;
        
        this.setName(name);
        this.setSource(source);
        this.setVersion(version);
        this.setEntityId(entityId);
        this.setEntityIdType(entityIdType);
        this.setTitle(title);
        
        return true;
    }

    /**
     * Initializes a previously new XsdEntity based off of an (XML) String.
     * 
     * @param entityXml (XML) String that the XsdEntity is to be initialized from.
     * @return boolean indicator of success/failure
     */
    public boolean initialize(String entityXml)
    {

        if (entityXml == null || StringHelper.isNullOrEmpty(entityXml.trim()))
        {
            return initialize();
        }
        else
        {
            Object deserializedObject = XmlHelper.deserialize(entityXml, Entity.class);

            if (deserializedObject == null || !(deserializedObject instanceof Entity))
            {
                return false;
            }
            _entity = (Entity) deserializedObject;

            if (!super.initialize()) return false;

            if (!initializeChildren()) return false;

            return initializeReferences();
        }
    }

    /**
     * Initializes from an existing XsdEntity.
     * 
     * @param entity XsdEntity to duplicate.
     * @return
     */
    public boolean initialize(CoalesceEntity entity)
    {
        // Copy Member Variables
        _entity = entity._entity;
        _parent = entity._parent;
        _childDataObjects = entity._childDataObjects;

        // Initialize References
        return initializeReferences();
    }

    /**
     * Initializes a previously new XsdEntity by initializing skeletal dataObjectChildren.
     * 
     * @return boolean indicator of success/failure
     */
    @Override
    public boolean initialize()
    {
        this._entity = new Entity();

        if (!super.initialize()) return false;

        if (!initializeChildren()) return false;

        return true;
    }

    protected boolean initializeChildren()
    {
        CoalesceLinkageSection linkageSection = new CoalesceLinkageSection();

        if (!linkageSection.initialize(this)) return false;

        _childDataObjects.put(linkageSection.getKey(), linkageSection);

        for (Section entitySection : _entity.getSection())
        {
            CoalesceSection section = new CoalesceSection();

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

    /**
     * Returns the XsdEntity's source attribute value.
     * 
     * @return String, source attribute value
     */
    public String getSource()
    {
        return getStringElement(_entity.getSource());
    }

    /**
     * Sets the XsdEntity's source attribute value.
     * 
     * @param value String, new value for the source attribute
     */
    public void setSource(String value)
    {
        _entity.setSource(value);
    }

    /**
     * Returns the XsdEntity's version attribute value.
     * 
     * @return String, version attribute value
     */
    public String getVersion()
    {
        return getStringElement(_entity.getVersion());
    }

    /**
     * Sets the XsdEntity's version attribute value.
     * 
     * @param value String, new value for the version attribute
     */
    public void setVersion(String value)
    {
        _entity.setVersion(value);
    }

    /**
     * Returns the XsdEntity's EntityId attribute value.
     * 
     * @return String, EntityId attribute value
     */
    public String getEntityId()
    {
        return getStringElement(_entity.getEntityid());
    }

    /**
     * Sets the XsdEntity's EntityId attribute value.
     * 
     * @param value String, new value for the EntityId attribute
     */
    public void setEntityId(String value)
    {
        _entity.setEntityid(value);
    }

    /**
     * Returns the XsdEntity's EntityIdType attribute value.
     * 
     * @return String, EntityIdType attribute value
     */
    public String getEntityIdType()
    {
        return getStringElement(_entity.getEntityidtype());
    }

    /**
     * Sets the XsdEntity's EntityIdType attribute value.
     * 
     * @param value String, new value for the EntityIdType attribute
     */
    public void setEntityIdType(String value)
    {
        _entity.setEntityidtype(value);
    }

    /**
     * Returns the XsdEntity's title attribute value.
     * 
     * @return String, title attribute value
     */
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

                CoalesceDataObject dataObject = getDataObjectForNamePath(path);

                if (dataObject != null && dataObject instanceof CoalesceField<?>)
                {
                    CoalesceField<?> field = (CoalesceField<?>) dataObject;
                    pathTitle += field.getBaseValue() + ", ";
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

    /**
     * Sets the XsdEntity's title attribute value.
     * 
     * @param value String, new value for the title attribute
     */
    public void setTitle(String value)
    {
        String currentTitle = _entity.getTitle();

        if ((currentTitle == null ^ value == null) || (value != null && !value.equals(getTitle())))
        {

            _entity.setTitle(value);

            // Set LastModified
            DateTime utcNow = JodaDateTimeHelper.nowInUtc();
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

    /**
     * Returns the XsdEntity's linkages from the XsdEntity's linkagesection.
     * 
     * @return Map<String, XsdLinkage>, XsdLinkages of relationships to this XsdEntity
     */
    public Map<String, CoalesceLinkage> getLinkages()
    {
        return getLinkages((String) null);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates a CoalesceEntityTemplate based off of this XsdEntity.
     * 
     * @return CoalesceEntityTemplate generated from this XsdEntity
     * 
     * @throws SAXException
     * @throws IOException
     */
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

    /**
     * Creates an XsdSection for this XsdEntity.
     * 
     * @param name String, the namepath of the section.
     * @param noIndex boolean
     * 
     * @return XsdSection, newly created and now belonging to this XsdEntity
     */
    public CoalesceSection createSection(String name, boolean noIndex)
    {
        return CoalesceSection.create(this, name, noIndex);
    }

    /**
     * Creates an XsdSection for this XsdEntity.
     * 
     * @param name String, the namepath of the section.
     * 
     * @return XsdSection, newly created and now belonging to this XsdEntity
     */
    public CoalesceSection createSection(String name)
    {
        return CoalesceSection.create(this, name);
    }

    /**
     * Returns this XsdEntity's sections.
     * 
     * @return Map<String, XsdSection>, sections belonging to this XsdEntity
     */
    public Map<String, CoalesceSection> getSections()
    {

        Map<String, CoalesceSection> sections = new HashMap<String, CoalesceSection>();

        for (CoalesceDataObject child : _childDataObjects.values())
        {
            if (child instanceof CoalesceSection)
            {
                sections.put(child.getKey(), (CoalesceSection) child);
            }
        }

        return sections;

    }

    /**
     * Returns this XsdEntity's XsdLinkageSection.
     * 
     * @return XsdLinkageSection belonging to this XsdEntity
     */
    public CoalesceLinkageSection getLinkageSection()
    {

        for (CoalesceDataObject child : _childDataObjects.values())
        {
            if (child instanceof CoalesceLinkageSection)
            {
                return (CoalesceLinkageSection) child;
            }
        }

        return null;

    }

    /**
     * Returns the XsdEntity's linkages, from the XsdEntity's linkagesection, for the EntityName specified. Returns all
     * linkages when the forEntityName parameter is null.
     * 
     * @param forEntityName String of the Entity Name to return linkages for
     * @return Map<String, XsdLinkage>, linkages with matches for the Entity Name parameter
     */
    public Map<String, CoalesceLinkage> getLinkages(String forEntityName)
    {
        Map<String, CoalesceLinkage> linkages = new HashMap<String, CoalesceLinkage>();

        // Get Linkage Section
        CoalesceLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage linkage = (CoalesceLinkage) cdo;
                if (forEntityName == null || linkage.getEntity2Name().equalsIgnoreCase(forEntityName))
                {
                    linkages.put(cdo.getKey(), linkage);
                }
            }
        }

        return linkages;

    }

    /**
     * Returns the XsdEntity's linkages, from the XsdEntity's linkagesection, based on LinkType.
     * 
     * @return Map<String, XsdLinkage>, linkages with matches for the ELinkTypes parameter
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType)
    {
        return getLinkages(forLinkType, null);
    }

    /**
     * Returns the XsdEntity's linkages, from the XsdEntity's linkagesection, based on LinkType and EntityName specified.
     * 
     * @param forLinkType ELinkTypes (one link type), the type of relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching linkages for
     * @return Map<String, XsdLinkage>, linkages with matches for the Entity Name and ELinkType parameters
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName)
    {
        return getLinkages(forLinkType, forEntityName, null);
    }

    /**
     * Returns the XsdEntity's linkages, from the XsdEntity's linkagesection, based on LinkType EntityName and Entity Source
     * specified.
     * 
     * @param forLinkType ELinkTypes, the type of relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching linkages for
     * @param forEntitySource String, the Entity source attribute to find matching linkages for
     * @return Map<String, XsdLinkage>, linkages with matches for the parameter criteria
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
    {
        return getLinkages(Arrays.asList(forLinkType), forEntityName, forEntitySource);
    }

    /**
     * Returns the XsdEntity's linkages, from the XsdEntity's linkagesection, based on a list of LinkTypes and the EntityName
     * specified.
     * 
     * @param forLinkType ELinkTypes (list of link types), the type of relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching linkages for
     * @return
     */
    public Map<String, CoalesceLinkage> getLinkages(List<ELinkTypes> forLinkTypes, String forEntityName)
    {
        return getLinkages(forLinkTypes, forEntityName, null);
    }

    /**
     * Returns the XsdEntity's XsdSection specified by namepath string.
     * 
     * @param namePath String, namepath of the desired XsdSection
     * @return XsdSection having the matching namepath parameter. Null if not found.
     */
    public CoalesceSection getSection(String namePath)
    {
        CoalesceDataObject dataObject = getDataObjectForNamePath(namePath);

        if (dataObject != null && dataObject instanceof CoalesceSection)
        {
            return (CoalesceSection) dataObject;
        }

        return null;
    }

    /**
     * Returns the XsdEntity's list of EntityIds specified by EntityIdType String.
     * 
     * @param typeParam, EntityIdType String to retrieve entityids for
     * @return List<String> list of entityids that match the EntityIdType typeParam
     */
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

    /**
     * Sets the XsdEntity's EntityId and EntityIdType attribute values when values do not exist. Appends values when the
     * attributes have values.
     * 
     * @param typeParam String EntityIdType value to append to the EntityIdType attribute
     * @param value String EntityId value to append to the EntityId attribute
     * 
     * @return boolean indicator of success/failure
     */
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

    /**
     * Change the XsdEntity's status to DELETED.
     */
    public void markAsDeleted()
    {
        this.setStatus(ECoalesceDataObjectStatus.DELETED);
    }

    /**
     * Creates a CoalesceEntitySyncShell based off of this XsdEntity.
     * 
     * @return CoalesceEntitySyncShell, newly created based on this XsdEntity
     * 
     * @throws SAXException
     * @throws IOException
     */
    public CoalesceEntitySyncShell getSyncEntity() throws SAXException, IOException
    {
        return CoalesceEntitySyncShell.create(this);
    }

    /**
     * Sets the Elements and attribute values of XsdEntity myEntity to the Elements and attribute values of XsdEntity
     * syncEntity when the syncEntity's LastModified values are more recent.
     * 
     * @param myEntity first of two XsdEntities to be merged into a new XsdEntity
     * @param syncEntity second of two XsdEntities to be merged into a new XsdEntity
     * @return XsdEntity result of the merged XsdEntities
     * 
     * @throws CoalesceException
     */
    public static CoalesceEntity mergeSyncEntity(CoalesceEntity myEntity, CoalesceEntity syncEntity) throws CoalesceException
    {
        try
        {
            DateTime myLastModified = myEntity.getLastModified();
            DateTime syncLastModified = syncEntity.getLastModified();

            CoalesceEntity entity1 = null;
            CoalesceEntity entity2 = null;

            // Figure out which order
            switch (myLastModified.compareTo(syncLastModified)) {
            case -1:
                entity1 = myEntity;
                entity2 = syncEntity;
                break;
            default:
                entity2 = myEntity;
                entity1 = syncEntity;
            }

            // Check if

            resolveConflicts(entity1, entity2);

            // Convert xsdEntity objects to Xml Elements
            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom2.Document entity2Doc = saxBuilder.build(new InputSource(new StringReader(entity2.toXml())));
            org.jdom2.Document entity1Doc = saxBuilder.build(new InputSource(new StringReader(entity1.toXml())));

            mergeSyncEntityXml(entity1Doc.getRootElement(), entity2Doc.getRootElement());

            // Convert back to entity object
            XMLOutputter xmlOutPutter = new XMLOutputter();
            String output = xmlOutPutter.outputString(entity1Doc);
            return CoalesceEntity.create(output);

        }
        catch (JDOMException | IOException e)
        {
            throw new CoalesceException("mergeSyncEntity", e);
        }
    }

    private static void resolveConflicts(CoalesceDataObject Entity1, CoalesceDataObject Entity2)
    {

        if (Entity1 instanceof CoalesceField<?>)
        {
            // do we have matching keys?
            if (Entity1.getKey().equals(Entity2.getKey()))
            {
                // check for conflicts
                resolveFieldConflicts((CoalesceField<?>) Entity1, (CoalesceField<?>) Entity2);
            }
        }
        else
        {
            // no matching keys, get children and recall function
            Map<String, CoalesceDataObject> Entity1Children = Entity1.getChildDataObjects();
            Map<String, CoalesceDataObject> Entity2Children = Entity2.getChildDataObjects();
            for (Map.Entry<String, CoalesceDataObject> Entity1Child : Entity1Children.entrySet())
            {
                for (Map.Entry<String, CoalesceDataObject> Entity2Child : Entity2Children.entrySet())
                {
                    if (Entity1Child != null && Entity2Child != null)
                    {
                        resolveConflicts(Entity1Child.getValue(), Entity2Child.getValue());
                    }
                }
            }
        }

    }

    private static void resolveFieldConflicts(CoalesceField<?> field1, CoalesceField<?> field2)
    {

        // Call SetChanged to determine if field history needs to be created
        String field1Value = "";
        String field2Value = "";

        // save LastModified times
        DateTime field1LastModified = field1.getLastModified();
        DateTime field2LastModified = field2.getLastModified();

        // switch (field1LastModified.compareTo(field2LastModified)) {
        // case -1:
        // if (field1.getValue() != null)
        // {
        // field1Value = field1.getValue();
        // }
        //
        // if (field2.getValue() != null)
        // {
        // field2Value = field2.getValue();
        // }
        //
        // if (!field1Value.equals(field2Value) && !field1.getHistory().isEmpty())
        // {
        // // TODO: delete duplicate history?
        // }
        // // TODO: can't pass in null as arg?
        // // call setChanged to create field history if values are different
        // field1.setChanged(field1Value, field2Value);
        // break;
        // default:
        // if (field1.getValue() != null)
        // {
        // field1Value = field1.getValue();
        // }
        //
        // if (field2.getValue() != null)
        // {
        // field2Value = field2.getValue();
        // }
        //
        // if (!field2Value.equals(field1Value) && !field2.getHistory().isEmpty())
        // {
        // // TODO: delete duplicate history?
        // }
        // // TODO: can't pass in null as arg?
        // // call setChanged to create field history if values are different
        // field2.setChanged(field2Value, field1Value);
        //
        // }

        if (field1.getBaseValue() != null)
        {
            field1Value = field1.getBaseValue();
        }

        if (field2.getBaseValue() != null)
        {
            field2Value = field2.getBaseValue();
        }

        if (!field1Value.equals(field2Value) && !field1.getHistory().isEmpty())
        {
            // TODO: delete duplicate history?
        }
        // TODO: can't pass in null as arg?
        // call setChanged to create field history if values are different
        field1.setChanged(field1Value, field2Value);

        // revert back to previous LastModified times
        field1.setLastModified(field1LastModified);
        field2.setLastModified(field2LastModified);
    }

    private static void mergeSyncEntityXml(Element myEntity, Element syncEntity)
    {
        // Get Attributes
        List<Attribute> myEntityDocAttributes = myEntity.getAttributes();
        List<Attribute> syncEntityDocAttributes = syncEntity.getAttributes();

        // Get Time Stamps
        DateTime myLastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(myEntityDocAttributes.get(2).getValue());
        DateTime updateLastModified = JodaDateTimeHelper.fromXmlDateTimeUTC(syncEntityDocAttributes.get(2).getValue());

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
                if (attributeReplaced == false)
                {
                    myEntityDocAttributes.add(syncAttribute.detach());
                }
            }
        }

        // Merge Attributes
        for (Attribute syncAttribute : syncEntityDocAttributes)
        {
            // Overwrite myAttribute with syncAttribute
            boolean attributeFound = false;
            for (Attribute myAttribute : myEntityDocAttributes)
            {
                if (myAttribute.getName().equals(syncAttribute.getName()))
                {
                    attributeFound = true;
                }
            }

            // Add syncAttribute if it is not there
            if (attributeFound == false)
            {
                myEntityDocAttributes.add(syncAttribute.detach());
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
                mergeSyncEntityXml(myEntityElement, syncElement);
            }
        }
    }

    @Override
    public String toXml()
    {
        return toXml(false);
    }

    /**
     * Returns the UTF-16 (XML) String of the CoalesceEntityTemplate if the setSQLServer string parameter = "true". If
     * setSQLServer parameter = "false", returns the UTF-8 (XML) String of the CoalesceEntityTemplate.
     * 
     * @param setSQLServer String, if "true" indicates desired return is UTF-16 (XML) String; otherwise, UTF-8 (XML) String
     *            will result
     * @return (XML) String UTF-16 (XML) String or UTF-8 (XML) String depending on parameter
     */
    public String toXml(String setSQLServer)
    {
        if (setSQLServer.trim().toLowerCase() == "true")
            return toXml(true).replace("UTF-8", "UTF-16");
        else
            return toXml();
    }

    /**
     * Returns the UTF-8 (XML) String of the Entity and, when removeBinary is true, removes the binary values.
     * 
     * @param removeBinary boolean. If true, field values of binary and file will be removed from the entityXml string output
     * @return (XML) String of the entity in UTF-8, with or without the fields of binary/file based on the parameter
     */
    public String toXml(Boolean removeBinary)
    {

        String entityXml = XmlHelper.serialize(_entity, "UTF-8");

        if (removeBinary)
        {

            // Set a copy of the Xml without the Binary data in it.
            Document NoBinaryXmlDoc;
            try
            {
                NoBinaryXmlDoc = XmlHelper.loadXmlFrom(entityXml);

                // Get all Binary Field Nodes. Ensures that the 'binary' attribute value is handled in a case insensitive
                // way.
                clearFieldTypeValue("binary", NoBinaryXmlDoc);

                // Get all File Field Nodes. Ensures that the 'file' attribute value is handled in a case insensitive way.
                clearFieldTypeValue("file", NoBinaryXmlDoc);

                // Get Xml
                entityXml = XmlHelper.formatXml(NoBinaryXmlDoc);

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

                XmlHelper.setAttribute(xmlDoc, childNode, "value", "");
            }

        }
        catch (XPathExpressionException xee)
        {
            // Xpath failed. Do nothing
        }
    }

    private Map<String, CoalesceLinkage> getLinkages(List<ELinkTypes> forLinkTypes, String forEntityName, String forEntitySource)
    {
        Map<String, CoalesceLinkage> linkages = new HashMap<String, CoalesceLinkage>();

        // Get Linkage Section
        CoalesceLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null) return null;

        for (ICoalesceDataObject cdo : linkageSection.getChildDataObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage linkage = (CoalesceLinkage) cdo;
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
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entity.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entity.getLastmodified()));
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
            _entity.setDatecreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entity.setLastmodified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
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
