package TIG055st2014.mailmaster.test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import TIG055st2014.mailmaster.Encryption;

import junit.framework.TestCase;

public class EncryptionTest  extends TestCase {

private Encryption enc;
	
	@Override
	public void setUp(){
		enc = new Encryption();
	
	}
	public void testEncrypt(){
		String Key = "Some Key";
		String password = "mailmaster123";
		enc.encrypt(Key, password);
		enc.decrypt(Key, password);
		assertFalse(enc.encrypt(Key,password).equals(password));
	
	}
	
	public void testDecrypt(){
		String Key = "Some Key";
		String password = "mailmaster123";
		String encreptCode = enc.encrypt(Key, password);
		enc.decrypt(Key, password);
		assertTrue(enc.decrypt(Key, encreptCode).equals(password));
	
	}
	

	public void testDecryptAndDecrpt(){
		String Key = "Some Key";
		String password = "mailmaster123";
		String encreptCode = enc.encrypt(Key, password);
		String decreptCode= enc.decrypt(Key, encreptCode);
		assertTrue(decreptCode.equals(password));
	
	}

}
