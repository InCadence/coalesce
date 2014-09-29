package Coalesce.Framework.Persistance;


public class CoalesceTable {
    /**
     * Returns the Coalesce object database name matching the given parameters.
     * @param objectType the Coalesce object
     * @return tableName Coalesce object database name
     */
    public static String gettableNameForObjectType(String objectType){
    	String tableName=""; 
    	
    	switch(objectType.trim().toLowerCase()){
	    	case "entity":
	    		tableName="CoalesceEntity";
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
                tableName=null;
    	}
    	return tableName;
    }

}
