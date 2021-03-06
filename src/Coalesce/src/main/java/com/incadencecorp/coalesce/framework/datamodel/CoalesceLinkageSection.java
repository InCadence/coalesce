package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
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

public class CoalesceLinkageSection extends CoalesceObjectHistory {

    /**
     * Default name for linkage sections.
     */
    public static final String NAME = "Linkages";

    private Linkagesection _entityLinkageSection;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link CoalesceLinkageSection} and ties it to its parent
     * {@link CoalesceEntity}.
     *
     * @param parent {@link CoalesceEntity} that the new
     *               {@link CoalesceLinkageSection} will belong to.
     * @return {@link CoalesceLinkageSection} the new
     * {@link CoalesceLinkageSection} .
     */
    public static CoalesceLinkageSection create(CoalesceEntity parent)
    {
        return CoalesceLinkageSection.create(parent, true);
    }

    /**
     * Creates an {@link CoalesceLinkageSection} and ties it to its parent
     * {@link CoalesceEntity}. Also sets the noIndex attribute.
     *
     * @param parent  {@link CoalesceEntity} that the new
     *                {@link CoalesceLinkageSection} will belong to.
     * @param noIndex boolean value.
     * @return {@link CoalesceLinkageSection} the new
     * {@link CoalesceLinkageSection} .
     */
    public static CoalesceLinkageSection create(CoalesceEntity parent, boolean noIndex)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");

        if (parent.getLinkageSection() != null)
            return parent.getLinkageSection();

        CoalesceLinkageSection linkageSection = new CoalesceLinkageSection();
        if (!linkageSection.initialize(parent))
            return null;

        linkageSection.setNoIndex(noIndex);

        parent.addChildCoalesceObject(linkageSection);

        return linkageSection;

    }

    /**
     * Initializes a previously new {@link CoalesceLinkageSection} and ties it
     * to its parent {@link CoalesceEntity}.
     *
     * @param parent {@link CoalesceEntity} containing an LinkageSection to base
     *               this {@link CoalesceLinkageSection} on.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceEntity parent)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");

        // Set References
        setParent(parent);
        _entityLinkageSection = parent.getEntityLinkageSection();

        super.initialize(_entityLinkageSection);

        if (StringHelper.isNullOrEmpty(getName()))
            setName(NAME);

        if (_entityLinkageSection != null)
        {

            // Add Linkages to Child List
            for (Linkage childLinkage : _entityLinkageSection.getLinkage())
            {

                CoalesceLinkage newLinkage = new CoalesceLinkage();
                if (!newLinkage.initialize(this, childLinkage))
                    continue;

                if (!getChildCoalesceObjects().containsKey(newLinkage.getKey()))
                {
                    addChildCoalesceObject(newLinkage);
                }
            }

            return true;
        }
        else
        {
            return false;
        }

    }

    // -----------------------------------------------------------------------//
    // Public Methods
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
     * @param key
     * @param type
     * @return the linkage that matches the key and type otherwise null
     */
    public CoalesceLinkage getLinkage(String key, ELinkTypes type)
    {
        for (ICoalesceObject cdo : getChildCoalesceObjects().values())
        {
            if (cdo instanceof CoalesceLinkage)
            {
                CoalesceLinkage linkage = (CoalesceLinkage) cdo;

                if (linkage.getLinkType() == type && linkage.getEntity2Key().equalsIgnoreCase(key))
                {
                    return linkage;
                }
            }
        }

        return null;
    }

    /**
     * Returns a hashmap of the {@link CoalesceLinkage}s contained in the
     * {@link CoalesceLinkageSection} .
     *
     * @return HashMap of this {@link CoalesceLinkageSection} 's
     * {@link CoalesceLinkage} s.
     */
    @JsonIgnore
    public Map<String, CoalesceLinkage> getLinkages()
    {
        Map<String, CoalesceLinkage> linkages = new HashMap<>();

        for (CoalesceObject xdo : getChildCoalesceObjects().values())
        {
            if (xdo instanceof CoalesceLinkage)
            {
                linkages.put(xdo.getKey(), (CoalesceLinkage) xdo);
            }
        }

        return linkages;

    }

    @JsonView(Views.Entity.class)
    public List<CoalesceLinkage> getLinkagesAsList()
    {
        return getObjectsAsList(CoalesceLinkage.class);
    }

    /**
     * Creates an {@link CoalesceLinkage} child for this
     * {@link CoalesceLinkageSection} .
     *
     * @return {@link CoalesceLinkage} newly created and added to this
     * {@link CoalesceLinkageSection} .
     */
    public CoalesceLinkage createLinkage()
    {
        return CoalesceLinkage.create(this);
    }

    /**
     * Removes all linkages
     */
    public void clearLinkages()
    {
        List<String> linkageKeys = new ArrayList<>();

        for (Linkage linkage : _entityLinkageSection.getLinkage())
        {
            linkageKeys.add(linkage.getKey());
        }

        for (String key : linkageKeys)
        {
            pruneCoalesceObject(key);
        }
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    protected Linkagesection getEntityLinkageSection()
    {
        return _entityLinkageSection;
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entityLinkageSection.getHistory().remove(child);
        }
        else if (child instanceof Linkage)
        {
            isSuccessful = _entityLinkageSection.getLinkage().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

}
