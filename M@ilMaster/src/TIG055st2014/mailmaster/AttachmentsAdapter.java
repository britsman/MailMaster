package TIG055st2014.mailmaster;

import java.util.ArrayList;

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
	private double Total;

	public AttachmentsAdapter(Context applicationContext, int attachmentsItem,
			int attachmentsText, ArrayList<String> attachments, double total) {
		super(applicationContext, attachmentsItem, attachmentsText, attachments);
		this.attach = attachments;
		this.context = applicationContext;
		this.Total = total;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final String a = attach.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.attachments_item, null);
		}
		ComposeActivity com = new ComposeActivity();
		TextView res = (TextView) convertView.findViewById(R.id.totalsize);

		// double result = Double(res.getText());

		if (Total >= 0.1) {
			convertView.setBackgroundColor(Color.RED);
		} else {
			convertView.setBackgroundColor(Color.GREEN);
		}
		TextView tv = (TextView) convertView
				.findViewById(R.id.attachments_text);
		tv.setText(a);
		ImageButton delete = (ImageButton) convertView
				.findViewById(R.id.deleteattachment_button);
		delete.setFocusableInTouchMode(false);
		delete.setFocusable(false);

		delete.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				attach.remove(position);
				remove(a);
			}

		});
		return (convertView);
	}

	private double Double(CharSequence text) {
		// TODO Auto-generated method stub
		return 0;
	}
}
