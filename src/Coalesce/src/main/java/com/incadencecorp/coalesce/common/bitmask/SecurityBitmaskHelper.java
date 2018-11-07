/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.common.bitmask;

/**
 * Helper methods for working with bitmasks.
 * 
 * @author n78554
 */
public final class SecurityBitmaskHelper {

    private static final char ON = '1';
    private static final char OFF = '0';
    
    private SecurityBitmaskHelper()
    {
        // Do Nothing
    }

    /**
     * 
     * @param mask
     * @return a string of 0(s) and 1(s).
     */
    public static String toString(final boolean[] mask)
    {
        StringBuilder sb = new StringBuilder();

        for (boolean tick : mask)
        {
            sb.append((tick) ? ON : OFF);
        }

        return sb.toString();
    }

    /**
     * 
     * @param mask a string of 0(s) and 1(s).
     * @return an array of booleans
     */
    public static boolean[] fromString(final String mask)
    {
        boolean[] results;

        if (mask != null)
        {
            results = new boolean[mask.length()];

            for (int ii = 0; ii < mask.length(); ii++)
            {
                results[ii] = (mask.charAt(ii) == ON);
            }
        }
        else
        {
            results = new boolean[] {};
        }

        return results;
    }

}
