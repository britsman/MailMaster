package TIG055st2014.mailmaster;

import java.io.InputStream;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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

public class ComposeActivity extends Activity {

	private SharedPreferences accounts;
	private String defaultAcc;
	private String pw;
	private static final int PICK_FROM_GALLERY = 101;
	String file_path;
	int columnIndex;
	public double kilobytes;
	private double total;
	public ListView listView;
	ArrayList<String> attachments;
	Uri URI = null;
	 private SharedPreferences sizePref;
	 private SharedPreferences.Editor sizeEdit;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
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
		TextView sender = (TextView) findViewById(R.id.sendAcc);
		sender.setText(defaultAcc);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
			/**
			 * Get Path
			 */
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			file_path = cursor.getString(columnIndex);
			if(!attachments.contains(file_path)){
			Log.e("Attachment Path:", file_path);
			URI = Uri.parse("file://" + file_path);

			// calculating the file size
			DataSource source = new FileDataSource(file_path);
			
			byte[] byts= file_path.getBytes();
			double bytes = 0;
			
			
			try{
				InputStream stream = source.getInputStream();				
				while(stream.read() != -1){
					bytes++;
				}
				stream.close();
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			
			kilobytes = bytes/1024 ;

			total = (double)sizePref.getFloat("Total", (float)0.0);
			total += kilobytes;
			sizeEdit.putFloat("Total", (float)total);
			sizeEdit.putFloat(file_path, (float)kilobytes);
			sizeEdit.commit();
			TextView result = (TextView) findViewById(R.id.totalsize);
			result.setText("Total size: " +total + " KB");
			if (total <= 5120) {
				attachments.add(file_path);
			} else {
				System.out.println("Sorry files are too big to attach...");
			}
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA" + total);
			//cursor.moveToFirst();
			listView = (ListView) findViewById(R.id.attachment_list);
			listView.setClickable(true);
			listView.setAdapter(new AttachmentsAdapter(getApplicationContext(),
					R.layout.attachments_item, R.id.attachments_text,
					attachments, result));
			}
			cursor.close();
		}
	}

	private void While(boolean b) {
		// TODO Auto-generated method stub

	}

	@SuppressLint("SdCardPath")
	public void onClickAttach(View v) {
		try {
			openGallery();
		} catch (Exception e) {
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

	public void onClickSend(View v) {
		String recipients = ((EditText) findViewById(R.id.receiveAccs))
				.getText().toString();
		String cc = ((EditText) findViewById(R.id.ccAccs)).getText().toString();
		String bcc = ((EditText) findViewById(R.id.bccAccs)).getText()
				.toString();
		String subject = ((EditText) findViewById(R.id.subject)).getText()
				.toString();
		String body = ((EditText) findViewById(R.id.body)).getText().toString();

		if (!recipients.equals("") && !subject.equals("") && !body.equals("")) {
			try {
				MailFunctionality mf = new MailFunctionality(defaultAcc, pw,
						(defaultAcc.split("@"))[1]);
				mf.sendMail(subject, body, defaultAcc, recipients, cc, bcc,
						attachments);
			} catch (Exception e) {
				Toast toast = Toast
						.makeText(
								getApplicationContext(),
								"One or more supplied adresses contain illegal characters.",
								Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
				Log.e("SendMail", e.getMessage(), e);
			}
		} else {
			// Missed fields
			Toast toast = Toast.makeText(getApplicationContext(),
					"One or more required fields are unfilled.",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
	}

}
