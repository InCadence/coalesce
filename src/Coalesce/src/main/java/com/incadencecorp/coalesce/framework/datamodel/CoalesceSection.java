package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

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

public class CoalesceSection extends CoalesceObjectHistory {

    private Section _entitySection;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link CoalesceSection}, by name, and ties it to its parent
     * {@link CoalesceEntity}.
     *
     * @param parent {@link CoalesceEntity} that the new {@link CoalesceSection}
     *               will belong to.
     * @param name   String, the name/namepath to be assigned to the
     *               {@link CoalesceSection} .
     * @return {@link CoalesceSection} , the new {@link CoalesceSection} .
     */
    public static CoalesceSection create(CoalesceEntity parent, String name)
    {
        return CoalesceSection.create(parent, name, false);
    }

    /**
     * Creates an {@link CoalesceSection}, by name, and ties it to its parent
     * {@link CoalesceSection}.
     *
     * @param parent {@link CoalesceSection} that the new
     *               {@link CoalesceSection} will belong to.
     * @param name   String, the name/namepath to be assigned to the
     *               {@link CoalesceSection} .
     * @return {@link CoalesceSection} , the new {@link CoalesceSection} .
     */
    public static CoalesceSection create(CoalesceSection parent, String name)
    {
        return CoalesceSection.create(parent, name, false);
    }

    /**
     * Creates an {@link CoalesceSection}, by name, and ties it to its parent
     * {@link CoalesceEntity}. Also sets the noIndex attribute.
     *
     * @param parent  {@link CoalesceEntity} that the new {@link CoalesceSection}
     *                will belong to.
     * @param name    String, the name/namepath to be assigned to the
     *                {@link CoalesceSection} .
     * @param noIndex boolean value.
     * @return {@link CoalesceSection} , the new {@link CoalesceSection} .
     */
    public static CoalesceSection create(CoalesceEntity parent, String name, boolean noIndex)
    {

        if (parent == null)
            throw new NullArgumentException("parent");
        if (name == null)
            throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name.trim()))
            throw new IllegalArgumentException("name cannot be empty");

        // Check that a section with the same name doesn't already exist
        for (CoalesceSection section : parent.getSectionsAsList())
        {
            if (section.getName().equalsIgnoreCase(name))
            {

                section.setNoIndex(noIndex);
                return section;
            }
        }

        Section newEntitySection = new Section();
        parent.getEntitySections().add(newEntitySection);

        CoalesceSection newSection = new CoalesceSection();
        if (!newSection.initialize(parent, newEntitySection))
            return null;

        newSection.setName(name);

        newSection.setNoIndex(noIndex);

        parent.addChildCoalesceObject(newSection);

        return newSection;
    }

    /**
     * Creates an {@link CoalesceSection}, by name, and ties it to its parent
     * {@link CoalesceSection}. Also sets the noIndex attribute.
     *
     * @param parent  {@link CoalesceSection} that the new
     *                {@link CoalesceSection} will belong to.
     * @param name    String, the name/namepath to be assigned to the
     *                {@link CoalesceSection} .
     * @param noIndex boolean value.
     * @return {@link CoalesceSection} , the new {@link CoalesceSection} .
     */
    public static CoalesceSection create(CoalesceSection parent, String name, boolean noIndex)
    {

        if (parent == null)
            throw new NullArgumentException("parent");
        if (name == null)
            throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name.trim()))
            throw new IllegalArgumentException("name cannot be empty");

        // Check that a section with the same name doesn't already exist
        for (CoalesceSection section : parent.getSectionsAsList())
        {
            if (section.getName().equalsIgnoreCase(name))
            {
                section.setNoIndex(noIndex);
                return section;
            }
        }

        Section newEntitySection = new Section();
        parent.getSectionSections().add(newEntitySection);

        CoalesceSection newSection = new CoalesceSection();
        if (!newSection.initialize(parent, newEntitySection))
            return null;

        newSection.setName(name);

        newSection.setNoIndex(noIndex);

        parent.addChildCoalesceObject(newSection);

        return newSection;
    }

    /**
     * Class constructor. Creates a CoalesceSection class.
     */
    public CoalesceSection()
    {
        super();
    }

    /**
     * Class constructor. Creates a CoalesceSection class off of an existing
     * CoalesceSection.
     *
     * @param section
     */
    public CoalesceSection(CoalesceSection section)
    {
        super(section);

        // Copy Member Variables
        _entitySection = section._entitySection;

    }

    /**
     * Initializes this {@link CoalesceSection} based on a Section and ties it
     * to its parent {@link CoalesceEntity}.
     *
     * @param parent  {@link CoalesceEntity} that the new {@link CoalesceSection}
     *                will belong to.
     * @param section that the new {@link CoalesceSection} will be based off of.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceEntity parent, Section section)
    {
        return initialize((CoalesceObject) parent, section);
    }

    /**
     * Initializes this {@link CoalesceSection} based on a Section and ties it
     * to its parent {@link CoalesceSection}.
     *
     * @param parent  {@link CoalesceSection} that the new
     *                {@link CoalesceSection} will belong to.
     * @param section that the new {@link CoalesceSection} will be based off of.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceSection parent, Section section)
    {
        return initialize((CoalesceObject) parent, section);
    }

    private boolean initialize(CoalesceObject parent, Section section)
    {

        if (parent == null)
            throw new NullArgumentException("parent");
        if (section == null)
            throw new NullArgumentException("section");

        // Set References
        setParent(parent);
        _entitySection = section;

        super.initialize(_entitySection);

        for (Recordset childRecordSet : _entitySection.getRecordset())
        {

            CoalesceRecordset newRecordSet = new CoalesceRecordset();
            if (!newRecordSet.initialize(this, childRecordSet))
                continue;

            if (!getChildCoalesceObjects().containsKey(newRecordSet.getKey()))
            {
                addChildCoalesceObject(newRecordSet);
            }
        }

        // loop for child Sections if they exist
        for (Section subSection : _entitySection.getSection())
        {
            CoalesceSection newsection = new CoalesceSection();

            if (!newsection.initialize(this, subSection))
                return false;

            addChildCoalesceObject(newsection);

        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    /**
     * Modify the JSON annotation
     */
    @JsonView(Views.Entity.class)
    @Override
    public String getKey()
    {
        return super.getKey();
    }

    @JsonView(Views.Entity.class)
    @Override
    public DateTime getDateCreated()
    {
        return super.getDateCreated();
    }

    @JsonView(Views.Entity.class)
    @Override
    public DateTime getLastModified()
    {
        return super.getLastModified();
    }

    /**
     * Creates an {@link CoalesceRecordset} for this {@link CoalesceSection},
     * with the name specified.
     *
     * @param name of the new {@link CoalesceRecordset} .
     * @return {@link CoalesceRecordset} , the new {@link CoalesceRecordset} .
     */
    @JsonIgnore
    public CoalesceRecordset createRecordset(String name)
    {
        return CoalesceRecordset.create(this, name);
    }

    /**
     * Returns {@link CoalesceSection}'s {@link CoalesceRecordset} with the
     * matching String NamePath.
     *
     * @param namePath of the new {@link CoalesceRecordset} .
     * @return {@link CoalesceRecordset} , the {@link CoalesceRecordset} with
     * the name path. Null if the name path is not a
     * {@link CoalesceRecordset} or doesn't exist.
     */
    public CoalesceRecordset getRecordset(String namePath)
    {
        return getCoalesceRecordsetForNamePath(namePath);
    }

    /**
     * @return map of {@link CoalesceRecordset} s contained by this section.
     * @see #getRecordsetsAsList()
     * @since 0.0.7
     * @deprecated
     */
    @JsonIgnore
    public Map<String, CoalesceRecordset> getRecordsets()
    {
        return getObjectsAsMap(CoalesceRecordset.class, ECoalesceObjectStatus.DELETED);
    }

    /**
     * Returns this {@link CoalesceEntity}'s {@link CoalesceRecordset}s.
     *
     * @return a list of record sets in order that they appear within this
     * section.
     */
    public List<CoalesceRecordset> getRecordsetsAsList()
    {
        return getObjectsAsList(CoalesceRecordset.class, ECoalesceObjectStatus.DELETED);
    }

    /**
     * Creates an {@link CoalesceSection} for this {@link CoalesceSection}, with
     * the name specified.
     *
     * @param name of the new {@link CoalesceSection} .
     * @return {@link CoalesceSection} , the new {@link CoalesceSection} .
     */
    public CoalesceSection createSection(String name)
    {
        return CoalesceSection.create(this, name);
    }

    /**
     * Returns {@link CoalesceSection}'s {@link CoalesceSection} with the
     * matching String NamePath.
     *
     * @param namePath of the new {@link CoalesceSection} .
     * @return {@link CoalesceSection} , the {@link CoalesceSection} with the
     * name path. Null if the name path is not a {@link CoalesceSection}
     * or doesn't exist.
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
     * @return a map of {@link CoalesceSection} s contained by this section.
     * @see #getSectionsAsList()
     * @since 0.0.7
     * @deprecated
     */
    @JsonIgnore
    public Map<String, CoalesceSection> getSections()
    {
        return getObjectsAsMap(CoalesceSection.class, ECoalesceObjectStatus.DELETED);
    }

    /**
     * Returns this {@link CoalesceSection}'s nested {@link CoalesceSection}s.
     *
     * @return a list of sections in order that they appear within this section.
     */
    public List<CoalesceSection> getSectionsAsList()
    {
        return getObjectsAsList(CoalesceSection.class, ECoalesceObjectStatus.DELETED);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns a Recordset List for the Entity Section.
     *
     * @return List&lt;Recordset&gt; Section's Recordset list.
     */
    protected List<Recordset> getEntityRecordSets()
    {
        return _entitySection.getRecordset();
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entitySection.getHistory().remove(child);
        }
        else if (child instanceof Section)
        {
            isSuccessful = _entitySection.getSection().remove(child);
        }
        else if (child instanceof Recordset)
        {
            isSuccessful = _entitySection.getRecordset().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

    /**
     * @return a list of {@link Section} that belong to this {@link CoalesceSection}.
     */
    private List<Section> getSectionSections()
    {
        return _entitySection.getSection();
    }

}
