package TIG055st2014.mailmaster.test;

import org.junit.Test;

import TIG055st2014.mailmaster.MailFunctionality;
import junit.framework.TestCase;

public class MailFunctionalityTest extends TestCase {
	
	private MailFunctionality mf;
	
	@Override
	public void setUp(){
		mf = new MailFunctionality("gusbriter@student.gu.se", "wav2!VEX", "student.gu.se");
	}
	public void testValidate(){
		assertTrue(mf.validate());
	}
	public void testValidateFailure(){
		mf = new MailFunctionality("gusbriter@student.gu.se", "WrongPassword", "student.gu.se");
		assertFalse(mf.validate());
	}
	public void testGetInbox(){		
		assertTrue(mf.getInbox().size() > 0);
	}
	public void testGetInboxFailure(){
		mf = new MailFunctionality("gusbriter@student.gu.se", "WrongPassword", "student.gu.se");
		assertFalse(mf.getInbox().size() > 0);
	}
}
