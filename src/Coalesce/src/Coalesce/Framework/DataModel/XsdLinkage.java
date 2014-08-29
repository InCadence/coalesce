package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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

        XsdLinkage linkage = new XsdLinkage();

        Linkage entityLinkage = new Linkage();
        parent.GetEntityLinkageSection().getLinkage().add(entityLinkage);

        if (!linkage.Initialize(parent, entityLinkage)) return null;

        linkage.SetName("Linkage");
        
        return linkage;

    }

    public boolean Initialize(XsdLinkageSection parent, Linkage linkage)
    {

        _parent = parent;
        _entityLinkage = linkage;

        return super.Initialize();
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    public String GetObjectKey()
    {
        return _entityLinkage.getKey();
    }

    public void SetKey(String value)
    {
        _entityLinkage.setKey(value);
    }

    public String GetName()
    {
        return _entityLinkage.getName();
    }

    public void SetName(String value)
    {
        _entityLinkage.setName(value);
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

    public String GetLinkType()
    {
        String val = _entityLinkage.getLinktype();

        int enumVal = 0;
        try
        {
            enumVal = Integer.parseInt(val);
        }
        catch (NumberFormatException e)
        {
            if (val.length() > 0)
            {
                enumVal = ELinkTypes.GetELinkTypeCodeForLabel(val);
            }
            else
            {
                enumVal = ELinkTypes.GetELinkTypeCodeForLabel(ELinkTypes.GetELinkTypeLabelForType(ELinkTypes.Undefined));
            }

        }

        return Integer.toString(enumVal);
    }

    public void SetLinkType(String value)
    {
        // Switch to string english name if this is an integer. (Could be a bitwise combination of LinkType enumerations)
        try
        {
            int type = Integer.parseInt(value);
            _entityLinkage.setLinktype(ELinkTypes.GetELinkTypeLabelForCode(type));
            SetChanged();
        }
        catch (NumberFormatException e)
        {
            _entityLinkage.setLinktype(ELinkTypes.GetELinkTypeLabelForCode(ELinkTypes.GetELinkTypeCodeForLabel(value)));
            SetChanged();
        }
    }

    public DateTime GetDateCreated()
    {
        try
        {

            // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkage.getDatecreated());
            return _entityLinkage.getDatecreated();

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult SetDateCreated(DateTime value)
    {
        try
        {
            // _entityLinkage.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityLinkage.setDatecreated(value);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public DateTime GetLastModified()
    {
        try
        {

            // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkage.getLastmodified());
            return _entityLinkage.getLastmodified();

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    protected CallResult SetObjectLastModified(DateTime value)
    {
        try
        {
            // _entityLinkage.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityLinkage.setLastmodified(value);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected CallResult GetObjectStatus(String status)
    {
        try
        {
            status = _entityLinkage.getStatus();

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected CallResult SetObjectStatus(String status)
    {
        try
        {
            _entityLinkage.setStatus(status);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public boolean GetIsMarkedDeleted()
    {
        return (GetStatus() == ECoalesceDataObjectStatus.DELETED);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public CallResult EstablishLinkage(CoalesceEntity Entity1,
                                       ELinkTypes LinkType,
                                       CoalesceEntity Entity2,
                                       String ClassificationMarking,
                                       String ModifiedBy,
                                       String InputLang)
    {
        try
        {

            // Set Values
            SetEntity1Key(Entity1.GetKey());
            SetEntity1Name(Entity1.GetName());
            SetEntity1Source(Entity1.GetSource());
            SetEntity1Version(Entity1.GetVersion());

            SetLinkType(GetNameForLinkType(LinkType));

            SetEntity2Key(Entity2.GetKey());
            SetEntity2Name(Entity2.GetName());
            SetEntity2Source(Entity2.GetSource());
            SetEntity2Version(Entity2.GetVersion());

            SetClassificationMarking(ClassificationMarking);
            SetModifiedBy(ModifiedBy);
            SetInputLang(InputLang);

            DateTime utcNow = JodaDateTimeHelper.NowInUtc();
            SetLastModified(utcNow);

            SetStatus(ECoalesceDataObjectStatus.ACTIVE);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public String ToXml()
    {
        return XmlHelper.Serialize(_entityLinkage);
    }

    // -----------------------------------------------------------------------//
    // Private and protected Methods
    // -----------------------------------------------------------------------//

    protected CallResult SetChanged()
    {
        try
        {

            DateTime utcNow = JodaDateTimeHelper.NowInUtc();
            SetLastModified(utcNow);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // -----------------------------------------------------------------------//
    // public Shared Methods
    // -----------------------------------------------------------------------//

    public ELinkTypes GetReciprocalLinkType(ELinkTypes LinkType)
    {
        try
        {
            switch (LinkType) {

            case Undefined:
                return ELinkTypes.Undefined;

            case IsParentOf:
                return ELinkTypes.IsChildOf;

            case IsChildOf:
                return ELinkTypes.IsParentOf;

            case Created:
                return ELinkTypes.WasCreatedBy;

            case WasCreatedBy:
                return ELinkTypes.Created;

            case HasMember:
                return ELinkTypes.IsAMemberOf;

            case IsAMemberOf:
                return ELinkTypes.HasMember;

            case HasParticipant:
                return ELinkTypes.IsAParticipantOf;

            case IsAParticipantOf:
                return ELinkTypes.HasParticipant;

            case IsWatching:
                return ELinkTypes.IsBeingWatchedBy;

            case IsBeingWatchedBy:
                return ELinkTypes.IsWatching;

            case IsAPeerOf:
                return ELinkTypes.IsAPeerOf;

            case IsOwnedBy:
                return ELinkTypes.HasOwnershipOf;

            case HasOwnershipOf:
                return ELinkTypes.IsOwnedBy;

            case IsUsedBy:
                return ELinkTypes.HasUseOf;

            case HasUseOf:
                return ELinkTypes.IsUsedBy;

            }

            return ELinkTypes.Undefined;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinakge");

            return ELinkTypes.Undefined;
        }
    }

    public String GetNameForLinkType(ELinkTypes LinkType)
    {
        try
        {

            return ELinkTypes.GetELinkTypeLabelForType(LinkType);

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinkage");

            return "Undefined";
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityLinkage.getOtherAttributes();
    }
}
