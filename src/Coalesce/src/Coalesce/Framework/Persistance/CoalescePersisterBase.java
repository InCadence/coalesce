package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.GeneratedJAXB.Entity;

public class CoalescePersisterBase implements ICoalescePersistor {

	private ICoalesceCacher _Cacher = null;
	
	// Interface Implementation
	
	@Override
	public boolean Initialize(ICoalesceCacher Cacher) {

		this._Cacher = Cacher;
		
		return true;
	
	}

	@Override
	public boolean SetEntity(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public XsdEntity GetEntity(String Key) {
		
		XsdEntity entity = null; 
		
		// Get Entity From Cache
		entity = this.GetEntityFromCache(Key);
		
		// Entity Cached?
		if (entity == null) {
			
			// No; Load Entity's XML
			String EntityXml = this.GetEntityXml(Key);
		
			// Found?
			if (EntityXml != null) {
			
				// Yes; Initialize Entity
				entity = new XsdEntity();
				entity.Initialize(EntityXml);
			
				// Add Entity to Cache
				this.AddEntityToCache(entity);				
			
			}
		
		}
		
		return entity;
		
	}

	@Override
	public XsdEntity GetEntity(String EntityId, String EntityIdType) {

		XsdEntity entity = null; 
		
		// Load Entity's XML
		String EntityXml = this.GetEntityXml(EntityId, EntityIdType);
		
		// Found?
		if (EntityXml != null) {
			
			// Yes; Initialize Entity
			entity = new XsdEntity();
			entity.Initialize(EntityXml);
			
			// Add Entity to Cache
			this.AddEntityToCache(entity);				
			
		}
		
		return entity;
		
	}

	@Override
	public XsdEntity GetEntity(String Name, String EntityId, String EntityIdType) {
		
		XsdEntity entity = null; 
		
		// Load Entity's XML
		String EntityXml = this.GetEntityXml(Name, EntityId, EntityIdType);
		
		// Found?
		if (EntityXml != null) {
			
			// Yes; Initialize Entity
			entity = new XsdEntity();
			entity.Initialize(EntityXml);
			
			// Add Entity to Cache
			this.AddEntityToCache(entity);				
			
		}
		
		return entity;
		
	}

	@Override
	public String GetEntityXml(String Key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityXml(String EntityId, String EntityIdType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityXml(String Name, String EntityId, String EntityIdType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object GetFieldValue(String fieldKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetXPath(String Key, String ObjectType, String EntityKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime GetCoalesceDataObjectLastModified(String Key,
			String ObjectType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> GetCoalesceEntityKeysForEntityId(String EntityId,
			String EntityIdType, String EntityName, String EntitySource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] GetBinaryArray(String BinaryFieldKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallResult PersistEntityTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityTemplateXml(String Key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityTemplateXml(String Name, String Source,
			String Version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityTemplateKey(String Name, String Source,
			String Version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetEntityTemplateMetadata() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Private Functions
	
	protected XsdEntity GetEntityFromCache(String Key) {
		
		XsdEntity entity = null;  
		
		//Cacher Initialized?
		if (this._Cacher != null) {
			
			// Yes; Contains Entity?
			if (this._Cacher.ContainsEntity(Key)) {
				
				//Yes; Retrieve Entity
				entity = this._Cacher.RetrieveEntity(Key);
				
			}
			
		}
		
		return entity;
		
	}
	
	protected boolean AddEntityToCache(XsdEntity entity) {

		boolean IsModified = false;
		
		//Cacher Initialized?
		if (this._Cacher != null) {
			
			//Yes; Retrieve Entity
			IsModified = this._Cacher.StoreEntity(entity);
			
		}
		
		return IsModified;
		
	}

}
