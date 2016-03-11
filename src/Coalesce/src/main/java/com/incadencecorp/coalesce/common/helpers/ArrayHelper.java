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

package com.incadencecorp.coalesce.common.helpers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Factory class for manipulating arrays.
 * 
 * @author n78554
 *
 */
public final class ArrayHelper {

    /**
     * @param values
     * @return an array of double from the String array argument.
     */
    public static final double[] toDoubleArray(String[] values)
    {
        double[] results = new double[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Double.valueOf(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of float from the String array argument.
     */
    public static final float[] toFloatArray(String[] values)
    {
        float[] results = new float[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Float.valueOf(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of int from the String array argument.
     */
    public static final int[] toIntegerArray(String[] values)
    {
        int[] results = new int[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Integer.valueOf(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of long from the String array argument.
     */
    public static final long[] toLongArray(String[] values)
    {
        long[] results = new long[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Long.valueOf(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of boolean from the String array argument.
     */
    public static final boolean[] toBooleanArray(String[] values)
    {
        boolean[] results = new boolean[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Boolean.valueOf(values[ii]);
        }

        return results;
    }
    
    /**
     * @param values
     * @return an array of UUID from the String array argument.
     */
    public static final UUID[] toUUIDArray(String[] values)
    {
        UUID[] results = new UUID[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = GUIDHelper.getGuid(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(boolean[] values)
    {
        String[] results = null;
        
        if (values != null) {

            results = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                results[ii] = Boolean.toString(values[ii]);
            }
            
        }

        return results;
    }
    
    /**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(long[] values)
    {
        String[] results = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Long.toString(values[ii]);
        }

        return results;
    }

/**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(double[] values)
    {
        String[] results = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Double.toString(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(int[] values)
    {
        String[] results = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Integer.toString(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(float[] values)
    {
        String[] results = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = Float.toString(values[ii]);
        }

        return results;
    }

    /**
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(UUID[] values)
    {
        String[] results = new String[values.length];

        for (int ii = 0; ii < values.length; ii++)
        {
            results[ii] = GUIDHelper.getGuidString(values[ii]);
        }

        return results;
    }
    
    /** 
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(Set<UUID> values)
    {
        String[] results = new String[values.size()];

        int ii = 0;
        
        for (UUID value : values)
        {
            results[ii++] = GUIDHelper.getGuidString(value);
        }

        return results;
    }
    
    /** 
     * @param values
     * @return an array of strings
     */
    public static final String[] toStringArray(List<UUID> values)
    {
        String[] results = new String[values.size()];

        for (int ii = 0; ii < values.size(); ii++)
        {
            results[ii] = GUIDHelper.getGuidString(values.get(ii));
        }

        return results;
    }

}
