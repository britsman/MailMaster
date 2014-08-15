package TIG055st2014.mailmaster.test.HelperClasses;

import javax.mail.Message;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import junit.framework.TestCase;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License 
Version 2 only; as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Testing class that covers most of the functionality based on javamail android
 * api that we use in our application. We have consciously avoided testing send 
 * and save draft, since testing those features would "spam" the folders in question
 * with messages.
 */
public class MailFunctionalityTest extends TestCase {
	
	private MailFunctionality mf;
	private String account;
	
	/**
	 * Here we set up the email account that the functionality will be tested with.
	 */
	@Override
	public void setUp(){
		account = "mailmastertesting@gmail.com";
		mf = new MailFunctionality(account, "mailmaster123", "gmail.com");
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		apv.setFolderName(account, "INBOX");
	}
	/**
	 * This test tries to verify that the testing account's login info is valid.
	 */
	public void testValidate(){
		assertTrue(mf.validateTest());
	}
	/**
	 * This test tries to verify that validation will fail if the supplied login info is¨
	 * invalid.
	 */
	public void testValidateFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.validateTest());
	}
	/**
	 * This test tries to verify that getting contents from an IMAPfolder will succeed when
	 * the testing account's login info is valid.
	 */
	public void testGetInbox(){		
		assertTrue(mf.getFolderTest().size() > 0);
	}
	/**
	 * This test tries to verify that getting contents from an IMAPfolder will fail when
	 * the supplied login info is invalid.
	 */
	public void testGetInboxFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.getFolderTest().size() > 0);
	}
	/**
	 * This test tries to verify that we can retrieve the full contents of a particular email message.
	 */
	public void testGetContents(){
		try{
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			apv.setEmail(mf.getFolderTest().get(0));
			assertFalse(mf.getTestContents().equalsIgnoreCase(""));
		}
		//This should never be reached if code is working.
		catch(Exception e){
			e.printStackTrace();
			assertTrue(false); 
		}
	}
	/**
	 * This test tries to verify that constructing a reply from an existing message should result
	 * in a new message with the expected subject.
	 */
	public void testGetReply(){
		try{
			Message temp1 = mf.getFolderTest().get(0);
			Message temp2 = mf.getTestReply(temp1);
			assertTrue(temp2.getSubject().equalsIgnoreCase("RE: " + temp1.getSubject())
					   || temp2.getSubject().equalsIgnoreCase(temp1.getSubject()));
		}
		//This should never be reached if code is working.
		catch(Exception e){
			e.printStackTrace();
			assertTrue(false); 
		}
	}
	
	
}
