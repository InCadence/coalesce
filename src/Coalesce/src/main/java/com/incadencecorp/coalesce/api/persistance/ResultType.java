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

package com.incadencecorp.coalesce.api.persistance;

/**
 * 
 * @author Derek
 *
 */
public class ResultType {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private boolean isSuccessful;
    private String message;
    private Exception exception;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public ResultType()
    {
        isSuccessful = true;
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * @return whether this result was successful.
     */
    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    /**
     * Sets whether this result was successful.
     * 
     * @param isSuccessful
     */
    public void setSuccessful(boolean isSuccessful)
    {
        this.isSuccessful = isSuccessful;
    }

    /**
     * @return details of this result.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets details of this result.
     * 
     * @param message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the exception that caused this result, if caused by an exception;
     *         otherwise <code>null</code>.
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Sets the exception that caused this result.
     * 
     * @param exception
     */
    public void setException(Exception exception)
    {
        this.setSuccessful(false);
        this.exception = exception;
        this.message = exception.getMessage();
    }

}
