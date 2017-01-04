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

package com.incadencecorp.coalesce.services.api;

import com.incadencecorp.coalesce.api.EResultStatus;

/**
 * Generic class allowing status to be attached to the results returned from a
 * wed service.
 * 
 * @author Derek Clemenzi
 *
 * @param <T>
 */
public class Results<T> {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private String error;
    private EResultStatus status;
    private T result;

    /*--------------------------------------------------------------------------
    Constructor
    --------------------------------------------------------------------------*/

    /**
     * Constructs the result from a valid Coalesce object and sets the status to
     * {@link EResultStatusType#SUCCESS}.
     * 
     * @param entity
     */
    public Results(final T result)
    {

        status = EResultStatus.SUCCESS;
        this.result = result;
        error = "";

    }

    /**
     * Constructs a result from an error message setting the entity to null and
     * setting the status to {@link EResultStatusType#FAILED}
     * 
     * @param error
     */
    public Results(final String error)
    {

        status = EResultStatus.FAILED;
        result = null;
        this.error = error;

    }

    /*--------------------------------------------------------------------------
    Public Getters
    --------------------------------------------------------------------------*/

    /**
     * @return {@link EResultStatusType#SUCCESS} if
     *         {@link EntityResult#getEntity() wont return null}.
     */
    public EResultStatus getStatus()
    {
        return status;
    }

    /**
     * @return the entity.
     */
    public T getResult()
    {
        return result;
    }

    /**
     * @return an error message if the status is
     *         {@link EResultStatusType#FAILED}
     */
    public String getError()
    {
        return error;
    }
}
