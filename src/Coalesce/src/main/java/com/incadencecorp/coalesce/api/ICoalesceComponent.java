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

import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.framework.PropertyLoader;

/**
 * Every Synchronizer's API should extend this common interface.
 * 
 * @author n78554
 */
public interface ICoalesceComponent {

    /**
     * @return the name.
     */
    String getName();

    /**
     * Set the name; overwriting any defaults.
     * 
     * @param value
     */
    void setName(String value);

    /**
     * Sets the properties.
     * 
     * @param values
     */
    void setProperties(Map<String, String> values);

    /**
     * Sets a property loader. Depending on the implementation this is used for
     * loading properties that were not specified by {@link #setProperties(Map)}
     * 
     * @param loader
     */
    void setPropertyLoader(PropertyLoader loader);

    /**
     * @return a list of properties used by this implementation.
     */
    List<String> getPropertyList();
}
