package TIG055st2014.mailmaster.test.HelperClasses;

import TIG055st2014.mailmaster.HelpClasses.Encryption;
import junit.framework.TestCase;

/**
 * This class takes care of testing the encrypt/decrypt functionality we use for
 * safely storing users' passwords.
 */
public class EncryptionTest  extends TestCase {

private Encryption enc;
	
	@Override
	public void setUp(){
		enc = new Encryption();
	}
	/**
	 * This test tries to verify that the encrypt function alters the password.
	 */
	public void testEncrypt(){
		String key = "Some Key";
		String password = "mailmaster123";
		assertFalse(enc.encrypt(key,password).equals(password));
	}
	/**
	 * This test tries to verify that the decrypt function alters an encrypted password.
	 */
	public void testDecrypt(){
		String key = "Some Key";
		String password = "mailmaster123";
		String encrypted = enc.encrypt(key, password);
		assertFalse(enc.decrypt(key, encrypted).equals(encrypted));
	}
	/**
	 * This test tries to verify that encrypting a String and decrypting the result gives
	 * back the original String.
	 */
	public void testDecryptAndDecrpt(){
		String key = "Some Key";
		String password = "mailmaster123";
		assertTrue(password.equals(enc.decrypt(key, enc.encrypt(key, password))));
	
	}
}