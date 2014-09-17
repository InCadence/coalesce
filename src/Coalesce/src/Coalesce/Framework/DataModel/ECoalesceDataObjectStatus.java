package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceDataObjectStatus
{
    ACTIVE("Active"), DELETED("Deleted"), UNKNOWN("Unknown");

    private String _label;

    /**
     * A mapping between the string representation and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ECoalesceDataObjectStatus> _labelToStatusMapping;

    private ECoalesceDataObjectStatus(String label)
    {
        _label = label;
    }

    private static void initMapping()
    {
        if (_labelToStatusMapping == null)
        {
            _labelToStatusMapping = new HashMap<String, ECoalesceDataObjectStatus>();
            for (ECoalesceDataObjectStatus s : values())
            {
                _labelToStatusMapping.put(s._label.trim().toLowerCase(), s);
            }
        }
    }

    public String getLabel()
    {
        return _label;
    }

    public static ECoalesceDataObjectStatus getTypeForLabel(String label)
    {
        initMapping();

        if (label == null) return ECoalesceDataObjectStatus.UNKNOWN;
        
        ECoalesceDataObjectStatus value = _labelToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = ECoalesceDataObjectStatus.UNKNOWN;

        return value;
    }

}
