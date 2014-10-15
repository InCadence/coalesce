package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;

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

    String decryptEntity(byte[] entityEncryptedBytes) throws CoalesceCryptoException;

    String decryptEntity(String entityEncryptedBase64) throws CoalesceCryptoException;

    byte[] encryptEntity(String entityXml) throws CoalesceCryptoException;

    String encryptEntityToBase64(String entityXml) throws CoalesceCryptoException;

    String decryptValue(byte[] valueEncryptedBytes) throws CoalesceCryptoException;

    String decryptValue(String valueEncryptedBase64) throws CoalesceCryptoException;

    byte[] decryptValueToBytes(byte[] valueEncryptedBytes) throws CoalesceCryptoException;

    byte[] encryptValue(String value) throws CoalesceCryptoException;

    String encryptValueToBase64(String value) throws CoalesceCryptoException;

    byte[] encryptValue(byte[] valueBytes) throws CoalesceCryptoException;

}
