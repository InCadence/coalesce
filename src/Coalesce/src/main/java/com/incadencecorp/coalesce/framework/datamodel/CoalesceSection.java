package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

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
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * by name, and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param name String, the name/namepath to be assigned to the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
     */
    public static CoalesceSection create(CoalesceEntity parent, String name)
    {
        return CoalesceSection.create(parent, name, false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * by name, and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param name String, the name/namepath to be assigned to the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
     */
    public static CoalesceSection create(CoalesceSection parent, String name)
    {
        return CoalesceSection.create(parent, name, false);
    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * by name, and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * Also sets the noIndex attribute.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param name String, the name/namepath to be assigned to the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @param noIndex boolean value.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
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
        for (CoalesceSection section : parent.getSections().values())
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
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * by name, and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}.
     * Also sets the noIndex attribute.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param name String, the name/namepath to be assigned to the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @param noIndex boolean value.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
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
        for (CoalesceSection section : parent.getSections().values())
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
     * 
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
     * 
     */
    public CoalesceSection(CoalesceSection section)
    {
        super(section);

        // Copy Member Variables
        _entitySection = section._entitySection;

    }

    /**
     * Initializes this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * based on a Section and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param section that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will be based off of.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceEntity parent, Section section)
    {
        return initialize((CoalesceObject) parent, section);
    }

    /**
     * Initializes this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * based on a Section and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will belong to.
     * @param section that the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            will be based off of.
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
                addChildCoalesceObject(newRecordSet.getKey(), newRecordSet);
            }
        }

        // loop for child Sections if they exist
        for (Section subSection : _entitySection.getSection())
        {
            CoalesceSection newsection = new CoalesceSection();

            if (!newsection.initialize(this, subSection))
                return false;

            addChildCoalesceObject(newsection.getKey(), newsection);

        }

        return true;
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * for this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * with the name specified.
     * 
     * @param name of the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         .
     */
    public CoalesceRecordset createRecordset(String name)
    {
        return CoalesceRecordset.create(this, name);
    }

    /**
     * Returns
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * with the matching String NamePath.
     * 
     * @param namePath of the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         , the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         with the name path. Null if the name path is not a
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         or doesn't exist.
     */
    public CoalesceRecordset getRecordset(String namePath)
    {
        return getCoalesceRecordsetForNamePath(namePath);
    }

    /**
     * Returns a hashmap of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * s.
     * 
     * @return hashmap of
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     *         s contained by this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
     */
    public Map<String, CoalesceRecordset> getRecordsets()
    {

        Map<String, CoalesceRecordset> recordSets = new HashMap<String, CoalesceRecordset>();

        for (CoalesceObject child : getChildCoalesceObjects().values())
        {
            if (child instanceof CoalesceRecordset)
            {
                recordSets.put(child.getKey(), (CoalesceRecordset) child);
            }
        }

        return recordSets;

    }

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * for this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection},
     * with the name specified.
     * 
     * @param name of the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
     */
    public CoalesceSection createSection(String name)
    {
        return CoalesceSection.create(this, name);
    }

    /**
     * Returns
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * with the matching String NamePath.
     * 
     * @param namePath of the new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *            .
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         , the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         with the name path. Null if the name path is not a
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         or doesn't exist.
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
     * Returns a hashmap of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}s.
     * 
     * @return hashmap of
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         s contained by this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     *         .
     */
    public Map<String, CoalesceSection> getSections()
    {

        Map<String, CoalesceSection> sections = new HashMap<String, CoalesceSection>();

        for (CoalesceObject child : getChildCoalesceObjects().values())
        {
            if (child instanceof CoalesceSection)
            {
                sections.put(child.getKey(), (CoalesceSection) child);
            }
        }

        return sections;

    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns a Recordset List for the Entity Section.
     * 
     * @return List<Recordset> Section's Recordset list.
     */
    protected List<Recordset> getEntityRecordSets()
    {
        return _entitySection.getRecordset();
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false; 
        
        if (child instanceof History) {
            isSuccessful = _entitySection.getHistory().remove(child);
        } else if (child instanceof Section) {
            isSuccessful = _entitySection.getSection().remove(child);
        }else if (child instanceof Recordset) {
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
     * @return a list of {@link Section} that belong to this
     *         {@link CoalesceSection}.
     */
    protected List<Section> getSectionSections()
    {
        return _entitySection.getSection();
    }

}
