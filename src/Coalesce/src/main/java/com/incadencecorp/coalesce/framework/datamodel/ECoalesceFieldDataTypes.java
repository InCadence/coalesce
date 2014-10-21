package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceFieldDataTypes
{

    STRING_TYPE("string"),
    DATE_TIME_TYPE("datetime"),
    URI_TYPE("uri"),
    BINARY_TYPE("binary"),
    BOOLEAN_TYPE("boolean"),
    INTEGER_TYPE("integer"),
    GUID_TYPE("guid"),
    GEOCOORDINATE_TYPE("geocoordinate"),
    FILE_TYPE("file"),
    GEOCOORDINATE_LIST_TYPE("geocoordinatelist"),
    DOUBLE_TYPE("double"),
    FLOAT_TYPE("float");

    private String _label;

    /**
     * A mapping between the string representation and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ECoalesceFieldDataTypes> _labelToStatusMapping;

    private ECoalesceFieldDataTypes(String label)
    {
        this._label = label;
    }

    private static void initMapping()
    {
        if (_labelToStatusMapping == null)
        {
            _labelToStatusMapping = new HashMap<String, ECoalesceFieldDataTypes>();
            for (ECoalesceFieldDataTypes s : values())
            {
                _labelToStatusMapping.put(s._label.trim().toLowerCase(), s);
            }

        }
    }

    /**
     * Returns the Label property of the ECoalesceFieldDataTypes type
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
     * Returns the ECoalesceFieldDataTypes type for the String type parameter
     * 
     * @param coalesceType
     *     allowed object is
     *     {@link String }
     * @return
     *     possible object is
     *     {@link ECoalesceFieldDataTypes }
     */
    public static ECoalesceFieldDataTypes getTypeForCoalesceType(String coalesceType)
    {
        initMapping();

        ECoalesceFieldDataTypes value = _labelToStatusMapping.get(coalesceType.trim().toLowerCase());

        if (value == null) value = ECoalesceFieldDataTypes.STRING_TYPE;

        return value;
    }

    /**
     * Returns the ECoalesceFieldDataTypes type for the String sqltype parameter
     * 
     * @param coalesceType
     *     allowed object is
     *     {@link String }
     * @return
     *     possible object is
     *     {@link ECoalesceFieldDataTypes }
     */
    public static ECoalesceFieldDataTypes getTypeForSQLType(String sqlType)
    {
        switch (sqlType.toUpperCase()) {

        case "ADVARWCHAR":
        case "ADLONGVARWCHAR":
            return ECoalesceFieldDataTypes.STRING_TYPE;

        case "ADDBTIMESTAMP":
            return ECoalesceFieldDataTypes.DATE_TIME_TYPE;

        case "ADBOOLEAN":
            return ECoalesceFieldDataTypes.BOOLEAN_TYPE;

        case "ADGUID":
            return ECoalesceFieldDataTypes.GUID_TYPE;

        case "ADSMALLINT":
        case "ADINTEGER":
            return ECoalesceFieldDataTypes.INTEGER_TYPE;

        case "ADLONGVARBINARY":
            return ECoalesceFieldDataTypes.BINARY_TYPE;

        default:
            return ECoalesceFieldDataTypes.STRING_TYPE;
        }

    }

}
