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

package com.incadencecorp.coalesce.api.subscriber.events;

import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;

/**
 * @author Derek Clemenzi
 */
public class CrudEvent {

    private String name;
    private ECrudOperations operation;
    private ObjectMetaData meta;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ECrudOperations getOperation()
    {
        return operation;
    }

    public void setOperation(ECrudOperations operation)
    {
        this.operation = operation;
    }

    public ObjectMetaData getMeta()
    {
        return meta;
    }

    public void setMeta(ObjectMetaData meta)
    {
        this.meta = meta;
    }
}
