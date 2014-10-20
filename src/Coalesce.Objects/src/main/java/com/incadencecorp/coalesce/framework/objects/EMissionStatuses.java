package com.incadencecorp.coalesce.framework.objects;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

public enum EMissionStatuses
{
    Pending("pending"),
    InProgress("inprogress"),
    Complete("complete"),
    Cancelled("cancelled"),
    Deleted("deleted"),
    Unknown("unknown");

    private String _label;

    EMissionStatuses(String label)
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

    private static Map<String, EMissionStatuses> _codeToStatusMapping;

    private static void initMapping()
    {
        if (_codeToStatusMapping == null)
        {
            _codeToStatusMapping = new HashMap<String, EMissionStatuses>();
            for (EMissionStatuses s : values())
            {
                _codeToStatusMapping.put(s._label.toLowerCase(), s);
            }
        }
    }

    public static EMissionStatuses fromLabel(String label)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(label)) return EMissionStatuses.Unknown;

        EMissionStatuses value = _codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = EMissionStatuses.Unknown;

        return value;
    }
}
