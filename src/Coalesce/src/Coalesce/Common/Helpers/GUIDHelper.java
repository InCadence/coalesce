package Coalesce.Common.Helpers;

import java.util.UUID;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class GUIDHelper {

    // private static String MODULE_NAME = "Coalesce.Common.Helpers.GUIDHelper";

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    public static boolean IsValid(String value)
    {
        try
        {
            if (value == null) return false;
            
            if (GUIDHelper.HasSurroundingBrackets(value)) value = value.replaceAll("[{}]", "");
                    
            UUID.fromString(value);

            return value.replaceAll("[-]", "").length() == 32;
        }
        catch (IllegalArgumentException ex)
        {
            // Invalid UUID
            return false;
        }
    }

    public static boolean HasBrackets(String value)
    {
        // Is Valid?
        if (!GUIDHelper.IsValid(value)) return false;

        // Doesn't have brackets?
        return GUIDHelper.HasSurroundingBrackets(value);
    }

    public static String AddBrackets(String value)
    {
        if (!GUIDHelper.IsValid(value)) return null;

        if (!GUIDHelper.HasSurroundingBrackets(value))
        {
            // No; Add Brackets
            value = "{" + value + "}";
        }

        return value.toUpperCase();
    }

    public static String RemoveBrackets(String value)
    {
        if (!GUIDHelper.IsValid(value)) return null;

        return value.replaceAll("[{}]", "").toUpperCase();
    }

    public static UUID GetGuid(String value)
    {
        if (GUIDHelper.IsValid(value))
        {
            return UUID.fromString(value.replaceAll("[{}]", ""));
        }
        else
        {
            return null;
        }
    }

    public static String GetGuidString(UUID value)
    {
        return GetGuidString(value, false);
    }

    public static String GetGuidString(UUID value, boolean withBrackets)
    {
        if (value == null) return null;
        
        if (withBrackets)
        {
            return AddBrackets(value.toString());
        }
        else
        {
            return value.toString().toUpperCase();
        }
    }

    // -----------------------------------------------------------------------//
    // Protected and Private Shared Methods
    // -----------------------------------------------------------------------//

    private static boolean HasSurroundingBrackets(String value)
    {
        if (value == null) return false;

        return (value.startsWith("{") && value.endsWith("}"));
    }

}
