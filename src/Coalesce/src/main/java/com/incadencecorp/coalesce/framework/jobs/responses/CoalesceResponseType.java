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

package com.incadencecorp.coalesce.framework.jobs.responses;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;

/**
 * 
 * @author Derek
 *
 */
public class CoalesceResponseType<T> implements ICoalesceResponseType<T> {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private EResultStatus _status;
    private String id;
    private String error;
    private T _result;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CoalesceResponseType()
    {
        _status = EResultStatus.SUCCESS;
    }

    /*--------------------------------------------------------------------------
    Overrides
    --------------------------------------------------------------------------*/

    @Override
    public final EResultStatus getStatus()
    {
        return _status;
    }

    @Override
    public final void setStatus(EResultStatus status)
    {
        _status = status;
    }

    @Override
    public final T getResult()
    {
        return _result;
    }

    @Override
    public final void setResult(T result)
    {
        _result = result;
    }
    
    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String value)
    {
        id = value;
        
    }

    @Override
    public String getError()
    {
        return error;
    }

    
    @Override
    public void setError(String error)
    {
        this.error = error;
    }

    /**
     * @return whether the job / task was completed
     */
    public final boolean isSuccessful()
    {
        return _status == EResultStatus.SUCCESS;
    }

    /**
     * Does nothing only used for JSON serialization
     */
    public final void setSuccessful(boolean value) {
        // Do Nothing
    }

}
