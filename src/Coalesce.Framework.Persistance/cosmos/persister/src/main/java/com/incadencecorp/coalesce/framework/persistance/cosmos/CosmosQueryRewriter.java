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

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.search.AbstractQueryRewriter;

/**
 * Walks through a Filter, re-writing any property names removing the tablename from the property along with the /
 */
class CosmosQueryRewriter extends AbstractQueryRewriter {

    public CosmosQueryRewriter()
    {
        super(new CosmosNormalizer());
    }

    @Override
    protected String getIdColumn()
    {
        return "id";
    }

    @Override
    protected String getCoalesceEntityTypeName()
    {
        return CosmosConstants.COLLECTION_ENTITIES;
    }

    @Override
    protected String getCoalesceLinkageTypeName()
    {
        return CosmosConstants.COLLECTION_LINKAGES;
    }

    @Override
    protected String getCoalesceRecordTypeName(String recordset)
    {
        return CosmosConstants.getCollectionName(recordset);
    }

}
