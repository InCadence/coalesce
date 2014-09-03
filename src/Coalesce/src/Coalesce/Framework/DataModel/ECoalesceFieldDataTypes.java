package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceFieldDataTypes
{
    // StringType (1),
    // DateTimeType (2),
    // UriType (3),
    // BinaryType (4),
    // BooleanType (5),
    // IntegerType (6),
    // GuidType (7),
    // GeocoordinateType (8),
    // FileType (9),
    // GeocoordinateListType (10);

    StringType(1, "string"),
    DateTimeType(2, "datetime"),
    UriType(3, "uri"),
    BinaryType(4, "binary"),
    BooleanType(5, "boolean"),
    IntegerType(6, "integer"),
    GuidType(7, "guid"),
    GeocoordinateType(8, "geocoordinate"),
    FileType(9, "file"),
    GeocoordinateListType(10, "geocoordinatelist");

    private int value;
    private String label;

    private ECoalesceFieldDataTypes(int value)
    {
        this.value = value;
    }

    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<Integer, ECoalesceFieldDataTypes> codeToStatusMapping;

    private ECoalesceFieldDataTypes(int code, String label)
    {
        this.value = code;
        this.label = label;
    }

    public static ECoalesceFieldDataTypes getStatus(int code)
    {
        if (codeToStatusMapping == null)
        {
            initMapping();
        }
        return codeToStatusMapping.get(code);
    }

    private static void initMapping()
    {
        codeToStatusMapping = new HashMap<Integer, ECoalesceFieldDataTypes>();
        for (ECoalesceFieldDataTypes s : values())
        {
            codeToStatusMapping.put(s.value, s);
        }
    }

    public int getValue()
    {
        return value;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("LinkTypes");
        sb.append("{code=").append(value);
        sb.append(", label='").append(label);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args)
    {
        System.out.println(ECoalesceFieldDataTypes.StringType);
        System.out.println(ECoalesceFieldDataTypes.getStatus(0));
    }

    public ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForCoalesceType(String coalesceType)
    {
        switch (coalesceType.toUpperCase()) {

        case "BINARY":
            return ECoalesceFieldDataTypes.BinaryType;

        case "BOOLEAN":
            return ECoalesceFieldDataTypes.BooleanType;

        case "DATETIME":
            return ECoalesceFieldDataTypes.DateTimeType;

        case "GEOCOORDINATE":
            return ECoalesceFieldDataTypes.GeocoordinateType;

        case "GEOCOORDINATELIST":
            return ECoalesceFieldDataTypes.GeocoordinateListType;

        case "GUID":
            return ECoalesceFieldDataTypes.GuidType;

        case "INTEGER":
            return ECoalesceFieldDataTypes.IntegerType;

        case "URI":
            return ECoalesceFieldDataTypes.UriType;

        case "FILE":
            return ECoalesceFieldDataTypes.FileType;

        default:
            return ECoalesceFieldDataTypes.StringType;
        }
    }

    public ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForSQLType(String sqlType)
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
