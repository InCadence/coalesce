package com.incadencecorp.coalesce.search.api;

import java.util.List;

import org.geotools.data.Query;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;

/**
 * This interface is used to provide searching capability to persistors.
 *
 * @author n78554
 */
public interface ICoalesceSearchPersistor {

    /**
     * Performs a search.
     *
     * @param query
     * @param parameters
     * @return
     * @throws CoalescePersistorException
     */
    List<CoalesceEntity> search(Query query, CoalesceParameter... parameters) throws CoalescePersistorException;

}
