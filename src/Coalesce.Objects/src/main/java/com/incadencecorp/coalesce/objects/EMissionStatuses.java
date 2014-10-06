package com.incadencecorp.coalesce.objects;

import java.util.HashMap;
import java.util.Map;

import Coalesce.Common.Helpers.StringHelper;


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

    private static Map<String, EMissionStatuses> codeToStatusMapping;

    private static void initMapping()
    {
        if (codeToStatusMapping == null)
        {
            codeToStatusMapping = new HashMap<String, EMissionStatuses>();
            for (EMissionStatuses s : values())
            {
                codeToStatusMapping.put(s._label.toLowerCase(), s);
            }
        }
    }

    public static EMissionStatuses fromLabel(String label)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(label)) return EMissionStatuses.Unknown;

        EMissionStatuses value = codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = EMissionStatuses.Unknown;

        return value;
    }
}
