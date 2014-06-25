package TIG055st2014.mailmaster;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity {
	
    private SharedPreferences accounts;
    private String defaultAcc;
    private String pw;
    private static final int PICK_FROM_GALLERY = 101;
    String  attachmentFile;
    Uri URI = null;
    int columnIndex;
 //   private Multipart _multipart; 
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		TextView sender = (TextView) findViewById(R.id.sendAcc);
		sender.setText(defaultAcc);
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
	           URI = Uri.parse("file://" + attachmentFile);
               cursor.close();
               }
        }
	
	
	@SuppressLint("SdCardPath")
	public void onClickAttach(View v) {
		try{
			/*BodyPart messageBodyPart = new MimeBodyPart(); 
			String filelocation="downloadfile.bin";
			DataSource source = new FileDataSource(filelocation); 
			messageBodyPart.setDataHandler(new DataHandler(source)); 
			messageBodyPart.setFileName(filelocation);
			String body = ((EditText) findViewById(R.id.body)).getText().toString();
		      messageBodyPart.setText(body); 
		       _multipart.addBodyPart(messageBodyPart);*/
		
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
		String recipients = ((EditText) findViewById(R.id.receiveAccs)).getText().toString();
		String subject = ((EditText) findViewById(R.id.subject)).getText().toString();
		String body = ((EditText) findViewById(R.id.body)).getText().toString();
		
		if(!recipients.equals("") && !subject.equals("") && !body.equals("")){
			try {   
				MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
				mf.sendMail(subject, body, defaultAcc, recipients);  
			} 
			catch (Exception e) {   
				Log.e("SendMail", e.getMessage(), e);   
			}
		}
		else{
            //Missed fields
            Toast toast = Toast.makeText(getApplicationContext(),
                    "One or more fields are unfilled.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
		}
	}
}
