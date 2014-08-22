package Coalesce.Framework.Persistance;

import unity.core.runtime.CallResult;

public interface ICoalesceEncrypter {

	public CallResult DecryptEntity(byte[] EntityEncryptedBytes, String EntityXml);
	public CallResult DecryptEntity(String EntityEncryptedBase64, String EntityXml);

	public CallResult EncryptEntity(String EntityXml, byte[] EntityEncryptedBytes);
	public CallResult EncryptEntity(String EntityXml, String EncryptedEntityBase64);

	public CallResult DecryptValue(byte[] ValueEncryptedBytes, String Value);
	public CallResult DecryptValue(String ValueEncryptedBase64, String Value);
	public CallResult DecryptValue(byte[] ValueEncryptedBytes, byte[] ValueBytes);

	public CallResult EncryptValue(String Value, byte[] ValueEncryptedBytes);
	public CallResult EncryptValue(String Value, String ValueEncryptedBase64);
	public CallResult EncryptValue(byte[] ValueBytes, byte[] ValueEncryptedBytes);
	
}
