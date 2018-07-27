/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.common.controllers.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;

public class GraphLink {

    private String source;
    private String target;
    private String label;
    private ELinkTypes type;
    private ECoalesceObjectStatus status;
    private boolean isBiDirectional;

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public ELinkTypes getType()
    {
        return type;
    }

    public void setType(ELinkTypes type)
    {
        this.type = type;
    }

    public ECoalesceObjectStatus getStatus()
    {
        return status;
    }

    public void setStatus(ECoalesceObjectStatus status)
    {
        this.status = status;
    }

    @JsonIgnore
    public boolean isBiDirectional()
    {
        return isBiDirectional;
    }

    public void setBiDirectional(boolean byDirectional)
    {
        isBiDirectional = byDirectional;
    }
}
