package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

public enum ELinkTypes
{
    UNDEFINED("Undefined"),
    IS_CHILD_OF("IsChildOf"),
    IS_PARENT_OF("IsParentOf"),
    CREATED("Created"),
    WAS_CREATED_BY("WasCreatedBy"),
    HAS_MEMBER("HasMember"),
    IS_A_MEMBER_OF("IsAMemberOf"),
    HAS_PARTICIPANT("HasParticipant"),
    IS_A_PARTICIPANT_OF("IsAParticipantOf"),
    IS_WATCHING("IsWatching"),
    IS_BEING_WATCHED_BY("IsBeingWatchedBy"),
    IS_A_PEER_OF("IsAPeerOf"),
    IS_OWNED_BY("IsOwnedBy"),
    HAS_OWNERSHIP_OF("HasOwnershipOf"),
    IS_USED_BY("IsUsedBy"),
    HAS_USE_OF("HasUseOf");

    private String _label;

    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ELinkTypes> codeToStatusMapping;

    private ELinkTypes(String label)
    {
        this._label = label;
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

    /**
     * Returns the Label property of the ELinkTypes type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getLabel()
    {
        return _label;
    }

    /**
     * Returns the ELinkTypes type for the String type parameter.
     * 
     * @param coalesceType
     *     allowed object is
     *     {@link String }
     * @return
     *     possible object is
     *     {@link ELinkTypes }
     */
    public static ELinkTypes getTypeForLabel(String coalesceType)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(coalesceType)) return ELinkTypes.UNDEFINED;
        
        ELinkTypes value = codeToStatusMapping.get(coalesceType.trim().toLowerCase());

        if (value == null) value = ELinkTypes.UNDEFINED;

        return value;

    }

    /**
     * Returns the reverse relationship ELinkTypes link type for this ELinkTypes.
     * 
     * @return
     *     possible object is
     *     {@link ELinkTypes }
     */
    public ELinkTypes getReciprocalLinkType()
    {

        switch (this) {

        case UNDEFINED:
            return ELinkTypes.UNDEFINED;

        case IS_PARENT_OF:
            return ELinkTypes.IS_CHILD_OF;

        case IS_CHILD_OF:
            return ELinkTypes.IS_PARENT_OF;

        case CREATED:
            return ELinkTypes.WAS_CREATED_BY;

        case WAS_CREATED_BY:
            return ELinkTypes.CREATED;

        case HAS_MEMBER:
            return ELinkTypes.IS_A_MEMBER_OF;

        case IS_A_MEMBER_OF:
            return ELinkTypes.HAS_MEMBER;

        case HAS_PARTICIPANT:
            return ELinkTypes.IS_A_PARTICIPANT_OF;

        case IS_A_PARTICIPANT_OF:
            return ELinkTypes.HAS_PARTICIPANT;

        case IS_WATCHING:
            return ELinkTypes.IS_BEING_WATCHED_BY;

        case IS_BEING_WATCHED_BY:
            return ELinkTypes.IS_WATCHING;

        case IS_A_PEER_OF:
            return ELinkTypes.IS_A_PEER_OF;

        case IS_OWNED_BY:
            return ELinkTypes.HAS_OWNERSHIP_OF;

        case HAS_OWNERSHIP_OF:
            return ELinkTypes.IS_OWNED_BY;

        case IS_USED_BY:
            return ELinkTypes.HAS_USE_OF;

        case HAS_USE_OF:
            return ELinkTypes.IS_USED_BY;

        default:
            return ELinkTypes.UNDEFINED;
        }

    }

}
