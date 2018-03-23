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
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;

/**
 * @author Derek Clemenzi
 */
public class LinkageEvent {

    private String name;
    private ECrudOperations operation;
    private ObjectMetaData entity1;
    private ELinkTypes relationship;
    private ObjectMetaData entity2;

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

    public ObjectMetaData getEntity1()
    {
        return entity1;
    }

    public void setEntity1(ObjectMetaData entity1)
    {
        this.entity1 = entity1;
    }

    public ELinkTypes getRelationship()
    {
        return relationship;
    }

    public void setRelationship(ELinkTypes relationship)
    {
        this.relationship = relationship;
    }

    public ObjectMetaData getEntity2()
    {
        return entity2;
    }

    public void setEntity2(ObjectMetaData entity2)
    {
        this.entity2 = entity2;
    }
}
