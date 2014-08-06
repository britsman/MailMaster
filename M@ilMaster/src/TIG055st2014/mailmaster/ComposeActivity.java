package TIG055st2014.mailmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage.RecipientType;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity used for composing both new emails and replies.
 */
public class ComposeActivity extends FragmentActivity implements
		OnItemSelectedListener {

	private SharedPreferences accounts;
	private Set<String> defAcc;
	private String currentAcc;
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
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		getActionBar().setDisplayShowHomeEnabled(false);
		save = false;
		// Different xml layouts are loaded depending on if new message/reply.
		if (apv.getIsReply()) {
			setContentView(R.layout.listview_attachments);
			listView = (ListView) findViewById(R.id.attachment_list);
			listView.addHeaderView(getLayoutInflater().inflate(
					R.layout.activity_reply, null));
		} else {
			setContentView(R.layout.listview_attachments);
			listView = (ListView) findViewById(R.id.attachment_list);
			listView.addHeaderView(getLayoutInflater().inflate(
					R.layout.activity_compose, null));

		}
		listView.setClickable(true);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));
		currentAcc = apv.getAccount();
		sizePref = getSharedPreferences("FileSizes", MODE_PRIVATE);
		sizeEdit = sizePref.edit();
		sizeEdit.putFloat("Total", (float) 0.0);
		sizeEdit.commit();
		String key = "Some Key";
		Encryption encryption = new Encryption();
		pw = encryption.decrypt(key, (accounts.getString(currentAcc, "")));
		attachments = new ArrayList<String>();
		if (!apv.getIsReply()) {
			fetchEmailsFromContacts();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		TextView sender;
		TextView result;

		/*
		 * If message is a reply then certain fields are instantiated based on
		 * fields from the message that is being replied to.
		 */
		if (apv.getIsReply()) {
			getActionBar().setTitle(R.string.composing_rp);
			result = (TextView) findViewById(R.id.totalsizeReply);
			sender = (TextView) findViewById(R.id.sendAccReply);
			sender.setText(currentAcc);
			TextView to = (TextView) findViewById(R.id.receiveAccsReply);
			TextView subject = (TextView) findViewById(R.id.subjectReply);
			EditText cc = (EditText) findViewById(R.id.ccAccsReply);
			cc.setText("");
			try {
				subject.setText(apv.getReply().getSubject());
				if (apv.getFolderName(currentAcc).contains("Sent")) {
					Address[] tempTo = apv.getEmail().getRecipients(
							RecipientType.TO);
					if (tempTo != null) {
						for (Address a : tempTo) {
							if (to.getText().toString().equals("")) {
								to.setText(a.toString());
							} else {
								to.setText(to.getText() + "," + a.toString());
							}
						}
					}
				} else {
					to.setText(apv.getEmail().getFrom()[0].toString());
				}
				this.addAddresses(
						apv.getEmail().getRecipients(Message.RecipientType.CC),
						cc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// If not reply, app attempts to load all fields since message might be
		// based on draft.
		else {
			getActionBar().setTitle(R.string.composing);
			result = (TextView) findViewById(R.id.totalsize);
			sender = (TextView) findViewById(R.id.sendAcc);
			sender.setText(currentAcc);
			if (apv.getFolderName(currentAcc).contains("Drafts")) {
				try {
					MailFunctionality mf = new MailFunctionality(currentAcc,
							accounts.getString(currentAcc, ""),
							(currentAcc.split("@"))[1]);
					EditText recipients = ((EditText) findViewById(R.id.receiveAccs));
					recipients.setTextColor(Color.parseColor("#a4c639"));
					EditText cc = ((EditText) findViewById(R.id.ccAccs));
					cc.setTextColor(Color.parseColor("#a4c639"));
					EditText bcc = ((EditText) findViewById(R.id.bccAccs));
					bcc.setTextColor(Color.parseColor("#a4c639"));
					EditText subject = ((EditText) findViewById(R.id.subject));
					subject.setTextColor(Color.parseColor("#a4c639"));
					subject.setText(apv.getEmail().getSubject().toString());
					mf.getContents(this);
					addAddresses(
							apv.getEmail().getRecipients(
									Message.RecipientType.TO), recipients);
					addAddresses(
							apv.getEmail().getRecipients(
									Message.RecipientType.CC), cc);
					addAddresses(
							apv.getEmail().getRecipients(
									Message.RecipientType.BCC), bcc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (listView.getAdapter() == null) {
			listView.setAdapter(new AttachmentsAdapter(getApplicationContext(),
					R.layout.attachments_item, R.id.attachments_text,
					attachments, result));
		}
	}

	/**
	 * Makes sure there are separators between the addresses to send to.
	 */
	private void addAddresses(Address[] addresses, EditText et) {
		if (addresses != null) {
			for (Address a : addresses) {
				if (et.getText().toString().equals("")) {
					et.setText(a.toString());
				} else {
					et.setText(et.getText() + "," + a.toString());
				}
			}
		}
	}

	/**
	 * Gets called after user has selected a gallery image to attach.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
			// get path to pressed picture.
			Uri selectedImage = data.getData();
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			file_path = cursor.getString(columnIndex);
			// Ignore press if file has already been attached previously.
			if (!attachments.contains(file_path)) {
				Log.d("Attachment Path:", file_path);
				attachments.add(file_path);

				File file = new File(file_path);
				// calculating the file size
				float bytes = file.length();
				kilobytes = bytes / 1024;
				total = sizePref.getFloat("Total", (float) 0.0);
				total += kilobytes;
				sizeEdit.putFloat("Total", total);
				sizeEdit.putFloat(file_path, kilobytes);
				sizeEdit.commit();
				TextView result;
				if (apv.getIsReply()) {
					result = (TextView) findViewById(R.id.totalsizeReply);
				} else {
					result = (TextView) findViewById(R.id.totalsize);
				}
				result.setText("Total size: " + total + " KB");
				// Attachments list is updated to contain the pressed
				// attachment.
				listView.setAdapter(new AttachmentsAdapter(
						getApplicationContext(), R.layout.attachments_item,
						R.id.attachments_text, attachments, result));
			}
			cursor.close();
		}
	}

	@SuppressLint("SdCardPath")
	public void onClickAttach(MenuItem m) {
		try {
			openGallery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launches chooser for gallery apps and restricts file selection to images.
	 */
	public void openGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("return-data", true);
		startActivityForResult(
				Intent.createChooser(intent, "Complete action using"),
				PICK_FROM_GALLERY);
	}

	public void onClickSend(View v) {
		total = sizePref.getFloat("Total", (float) 0.0);
		if (total > 20480) {// The maximum attachment size to make email
							// receivable by microsoft accounts
			Toast toast = Toast.makeText(getApplicationContext(),
					"Could not send, files are too big to attach!",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		} else {
			String recipients, cc, bcc, subject, body;
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			if (apv.getIsReply()) {
				recipients = ((TextView) findViewById(R.id.receiveAccsReply))
						.getText().toString();
				cc = ((EditText) findViewById(R.id.ccAccsReply)).getText()
						.toString();
				bcc = ((EditText) findViewById(R.id.bccAccsReply)).getText()
						.toString();
				subject = ((TextView) findViewById(R.id.subjectReply))
						.getText().toString();
				body = ((EditText) findViewById(R.id.bodyReply)).getText()
						.toString();
			} else {
				recipients = ((TextView) findViewById(R.id.receiveAccs))
						.getText().toString();
				cc = ((EditText) findViewById(R.id.ccAccs)).getText()
						.toString();
				bcc = ((EditText) findViewById(R.id.bccAccs)).getText()
						.toString();
				subject = ((EditText) findViewById(R.id.subject)).getText()
						.toString();
				body = ((EditText) findViewById(R.id.body)).getText()
						.toString();
			}
			if (!recipients.equals("") && !subject.equals("")
					&& !body.equals("")) {
				try {
					MailFunctionality mf = new MailFunctionality(currentAcc,
							pw, (currentAcc.split("@"))[1]);
					mf.sendMail(subject, body, currentAcc, recipients, cc, bcc,
							attachments, getApplicationContext(), this);
					startActivity(new Intent(
							"TIG055st2014.mailmaster.MailFolderActivity"));
				} catch (Exception e) {
					e.printStackTrace();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		new SaveDraftFragment().show(getSupportFragmentManager(), "SaveDraft");
	}

	/**
	 * Used when user wishes to save a draft. Both replies and new messages can
	 * be saved.
	 */

	public void fetchEmailsFromContacts() {

		String email = null;
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		List<String> output = new ArrayList<String>();

		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);
		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {
			output.add("");
			while (cursor.moveToNext()) {
				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DISPLAY_NAME));

				// Query and loop for every email of the contact
				Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,
						null, EmailCONTACT_ID + " = ?",
						new String[] { contact_id }, null);

				while (emailCursor.moveToNext()) {
					email = emailCursor.getString(emailCursor
							.getColumnIndex(DATA));

					output.add(email);

				}
				emailCursor.close();
			}

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ComposeActivity.this, android.R.layout.simple_spinner_item,
				output);
		AutoCompleteTextView myAutoComplete = (AutoCompleteTextView) findViewById(R.id.receiveAccs);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myAutoComplete.setAdapter(adapter);
		myAutoComplete.setOnItemSelectedListener(this);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(0).equals("");
		Toast.makeText(parent.getContext(),
				"Email Selected : " + parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();
	}

	public void dialogResult() {
		if (save) {
			String recipients, cc, bcc, subject, body;
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			if (apv.getIsReply()) {
				recipients = (findViewById(R.id.receiveAccsReply)).getContext()
						.toString();
				cc = ((EditText) findViewById(R.id.ccAccsReply)).getText()
						.toString();
				bcc = ((EditText) findViewById(R.id.bccAccsReply)).getText()
						.toString();
				subject = ((TextView) findViewById(R.id.subjectReply))
						.getText().toString();
				body = ((EditText) findViewById(R.id.bodyReply)).getText()
						.toString();
			} else {
				recipients = ((TextView) findViewById(R.id.receiveAccsReply))
						.getText().toString();
				cc = ((EditText) findViewById(R.id.ccAccs)).getText()
						.toString();
				bcc = ((EditText) findViewById(R.id.bccAccs)).getText()
						.toString();
				subject = ((EditText) findViewById(R.id.subject)).getText()
						.toString();
				body = ((EditText) findViewById(R.id.body)).getText()
						.toString();
			}
			if (!recipients.equals("") || !subject.equals("")
					|| !body.equals("") || !cc.equals("") || !bcc.equals("")) {
				try {
					MailFunctionality mf = new MailFunctionality(currentAcc,
							pw, (currentAcc.split("@"))[1]);
					mf.saveDraft(subject, body, currentAcc, recipients, cc,
							bcc, getApplicationContext(), this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		startActivity(new Intent("TIG055st2014.mailmaster.MailFolderActivity"));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
}
