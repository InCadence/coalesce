package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * This class is the root element of the meta model.
 *
 * @author n78554
 */
public class CoalesceEntity extends CoalesceObjectHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceEntity.class);

    // ----------------------------------------------------------------------//
    // Private and protected Objects
    // ----------------------------------------------------------------------//

    /**
     * Attribute that stores the title of the entity.
     */
    public static final String ATTRIBUTE_TITLE = "title";
    /**
     * Attribute that stores the type of unique identifier.
     */
    public static final String ATTRIBUTE_ENTITYIDTYPE = "entityidtype";
    /**
     * Attribute that stores the unique identifier of the entity.
     */
    public static final String ATTRIBUTE_ENTITYID = "entityid";
    /**
     * Attribute that stores the version of the entity.
     */
    public static final String ATTRIBUTE_VERSION = "version";
    /**
     * Attribute that stores the source of the entity.
     */
    public static final String ATTRIBUTE_SOURCE = "source";
    /**
     * Attribute that stores the full name of the class that generated this entity.
     */
    public static final String ATTRIBUTE_CLASSNAME = "classname";
    /**
     * The time the object was last uploaded to the server.
     */
    public static final String ATTRIBUTE_UPLOADEDTOSERVER = "uploadedtoserver";

    private Entity _entity;

    // ----------------------------------------------------------------------//
    // Factory and Initialization
    // ----------------------------------------------------------------------//

    /**
     * Creates a {@link CoalesceEntity} based off of an (XML) String.
     *
     * @param entityXml (XML) String that the {@link CoalesceEntity} is to be
     *                  created from
     * @return {@link CoalesceEntity} resulting from entityXml String parameter,
     * null if failed
     */
    public static CoalesceEntity create(String entityXml)
    {

        // Create Entity
        CoalesceEntity entity = new CoalesceEntity();

        boolean passed = entity.initialize(entityXml);

        if (!passed)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("(FAILED) Xml: {}", entityXml);
            }

            return null;
        }

        return entity;

    }

    /**
     * Creates an {@link CoalesceEntity} based off of an (XML) String and sets
     * the title.
     *
     * @param entityXml (XML) String that the {@link CoalesceEntity} is to be
     *                  created from.
     * @param title     String that could be a a field namepath.
     * @return {@link CoalesceEntity} resulting from entityXml String parameter,
     * null if failed
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
     * Creates a new {@link CoalesceEntity} of the name, source and version.
     *
     * @param name    String identifying the {@link CoalesceEntity} type to create
     * @param source  String identifying the {@link CoalesceEntity} source
     * @param version String identifying the {@link CoalesceEntity} version
     * @return {@link CoalesceEntity} resulting from entityXml String parameter,
     * null if failed
     */
    public static CoalesceEntity create(String name, String source, String version)
    {
        return CoalesceEntity.create(name, source, version, null, null, null);
    }

    /**
     * Creates a new {@link CoalesceEntity} of the name, source and version
     * specified for the entityId and entityIdType specified.
     *
     * @param name         String identifying the {@link CoalesceEntity} type to create
     * @param source       String identifying the {@link CoalesceEntity} source
     * @param version      String identifying the {@link CoalesceEntity} version
     * @param entityId     String of the entity id, could be a guid, tcn, bag-tag id
     *                     or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn,
     *                     bag-tag id or other value)
     * @return {@link CoalesceEntity} resulting from entityXml String parameter,
     * null if failed
     */
    public static CoalesceEntity create(String name, String source, String version, String entityId, String entityIdType)
    {
        return CoalesceEntity.create(name, source, version, entityId, entityIdType, null);
    }

    /**
     * Creates a new {@link CoalesceEntity} of the name, source and version
     * specified for the entityId and entityIdType specified. Also sets the
     * title.
     *
     * @param name         String identifying the {@link CoalesceEntity} type to create
     * @param source       String identifying the {@link CoalesceEntity} source
     * @param version      String identifying the {@link CoalesceEntity} version
     * @param entityId     String of the entity id, could be a guid, tcn, bag-tag id
     *                     or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn,
     *                     bag-tag id or other value)
     * @param title        String that could be a a field namepath.
     * @return {@link CoalesceEntity} resulting from entityXml String parameter,
     * null if failed
     */
    public static CoalesceEntity create(String name,
                                        String source,
                                        String version,
                                        String entityId,
                                        String entityIdType,
                                        String title)
    {
        CoalesceEntity entity = new CoalesceEntity();
        if (entity.initializeEntity(name, source, version, entityId, entityIdType, title))
        {
            entity.initializeReferences();
        }
        return entity;
    }

    /**
     * Initializes a previously new {@link CoalesceEntity} by initializing
     * skeletal child objects.
     *
     * @return boolean indicator of success/failure
     */
    public boolean initialize()
    {
        if (this.isInitialized())
        {
            return true;
        }

        _entity = new Entity();

        return super.initialize(_entity) && initializeChildren();

    }

    /**
     * Initializes core settings.
     *
     * @param name         String identifying the {@link CoalesceEntity} type to create
     * @param source       String identifying the {@link CoalesceEntity} source
     * @param version      String identifying the {@link CoalesceEntity} version
     * @param entityId     String of the entity id, could be a guid, tcn, bag-tag id
     *                     or other value
     * @param entityIdType String identifying the entity id's type (guid, tcn,
     *                     bag-tag id or other value)
     * @param title        String that could be a a field namepath.
     */
    protected boolean initializeEntity(String name,
                                       String source,
                                       String version,
                                       String entityId,
                                       String entityIdType,
                                       String title)
    {
        if (this.isInitialized())
            return true;

        _entity = new Entity();

        if (!super.initialize(_entity))
            return false;

        if (!initializeChildren())
            return false;

        this.setName(name);
        this.setSource(source);
        this.setVersion(version);
        this.setEntityId(entityId);
        this.setEntityIdType(entityIdType);
        this.setTitle(title);

        this.setAttribute(ATTRIBUTE_CLASSNAME, this.getClass().getName());

        return true;
    }

    /**
     * Initializes a previously new {@link CoalesceEntity} based off of an (XML)
     * String.
     *
     * @param entityXml (XML) String that the {@link CoalesceEntity} is to be
     *                  initialized from.
     * @return boolean indicator of success/failure
     */
    public boolean initialize(String entityXml)
    {
        if (this.isInitialized())
            return true;

        if (entityXml == null || StringHelper.isNullOrEmpty(entityXml.trim()))
        {
            return initialize();
        }
        else
        {
            Object deserializedObject = XmlHelper.deserialize(entityXml, Entity.class);

            if (deserializedObject == null || !(deserializedObject instanceof Entity))
            {
                LOGGER.error("Failed to parse XML");
                return false;
            }
            _entity = (Entity) deserializedObject;

            if (!super.initialize(_entity))
            {
                LOGGER.error("Failed to intialize");

                return false;
            }

            if (!initializeChildren())
            {
                LOGGER.error("Failed to initialize children");

                return false;
            }

            return initializeReferences();
        }
    }

    /**
     * Initializes from an existing entity.
     *
     * @param entity {@link CoalesceEntity} to duplicate.
     * @return boolean indicator of success/failure
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
     * @return whether this object has been initialized or not.
     */
    @JsonIgnore
    public boolean isInitialized()
    {
        return _entity != null;
    }

    /**
     * Initializes a previously new {@link CoalesceEntity} by initializing
     * skeletal child objects.
     *
     * @return boolean indicator of success/failure
     */
    protected boolean initializeChildren()
    {
        CoalesceLinkageSection linkageSection = new CoalesceLinkageSection();

        if (!linkageSection.initialize(this))
            return false;

        addChildCoalesceObject(linkageSection);

        for (Section entitySection : _entity.getSection())
        {
            CoalesceSection section = new CoalesceSection();

            if (!section.initialize(this, entitySection))
                return false;

            addChildCoalesceObject(section);

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

    public final void setUploadedToServer(DateTime value)
    {
        if (value != null)
        {
            // Set Uploaded to Server
            _entity.setUploadedtoserver(value);
        }
    }

    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * UploadedToServer attribute.
     *
     * @return DateTime of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's UploadedToServer attribute.
     */
    public DateTime getUploadedToServer()
    {
        return _entity.getUploadedtoserver();
    }

    /**
     * Returns the {@link CoalesceEntity}'s source attribute value.
     *
     * @return String, source attribute value
     */
    public String getSource()
    {
        return getStringElement(_entity.getSource());
    }

    /**
     * Sets the {@link CoalesceEntity}'s source attribute value.
     *
     * @param value String, new value for the source attribute
     */
    public void setSource(String value)
    {
        _entity.setSource(value);
    }

    /**
     * Returns the {@link CoalesceEntity}'s version attribute value.
     *
     * @return String, version attribute value
     */
    public String getVersion()
    {
        return getStringElement(_entity.getVersion());
    }

    /**
     * Sets the {@link CoalesceEntity}'s version attribute value.
     *
     * @param value String, new value for the version attribute
     */
    public void setVersion(String value)
    {
        _entity.setVersion(value);
    }

    /**
     * @return the {@link #getModifiedBy()} of the first history entry if there is history otherwise of the entity.
     */
    public final String getCreatedBy()
    {
        List<History> history = _entity.getHistory();
        return history.isEmpty() ? _entity.getModifiedby() : history.get(history.size() - 1).getModifiedby();
    }

    /**
     * Returns the {@link CoalesceEntity}'s EntityId attribute value.
     *
     * @return String, EntityId attribute value
     */
    public String getEntityId()
    {
        return getStringElement(_entity.getEntityid());
    }

    /**
     * Sets the {@link CoalesceEntity}'s EntityId attribute value.
     *
     * @param value String, new value for the EntityId attribute
     */
    public void setEntityId(String value)
    {
        _entity.setEntityid(value);
    }

    /**
     * Returns the {@link CoalesceEntity}'s EntityIdType attribute value.
     *
     * @return String, EntityIdType attribute value
     */
    public String getEntityIdType()
    {
        return getStringElement(_entity.getEntityidtype());
    }

    /* TODO Resolve the default NEW status unit test failures
    @Override
    public ECoalesceObjectStatus getStatus()
    {

        ECoalesceObjectStatus status = _entity.getStatus();

        if (status == null)
        {
            status = ECoalesceObjectStatus.NEW;
        }

        return status;

    }
    //*/

    /**
     * Sets the {@link CoalesceEntity}'s EntityIdType attribute value.
     *
     * @param value String, new value for the EntityIdType attribute
     */
    public void setEntityIdType(String value)
    {
        _entity.setEntityidtype(value);
    }

    /**
     * Returns the {@link CoalesceEntity}'s title attribute value.
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

                CoalesceObject coalesceObject = getCoalesceObjectForNamePath(path);

                if (coalesceObject != null)
                {
                    if (coalesceObject instanceof CoalesceField<?>)

                    {
                        CoalesceField<?> field = (CoalesceField<?>) coalesceObject;

                        if (pathTitle == null)
                            pathTitle = "";
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
                return getName();
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
                return getName();
            }
            else
            {
                return pathTitle;
            }
        }

    }

    /**
     * Sets the {@link CoalesceEntity}'s title attribute value.
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
            setLastModified(JodaDateTimeHelper.nowInUtc());
        }

    }

    /**
     * @return the fully qualified class name.
     */
    public String getClassName()
    {
        return this.getAttribute(ATTRIBUTE_CLASSNAME);
    }

    /**
     * Returns the {@link CoalesceEntity}'s linkages from the
     * {@link CoalesceEntity}'s linkagesection.
     *
     * @return Map&lt;String, CoalesceLinkage&gt; CoalesceLinkages of relationships to
     * this CoalesceEntity
     */
    @JsonIgnore
    public Map<String, CoalesceLinkage> getLinkages()
    {
        return getLinkages((String) null);
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates a CoalesceEntityTemplate based off of this {@link CoalesceEntity}
     * .
     *
     * @return CoalesceEntityTemplate generated from this {@link CoalesceEntity}
     * @throws CoalesceException
     */
    public CoalesceEntityTemplate createNewEntityTemplate() throws CoalesceException
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
     * Creates an {@link CoalesceSection} for this {@link CoalesceEntity}.
     *
     * @param name    String, the namepath of the section.
     * @param noIndex boolean
     * @return {@link CoalesceSection} , newly created and now belonging to this
     * {@link CoalesceEntity}
     */
    public CoalesceSection createSection(String name, boolean noIndex)
    {
        return CoalesceSection.create(this, name, noIndex);
    }

    /**
     * Creates an {@link CoalesceSection} for this {@link CoalesceEntity}.
     *
     * @param name String, the namepath of the section.
     * @return {@link CoalesceSection} , newly created and now belonging to this
     * {@link CoalesceEntity}
     */
    public CoalesceSection createSection(String name)
    {
        return CoalesceSection.create(this, name);
    }

    /**
     * Used to locate a record instance for a given record set. If not found its
     * created.
     *
     * @param clazz
     * @param names
     * @return Coalesce Record
     */
    public <T extends CoalesceRecord> T createSingleton(Class<T> clazz, String... names)
    {

        T result = null;

        // Get Record Set
        CoalesceRecordset recordSet = getCoalesceRecordsetForNamePath(names);

        // Found?
        if (recordSet != null)
        {

            // Yes; Get Record
            CoalesceRecord record = (CoalesceRecord) recordSet.getCoalesceObjectForNamePath(names[names.length - 1],
                                                                                            names[names.length - 1]
                                                                                                    + " Record");

            // Found?
            if (record == null)
            {
                // No; Create
                record = recordSet.addNew();
            }

            try
            {
                result = clazz.getConstructor(CoalesceRecord.class).newInstance(record);
            }
            catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e)
            {
                LOGGER.error("Failed to create record", e);
            }
        }

        return result;
    }

    /**
     * Returns this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}'s
     * sections.
     *
     * @return Map&lt;String, CoalesceSection&gt; sections belonging to this
     * CoalesceEntity
     */
    @JsonIgnore
    public Map<String, CoalesceSection> getSections()
    {
        return getObjectsAsMap(CoalesceSection.class);
    }

    /**
     * Returns this {@link CoalesceEntity}'s {@link CoalesceSection}s.
     *
     * @return a list of sections in order that they appear within this entity.
     */
    public List<CoalesceSection> getSectionsAsList()
    {
        return getObjectsAsList(CoalesceSection.class);
    }

    /**
     * Returns this {@link CoalesceEntity}'s {@link CoalesceLinkageSection} .
     *
     * @return {@link CoalesceLinkageSection} belonging to this
     * {@link CoalesceEntity}
     */
    public CoalesceLinkageSection getLinkageSection()
    {

        for (CoalesceObject child : getChildCoalesceObjects().values())
        {
            if (child instanceof CoalesceLinkageSection)
            {
                return (CoalesceLinkageSection) child;
            }
        }

        return null;

    }

    /**
     * Returns the {@link CoalesceEntity}'s linkages, from the
     * {@link CoalesceEntity}'s linkagesection, for the EntityName specified.
     * Returns all linkages when the forEntityName parameter is null.
     *
     * @param forEntityName String of the Entity Name to return linkages for
     * @return Map&lt;String, CoalesceLinkage&gt; linkages with matches for the Entity
     * Name parameter
     */
    public Map<String, CoalesceLinkage> getLinkages(String forEntityName)
    {
        Map<String, CoalesceLinkage> linkages = new HashMap<>();

        // Get Linkage Section
        CoalesceLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null)
            return null;

        for (ICoalesceObject cdo : linkageSection.getChildCoalesceObjects().values())
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
     * Returns the {@link CoalesceEntity}'s linkages, from the
     * {@link CoalesceEntity}'s linkagesection, based on LinkType.
     *
     * @param forLinkType
     * @return Map&lt;String, CoalesceLinkage&gt; linkages with matches for the
     * ELinkTypes parameter
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType)
    {
        return getLinkages(forLinkType, null);
    }

    /**
     * @param forLinkType
     * @return the linkage for the specified type.
     * @throws CoalesceException if more then one linkage of the specified type
     *                           exists.
     */
    public CoalesceLinkage getLinkage(ELinkTypes forLinkType) throws CoalesceException
    {
        return getLinkage(forLinkType, null);
    }

    /**
     * Returns the {@link CoalesceEntity}'s linkages, from the
     * {@link CoalesceEntity}'s linkagesection, based on LinkType and EntityName
     * specified.
     *
     * @param forLinkType   ELinkTypes (one link type), the type of relationship
     *                      link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching
     *                      linkages for
     * @return Map&lt;String, CoalesceLinkage&gt; linkages with matches for the Entity
     * Name and ELinkType parameters
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName)
    {
        return getLinkages(forLinkType, forEntityName, null);
    }

    /**
     * @param forLinkType
     * @param forEntityName
     * @return the linkage for the specified type.
     * @throws CoalesceException if more then one linkage of the specified type
     *                           exists.
     */
    public CoalesceLinkage getLinkage(ELinkTypes forLinkType, String forEntityName) throws CoalesceException
    {
        return getLinkage(forLinkType, forEntityName, null);
    }

    /**
     * Returns the {@link CoalesceEntity}'s linkages, from the
     * {@link CoalesceEntity}'s linkagesection, based on LinkType EntityName and
     * Entity Source specified.
     *
     * @param forLinkType     ELinkTypes, the type of relationship link to find
     *                        matching linkages for
     * @param forEntityName   String, the Entity name attribute to find matching
     *                        linkages for
     * @param forEntitySource String, the Entity source attribute to find
     *                        matching linkages for
     * @return Map&lt;String, CoalesceLinkage&gt; linkages with matches for the
     * parameter criteria
     */
    public Map<String, CoalesceLinkage> getLinkages(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
    {
        return getLinkages(Collections.singletonList(forLinkType), forEntityName, forEntitySource);
    }

    /**
     * @param forLinkType
     * @param forEntityName
     * @param forEntitySource
     * @return the linkage for the specified type.
     * @throws CoalesceException if more then one linkage of the specified type
     *                           exists.
     */
    public CoalesceLinkage getLinkage(ELinkTypes forLinkType, String forEntityName, String forEntitySource)
            throws CoalesceException
    {
        Map<String, CoalesceLinkage> results = getLinkages(Collections.singletonList(forLinkType),
                                                           forEntityName,
                                                           forEntitySource);

        switch (results.size())
        {

        case 0:
            return null;
        case 1:
            return results.values().iterator().next();
        default:
            throw new CoalesceException("Multiple Links Found");
        }

    }

    /**
     * @param forLinkTypes  ELinkTypes (list of link types), the type of
     *                      relationship link to find matching linkages for
     * @param forEntityName String, the Entity name attribute to find matching
     *                      linkages for
     * @return the {@link CoalesceEntity} 's linkages, from the
     * {@link CoalesceEntity} 's linkagesection, based on a list of
     * LinkTypes and the EntityName specified.
     */
    public Map<String, CoalesceLinkage> getLinkages(List<ELinkTypes> forLinkTypes, String forEntityName)
    {
        return getLinkages(forLinkTypes, forEntityName, null);
    }

    /**
     * Returns the {@link CoalesceSection} specified by the namepath string.
     *
     * @param namePath String, namepath of the desired {@link CoalesceSection}
     * @return {@link CoalesceSection} having the matching namepath parameter.
     * Null if not found.
     */
    public CoalesceSection getSection(String namePath)
    {
        CoalesceObject coalesceObject = getCoalesceObjectForNamePath(namePath);

        if (coalesceObject != null && coalesceObject instanceof CoalesceSection)
        {
            return (CoalesceSection) coalesceObject;
        }

        return null;
    }

    /**
     * Returns the {@link CoalesceEntity}'s list of EntityIds specified by
     * EntityIdType String.
     *
     * @param typeParam EntityIdType String to retrieve entityIds for
     * @return List&lt;String&gt; list of entityIds that match the EntityIdType
     * typeParam
     */
    public List<String> getEntityId(String typeParam)
    {
        List<String> values = new ArrayList<>();

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
     * Sets the {@link CoalesceEntity}'s EntityId and EntityIdType attribute
     * values when values do not exist. Appends values when the attributes have
     * values.
     *
     * @param typeParam String EntityIdType value to append to the EntityIdType
     *                  attribute
     * @param value     String EntityId value to append to the EntityId attribute
     * @return boolean indicator of success/failure
     */
    public boolean setEntityId(String typeParam, String value)
    {
        if (typeParam == null)
            throw new IllegalArgumentException("typeParam");
        if (value == null)
            throw new IllegalArgumentException("value");
        if (typeParam.trim().equals(""))
            throw new IllegalArgumentException("typeParam cannot be empty");
        if (value.trim().equals(""))
            throw new IllegalArgumentException("value cannot be empty");

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
     * @param value
     * @return whether the version specified is valid
     */
    public boolean isValidObjectVersion(int value)
    {
        boolean valid = true;

        if (value > getObjectVersion() || value < 1)
        {
            // Version Out of Range
            valid = false;
        }
        else if (value == getObjectVersion())
        {
            // Version Deleted
            valid = !isObjectVersionDeleted();
        }
        else
        {
            // Check History
            for (CoalesceHistory history : getHistory())
            {
                if (history.getObjectVersion() == value)
                {
                    valid = !history.isObjectVersionDeleted();
                    break;
                }
            }
        }

        return valid;
    }

    /**
     * Increments the object's version.
     *
     * @param userId
     * @param ip
     */
    public final void incrementObjectVersion(String userId, String ip)
    {
        createHistory(userId, ip, getObjectVersion());

        setObjectVersion(getObjectVersion() + 1);
    }

    /**
     * Change the {@link CoalesceEntity}'s status to DELETED.
     */
    public void markAsDeleted()
    {
        this.setStatus(ECoalesceObjectStatus.DELETED);
    }

    /**
     * Creates a {@link CoalesceEntitySyncShell} based off of this
     * {@link CoalesceEntity}.
     *
     * @return {@link CoalesceEntitySyncShell} , newly created based on this
     * {@link CoalesceEntity}
     * @throws SAXException
     * @throws IOException
     */
    @JsonIgnore
    public CoalesceEntitySyncShell getSyncEntity() throws SAXException, IOException
    {
        return CoalesceEntitySyncShell.create(this);
    }

    /**
     * @param originalEntity
     * @param updatedEntity
     * @return {@link #mergeSyncEntity(CoalesceEntity, CoalesceEntity, String, String)}
     * with nulls being passed in as the userId and ip.
     * @throws CoalesceException
     */
    @Deprecated
    public static CoalesceEntity mergeSyncEntity(CoalesceEntity originalEntity, CoalesceEntity updatedEntity)
            throws CoalesceException
    {
        return mergeSyncEntity(originalEntity, updatedEntity, null, null);
    }

    /**
     * Sets the Elements and attribute values of {@link CoalesceEntity} myEntity
     * to the Elements and attribute values of {@link CoalesceEntity} syncEntity
     * when the syncEntity's LastModified values are more recent.
     *
     * @param originalEntity is the original entity.
     * @param updatedEntity  is that entity that should be merged into the
     *                       original.
     * @param userId         the ID of the user making the change.
     * @param ip             the IP of the user making the change.
     * @return {@link CoalesceEntity} result of the merged CoalesceEntities
     * @throws CoalesceException
     */
    public static CoalesceEntity mergeSyncEntity(CoalesceEntity originalEntity,
                                                 CoalesceEntity updatedEntity,
                                                 String userId,
                                                 String ip) throws CoalesceException
    {

        CoalesceIteratorMerge iterator = new CoalesceIteratorMerge();

        return iterator.merge(userId, ip, originalEntity, updatedEntity);

    }

    /**
     * Serializes the Coalesce Entity into XML without binary data using the
     * specified encoding.
     *
     * @param encoding the encoding format to use.
     * @return (XML) String of the entity without the binary data.
     */
    public String toXml(String encoding)
    {
        return toXml(false, encoding);
    }

    /**
     * Serializes the Coalesce Entity into UTF-8 XML and, when removeBinary is
     * true, removes the binary values.
     *
     * @param removeBinary boolean. If true, field values of binary and file
     *                     will be removed from the entityXml string output.
     * @return (XML) String of the entity in UTF-8, with or without the fields
     * of binary/file based on the parameter.
     */
    public String toXml(boolean removeBinary)
    {
        return toXml(removeBinary, "UTF-8");
    }

    /**
     * Serializes the Coalesce Entity into XML using the specified encoding and,
     * when removeBinary is true, removes the binary values.
     *
     * @param removeBinary boolean. If true, field values of binary and file
     *                     will be removed from the entityXml string output.
     * @param encoding     the encoding format to use.
     * @return (XML) String of the entity, with or without the fields of
     * binary/file based on the parameter.
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
                // Get all Binary Field Nodes. Ensures that the 'binary'
                // attribute value is handled in a case insensitive
                // way.
                clearFieldTypeValue("binary", noBinaryXmlDoc);

                // Get all File Field Nodes. Ensures that the 'file' attribute
                // value is handled in a case insensitive way.
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

        String expression =
                "//field[translate(@datatype,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + fieldType
                        + "']";

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(expression, xmlDoc.getDocumentElement(), XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node childNode = nodes.item(i);

            XmlHelper.setAttribute(xmlDoc, childNode, "value", "");
        }

    }

    private Map<String, CoalesceLinkage> getLinkages(List<ELinkTypes> forLinkTypes,
                                                     String forEntityName,
                                                     String forEntitySource)
    {
        Map<String, CoalesceLinkage> linkages = new HashMap<>();

        // Get Linkage Section
        CoalesceLinkageSection linkageSection = getLinkageSection();
        if (linkageSection == null)
            return null;

        for (ICoalesceObject cdo : linkageSection.getChildCoalesceObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {

                CoalesceLinkage linkage = (CoalesceLinkage) cdo;

                if ((forEntityName == null || linkage.getEntity2Name().equalsIgnoreCase(forEntityName))
                        && forLinkTypes.contains(linkage.getLinkType()) && (forEntitySource == null
                        || linkage.getEntity2Source().equalsIgnoreCase(forEntitySource)) && !linkage.isMarkedDeleted())
                {
                    linkages.put(linkage.getKey(), linkage);
                }
            }
        }

        return linkages;

    }

    /**
     * @return the Linkagesection that belong to this {@link CoalesceEntity}.
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
     * @return a list of {@link Section} that belong to this
     * {@link CoalesceEntity}.
     */
    protected List<Section> getEntitySections()
    {
        return _entity.getSection();
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entity.getHistory().remove(child);
        }
        else if (child instanceof Section)
        {
            isSuccessful = _entity.getSection().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        switch (name.toLowerCase())
        {
        case ATTRIBUTE_SOURCE:
            _entity.setSource(value);
            return true;
        case ATTRIBUTE_VERSION:
            _entity.setVersion(value);
            return true;
        case ATTRIBUTE_ENTITYID:
            _entity.setEntityid(value);
            return true;
        case ATTRIBUTE_ENTITYIDTYPE:
            setEntityIdType(value);
            return true;
        case ATTRIBUTE_TITLE:
            _entity.setTitle(value);
            return true;
        case ATTRIBUTE_UPLOADEDTOSERVER:
            setUploadedToServer(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        default:
            return setOtherAttribute(name, value);
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName(ATTRIBUTE_SOURCE), _entity.getSource());
        map.put(new QName(ATTRIBUTE_VERSION), _entity.getVersion());
        map.put(new QName(ATTRIBUTE_ENTITYID), _entity.getEntityid());
        map.put(new QName(ATTRIBUTE_ENTITYIDTYPE), _entity.getEntityidtype());
        map.put(new QName(ATTRIBUTE_TITLE), _entity.getTitle());
        map.put(new QName(ATTRIBUTE_UPLOADEDTOSERVER), JodaDateTimeHelper.toXmlDateTimeUTC(getUploadedToServer()));

        return map;
    }

}
