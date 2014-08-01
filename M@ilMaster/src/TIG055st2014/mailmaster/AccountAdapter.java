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
		final String defAcc = accounts.getString("default", "");

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.account_item, null);
		}
		if(s.equals(defAcc)){
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
						if(s.equals(defAcc)){        
							/*If there are memorized accounts left, but none of them are default,
                	then set the first one as the new default.*/
							if(names.size() > 0){
								accEdit.putString("default", names.get(0));
							}
							//If no accounts remain, disable the 'open folder' icon.
							else{
								accEdit.remove("default");
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