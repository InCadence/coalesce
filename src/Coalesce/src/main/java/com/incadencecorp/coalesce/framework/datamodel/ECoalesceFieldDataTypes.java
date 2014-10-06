package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceFieldDataTypes
{

    StringType("string"),
    DateTimeType("datetime"),
    UriType("uri"),
    BinaryType("binary"),
    BooleanType("boolean"),
    IntegerType("integer"),
    GuidType("guid"),
    GeocoordinateType("geocoordinate"),
    FileType("file"),
    GeocoordinateListType("geocoordinatelist");

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

    public String getLabel()
    {
        return _label;
    }

    public static ECoalesceFieldDataTypes getTypeForCoalesceType(String coalesceType)
    {
        initMapping();

        ECoalesceFieldDataTypes value = _labelToStatusMapping.get(coalesceType.trim().toLowerCase());

        if (value == null) value = ECoalesceFieldDataTypes.StringType;

        return value;
    }

    public static ECoalesceFieldDataTypes getTypeForSQLType(String sqlType)
    {
        switch (sqlType.toUpperCase()) {

        case "ADVARWCHAR":
        case "ADLONGVARWCHAR":
            return ECoalesceFieldDataTypes.StringType;

        case "ADDBTIMESTAMP":
            return ECoalesceFieldDataTypes.DateTimeType;

        case "ADBOOLEAN":
            return ECoalesceFieldDataTypes.BooleanType;

        case "ADGUID":
            return ECoalesceFieldDataTypes.GuidType;

        case "ADSMALLINT":
        case "ADINTEGER":
            return ECoalesceFieldDataTypes.IntegerType;

        case "ADLONGVARBINARY":
            return ECoalesceFieldDataTypes.BinaryType;

        default:
            return ECoalesceFieldDataTypes.StringType;
        }

    }

}
