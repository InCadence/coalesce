package Coalesce.Framework.DataModel;

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
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, ECoalesceFieldDataTypes> codeToStatusMapping;

    private ECoalesceFieldDataTypes(String label)
    {
        this._label = label;
    }

    public static ECoalesceFieldDataTypes getStatus(int code)
    {
        initMapping();

        return codeToStatusMapping.get(code);
    }

    private static void initMapping()
    {
        if (codeToStatusMapping == null)
        {
            codeToStatusMapping = new HashMap<String, ECoalesceFieldDataTypes>();
            for (ECoalesceFieldDataTypes s : values())
            {
                codeToStatusMapping.put(s._label.toLowerCase(), s);
            }

        }
    }

    public String getLabel()
    {
        return _label;
    }

    public static ECoalesceFieldDataTypes GetTypeForCoalesceType(String coalesceType)
    {
        initMapping();

        ECoalesceFieldDataTypes value = codeToStatusMapping.get(coalesceType.trim().toLowerCase());

        if (value == null) value = ECoalesceFieldDataTypes.StringType;

        return value;
    }

    public static ECoalesceFieldDataTypes GetTypeForSQLType(String sqlType)
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
