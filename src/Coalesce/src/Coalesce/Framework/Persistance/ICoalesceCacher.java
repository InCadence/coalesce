package Coalesce.Framework.Persistance;

import Coalesce.Framework.DataModel.XsdEntity;

public interface ICoalesceCacher {

    public boolean ContainsEntity(String Key);

    public boolean StoreEntity(XsdEntity entity);
    public XsdEntity RetrieveEntity(String Key);
    public boolean RemoveEntity(String Key);

    public long EntityCount();
    public boolean ClearCache();

    public  ECoalesceCacheStates getState();
    public boolean getSupportsDelayedSave();
	
}
