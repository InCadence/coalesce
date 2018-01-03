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

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for components of the synchronizer.
 *
 * @author n78554
 */
public class CoalesceComponentImpl implements ICoalesceComponent {

    // Use the CoalesceFramework
    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceFramework.class);

    protected PropertyLoader loader;
    protected Map<String, String> parameters = new HashMap<String, String>();

    private String name;

    @Override
    public String getName()
    {
        if (StringHelper.isNullOrEmpty(name))
        {
            // Is CoalesceFramework has trace enabled use the full class name by default
            if (LOGGER.isTraceEnabled())
            {
                name = this.getClass().getName();
            }
            else
            {
                name = this.getClass().getSimpleName();
            }
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

        if (loader != null)
        {
            setProperties(loader.getSettings());
        }
    }

    @Override
    public List<String> getPropertyList()
    {
        return new ArrayList<String>();
    }

}
