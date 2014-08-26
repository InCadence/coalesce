package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.GeneratedJAXB.*;
import unity.core.runtime.CallResult;

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

public interface ICoalescePersistor {
		
	public class EntityMetaData {

		public String Id;
		public String Type;
		public String Key;
		
	}
	
	public boolean Initialize(ICoalesceCacher Cacher);
	
	public boolean SetEntity(Entity entity);
	
	// Get Entity
	public XsdEntity GetEntity(String Key);
	public XsdEntity GetEntity(String EntityId, String EntityIdType);
	public XsdEntity GetEntity(String Name, String EntityId, String EntityIdType);
	
	// Get Entity XML
	public String GetEntityXml(String Key);
	public String GetEntityXml(String EntityId, String EntityIdType);
	public String GetEntityXml(String Name, String EntityId, String EntityIdType);
	
	public Object GetFieldValue(String fieldKey);
	public String GetXPath(String Key, String ObjectType, String EntityKey);
	
	public DateTime GetCoalesceDataObjectLastModified(String Key, String ObjectType);

	public List<String> GetCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName, String EntitySource);
	
	public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key);
	
	public byte[] GetBinaryArray(String BinaryFieldKey);

	//Deprecated Functions
	public String PersistEntityTemplate(/*CoalesceEntityTemplate EntityTemplate*/);
	
	// Entity Templates
	public String GetEntityTemplateXml(String Key);
	public String GetEntityTemplateXml(String Name, String Source, String Version);
	public String GetEntityTemplateKey(String Name, String Source, String Version);
	public String GetEntityTemplateMetadata();
		
}