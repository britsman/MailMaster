package TIG055st2014.mailmaster;

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
	}
	/**
	 * This function is called automatically once for each listitem, and is used to apply
	 * formatting based on certain conditions.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final String s = names.get(position);
		final Set<String> defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.account_item, null);
		}
		if(defAcc.contains(s)){
			convertView.setBackgroundColor(Color.GREEN);
		}
		else{
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.account_text);
		tv.setText(s);
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
						accEdit.remove(s);
						names.remove(position);
						if(defAcc.contains(s)){        
							/*If there are enabled accounts left, update the list and the
							 * enabled counter.*/
							if(names.size() > 0 && defAcc.size() > 1){
								defAcc.remove(s);
								accEdit.putStringSet("default", defAcc);
								int count = accounts.getInt("enabled", 1);
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
						remove(s);
					}
				}
				);
		return (convertView);
	}
}