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

/**
 * @author Derek Clemenzi
 */
public class Edge extends Vertex {

    public static final String OUT_VECTOR = "source";
    public static final String IN_VECTOR = "target";
    public static final String LABEL = "label";

    public String getOutV()
    {
        return (String) this.get(OUT_VECTOR);
    }

    public void setOutV(String value)
    {
        this.put(OUT_VECTOR, value);
    }

    public String getInV()
    {
        return (String) this.get(IN_VECTOR);
    }

    public void setInV(String value)
    {
        this.put(IN_VECTOR, value);
    }

    public String getLabel()
    {
        return (String) this.get(LABEL);
    }

    public void setLabel(String value)
    {
        this.put(LABEL, value);
    }
}
