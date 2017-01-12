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

package com.incadencecorp.coalesce.framework.tasks;

import java.security.Principal;

public class TaskParameters<T, Y> {

    private T target;
    private Y params;
    private Principal principal; 
    private String ip;

    public T getTarget()
    {
        return target;
    }
    
    public void setTarget(T _framework)
    {
        this.target = _framework;
    }
    
    public Principal getPrincipal()
    {
        return principal;
    }
    
    public String getPrincipalName()
    {
        return principal == null ? "" : principal.getName();
    }
    
    public void setPrincipal(Principal principal)
    {
        this.principal = principal;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    
    public Y getParams()
    {
        return params;
    }

    
    public void setParams(Y params)
    {
        this.params = params;
    }
    
    
    
}
