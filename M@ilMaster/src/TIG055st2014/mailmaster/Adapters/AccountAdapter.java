package TIG055st2014.mailmaster.Adapters;

import TIG055st2014.mailmaster.R;
import android.app.Activity;
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
 * Adapter used to apply formatting and color to the text of items in the AccountSettings 
 * list, as well as to add a "delete button" component to every list item.
 */
public class AccountAdapter extends ArrayAdapter<String> {

	private Context context;
	private SharedPreferences accounts;
	private SharedPreferences.Editor accEdit;
	private ArrayList<String> names;
	/**
	 * Used to determine what colour the emails of a certain active account will have in 
	 * the email-list.
	 */
	private int[] colours;
	/**
	 * Reference to the AccountSettingsActivity instance that the current list belongs to.
	 * This is needed to update the options menu of the activity from within the adapter.
	 */
	private Activity act;


	public AccountAdapter(Context c, int r, int tv, ArrayList<String> l, Activity a) {
		super(c,r,tv,l);
		this.context = c;
		this.names = l;
		accounts = c.getSharedPreferences("StoredAccounts", c.MODE_PRIVATE);
		accEdit = accounts.edit();
		act = a;
		this.colours = new int[]{
				Color.argb(155, 215, 255, 188), Color.argb(155, 188, 243, 255), Color.argb(155, 255, 181, 132)
		};
	}
	/**
	 * This function is called automatically once for each listitem, and is used to apply
	 * formatting based on certain conditions.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final String name = names.get(position);
		final Set<String> activeAccs = new HashSet<String>();
		int i = 0;
		activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.account_item, null);
		}
		//If account is the first active account, it will receive the first background colour etc.
		for(String s : activeAccs){
			if(s.equals(name)){
				convertView.setBackgroundColor(colours[i]);
				break;
			}
			/*Colour is set to transparent each time account does not match the current index in 
			  defAcc. Will thus also remain transparent if the account is not currently active**/
			else{
				i++;
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		TextView tv = (TextView) convertView.findViewById(R.id.account_text);
		tv.setText(name);
		ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete_button);
		/*Need to make the button unfocusable, in order to be able to click the button and
          the background of the item separately.
		 */
		delete.setFocusableInTouchMode(false);
		delete.setFocusable(false);
		delete.setOnClickListener(
				new ImageButton.OnClickListener() {
					@Override
					public void onClick(View v) {
						accEdit.remove(name);
						names.remove(position);
						activeAccs.clear();
						activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));
						if(activeAccs.contains(name)){        
							/*If there are enabled accounts left, update the list and the
							 * enabled counter.*/
							if(names.size() > 0 && activeAccs.size() > 1){
								activeAccs.remove(name);
								accEdit.putStringSet("default", activeAccs);
								int count = accounts.getInt("enabled", 2);
								accEdit.putInt("enabled", count-1);
							}
							//If no accounts remain, disable the 'open folder' icon.
							else{
								accEdit.remove("default");
								accEdit.putInt("enabled", 0);
								act.invalidateOptionsMenu();
							}
						}
						accEdit.commit();
						remove(name);
					}
				}
				);
		return (convertView);
	}
}