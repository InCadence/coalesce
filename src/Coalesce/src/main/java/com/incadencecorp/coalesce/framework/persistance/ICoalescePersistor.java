/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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
package com.incadencecorp.coalesce.framework.persistance;

import java.util.EnumSet;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

/**
 * Interface for storing and retrieving Coalesce entities from different
 * databases depending on the implementation.
 */
public interface ICoalescePersistor extends ICoalesceTemplatePersister{

    /**
     * Saves the Coalesce entity to the database.
     *
     * @param entities the Coalesce entities to be saved.
     * @param allowRemoval specifies whether an entity marked as deleted should
     *            be removed from the database.
     * @return true if successfully saved.
     * @throws CoalescePersistorException on error
     */
    boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param keys the primary key of the entity.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException on error
     */
    CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException;

    /**
     * Returns the Coalesce entity's XML that matches the given parameters.
     *
     * @param keys the primary key of the entity.
     * @return the matching Coalesce entity's XML.
     * @throws CoalescePersistorException on error
     */
    String[] getEntityXml(String... keys) throws CoalescePersistorException;

    /**
     * @return EnumSet of EPersistorCapabilities
     */
    EnumSet<EPersistorCapabilities> getCapabilities();

}
