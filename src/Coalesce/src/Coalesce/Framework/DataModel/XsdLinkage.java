package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection.Linkage;

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

public class XsdLinkage extends XsdDataObject {

    private Linkage _entityLinkage;

    public static XsdLinkage create(XsdLinkageSection parent)
    {
        if (parent == null) throw new NullArgumentException("parent");

        XsdLinkage newLinkage = new XsdLinkage();

        Linkage entityLinkage = new Linkage();
        parent.getEntityLinkageSection().getLinkage().add(entityLinkage);

        if (!newLinkage.initialize(parent, entityLinkage)) return null;

        newLinkage.setName("Linkage");

        // Add to Parent's Child Collection
        if (!(parent._childDataObjects.containsKey(newLinkage.getKey())))
        {
            parent._childDataObjects.put(newLinkage.getKey(), newLinkage);
        }

        return newLinkage;

    }

    public boolean initialize(XsdLinkageSection parent, Linkage linkage)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (linkage == null) throw new NullArgumentException("linkage");
        
        _parent = parent;
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

    public String getModifiedBy()
    {
        return _entityLinkage.getModifiedby();
    }

    public void setModifiedBy(String value)
    {
        _entityLinkage.setModifiedby(value);
        setChanged();
    }

    public Marking getClassificationMarking()
    {
        return new Marking(_entityLinkage.getClassificationmarking());
    }

    public void setClassificationMarking(Marking value)
    {
        _entityLinkage.setClassificationmarking(value.ToPortionString());
        setChanged();
    }

    public String getEntity1Key()
    {
        return _entityLinkage.getEntity1Key();
    }

    public void setEntity1Key(String value)
    {
        _entityLinkage.setEntity1Key(value);
        setChanged();
    }

    public String getEntity1Name()
    {
        return _entityLinkage.getEntity1Name();
    }

    public void setEntity1Name(String value)
    {
        _entityLinkage.setEntity1Name(value);
        setChanged();
    }

    public String getEntity1Source()
    {
        return _entityLinkage.getEntity1Source();
    }

    public void setEntity1Source(String value)
    {
        _entityLinkage.setEntity1Source(value);
        setChanged();
    }

    public String getEntity1Version()
    {
        return _entityLinkage.getEntity1Version();
    }

    public void setEntity1Version(String value)
    {
        _entityLinkage.setEntity1Version(value);
        setChanged();
    }

    public String getEntity2Key()
    {
        return _entityLinkage.getEntity2Key();
    }

    public void setEntity2Key(String value)
    {
        _entityLinkage.setEntity2Key(value);
        setChanged();
    }

    public String getEntity2Name()
    {
        return _entityLinkage.getEntity2Name();
    }

    public void setEntity2Name(String value)
    {
        _entityLinkage.setEntity2Name(value);
        setChanged();
    }

    public String getEntity2Source()
    {
        return _entityLinkage.getEntity2Source();
    }

    public void setEntity2Source(String value)
    {
        _entityLinkage.setEntity2Source(value);
        setChanged();
    }

    public String getEntity2Version()
    {
        return _entityLinkage.getEntity2Version();
    }

    public void setEntity2Version(String value)
    {
        _entityLinkage.setEntity2Version(value);
        setChanged();
    }

    public String getInputLang()
    {
        return _entityLinkage.getInputlang();
    }

    public void setInputLang(String value)
    {
        _entityLinkage.setInputlang(value);
        setChanged();
    }

    public ELinkTypes getLinkType()
    {
        return ELinkTypes.GetTypeForLabel(_entityLinkage.getLinktype());
    }

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

    public boolean getIsMarkedDeleted()
    {
        return (getStatus() == ECoalesceDataObjectStatus.DELETED);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public void establishLinkage(XsdEntity Entity1,
                                 ELinkTypes LinkType,
                                 XsdEntity Entity2,
                                 Marking ClassificationMarking,
                                 String ModifiedBy,
                                 String InputLang)
    {
        // Set Values
        setEntity1Key(Entity1.getKey());
        setEntity1Name(Entity1.getName());
        setEntity1Source(Entity1.getSource());
        setEntity1Version(Entity1.getVersion());

        setLinkType(LinkType);

        setEntity2Key(Entity2.getKey());
        setEntity2Name(Entity2.getName());
        setEntity2Source(Entity2.getSource());
        setEntity2Version(Entity2.getVersion());

        setClassificationMarking(ClassificationMarking);
        setModifiedBy(ModifiedBy);
        setInputLang(InputLang);

        DateTime utcNow = JodaDateTimeHelper.NowInUtc();
        setLastModified(utcNow);

        setStatus(ECoalesceDataObjectStatus.ACTIVE);
    }

    public String toXml()
    {
        return XmlHelper.Serialize(_entityLinkage);
    }

    // -----------------------------------------------------------------------//
    // Private and protected Methods
    // -----------------------------------------------------------------------//

    private void setChanged()
    {
        DateTime utcNow = JodaDateTimeHelper.NowInUtc();
        setLastModified(utcNow);
    }

    // -----------------------------------------------------------------------//
    // public Shared Methods
    // -----------------------------------------------------------------------//

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityLinkage.getOtherAttributes();
    }
}
