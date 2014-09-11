package Coalesce.Framework.Persistance;

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

import org.apache.tomcat.util.codec.binary.Base64;

import Coalesce.Common.Exceptions.CoalesceCryptoException;

public abstract class CoalesceEncrypter implements ICoalesceEncrypter {

    private static HashMap<String, EncoderParameters> _parameters = new HashMap<String, EncoderParameters>();

    private EncoderParameters _parameter;
    private String _Transformation;

    public CoalesceEncrypter(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException,
            UnsupportedEncodingException
    {

        this._Transformation = "AES/CBC/PKCS5Padding";

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

        Cipher cipher = null;

        try
        {

            // Load Cipher
            cipher = Cipher.getInstance(this._Transformation);

            // Initialize Cipher
            cipher.init(Cipher.ENCRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new CoalesceCryptoException("getEncryptionCipher", e);
        }

        return cipher;

    }

    public Cipher getDecryptionCipher() throws CoalesceCryptoException
    {

        Cipher cipher = null;

        try
        {

            // Load Cipher
            cipher = Cipher.getInstance(this._Transformation);

            // Initialize Cipher
            cipher.init(Cipher.DECRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));

        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new CoalesceCryptoException("getDecryptionCipher", e);
        }

        return cipher;

    }

    @Override
    public String decryptEntity(byte[] EntityEncryptedBytes) throws CoalesceCryptoException
    {
        return this.decryptValue(EntityEncryptedBytes);
    }

    @Override
    public String decryptEntity(String EntityEncryptedBase64) throws CoalesceCryptoException
    {
        return this.decryptValue(EntityEncryptedBase64);
    }

    @Override
    public byte[] encryptEntity(String EntityXml) throws CoalesceCryptoException
    {
        return this.encryptValue(EntityXml);
    }

    @Override
    public String encryptEntityToBase64(String EntityXml) throws CoalesceCryptoException
    {
        return this.encryptValueToBase64(EntityXml);
    }

    @Override
    public String decryptValue(byte[] ValueEncryptedBytes) throws CoalesceCryptoException
    {

        String DecryptString = null;

        byte[] utf8 = this.decryptValueToBytes(ValueEncryptedBytes);

        try
        {

            DecryptString = new String(utf8, "UTF8");

        }
        catch (UnsupportedEncodingException e)
        {
            throw new CoalesceCryptoException("decryptValue", e);
        }

        return DecryptString;

    }

    @Override
    public String decryptValue(String ValueEncryptedBase64) throws CoalesceCryptoException
    {

        Base64 Encoder = new Base64();

        byte[] DecodedBytes = Encoder.decode(ValueEncryptedBase64);

        return this.decryptValue(DecodedBytes);

    }

    @Override
    public byte[] decryptValueToBytes(byte[] ValueEncryptedBytes) throws CoalesceCryptoException
    {

        byte[] utf8 = null;

        // Decrypt Data
        try
        {
            utf8 = this.getDecryptionCipher().doFinal(ValueEncryptedBytes);
        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new CoalesceCryptoException("decryptValue", e);
        }

        return utf8;

    }

    @Override
    public byte[] encryptValue(String Value) throws CoalesceCryptoException
    {
        return this.encryptValue(Value.getBytes());
    }

    @Override
    public String encryptValueToBase64(String Value) throws CoalesceCryptoException
    {

        byte[] utf8 = this.encryptValue(Value);

        if (utf8 != null)
        {

            Base64 Encoder = new Base64();
            return Encoder.encodeToString(utf8);

        }
        else
        {
            return null;
        }

    }

    @Override
    public byte[] encryptValue(byte[] ValueBytes) throws CoalesceCryptoException
    {

        byte[] utf8 = null;

        try
        {
            utf8 = this.getEncryptionCipher().doFinal(ValueBytes);
        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new CoalesceCryptoException("encryptValue", e);
        }

        return utf8;
    }

    private class EncoderParameters {

        public final SecretKey key;
        public final byte[] iv;

        public EncoderParameters(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException,
                UnsupportedEncodingException
        {

            // Salt
            byte[] salt = { 0x0, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, (byte) 0xF1, (byte) 0xF0, (byte) 0xEE, 0x21, 0x22, 0x45 };

            Rfc2898DeriveBytes derived_bytes = new Rfc2898DeriveBytes(passPhrase, salt, 1000);

            // Create Key and IV
            byte[] key_bytes = derived_bytes.getBytes(32);
            iv = derived_bytes.getBytes(16);

            key = new SecretKeySpec(key_bytes, "AES");

        }
    }

}
