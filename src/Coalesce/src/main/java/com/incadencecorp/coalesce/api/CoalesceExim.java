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

package com.incadencecorp.coalesce.api;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

/**
 * Defines the interface for importing and exporting Coalesce Entities to other
 * data formats.
 * 
 * @author n78554
 * @param <T>
 */
public interface CoalesceExim<T> {

    /**
     * @param entity to export
     * @param includeEntityType specifies whether to include the name, source,
     *            and version of the entity that the values came from.
     * @return the values of a {@link CoalesceEntity} in the <T> format.
     * @throws CoalesceException on an error
     */
    T exportValues(CoalesceEntity entity, boolean includeEntityType) throws CoalesceException;

    /**
     * Imports the values into the {@link CoalesceEntity}
     * 
     * @param values to populate the {@link CoalesceEntity} with.
     * @param entity to populate with values.
     * @throws CoalesceException if the values do not map correctly to the
     *             {@link CoalesceEntity}
     */
    void importValues(T values, CoalesceEntity entity) throws CoalesceException;

    /**
     * 
     * @param values to populate the {@link CoalesceEntityTemplate} with.
     * @param template to create a new entity from and populate with values.
     * @return a newly created {@link CoalesceEntity} from the
     *         {@link CoalesceEntityTemplate} populated with the values.
     * @throws CoalesceException if the values do not map correctly to the
     *             {@link CoalesceEntityTemplate}
     */
    CoalesceEntity importValues(T values, CoalesceEntityTemplate template) throws CoalesceException;

}
