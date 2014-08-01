package TIG055st2014.mailmaster.test;

import javax.mail.Message;

import org.junit.Test;

import TIG055st2014.mailmaster.AppVariablesSingleton;
import TIG055st2014.mailmaster.MailFunctionality;
import junit.framework.TestCase;

public class MailFunctionalityTest extends TestCase {
	
	private MailFunctionality mf;
	
	@Override
	public void setUp(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		AppVariablesSingleton d = AppVariablesSingleton.getInstance();
		d.setFolderName("INBOX");
	}
	public void testValidate(){
		assertTrue(mf.validateTest());
	}
	public void testValidateFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.validateTest());
	}
	public void testGetInbox(){		
		assertTrue(mf.getFolderTest().size() > 0);
	}
	public void testGetInboxFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.getFolderTest().size() > 0);
	}
	public void testGetContents(){
		try{
			AppVariablesSingleton d = AppVariablesSingleton.getInstance();
			d.setEmail(mf.getFolderTest().get(0));
			assertFalse(mf.getTestContents().equalsIgnoreCase(""));
		}
		catch(Exception e){
			e.printStackTrace();
			assertTrue(false); //This should never be reached if code is working.
		}
	}
	public void testGetReply(){
		try{
			Message temp1 = mf.getFolderTest().get(0);
			Message temp2 = mf.getTestReply(temp1);
			assertTrue(temp2.getSubject().equalsIgnoreCase("RE: " + temp1.getSubject())
					   || temp2.getSubject().equalsIgnoreCase(temp1.getSubject()));
		}
		catch(Exception e){
			e.printStackTrace();
			assertTrue(false); //This should never be reached if code is working.
		}
	}
	
	
}
