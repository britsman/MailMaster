package TIG055st2014.mailmaster.Activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage.RecipientType;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Adapters.AttachmentsAdapter;
import TIG055st2014.mailmaster.Fragments.SaveDraftFragment;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.view.MotionEvent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Activity used for composing both new emails and replies.
 */
public class ComposeActivity extends FragmentActivity implements
OnItemSelectedListener {

	private SharedPreferences accounts;
	private SharedPreferences savedContacts;
	private SharedPreferences.Editor contactsEdit;
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
	public SharedPreferences.Editor sizeEdit;
	public boolean save;
	private TreeSet<String> output;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
		}
		catch(Exception e){
			e.printStackTrace();
		}
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
		savedContacts = getSharedPreferences("StoredContacts", MODE_PRIVATE);
		contactsEdit = savedContacts.edit();
		currentAcc = apv.getAccount();
		sizePref = getSharedPreferences("FileSizes", MODE_PRIVATE);
		sizeEdit = sizePref.edit();
		sizeEdit.putFloat("Total", (float) 0.0);
		sizeEdit.commit();
		String key = "Some Key";
		Encryption encryption = new Encryption();
		pw = encryption.decrypt(key, (accounts.getString(currentAcc, "")));
		attachments = new ArrayList<String>();
		fetchEmailsFromContacts();

	}

	@Override
	protected void onStart() {
		super.onStart();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		TextView sender;
		TextView from;
		TextView result;

		/*
		 * If message is a reply then certain fields are instantiated based on
		 * fields from the message that is being replied to. BCC is not filled
		 * (unless replying on one's own message) since they're supposed to be secret.
		 */
		if (apv.getIsReply()) {
			getActionBar().setTitle(R.string.composing_rp);
			result = (TextView) findViewById(R.id.totalsizeReply);
			sender = (TextView) findViewById(R.id.sendAccReply);
			sender.setText(currentAcc);
			sender.setEnabled(false);
			sender.setVisibility(View.GONE);
			from = (TextView) findViewById(R.id.senderReply);
			from.setEnabled(false);
			from.setVisibility(View.GONE);
			TextView to = (TextView) findViewById(R.id.receiveAccsReply);
			TextView subject = (TextView) findViewById(R.id.subjectReply);
			EditText cc = (EditText) findViewById(R.id.ccAccsReply);
			EditText bcc = ((EditText) findViewById(R.id.bccAccsReply));
			to.setText("");
			cc.setText("");
			bcc.setText("");

			try {
				subject.setText(apv.getReply().getSubject());
				//Used if replying to email sent by yourself (basically using sent email as draft).
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
					this.addAddresses(
							apv.getEmail().getRecipients(Message.RecipientType.BCC),
							bcc);
				}
				//Used for "normal" reply.
				else {
					to.setText(apv.getEmail().getFrom()[0].toString());
				}
				this.addAddresses(
						apv.getEmail().getRecipients(Message.RecipientType.CC),
						cc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* If not reply, app attempts to load all fields since message might be
		   based on draft. **/
		else {
			getActionBar().setTitle(R.string.composing);
			result = (TextView) findViewById(R.id.totalsize);
			sender = (TextView) findViewById(R.id.sendAcc);
			sender.setText(currentAcc);
			sender.setEnabled(false);
			sender.setVisibility(View.GONE);
			from = (TextView) findViewById(R.id.sender);
			from.setEnabled(false);
			from.setVisibility(View.GONE);
			if (apv.getFolderName(currentAcc).contains("Drafts")) {
				try {
					MailFunctionality mf = new MailFunctionality(currentAcc,
							accounts.getString(currentAcc, ""),
							(currentAcc.split("@"))[1]);
					EditText recipients = ((EditText) findViewById(R.id.receiveAccs));
					EditText cc = ((EditText) findViewById(R.id.ccAccs));
					EditText bcc = ((EditText) findViewById(R.id.bccAccs));
					EditText subject = ((EditText) findViewById(R.id.subject));
					if(apv.getEmail().getSubject() != null){
						subject.setText(apv.getEmail().getSubject().toString());
					}
					recipients.setText("");
					cc.setText("");
					bcc.setText("");

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
	 * Also adds a comma at the end of the last address, since that is the
	 * character our autocomplete looks for when determining if a new
	 * "word" has been started.
	 */
	private void addAddresses(Address[] addresses, EditText et) {
		if (addresses != null) {
			for (Address a : addresses) {
				et.setText(et.getText() + a.toString() + ",");
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
				String total_size = result.getResources().getString(R.string.total_size);
				result.setText(total_size+ " " + total + " KB");
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
	/**
	 * onClick for the send item in the actionbar.
	 */
	public void onClickSend(MenuItem m) {
		total = sizePref.getFloat("Total", (float) 0.0);
		if (total > 20480) {// The maximum attachment size to make email
			// receivable by microsoft accounts
			Toast toast = Toast.makeText(getApplicationContext(),
					getApplicationContext().getResources()
					.getString(R.string.toast_big_size),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		} 
		else {
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
			} 
			else {
				recipients = ((EditText) findViewById(R.id.receiveAccs))
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
							"TIG055st2014.mailmaster.Activities.MailFolderActivity"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Missed fields
				Toast toast = Toast.makeText(getApplicationContext(),
						getApplicationContext().getResources()
						.getString(R.string.toast_addacc),
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

	/**
	 * If back is pressed we check if the user wishes to save a draft.
	 */
	@Override
	public void onBackPressed() {
		new SaveDraftFragment().show(getSupportFragmentManager(), "SaveDraft");
	}

	/**
	 * This function finds all phonecontacts that have email addresses linked to them, and
	 * adds them as autocomplete options for when the user is typing in addresses too send
	 * to.
	 */
	public void fetchEmailsFromContacts() {
		String email = null;
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		//Using TreeSet to avoid duplicate entries.
		output = new TreeSet<String>();

		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);
		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				// Query and loop for every email of the contact
				Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,
						null, EmailCONTACT_ID + " = ?",
						new String[] { contact_id }, null);

				while (emailCursor.moveToNext()) {
					email = emailCursor.getString(emailCursor
							.getColumnIndex(DATA));
					output.add(email +",");
				}
				emailCursor.close();
			}
		}
		Set<String> temp = new HashSet<String>();
		//Load all previously saved contacts.
		temp.addAll(savedContacts.getStringSet("contacts", new HashSet<String>()));
		output.addAll(temp);
		String[] array = new String[0];
		adapter = new ArrayAdapter<String>(
				ComposeActivity.this, android.R.layout.simple_spinner_item, output.toArray(array));

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if (!apv.getIsReply()){
			final MultiAutoCompleteTextView myAutoComplete = (MultiAutoCompleteTextView) findViewById(R.id.receiveAccs);
			final MultiAutoCompleteTextView myAutoCompletecc = (MultiAutoCompleteTextView) findViewById(R.id.ccAccs);
			final MultiAutoCompleteTextView myAutoCompletebcc = (MultiAutoCompleteTextView) findViewById(R.id.bccAccs);
			setupAC(myAutoComplete, adapter);
			setupAC(myAutoCompletecc, adapter);
			setupAC(myAutoCompletebcc, adapter);
		}
		else{
			final MultiAutoCompleteTextView myAutoCompleteccrep = (MultiAutoCompleteTextView) findViewById(R.id.ccAccsReply);
			final MultiAutoCompleteTextView myAutoCompletebccrep = (MultiAutoCompleteTextView) findViewById(R.id.bccAccsReply);
			setupAC(myAutoCompleteccrep, adapter);
			setupAC(myAutoCompletebccrep, adapter);
		}
	}
	/**
	 * Function used to configure the various MultiAutoCompleteTextViews (the autocomplete suggestions list that shows up when
	 * writing in the to/cc/bcc fields).
	 */
	private void setupAC(final MultiAutoCompleteTextView ma, ArrayAdapter<String> adapter){
		ma.setAdapter(adapter);
		ma.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		//How many letters needed to start showing suggestions.
		ma.setThreshold(0);
		ma.setOnTouchListener(new View.OnTouchListener(){
			//Making sure suggestions are shown directly.
			@Override
			public boolean onTouch(View v, MotionEvent event){
				ma.showDropDown();
				return false;
			}
		});
		ma.setOnItemSelectedListener(this);
	}
	/**
	 * onClick for entries in the autocomplete suggestion list.
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
		Toast.makeText(parent.getContext(),
				getApplicationContext().getResources()
				.getString(R.string.toast_emailsel)+ " " + parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();
	}
	/**
	 * Used when user wishes to save a draft. Both replies and new messages can
	 * be saved.
	 */
	public void dialogResult() {
		if (save) {
			String recipients, cc, bcc, subject, body;
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			if (apv.getIsReply()) {
				recipients = ((TextView)findViewById(R.id.receiveAccsReply)).getText()
						.toString();
				cc = ((EditText) findViewById(R.id.ccAccsReply)).getText()
						.toString();
				bcc = ((EditText) findViewById(R.id.bccAccsReply)).getText()
						.toString();
				subject = ((TextView) findViewById(R.id.subjectReply))
						.getText().toString();
				body = ((EditText) findViewById(R.id.bodyReply)).getText()
						.toString();
			} 
			else {
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
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.MailFolderActivity"));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
	/**
	 * Called from asynctask to update the contacts used as autocomplete alternatives
	 */
	public void updateContacts(Set<String> contacts){
		Set<String> temp = new HashSet<String>();
		temp.addAll(savedContacts.getStringSet("contacts", new HashSet<String>()));
		temp.addAll(contacts);
		contactsEdit.putStringSet("contacts", temp);
		contactsEdit.commit();
		output.addAll(temp);
		adapter.clear();
		adapter.addAll(output);
	}
	/**
	 * Used to fetch additional contacts besides those with email present on the phone. Does not actually
	 * use the real contact apis of our supported email providers, due to time constraints. Instead the
	 * asynctask started by this function fetches all unique addresses that the current account has sent
	 * email to in the past. This is of course email provider independent. Takes time depending on how
	 * many emails the account has sent, expect it to take 3-6 minutes. However, these "contacts" are
	 * then saved to sharedpreferences, so you only need to parse them once. 
	 */
	public void downloadContacts(MenuItem m){
		MailFunctionality mf = new MailFunctionality(currentAcc,
				pw, (currentAcc.split("@"))[1]);
		mf.getContacts(this);
	}
}
