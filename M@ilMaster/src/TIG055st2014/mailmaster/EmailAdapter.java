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

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

/**
* Adapter used to apply formatting to items in inbox list.
*/
public class EmailAdapter extends ArrayAdapter<Message> {

    private Context context;
    private ArrayList<Message> emails;

    public EmailAdapter(Context c, int r, int tv, ArrayList<Message> l) {
        super(c,r,tv,l);
        this.context = c;
        this.emails = l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	try{
        	if (convertView == null) {
            	LayoutInflater inflater = (LayoutInflater) context
            		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	convertView = inflater.inflate(R.layout.email_item, null);
        	}
        	Message temp = emails.get(position);
            final String s = "From: " + (temp.getFrom())[0] + "\nSent: " + 
        	temp.getReceivedDate() + "\nSubject: " + temp.getSubject();
        	TextView tv = (TextView) convertView.findViewById(R.id.email_preview);
        	tv.setText(s);
        	if(temp.getFlags().contains(Flag.SEEN)){
        		tv.setTextColor(Color.BLACK);
        	}
        	else{
        		tv.setTextColor(Color.BLUE);
        	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return (convertView);
    }
}
