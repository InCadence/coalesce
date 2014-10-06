/**
 * 
 */
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
 * @author Tom.Johnson
 *
 */
public class CoalesceCryptoException extends CoalesceException {

    /**
     * 
     */
    private static final long serialVersionUID = 2092914763132572518L;

    /**
     * @param message
     * @param ex
     */
    public CoalesceCryptoException(String message, Exception ex)
    {
        super(message, ex);
        // TODO Auto-generated constructor stub
    }

}
