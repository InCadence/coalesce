package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public enum ELinkTypes {
    Undefined(0, "Undefined"),
    IsChildOf(1, "IsChildOf"),
    IsParentOf(2, "IsParentOf"),
    Created(8, "Created"),
    WasCreatedBy(16, "WasCreatedBy"),
    HasMember(32, "HasMember"),
    IsAMemberOf(64, "IsAMemberOf"),
    HasParticipant(128, "HasParticipant"),
    IsAParticipantOf(256, "IsAParticipantOf"),
    IsWatching(512, "IsWatching"),
    IsBeingWatchedBy(1024, "IsBeingWatchedBy"),
    IsAPeerOf(2048, "IsAPeerOf"),
    IsOwnedBy(4096, "IsOwnedBy"),
    HasOwnershipOf(8192, "HasOwnershipOf"),
    IsUsedBy(16384, "IsUsedBy"),
    HasUseOf(32768, "HasUseOf");
    
    private int value;
    private String label;
    
    private ELinkTypes(int value){
    	this.value= value;
    }
    
    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<Integer, ELinkTypes> codeToStatusMapping;

    private ELinkTypes(int code, String label){
        this.value = code;
        this.label = label;
    }
 
    public static ELinkTypes getStatus(int code) {
        if (codeToStatusMapping == null) {
            initMapping();
        }
        return codeToStatusMapping.get(code);
    }
 
    private static void initMapping() {
        codeToStatusMapping = new HashMap<Integer, ELinkTypes>();
        for (ELinkTypes s : values()) {
            codeToStatusMapping.put(s.value, s);
        }
    }
 
    public int getValue() {
        return value;
    }
 
    public String getLabel() {
        return label;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LinkTypes");
        sb.append("{code=").append(value);
        sb.append(", label='").append(label);
        sb.append('}');
        return sb.toString();
    }
 
    public static void main(String[] args) {
        System.out.println(ELinkTypes.Undefined);
        System.out.println(ELinkTypes.getStatus(0));
    }
    
    public static int GetELinkTypeCodeForLabel(String value){
    	switch (value.toLowerCase()){
    	case "undefined":
    		return 0;
    	case "ischildof":
    		return 1;
    	case "isparentof":
    		return 2;
    	case "created":
    		return 8;
    	case "wascreatedby":
    		return 16;
    	case "hasmember":
    		return 32;
    	case "isamemberof":
    		return 64;
    	case "hasparticipant":
    		return 128;
    	case "isaparticipantof":
    		return 256;
    	case "iswatching":
    		return 512;
    	case "isbeingwatchedby":
    		return 1024;
    	case "isapeerof":
    		return 2048;
    	case "isownedby":
    		return 4096;
    	case "hasownershipof":
    		return 8192;
    	case "isusedby":
    		return 16384;
    	case "hasuseof":
    		return 32768;
    	default:
    		return 0;
    	}

    }

    public static String GetELinkTypeLabelForCode(int value){
    	switch (value){
    	case 0:
    		return "Undefined";
    	case 1:
    		return "IsChildOf";
    	case 2: 
    		return "IsParentOf";
    	case 8:
    		return "Created";
    	case 16:
    		return "WasCreatedBy";
    	case 32:
    		return "HasMember";
    	case 64:
    		return "IsAMemberOf";
    	case 128:
    		return "HasParticipant";
    	case 256:
    		return "IsAParticipantOf";
    	case 512:
    		return "IsWatching";
    	case 1024:
    		return "IsBeingWatchedBy";
    	case 2048:
    		return "IsAPeerOf";
    	case 4096:
    		return "IsOwnedBy";
    	case 8192:
    		return "HasOwnershipOf";
    	case 16384:
    		return "IsUsedBy";
    	case 32768:
    		return "HasUseOf";
		default: 
    		return "Undefined";
    	}
    }

    public static ELinkTypes GetELinkTypeTypeForCode(int value){
    	switch (value){
    	case 0:
    		return ELinkTypes.Undefined;
    	case 1:
    		return ELinkTypes.IsChildOf;
    	case 2: 
    		return ELinkTypes.IsParentOf;
    	case 8:
    		return ELinkTypes.Created;
    	case 16:
    		return ELinkTypes.WasCreatedBy;
    	case 32:
    		return ELinkTypes.HasMember;
    	case 64:
    		return ELinkTypes.IsAMemberOf;
    	case 128:
    		return ELinkTypes.HasParticipant;
    	case 256:
    		return ELinkTypes.IsAParticipantOf;
    	case 512:
    		return ELinkTypes.IsWatching;
    	case 1024:
    		return ELinkTypes.IsBeingWatchedBy;
    	case 2048:
    		return ELinkTypes.IsAPeerOf;
    	case 4096:
    		return ELinkTypes.IsOwnedBy;
    	case 8192:
    		return ELinkTypes.HasOwnershipOf;
    	case 16384:
    		return ELinkTypes.IsUsedBy;
    	case 32768:
    		return ELinkTypes.HasUseOf;
		default: 
    		return ELinkTypes.Undefined;
    	}
    }
    
    public static String GetELinkTypeLabelForType(ELinkTypes LinkType) { 
    	
        switch(LinkType){

            case Undefined:
                return "Undefined";

            case IsParentOf:
                return "IsParentOf";

            case IsChildOf:
                return "IsChildOf";

            case Created:
                return "Created";

            case WasCreatedBy:
                return "WasCreatedBy";

            case HasMember:
                return "HasMember";

            case IsAMemberOf:
                return "IsAMemberOf";

            case HasParticipant:
                return "HasParticipant";

            case IsAParticipantOf:
                return "IsAParticipantOf";

            case IsWatching:
                return "IsWatching";

            case IsBeingWatchedBy:
                return "IsBeingWatchedBy";

            case IsAPeerOf:
                return "IsAPeerOf";

            case IsOwnedBy:
                return "IsOwnedBy";

            case HasOwnershipOf:
                return "HasOwnershipOf";

            case IsUsedBy:
                return "IsUsedBy";

            case HasUseOf:
                return "HasUseOf";

            default:
                return "Undefined";
        }

    }

    public static ELinkTypes GetReciprocalLinkType(ELinkTypes LinkType) { 
        try{
            switch(LinkType){

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

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceLinakge");

            // return Undefined
            return ELinkTypes.Undefined;
        }
    }
    
}
