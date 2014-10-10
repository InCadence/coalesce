package com.incadencecorp.coalesce.framework.persistance;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xerces.impl.dv.util.Base64;

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
public class CoalesceEncrypter implements ICoalesceEncrypter {

    private static HashMap<String, EncoderParameters> _parameters = new HashMap<String, EncoderParameters>();

    private EncoderParameters _parameter;
    private String _transformation;

    public CoalesceEncrypter(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException,
            UnsupportedEncodingException
    {

        _transformation = "AES/CBC/PKCS5Padding";

        synchronized (_parameters)
        {
            _parameter = _parameters.get(passPhrase);
            if (_parameter == null)
            {
                _parameter = new EncoderParameters(passPhrase);
                _parameters.put(passPhrase, _parameter);
            }
        }

    }

    public Cipher getEncryptionCipher() throws CoalesceCryptoException
    {
        try
        {

            // Load Cipher
            Cipher cipher = Cipher.getInstance(_transformation);

            // Initialize Cipher
            cipher.init(Cipher.ENCRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));

            return cipher;

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new CoalesceCryptoException("getEncryptionCipher", e);
        }
    }

    public Cipher getDecryptionCipher() throws CoalesceCryptoException
    {
        try
        {

            // Load Cipher
            Cipher cipher = Cipher.getInstance(_transformation);

            // Initialize Cipher
            cipher.init(Cipher.DECRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));

            return cipher;

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new CoalesceCryptoException("getDecryptionCipher", e);
        }
    }

    @Override
    public String decryptEntity(byte[] entityEncryptedBytes) throws CoalesceCryptoException
    {
        return decryptValue(entityEncryptedBytes);
    }

    @Override
    public String decryptEntity(String entityEncryptedBase64) throws CoalesceCryptoException
    {
        return decryptValue(entityEncryptedBase64);
    }

    @Override
    public byte[] encryptEntity(String entityXml) throws CoalesceCryptoException
    {
        return encryptValue(entityXml);
    }

    @Override
    public String encryptEntityToBase64(String entityXml) throws CoalesceCryptoException
    {
        return encryptValueToBase64(entityXml);
    }

    @Override
    public String decryptValue(byte[] valueEncryptedBytes) throws CoalesceCryptoException
    {
        byte[] utf8 = decryptValueToBytes(valueEncryptedBytes);

        try
        {
            String decryptString = new String(utf8, "UTF8");

            return decryptString;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new CoalesceCryptoException("decryptValue", e);
        }
    }

    @Override
    public String decryptValue(String valueEncryptedBase64) throws CoalesceCryptoException
    {

        byte[] decodedBytes = Base64.decode(valueEncryptedBase64);

        return decryptValue(decodedBytes);

    }

    @Override
    public byte[] decryptValueToBytes(byte[] valueEncryptedBytes) throws CoalesceCryptoException
    {
        try
        {
            byte[] utf8 = getDecryptionCipher().doFinal(valueEncryptedBytes);

            return utf8;

        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new CoalesceCryptoException("decryptValue", e);
        }
    }

    @Override
    public byte[] encryptValue(String value) throws CoalesceCryptoException
    {
        try
        {
            return encryptValue(value.getBytes("UTF8"));
        }
        catch (UnsupportedEncodingException uee)
        {
            // Should never happen at run time
            return null;
        }
    }

    @Override
    public String encryptValueToBase64(String value) throws CoalesceCryptoException
    {

        byte[] utf8 = encryptValue(value);

        if (utf8 != null)
        {

            return Base64.encode(utf8);

        }
        else
        {
            return null;
        }

    }

    @Override
    public byte[] encryptValue(byte[] valueBytes) throws CoalesceCryptoException
    {
        try
        {
            byte[] utf8 = getEncryptionCipher().doFinal(valueBytes);

            return utf8;

        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new CoalesceCryptoException("encryptValue", e);
        }
    }

    private class EncoderParameters {

        public final SecretKey key;
        public final byte[] iv;

        public EncoderParameters(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException,
                UnsupportedEncodingException
        {

            // Salt
            byte[] salt = { 0x0, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, (byte) 0xF1, (byte) 0xF0, (byte) 0xEE, 0x21, 0x22, 0x45 };

            Rfc2898DeriveBytes derived_Bytes = new Rfc2898DeriveBytes(passPhrase, salt, 1000);

            // Create Key and IV
            byte[] key_Bytes = derived_Bytes.getBytes(32);
            iv = derived_Bytes.getBytes(16);

            key = new SecretKeySpec(key_Bytes, "AES");

        }
    }

}
