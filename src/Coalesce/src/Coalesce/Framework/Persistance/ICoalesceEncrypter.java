package Coalesce.Framework.Persistance;

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

	public String DecryptEntity(byte[] EntityEncryptedBytes);
	public String DecryptEntity(String EntityEncryptedBase64);

	public byte[] EncryptEntity(String EntityXml);
	public String EncryptEntityToBase64(String EntityXml);

	public String DecryptValue(byte[] ValueEncryptedBytes);
	public String DecryptValue(String ValueEncryptedBase64);
	public byte[] DecryptValueToBytes(byte[] ValueEncryptedBytes);

	public byte[] EncryptValue(String Value);
	public String EncryptValueToBase64(String Value);
	public byte[] EncryptValue(byte[] ValueBytes);
	
}
