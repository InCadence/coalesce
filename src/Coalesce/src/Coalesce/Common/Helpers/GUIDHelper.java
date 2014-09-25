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

/**
 * Provides helper methods for working with GUIDs. The expected behavior of the functions contained in this class match the
 * behavior of the System.GUID class in .NET
 * 
 * @author InCadence
 *
 */
public class GUIDHelper {

    // -----------------------------------------------------------------------//
    // Public Shared Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns <code>true</code> if this value is a valid GUID. There must be matching {} or none at all, the characters used
     * must be valid hex characters and the length of the must equal 32 characters after removing all '-'.
     * 
     * @param value the value to be tested
     * @return <code>true</code> if the value is a valid GUID.
     */
    public static boolean isValid(String value)
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
        if (!GUIDHelper.isValid(value)) return false;

        // Doesn't have brackets?
        return GUIDHelper.HasSurroundingBrackets(value);
    }

    public static String AddBrackets(String value)
    {
        if (!GUIDHelper.isValid(value)) return null;

        if (!GUIDHelper.HasSurroundingBrackets(value))
        {
            // No; Add Brackets
            value = "{" + value + "}";
        }

        return value.toUpperCase();
    }

    public static String RemoveBrackets(String value)
    {
        if (!GUIDHelper.isValid(value)) return null;

        return value.replaceAll("[{}]", "").toUpperCase();
    }

    public static UUID GetGuid(String value)
    {
        if (GUIDHelper.isValid(value))
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
