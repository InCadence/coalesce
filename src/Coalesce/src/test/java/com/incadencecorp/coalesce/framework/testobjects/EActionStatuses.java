package com.incadencecorp.coalesce.framework.testobjects;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

import java.util.HashMap;
import java.util.Map;

public enum EActionStatuses {
    CollectionPending("pending"),
    CollectionInProgress("inprogress"),
    CollectionComplete("complete"),
    ExploitationRequired("exploitation_required"),
    ExploitationPending("exploitation_pending"),
    ExploitationComplete("exploitation_complete"),
    Unknown("unknown");

    private static final Object SYNC_INIT = new Object();

    private String _label;

    EActionStatuses(String label)
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

    private static Map<String, EActionStatuses> _codeToStatusMapping;

    private static void initMapping()
    {
        synchronized (SYNC_INIT)
        {
            if (_codeToStatusMapping == null)
            {
                _codeToStatusMapping = new HashMap<>();
                for (EActionStatuses s : values())
                {
                    _codeToStatusMapping.put(s._label.toLowerCase(), s);
                }
            }
        }
    }

    public static EActionStatuses fromLabel(String label)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(label))
            return EActionStatuses.Unknown;

        EActionStatuses value = _codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null)
            value = EActionStatuses.Unknown;

        return value;
    }

}
