package Coalesce.Framework.Persistance;

import Coalesce.Framework.DataModel.Entity;
import unity.core.runtime.CallResult;

public interface ICoalesceCacher {

    public CallResult ContainsEntity(String Key);

    public CallResult StoreEntity(Entity entity, boolean IsModified);
    public CallResult RetrieveEntity(String Key, Entity entity);
    public CallResult RemoveEntity(String Key);

    public CallResult EntityCount(long Count);
    public CallResult ClearCache();

    public  ECoalesceCacheStates getState();
    public boolean getSupportsDelayedSave();
	
}
