package Coalesce.Common.Helpers;

import java.util.UUID;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import unity.core.runtime.CallResult.ValueResult;

public class GUIDHelper {

    private static String MODULE = "Coalesce.Common.Helpers.GUIDHelper";

    // Make static class
    private GUIDHelper() {
    }

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    public static String IsValid(String value)
    {
        try {

        	String stripped = GUIDHelper.GetValidStrippedString(value);           
        	if (stripped == null) return null;
        	
            @SuppressWarnings("unused")
            UUID g = UUID.fromString(stripped);

            String formattedString = value.toUpperCase();

            return formattedString;

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED, ex, GUIDHelper.MODULE);
            return null;
        }
    }

    public static ValueResult<String> HasBrackets(String value)
    {
        try {
            // return true if it's a Valid GUID and it has brackets.

            // Is Valid?
            String validGUID = GUIDHelper.IsValid(value);
            if (validGUID == null) return GUIDHelper.NotGuid();

            // Doesn't have brackets?
            if (GUIDHelper.HasSurroundingBrackets(validGUID)) {
                return new ValueResult<String>(validGUID, CallResult.successCallResult);
            } else {
                return new ValueResult<String>(validGUID, new CallResult(CallResults.FAILED, "No brackets", GUIDHelper.MODULE));
            }

        } catch (Exception ex) {
            return new ValueResult<String>(null, new CallResult(CallResults.FAILED_ERROR, ex, GUIDHelper.MODULE));
        }
    }


    public static String AddBrackets(String value)
    {
        try {

        	String validGuid = GUIDHelper.IsValid(value);
            if (validGuid == null) return null;
                
            if (!GUIDHelper.HasSurroundingBrackets(validGuid)) {
                
            	// No; Add Brackets
            	validGuid = "{" + validGuid + "}";

            }

            return validGuid;

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, GUIDHelper.MODULE);
            return null;
        }
    }

    public static String RemoveBrackets(String value)
    {
        try {
            // Is Valid?
            String validGuid = GUIDHelper.IsValid(value);
            if (validGuid == null) return null;
             
            validGuid = validGuid.replaceAll("[{}]", "");

            return validGuid;

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, GUIDHelper.MODULE);
            return null;
        }
    }

    public static UUID GetGuid(String value)
    {
        try {
        	String stripped = GetValidStrippedString(value);
        	if (stripped == null) return null;
        	
            UUID guid = UUID.fromString(value.replaceAll("[{}]", ""));

            return guid;
            
        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED, "Failed to construct a guid", GUIDHelper.MODULE);
            return null;
        }
    }

    public static String GetGuidString(UUID value)
    {
        return GetGuidString(value, false);
    }

    public static String GetGuidString(UUID value, boolean withBrackets)
    {
        try {
        	String guidString = value.toString();
            if (withBrackets) {
            	
            	return AddBrackets(guidString);

            } else {
            	return GUIDHelper.IsValid(guidString);
            }

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, GUIDHelper.MODULE);

            return null;
        }
    }

    // -----------------------------------------------------------------------//
    // Protected and Private Shared Methods
    // -----------------------------------------------------------------------//

    private static boolean HasSurroundingBrackets(String value) {
    	return (value.startsWith("{") && value.endsWith("}"));
    }
    
    private static ValueResult<String> NotGuid() {
    	return new ValueResult<String>(null, new CallResult(CallResults.FAILED, "Not a guid", GUIDHelper.MODULE));
    }
    
    private static String GetValidStrippedString(String value) {
    	
        // Check matching brackets
        boolean openingBracket = value.startsWith("{");
        boolean closingBracket = value.endsWith("}");
        if (openingBracket ^ closingBracket) return null;
        
        String stripped = value.replaceAll("[{}]",  "");
        
        // Check length
        if (stripped.replace("-", "").length() != 32) return null;

        return stripped;
        
    }

}
