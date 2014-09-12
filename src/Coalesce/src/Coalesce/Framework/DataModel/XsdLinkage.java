package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

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

    public static XsdLinkage Create(XsdLinkageSection parent)
    {

        XsdLinkage newLinkage = new XsdLinkage();

        Linkage entityLinkage = new Linkage();
        parent.GetEntityLinkageSection().getLinkage().add(entityLinkage);

        if (!newLinkage.Initialize(parent, entityLinkage)) return null;

        newLinkage.setName("Linkage");

        // Add to Parent's Child Collection
        if (!(parent._childDataObjects.containsKey(newLinkage.getKey())))
        {
            parent._childDataObjects.put(newLinkage.getKey(), newLinkage);
        }

        return newLinkage;

    }

    public boolean Initialize(XsdLinkageSection parent, Linkage linkage)
    {

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

    public String GetModifiedBy()
    {
        return _entityLinkage.getModifiedby();
    }

    public void SetModifiedBy(String value)
    {
        _entityLinkage.setModifiedby(value);
        SetChanged();
    }

    public String GetClassificationMarking()
    {
        return _entityLinkage.getClassificationmarking();
    }

    public void SetClassificationMarking(String value)
    {
        _entityLinkage.setClassificationmarking(value);
        SetChanged();
    }

    public String GetEntity1Key()
    {
        return _entityLinkage.getEntity1Key();
    }

    public void SetEntity1Key(String value)
    {
        _entityLinkage.setEntity1Key(value);
        SetChanged();
    }

    public String GetEntity1Name()
    {
        return _entityLinkage.getEntity1Name();
    }

    public void SetEntity1Name(String value)
    {
        _entityLinkage.setEntity1Name(value);
        SetChanged();
    }

    public String GetEntity1Source()
    {
        return _entityLinkage.getEntity1Source();
    }

    public void SetEntity1Source(String value)
    {
        _entityLinkage.setEntity1Source(value);
        SetChanged();
    }

    public String GetEntity1Version()
    {
        return _entityLinkage.getEntity1Version();
    }

    public void SetEntity1Version(String value)
    {
        _entityLinkage.setEntity1Version(value);
        SetChanged();
    }

    public String GetEntity2Key()
    {
        return _entityLinkage.getEntity2Key();
    }

    public void SetEntity2Key(String value)
    {
        _entityLinkage.setEntity2Key(value);
        SetChanged();
    }

    public String GetEntity2Name()
    {
        return _entityLinkage.getEntity2Name();
    }

    public void SetEntity2Name(String value)
    {
        _entityLinkage.setEntity2Name(value);
        SetChanged();
    }

    public String GetEntity2Source()
    {
        return _entityLinkage.getEntity2Source();
    }

    public void SetEntity2Source(String value)
    {
        _entityLinkage.setEntity2Source(value);
        SetChanged();
    }

    public String GetEntity2Version()
    {
        return _entityLinkage.getEntity2Version();
    }

    public void SetEntity2Version(String value)
    {
        _entityLinkage.setEntity2Version(value);
        SetChanged();
    }

    public String GetInputLang()
    {
        return _entityLinkage.getInputlang();
    }

    public void SetInputLang(String value)
    {
        _entityLinkage.setInputlang(value);
        SetChanged();
    }

    public ELinkTypes GetLinkType()
    {
        return ELinkTypes.GetTypeForLabel(_entityLinkage.getLinktype());
    }

    public void SetLinkType(ELinkTypes value)
    {
        _entityLinkage.setLinktype(value.getLabel());
        SetChanged();
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
        _entityLinkage.setStatus(status.toLabel());
    }

    public boolean GetIsMarkedDeleted()
    {
        return (getStatus() == ECoalesceDataObjectStatus.DELETED);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public void EstablishLinkage(XsdEntity Entity1,
                                 ELinkTypes LinkType,
                                 XsdEntity Entity2,
                                 String ClassificationMarking,
                                 String ModifiedBy,
                                 String InputLang)
    {
        // Set Values
        SetEntity1Key(Entity1.getKey());
        SetEntity1Name(Entity1.getName());
        SetEntity1Source(Entity1.getSource());
        SetEntity1Version(Entity1.getVersion());

        SetLinkType(LinkType);

        SetEntity2Key(Entity2.getKey());
        SetEntity2Name(Entity2.getName());
        SetEntity2Source(Entity2.getSource());
        SetEntity2Version(Entity2.getVersion());

        SetClassificationMarking(ClassificationMarking);
        SetModifiedBy(ModifiedBy);
        SetInputLang(InputLang);

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

    protected void SetChanged()
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
