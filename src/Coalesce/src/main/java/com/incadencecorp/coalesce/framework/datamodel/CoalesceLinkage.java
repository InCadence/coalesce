package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.api.CoalesceAttributes;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.LocaleConverter;


import javax.xml.namespace.QName;
import java.util.Locale;
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
 * This class represents the association between two Coalesce Entities.
 *
 * @author Derek C.
 */
public class CoalesceLinkage extends CoalesceObjectHistory implements ICoalesceLinkage {

    /**
     * Key of the entity this entity is linked to.
     */
    public static final String ATTRIBUTE_ENTITY2KEY = "entity2key";

    /**
     * Name of the entity this entity is linked to.
     */
    public static final String ATTRIBUTE_ENTITY2NAME = "entity2name";

    /**
     * Source of the entity this entity is linked to.
     */
    public static final String ATTRIBUTE_ENTITY2SOURCE = "entity2source";

    /**
     * Model version of the entity this entity is linked to.
     */
    public static final String ATTRIBUTE_ENTITY2VERSION = "entity2version";

    /**
     * Object version of the entity this entity is linked to.
     */
    public static final String ATTRIBUTE_ENTITY2OBJECTVERSION = "entity2objectversion";

    /**
     * Type of linkage
     */
    public static final String ATTRIBUTE_LINKTYPE = "linktype";

    /**
     * Key of this entity
     */
    public static final String ATTRIBUTE_ENTITY1KEY = "entity1key";

    /**
     * Name of this entity
     */
    public static final String ATTRIBUTE_ENTITY1NAME = "entity1name";

    /**
     * Source of this entity
     */
    public static final String ATTRIBUTE_ENTITY1SOURCE = "entity1source";

    /**
     * Model version of this entity
     */
    public static final String ATTRIBUTE_ENTITY1VERSION = "entity1version";

    /**
     * Default name for linkage elements.
     */
    public static final String NAME = "Linkage";

    /**
     * Label assigned to the linkage.
     */
    public static final String ATTRIBUTE_LABEL = "label";

    private Linkage _entityLinkage;

    /**
     * Creates an {@link CoalesceLinkage} and ties it to its parent
     * {@link CoalesceLinkageSection} .
     *
     * @param parent {@link CoalesceLinkageSection} , the linkage section that
     *               this new linkage will belong to
     * @return {@link CoalesceLinkage} , the new linkage to describe a
     * relationship between two classes
     */
    public static CoalesceLinkage create(CoalesceLinkageSection parent)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");

        CoalesceLinkage newLinkage = new CoalesceLinkage();

        Linkage entityLinkage = new Linkage();
        parent.getEntityLinkageSection().getLinkage().add(entityLinkage);

        if (!newLinkage.initialize(parent, entityLinkage))
            return null;

        CoalesceEntity entity = parent.getEntity();

        newLinkage.setSuspendHistory(true);
        newLinkage.setName(NAME);
        newLinkage.setEntity1Key(entity.getKey());
        newLinkage.setEntity1Name(entity.getName());
        newLinkage.setEntity1Source(entity.getSource());
        newLinkage.setEntity1Version(entity.getVersion());
        newLinkage.setObjectVersion(entity.getObjectVersion());

        parent.addChildCoalesceObject(newLinkage);

