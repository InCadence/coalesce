package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Linkage;

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

public class CoalesceLinkage extends CoalesceDataObject implements ICoalesceLinkage {

    private Linkage _entityLinkage;

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage} and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}, the linkage section that
     *            this new linkage will belong to
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}, the new linkage to describe a
     *         relationship between two classes
     */
    public static CoalesceLinkage create(CoalesceLinkageSection parent)
    {
        if (parent == null) throw new NullArgumentException("parent");

        CoalesceLinkage newLinkage = new CoalesceLinkage();

        Linkage entityLinkage = new Linkage();
        parent.getEntityLinkageSection().getLinkage().add(entityLinkage);

        if (!newLinkage.initialize(parent, entityLinkage)) return null;

        newLinkage.setName("Linkage");

        parent.addChild(newLinkage);

        return newLinkage;

    }

    /**
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage} and ties it to its
     * parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}, the linkage section that
     *            this new linkage will belong to
     * @param linkage Linkage, the linkage describing a relationship between two classes
     * @return boolean indicator of success/failure
     */
    protected boolean initialize(CoalesceLinkageSection parent, Linkage linkage)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (linkage == null) throw new NullArgumentException("linkage");

        setParent(parent);
        _entityLinkage = linkage;

        return super.initialize();
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    public String getObjectKey()
    {
        return _entityLinkage.getKey();
    }

    @Override
    public void setObjectKey(String value)
    {
        _entityLinkage.setKey(value);
    }

    @Override
    public String getName()
    {
        return _entityLinkage.getName();
    }

    @Override
    public void setName(String value)
    {
        _entityLinkage.setName(value);
    }

    @Override
    public String getType()
    {
        return "linkage";
    }

    @Override
    public String getModifiedBy()
    {
        return _entityLinkage.getModifiedby();
    }

    @Override
    public void setModifiedBy(String value)
    {
        _entityLinkage.setModifiedby(value);
        setChanged();
    }

    @Override
    public Marking getClassificationMarking()
    {
        return new Marking(_entityLinkage.getClassificationmarking());
    }

    @Override
    public void setClassificationMarking(Marking value)
    {
        _entityLinkage.setClassificationmarking(value.toPortionString());
        setChanged();
    }

    @Override
    public String getEntity1Key()
    {
        return _entityLinkage.getEntity1Key();
    }

    @Override
    public void setEntity1Key(String value)
    {
        _entityLinkage.setEntity1Key(value);
        setChanged();
    }

    @Override
    public String getEntity1Name()
    {
        return _entityLinkage.getEntity1Name();
    }

    @Override
    public void setEntity1Name(String value)
    {
        _entityLinkage.setEntity1Name(value);
        setChanged();
    }

    @Override
    public String getEntity1Source()
    {
        return _entityLinkage.getEntity1Source();
    }

    @Override
    public void setEntity1Source(String value)
    {
        _entityLinkage.setEntity1Source(value);
        setChanged();
    }

    @Override
    public String getEntity1Version()
    {
        return _entityLinkage.getEntity1Version();
    }

    @Override
    public void setEntity1Version(String value)
    {
        _entityLinkage.setEntity1Version(value);
        setChanged();
    }

    @Override
    public String getEntity2Key()
    {
        return _entityLinkage.getEntity2Key();
    }

    @Override
    public void setEntity2Key(String value)
    {
        _entityLinkage.setEntity2Key(value);
        setChanged();
    }

    @Override
    public String getEntity2Name()
    {
        return _entityLinkage.getEntity2Name();
    }

    @Override
    public void setEntity2Name(String value)
    {
        _entityLinkage.setEntity2Name(value);
        setChanged();
    }

    @Override
    public String getEntity2Source()
    {
        return _entityLinkage.getEntity2Source();
    }

    @Override
    public void setEntity2Source(String value)
    {
        _entityLinkage.setEntity2Source(value);
        setChanged();
    }

    @Override
    public String getEntity2Version()
    {
        return _entityLinkage.getEntity2Version();
    }

    @Override
    public void setEntity2Version(String value)
    {
        _entityLinkage.setEntity2Version(value);
        setChanged();
    }

    @Override
    public Locale getInputLang()
    {
        String inputLang = _entityLinkage.getInputlang();

        if (inputLang == null) return null;

        String[] parts = inputLang.replace("-", "_").split("_");

        String formatted;
        switch (parts.length) {
        case 1:
            formatted = parts[0].toLowerCase();
            break;

        case 2:
            formatted = parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
            break;

        case 3:
            formatted = parts[0].toLowerCase() + "_" + parts[1].toUpperCase() + "_" + parts[2];
            break;

        default:
            return null;

        }

        return LocaleUtils.toLocale(formatted);

    }

    @Override
    public void setInputLang(Locale value)
    {
        _entityLinkage.setInputlang(value.toString());
        setChanged();
    }

    @Override
    public ELinkTypes getLinkType()
    {
        return ELinkTypes.getTypeForLabel(_entityLinkage.getLinktype());
    }

    @Override
    public void setLinkType(ELinkTypes value)
    {
        _entityLinkage.setLinktype(value.getLabel());
        setChanged();
    }

    @Override
    public DateTime getDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkage.getDatecreated());
        return _entityLinkage.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // _entityLinkage.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityLinkage.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkage.getLastmodified());
        return _entityLinkage.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // _entityLinkage.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityLinkage.setLastmodified(value);
    }

    @Override
    protected String getObjectStatus()
    {
        return _entityLinkage.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entityLinkage.setStatus(status.getLabel());
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage}'s status identifying if it is
     * current or deleted.
     * 
     * @return boolean indicates if the linkage has been marked as deleted.
     */
    public boolean getIsMarkedDeleted()
    {
        return (getStatus() == ECoalesceDataObjectStatus.DELETED);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    /**
     * Sets the two entities key, name, source and version as well as the link type, classification, modified by, input
     * language, dates created and modified and active status.
     * 
     * @param entity1 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} belonging to the first entity.
     *            Provides the entity's key, name, source and version
     * @param linkType ELinkTypes value for the relationship type identification between the entities
     * @param entity2 {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} belonging to the second entity.
     *            Provides the entity's key, name, source and version
     * @param classificationMarking Marking of the classification of the relationship
     * @param modifiedBy identification of who entered the relationship
     * @param inputLang language that the relationship was created in
     */
    public void establishLinkage(CoalesceEntity entity1,
                                 ELinkTypes linkType,
                                 CoalesceEntity entity2,
                                 Marking classificationMarking,
                                 String modifiedBy,
                                 Locale inputLang)
    {
        // Set Values
        setEntity1Key(entity1.getKey());
        setEntity1Name(entity1.getName());
        setEntity1Source(entity1.getSource());
        setEntity1Version(entity1.getVersion());

        setLinkType(linkType);

        setEntity2Key(entity2.getKey());
        setEntity2Name(entity2.getName());
        setEntity2Source(entity2.getSource());
        setEntity2Version(entity2.getVersion());

        setClassificationMarking(classificationMarking);
        setModifiedBy(modifiedBy);
        setInputLang(inputLang);

        DateTime utcNow = JodaDateTimeHelper.nowInUtc();
        setLastModified(utcNow);

        setStatus(ECoalesceDataObjectStatus.ACTIVE);
    }

    @Override
    public String toXml()
    {
        return XmlHelper.serialize(_entityLinkage);
    }

    // -----------------------------------------------------------------------//
    // Private and protected Methods
    // -----------------------------------------------------------------------//

    private void setChanged()
    {
        DateTime utcNow = JodaDateTimeHelper.nowInUtc();
        setLastModified(utcNow);
    }

    // -----------------------------------------------------------------------//
    // public Shared Methods
    // -----------------------------------------------------------------------//

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return _entityLinkage.getOtherAttributes();
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name.toLowerCase()) {
        case "key":
            setKey(value);
            return true;
        case "datecreated":
            setDateCreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            setLastModified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "name":
            setName(value);
            return true;
        case "entity1key":
            setEntity1Key(value);
            return true;
        case "entity1name":
            setEntity1Name(value);
            return true;
        case "entity1source":
            setEntity1Source(value);
            return true;
        case "entity1version":
            setEntity1Version(value);
            return true;
        case "linktype":
            setLinkType(ELinkTypes.getTypeForLabel(value));
            return true;
        case "entity2key":
            setEntity2Key(value);
            return true;
        case "entity2name":
            setEntity2Name(value);
            return true;
        case "entity2source":
            setEntity2Source(value);
            return true;
        case "entity2version":
            setEntity2Version(value);
            return true;
        case "classificationmarking":
            setClassificationMarking(new Marking(value));
            return true;
        case "modifiedby":
            setModifiedBy(value);
            return true;
        case "inputlang":

            Locale inputLang = CoalesceDataObject.parseLocale(value);

            if (inputLang == null) return false;
            
            setInputLang(inputLang);

            return true;

        case "status":
            setStatus(ECoalesceDataObjectStatus.getTypeForLabel(value));
            return true;
        default:
            if (setOtherAttribute(name, value))
            {
                setChanged();
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
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entityLinkage.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityLinkage.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityLinkage.getLastmodified()));
        map.put(new QName("name"), _entityLinkage.getName());
        map.put(new QName("entity1key"), _entityLinkage.getEntity1Key());
        map.put(new QName("entity1name"), _entityLinkage.getEntity1Name());
        map.put(new QName("entity1source"), _entityLinkage.getEntity1Source());
        map.put(new QName("entity1version"), _entityLinkage.getEntity1Version());
        map.put(new QName("linktype"), _entityLinkage.getLinktype());
        map.put(new QName("entity2key"), _entityLinkage.getEntity2Key());
        map.put(new QName("entity2name"), _entityLinkage.getEntity2Name());
        map.put(new QName("entity2source"), _entityLinkage.getEntity2Source());
        map.put(new QName("entity2version"), _entityLinkage.getEntity2Version());
        map.put(new QName("classificationmarking"), _entityLinkage.getClassificationmarking());
        map.put(new QName("modifiedby"), _entityLinkage.getModifiedby());
        map.put(new QName("inputlang"), _entityLinkage.getInputlang());
        map.put(new QName("status"), _entityLinkage.getStatus());
        return map;
    }
}
