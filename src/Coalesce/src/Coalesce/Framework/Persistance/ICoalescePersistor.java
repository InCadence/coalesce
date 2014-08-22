package Coalesce.Framework.Persistance;

import Coalesce.Framework.GeneratedJAXB.*;
import unity.core.runtime.CallResult;

public interface ICoalescePersistor {
	
	public CallResult Initialize(ICoalesceEncrypter Encrypter);
	
	public CallResult SetEntity(Entity entity);
	
	public CallResult GetEntity(String Key, Entity entity);
	public CallResult GetEntity(String EntityId, String EntityIdType, Entity entity);
	public CallResult GetEntity(String Name, String EntityId, String EntityIdType, Entity entity);
	
	public CallResult GetEntityXml(String Key, String Xml);
	public CallResult GetEntityXml(String EntityId, String EntityIdType, String Xml);
	public CallResult GetEntityXml(String Name, String EntityId, String EntityIdType, String Xml);
	
	public CallResult getFieldValue(String fieldKey, String value);
	public CallResult getXPath(String Key, String ObjectType, String EntityKey, String XPath);
	
}