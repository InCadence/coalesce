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

package com.incadencecorp.coalesce.framework;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * Base implementation for components of the synchronizer.
 * 
 * @author n78554
 */
public class CoalesceComponentImpl implements ICoalesceComponent {

    protected PropertyLoader loader;
    protected Map<String, String> parameters = new HashMap<String, String>();;

    private String name;

    @Override
    public String getName()
    {
        if (StringHelper.isNullOrEmpty(name))
        {
            name = this.getClass().getName();
        }

        return name;
    }

    @Override
    public void setName(String value)
    {
        name = value;
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        if (params != null)
        {
            this.parameters.putAll(params);
        }
    }

    @Override
    public final void setPropertyLoader(PropertyLoader loader)
    {
        this.loader = loader;
    }

}