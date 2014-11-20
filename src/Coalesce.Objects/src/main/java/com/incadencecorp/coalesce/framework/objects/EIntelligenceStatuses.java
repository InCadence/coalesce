package com.incadencecorp.coalesce.framework.objects;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

public enum EIntelligenceStatuses
{
    CollectionPending("pending"), 
    CollectionInProgress("inprogress"), 
    CollectionComplete("complete"), 
    ExploitationRequired("exploitation_required"), 
    ExploitationPending("exploitation_pending"), 
    ExploitationComplete("exploitation_complete"), 
    Unknown("unknown");

    private String _label;

    EIntelligenceStatuses(String label)
    {
        this._label = label;
    }

    public String getLabel()
    {
        return this._label;
    }

    // ----------------------------------------------------------------------//
    // Static Methods
    // ----------------------------------------------------------------------//

    private static Map<String, EIntelligenceStatuses> _codeToStatusMapping;

    private static void initMapping()
    {
        if (_codeToStatusMapping == null)
        {
            _codeToStatusMapping = new HashMap<String, EIntelligenceStatuses>();
            for (EIntelligenceStatuses s : values())
            {
                _codeToStatusMapping.put(s._label.toLowerCase(), s);
            }
        }
    }

    public static EIntelligenceStatuses fromLabel(String label)
    {
        
        initMapping();
        
        if (StringHelper.isNullOrEmpty(label)) return EIntelligenceStatuses.Unknown;
    
        EIntelligenceStatuses value = _codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = EIntelligenceStatuses.Unknown;

        return value;
    }

}
