/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.util;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This utility class compresses and decompresses Strings and CoalesceEntities.
 *
 * @author Derek Clemenzi
 */
public final class CoalesceCompressionUtil {

    private CoalesceCompressionUtil()
    {
        // Do Nothing
    }

    /**
     * @param entity to compress
     * @return {@link #compress(String)} passing in the XML of the provided entity.
     * @throws IOException on error
     */
    public static byte[] compress(CoalesceEntity entity) throws IOException
    {
        return compress(entity.toXml());
    }

    /**
     * @param str to compress
     * @return the compressed bytes for the provided String.
     * @throws IOException on error
     */
    public static byte[] compress(String str) throws IOException
    {
        byte[] compressed;

        if (!StringHelper.isNullOrEmpty(str))
        {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream())
            {
                try (GZIPOutputStream gzip = new GZIPOutputStream(out))
                {
                    gzip.write(str.getBytes());
                }

                compressed = out.toByteArray();
            }
        }
        else
        {
            compressed = new byte[0];
        }

        return compressed;
    }

    /**
     * @param compressed bytes to decompress
     * @param clazz targeted entity type
     * @param <T> targeted entity type
     * @return the entity initialized with the results of {@link #decompress(byte[])}.
     * @throws CoalesceException on error
     */
    public static <T extends CoalesceEntity> T decompress(byte[] compressed, Class<T> clazz) throws CoalesceException
    {
        try
        {
            T entity = clazz.newInstance();
            entity.initialize(decompress(compressed));
            return entity;
        }
        catch (IllegalAccessException | InstantiationException | IOException e)
        {
            throw new CoalesceException(e);
        }
    }

    /**
     * @param compressed bytes to decompress
     * @return if provided bytes were compressed w/ gzip then the decompressed String representation; otherwise the provided
     * bytes as a String.
     * @throws IOException on error
     */
    public static String decompress(byte[] compressed) throws IOException
    {
        if (isCompressed(compressed))
        {
            StringBuilder sb = new StringBuilder();

            try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed))
            {
                try (GZIPInputStream gzip = new GZIPInputStream(bis))
                {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(gzip, "UTF-8")))
                    {
                        String line;
                        while ((line = br.readLine()) != null)
                        {
                            sb.append(line);
                        }
                    }
                }
            }

            return sb.toString();
        }
        else
        {
            return new String(compressed);
        }
    }

    private static boolean isCompressed(byte[] bytes) throws IOException
    {
        return (bytes != null) && (bytes.length >= 2) && ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1]
                == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
    }
}
