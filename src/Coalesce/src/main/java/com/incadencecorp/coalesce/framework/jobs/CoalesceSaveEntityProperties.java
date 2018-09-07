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

package com.incadencecorp.coalesce.framework.jobs;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

/**
 * Properties container.
 * 
 * @author Derek
 *
 */
public class CoalesceSaveEntityProperties {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private boolean allowRemoval;
    private CoalesceEntity entities[];

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * @return whether entities marked as deleted will be removed from the
     *         database.
     */
    public boolean isAllowRemoval()
    {
        return allowRemoval;
    }

    /**
     * Sets whether entities marked as deleted will be removed from the
     * database.
     */
    public void setAllowRemoval(boolean allowRemoval)
    {
        this.allowRemoval = allowRemoval;
    }

    /**
     * @return the entities to be processed.
     */
    public CoalesceEntity[] getEntities()
    {
        return entities;
    }

    /**
     * Sets the entities to be processed.
     */
    public void setEntities(CoalesceEntity... entities)
    {
        this.entities = entities;
    }

}
