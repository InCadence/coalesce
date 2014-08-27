package Coalesce.Common.Helpers;

public class StringHelper {

    // Make static class
    private StringHelper() { }

    public static boolean IsNullOrEmpty(String ... values){

    	for (String value : values) {
    		if (value == null || value.equals("")) {
    			return true;
    		}
    	}

    	return false;
    }
    
    public static String TrimParentheses(String value) {
        
        while (value.startsWith("(") || value.endsWith(")")) {
            value = value.replaceAll("^\\(|\\)$", "");
        }

        return value;
            
    }
}
