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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.PropertyLoader;

/**
 * This cache is responsible for retrieving hashes for list of strings used to
 * generate bit mask to determine if changes were made.
 * 
 * @author n78554
 */
public class SecurityBitmaskHashes {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityBitmaskHashes.class);

    private PropertyLoader loader;
    private Map<String, SecurityBitmaskHashInfo> hashes = new ConcurrentHashMap<>();
    private String algorithm = "SHA1";

    /**
     * Sets the property loader used to retrieve and store hashes.
     * 
     * @param loader
     */
    public void setPropertyLoader(PropertyLoader loader)
    {
        this.loader = loader;

        String value = loader.getProperty(CoalesceParameters.HASH_ALGORITHM);

        // Configured?
        if (!StringHelper.isNullOrEmpty(value))
        {
            algorithm = value;
        }
    }

    /**
     * Verifies that the options of the specified creator have not changed.
     * 
     * @param creator
     * @throws CoalesceException
     */
    public void verifyCreator(SecurityBitmaskCreator creator) throws CoalesceException
    {
        if (StringHelper.isNullOrEmpty(creator.getName())) {
            throw new IllegalArgumentException("Creator's name is null");
        }
        
        verifyValues(creator.getName(), creator.getOptions());
    }

    /**
     * Verifies that the collection of Strings have not changed since the last
     * time a hash was computed.
     * 
     * @param name
     * @param values
     * @throws CoalesceException
     */
    public void verifyValues(String name, Collection<String> values) throws CoalesceException
    {
        verifyValues(name, values.toArray(new String[values.size()]));
    }

    /**
     * Verifies that the array of Strings have not changed since the last time a
     * hash was computed.
     * 
     * @param name
     * @param values
     * @throws CoalesceException
     */
    public void verifyValues(String name, String... values) throws CoalesceException
    {
        SecurityBitmaskHashInfo info = getHashInfo(name);

        // Registered?
        if (info.getCount() != 0)
        {
            if (info.getCount() > values.length)
            {
                throw new CoalesceException(String.format(CoalesceErrors.ERR_HASH_VALUES_RMV, name));
            }

            // Yes; Compute Hash
            BigInteger hash = computeHash(joinEscapedArray(values, 0, info.getCount()));

            // Validate
            if (info.getHash().compareTo(hash) != 0)
            {
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("\tOriginal Hash: {}", info.getHash().toString());
                    LOGGER.trace("\tNew Hash: {}", hash.toString());
                }

                throw new CoalesceException(String.format(CoalesceErrors.ERR_HASH_VALUES_MODIFIED, name));
            }
        }

        // Size Changed?
        if (info.getCount() < values.length)
        {
            // No; Compute Hash
            BigInteger hash = computeHash(joinEscapedArray(values));

            if (info.getCount() != 0)
            {
                LOGGER.warn(String.format(CoalesceErrors.ERR_HASH_VALUES_ADD, values.length - info.getCount(),  name));
                
                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("\tOriginal Hash: {}", info.getHash().toString());
                    LOGGER.trace("\tNew Hash: {}", hash.toString());
                }
            }

            // Register
            hashes.put(name, new SecurityBitmaskHashInfo(name, hash, values.length));

            if (loader != null)
            {
                // Save Hash
                loader.setProperty(String.format(CoalesceParameters.HASH_COUNT, name), String.valueOf(values.length));
                loader.setProperty(String.format(CoalesceParameters.HASH_VALUE, name), hash.toString());
            }

        }

    }

    /**
     * 
     * @param value
     * @return hash information for the provided identifier.
     */
    public SecurityBitmaskHashInfo getHashInfo(String value)
    {
        SecurityBitmaskHashInfo result;

        // Cached?
        if (hashes.containsKey(value))
        {
            // Yes; Read from Cache
            result = hashes.get(value);
        }
        else if (loader != null)
        {
            // No; Read from Loader
            String countStr = loader.getProperty(String.format(CoalesceParameters.HASH_COUNT, value));
            String hashStr = loader.getProperty(String.format(CoalesceParameters.HASH_VALUE, value));

            // Found?
            if (hashStr != null)
            {
                // Yes; Store Hash Info
                result = new SecurityBitmaskHashInfo(value, new BigInteger(hashStr), Integer.parseInt(countStr));

                hashes.put(value, result);
            }
            else
            {
                result = new SecurityBitmaskHashInfo(value, BigInteger.ZERO, 0);
            }
        }
        else
        {
            result = new SecurityBitmaskHashInfo(value, BigInteger.ZERO, 0);
        }

        return result;
    }

    private String joinEscapedArray(String[] values)
    {
        return joinEscapedArray(values, 0, values.length);
    }

    private String joinEscapedArray(String[] values, int startIdx, int endIdx)
    {

        String results;

        if (values != null && values.length > 0)
        {

            String[] escaped = new String[values.length];

            for (int ii = 0; ii < values.length; ii++)
            {
                escaped[ii] = StringEscapeUtils.escapeCsv(values[ii]);
            }

            results = StringUtils.join(escaped, ",", startIdx, endIdx);
        }
        else
        {
            results = null;
        }

        return results;
    }

    private BigInteger computeHash(String value) throws CoalesceException
    {

        BigInteger results;

        try
        {
            MessageDigest m;
            m = MessageDigest.getInstance(algorithm);
            m.update(value.getBytes());

            results = new BigInteger(1, m.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new CoalesceException("No Such Algirthm", e);
        }

        return results;
    }

}
