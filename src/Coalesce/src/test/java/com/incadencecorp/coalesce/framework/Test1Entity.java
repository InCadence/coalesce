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

package com.incadencecorp.coalesce.framework;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

/**
 * This entity is used for testing the object factory.
 * 
 * @author n78554
 */
public class Test1Entity extends CoalesceEntity {

    /**
     * Default Constructor
     */
    public Test1Entity()
    {

    }

    @Override
    public boolean initialize()
    {
        boolean isInitialized = false;
        if (initializeEntity("Test1", "UNIT TEST", "1", "", "", ""))
        {
            isInitialized = initializeReferences();
        }
        return isInitialized;
    }

    @Override
    protected boolean initializeEntity(String name,
                                       String source,
                                       String version,
                                       String entityId,
                                       String entityIdType,
                                       String title)
    {

        boolean isInitialized = false;

        if (super.initializeEntity(name, source, version, entityId, entityIdType, title))
        {

            isInitialized = true;
        }

        return isInitialized;
    }

}
