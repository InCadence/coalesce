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
import com.incadencecorp.coalesce.framework.generatedjaxb.Linkagesection;
import com.incadencecorp.coalesce.framework.generatedjaxb.Section;

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
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} based off of an (XML) String.
     * 
     * @param entityXml (XML) String that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} is to be
     *            created from
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} resulting from entityXml String
     *         parameter, null if failed
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} based off of an (XML) String and sets
     * the title.
     * 
     * @param entityXml (XML) String that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} is to be
     *            created from.
     * @param title String that could be a a field namepath.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} resulting from entityXml String
     *         parameter, null if failed
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
     * Creates a new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} of the name, source and version
     * specified for the entityId and entityIdType specified.
     * 
     * @param name String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} type to
     *            create
     * @param source String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} source
     * @param version String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} resulting from entityXml String
     *         parameter, null if failed
     */
    public static CoalesceEntity create(String name, String source, String version, String entityId, String entityIdType)
    {
        return CoalesceEntity.create(name, source, version, entityId, entityIdType, null);
    }

    /**
     * Creates a new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} of the name, source and version
     * specified for the entityId and entityIdType specified. Also sets the title.
     * 
     * @param name String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} type to
     *            create
     * @param source String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} source
     * @param version String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * @param title String that could be a a field namepath.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} resulting from entityXml String
     *         parameter, null if failed
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
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} by initializing
     * skeletal dataObjectChildren.
     * 
     * @return boolean indicator of success/failure
     */
    @Override
    public boolean initialize()
    {
        _entity = new Entity();

        if (!super.initialize()) return false;

        if (!initializeChildren()) return false;

        return true;
    }

    /**
     * Initializes core settings.
     * 
     * @param name String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} type to
     *            create
     * @param source String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} source
     * @param version String identifying the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} version
     * @param entityId String of the entity id, could be a guid, tcn, bag-tag id or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn, bag-tag id or other value)
     * @param title String that could be a a field namepath.
     */
    protected boolean initializeEntity(String name,
                                       String source,
                                       String version,
                                       String entityId,
                                       String entityIdType,
                                       String title)
    {
        _entity = new Entity();

        if (!super.initialize()) return false;

        if (!initializeChildren()) return false;

        this.setName(name);
        this.setSource(source);
        this.setVersion(version);
        this.setEntityId(entityId);
        this.setEntityIdType(entityIdType);
        this.setTitle(title);

        return true;
    }

    /**
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} based off of an
     * (XML) String.
     * 
     * @param entityXml (XML) String that the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} is to be
     *            initialized from.
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
     * Initializes from an existing {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param entity {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} to duplicate.
     * @return
     */
    public boolean initialize(CoalesceEntity entity)
    {
        super.initialize(entity);

        // Copy Member Variables
        _entity = entity._entity;

        // Initialize References
        return initializeReferences();
    }

    /**
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} by initializing
     * skeletal dataObjectChildren.
     * 
     * @return boolean indicator of success/failure
     */
    protected boolean initializeChildren()
    {
        CoalesceLinkageSection linkageSection = new CoalesceLinkageSection();

        if (!linkageSection.initialize(this)) return false;

        setChildDataObject(linkageSection.getKey(), linkageSection);

        for (Section entitySection : _entity.getSection())
        {
            CoalesceSection section = new CoalesceSection();

            if (!section.initialize(this, entitySection)) return false;

            setChildDataObject(section.getKey(), section);

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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s source attribute value.
     * 
     * @return String, source attribute value
     */
    public String getSource()
    {
        return getStringElement(_entity.getSource());
    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s source attribute value.
     * 
     * @param value String, new value for the source attribute
     */
    public void setSource(String value)
    {
        _entity.setSource(value);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s version attribute value.
     * 
     * @return String, version attribute value
     */
    public String getVersion()
    {
        return getStringElement(_entity.getVersion());
    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s version attribute value.
     * 
     * @param value String, new value for the version attribute
     */
    public void setVersion(String value)
    {
        _entity.setVersion(value);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s EntityId attribute value.
     * 
     * @return String, EntityId attribute value
     */
    public String getEntityId()
    {
        return getStringElement(_entity.getEntityid());
    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s EntityId attribute value.
     * 
     * @param value String, new value for the EntityId attribute
     */
    public void setEntityId(String value)
    {
        _entity.setEntityid(value);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s EntityIdType attribute value.
     * 
     * @return String, EntityIdType attribute value
     */
    public String getEntityIdType()
    {
        return getStringElement(_entity.getEntityidtype());
    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s EntityIdType attribute value.
     * 
     * @param value String, new value for the EntityIdType attribute
     */
    public void setEntityIdType(String value)
    {
        _entity.setEntityidtype(value);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s title attribute value.
     * 
     * @return String, title attribute value
     */
    public String getTitle()
    {
        String title = _entity.getTitle();

        String pathTitle = null;

        // Check if value contains an XPath
        if (title != null && title.contains("/"))
        {

            String[] paths = title.split(",");
            for (String path : paths)
            {

                CoalesceDataObject dataObject = getDataObjectForNamePath(path);

                if (dataObject != null)
                {
                    if (dataObject instanceof CoalesceField<?>)

                    {
                        CoalesceField<?> field = (CoalesceField<?>) dataObject;

                        if (pathTitle == null) pathTitle = "";
                        pathTitle += getStringElement(field.getBaseValue()) + ", ";
                    }
                }
            }

            pathTitle = StringUtils.strip(pathTitle, ", ");

        }

        // If not found
        if (pathTitle == null)
        {
            if (StringHelper.isNullOrEmpty(title))
            {
                return getSource();
            }
            else
            {
                return title;
            }
        }
        else
        {
            // If field not set
            if (StringHelper.isNullOrEmpty(pathTitle))
            {
                return getSource();
            }
            else
            {
                return pathTitle;
            }
        }

    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s title attribute value.
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection.
     * 
     * @return Map<String, CoalesceLinkage> CoalesceLinkages of relationships to this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     */
    public Map<String, CoalesceLinkage> getLinkages()
    {
        return getLinkages((String) null);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates a CoalesceEntityTemplate based off of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @return CoalesceEntityTemplate generated from this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
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
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} for this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param name String, the namepath of the section.
     * @param noIndex boolean
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}, newly created and now belonging to
     *         this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     */
    public CoalesceSection createSection(String name, boolean noIndex)
    {
        return CoalesceSection.create(this, name, noIndex);
    }

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} for this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param name String, the namepath of the section.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}, newly created and now belonging to
     *         this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     */
    public CoalesceSection createSection(String name)
    {
        return CoalesceSection.create(this, name);
    }

    /**
     * Returns this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s sections.
     * 
     * @return Map<String, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}> sections belonging to this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     */
    public Map<String, CoalesceSection> getSections()
    {

        Map<String, CoalesceSection> sections = new HashMap<String, CoalesceSection>();

        for (CoalesceDataObject child : getChildDataObjects().values())
        {
            if (child instanceof CoalesceSection)
            {
                sections.put(child.getKey(), (CoalesceSection) child);
            }
        }

        return sections;

    }

    /**
     * Returns this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection} belonging to this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     */
    public CoalesceLinkageSection getLinkageSection()
    {

        for (CoalesceDataObject child : getChildDataObjects().values())
        {
            if (child instanceof CoalesceLinkageSection)
            {
                return (CoalesceLinkageSection) child;
            }
        }

        return null;

    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages, from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection, for the EntityName specified.
     * Returns all linkages when the forEntityName parameter is null.
     * 
     * @param forEntityName String of the Entity Name to return linkages for
     * @return Map<String, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}> linkages with matches for
     *         the Entity Name parameter
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages, from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection, based on LinkType.
     * 
     * @return Map<String, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}> linkages with matches for
     *         the ELinkTypes parameter
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType)
    {
        return getLinkages(forLinkType, null);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages, from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection, based on LinkType and
     * EntityName specified.
     * 
     * @param forLinkType ELinkTypes (one link type), the type of relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching linkages for
     * @return Map<String, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}> linkages with matches for
     *         the Entity Name and ELinkType parameters
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName)
    {
        return getLinkages(forLinkType, forEntityName, null);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages, from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection, based on LinkType EntityName
     * and Entity Source specified.
     * 
     * @param forLinkType ELinkTypes, the type of relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching linkages for
     * @param forEntitySource String, the Entity source attribute to find matching linkages for
     * @return Map<String, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}> linkages with matches for
     *         the parameter criteria
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
    {
        return getLinkages(Arrays.asList(forLinkType), forEntityName, forEntitySource);
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkages, from the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s linkagesection, based on a list of LinkTypes
     * and the EntityName specified.
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} specified by namepath string.
     * 
     * @param namePath String, namepath of the desired {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} having the matching namepath parameter.
     *         Null if not found.
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s list of EntityIds specified by
     * EntityIdType String.
     * 
     * @param typeParam, EntityIdType String to retrieve entityIds for
     * @return List<String> list of entityIds that match the EntityIdType typeParam
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
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s EntityId and EntityIdType attribute
     * values when values do not exist. Appends values when the attributes have values.
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
        if (typeParam.trim().equals("")) throw new IllegalArgumentException("typeParam cannot be empty");
        if (value.trim().equals("")) throw new IllegalArgumentException("value cannot be empty");

        // Collection Already have Unique ID?
        if (StringHelper.isNullOrEmpty(getEntityId()))
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
     * Change the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s status to DELETED.
     */
    public void markAsDeleted()
    {
        this.setStatus(ECoalesceDataObjectStatus.DELETED);
    }

    /**
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell} based off of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell}, newly created based on this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * 
     * @throws SAXException
     * @throws IOException
     */
    public CoalesceEntitySyncShell getSyncEntity() throws SAXException, IOException
    {
        return CoalesceEntitySyncShell.create(this);
    }

    /**
     * Sets the Elements and attribute values of {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * myEntity to the Elements and attribute values of {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * syncEntity when the syncEntity's LastModified values are more recent.
     * 
     * @param myEntity first of two CoalesceEntities to be merged into a new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * @param syncEntity second of two CoalesceEntities to be merged into a new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} result of the merged CoalesceEntities
     * 
     * @throws CoalesceException
     */
    public static CoalesceEntity mergeSyncEntity(CoalesceEntity myEntity, CoalesceEntity syncEntity)
            throws CoalesceException
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

            // Convert CoalesceEntity objects to Xml Elements
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

    private static void resolveConflicts(CoalesceDataObject entity1, CoalesceDataObject entity2)
    {

        if (entity1 instanceof CoalesceField<?>)
        {
            // do we have matching keys?
            if (entity1.getKey().equals(entity2.getKey()))
            {
                // check for conflicts
                resolveFieldConflicts((CoalesceField<?>) entity1, (CoalesceField<?>) entity2);
            }
        }
        else
        {
            // no matching keys, get children and recall function
            Map<String, CoalesceDataObject> entity1Children = entity1.getChildDataObjects();
            Map<String, CoalesceDataObject> entity2Children = entity2.getChildDataObjects();
            for (Map.Entry<String, CoalesceDataObject> entity1Child : entity1Children.entrySet())
            {
                for (Map.Entry<String, CoalesceDataObject> entity2Child : entity2Children.entrySet())
                {
                    if (entity1Child != null && entity2Child != null)
                    {
                        resolveConflicts(entity1Child.getValue(), entity2Child.getValue());
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
        field1.createHistory(field1Value, field2Value);

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
                if (!attributeReplaced)
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
            if (!attributeFound)
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

    /**
     * Serializes the Coalesce Entity into XML without binary data using the specified encoding.
     * 
     * @param encoding the encoding format to use.
     * @return (XML) String of the entity without the binary data.
     */
    public String toXml(String encoding)
    {
        return toXml(false, encoding);
    }

    /**
     * Serializes the Coalesce Entity into UTF-8 XML without binary.
     * 
     * @return (XML) String of the entity in UTF-8 without the binary data.
     */
    @Override
    public String toXml()
    {
        return toXml(false, "UTF-8");
    }

    /**
     * Serializes the Coalesce Entity into UTF-8 XML and, when removeBinary is true, removes the binary values.
     * 
     * @param removeBinary boolean. If true, field values of binary and file will be removed from the entityXml string
     *            output.
     * @return (XML) String of the entity in UTF-8, with or without the fields of binary/file based on the parameter.
     */
    public String toXml(boolean removeBinary)
    {
        return toXml(removeBinary, "UTF-8");
    }

    public static final String UTF8_BOM = "\uFEFF";

    /**
     * Serializes the Coalesce Entity into XML using the specified encoding and, when removeBinary is true, removes the
     * binary values.
     * 
     * @param removeBinary boolean. If true, field values of binary and file will be removed from the entityXml string
     *            output.
     * @param encoding the encoding format to use.
     * @return (XML) String of the entity, with or without the fields of binary/file based on the parameter.
     */
    public String toXml(boolean removeBinary, String encoding)
    {

        String entityXml = XmlHelper.serialize(_entity, encoding);

        if (removeBinary)
        {

            // Set a copy of the Xml without the Binary data in it.
            Document noBinaryXmlDoc;

            try
            {
                noBinaryXmlDoc = XmlHelper.loadXmlFrom(entityXml);
            }
            catch (SAXException | IOException e)
            {
                // Failed to Load XML
                return null;
            }

            try
            {
                // Get all Binary Field Nodes. Ensures that the 'binary' attribute value is handled in a case insensitive
                // way.
                clearFieldTypeValue("binary", noBinaryXmlDoc);

                // Get all File Field Nodes. Ensures that the 'file' attribute value is handled in a case insensitive way.
                clearFieldTypeValue("file", noBinaryXmlDoc);
            }
            catch (XPathExpressionException e)
            {
                // Failed to Remove Binary
                return null;
            }

            // Get Xml
            entityXml = XmlHelper.formatXml(noBinaryXmlDoc);
        }

        return entityXml;

    }

    /*--------------------------------------------------------------------------
    Private and Protected Functions
    --------------------------------------------------------------------------*/

    private void clearFieldTypeValue(String fieldType, Document xmlDoc) throws XPathExpressionException
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

    private Map<String, CoalesceLinkage> getLinkages(List<ELinkTypes> forLinkTypes,
                                                     String forEntityName,
                                                     String forEntitySource)
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

    /**
     * Returns the  Linkagesection that belong to this {@link CoalesceEntity}.
     * @return
     *     possible object is
     *     {@link List<Section> }
     */
    protected Linkagesection getEntityLinkageSection()
    {
        Linkagesection linkageSection = _entity.getLinkagesection();

        if (linkageSection == null)
        {
            linkageSection = new Linkagesection();
            _entity.setLinkagesection(linkageSection);
        }

        return linkageSection;
    }

    /**
     * Returns a list of {@link Section} that belong to this {@link CoalesceEntity}.
     * @return
     *     possible object is
     *     {@link List<Section> }
     */
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
        switch (name.toLowerCase()) {
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
        case "entityidtype":
            setEntityIdType(value);
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
        return _entity.getOtherAttributes();
    }

}
