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

package com.incadencecorp.coalesce.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.bitmask.SecurityBitmaskCreator;
import com.incadencecorp.coalesce.common.bitmask.SecurityBitmaskHashes;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import com.incadencecorp.unity.common.connectors.MemoryConnector;

/**
 * These unit test ensure proper behavior of the
 * {@link SecurityBitmaskHashes}.
 * 
 * @author n78554
 */
public class SecurtyBitmaskHashesTest {

    private static final String NAME = "unittest";

    /**
     * Ensures that if any of the options change validation will fail.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testHashValidationFailure() throws Exception
    {

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(new PropertyLoader(new MemoryConnector(), "hash.properties"));

        cache.verifyValues("Derek", "A", "B", "C", "D");
        cache.verifyValues("Derek", "C", "B", "F", "R");

    }

    /**
     * Ensures that as long as the original values remain the same you can add
     * additional options and the hash will be recalculated.
     * 
     * @throws Exception
     */
    @Test
    public void testHashOptionsIncreased() throws Exception
    {

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(new PropertyLoader(new MemoryConnector(), "hash.properties"));

        cache.verifyValues(NAME, "A", "B");

        BigInteger original = cache.getHashInfo(NAME).getHash();

        cache.verifyValues(NAME, "A", "B", "C");

        BigInteger updated = cache.getHashInfo(NAME).getHash();

        // Ensure the hash was updated
        Assert.assertNotEquals(0, original.compareTo(updated));
    }

    /**
     * Ensures that if the original options change it will fail validation.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testHashOptionsIncreasedFailure() throws Exception
    {
        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(new PropertyLoader(new MemoryConnector(), "hash.properties"));

        cache.verifyValues(NAME, "A", "B");

        cache.verifyValues(NAME, "D", "B", "C");
    }

    /**
     * Ensures that if the options decrease that it will fail validation.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testHashOptionsDecreased() throws Exception
    {
        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(new PropertyLoader(new MemoryConnector(), "hash.properties"));

        cache.verifyValues(NAME, "A", "B", "C");

        cache.verifyValues(NAME, "A", "B");
    }

    /**
     * Ensures that if the options decrease that it will fail validation.
     * 
     * @throws Exception
     */
    @Test
    public void testHashValidationWithoutLoader() throws Exception
    {
        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();

        cache.verifyValues(NAME, "A", "B", "C");
    }

    /**
     * Ensures that hashes are persisted between runs if a non memory connector
     * is specified.
     * 
     * @throws Exception
     */
    @Test
    public void testHashPersistence() throws Exception
    {
        PropertyLoader loader = new PropertyLoader(new FilePropertyConnector("src/main/resources"), "hash.properties");
        loader.setProperty(CoalesceParameters.HASH_ALGORITHM, "SHA1");

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(loader);
        cache.verifyValues(NAME, "A", "B", "C");

        Assert.assertEquals(NAME, cache.getHashInfo(NAME).getName());

    }

    /**
     * Ensures that an exception is thrown if an invalid hash algorithm is
     * specified.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testHashAlgorithmFailure() throws Exception
    {
        PropertyLoader loader = new PropertyLoader(new MemoryConnector(), "hash.properties");
        loader.setProperty(CoalesceParameters.HASH_ALGORITHM, "Bad");

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(loader);
        cache.verifyValues(NAME, "A", "B", "C");
    }

    /**
     * Ensures that if no values are specified then validation will return
     * successful if the hash does not already exists.
     * 
     * @throws Exception
     */
    @Test
    public void testHashNoValues() throws Exception
    {
        PropertyLoader loader = new PropertyLoader(new MemoryConnector(), "hash.properties");
        loader.setProperty(CoalesceParameters.HASH_ALGORITHM, "Bad");

        List<String> options = new ArrayList<String>();

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.setPropertyLoader(loader);
        cache.verifyValues(NAME, options);
    }

    /**
     * Ensures that you can verify the options of a
     * {@link SecurityBitmaskCreator}.
     * 
     * @throws Exception
     */
    @Test
    public void testVerifyCreator() throws Exception
    {
        SecurityBitmaskCreator creator = new SecurityBitmaskCreator("C", "B");
        creator.setName(NAME);

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.verifyCreator(creator);
        cache.verifyValues(NAME, "C", "B");

    }

    /**
     * Ensures that if there is no name specified a
     * {@link IllegalArgumentException} will be thrown.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testVerifyCreatorNullNameFailure() throws Exception
    {
        SecurityBitmaskCreator creator = new SecurityBitmaskCreator("A", "B");

        // Verify Error
        new SecurityBitmaskHashes().verifyCreator(creator);

    }

    /**
     * Ensures that if the options of a creator are modified it will throw an
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void testVerifyCreatorModifiedFailure() throws Exception
    {
        SecurityBitmaskCreator creator = new SecurityBitmaskCreator("A", "B");
        creator.setName(NAME);

        SecurityBitmaskHashes cache = new SecurityBitmaskHashes();
        cache.verifyCreator(creator);

        creator = new SecurityBitmaskCreator("A", "C");
        creator.setName(NAME);

        // Verify Error
        cache.verifyCreator(creator);

    }

}
