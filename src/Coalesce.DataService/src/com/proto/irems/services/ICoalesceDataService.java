package com.proto.irems.services;

import javax.jws.WebService;

@WebService
public interface ICoalesceDataService {
	public boolean setEntity( String entityXML);
	public String getEntity(String Key);
	public String[] getEntityKeys(String EntityId,String EntityIdType);
	public String getEntityByName(String Name,String EntityId,String EntityIdType);
	public String getEntityXML(String Key);
	public String[] getEntityXMLKeys(String EntityId, String EntityIdType);
	public String getEntityXMLByName(String Name,String EntityId,String EntityIdType);
	public String getFieldValue(String FieldKey);
	public String getXPath(String Key, String ObjectType, String EntityKey);
}
