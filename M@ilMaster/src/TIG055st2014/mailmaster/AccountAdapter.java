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

/**
* Adapter used to apply formatting to items in the AccountSettings list, as well as to add
* a "delete button" component to every list item.
*/
public class AccountAdapter extends ArrayAdapter<String> {

    private Context context;
    private SharedPreferences accounts;
    private SharedPreferences.Editor accEdit;
    private ArrayList<String> names;

    public AccountAdapter(Context c, int r, int tv, ArrayList<String> l) {
        super(c,r,tv,l);
        this.context = c;
        this.names = l;
        accounts = c.getSharedPreferences("StoredAccounts", c.MODE_PRIVATE);
        accEdit = accounts.edit();
    }

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
        delete.setFocusableInTouchMode(false);
        delete.setFocusable(false);
        delete.setOnClickListener(
            new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                accEdit.remove(s);
                names.remove(position);
                if(s.equals(defAcc)){                	
                	if(names.size() > 0){
                		accEdit.putString("default", names.get(0));
                	}
                	else{
                		accEdit.remove("default");
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