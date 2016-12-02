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

package com.incadencecorp.coalesce.framework.iterators;

import com.incadencecorp.coalesce.api.ICoalescePopulator;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.populators.RandomPopulatorImpl;

/**
 * Populates an entity with data.
 * 
 * @author n78554
 *
 */
public class CoalescePopulatorIterator extends CoalesceIterator<ICoalescePopulator> {

    /**
     * Populates entities with {@link RandomPopulatorImpl}.
     * 
     * @param entities
     * @throws CoalesceException
     */
    public void iterate(CoalesceEntity... entities) throws CoalesceException
    {
        iterate(new RandomPopulatorImpl(), entities);
    }

    /**
     * Populates entities with the provided {@link ICoalescePopulator}.
     * 
     * @param populator
     * @param entities
     * @throws CoalesceException
     */
    public void iterate(ICoalescePopulator populator, CoalesceEntity... entities) throws CoalesceException
    {
        for (CoalesceEntity entity : entities)
        {
            if (!entity.isInitialized())
            {
                entity.initialize();
            }

            processActiveElements(entity, populator);
        }
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, ICoalescePopulator populator)
    {
        populator.populate(recordset);

        // Stop recursive processing
        return false;
    }

}
