package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceObjectStatus
{
    ACTIVE("Active"), DELETED("Deleted"), UNKNOWN("Unknown");

    private String _label;

    /**
     * A mapping between the string representation and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ECoalesceObjectStatus> _labelToStatusMapping;

    private ECoalesceObjectStatus(String label)
    {
        _label = label;
    }

    private static void initMapping()
    {
        if (_labelToStatusMapping == null)
        {
            _labelToStatusMapping = new HashMap<String, ECoalesceObjectStatus>();
            for (ECoalesceObjectStatus s : values())
            {
                _labelToStatusMapping.put(s._label.trim().toLowerCase(), s);
            }
        }
    }

    /**
     * Returns the Label property of the ECoalesceObjectStatus type.
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
     * Returns the ECoalesceObjectStatus type for the String label parameter.
     * 
     * @param coalesceType
     *     allowed object is
     *     {@link String }
     * @return
     *     possible object is
     *     {@link ECoalesceObjectStatus }
     */
    public static ECoalesceObjectStatus getTypeForLabel(String label)
    {
        initMapping();

        if (label == null) return ECoalesceObjectStatus.UNKNOWN;

        ECoalesceObjectStatus value = _labelToStatusMapping.get(label.trim().toLowerCase());

        if (value == null) value = ECoalesceObjectStatus.UNKNOWN;

        return value;
    }

}
