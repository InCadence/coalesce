/**
 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
 * /// Copyright 2016 - Lockheed Martin Corporation, All Rights Reserved /// ///
 * Notwithstanding any contractor copyright notice, the government has ///
 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
 * 252.227-7014. Use of this work other than as specifically authorized by ///
 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
 * Rights. The Government has the right to use, modify, /// reproduce, perform,
 * display, release or disclose this computer software /// in whole or in part,
 * in any manner, and for any purpose whatsoever, /// and to have or authorize
 * others to do so. /// /// Distribution Statement D. Distribution authorized to
 * the Department of /// Defense and U.S. DoD contractors only in support of US
 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
 * Program /// Management under the Director of the Office of Naval
 * Intelligence. ///
 * -------------------------------UNCLASSIFIED---------------------------------
 */

package com.incadencecorp.coalesce.framework.enumerationprovider.impl;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This implementation is used to define enumerations within code.
 * 
 * @author n78554
 */
public class CodeEnumerationProvider extends AbstractEnumerationProvider {

    /**
     * Default Constructor; does not provide any enumeration support. 
     */
    public CodeEnumerationProvider()
    {
        // Do Nothing
    }
    
    /**
     * Constructs a provider with user defined enumerations.
     * 
     * @param enumerations
     */
    public CodeEnumerationProvider(Map<String, List<String>> enumerations)
    {
        for (Entry<String, List<String>> entry : enumerations.entrySet())
        {
            addEnumeration(null, entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        return null;
    }

}
