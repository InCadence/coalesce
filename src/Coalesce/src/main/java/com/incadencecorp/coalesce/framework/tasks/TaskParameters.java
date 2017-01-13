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

import com.incadencecorp.coalesce.api.ICoalescePrincipal;

/**
 * This is a wrapper class for parameters to be passed down to individual tasks.
 * 
 * @author Derek Clemenzi
 *
 * @param <T>
 * @param <Y>
 */
public class TaskParameters<T, Y> {

    private T target;
    private Y params;
    private ICoalescePrincipal principal;

    /**
     * Sets the task's target.
     * 
     * @return the task's target.
     */
    public T getTarget()
    {
        return target;
    }

    /**
     * Sets the task's target.
     * 
     * @param value
     */
    public void setTarget(T value)
    {
        this.target = value;
    }

    /**
     * @return the user's principal who kicked off the task.
     */
    public ICoalescePrincipal getPrincipal()
    {
        return principal;
    }

    /**
     * @return the user's name who kicked off the task.
     */
    public String getPrincipalName()
    {
        return principal == null ? "" : principal.getName();
    }

    /**
     * Sets the user's principal who kicked off the task.
     * 
     * @param value
     */
    public void setPrincipal(ICoalescePrincipal value)
    {
        this.principal = value;
    }
    

    /**
     * @return the user's IP who kicked off the task.
     */
    public String getPrincipalIp()
    {
        return principal == null ? "" : principal.getIp();
    }

    /**
     * @return the parameters from the original request that applies to this
     *         task.
     */
    public Y getParams()
    {
        return params;
    }

    /**
     * Sets the parameters from the original request that applies to this task.
     * 
     * @param value
     */
    public void setParams(Y value)
    {
        this.params = value;
    }

}
