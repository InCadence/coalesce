package Coalesce.Framework.Persistance;

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
