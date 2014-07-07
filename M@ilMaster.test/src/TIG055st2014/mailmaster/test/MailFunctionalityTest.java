package TIG055st2014.mailmaster.test;

import org.junit.Test;

import TIG055st2014.mailmaster.MailFunctionality;
import junit.framework.TestCase;

public class MailFunctionalityTest extends TestCase {
	
	private MailFunctionality mf;
	
	@Override
	public void setUp(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
	}
	public void testValidate(){
		assertTrue(mf.validate());
	}
	public void testValidateFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.validate());
	}
	public void testGetInbox(){		
		assertTrue(mf.getInbox().size() > 0);
	}
	public void testGetInboxFailure(){
		mf = new MailFunctionality("mailmastertesting@gmail.com", "WrongPassword", "gmail.com");
		assertFalse(mf.getInbox().size() > 0);
	}
}
