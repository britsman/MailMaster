package TIG055st2014.mailmaster;

import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Adapter used to apply formatting and color to the text of items in the Attachments 
 * list in ComposeActivity, as well as to add a "delete button" component to every list item.
 */
public class AttachmentsAdapter extends ArrayAdapter<String> {

	private ArrayList<String> attach;
	private Context context;
	private float total;
	private SharedPreferences sizePref;
	private SharedPreferences.Editor sizeEdit;
	/**
	 * This is the label in ComposeActivity that needs to be updated when attachments are
	 * deleted.
	 */
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
	/**
	 * This function is called automatically once for each listitem, and is used to apply
	 * formatting based on certain conditions.
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		final String a = attach.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.attachments_item, null);
		}
		sizePref = context.getSharedPreferences("FileSizes", Context.MODE_PRIVATE);
		//20 mb is the maximum attachment size to make email recievable by microsoft accounts.
		if (total > 20480) {
			convertView.setBackgroundColor(Color.RED);
			text.setTextColor(Color.RED);
		} else {
			convertView.setBackgroundColor(Color.GREEN);
			text.setTextColor(Color.BLACK);
		}	
		TextView tv = (TextView) convertView
				.findViewById(R.id.attachments_text);
		
		//We split the stored filename so we only show the name (not the absolute path) in the GUI.
		String temp[] = a.split("/");
		tv.setText(temp[temp.length-1] + "\t" + 
				sizePref.getFloat(a, (float)0.0) + " KB");
		ImageButton delete = (ImageButton) convertView
				.findViewById(R.id.deleteattachment_button);
		/*Need to make the button unfocusable, in order to be able to click the button and
        the background of the item separately.
		 */
		delete.setFocusableInTouchMode(false);
		delete.setFocusable(false);

		delete.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				attach.remove(position);
				sizePref = context.getSharedPreferences("FileSizes", context.MODE_PRIVATE);
				sizeEdit = sizePref.edit();
				total = sizePref.getFloat("Total", (float)0.0) - sizePref.getFloat(a, (float)0.0);
				//20 mb is the maximum attachment size to make email recievable by microsoft accounts.
				if (total > 20480) {
					text.setTextColor(Color.RED);
				} 
				else {
					text.setTextColor(Color.BLACK);
				}
				/*Hack to stop total from going below zero after adding then removing multiple activities.
				Numbers look correct when adding/removing attachments, but sometimes when removing the last attachment
				the totalsize will go below zero for some reason */
				if(total < 0){
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
