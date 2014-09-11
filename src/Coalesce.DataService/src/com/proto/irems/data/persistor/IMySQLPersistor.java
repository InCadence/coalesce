package com.proto.irems.data.persistor;



public interface IMySQLPersistor {

	public abstract boolean setEntity(String entityXML);
	public abstract String getEntityXml(String Key);

	public abstract String getEntityXmlNameIdType(String Name,
			String EntityId, String EntityIdType);

	public abstract String[] getEntityKeys(String EntityId,
			String EntityIdType);

	public abstract String getFieldValue(String FieldKey);

	public abstract boolean setFieldValue(String key, String value);

	public abstract String getXPath(String FieldKey, String ObjectKey,
			String EntityKey, String XPath);

}