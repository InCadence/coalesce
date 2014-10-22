package com.incadencecorp.coalesce.common.helpers;

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
 * Provides helper methods for manipulating and testing {@link String} values.
 * 
 * @author InCadence
 *
 */
public final class StringHelper {

    // Make static class
    private StringHelper()
    {
        // Do Nothing
    }

    /**
     * Returns <code>true</code> if any of the values are either <code>null</code> or an empty string.
     * 
     * @param values the values to check.
     * @return <code>true</code> if any of the values are either <code>null</code>or an empty string.
     */
    public static boolean isNullOrEmpty(String... values)
    {

        for (String value : values)
        {
            if (value == null || value.trim().equals(""))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the new {@link String} value with all leading and trailing parentheses removed.
     * 
     * @param value the string to be trimmed.
     * @return the new {@link String} value with all leading and trailing parentheses removed.
     */
    public static String trimParentheses(String value)
    {

        while (value.startsWith("(") || value.endsWith(")"))
        {
            value = value.replaceAll("^\\(|\\)$", "");
        }

        return value;

    }

}
