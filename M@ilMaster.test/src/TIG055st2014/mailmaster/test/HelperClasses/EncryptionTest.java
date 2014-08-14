package TIG055st2014.mailmaster.test.HelperClasses;

import TIG055st2014.mailmaster.HelpClasses.Encryption;
import junit.framework.TestCase;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
for more details. You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

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