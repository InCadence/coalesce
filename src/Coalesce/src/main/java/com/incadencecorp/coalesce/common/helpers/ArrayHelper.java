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

import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Factory class for manipulating arrays.
 *
 * @author n78554
 */
public final class ArrayHelper {

    /**
     * @param values
     * @return an array of double from the String array argument.
     */
    public static double[] toDoubleArray(String[] values)
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
    public static float[] toFloatArray(String[] values)
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
    public static int[] toIntegerArray(String[] values)
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
    public static long[] toLongArray(String[] values)
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
    public static boolean[] toBooleanArray(String[] values)
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
    public static UUID[] toUUIDArray(String[] values)
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
    public static String[] toStringArray(boolean[] values)
    {
        String[] results = null;

        if (values != null)
        {

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
    public static String[] toStringArray(long[] values)
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
    public static String[] toStringArray(double[] values)
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
    public static String[] toStringArray(int[] values)
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
    public static String[] toStringArray(float[] values)
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
    public static String[] toStringArray(UUID[] values)
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
    public static String[] toStringArray(Set<UUID> values)
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
    public static String[] toStringArray(List<UUID> values)
    {
        String[] results = new String[values.size()];

        for (int ii = 0; ii < values.size(); ii++)
        {
            results[ii] = GUIDHelper.getGuidString(values.get(ii));
        }

        return results;
    }

    /**
     * Searching a list of objects for a given key.
     *
     * @param list
     * @param key
     * @return the object if found; otherwise <code>null</code>.
     */
    public static <T extends CoalesceObject> T getItem(List<T> list, String key)
    {
        for (T item : list)
        {
            if (item.getKey().equalsIgnoreCase(key))
            {
                return item;
            }
        }

        return null;
    }

    /**
     * @return a list collections of string as a list all lowercased.
     */
    public static List<String> tolowerCase(Collection<String> strings)
    {
        return strings.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    /**
     * @return an array of string all lowercased.
     */
    public static String[] tolowerCase(String[] strings)
    {
        return Arrays.stream(strings).map(String::toLowerCase).collect(Collectors.toList()).toArray(new String[strings.length]);
    }

    /**
     * @param arrayToSplit array to split into chunks
     * @param chunkSize    size of the chunks
     * @param generator    initialization method for creating the array of chunks
     * @param <A>          type of the array
     * @return the array divided into chunks.
     */
    public static <A> A[][] createChunks(A[] arrayToSplit, int chunkSize, IntFunction<A[][]> generator)
    {
        // Determine size of last array
        int rest = arrayToSplit.length % chunkSize;

        // Determine number of chunks
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);

        A[][] arrays = generator.apply(chunks);

        // Create Arrays
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++)
        {
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0)
        {
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }

        return arrays;
    }
}
