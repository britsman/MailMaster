package TIG055st2014.mailmaster.Adapters;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.mail.Flags.Flag;
import javax.mail.Message;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
for more details. You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Adapter used to apply formatting to items in the emails list.
 */
public class EmailAdapter extends ArrayAdapter<Message> {

	private Context context;
	private ArrayList<Message> emails;
	private SharedPreferences accounts;
	private int[] colours;
	private Set<String> activeAccs;
	
	public EmailAdapter(Context c, int r, int tv, ArrayList<Message> l) {
		super(c,r,tv,l);
		this.context = c;
		this.emails = l;
		this.accounts = c.getSharedPreferences("StoredAccounts", c.MODE_PRIVATE);
		/* Used to apply colour to each email corresponding to the colour their account
		   has on the accountsettings page. **/
		this.colours = new int[]{
				Color.argb(155, 215, 255, 188), Color.argb(155, 188, 243, 255), Color.argb(155, 255, 181, 132)
		};
		activeAccs = new HashSet<String>();
		activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));
	}
	/**
	 * This function is called automatically once for each listitem, and is used to apply
	 * formatting based on certain conditions.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try{
			String from = context.getResources().getString(R.string.sender);
			String sent = context.getResources().getString(R.string.sent);
			String subject = context.getResources().getString(R.string.subject);
			int i = 0;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.email_item, null);
			}
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			Message temp = emails.get(position);
			String info = from + (temp.getFrom())[0] + "\n" + sent +": "+  
					temp.getReceivedDate() + "\n " + subject+ ": " + temp.getSubject();
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
			for(String s : activeAccs){
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
