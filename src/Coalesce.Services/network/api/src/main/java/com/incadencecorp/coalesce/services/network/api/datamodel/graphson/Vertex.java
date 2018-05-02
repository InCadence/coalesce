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

package com.incadencecorp.coalesce.services.network.api.datamodel.graphson;

import java.util.HashMap;

/**
 * @author Derek Clemenzi
 */
public class Vertex extends HashMap<String, Object> {

    public static final String ID = "id";
    public static final String TYPE = "type";

    public String getId()
    {
        return (String) this.get(ID);
    }

    public void setId(String value)
    {
        this.put(ID, value);
    }

    public String getType()
    {
        return (String) this.get(TYPE);
    }

    public void setType(String value)
    {
        this.put(TYPE, value);
    }
}
