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

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public abstract class CoalesceEncrypter implements ICoalesceEncrypter {

	private static HashMap<String, EncoderParameters> _parameters = new HashMap<String, EncoderParameters>();

	private EncoderParameters _parameter;
	private String _Transformation;
	
	public CoalesceEncrypter(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		this._Transformation = "AES/CBC/PKCS5Padding";
		
	    synchronized (_parameters) {
	    	_parameter = _parameters.get(passPhrase);
	        if (_parameter == null) {
	           _parameter = new EncoderParameters(passPhrase);
	           _parameters.put(passPhrase, _parameter);
	        }
	     }
	      
	}
	
	public Cipher GetEncryptionCipher() {
		
        Cipher cipher = null;
        
		try {
			
			// Load Cipher
			cipher = Cipher.getInstance(this._Transformation);
			
	        // Initialize Cipher
			cipher.init(Cipher.ENCRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, this);
			cipher = null;
		}
		
		return cipher; 
		
	}
	
	public Cipher GetDecryptionCipher() {
		
        Cipher cipher = null;
        
		try {
			
			// Load Cipher
			cipher = Cipher.getInstance(this._Transformation);
			
	        // Initialize Cipher
			cipher.init(Cipher.DECRYPT_MODE, _parameter.key, new IvParameterSpec(_parameter.iv));
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, this);
			cipher = null;
		}
		
		return cipher; 
		
	}
	
	@Override
	public String DecryptEntity(byte[] EntityEncryptedBytes) {
		return this.DecryptValue(EntityEncryptedBytes);
	}

	@Override
	public String DecryptEntity(String EntityEncryptedBase64) {
		return this.DecryptValue(EntityEncryptedBase64);
	}

	@Override
	public byte[] EncryptEntity(String EntityXml) {
		return this.EncryptValue(EntityXml);
	}

	@Override
	public String EncryptEntityToBase64(String EntityXml) {
		return this.EncryptValueToBase64(EntityXml);
	}

	@Override
	public String DecryptValue(byte[] ValueEncryptedBytes) {
		
		String DecryptString = null;

		byte[] utf8 = this.DecryptValueToBytes(ValueEncryptedBytes);
		
	    try {

		    DecryptString = new String(utf8, "UTF8");
			
		} catch (UnsupportedEncodingException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, this);
		}
	    
	    return DecryptString;
	    
	}

	@Override
	public String DecryptValue(String ValueEncryptedBase64) {
		
		Base64 Encoder = new Base64();
		
		byte[] DecodedBytes = Encoder.decode(ValueEncryptedBase64);
		
	    return this.DecryptValue(DecodedBytes);
	    
	}

	@Override
	public byte[] DecryptValueToBytes(byte[] ValueEncryptedBytes) {
		
	    byte[] utf8 = null;

		// Decrypt Data
	    try {
		    utf8 = this.GetDecryptionCipher().doFinal(ValueEncryptedBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, this);
		}
	    
	    return utf8;
	    
	}

	@Override
	public byte[] EncryptValue(String Value) {
		return this.EncryptValue(Value.getBytes());
	}

	@Override
	public String EncryptValueToBase64(String Value) {
		
		byte[] utf8 = this.EncryptValue(Value);
		
		if (utf8 != null) {

			Base64 Encoder = new Base64();
			return Encoder.encodeToString(utf8);
			
		} 
		else {
			return null;
		}
		
	}

	@Override
	public byte[] EncryptValue(byte[] ValueBytes) {
		
		byte[] utf8 = null;
		
		try {
			utf8 = this.GetEncryptionCipher().doFinal(ValueBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, this);
		}
		
		return utf8;
	}

	private class EncoderParameters {
		
		public final SecretKey key;
		public final byte[] iv;
		      
		public EncoderParameters(String passPhrase) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		         
			// Salt
			byte[] salt = {0x0, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, (byte) 0xF1, (byte) 0xF0, (byte) 0xEE, 0x21, 0x22, 0x45};

		    Rfc2898DeriveBytes derived_bytes = new Rfc2898DeriveBytes(passPhrase, salt, 1000);
		         
		    // Create Key and IV
		    byte[] key_bytes = derived_bytes.getBytes(32);
		    iv = derived_bytes.getBytes(16);

		    key = new SecretKeySpec(key_bytes, "AES");

		}
	}
	
}
