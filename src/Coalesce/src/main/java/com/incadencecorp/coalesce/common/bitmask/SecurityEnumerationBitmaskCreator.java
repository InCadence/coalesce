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

import java.security.Principal;

import com.incadencecorp.coalesce.api.ICoalesceSetup;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;

/**
 * This creator uses {@link EnumerationProviderUtil} to get valid options.
 * 
 * @author n78554
 *
 */
public class SecurityEnumerationBitmaskCreator extends SecurityBitmaskCreator implements ICoalesceSetup {

    private String name;
    private Principal principal = null;
    private boolean isInitialzied = false;

    /**
     * Creates a creator using EnumerationProviderUtil to load the options.
     * 
     * @param name of the enumeration
     */
    public SecurityEnumerationBitmaskCreator(String name)
    {
        this(name, null);
    }

    /**
     * Creates a creator using EnumerationProviderUtil to load the options.
     * 
     * @param name of the enumeration
     * @param principal to use when looking up enumerations.
     */
    public SecurityEnumerationBitmaskCreator(String name, Principal principal)
    {
        this.name = name;
        this.principal = principal;
    }

    /**
     * @return the enumeration used by this creator.
     */
    public String getEnumerationName()
    {
        return name;
    }

    /**
     * Loads the options for the specified enumeration.
     */
    @Override
    public void setup()
    {
        if (!isInitialzied)
        {
            validOptions.addAll(EnumerationProviderUtil.getValues(principal, name));
            isInitialzied = true;
        }
    }

}