        return newLinkage;

    }

    /**
     * Initializes a previously new {@link CoalesceLinkage} and ties it to its
     * parent {@link CoalesceLinkageSection} .
     *
     * @param parent  {@link CoalesceLinkageSection} , the linkage section that
     *                this new linkage will belong to
     * @param linkage Linkage, the linkage describing a relationship between two
     *                classes
     * @return boolean indicator of success/failure
     */
    protected boolean initialize(CoalesceLinkageSection parent, Linkage linkage)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");
        if (linkage == null)
            throw new IllegalArgumentException("linkage");

        setParent(parent);
        _entityLinkage = linkage;

        return super.initialize(_entityLinkage);
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    public void setStatus(ECoalesceObjectStatus value)
    {
        if (!isSuspendHistory() && !value.equals(getStatus()))
        {
            createHistory("", "", null);
        }

        super.setStatus(value);
    }

    @Override
    public String getClassificationMarkingAsString()
    {
        return _entityLinkage.getClassificationmarking();
    }

    @JsonIgnore
    public Marking getClassificationMarking()
    {
        return new Marking(getClassificationMarkingAsString());
    }

    @Override
    public void setClassificationMarkingAsString(String value)
    {
        if (!isSuspendHistory() && !value.equals(getClassificationMarkingAsString()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setClassificationmarking(value);
    }

    @JsonIgnore
    public void setClassificationMarking(Marking value)
    {
        setClassificationMarkingAsString(value.toPortionString());
    }

    @JsonIgnore
    @Override
    public String getEntity1Key()
    {
        return _entityLinkage.getEntity1Key();
    }

    @Override
    public void setEntity1Key(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity1Key()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity1Key(value);
    }

    @JsonIgnore
    @Override
    public String getEntity1Name()
    {
        return _entityLinkage.getEntity1Name();
    }

    @Override
    public void setEntity1Name(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity1Name()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity1Name(value);
    }

    @JsonIgnore
    @Override
    public String getEntity1Source()
    {
        return _entityLinkage.getEntity1Source();
    }

    @Override
    public void setEntity1Source(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity1Source()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity1Source(value);
    }

    @Override
    public String getEntity1Version()
    {
        return _entityLinkage.getEntity1Version();
    }

    @Override
    public void setEntity1Version(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity1Version()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity1Version(value);
    }

    @Override
    public String getEntity2Key()
    {
        return _entityLinkage.getEntity2Key();
    }

    @Override
    public void setEntity2Key(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity2Key()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity2Key(value);
    }

    @Override
    public String getEntity2Name()
    {
        return _entityLinkage.getEntity2Name();
    }

    @Override
    public void setEntity2Name(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity2Name()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity2Name(value);
    }

    @Override
    public String getEntity2Source()
    {
        return _entityLinkage.getEntity2Source();
    }

    @Override
    public void setEntity2Source(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity2Source()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity2Source(value);
    }

    @Override
    public String getEntity2Version()
    {
        return _entityLinkage.getEntity2Version();
    }

    @Override
    public void setEntity2Version(String value)
    {
        if (!isSuspendHistory() && !value.equals(getEntity2Version()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity2Version(value);
    }

    @Override
    public int getEntity2ObjectVersion()
    {
        return (_entityLinkage.getEntity2Objectversion() != null) ? _entityLinkage.getEntity2Objectversion() : 0;
    }

    @Override
    public void setEntity2ObjectVersion(int value)
    {
        if (!isSuspendHistory() && value != getEntity2ObjectVersion())
        {
            createHistory("", "", null);
        }

        _entityLinkage.setEntity2Objectversion(value);
    }

    @Override
    public Locale getInputLang()
    {
        return _entityLinkage.getInputlang();
    }

    @Override
    public void setInputLang(Locale value)
    {
        if (!isSuspendHistory() && !value.equals(getInputLang()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setInputlang(value);
    }

    @Override
    public ELinkTypes getLinkType()
    {
        return ELinkTypes.getTypeForLabel(_entityLinkage.getLinktype());
    }

    @Override
    public void setLinkType(ELinkTypes value)
    {
        if (!isSuspendHistory() && !value.equals(getLinkType()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setLinktype(value.getLabel());
    }

    @Override
    public String getLabel()
    {
        return _entityLinkage.getLabel();
    }

    @Override
    public void setLabel(String value)
    {
        if (!isSuspendHistory() && !value.equals(getLabel()))
        {
            createHistory("", "", null);
        }

        _entityLinkage.setLabel(value);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    /**
     * Sets the two entities key, name, source and version as well as the link
     * type, classification, modified by, input language, dates created and
     * modified and active status.
     *
     * @param linkType              ELinkTypes value for the relationship type identification
     *                              between the entities
     * @param entity2               {@link CoalesceEntity} belonging to the second entity.
     *                              Provides the entity's key, name, source and version
     * @param classificationMarking Marking of the classification of the
     *                              relationship
     * @param modifiedBy            identification of who entered the relationship
     * @param modifiedByIP
     * @param label
     * @param inputLang             language that the relationship was created in
     * @param status
     */
    public void establishLinkage(ELinkTypes linkType,
                                 ECoalesceObjectStatus status,
                                 CoalesceEntity entity2,
                                 Marking classificationMarking,
                                 String modifiedBy,
                                 String modifiedByIP,
                                 String label,
                                 Locale inputLang)
    {

        establishLinkage(linkType,
                         status,
                         entity2.getKey(),
                         entity2.getName(),
                         entity2.getSource(),
                         entity2.getVersion(),
                         entity2.getObjectVersion(),
                         classificationMarking,
                         modifiedBy,
                         modifiedByIP,
                         label,
                         inputLang);

    }

    /**
     * Creates a link between entity1 and entity2
     *
     * @param linkType
     * @param entity2Key
     * @param entity2Name
     * @param entity2Source
     * @param entity2Version
     * @param entity2ObjectVersion
     * @param classificationMarking
     * @param modifiedBy
     * @param modifiedByIP
     * @param label
     * @param inputLang
     * @param status
     */
    public void establishLinkage(ELinkTypes linkType,
                                 ECoalesceObjectStatus status,
                                 String entity2Key,
                                 String entity2Name,
                                 String entity2Source,
                                 String entity2Version,
                                 int entity2ObjectVersion,
                                 Marking classificationMarking,
                                 String modifiedBy,
                                 String modifiedByIP,
                                 String label,
                                 Locale inputLang)
    {
        CoalesceEntity entity1 = getEntity();

        // Set Values
        setObjectVersion(entity1.getObjectVersion());
        setLabel(label);
        setStatus(status);

        setEntity1Key(entity1.getKey());
        setEntity1Name(entity1.getName());
        setEntity1Source(entity1.getSource());
        setEntity1Version(entity1.getVersion());

        setLinkType(linkType != null ? linkType : ELinkTypes.UNDEFINED);

        setEntity2Key(entity2Key);
        setEntity2Name(entity2Name);
        setEntity2Source(entity2Source);
        setEntity2Version(entity2Version);
        setEntity2ObjectVersion(entity2ObjectVersion);

        setClassificationMarking(classificationMarking);
        setModifiedBy(modifiedBy);
        setModifiedByIP(modifiedByIP);
        setInputLang(inputLang);
    }

    // -----------------------------------------------------------------------//
    // public Shared Methods
    // -----------------------------------------------------------------------//

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entityLinkage.getHistory().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        switch (name.toLowerCase())
        {
        case ATTRIBUTE_ENTITY1KEY:
            setEntity1Key(value);
            return true;
        case ATTRIBUTE_ENTITY1NAME:
            setEntity1Name(value);
            return true;
        case ATTRIBUTE_ENTITY1SOURCE:
            setEntity1Source(value);
            return true;
        case ATTRIBUTE_ENTITY1VERSION:
            setEntity1Version(value);
            return true;
        case ATTRIBUTE_LINKTYPE:
            ELinkTypes type = ELinkTypes.getTypeForLabel(value);

            if (type == ELinkTypes.UNDEFINED)
            {
                type = ELinkTypes.valueOf(value);
            }

            setLinkType(type);
            return true;
        case ATTRIBUTE_ENTITY2KEY:
            setEntity2Key(value);
            return true;
        case ATTRIBUTE_ENTITY2NAME:
            setEntity2Name(value);
            return true;
        case ATTRIBUTE_ENTITY2SOURCE:
            setEntity2Source(value);
            return true;
        case ATTRIBUTE_ENTITY2VERSION:
            setEntity2Version(value);
            return true;
        case ATTRIBUTE_ENTITY2OBJECTVERSION:
            setEntity2ObjectVersion(Integer.parseInt(value));
            return true;
        case CoalesceAttributes.ATTRIBUTE_MARKING:
            setClassificationMarking(new Marking(value));
            return true;
        case CoalesceAttributes.ATTRIBUTE_INPUTLANG:

            Locale inputLang = LocaleConverter.parseLocale(value);

            if (inputLang == null)
                return false;

            setInputLang(inputLang);

            return true;
        case ATTRIBUTE_LABEL:
            setLabel(value);
            return true;

        default:
            if (setOtherAttribute(name, value))
            {
                updateLastModified();
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName(ATTRIBUTE_ENTITY1KEY), _entityLinkage.getEntity1Key());
        map.put(new QName(ATTRIBUTE_ENTITY1NAME), _entityLinkage.getEntity1Name());
        map.put(new QName(ATTRIBUTE_ENTITY1SOURCE), _entityLinkage.getEntity1Source());
        map.put(new QName(ATTRIBUTE_ENTITY1VERSION), _entityLinkage.getEntity1Version());
        map.put(new QName(ATTRIBUTE_LINKTYPE), _entityLinkage.getLinktype());
        map.put(new QName(ATTRIBUTE_ENTITY2KEY), _entityLinkage.getEntity2Key());
        map.put(new QName(ATTRIBUTE_ENTITY2NAME), _entityLinkage.getEntity2Name());
        map.put(new QName(ATTRIBUTE_ENTITY2SOURCE), _entityLinkage.getEntity2Source());
        map.put(new QName(ATTRIBUTE_ENTITY2VERSION), _entityLinkage.getEntity2Version());
        map.put(new QName(ATTRIBUTE_LABEL), _entityLinkage.getLabel());
        map.put(new QName(CoalesceAttributes.ATTRIBUTE_MARKING), _entityLinkage.getClassificationmarking());

        if (_entityLinkage.getEntity2Objectversion() == null)
        {
            map.put(new QName(ATTRIBUTE_ENTITY2OBJECTVERSION), "0");
        }
        else
        {
            map.put(new QName(ATTRIBUTE_ENTITY2OBJECTVERSION), Integer.toString(_entityLinkage.getEntity2Objectversion()));
        }

        if (_entityLinkage.getInputlang() == null)
        {
            map.put(new QName(CoalesceAttributes.ATTRIBUTE_INPUTLANG), null);
        }
        else
        {
            map.put(new QName(CoalesceAttributes.ATTRIBUTE_INPUTLANG), _entityLinkage.getInputlang().toString());
        }

        return map;
    }

    protected Linkage getBaseLinkage()
    {
        return _entityLinkage;
    }

}
