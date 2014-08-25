package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import Coalesce.Framework.GeneratedJAXB.*;
import unity.core.runtime.CallResult;

public interface ICoalescePersistor {
		
	public class EntityMetaData {

		public String Id;
		public String Type;
		public String Key;
		
	}
	
	public boolean Initialize(ICoalesceEncrypter Encrypter);
	
	public boolean SetEntity(Entity entity);
	
	// Get Entity
	public Entity GetEntity(String Key);
	public Entity GetEntity(String EntityId, String EntityIdType);
	public Entity GetEntity(String Name, String EntityId, String EntityIdType);
	
	// Get Entity XML
	public String GetEntityXml(String Key);
	public String GetEntityXml(String EntityId, String EntityIdType);
	public String GetEntityXml(String Name, String EntityId, String EntityIdType);
	
	public Object GetFieldValue(String fieldKey);
	public String GetXPath(String Key, String ObjectType, String EntityKey);
	
	//Deprecated Functions
	public CallResult PersistEntityTemplate(/*CoalesceEntityTemplate EntityTemplate*/);
	
	// Entity Templates
	public String GetEntityTemplateXml(String Key);
	public String GetEntityTemplateXml(String Name, String Source, String Version);
	public String GetEntityTemplateKey(String Name, String Source, String Version);
	public String GetEntityTemplateMetadata();
		
	public DateTime GetCoalesceDataObjectLastModified(String Key, String ObjectType);
	public List<String> GetCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName, String EntitySource);
	public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key);
	public byte[] GetBinaryArray(String BinaryFieldKey);

	
}