package com.incadencecorp.coalesce.common.helpers;

/**
 * This is a utility class for mapping Coalesce objects to database tables.
 */
public final class CoalesceTableHelper {

    private CoalesceTableHelper()
    {
        // Do Nothing
    }

    /**
     * Returns the Coalesce object database name matching the given parameters.
     * 
     * @param objectType the Coalesce object
     * @return tableName Coalesce object database name
     */
    public static String getTableNameForObjectType(String objectType)
    {
        String tableName = "";

        switch (objectType.trim().toLowerCase()) {
        case "entity":
            tableName = "CoalesceEntity";
            break;
        case "entitytemplate":
            tableName = "CoalesceEntityTemplate";
            break;
        case "field":
            tableName = "CoalesceField";
            break;
        case "fielddefinition":
            tableName = "CoalesceFieldDefinition";
            break;
        case "fieldhistory":
            tableName = "CoalesceFieldHistory";
            break;
        case "linkage":
            tableName = "CoalesceLinkage";
            break;
        case "linkagesection":
            tableName = "CoalesceLinkageSection";
            break;
        case "record":
            tableName = "CoalesceRecord";
            break;
        case "recordset":
            tableName = "CoalesceRecordset";
            break;
        case "section":
            tableName = "CoalesceSection";
            break;
        default:
            tableName = null;
        }
        return tableName;
    }

}
