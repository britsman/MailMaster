package TIG055st2014.mailmaster;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends FragmentActivity {

	private SharedPreferences accounts;
	private String defaultAcc;
	private String pw;
	private static final int PICK_FROM_GALLERY = 101;
	String file_path;
	int columnIndex;
	public float kilobytes;
	private float total;
	public ListView listView;
	ArrayList<String> attachments;
	private SharedPreferences sizePref;
	private SharedPreferences.Editor sizeEdit;
	protected boolean save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayEmail d = DisplayEmail.getInstance();
		getActionBar().setDisplayShowHomeEnabled(false);
		save = false;
		if(d.getIsReply()){
			setContentView(R.layout.listview_attachments);
			listView = (ListView) findViewById(R.id.attachment_list);
			listView.addHeaderView(getLayoutInflater().inflate(R.layout.activity_reply, null));
		}
		else{
			setContentView(R.layout.listview_attachments);
			listView = (ListView) findViewById(R.id.attachment_list);
			listView.addHeaderView(getLayoutInflater().inflate(R.layout.activity_compose, null));

		}
		listView.setClickable(true);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		defaultAcc = accounts.getString("default", "");
		sizePref = getSharedPreferences("FileSizes", MODE_PRIVATE);
		sizeEdit = sizePref.edit();
		sizeEdit.putFloat("Total", (float)0.0);
		sizeEdit.commit();
		pw = accounts.getString(defaultAcc, "");
		attachments = new ArrayList<String>();
	}

	@Override
	protected void onStart() {
		super.onStart();
		DisplayEmail d = DisplayEmail.getInstance();
		TextView sender; 
		TextView result;

		if(d.getIsReply()){
			getActionBar().setTitle(R.string.composing_rp);
			result = (TextView) findViewById(R.id.totalsizeReply);
			sender = (TextView) findViewById(R.id.sendAccReply);
			sender.setText(defaultAcc);
			TextView to = (TextView) findViewById(R.id.receiveAccsReply);
			TextView subject = (TextView) findViewById(R.id.subjectReply);
			EditText cc = (EditText) findViewById(R.id.ccAccsReply);
			cc.setText("");
			try{	
				subject.setText(d.getReply().getSubject());
				if(d.getFolderName().contains("Sent")){
					Address [] tempTo = d.getEmail().getRecipients(RecipientType.TO);
					if(tempTo != null){
						for(Address a : tempTo){
							if(to.getText().toString().equals("")){
								to.setText(a.toString());
							}
							else{
								to.setText(to.getText() + "," + a.toString());
							}
						}
					}
				}
				else{
					to.setText(d.getEmail().getFrom()[0].toString());
				}
				this.addAddresses(d.getEmail().getRecipients(Message.RecipientType.CC), cc);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		else{
			getActionBar().setTitle(R.string.composing);
			result = (TextView) findViewById(R.id.totalsize);
			sender = (TextView) findViewById(R.id.sendAcc);
			sender.setText(defaultAcc);
			if(d.getFolderName().contains("Drafts")){
				try{
				MailFunctionality mf = new MailFunctionality(defaultAcc, accounts.getString(defaultAcc, ""), 
															 (defaultAcc.split("@"))[1]);
				EditText recipients = ((EditText) findViewById(R.id.receiveAccs));
				EditText cc = ((EditText) findViewById(R.id.ccAccs));
				EditText bcc = ((EditText) findViewById(R.id.bccAccs));
				EditText subject = ((EditText) findViewById(R.id.subject));
				subject.setText(d.getEmail().getSubject().toString());
				mf.getContents(this);
				addAddresses(d.getEmail().getRecipients(Message.RecipientType.TO), recipients);
				addAddresses(d.getEmail().getRecipients(Message.RecipientType.CC), cc);
				addAddresses(d.getEmail().getRecipients(Message.RecipientType.BCC), bcc);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		if(listView.getAdapter() == null){
			listView.setAdapter(new AttachmentsAdapter(getApplicationContext(),
					R.layout.attachments_item, R.id.attachments_text,
					attachments, result));
		}
	}
	private void addAddresses(Address[] addresses, EditText et){
		if(addresses != null){
			for(Address a : addresses){
				if(et.getText().toString().equals("")){
					et.setText(a.toString());
				}
				else{
					et.setText(et.getText() + "," + a.toString());
				}
			}
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
			/**
			 * Get Path
			 */
			Uri selectedImage = data.getData();
			DisplayEmail d = DisplayEmail.getInstance();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			file_path = cursor.getString(columnIndex);
			if(!attachments.contains(file_path)){
				Log.d("Attachment Path:", file_path);
				attachments.add(file_path);

				// calculating the file size
				File file = new File(file_path);
				float bytes = file.length();
				kilobytes = bytes/1024 ;

				total = sizePref.getFloat("Total", (float)0.0);
				total += kilobytes;
				sizeEdit.putFloat("Total", total);
				sizeEdit.putFloat(file_path, kilobytes);
				sizeEdit.commit();
				TextView result;
				if(d.getIsReply()){
					result = (TextView) findViewById(R.id.totalsizeReply);
				}
				else{
					result = (TextView) findViewById(R.id.totalsize);
				}
				result.setText("Total size: " +total + " KB");
				listView.setAdapter(new AttachmentsAdapter(getApplicationContext(),
						R.layout.attachments_item, R.id.attachments_text,
						attachments, result));
			}
			cursor.close();
		}
	}			
	@SuppressLint("SdCardPath")
	public void onClickAttach(MenuItem m) {
		try{		
			openGallery();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void openGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.putExtra("return-data", true);
		startActivityForResult(
				Intent.createChooser(intent, "Complete action using"),
				PICK_FROM_GALLERY);
	}	
	public void onClickSend(View v){
		total = sizePref.getFloat("Total", (float)0.0);
		if (total > 20480) {//The maximum attachment size to make email recievable by microsoft accounts
			Toast toast = Toast.makeText(getApplicationContext(),
					"Could not send, files are too big to attach!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
		else{
			String recipients,cc,bcc,subject,body;
			DisplayEmail d = DisplayEmail.getInstance();
			if(d.getIsReply()){
				recipients = ((TextView) findViewById(R.id.receiveAccsReply)).getText().toString();
				cc = ((EditText) findViewById(R.id.ccAccsReply)).getText().toString();
				bcc = ((EditText) findViewById(R.id.bccAccsReply)).getText().toString();
				subject = ((TextView) findViewById(R.id.subjectReply)).getText().toString();
				body = ((EditText) findViewById(R.id.bodyReply)).getText().toString();
			}
			else{
				recipients = ((EditText) findViewById(R.id.receiveAccs)).getText().toString();
				cc = ((EditText) findViewById(R.id.ccAccs)).getText().toString();
				bcc = ((EditText) findViewById(R.id.bccAccs)).getText().toString();
				subject = ((EditText) findViewById(R.id.subject)).getText().toString();
				body = ((EditText) findViewById(R.id.body)).getText().toString();
			}
			if(!recipients.equals("") && !subject.equals("") && !body.equals("")){
				try {   
					MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
					mf.sendMail(subject, body, defaultAcc, recipients, cc, bcc, attachments, getApplicationContext(), this);  
					startActivity(new Intent("TIG055st2014.mailmaster.InboxActivity"));
				} 
				catch (Exception e) {    
					e.printStackTrace();
				}
			} 
			else {
				// Missed fields
				Toast toast = Toast.makeText(getApplicationContext(),
						"One or more required fields are unfilled.",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
    @Override
    public void onBackPressed() {
    	new SaveDraftFragment().show(getSupportFragmentManager(), "SaveDraft");
    }
    public void dialogResult(){
    	if(save){
    		String recipients,cc,bcc,subject,body;
    		DisplayEmail d = DisplayEmail.getInstance();
    		if(d.getIsReply()){
    			recipients = ((TextView) findViewById(R.id.receiveAccsReply)).getText().toString();
    			cc = ((EditText) findViewById(R.id.ccAccsReply)).getText().toString();
    			bcc = ((EditText) findViewById(R.id.bccAccsReply)).getText().toString();
    			subject = ((TextView) findViewById(R.id.subjectReply)).getText().toString();
    			body = ((EditText) findViewById(R.id.bodyReply)).getText().toString();
    		}
    		else{
    			recipients = ((EditText) findViewById(R.id.receiveAccs)).getText().toString();
    			cc = ((EditText) findViewById(R.id.ccAccs)).getText().toString();
    			bcc = ((EditText) findViewById(R.id.bccAccs)).getText().toString();
    			subject = ((EditText) findViewById(R.id.subject)).getText().toString();
    			body = ((EditText) findViewById(R.id.body)).getText().toString();
    		}
    		if(!recipients.equals("") || !subject.equals("") || !body.equals("") || !cc.equals("") || !bcc.equals("")){
    			try {   
    				MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
    				mf.saveDraft(subject, body, defaultAcc, recipients, cc, bcc, getApplicationContext(), this);  
    			} 
    			catch (Exception e) {    
    				e.printStackTrace();
    			}
    		} 
    	}
    	startActivity(new Intent("TIG055st2014.mailmaster.InboxActivity"));
    }
}
