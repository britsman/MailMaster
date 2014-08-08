package TIG055st2014.mailmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

/**
 * Adapter used to apply formatting to items in the emails list.
 */
public class EmailAdapter extends ArrayAdapter<Message> {

	private Context context;
	private ArrayList<Message> emails;
	private SharedPreferences accounts;
	private int[] colours;
	private Set<String> defAcc;
	
	public EmailAdapter(Context c, int r, int tv, ArrayList<Message> l) {
		super(c,r,tv,l);
		this.context = c;
		this.emails = l;
		this.accounts = c.getSharedPreferences("StoredAccounts", c.MODE_PRIVATE);
		this.colours = new int[]{
				Color.argb(155, 215, 255, 188), Color.argb(155, 188, 243, 255), Color.argb(155, 255, 181, 132)
		};
		defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));
	}
	/**
	 * This function is called automatically once for each listitem, and is used to apply
	 * formatting based on certain conditions.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try{
			int i = 0;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.email_item, null);
			}
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			Message temp = emails.get(position);
			String info = "From: " + (temp.getFrom())[0] + "\nSent: " + 
					temp.getReceivedDate() + "\nSubject: " + temp.getSubject();
			TextView tv = (TextView) convertView.findViewById(R.id.email_preview);
			tv.setText(info);
			//If email has been read, the item's text is black
			if(temp.getFlags().contains(Flag.SEEN)){
				tv.setTextColor(Color.BLACK);
			}
			//Text is set to blue in order to highlight that the email is unread.
			else{
				tv.setTextColor(Color.BLUE);
			}
			for(String s : defAcc){
				if(temp.getFolder().equals(apv.getEmailFolder(s))){
					tv.setBackgroundColor(colours[i]);
					break;
				}
				else{
					i++;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return (convertView);
	}
}
