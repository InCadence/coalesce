package com.incadencecorp.coalesce.common.helpers;

import java.util.UUID;
import java.util.regex.Pattern;

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
public final class GUIDHelper {

    private static final String REGEX_UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final String REGEX_UUID_BRACES = REGEX_UUID + "|\\{" + REGEX_UUID + "\\}";
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX_UUID_BRACES);

    private GUIDHelper()
    {
        // Do Nothing
    }

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
        return value != null && REGEX_PATTERN.matcher(value).matches();
    }

    /**
     * Returns <code>true</code> if the value is a valid GUID and also has matching surrounding brackets.
     * 
     * @param value the value to check.
     * @return <code>true</code> if the value is a valid GUID and surrounded by matching brackets.
     */
    public static boolean hasBrackets(String value)
    {
        return GUIDHelper.isValid(value) && GUIDHelper.hasSurroundingBrackets(value);
    }

    /**
     * Returns the original value with surrounding brackets added. If the original value was not a valid GUID then
     * <code>null</code> is returned.
     * 
     * @param value the value to add brackets to.
     * @return the original value with surrounding brackets added. If the original value was not a valid GUID then
     *         <code>null</code> is returned.
     */
    public static String addBrackets(String value)
    {
        if (!GUIDHelper.isValid(value)) return null;

        if (!GUIDHelper.hasSurroundingBrackets(value))
        {
            value = "{" + value + "}";
        }

        return value.toUpperCase();
    }

    /**
     * Returns the original value with surrounding brackets removed. If the original value was not a valid GUID then
     * <code>null</code> returned.
     * 
     * @param value the value to remove brackets from.
     * @return the original value with surrounding brackets removed. If the original value was not a valid GUID then
     *         <code>null</code> returned.
     */
    public static String removeBrackets(String value)
    {
        if (!GUIDHelper.isValid(value)) return null;

        return value.replaceAll("[{}]", "").toUpperCase();
    }

    /**
     * Returns the {@link java.util.UUID} representing the value provided. If the value is not a valid GUID then
     * <code>null</code> is returned;
     * 
     * @param value the value to be converted.
     * @return the {@link java.util.UUID} representing the value provided. If the value is not a valid GUID then
     *         <code>null</code> is returned
     */
    public static UUID getGuid(String value)
    {
        if (!GUIDHelper.isValid(value)) return null;

        return UUID.fromString(value.replaceAll("[{}]", ""));

    }

    /**
     * Returns the string representation of the provided GUID without surrounding brackets.
     * 
     * @param value the GUID
     * @return the string representation of the provided GUID without surrounding brackets.
     */
    public static String getGuidString(UUID value)
    {
        return getGuidString(value, false);
    }

    /**
     * Returns the string representation of the provided GUID while conditionally adding surrounding brackets.
     * 
     * @param value the GUID value.
     * @param withBrackets whether to include surrounding brackets.
     * @return the string representation of the provided GUID with conditionally added brackets.
     */
    public static String getGuidString(UUID value, boolean withBrackets)
    {
        if (value == null) return null;

        if (withBrackets)
        {
            return addBrackets(value.toString());
        }
        else
        {
            return value.toString().toUpperCase();
        }
    }
    
    /**
     * Converts an array of Strings to UUIDs
     * 
     * @param values
     * @return an array of UUIDs or throws an IllegalArgumentException if they
     *         are not valid UUIDs.
     * @throws IllegalArgumentException 
     */
    public static UUID[] toUUIDArray(String[] values) throws IllegalArgumentException
    {

        UUID[] result = new UUID[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            result[ii] = GUIDHelper.getGuid(values[ii]);
            
            if (result[ii] == null) {
                throw new IllegalArgumentException("Invalid UUID string: " + values[ii]);
            }
        }

        return result;

    }

    /**
     * Converts an array of UUIDs to Strings
     * 
     * @param values
     * @return an array of strings from the provided UUIDs.
     */
    public static String[] toStringArray(UUID[] values)
    {

        String[] result = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            result[ii] = GUIDHelper.getGuidString(values[ii]);
        }

        return result;
        
    }

    // -----------------------------------------------------------------------//
    // Protected and Private Shared Methods
    // -----------------------------------------------------------------------//

    private static boolean hasSurroundingBrackets(String value)
    {
        return (value.startsWith("{") && value.endsWith("}"));
    }

}
