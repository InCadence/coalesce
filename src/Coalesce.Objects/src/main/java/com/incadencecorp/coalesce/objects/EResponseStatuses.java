package com.incadencecorp.coalesce.objects;

import java.util.HashMap;
import java.util.Map;

import Coalesce.Common.Helpers.StringHelper;

public enum EResponseStatuses
{
    WatchList("watchlist"),
    Alert("alertstatus"),
    Response("response"),
    NoResponse("noresponse"),
    Error("errorstatus"),
    Unknown("unknown");

    private String _label;

    EResponseStatuses(String label)
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

    private static Map<String, EResponseStatuses> codeToStatusMapping;

    private static void initMapping()
    {
        if (codeToStatusMapping == null)
        {
            codeToStatusMapping = new HashMap<String, EResponseStatuses>();
            for (EResponseStatuses s : values())
            {
                codeToStatusMapping.put(s._label.toLowerCase(), s);
            }
        }
    }

    public static EResponseStatuses fromLabel(String label)
    {

        initMapping();

        if (StringHelper.isNullOrEmpty(label)) return EResponseStatuses.Unknown;

        EResponseStatuses value = codeToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = EResponseStatuses.Unknown;

        return value;
    }
}
