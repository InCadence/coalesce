package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

public enum ELinkTypes
{
    Undefined("Undefined"),
    IsChildOf("IsChildOf"),
    IsParentOf("IsParentOf"),
    Created("Created"),
    WasCreatedBy("WasCreatedBy"),
    HasMember("HasMember"),
    IsAMemberOf("IsAMemberOf"),
    HasParticipant("HasParticipant"),
    IsAParticipantOf("IsAParticipantOf"),
    IsWatching("IsWatching"),
    IsBeingWatchedBy("IsBeingWatchedBy"),
    IsAPeerOf("IsAPeerOf"),
    IsOwnedBy("IsOwnedBy"),
    HasOwnershipOf("HasOwnershipOf"),
    IsUsedBy("IsUsedBy"),
    HasUseOf("HasUseOf");

    private String _label;

    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ELinkTypes> codeToStatusMapping;

    private ELinkTypes(String label)
    {
        this._label = label;
    }

    public static ELinkTypes getStatus(int code)
    {

        initMapping();

        return codeToStatusMapping.get(code);
    }

    private static void initMapping()
    {

        if (codeToStatusMapping == null)
        {
            codeToStatusMapping = new HashMap<String, ELinkTypes>();
            for (ELinkTypes s : values())
            {
                codeToStatusMapping.put(s._label.toLowerCase(), s);
            }
        }
    }

    public String getLabel()
    {
        return _label;
    }

    public static ELinkTypes GetTypeForLabel(String coalesceType)
    {

        initMapping();

        ELinkTypes value = codeToStatusMapping.get(coalesceType.trim().toLowerCase());

        if (value == null) value = ELinkTypes.Undefined;

        return value;

    }

    public ELinkTypes GetReciprocalLinkType()
    {

        switch (this) {

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

        default:
            return ELinkTypes.Undefined;
        }

    }

}
