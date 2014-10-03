package Coalesce.Framework.Persistance;

import Coalesce.Framework.DataModel.CoalesceEntity;

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

public interface ICoalesceCacher {

    public boolean containsEntity(String Key);

    public boolean storeEntity(CoalesceEntity entity);

    public CoalesceEntity retrieveEntity(String Key);

    public boolean removeEntity(String Key);

    public long entityCount();

    public boolean clearCache();

    public ECoalesceCacheStates getState();

    public boolean getSupportsDelayedSave();

}
