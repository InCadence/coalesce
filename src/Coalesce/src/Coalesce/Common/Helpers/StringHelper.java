package Coalesce.Common.Helpers;

public class StringHelper {

    // Make static class
    private StringHelper() { }

    public static boolean IsNullOrEmpty(String ... values){

    	for (String value : values) {
    		if (value == null || value.trim().equals("")) {
    			return true;
    		}
    	}

    	return false;
    }
    
}
