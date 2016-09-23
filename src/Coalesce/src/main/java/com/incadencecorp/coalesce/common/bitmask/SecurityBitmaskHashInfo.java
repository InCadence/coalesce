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

/**
 * Container class for information about a hash code.
 * 
 * @author n78554
 *
 */
public final class SecurityBitmaskHashInfo {

    private String name;
    private BigInteger hash;
    private int count;

    /**
     * Default Constructor
     * 
     * @param name
     * 
     * @param hash
     * @param count
     */
    public SecurityBitmaskHashInfo(String name, BigInteger hash, int count)
    {
        this.name = name;
        this.hash = hash;
        this.count = count;
    }

    /**
     * @return the identifier for this hash.
     */
    public final String getName()
    {
        return name;
    }

    /**
     * @return the hash value
     */
    public final BigInteger getHash()
    {
        return hash;
    }

    /**
     * @return the number of elements that created the hash.
     */
    public final int getCount()
    {
        return count;
    }

}
