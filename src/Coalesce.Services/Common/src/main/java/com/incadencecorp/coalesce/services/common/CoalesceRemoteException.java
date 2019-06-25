/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Derek Clemenzi
 */
public class CoalesceRemoteException extends RemoteException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceRemoteException.class);

    public CoalesceRemoteException(String error)
    {
        super(error);
    }

    public CoalesceRemoteException(String error, Exception e)
    {
        super(error, e);

        if (e == null)
        {
            LOGGER.warn(error);
        }
        else
        {
            LOGGER.error(error, e);
        }
    }

}
