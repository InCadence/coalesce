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

package com.incadencecorp.coalesce.api;

/**
 * Common Parameters
 *
 * @author n78554
 */
public final class CoalesceParameters {

    private CoalesceParameters()
    {
        // Do Nothing
    }

    private static final String INC = "com.incadencecorp.";
    private static final String COALESCE = INC + "coalesce.";
    private static final String SECURITY = COALESCE + "security.";

    public static final String COALESCE_CONFIG_LOCATION_PROPERTY = "COALESCE_CONFIG_LOCATION";
    public static final String COALESCE_CONFIG_LOCATION =
            System.getProperty(COALESCE_CONFIG_LOCATION_PROPERTY) == null ? "config" : System.getProperty(
                    "COALESCE_CONFIG_LOCATION");

    /*--------------------------------------------------------------------------
    General Properties (com.incadencecorp.%)
    --------------------------------------------------------------------------*/

    /**
     * (URI) Specifies a directory the scanner can use. This can either be
     * absolute "file:/..." or relative from System.getProperty("user.dir").
     */
    public static final String PARAM_DIRECTORY = INC + "directory";

    /**
     * (Integer) When creating filenames from UUIDs this determines the
     * sub-directory which is based of the first X characters of the UUID.
     */
    public static final String PARAM_SUBDIR_LEN = INC + "subdirlen";

    /**
     * (Integer) Sets the number of items to process ina single run.
     */
    public static final String PARAM_BLOCK_SIZE = INC + "blocksize";

    /*--------------------------------------------------------------------------
    Hash Properties (com.incadencecorp.coalesce.security.hash.%)
    --------------------------------------------------------------------------*/

    private static final String HASH = SECURITY + "hash.";

    /**
     * (BigInteger) Hash value. Must be formatted with the name of the hash.
     */
    public static final String HASH_VALUE = HASH + "%s.value";

    /**
     * (BigInteger) Number of strings that were used to produce the hash. Must
     * be formatted with the name of the hash.
     */
    public static final String HASH_COUNT = HASH + "%s.count";

    /**
     * (String) Hash algorithm to use for calculating hashes (ex: SHA1).
     */
    public static final String HASH_ALGORITHM = HASH + "algorithm";

}
