package com.proto.irems.services;

import java.util.List;

import org.joda.time.DateTime;

import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.Persistance.ICoalesceCacher;

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

public abstract class CoalescePersisterBase implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
		Private Member Variables
	--------------------------------------------------------------------------*/

	private ICoalesceCacher _Cacher = null;
	
    /*--------------------------------------------------------------------------
		Interface Implementation
	--------------------------------------------------------------------------*/
	
	public boolean Initialize(ICoalesceCacher Cacher) {

		this._Cacher = Cacher;
		
		return true;
	
	}

	@Override
	public boolean SetEntity(XsdEntity entity, boolean AllowRemoval) {
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
				entity.initialize(EntityXml);
			
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
			entity.initialize(EntityXml);
			
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
			entity.initialize(EntityXml);
			
			// Add Entity to Cache
			this.AddEntityToCache(entity);				
			
		}
		
		return entity;
		
	}

	@Override
	public String GetEntityXml(String Key) {
		return null;
	}

	@Override
	public String GetEntityXml(String EntityId, String EntityIdType) {
		return null;
	}

	@Override
	public String GetEntityXml(String Name, String EntityId, String EntityIdType) {
		return null;
	}

	@Override
	public Object GetFieldValue(String fieldKey) {
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
		return null;
	}

	@Override
	public List<String> GetCoalesceEntityKeysForEntityId(String EntityId,
			String EntityIdType, String EntityName, String EntitySource) {
		return null;
	}

	@Override
	public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key) {
		return null;
	}

	@Override
	public byte[] GetBinaryArray(String BinaryFieldKey) {
		return null;
	}

	@Override
	public String PersistEntityTemplate() {
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
	
    /*--------------------------------------------------------------------------
		Private Functions
	--------------------------------------------------------------------------*/
	
	protected XsdEntity GetEntityFromCache(String Key) {
		
		XsdEntity entity = null;  
		
		//Cacher Initialized?
		if (this._Cacher != null) {
			
			// Yes; Contains Entity?
			if (this._Cacher.containsEntity(Key)) {
				
				//Yes; Retrieve Entity
				entity = this._Cacher.retrieveEntity(Key);
				
			}
			
		}
		
		return entity;
		
	}
	
	protected boolean AddEntityToCache(XsdEntity entity) {

		boolean IsModified = false;
		
		//Cacher Initialized?
		if (this._Cacher != null) {
			
			//Yes; Retrieve Entity
			IsModified = this._Cacher.storeEntity(entity);
			
		}
		
		return IsModified;
		
	}

}
