package com.incadencecorp.coalesce.common.exceptions;

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

/**
 * Exception that is thrown by Coalesce whenever there is an error during the parsing or formatting of data.
 */
public class CoalesceDataFormatException extends CoalesceException {

    private static final long serialVersionUID = -4679217177054112286L;

    /**
     * Constructor function. Sets the message to the parameter passed in.
     *
     * @param message allowed object is {@link String }
     */
    public CoalesceDataFormatException(final String message)
    {
        super(message);
    }

    public CoalesceDataFormatException(final String message, final Exception e)
    {
        super(message, e);
    }

}
