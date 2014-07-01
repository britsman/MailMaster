package TIG055st2014.mailmaster;

import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity{
	
    private SharedPreferences accounts;
    private String defaultAcc;
    private String pw;
    private static final int PICK_FROM_GALLERY = 101;
    String  attachmentFile;
    int columnIndex;
    ArrayList<String> attachments;
    Uri URI = null;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayEmail d = DisplayEmail.getInstance();
		if(d.getIsReply()){
			setContentView(R.layout.activity_reply);
		}
		else{
			setContentView(R.layout.activity_compose);
		}
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
        attachments = new ArrayList<String>();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		DisplayEmail d = DisplayEmail.getInstance();
		TextView sender; 
		if(d.getIsReply()){
			sender = (TextView) findViewById(R.id.sendAccReply);
			sender.setText(defaultAcc);
			TextView to = (TextView) findViewById(R.id.receiveAccsReply);
			TextView subject = (TextView) findViewById(R.id.subjectReply);
			EditText cc = (EditText) findViewById(R.id.ccAccsReply);
			cc.setText("");
			try{	
				to.setText(d.getEmail().getFrom()[0].toString());
				Address[] tempcc = d.getEmail().getRecipients(Message.RecipientType.CC);
				if(tempcc != null){
					for(int i = 0; i < tempcc.length; i++){
						if(cc.getText().toString().equals("")){
							cc.setText(tempcc[i].toString());
						}
						else{
							cc.setText(cc.getText() + "," + tempcc[i].toString());
						}
					}
				}
				subject.setText(d.getReply().getSubject());
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		else{
			sender = (TextView) findViewById(R.id.sendAcc);
			sender.setText(defaultAcc);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
               /**
                * Get Path
                */
               Uri selectedImage = data.getData();
               String[] filePathColumn = { MediaStore.Images.Media.DATA };

               Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
               cursor.moveToFirst();
               columnIndex = cursor.getColumnIndex(filePathColumn[0]);
               attachmentFile = cursor.getString(columnIndex);
               Log.e("Attachment Path:", attachmentFile);
	           attachments.add(attachmentFile);
               cursor.close();
               }
        }
	@SuppressLint("SdCardPath")
	public void onClickAttach(MenuItem m) {
		try{		
			openGallery();
		 }catch (Exception e) {   
				Log.e("FileAttach", e.getMessage(), e);   
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
				mf.sendMail(subject, body, defaultAcc, recipients, cc, bcc, attachments);  
			} 
			catch (Exception e) {   
	            Toast toast = Toast.makeText(getApplicationContext(),
	                    "One or more supplied adresses contain illegal characters.", Toast.LENGTH_SHORT);
	            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
	            toast.show();
				Log.e("SendMail", e.getMessage(), e);   
			}
		}
		else{
            //Missed fields
            Toast toast = Toast.makeText(getApplicationContext(),
                    "One or more required fields are unfilled.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }
}
