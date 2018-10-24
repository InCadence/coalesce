package com.incadencecorp.coalesce.framework.testobjects;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

import java.util.HashMap;
import java.util.Map;

public enum EMissionStatuses {
    Pending("pending"),
    InProgress("inprogress"),
    Complete("complete"),
    Cancelled("cancelled"),
    Deleted("deleted"),
    Unknown("unknown");

    private static final Object SYNC_INIT = new Object();

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
        synchronized (SYNC_INIT)
        {
            if (_codeToStatusMapping == null)
            {
                _codeToStatusMapping = new HashMap<>();
                for (EMissionStatuses s : values())
                {
                    _codeToStatusMapping.put(s._label.toLowerCase(), s);
                }
            }
        }
    }

    public static EMissionStatuses fromLabel(String label)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(label))
            return EMissionStatuses.Unknown;

        EMissionStatuses value = _codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null)
            value = EMissionStatuses.Unknown;

        return value;
    }
}
