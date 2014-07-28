package TIG055st2014.mailmaster;

import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

public class AttachmentsAdapter extends ArrayAdapter<String> {

	private ArrayList<String> attach;
	private Context context;
	private float total;
	private SharedPreferences sizePref;
	private SharedPreferences.Editor sizeEdit;
	private TextView text; 

	public AttachmentsAdapter(Context applicationContext, int attachmentsItem,
			int attachmentsText, ArrayList<String> attachments, TextView t) {
		super(applicationContext, attachmentsItem, attachmentsText, attachments);
		this.attach = attachments;
		this.context = applicationContext;
        text=t;
		sizePref = context.getSharedPreferences("FileSizes", context.MODE_PRIVATE);
        sizeEdit = sizePref.edit();
        total= sizePref.getFloat("Total", (float) 0.0);
        
		
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final String a = attach.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.attachments_item, null);
		}
		ComposeActivity com = new ComposeActivity();
		sizePref = context.getSharedPreferences("FileSizes", context.MODE_PRIVATE);

		if (total > 20480) {//The maximum attachment size to make email recievable by microsoft accounts
			convertView.setBackgroundColor(Color.RED);
			text.setTextColor(Color.RED);
		} else {
			convertView.setBackgroundColor(Color.GREEN);
			text.setTextColor(Color.BLACK);
		}
		TextView tv = (TextView) convertView
				.findViewById(R.id.attachments_text);
		String temp[] = a.split("/");
		tv.setText(temp[temp.length-1] + "\t" + 
				sizePref.getFloat(a, (float)0.0) + " KB");
		ImageButton delete = (ImageButton) convertView
				.findViewById(R.id.deleteattachment_button);
		delete.setFocusableInTouchMode(false);
		delete.setFocusable(false);

		delete.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				attach.remove(position);
				sizePref = context.getSharedPreferences("FileSizes", context.MODE_PRIVATE);
		        sizeEdit = sizePref.edit();
		        total = sizePref.getFloat("Total", (float)0.0) - sizePref.getFloat(a, (float)0.0);
				if (total > 20480) {//The maximum attachment size to make email recievable by microsoft accounts
					text.setTextColor(Color.RED);
				} 
				else {
					text.setTextColor(Color.BLACK);
				}
				if(total < 1){//Solves a bug where sometimes total goes below zero after all attachments are deleted.
					total = (float) 0.0;
				}
		        text.setText("Total size: " +total + " KB");
		        sizeEdit.putFloat("Total", total);
		        sizeEdit.remove(a);
		        sizeEdit.commit();
				remove(a);
			}

		});
		return (convertView);
	}
}
