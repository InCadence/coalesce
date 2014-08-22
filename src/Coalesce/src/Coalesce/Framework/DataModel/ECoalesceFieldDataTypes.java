package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

//import sun.org.mozilla.javascript.internal.ObjToIntMap.Iterator;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public enum ECoalesceFieldDataTypes {
//  StringType (1), 
//  DateTimeType (2), 
//  UriType (3), 
//  BinaryType (4), 
//  BooleanType (5), 
//  IntegerType (6), 
//  GuidType (7), 
//  GeocoordinateType (8), 
//  FileType (9), 
//  GeocoordinateListType (10);
	
  StringType (1, "StringType"), 
  DateTimeType (2, "DateTimeType"), 
  UriType (3, "UriType"), 
  BinaryType (4, "BinaryType"), 
  BooleanType (5, "BooleanType"), 
  IntegerType (6, "IntegerType"), 
  GuidType (7, "GuidType"), 
  GeocoordinateType (8, "GeocoordinateType"), 
  FileType (9, "FileType"), 
  GeocoordinateListType (10, "GeocoordinateListType");
  
  private int value;
  private String label;
  
  private ECoalesceFieldDataTypes(int value){
  	this.value= value;
  }
  
  /**
   * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
   */
  private static Map<Integer, ECoalesceFieldDataTypes> codeToStatusMapping;

  private ECoalesceFieldDataTypes(int code, String label){
      this.value = code;
      this.label = label;
  }

  public static ECoalesceFieldDataTypes getStatus(int code) {
      if (codeToStatusMapping == null) {
          initMapping();
      }
      return codeToStatusMapping.get(code);
  }

  private static void initMapping() {
      codeToStatusMapping = new HashMap<Integer, ECoalesceFieldDataTypes>();
      for (ECoalesceFieldDataTypes s : values()) {
          codeToStatusMapping.put(s.value, s);
      }
  }

  public int getValue() {
      return value;
  }

  public String getLabel() {
      return label;
  }

  @Override
  public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("LinkTypes");
      sb.append("{code=").append(value);
      sb.append(", label='").append(label);
      sb.append('}');
      return sb.toString();
  }

  public static void main(String[] args) {
      System.out.println(ECoalesceFieldDataTypes.StringType);
      System.out.println(ECoalesceFieldDataTypes.getStatus(0));
  }
  
	public static ECoalesceFieldDataTypes GetELinkTypeForLabel(String value)
	{
		switch (value.toLowerCase()) {
		case "StringType":
			return ECoalesceFieldDataTypes.StringType;
		case "DateTimeType":
			return ECoalesceFieldDataTypes.DateTimeType;
		case "UriType":
			return ECoalesceFieldDataTypes.UriType;
		case "BinaryType":
			return ECoalesceFieldDataTypes.BinaryType;
		case "BooleanType":
			return ECoalesceFieldDataTypes.BooleanType;
		case "IntegerType":
			return ECoalesceFieldDataTypes.IntegerType;
		case "GuidType":
			return ECoalesceFieldDataTypes.GuidType;
		case "GeocoordinateType":
			return ECoalesceFieldDataTypes.GeocoordinateType;
		case "FileType":
			return ECoalesceFieldDataTypes.FileType;
		case "GeocoordinateListType":
			return ECoalesceFieldDataTypes.GeocoordinateListType;
		default:
			return ECoalesceFieldDataTypes.StringType;
		}
	}
	
  public int GetELinkTypeCodeForLabel(String value){
  	switch (value.toLowerCase()){
	  	case "StringType":
	  		return 1;
	  	case "DateTimeType":
	  		return 2;
	  	case "UriType":
	  		return 3;
	  	case "BinaryType":
	  		return 4;
	  	case "BooleanType":
	  		return 5;
	  	case "IntegerType":
	  		return 6;
	  	case "GuidType":
	  		return 7;
	  	case "GeocoordinateType":
	  		return 8;
	  	case "FileType":
	  		return 9;
	  	case "GeocoordinateListType":
	  		return 10;
	  	default:
	  		return 1;
  	}

  }

  public String GetELinkTypeLabelForCode(int value){
  	switch (value){
	  	case 1:
	  		return "StringType";
	  	case 2: 
	  		return "DateTimeType";
	  	case 3:
	  		return "UriType";
	  	case 4:
	  		return "BinaryType";
	  	case 5:
	  		return "BooleanType";
	  	case 6:
	  		return "IntegerType";
	  	case 7:
	  		return "GuidType";
	  	case 8:
	  		return "GeocoordinateType";
	  	case 9:
	  		return "FileType";
	  	case 10:
	  		return "GeocoordinateListType";
	  	default: 
	  		return "Undefined";
  	}
  }

  public ECoalesceFieldDataTypes GetELinkTypeTypeForCode(int value){
  	switch (value){
	  	case 1:
	  		return ECoalesceFieldDataTypes.StringType;
	  	case 2:
	  		return ECoalesceFieldDataTypes.DateTimeType;
	  	case 3: 
	  		return ECoalesceFieldDataTypes.UriType;
	  	case 4:
	  		return ECoalesceFieldDataTypes.BinaryType;
	  	case 5:
	  		return ECoalesceFieldDataTypes.BooleanType;
	  	case 6:
	  		return ECoalesceFieldDataTypes.IntegerType;
	  	case 7:
	  		return ECoalesceFieldDataTypes.GuidType;
	  	case 8:
	  		return ECoalesceFieldDataTypes.GeocoordinateType;
	  	case 9:
	  		return ECoalesceFieldDataTypes.FileType;
	  	case 10:
	  		return ECoalesceFieldDataTypes.GeocoordinateListType;
	  	default: 
	  		return ECoalesceFieldDataTypes.StringType;
  	}
  }
  
  public String GetELinkTypeLabelForType(ECoalesceFieldDataTypes Type) { 
  	
      switch(Type){

          case StringType:
              return "StringType";

          case DateTimeType:
              return "DateTimeType";

          case UriType:
              return "UriType";

          case BinaryType:
              return "BinaryType";

          case BooleanType:
              return "BooleanType";

          case IntegerType:
              return "IntegerType";

          case GuidType:
              return "GuidType";

          case GeocoordinateType:
              return "GeocoordinateType";

          case FileType:
              return "FileType";

          case GeocoordinateListType:
              return "GeocoordinateListType";

          default:
              return "StringType";
      }

  }

  public ECoalesceFieldDataTypes GetReciprocalLinkType(ECoalesceFieldDataTypes LinkType) { 
      try{
          switch(LinkType){

          case StringType:
                  return ECoalesceFieldDataTypes.StringType;

          case DateTimeType:
                  return ECoalesceFieldDataTypes.DateTimeType;

              case UriType:
                  return ECoalesceFieldDataTypes.UriType;

              case BinaryType:
                  return ECoalesceFieldDataTypes.BinaryType;

              case BooleanType:
                  return ECoalesceFieldDataTypes.BooleanType;

              case IntegerType:
                  return ECoalesceFieldDataTypes.IntegerType;

              case GuidType:
                  return ECoalesceFieldDataTypes.GuidType;

              case GeocoordinateType:
                  return ECoalesceFieldDataTypes.GeocoordinateType;

              case FileType:
                  return ECoalesceFieldDataTypes.FileType;

              case GeocoordinateListType:
                  return ECoalesceFieldDataTypes.GeocoordinateListType;

              default:
                  return ECoalesceFieldDataTypes.StringType;
          }

      }catch(Exception ex){
          // Log
          CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.ECoalesceFieldDataTypes");

          // return Undefined
          return ECoalesceFieldDataTypes.StringType;
      }
  }
  
}
