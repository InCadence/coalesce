package com.proto.irems.data.persistor;

public class CoalesceTable {
    public static String gettableNameForObjectType(String objectType){
    	String tableName="";
    	
    	switch(objectType){
	    	case "entity":
	    		tableName="coalesceentity";
	    		break;
            case "entitytemplate":
                tableName = "[CoalesceEntityTemplate]";
            	break;
            case "field":
                tableName = "[CoalesceField]";
            	break;
            case "fielddefinition":
                tableName = "[CoalesceFieldDefinition]";
                break;
            case "fieldhistory":
                tableName = "[CoalesceFieldHistory]";
                break;
            case "linkage":
                tableName = "[CoalesceLinkage]";
                break;
            case "linkagesection":
                tableName = "[CoalesceLinkageSection]";
                break;
            case "record":
                tableName = "[CoalesceRecord]";
                break;
            case "recordset":
                tableName = "[CoalesceRecordset]";
                break;
            case "section":
                tableName = "[CoalesceSection]";
                break;
            default:
                tableName=null;
    	}
    	return tableName;
    }

}
